package com.designwright.research.microserviceplatform.service.greeting.controller;

import com.designwright.research.microserviceplatform.common.eventutils.annotations.EventMapping;
import com.designwright.research.microserviceplatform.service.greeting.service.GreetingService;
import com.designwright.research.microserviceplatform.common.eventutils.annotations.EventController;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.Timestamp;

@EventController(context = "ServiceDataExchange.InteractionQueue")
public class GreetingController {

    private final GreetingService greetingService;

    @Autowired
    public GreetingController(GreetingService greetingService) {
        this.greetingService = greetingService;
    }

    @EventMapping("greeting.get.requested")
    public String getGreeting(String username) {
        return greetingService.getSimpleGreeting(username);
    }

    @EventMapping("greeting.get.requested")
    public String getMultipleGreetings(String username, Integer repeats) {
        return greetingService.getRepeatedGreeting(username, repeats);
    }

    @EventMapping("greeting.get.requested")
    public String getDatedGreeting(String username, Timestamp date) {
        return greetingService.getDatedGreeting(username, date);
    }

    @EventMapping("greeting.post.requested")
    public String getDatedGreeting(String username) {
        return greetingService.getPostedGreeting(username);
    }
}
