package com.designwright.research.microserviceplatform.service.restapi.config;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;

@Getter
@EqualsAndHashCode
public class ApiEndpoint {

    private final String requestMethod;
    private final String requestUrl;
    private String pathVariableName;

    public ApiEndpoint(String requestMethod, String endpointUrl) {
        this.requestMethod = requestMethod;
        ArrayList<String> urlBits = new ArrayList<>(Arrays.asList(endpointUrl.split("/")));
        String lastBit = urlBits.remove(urlBits.size() - 1);

        if (lastBit.substring(0, 1).equalsIgnoreCase("{")) {
            pathVariableName = StringUtils.substringBetween(lastBit, "{", "}");
            requestUrl = String.join("/", urlBits);
        } else {
            requestUrl = endpointUrl;
        }
    }
}
