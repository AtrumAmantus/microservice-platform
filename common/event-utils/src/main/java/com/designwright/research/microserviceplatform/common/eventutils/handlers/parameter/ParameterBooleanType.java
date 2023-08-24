package com.designwright.research.microserviceplatform.common.eventutils.handlers.parameter;

import java.util.regex.Pattern;

public class ParameterBooleanType extends HandlerParameterType<Boolean> {

    private static final Pattern pattern = Pattern.compile("^true|false$");

    @Override
    public String getTypeName() {
        return Boolean.class.getName();
    }

    @Override
    public Class<Boolean> getType() {
        return Boolean.class;
    }

    @Override
    public boolean isSameTypeCheck(String someValue) {
        return pattern.matcher(someValue).find();
    }

    @Override
    public Object castToType(String someValue) {
        return Boolean.parseBoolean(someValue);
    }
}
