package com.designwright.research.microserviceplatform.service.usermanagement.controller;

import com.designwright.research.microserviceplatform.common.eventutils.annotations.EventMapping;
import com.designwright.research.microserviceplatform.domain.User;
import com.designwright.research.microserviceplatform.service.usermanagement.service.UserService;
import com.designwright.research.microserviceplatform.common.eventutils.annotations.EventController;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
@EventController(context = "ServiceDataExchange.InteractionQueue")
public class UserController {

    private final UserService userService;

    @EventMapping("user.get.requested")
    public List<User> findAllUsers() {
        return userService.findAllUsers();
    }

    @EventMapping("user.get.requested")
    public User getUserById(Long id) {
        return userService.getUserById(id);
    }

//    @EventMapping("user.post.requested")
//    public Boolean createUser(User user) {
//        return userService.createUser(user);
//    }
}
