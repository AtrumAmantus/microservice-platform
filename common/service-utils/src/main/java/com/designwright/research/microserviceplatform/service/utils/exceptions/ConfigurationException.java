package com.designwright.research.microserviceplatform.service.utils.exceptions;

public class ConfigurationException extends RuntimeException {

    public ConfigurationException(String environmentName) {
        super("No WebApi configuration defined for environment '" + environmentName + "'");
    }

}