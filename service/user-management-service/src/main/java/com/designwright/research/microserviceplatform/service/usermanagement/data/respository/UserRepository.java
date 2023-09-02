package com.designwright.research.microserviceplatform.service.usermanagement.data.respository;

import com.designwright.research.microserviceplatform.service.usermanagement.data.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findUserByUsername(String username);

    // TODO: Create a default implementation that throws a ResourceNotFound exception
    UserEntity getUserById(long id);

}
