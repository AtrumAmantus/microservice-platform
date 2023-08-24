package com.designwright.research.microserviceplatform.service.restapi.config;

import com.designwright.research.microserviceplatform.utils.ListUtils;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class ApiVersion {

    private String version;
    private List<ApiResource> apiResources;

    Map<String, ApiEndpoint> getEndpointMap(String context) {
        Map<String, ApiEndpoint> endpointMap = new HashMap<>();

        if (!ListUtils.isEmpty(apiResources) && !StringUtils.isEmpty(version)) {
            for (ApiResource apiResource : apiResources) {
                endpointMap.putAll(apiResource.getEndpointMap(context + "/" + version + "/"));
            }
        }

        return endpointMap;
    }
}
