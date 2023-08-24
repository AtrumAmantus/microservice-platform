package com.designwright.research.microserviceplatform.service.config;

import com.designwright.research.microserviceplatform.common.eventutils.handlers.EventControllerStore;
import com.designwright.research.microserviceplatform.common.eventutils.handlers.EventHandler;
import com.designwright.research.microserviceplatform.common.eventutils.annotations.EventController;
import com.designwright.research.microserviceplatform.common.eventutils.annotations.EventMapping;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** Configures {@link EventController} based services to accept and respond to events. */
@RequiredArgsConstructor
@Configuration
public class EventConfiguration {

    /** Reference to the Spring {@link ApplicationContext}. */
    private final ApplicationContext context;

    /**
     * Builds a {@link EventControllerStore} that contains a definition of all the {@link EventController}s and
     * their contained {@link EventHandler} methods that  handle different event types.
     *
     * @return The created {@link EventControllerStore} object.
     */
    @Bean
    EventControllerStore commandHandlers() {
        EventControllerStore eventControllerStore = new EventControllerStore();

        List<Object> beans = new ArrayList<>(context.getBeansWithAnnotation(EventController.class).values());

        // For each spring bean annotated as a EventController
        for (Object bean : beans) {
            Object classInstance = context.getBean(bean.getClass());

            if (classInstance != null) {
                // Store metadata
                Set<String> eventTypes = new HashSet<>();
                Method[] beanMethods = bean.getClass().getMethods();
                String className = classInstance.getClass().getSimpleName();
                eventControllerStore.setController(className, classInstance);

                // Store details for each method annotated with EventMapping
                for (Method method : beanMethods) {
                    if (method.isAnnotationPresent(EventMapping.class)) {
                        String eventType = method.getAnnotation(EventMapping.class).value();
                        eventControllerStore.addHandlerForEvent(eventType, method);
                        eventTypes.add(eventType);
                    }
                }

                String eventMapping = classInstance.getClass().getAnnotation(EventController.class).context();
                eventControllerStore.addEventActions(eventMapping, eventTypes);
            }
        }

        return eventControllerStore;
    }
}
