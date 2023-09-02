package com.designwright.research.microserviceplatform.common.eventutils;

import com.designwright.research.microserviceplatform.service.common.config.ServiceConfiguration;

/** Allows the publishing of {@link EventMessage}s */
public abstract class EventPublisher {

    /**
     * Publishes the given {@link EventMessage} object to a given exchange, with a given routing key.
     *
     * @param exchange   The exchange to publish to.
     * @param routingKey The routing key to use for publishing the message.
     * @param message    The message to publish.
     * @param <P>        The type of {@link EventMessage} to publish.
     */
    public <P extends EventMessage<?>> void publish(String exchange, String routingKey, P message) {
        message.setPublisherId(ServiceConfiguration.getApplicationId());
        publishImpl(exchange, routingKey, message);
    }

    /**
     * Publishes the given {@link EventMessage} object to a given exchange, with a given routing key.
     *
     * @param exchange   The exchange to publish to.
     * @param routingKey The routing key to use for publishing the message.
     * @param message    The message to publish.
     * @param <P>        The type of {@link EventMessage} to publish.
     */
    public abstract <P extends EventMessage<?>> void publishImpl(String exchange, String routingKey, P message);

    /**
     * Publishes the given {@link EventMessage} object to a given exchange, with a given routing key, and returns the
     * response {@link EventMessage}.
     *
     * @param exchange   The exchange to publish to.
     * @param routingKey The routing key to use for publishing the message.
     * @param message    The message to publish.
     * @param <P>        The type of {@link EventMessage} to publish.
     * @param <C>        The type of {@link EventMessage} to be returned.
     *
     * @return The response {@link EventMessage} from processing of the published {@link EventMessage}.
     */
    public <P extends EventMessage<?>, C extends EventMessage<?>> C publishAndReceive(
            String exchange,
            String routingKey,
            P message) {
        message.setPublisherId(ServiceConfiguration.getApplicationId());
        return publishAndReceiveImpl(exchange, routingKey, message);
    }

    /**
     * Publishes the given {@link EventMessage} object to a given exchange, with a given routing key, and returns the
     * response {@link EventMessage}.
     *
     * @param exchange   The exchange to publish to.
     * @param routingKey The routing key to use for publishing the message.
     * @param message    The message to publish.
     * @param <P>        The type of {@link EventMessage} to publish.
     * @param <C>        The type of {@link EventMessage} to be returned.
     *
     * @return The response {@link EventMessage} from processing of the published {@link EventMessage}.
     */
    public abstract <P extends EventMessage<?>, C extends EventMessage<?>> C publishAndReceiveImpl(
            String exchange,
            String routingKey,
            P message);

    /**
     * Returns the name of the exchange this publisher is associated with.
     *
     * TODO: This is rabbit specific, and too limiting.
     *
     * @return The name o the exchange.
     */
    public abstract String getExchange();

}
