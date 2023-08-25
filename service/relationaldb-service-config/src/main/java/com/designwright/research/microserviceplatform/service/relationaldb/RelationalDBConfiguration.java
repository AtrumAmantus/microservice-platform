package com.designwright.research.microserviceplatform.service.relationaldb;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

//@Configuration
@EnableJpaRepositories(basePackages = {"com.designwright.research.microserviceplatform.service"})
public class RelationalDBConfiguration {
}
