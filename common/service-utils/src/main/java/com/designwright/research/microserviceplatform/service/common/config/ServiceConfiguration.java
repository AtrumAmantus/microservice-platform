package com.designwright.research.microserviceplatform.service.common.config;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

@RequiredArgsConstructor
@Configuration
public class ServiceConfiguration {

    @Getter
    private static String applicationId;

    static final String[] namePrefixes = Set.of(
            "Boring",
            "Silly",
            "Crazy",
            "Happy",
            "Talented",
            "Snoozing",
            "Huge",
            "Terrible",
            "Terrific",
            "Snazzy",
            "Spiffy").toArray(new String[0]);

    static final String[] nameSuffixes = Set.of(
            "Walnut",
            "Grape",
            "Rhino",
            "Elephant",
            "Apple",
            "Rainbow",
            "Butterfly",
            "Mushroom",
            "Tomato",
            "Alligator",
            "Hippo").toArray(new String[0]);

    private final Environment environment;
    private final Random random;

    @PostConstruct
    public void init() {
        String appId = Optional.ofNullable(environment.getProperty("spring.application.name"))
                .filter(StringUtils::isNotEmpty)
                .orElse(generateServiceName());

        // TODO: This is a hack, fix later
        applicationId = appId + "_" + UUID.randomUUID().toString().substring(0, 8);
    }

    private String generateServiceName() {
        return namePrefixes[random.nextInt(namePrefixes.length)] + "_"
                + nameSuffixes[random.nextInt(nameSuffixes.length)];
    }

}
