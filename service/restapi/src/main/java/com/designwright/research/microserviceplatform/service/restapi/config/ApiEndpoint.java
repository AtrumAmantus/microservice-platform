package com.designwright.research.microserviceplatform.service.restapi.config;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

@Getter
@EqualsAndHashCode
public class ApiEndpoint<T extends Serializable> {

    private final String requestMethod;
    private final String requestUrl;
    private final Class<T> requestModel;
    private String pathVariableName;

    public ApiEndpoint(String requestMethod, String endpointUrl, Class<T> requestModel) {
        this.requestMethod = requestMethod;
        this.requestModel = requestModel;
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
