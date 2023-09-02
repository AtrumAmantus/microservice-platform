package com.designwright.research.microserviceplatform.service.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Random;

@Configuration
public class CommonBeanConfiguration {

    @Bean
    Random random() {
        return new Random();
    }

}
