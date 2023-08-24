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

    private final List<String> providedParameters = new ArrayList<>();
    @Getter(AccessLevel.PRIVATE)
    private final Map<String, Object> matchingParameters = new HashMap<>();

    private List<String> expectedParameters = new ArrayList<>();
    private List<String> parameterOrder = new ArrayList<>();

    /**
     * Add the name of a parameter that is provided.
     *
     * @param parameterName The name of the provided parameter.
     */
    public void addProvidedParameter(String parameterName) {
        providedParameters.add(parameterName);
    }

    public void addMatchingParameter(String parameterName, Object parameterValue) {
        matchingParameters.put(parameterName, parameterValue);
    }

    public List<Object> getParameterValues() {
        List<Object> returnValues = new ArrayList<>();

        for (String parameterName : parameterOrder) {
            returnValues.add(matchingParameters.get(parameterName));
        }

        return returnValues;
    }

    public Float getMatchScore() {
        return (float) (providedParameters.size() + expectedParameters.size()) / (matchingParameters.size() * 2);
    }

    public boolean isPartialMatch() {
        return !matchingParameters.isEmpty();
    }

    public boolean isSolidMatch() {
        return getMatchScore().equals(1f);
    }
}
