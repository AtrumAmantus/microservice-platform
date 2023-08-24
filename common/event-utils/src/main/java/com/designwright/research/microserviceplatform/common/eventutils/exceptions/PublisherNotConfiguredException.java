package com.designwright.research.microserviceplatform.common.eventutils.exceptions;

public class PublisherNotConfiguredException extends RuntimeException {
    public enum Reason {
        NOT_CONFIGURED("The publisher has not been configured.");

        private final String message;

        Reason(String message) {
            this.message = message;
        }

        @Override
        public String toString() {
            return message;
        }
    }

    public PublisherNotConfiguredException(Reason reason) {
        super(reason.toString());
    }
}
