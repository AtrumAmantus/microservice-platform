package com.designwright.research.microserviceplatform.common.eventutils.annotations;

import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Denotes a controller with various methods that are used to handle different events with certain available
 * parameters, similar to how a RestController defines how to handle a given http request.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface EventController {

    /**
     * Defines the context for controller, essentially where the events should be mapped from on the broker side.
     *
     * @return A String representing the context, such as an exchange/queue name.
     */
    String context();
}
