package com.designwright.research.microserviceplatform.service.restapi.controller;

import com.designwright.research.microserviceplatform.common.eventutils.BasicEventMessage;
import com.designwright.research.microserviceplatform.common.eventutils.EventMessage;
import com.designwright.research.microserviceplatform.service.restapi.config.ApiEndpoint;
import com.designwright.research.microserviceplatform.service.restapi.processing.Processor;
import com.designwright.research.microserviceplatform.service.restapi.utility.ControllerUtility;
import com.designwright.research.microserviceplatform.utils.exceptions.ResourceNotFoundException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@RestController
class WebController {

    private final ControllerUtility controllerUtility;

    private static final String DEFAULT_EVENT_TYPE = "endpoint.get.Requested";

    // Squid:S3752 - Intentionally routing all request types here.
    // Unused - This is called dynamically by Spring Boot.
    @SuppressWarnings({"squid:S3752", "unused"})
    @RequestMapping(value = "/**")
    public EventMessage<Serializable> processGenericRequest(HttpServletRequest request) {
        EventMessage<Serializable> eventMessage = addRequestParameters(getGenericMessage(), request);
        addRequestBody(eventMessage, request);
        return processRequest(
                eventMessage,
                request
        );
    }

    private EventMessage<Serializable> getGenericMessage() {
        return BasicEventMessage.builder(DEFAULT_EVENT_TYPE)
                .build();
    }

    private EventMessage<Serializable> addRequestParameters(EventMessage<Serializable> eventMessage, HttpServletRequest request) {
        Enumeration<String> parameters = request.getParameterNames();
        Map<String, String> requestParameters = new HashMap<>();
        while (parameters.hasMoreElements()) {
            String parameter = parameters.nextElement();
            requestParameters.put(parameter, request.getParameter(parameter));
        }
        eventMessage.addParameterValues(requestParameters);
        return eventMessage;
    }

    private void addRequestBody(EventMessage<Serializable> eventMessage, HttpServletRequest request) {
        try {
            String bodyStream = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
            if (!StringUtils.isEmpty(bodyStream)) {
                JavaType javaType = new ObjectMapper().getTypeFactory().constructCollectionType(List.class, Object.class);
                ArrayList<? extends Serializable> bodyList = new ObjectMapper().readValue(bodyStream, javaType);
                eventMessage.setPayload(bodyList);
            }
        } catch (JsonMappingException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Received bad request: JSON body has syntax error.", ex);
        } catch (IOException ex) {
            throwBadRequestException(ex);
        }
    }

    @ExceptionHandler({HttpMessageNotReadableException.class})
    public void handleException(RuntimeException ex) {
        throwBadRequestException(ex);
    }

    private void throwBadRequestException(Throwable ex) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Received bad request.", ex);
    }

    private EventMessage<Serializable> processRequest(EventMessage<Serializable> eventMessage, HttpServletRequest request) {
        if (log.isDebugEnabled())
            log.debug("Endpoint: {}, RequestType: {}, Params: {}", request.getRequestURI(), request.getMethod(), eventMessage.getParameterValues());

        ApiEndpoint apiEndpoint = controllerUtility.findRequestEndpoint(request);

        if (apiEndpoint != null) {
            eventMessage.setEventType(controllerUtility.parseEventType(request.getMethod(), apiEndpoint.getRequestUrl()));

            eventMessage = eventPreProcessing(eventMessage);

            return eventPostProcessing(
                    processRequestEvent(eventMessage, apiEndpoint, request)
            );
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }

    private EventMessage<Serializable> eventPreProcessing(EventMessage<Serializable> eventMessage) {
        Processor preProcessor = controllerUtility.getPreProcessor(eventMessage);
        return preProcessor.process(eventMessage);
    }

    private EventMessage<Serializable> eventPostProcessing(EventMessage<Serializable> eventMessage) {
        Processor postProcessor = controllerUtility.getPostProcessor(eventMessage);
        return postProcessor.process(eventMessage);
    }

    private EventMessage<Serializable> processRequestEvent(EventMessage<Serializable> eventMessage, ApiEndpoint apiEndpoint, HttpServletRequest request) {
        EventMessage<Serializable> response = null;

        try {
            if (apiEndpoint.getPathVariableName() != null) {
                eventMessage = controllerUtility.checkAndSetPathVariable(eventMessage, apiEndpoint, request.getRequestURI());
            }

            response = eventMessage.publishAndReceive();

            if (response.getStatus() == EventMessage.EventStatus.NOT_FOUND) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, response.getErrorMessage(), new ResourceNotFoundException(""));
            }

            return response;
        } catch (UnsupportedEncodingException ex) {
            throwBadRequestException(ex);
        } catch (ResponseStatusException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("Invalid server configuration");
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Invalid server configuration", ex);
        }
        return response;
    }
}