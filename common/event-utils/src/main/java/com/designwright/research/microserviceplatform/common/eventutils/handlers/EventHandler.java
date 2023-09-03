package com.designwright.research.microserviceplatform.common.eventutils.handlers;

import com.designwright.research.microserviceplatform.common.eventutils.EventMessage;
import com.designwright.research.microserviceplatform.common.eventutils.handlers.parameter.HandlerParameterType;
import com.designwright.research.microserviceplatform.common.eventutils.annotations.EventController;
import lombok.Data;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Defines a method of an {@link EventController} that handles an event with some set supported parameter
 * arguments.
 */
@Data
public class EventHandler {

    /** The name of the controller this handler belongs to */
    private final String controllerName;
    /** The {@link Method} reference that this handler is representing */
    private final Method controllerMethod;
    /** The {@link Parameter}s that are taken by the controller method */
    private final Set<Parameter> methodParameters = new HashSet<>();
    /** The order of the parameters for this method that is required when invoking it */
    private final List<String> methodParameterOrder = new ArrayList<>();
    /** A map of definitions for each parameter, keyed by the parameter name */
    private Map<String, HandlerParameterType<?>> methodParameterNameToDefinition;

    /**
     * Add a parameter to the list of parameters required by this method.
     *
     * <p>The order in which the parameters are added dictates the parameter order when
     * invoking the method.
     *
     * @param parameter The {@link Parameter} to add.
     */
    public void addParameter(Parameter parameter) {
        methodParameters.add(parameter);
        methodParameterOrder.add(parameter.getName());
    }

    /**
     * Gets a {@link HandlerParameterType} definition for the given parameter.
     *
     * @param parameterName The name of the parameter to get a {@link HandlerParameterType} definition for.
     *
     * @return An {@link Optional} potentially containing the {@link HandlerParameterType} definition associated
     * with the given parameter name.
     */
    public Optional<HandlerParameterType<?>> getHandlerParameterType(String parameterName) {
        return Optional.ofNullable(methodParameterNameToDefinition.get(parameterName));
    }

    /**
     * Creates a {@link AnalysisResult} of the parameters for the given {@link EventMessage} to determine which
     * ones are supported by this {@link EventHandler}.
     *
     * @param eventMessage Map of the given parameters (name,value) to test against.
     *
     * @return The created {@link AnalysisResult}.
     */
    public <T extends Serializable> AnalysisResult analyzeAgainstSupportedParameters(EventMessage<T> eventMessage) {
        Map<String, String> requestParameterNameToValue = eventMessage.getParameterValues();

        AnalysisResult analysis = new AnalysisResult(controllerMethod, controllerName);
        analysis.setMethodParameterOrder(methodParameterOrder);
        eventMessage.getParameterValues().keySet().forEach(analysis::addRequestParameter);

        for (Parameter parameter : methodParameters) {
            if (parameter.getType().isAssignableFrom(EventMessage.class)) {
                // If the method asks for the EventMessage, just add it
                analysis.addMatchingParameter(parameter.getName(), eventMessage);
            } else {
                // Otherwise, check what type of value the method parameter is and see if the request has it
                String parameterName = parameter.getName();

                if (requestParameterNameToValue.containsKey(parameterName)) {
                    addIfParameterIsSupported(analysis, parameterName, requestParameterNameToValue.get(parameterName));
                }
            }
        }

        return analysis;
    }

    /**
     * Adds the given parameter to the given {@link AnalysisResult} if it is a supported {@link HandlerParameterType}
     * for this {@link EventHandler}.
     *
     * @param analysisResult The {@link AnalysisResult} to add the parameter to, if it's supported.
     * @param parameterName  The name of the parameter to add.
     * @param parameterValue The value for the parameter to parse and add.
     */
    void addIfParameterIsSupported(AnalysisResult analysisResult, String parameterName, String parameterValue) {
        getHandlerParameterType(parameterName)
                .filter(handlerParameterType -> handlerParameterType.isSameType(parameterValue))
                .ifPresent(handlerParameterType -> analysisResult
                        .addMatchingParameter(
                                parameterName,
                                handlerParameterType.castToType(parameterValue)));
    }

    // TODO: Write a equals() and hash() that check based on parameters to prevent collisions for the same event.
}
