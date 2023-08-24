package com.designwright.research.microserviceplatform.common.eventutils.handlers.parameter;

import java.util.regex.Pattern;

public class ParameterIntegerType extends HandlerParameterType<Integer> {

    private static final Pattern pattern = Pattern.compile("^[+\\-]?\\d+$");

    @Override
    public String getTypeName() {
        return Integer.class.getName();
    }

    @Override
    public Class<Integer> getType() {
        return Integer.class;
    }

    @Override
    public boolean isSameTypeCheck(String someValue) {
        if (pattern.matcher(someValue).find()) {
            long value = Long.parseLong(someValue);
            return value <= Integer.MAX_VALUE;
        }
        return false;
    }

    @Override
    public Object castToType(String someValue) {
        return Integer.parseInt(someValue);
    }
}
