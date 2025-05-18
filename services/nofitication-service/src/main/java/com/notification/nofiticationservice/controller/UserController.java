package com.notification.nofiticationservice.controller;

import com.notification.nofiticationservice.entity.User;
import com.notification.nofiticationservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/update")
    public String updateUser(@RequestBody User user) {
        User existingUser = userRepository.findById(user.getUserId()).orElse(new User());
        existingUser.setUserId(user.getUserId());
        existingUser.setEmail(user.getEmail());
        userRepository.save(existingUser);
        return "User updated in notification-service";
    }
}
