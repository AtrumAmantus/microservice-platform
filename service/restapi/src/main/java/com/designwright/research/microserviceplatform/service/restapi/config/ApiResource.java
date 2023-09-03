package com.designwright.research.microserviceplatform.service.restapi.config;

import lombok.Setter;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

public class ApiResource {
    private final Map<RequestType, String> requestTypeToUrl = new EnumMap<>(RequestType.class);

    @Setter
    private Class<?> requestModel;

    private enum RequestType {
        GET,
        POST,
        PUT,
        DELETE
    }

    public void setGet(String url) {
        requestTypeToUrl.put(RequestType.GET, url);
    }

    public void setPost(String url) {
        requestTypeToUrl.put(RequestType.POST, url);
    }

    public void setPut(String url) {
        requestTypeToUrl.put(RequestType.PUT, url);
    }

    public void setDelete(String url) {
        requestTypeToUrl.put(RequestType.DELETE, url);
    }

    Map<String, ApiEndpoint<?>> getEndpointMap(String baseUrl) {
        Map<String, ApiEndpoint<?>> endpointMap = new HashMap<>();

        for (Map.Entry<RequestType, String> entry : requestTypeToUrl.entrySet()) {
            ApiEndpoint<?> apiEndpoint = new ApiEndpoint(
                    entry.getKey().toString(),
                    baseUrl + entry.getValue(),
                    requestModel);

            endpointMap.put(apiEndpoint.getRequestMethod() + ":" + apiEndpoint.getRequestUrl(), apiEndpoint);
        }

        return endpointMap;
    }
}
