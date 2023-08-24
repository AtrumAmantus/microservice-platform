package com.designwright.research.microserviceplatform.common.eventutils.exceptions;

public class UnsupportedEventMessageException extends RuntimeException {

    public <T> UnsupportedEventMessageException(Class<T> classType) {
        super("Unsupported type for event message: " + classType);
    }

}
