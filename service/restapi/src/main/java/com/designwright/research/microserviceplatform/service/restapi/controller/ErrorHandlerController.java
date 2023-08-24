package com.designwright.research.microserviceplatform.service.restapi.controller;

import com.designwright.research.microserviceplatform.common.eventutils.BasicEventMessage;
import com.designwright.research.microserviceplatform.common.eventutils.EventMessage;
import com.designwright.research.microserviceplatform.utils.exceptions.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

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

    private String getErrorMessageByStatus(HttpStatus httpStatus) {
        String errorMessage;
        switch (httpStatus.value()) {
            case 400:
                errorMessage = "400 Received bad request.";
                break;
            case 404:
                errorMessage = "404 The requested endpoint does not exist.";
                break;
            case 500:
                errorMessage = "500 There was an internal server error.";
                break;
            default:
                errorMessage = httpStatus.getReasonPhrase();
                break;
        }
        return errorMessage;
    }

    @GetMapping("/error")
    public ResponseEntity<EventMessage> handleError(HttpServletRequest request) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        EventMessage eventMessage;
        HttpStatus httpStatus;

        Object exception = request.getAttribute(DispatcherServlet.EXCEPTION_ATTRIBUTE);

        if (exception instanceof ResponseStatusException) {
            ResponseStatusException rsException = (ResponseStatusException) exception;
            httpStatus = rsException.getStatus();
            String errorMessage;
            if (!StringUtils.isEmpty(rsException.getReason())) {
                errorMessage = httpStatus.value() + " " + rsException.getReason();
            } else {
                errorMessage = getErrorMessageByStatus(httpStatus);
            }
            if (rsException.getCause() instanceof ResourceNotFoundException) {
                eventMessage = generateErrorResponse(request, errorMessage, true);
            } else {
                eventMessage = generateErrorResponse(request, errorMessage, false);
            }
        } else {
            httpStatus = HttpStatus.I_AM_A_TEAPOT;
            eventMessage = generateErrorResponse(request, "I am a teapot.", false);
        }

        logger.debug("HttpStatusCode: {}, Method: {}, URI: {}", status, request.getMethod(), request.getRequestURI());
        return new ResponseEntity<>(eventMessage, httpStatus);
    }

    @Override
    public String getErrorPath() {
        return "/error";
    }
}
