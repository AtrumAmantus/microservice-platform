package com.designwright.research.microserviceplatform.service.greeting.controller;

import com.designwright.research.microserviceplatform.common.eventutils.annotations.EventMapping;
import com.designwright.research.microserviceplatform.service.greeting.service.InsultService;
import com.designwright.research.microserviceplatform.common.eventutils.annotations.EventController;
import org.springframework.beans.factory.annotation.Autowired;

@EventController(context = "ServiceDataExchange.InsultServiceQueue")
public class InsultController {

    public final InsultService insultService;

    @Autowired
    public InsultController(InsultService insultService) {
        this.insultService = insultService;
    }

    @EventMapping("insult.get.requested")
    public String getInsult(String username) {
        return insultService.getSimpleInsult(username);
    }
}