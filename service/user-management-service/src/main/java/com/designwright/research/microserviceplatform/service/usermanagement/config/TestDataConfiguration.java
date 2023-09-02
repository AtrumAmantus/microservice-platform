package com.designwright.research.microserviceplatform.service.usermanagement.config;

import com.designwright.research.microserviceplatform.domain.User;
import com.designwright.research.microserviceplatform.service.usermanagement.data.entity.UserEntity;
import com.designwright.research.microserviceplatform.service.usermanagement.data.respository.UserRepository;
import com.designwright.research.microserviceplatform.service.utils.MappingUtils;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Profile("h2")
@Configuration
public class TestDataConfiguration {

    private final UserRepository userRepository;
    private final MappingUtils mappingUtils;

    @PostConstruct
    public void init() throws IOException {
        log.info("Seeding test data...");

        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("seedData.json")) {
            List<User> seedUsers = mappingUtils.convertFromJsonList(inputStream, User.class);
            List<UserEntity> seedEntities = mappingUtils.convertToType(seedUsers, UserEntity.class);

            userRepository.saveAll(seedEntities);
        }
    }
}
