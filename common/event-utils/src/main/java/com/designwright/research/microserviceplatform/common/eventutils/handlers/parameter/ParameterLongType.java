package com.designwright.research.microserviceplatform.common.eventutils.handlers.parameter;

import java.util.regex.Pattern;

public class ParameterLongType extends HandlerParameterType<Long> {

    private static final Pattern pattern = Pattern.compile("^[+\\-]?\\d+$");

    @Override
    public String getTypeName() {
        return Long.class.getName();
    }

    @Override
    public Class<Long> getType() {
        return Long.class;
    }

    @Override
    public boolean isSameTypeCheck(String someValue) {
        return pattern.matcher(someValue).find();
    }

    @Override
    public Object castToType(String someValue) {
        return Long.parseLong(someValue);
    }
}
