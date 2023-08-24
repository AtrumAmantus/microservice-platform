package com.designwright.research.microserviceplatform.common.eventutils.handlers.parameter;

import java.util.regex.Pattern;

public class ParameterFloatType extends HandlerParameterType<Float> {

    private static final Pattern pattern = Pattern.compile("^[+\\-]?\\d+(\\.\\d{1,6})?$");

    @Override
    public String getTypeName() {
        return Float.class.getName();
    }

    @Override
    public Class<Float> getType() {
        return Float.class;
    }

    @Override
    public boolean isSameTypeCheck(String someValue) {
        return pattern.matcher(someValue).find();
    }

    @Override
    public Object castToType(String someValue) {
        return Float.parseFloat(someValue);
    }
}
