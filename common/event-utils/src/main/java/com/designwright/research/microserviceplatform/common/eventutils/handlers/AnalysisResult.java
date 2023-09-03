package com.designwright.research.microserviceplatform.common.eventutils.handlers;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@RequiredArgsConstructor
public class AnalysisResult {

    private final Method analyzedMethod;
    private final String analyzedClassName;

    private final List<String> requestParameters = new ArrayList<>();
    @Getter(AccessLevel.PRIVATE)
    private final Map<String, Object> matchingParameters = new HashMap<>();

    private List<String> methodParameterOrder = new ArrayList<>();

    /**
     * Add the name of a parameter that is provided.
     *
     * @param requestParameterName The name of the provided parameter.
     */
    public void addRequestParameter(String requestParameterName) {
        requestParameters.add(requestParameterName);
    }

    /**
     * Add the name and value of a parameter that is both in the request and on the method signature.
     *
     * @param parameterName The name of the parameter that is both in the request and method signature.
     * @param parameterValue The value for the matching parameter.
     */
    public void addMatchingParameter(String parameterName, Object parameterValue) {
        matchingParameters.put(parameterName, parameterValue);
    }

    /**
     * Returns a collection of parameter values, in the order listed in the method signature.
     *
     * @return A {@link List} of the parameter values.
     */
    public List<Object> getParameterValues() {
        List<Object> returnValues = new ArrayList<>();

        for (String parameterName : methodParameterOrder) {
            returnValues.add(matchingParameters.get(parameterName));
        }

        return returnValues;
    }

    /**
     * Gets the score for how good of a match the {@link EventHandler} was for the given request.
     *
     * <p>A score of 0 means the request does not satisfy the {@link EventHandler}</p>
     *
     * <p>A positive score means at least a partial match, with a lower score meaning a more precise match</p>
     *
     * @return A score representing the precision of the match.
     */
    public int getMatchScore() {
        int score;

        if (methodParameterOrder.size() != matchingParameters.size()) {
            // We didn't match every required method parameter, failed match
            score = 0;
        } else {
            // TODO: This calculation needs some work, it can goof with multiple EventMessage method parameters
            // Score based on how many parameters were in the request and how many matched.
            // Adding +2 to offset the value in instances where the method wanted an
            // EventMessage and the request has 0 parameters
            score = (requestParameters.size() - methodParameterOrder.size()) + 2;
        }

        return score;
    }

    public boolean isPartialMatch() {
        return getMatchScore() > 0;
    }

    public boolean isSolidMatch() {
        return getMatchScore() == 2;
    }
}
