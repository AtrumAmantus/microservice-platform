package com.designwright.research.microserviceplatform.common.eventutils;

import com.designwright.research.microserviceplatform.common.eventutils.exceptions.UnsupportedEventMessageException;

import java.io.Serializable;

public class EventMessageFactory {

    private EventMessageFactory() {

    }

    public static <T extends Serializable> EventMessage<T> clone(EventMessage<T> eventMessage) {
        if (eventMessage.getClass().isAssignableFrom(BasicEventMessage.class)) {
            return BasicEventMessage.from((BasicEventMessage) eventMessage);
        } else {
            throw new UnsupportedEventMessageException(eventMessage.getClass());
        }
    }
}
