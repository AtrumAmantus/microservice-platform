package com.designwright.research.microserviceplatform.common.eventutils.annotations;

import com.designwright.research.microserviceplatform.common.eventutils.handlers.EventHandler;
import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Denotes a method that will act as an {@link EventHandler} for incoming events of the same event type with
 * the same parameters.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface EventMapping {

    /** Denotes the event type that this handler is to be used for */
    String value();
}
