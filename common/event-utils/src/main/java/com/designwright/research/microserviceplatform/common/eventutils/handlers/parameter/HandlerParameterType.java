package com.designwright.research.microserviceplatform.common.eventutils.handlers.parameter;

public abstract class HandlerParameterType<T> {

    public abstract String getTypeName();

    public abstract Class<T> getType();

    public boolean isSameType(String someValue) {
        return someValue != null && isSameTypeCheck(someValue);
    }

    protected abstract boolean isSameTypeCheck(String someValue);

    public abstract Object castToType(String someValue);
}
