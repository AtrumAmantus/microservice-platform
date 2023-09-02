package com.designwright.research.microserviceplatform.service.config;

import com.designwright.research.microserviceplatform.common.eventutils.handlers.EventControllerStore;
import com.designwright.research.microserviceplatform.common.eventutils.EventMessage;
import com.designwright.research.microserviceplatform.service.common.config.ServiceConfiguration;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.Serializable;

/** A listener that is used to handle incoming messaging events from the broker. */
@RequiredArgsConstructor
@Component
public class EventListener {

    private final EventControllerStore eventControllerStore;

    /**
     * Handles incoming events from the broker, invoking the store with the received message.
     *
     * @param eventMessage The {@link EventMessage} received from the broker.
     * @param <T>          The type of paylaod for the {@link EventMessage}
     *
     * @return The original {@link EventMessage}, processed, and potentially modified.
     */
    public <T extends Serializable> EventMessage<T> processCommand(EventMessage<T> eventMessage) {
        eventControllerStore.process(eventMessage);
        eventMessage.setPublisherId(ServiceConfiguration.getApplicationId());
        return eventMessage;
    }
}