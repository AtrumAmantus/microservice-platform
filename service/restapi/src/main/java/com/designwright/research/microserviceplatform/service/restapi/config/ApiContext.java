package com.designwright.research.microserviceplatform.service.restapi.config;

import com.designwright.research.microserviceplatform.utils.ListUtils;
import lombok.Data;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class ApiContext {

    private String context;
    private List<ApiVersion> apiVersions;

    public Map<String, ApiEndpoint> getEndpointMap() {
        Map<String, ApiEndpoint> endpointMap = new HashMap<>();

        if (!ListUtils.isEmpty(apiVersions) && !StringUtils.isEmpty(context)) {
            for (ApiVersion apiVersion : apiVersions) {
                endpointMap.putAll(apiVersion.getEndpointMap(context));
            }
        }

        return endpointMap;
    }
}
