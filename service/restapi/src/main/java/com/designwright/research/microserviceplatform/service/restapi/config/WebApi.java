package com.designwright.research.microserviceplatform.service.restapi.config;

import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ConfigurationProperties("webapi")
class WebApi {

    @Setter
    private List<ApiContext> apiContexts;

    public Map<String, ApiEndpoint> getEndpointMap() {
        Map<String, ApiEndpoint> endpointMap = new HashMap<>();

        if (apiContexts != null && !apiContexts.isEmpty()) {
            for (ApiContext apiContext : apiContexts) {
                endpointMap.putAll(apiContext.getEndpointMap());
            }
        }

        return endpointMap;
    }
}
