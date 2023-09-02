package com.designwright.research.microserviceplatform.service.config;

import com.designwright.research.microserviceplatform.common.eventutils.handlers.EventControllerStore;
import com.designwright.research.microserviceplatform.common.eventutils.EventMessage;
import com.designwright.research.microserviceplatform.common.eventutils.EventPublisher;
import com.designwright.research.microserviceplatform.common.rabbitutils.RabbitPublisher;
import com.designwright.research.microserviceplatform.service.common.config.ServiceConfiguration;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Declarable;
import org.springframework.amqp.core.Declarables;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionNameStrategy;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** RabbitMQ specific configurations required to make a connection to the RabbitMQ Event Broker. */
@Configuration
public class RabbitConfiguration {

    @Value("${spring.rabbitmq.host}")
    private String RABBIT_HOST;
    @Value("${spring.rabbitmq.port}")
    private int RABBIT_PORT;
    @Value("${spring.rabbitmq.username}")
    private String RABBIT_USER;
    @Value("${spring.rabbitmq.password}")
    private String RABBIT_PWD;

    /**
     * Creates the various Exchanges, Queues, and Bindings required to allow communication with the broker.
     *
     * @param eventControllerStore The store that defines the various controllers of this service.
     *
     * @return A {@link Declarables} that holds the defined Exchanges, Queues, and Bindings.
     */
    @Bean
    Declarables declarables(EventControllerStore eventControllerStore) {
        List<Declarable> declarables = new ArrayList<>();

        Set<String> createdExchangeNames = new HashSet<>();
        Set<String> createdQueueNames = new HashSet<>();

        for (Map.Entry<String, Set<String>> queueToEventTypeSet : eventControllerStore.getQueueToEventTypes().entrySet()) {
            // Context is expected to be in the format of "Exchange.Queue"
            String[] queuePath = queueToEventTypeSet.getKey().split("\\.");
            String exchangeName = queuePath[0];
            String queueName = queuePath[1];

            // Create Exchange
            TopicExchange exchange = new TopicExchange(exchangeName, false, false);

            if (!createdExchangeNames.contains(exchangeName)) {
                declarables.add(exchange);
                createdExchangeNames.add(exchangeName);
            }

            // Create Queue
            Queue queue = new Queue(queueName, false);

            if (!createdQueueNames.contains(queueName)) {
                declarables.add(queue);
                createdQueueNames.add(queueName);
            }

            // Bind each queue to the exchange for every supported event type.
            for (String eventType : queueToEventTypeSet.getValue()) {
                Binding binding = BindingBuilder.bind(queue).to(exchange).with(eventType);
                declarables.add(binding);
            }
        }

        return new Declarables(declarables);
    }

    /**
     * Creates a {@link SimpleMessageListenerContainer} that is used to define which queues will be
     * connected to by this service and how to handle {@link EventMessage}s that are pulled from them.
     *
     * @param connectionFactory    The {@link ConnectionFactory} used to connect to the broker.
     * @param listenerAdapter      The {@link MessageListener} used to define how to handle incoming {@link EventMessage}s.
     * @param eventControllerStore The store used to process incoming {@link EventMessage}s with the correct controller.
     *
     * @return The created {@link SimpleMessageListenerContainer}.
     */
    @Bean
    SimpleMessageListenerContainer container(
            ConnectionFactory connectionFactory,
            MessageListenerAdapter listenerAdapter,
            EventControllerStore eventControllerStore) {
        String[] queueNames = eventControllerStore.getQueueToEventTypes().keySet().stream()
                .map(s -> s.split("\\.")[1]).toArray(String[]::new);

        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames(queueNames);
        container.setMessageListener(listenerAdapter);
        return container;
    }

    /**
     * Create an adapter to be used for handling {@link EventMessage}s that come in from the broker.
     *
     * @param eventListener The listener that will be used to handle incoming {@link EventMessage}s.
     *
     * @return The created {@link MessageListenerAdapter} that spring will use to handle incoming
     * {@link EventMessage}s from the broker.
     */
    @Bean
    MessageListenerAdapter listenerAdapter(EventListener eventListener) {
        return new MessageListenerAdapter(eventListener, "processCommand");
    }

    /**
     * Creates a {@link ConnectionFactory} for connecting to the event broker.
     *
     * @param nameStrategy The naming strategy to use for identifying this service to the broker.
     *
     * @return The created {@link ConnectionFactory}.
     */
    @Bean
    ConnectionFactory connectionFactory(ConnectionNameStrategy nameStrategy) {
        CachingConnectionFactory cachingConnectionFactory = new CachingConnectionFactory();
        cachingConnectionFactory.setUsername(RABBIT_USER);
        cachingConnectionFactory.setPassword(RABBIT_PWD);
        cachingConnectionFactory.setHost(RABBIT_HOST);
        cachingConnectionFactory.setPort(RABBIT_PORT);
        cachingConnectionFactory.setRequestedHeartBeat(30);
        cachingConnectionFactory.setConnectionTimeout(30000);
        cachingConnectionFactory.setConnectionNameStrategy(nameStrategy);

        return cachingConnectionFactory;
    }

    /**
     * Creates and returns a {@link ConnectionNameStrategy} to be used for identifying connections
     * on the broker.
     *
     * @return The created {@link ConnectionNameStrategy}
     */
    @Bean
    ConnectionNameStrategy namingStrategy() {
        return connectionFactory -> ServiceConfiguration.getApplicationId();
    }

    /**
     * Creates a publisher to be used for publishing {@link EventMessage}s.
     * <p>
     * TODO: Readdress this design, setting a common publisher, and doing so in this way, does not
     *  seem like a great approach.
     *
     * @param rabbitTemplate The {@link RabbitTemplate} used to publish {@link EventMessage}s.
     *
     * @return The created {@link EventPublisher}.
     */
    @Bean
    EventPublisher eventPublisher(RabbitTemplate rabbitTemplate) {
        RabbitPublisher publisher = new RabbitPublisher(rabbitTemplate);
        EventMessage.setPublisher(publisher);
        return publisher;
    }
}
