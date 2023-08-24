package com.designwright.research.microserviceplatform.service.restapi.processing;

import com.designwright.research.microserviceplatform.common.eventutils.EventMessage;

import java.io.Serializable;

public interface Processor {

    <T extends Serializable> EventMessage<T> process(EventMessage<T> eventMessage);
}
