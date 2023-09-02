package com.designwright.research.microserviceplatform.common.rabbitutils;

import com.designwright.research.microserviceplatform.common.eventutils.EventMessage;
import com.designwright.research.microserviceplatform.common.eventutils.EventMessageFactory;
import com.designwright.research.microserviceplatform.common.eventutils.EventPublisher;
import com.designwright.research.microserviceplatform.common.eventutils.exceptions.InvalidMessageTypeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpTimeoutException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

/**
 * RabbitMQ specific implementation of the {@link EventPublisher}, defining how to publish events to the
 * RabbitMQ broker.
 */
@Slf4j
@RequiredArgsConstructor
public class RabbitPublisher extends EventPublisher {

    private static final String MESSAGE_REPLY_TIMED_OUT = "Message reply timed out.";
    private static final String THERE_WAS_AN_ISSUE_RECEIVING_THE_MESSAGE = "There was an issue receiving the message.";
    // TODO: Hardcode bad.
    private static final String EXCHANGE_NAME = "ServiceDataExchange";

    /** Reference to the spring oriented rabbit client */
    private final RabbitTemplate rabbitTemplate;

    @Override
    public <P extends EventMessage<?>> void publishImpl(String exchange, String routingKey, P message) {
        try {
            rabbitTemplate.convertAndSend(exchange, routingKey, message);
        } catch (AmqpTimeoutException ex) {
            log.error(MESSAGE_REPLY_TIMED_OUT, ex);
        } catch (Exception ex) {
            log.error(THERE_WAS_AN_ISSUE_RECEIVING_THE_MESSAGE, ex);
        }
    }

    @Override
    public EventMessage publishAndReceiveImpl(String exchange, String routingKey, EventMessage message) {
        EventMessage response;
        Object responseObject;
        try {
            responseObject = rabbitTemplate.convertSendAndReceive(exchange, routingKey, message);

            if (responseObject instanceof EventMessage) {
                response = (EventMessage) responseObject;
            } else if (responseObject != null) {
                throw new InvalidMessageTypeException("The returned message was not a valid type (" + responseObject.getClass().getSimpleName() + "); EventMessage expected.");
            } else {
                throw new AmqpTimeoutException("timeout.");
            }
        } catch (AmqpTimeoutException ex) {
            log.error(MESSAGE_REPLY_TIMED_OUT, ex);
            response = EventMessageFactory.clone(message);
            response.setStatus(EventMessage.EventStatus.FAIL);
            response.setErrorMessage(MESSAGE_REPLY_TIMED_OUT);
        } catch (Exception ex) {
            log.error(THERE_WAS_AN_ISSUE_RECEIVING_THE_MESSAGE, ex);
            response = EventMessageFactory.clone(message);
            response.setStatus(EventMessage.EventStatus.FAIL);
            response.setErrorMessage(THERE_WAS_AN_ISSUE_RECEIVING_THE_MESSAGE);
        }

        return response;
    }

    @Override
    public String getExchange() {
        return EXCHANGE_NAME;
    }
}
