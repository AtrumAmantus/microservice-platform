package com.designwright.research.microserviceplatform.common.eventutils.handlers;

import com.designwright.research.microserviceplatform.common.eventutils.handlers.parameter.HandlerParameterType;
import com.designwright.research.microserviceplatform.common.eventutils.handlers.parameter.HandlerParameterFactory;
import com.designwright.research.microserviceplatform.common.eventutils.EventMessage;
import com.designwright.research.microserviceplatform.common.eventutils.annotations.EventController;
import lombok.Data;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Used to create a store of the various {@link EventController}s that are defined by this service, along with
 * the metadata of the methods and events that the controllers handle.
 */
@Data
public class EventControllerStore {

    private final Map<String, Object> controllerClassToInstance = new HashMap<>();
    private final Map<String, Set<String>> queueToEventTypes = new HashMap<>();
    /**
     * Map of {@link EventHandler} {@link Set}s, keyed by the event type they handle.
     *
     * <p>Some events are handled by multiple {@link EventHandler}s, but with different parameters
     */
    private final Map<String, Set<EventHandler>> eventToHandlerMethod = new HashMap<>();

    public void setController(String name, Object controller) {
        controllerClassToInstance.put(name, controller);
    }

    /**
     * Creates and adds an {@link EventHandler} for the given event type and referenced {@link Method}.
     *
     * @param eventType The event type to associate with the added {@link EventHandler}.
     * @param handler   The {@link Method} reference toe with the added {@link EventHandler}.
     */
    public void addHandlerForEvent(String eventType, Method handler) {
        // TODO: Add a check that multiple EventHandlers don't handle the same event with the same parameters.
        eventToHandlerMethod.computeIfAbsent(eventType, k -> new HashSet<>());

        EventHandler eventHandler = new EventHandler(handler.getDeclaringClass().getSimpleName(), handler);
        Map<String, HandlerParameterType<?>> methodParameterNameToHandlerParameterType = new HashMap<>();

        // TODO: Allow for custom parameter types?
        for (Parameter parameter : handler.getParameters()) {
            eventHandler.addParameter(parameter);
            methodParameterNameToHandlerParameterType.put(
                    parameter.getName(),
                    HandlerParameterFactory.createFromClassType(parameter.getType()));
        }

        eventHandler.setMethodParameterNameToDefinition(methodParameterNameToHandlerParameterType);
        eventToHandlerMethod.get(eventType).add(eventHandler);
    }

    /**
     * Adds a {@link Set} of event types that are handled by a given queue.
     *
     * @param queueName       The name of the queue handling the event types.
     * @param eventNamesToAdd The {@link Set} of event types handled by the queue.
     */
    public void addEventActions(String queueName, Set<String> eventNamesToAdd) {
        Set<String> eventNameList = new HashSet<>();

        if (queueToEventTypes.containsKey(queueName)) {
            eventNameList.addAll(queueToEventTypes.get(queueName));
        }

        eventNameList.addAll(eventNamesToAdd);
        queueToEventTypes.put(queueName, eventNameList);
    }

    /**
     * Process an incoming {@link EventMessage} from the broker, calling the appropriate {@link EventHandler}
     * to process the {@link EventMessage}.
     *
     * @param request The incoming {@link EventMessage} from the broker.
     * @param <T>     The type of payload contained in the {@link EventMessage}.
     */
    public <T extends Serializable> void process(EventMessage<T> request) {
        String eventType = request.getEventType();

        // Check if we can handle this event
        if (eventToHandlerMethod.containsKey(eventType)) {
            List<Object> valuesToInvokeWith = new ArrayList<>();
            Set<EventHandler> eventMethods = eventToHandlerMethod.get(eventType);
            AnalysisResult bestMatchingMethod = null;
            Method methodToInvoke = null;
            Object classNameToInvoke = null;

            // Find a method that matches the given parameters, or one that has the most of them at least
            for (EventHandler eventHandler : eventMethods) {
                AnalysisResult analysis = eventHandler.analyzeAgainstSupportedParameters(request);

                if (analysis.isSolidMatch()) {
                    // We found a method that matches all given parameters
                    methodToInvoke = analysis.getAnalyzedMethod();
                    valuesToInvokeWith = analysis.getParameterValues();
                    classNameToInvoke = analysis.getAnalyzedClassName();
                    break;
                } else if (analysis.isPartialMatch()) {
                    // This method has "some" of the given parameters, check if it has the most
                    if (bestMatchingMethod != null) {
                        if (analysis.getMatchScore() < bestMatchingMethod.getMatchScore()) {
                            bestMatchingMethod = analysis;
                        }
                    } else {
                        bestMatchingMethod = analysis;
                    }
                }
            }

            // If we only found a partial match, no solid match, identify the valid parameters to use
            if (methodToInvoke == null && bestMatchingMethod != null) {
                methodToInvoke = bestMatchingMethod.getAnalyzedMethod();
                valuesToInvokeWith = bestMatchingMethod.getParameterValues();
                classNameToInvoke = bestMatchingMethod.getAnalyzedClassName();
            }

            // Check if we found a method to invoke
            if (methodToInvoke != null) {
                try {
                    Object classInstance = controllerClassToInstance.get(classNameToInvoke);

                    request.setStatus(EventMessage.EventStatus.SUCCESS);
                    Object result = methodToInvoke.invoke(classInstance, valuesToInvokeWith.toArray());

                    if (result instanceof List) {
                        request.setPayload((List<T>) result);
                    } else {
                        request.setPayload(new ArrayList<T>(List.<T>of((T)result)));
                    }

                } catch (IllegalAccessException | InvocationTargetException ex) {
                    log.error("Failed to process event", ex);
                    request.setStatus(EventMessage.EventStatus.FAIL);
                    request.setErrorMessage("Can not process this event type.");
                }
            } else {
                request.setStatus(EventMessage.EventStatus.FAIL);
                request.setErrorMessage("No available methods accept the given parameters.");
            }
        } else {
            request.setStatus(EventMessage.EventStatus.FAIL);
            request.setErrorMessage("No event type was specified.");
        }

        if (request.getEventType().endsWith(".requested")) {
            request.setEventType(request.getEventType().replace(".requested", ".provided"));
        }
    }
}
