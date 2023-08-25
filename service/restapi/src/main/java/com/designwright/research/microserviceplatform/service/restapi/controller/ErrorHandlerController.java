package com.designwright.research.microserviceplatform.service.restapi.controller;

import com.designwright.research.microserviceplatform.common.eventutils.BasicEventMessage;
import com.designwright.research.microserviceplatform.common.eventutils.EventMessage;
import com.designwright.research.microserviceplatform.service.utils.exceptions.ResourceNotFoundException;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.DispatcherServlet;

@Controller
public class ErrorHandlerController implements ErrorController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private BasicEventMessage generateErrorResponse(HttpServletRequest request, String errorMessage, boolean notFound) {
        return BasicEventMessage.<String>builder("endpoint.provided")
                .parameter("uriRequest", request.getAttribute(RequestDispatcher.FORWARD_REQUEST_URI).toString())
                .status(
                        notFound ? EventMessage.EventStatus.NOT_FOUND : EventMessage.EventStatus.FAIL
                )
                .message(errorMessage)
                .build();
    }

    private String getErrorMessageByStatus(HttpStatusCode httpStatusCode, String defaultReason) {
        return switch (httpStatusCode.value()) {
            case 400 -> "400 Received bad request.";
            case 404 -> "404 The requested endpoint does not exist.";
            case 500 -> "500 There was an internal server error.";
            default -> defaultReason;
        };
    }

    @GetMapping("/error")
    public ResponseEntity<EventMessage> handleError(HttpServletRequest request) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        EventMessage eventMessage;
        HttpStatusCode httpStatusCode;

        Object exception = request.getAttribute(DispatcherServlet.EXCEPTION_ATTRIBUTE);

        if (exception instanceof ResponseStatusException rsException) {
            httpStatusCode = rsException.getStatusCode();
            String errorMessage;

            if (!StringUtils.isEmpty(rsException.getReason())) {
                errorMessage = httpStatusCode.value() + " " + rsException.getReason();
            } else {
                errorMessage = getErrorMessageByStatus(httpStatusCode, rsException.getReason());
            }

            if (rsException.getCause() instanceof ResourceNotFoundException) {
                eventMessage = generateErrorResponse(request, errorMessage, true);
            } else {
                eventMessage = generateErrorResponse(request, errorMessage, false);
            }
        } else {
            httpStatusCode = HttpStatus.I_AM_A_TEAPOT;
            eventMessage = generateErrorResponse(request, "I am a teapot.", false);
        }

        logger.debug("HttpStatusCode: {}, Method: {}, URI: {}", status, request.getMethod(), request.getRequestURI());
        return new ResponseEntity<>(eventMessage, httpStatusCode);
    }
}
