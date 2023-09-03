package com.designwright.research.microserviceplatform.service.usermanagement.service;

import com.designwright.research.microserviceplatform.domain.User;
import com.designwright.research.microserviceplatform.service.usermanagement.data.respository.UserRepository;
import com.designwright.research.microserviceplatform.service.utils.MappingUtils;
import com.designwright.research.microserviceplatform.service.utils.exceptions.ResourceNotFoundException;
import com.designwright.research.microserviceplatform.service.usermanagement.data.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final MappingUtils mappingUtils;

    public List<User> findAllUsers() {
        return mappingUtils.convertToType(userRepository.findAll(), User.class);
    }

    public User getUserById(Long id) {
        Optional<UserEntity> userEntity = userRepository.findById(id);

        if (userEntity.isEmpty()) {
            throw new ResourceNotFoundException("Resource not found");
        }

        return mappingUtils.convertToType(userEntity.get(), User.class);
    }

    public boolean createUser(User user) {
        Optional<UserEntity> existingUser = userRepository
                .findUserByUsername(user.getUsername().toLowerCase(Locale.ROOT));

        if (existingUser.isPresent()) {
            return false;
        } else {
            UserEntity userEntity = mappingUtils.convertToType(user, UserEntity.class);

            userRepository.save(userEntity);
        }

        return true;
    }
}
