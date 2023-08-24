package com.designwright.research.microserviceplatform.common.eventutils.handlers;

import com.designwright.research.microserviceplatform.common.eventutils.handlers.parameter.HandlerParameterType;
import com.designwright.research.microserviceplatform.common.eventutils.annotations.EventController;
import lombok.Data;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
    /** The order of the parameters for this method that is required when invoking it */
    private final List<String> methodParameterOrder = new ArrayList<>();
    /** A map of definitions for each parameter, keyed by the parameter name */
    private Map<String, HandlerParameterType<?>> parameterNameToDefinition;

    /**
     * Add a parameter to the list of parameters required by this method.
     *
     * <p>The order in which the parameters are added dictates the parameter order when
     * invoking the method.
     *
     * @param parameterName The name of the parameter to add.
     */
    public void addParameter(String parameterName) {
        methodParameterOrder.add(parameterName);
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
        return Optional.ofNullable(parameterNameToDefinition.get(parameterName));
    }

    /**
     * Creates a {@link AnalysisResult} of the given parameters to determine which ones are supported by this
     * {@link EventHandler}.
     *
     * @param parameterNameToValue Map of the given parameters (name,value) to test against.
     *
     * @return The created {@link AnalysisResult}.
     */
    public AnalysisResult analyzeAgainstSupportedParameters(Map<String, String> parameterNameToValue) {
        AnalysisResult analysis = new AnalysisResult(controllerMethod, controllerName);
        analysis.setParameterOrder(methodParameterOrder);
        analysis.setExpectedParameters(new ArrayList<>(parameterNameToDefinition.keySet()));

        for (Map.Entry<String, String> parameter : parameterNameToValue.entrySet()) {
            analysis.addProvidedParameter(parameter.getKey());

            addIfParameterIsSupported(analysis, parameter.getKey(), parameter.getValue());
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
