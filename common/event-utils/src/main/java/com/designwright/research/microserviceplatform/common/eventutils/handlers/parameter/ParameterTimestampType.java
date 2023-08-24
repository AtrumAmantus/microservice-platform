package com.designwright.research.microserviceplatform.common.eventutils.handlers.parameter;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.regex.Pattern;

public class ParameterTimestampType extends HandlerParameterType<Timestamp> {

    private static final Pattern pattern = Pattern.compile("^[+\\-]?\\d+$");

    @Override
    public String getTypeName() {
        return Timestamp.class.getName();
    }

    @Override
    public Class<Timestamp> getType() {
        return Timestamp.class;
    }

    @Override
    public boolean isSameTypeCheck(String someValue) {
        return pattern.matcher(someValue).find();
    }

    @Override
    public Object castToType(String someValue) {
        return Timestamp.from(Instant.ofEpochMilli(Long.parseLong(someValue)));
    }
}
