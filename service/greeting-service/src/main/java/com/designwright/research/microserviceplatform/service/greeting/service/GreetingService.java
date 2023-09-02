package com.designwright.research.microserviceplatform.service.greeting.service;

import org.springframework.stereotype.Service;

import java.sql.Timestamp;

@Service
public class GreetingService {

    public String getSimpleGreeting(String username) {
        return "Why, hello there, " + username;
    }

    public String getRepeatedGreeting(String username, Integer repeats) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < repeats; i++) {
            if (!sb.isEmpty()) {
                sb.append(", ");
            }
            sb.append("hello");
        }

        sb.append(" ").append(username);

        return sb.toString();
    }

    public String getDatedGreeting(String username, Timestamp date) {
        return "Hey " + username + ", haven't seen you since " + date;
    }

    public String getPostedGreeting(String username) {
        return "Okay, I'll get that for you right away " + username;
    }
}
