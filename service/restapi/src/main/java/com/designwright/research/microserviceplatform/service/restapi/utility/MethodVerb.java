package com.designwright.research.microserviceplatform.service.restapi.utility;

public enum MethodVerb {
    GET("get"),
    POST("create"),
    PUT("update"),
    DELETE("delete");

    private final String eventVerb;

    MethodVerb(String eventVerb) {
        this.eventVerb = eventVerb;
    }

    public String getVerb() {
        return eventVerb;
    }
}
