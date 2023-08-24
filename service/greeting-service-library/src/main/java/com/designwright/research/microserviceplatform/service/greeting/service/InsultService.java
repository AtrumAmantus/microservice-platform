package com.designwright.research.microserviceplatform.service.greeting.service;

import org.springframework.stereotype.Service;

@Service
public class InsultService {

    public String getSimpleInsult(String username) {
        return "I hate you, " + username + "!";
    }
}
