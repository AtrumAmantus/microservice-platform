package com.designwright.research.microserviceplatform.service.restapi.processing;

import com.designwright.research.microserviceplatform.common.eventutils.EventMessage;

import java.io.Serializable;

public class DefaultProcessor implements Processor {

    public <T extends Serializable> EventMessage<T> process(EventMessage<T> eventMessage) {
        return eventMessage;
    }
}