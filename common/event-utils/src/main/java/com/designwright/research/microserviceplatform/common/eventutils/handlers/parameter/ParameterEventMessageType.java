package com.designwright.research.microserviceplatform.common.eventutils.handlers.parameter;

import com.designwright.research.microserviceplatform.common.eventutils.EventMessage;

public class ParameterEventMessageType extends HandlerParameterType<EventMessage> {

    @Override
    public String getTypeName() {
        return EventMessage.class.getName();
    }

    @Override
    public Class<EventMessage> getType() {
        return EventMessage.class;
    }

    @Override
    public boolean isSameTypeCheck(String someValue) {
        return false;
    }

    @Override
    public Object castToType(String someValue) {
        return null;
    }
}
