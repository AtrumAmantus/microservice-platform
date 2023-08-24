package com.designwright.research.microserviceplatform.common.eventutils.exceptions;

public class InvalidMessageTypeException extends RuntimeException {
    public InvalidMessageTypeException(String message) {
        super(message);
    }
}
