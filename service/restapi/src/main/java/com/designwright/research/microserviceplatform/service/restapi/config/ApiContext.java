package com.designwright.research.microserviceplatform.service.restapi.config;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class ApiContext {

    private String context;
    private List<ApiVersion> apiVersions = new ArrayList<>();

    public Map<String, ApiEndpoint<?>> getEndpointMap() {
        Map<String, ApiEndpoint<?>> endpointMap = new HashMap<>();

        if (apiVersions != null && !apiVersions.isEmpty() && !StringUtils.isEmpty(context)) {
            for (ApiVersion apiVersion : apiVersions) {
                endpointMap.putAll(apiVersion.getEndpointMap(context));
            }
        }

        return endpointMap;
    }
}
