package com.designwright.research.microserviceplatform.common.eventutils;

import com.designwright.research.microserviceplatform.common.eventutils.exceptions.PublisherNotConfiguredException;
import lombok.Data;
import lombok.Setter;
import org.apache.commons.lang3.SerializationUtils;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/** Contains the relevant payload and metadata for an event passed around between services */
@Data
public abstract class EventMessage<T extends Serializable> implements Serializable {

    @Serial
    private static final long serialVersionUID = 8098058613268126284L;

    /** Denotes the status of the event */
    public enum EventStatus {
        /** Event is still needing to be processed */
        PENDING,
        /** Event was successfully processed */
        SUCCESS,
        /** Event failed to process */
        FAIL,
        /** Event processed, but with potential issues */
        WARNING,
        /** The event was not processable by anything */
        NOT_FOUND
    }

    /** Unique identifier to track the life of an event */
    private final UUID referenceId;
    /** Parameters associated with the event */
    private final Map<String, String> parameterValues;
    /** The type of event associated with this message */
    private String eventType;
    /** The ID for the service that published this message */
    private String publisherId;
    /** Any error or warning message that may be related to this event's processing */
    private String errorMessage;
    /** The current {@link EventStatus} of the message */
    private EventStatus status;
    /** The payload of the message */
    private List<T> payload;

    @Setter
    private static EventPublisher publisher;

    protected EventMessage() {
        this.parameterValues = new HashMap<>();
        this.referenceId = generateReferenceId();
    }

    protected EventMessage(EventMessage<T> eventMessage) {
        this.parameterValues = SerializationUtils.clone(new HashMap<>(eventMessage.parameterValues));
        this.referenceId = eventMessage.referenceId;
        this.eventType = eventMessage.eventType;
        this.errorMessage = eventMessage.errorMessage;
        this.status = eventMessage.status;
        this.payload = eventMessage.payload;
    }

    private UUID generateReferenceId() {
        return UUID.randomUUID();
    }

    public void addParameterValues(Map<String, String> parameterValues) {
        if (parameterValues != null) {
            this.parameterValues.putAll(parameterValues);
        }
    }

    public void addParameter(String parameterName, String parameterValue) {
        this.parameterValues.put(parameterName, parameterValue);
    }

    public void publish(String exchange, String routingKey) {
        if (publisher != null) {
            publisher.publish(exchange, routingKey, this);
        }

        throw new PublisherNotConfiguredException(PublisherNotConfiguredException.Reason.NOT_CONFIGURED);
    }

    public EventMessage<T> publishAndReceive() {
        if (publisher != null) {
            return publisher.publishAndReceive(publisher.getExchange(), this.eventType, this);
        }

        throw new PublisherNotConfiguredException(PublisherNotConfiguredException.Reason.NOT_CONFIGURED);
    }
}
