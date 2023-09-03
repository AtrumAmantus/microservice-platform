package com.designwright.research.microserviceplatform.common.eventutils.handlers.parameter;

import com.designwright.research.microserviceplatform.common.eventutils.EventMessage;

import java.sql.Timestamp;

public class HandlerParameterFactory {

    private HandlerParameterFactory() {

    }

    public static <T> HandlerParameterType<?> createFromClassType(Class<T> clazz) {
        if (clazz.equals(String.class)) {
            return new ParameterStringType();
        } else if (clazz.equals(Integer.class) || (clazz.equals(int.class))) {
            return new ParameterIntegerType();
        } else if (clazz.equals(Long.class) || (clazz.equals(long.class))) {
            return new ParameterLongType();
        } else if (clazz.equals(Float.class) || (clazz.equals(float.class))) {
            return new ParameterFloatType();
        } else if (clazz.equals(Boolean.class) || (clazz.equals(boolean.class))) {
            return new ParameterBooleanType();
        } else if (clazz.equals(Timestamp.class)) {
            return new ParameterTimestampType();
        } else if (clazz.equals(EventMessage.class)) {
            return new ParameterEventMessageType();
        }

        throw new RuntimeException("Unknown parameter type");
    }
}
