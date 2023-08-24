package com.designwright.research.microserviceplatform.common.eventutils;

import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/** Basic implementation of the {@link EventMessage} abstract class */
@EqualsAndHashCode(callSuper = true)
public class BasicEventMessage<T extends Serializable> extends EventMessage<T> {

    private BasicEventMessage() {
        super();
    }

    private BasicEventMessage(EventMessage<T> eventMessage) {
        super(eventMessage);
    }

    public static <T extends Serializable> BasicEventMessage<T> from(BasicEventMessage<T> eventMessage) {
        return new BasicEventMessage<>(eventMessage);
    }

    public static <T extends Serializable> Builder<T> builder(String eventType) {
        Builder<T> builder = new Builder<>();
        builder.eventType = eventType;
        return builder;
    }

    public static class Builder<T extends Serializable> {

        private String eventType;
        private Map<String, String> parameters;
        private String message;
        private EventStatus status;
        private T payload;

        public static <T extends Serializable> BasicEventMessage<T> from(EventMessage<T> eventMessage) {
            return new BasicEventMessage<>(eventMessage);
        }

        public Builder<T> parameters(Map<String, String> parameters) {
            this.parameters = new HashMap<>(parameters);
            return this;
        }

        public Builder<T> parameter(String parameterName, String parameterValue) {
            if (parameters == null) {
                parameters = new HashMap<>();
            }
            parameters.put(parameterName, parameterValue);
            return this;
        }

        public Builder<T> message(String message) {
            this.message = message;
            return this;
        }

        public Builder<T> status(EventStatus status) {
            this.status = status;
            return this;
        }

        public Builder<T> payload(T payload) {
            this.payload = payload;
            return this;
        }

        public BasicEventMessage<T> build() {
            BasicEventMessage<T> eventMessage = new BasicEventMessage<>();
            eventMessage.setEventType(eventType);
            eventMessage.addParameterValues(parameters);
            eventMessage.setStatus(status);
            eventMessage.setErrorMessage(message);
            eventMessage.setPayload(payload);
            return eventMessage;
        }
    }

}