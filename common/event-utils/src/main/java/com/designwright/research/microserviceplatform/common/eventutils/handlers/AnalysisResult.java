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

/** TODO: Reconsider this class's role and scope, it's a little confusing */
@Data
@RequiredArgsConstructor
public class AnalysisResult {

    private final Method analyzedMethod;
    private final String analyzedClassName;

    private final List<String> requestParameters = new ArrayList<>();
    @Getter(AccessLevel.PRIVATE)
    private final Map<String, Object> matchingParameters = new HashMap<>();

    private List<String> methodParameters = new ArrayList<>();
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

    // TODO: Matching score logic needs to be reworked

    public Float getMatchScore() {
        float score;

        if (requestParameters.isEmpty() && methodParameters.isEmpty()) {
            // There were no parameters sent, and this method expects none, perfect match
            score = 1.0f;
        } else if (matchingParameters.isEmpty()) {
            // There were no matching parameters, bad score
            score = 0.0f;
        } else {
            // Score based on how many parameters were in the request and how many matched.
            score = (requestParameters.size() + methodParameters.size()) / (matchingParameters.size() * 2.0f);
        }

        return score;
    }

    public boolean isPartialMatch() {
        return !matchingParameters.isEmpty();
    }

    public boolean isSolidMatch() {
        return getMatchScore().equals(1f);
    }
}
