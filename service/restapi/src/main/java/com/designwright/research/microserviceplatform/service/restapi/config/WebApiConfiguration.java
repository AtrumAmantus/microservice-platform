package com.designwright.research.microserviceplatform.service.restapi.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Configuration
@EnableConfigurationProperties({WebApi.class})
public class WebApiConfiguration {

    private final WebApi webApi;

    public Map<String, ApiEndpoint> getEndpointMap() {
        if (webApi != null) {
            return webApi.getEndpointMap();
        }

        return new HashMap<>();
    }
}
