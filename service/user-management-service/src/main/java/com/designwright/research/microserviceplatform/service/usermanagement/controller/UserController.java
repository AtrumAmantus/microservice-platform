package com.designwright.research.microserviceplatform.service.usermanagement.controller;

import com.designwright.research.microserviceplatform.common.eventutils.EventMessage;
import com.designwright.research.microserviceplatform.common.eventutils.annotations.EventMapping;
import com.designwright.research.microserviceplatform.domain.User;
import com.designwright.research.microserviceplatform.service.usermanagement.service.UserService;
import com.designwright.research.microserviceplatform.common.eventutils.annotations.EventController;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
@EventController(context = "ServiceDataExchange.UserServiceQueue")
public class UserController {

    private final UserService userService;

    @EventMapping("users.get.requested")
    public List<User> findAllUsers() {
        return userService.findAllUsers();
    }

    @EventMapping("users.get.requested")
    public User getUserById(Long id) {
        return userService.getUserById(id);
    }

    @EventMapping("users.create.requested")
    public boolean createUser(EventMessage<User> eventMessage) {
        return userService.createUser(eventMessage.getPayload().get(0));
    }

}
