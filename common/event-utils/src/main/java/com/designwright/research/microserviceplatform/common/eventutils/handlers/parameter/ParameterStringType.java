package com.designwright.research.microserviceplatform.common.eventutils.handlers.parameter;

public class ParameterStringType extends HandlerParameterType<String> {

    @Override
    public String getTypeName() {
        return String.class.getName();
    }

    @Override
    public Class<String> getType() {
        return String.class;
    }

    @Override
    public boolean isSameTypeCheck(String someValue) {
        return true;
    }

    @Override
    public Object castToType(String someValue) {
        return someValue;
    }
}
