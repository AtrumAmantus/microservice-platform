package com.designwright.research.microserviceplatform.service.restapi.utility;

import com.designwright.research.microserviceplatform.common.eventutils.EventMessage;
import com.designwright.research.microserviceplatform.service.restapi.config.ApiEndpoint;
import com.designwright.research.microserviceplatform.service.restapi.config.ProcessorConfiguration;
import com.designwright.research.microserviceplatform.service.restapi.config.WebApiConfiguration;
import com.designwright.research.microserviceplatform.utils.exceptions.ConfigurationException;
import com.designwright.research.microserviceplatform.service.restapi.processing.Processor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import static org.apache.commons.lang3.StringUtils.lowerCase;
import static org.apache.commons.lang3.StringUtils.substring;
import static org.apache.commons.lang3.StringUtils.upperCase;

@Slf4j
@Component
public class ControllerUtility {

    private final ProcessorConfiguration processorConfiguration;
    private final Map<String, ApiEndpoint> endpointMap;

    @Autowired
    public ControllerUtility(
            Environment environment,
            ProcessorConfiguration processorConfiguration,
            WebApiConfiguration webApiConfiguration) {
        this.processorConfiguration = processorConfiguration;
        endpointMap = MapUtils.emptyIfNull(webApiConfiguration.getEndpointMap());
        log.info("{} endpoint(s) found and configured.", endpointMap.size());
        if (endpointMap.isEmpty()) {
            throw new ConfigurationException(environment.getActiveProfiles()[0]);
        }
    }

    public ApiEndpoint findRequestEndpoint(HttpServletRequest request) {
        String requestMethod = upperCase(request.getMethod()) + ":";
        String requestUrl = removeEndSlash(request.getRequestURI());
        ApiEndpoint apiEndpoint = endpointMap.get(requestMethod + requestUrl);
        if (apiEndpoint == null) {
            String revisedUrl = removeLastLayerOfUrl(requestUrl);
            apiEndpoint = endpointMap.get(requestMethod + revisedUrl);
            if (apiEndpoint != null && apiEndpoint.getPathVariableName() == null) {
                apiEndpoint = null;
            }
        }
        return apiEndpoint;
    }

    public Processor getPostProcessor(EventMessage<Serializable> eventMessage) {
        return processorConfiguration.getPostProcessor(getProcessorNamePrefixFromEventType(eventMessage.getEventType()));
    }

    public Processor getPreProcessor(EventMessage<Serializable> eventMessage) {
        return processorConfiguration.getPreProcessor(getProcessorNamePrefixFromEventType(eventMessage.getEventType()));
    }

    public EventMessage<Serializable> checkAndSetPathVariable(
            EventMessage<Serializable> eventMessage,
            ApiEndpoint apiEndpoint,
            String requestUrl) throws UnsupportedEncodingException {
        if (urlHasPathVariableDefined(requestUrl, apiEndpoint.getRequestUrl())) {
            String pathVariableValue = java.net.URLDecoder.decode(getLastLayerOfUrl(requestUrl), StandardCharsets.UTF_8.name());
            eventMessage.addParameter(apiEndpoint.getPathVariableName(), pathVariableValue);
        }
        return eventMessage;
    }

    public String parseEventType(String requestMethod, String endpointUrl) {
        StringBuilder eventType = new StringBuilder();
        String eventPrefix = getLastLayerOfUrl(endpointUrl);
        eventType.append(eventPrefix);
        eventType.append(".");
        eventType.append(MethodVerb.valueOf(requestMethod).getVerb());
        eventType.append(".requested");
        return eventType.toString();
    }

    private String getLastLayerOfUrl(String url) {
        String[] urlBits = url.split("/");
        return urlBits[urlBits.length - 1];
    }

    private String removeLastLayerOfUrl(String url) {
        ArrayList<String> urlBits = new ArrayList<>(Arrays.asList(url.split("/")));
        urlBits.remove(urlBits.size() - 1);
        return String.join("/", urlBits);
    }

    private String getProcessorNamePrefixFromEventType(String eventType) {
        return lowerCase(eventType.split("\\.")[0]);
    }

    private boolean urlHasPathVariableDefined(String requestUrl, String endpointUrl) {
        return !removeEndSlash(requestUrl).equalsIgnoreCase(endpointUrl);
    }

    private String removeEndSlash(String url) {
        return substring(url, -1).equalsIgnoreCase("/") ? substring(url, 0, -1) : url;
    }
}