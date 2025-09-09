package com.example.team6_backend.controller;

import com.example.team6_backend.entity.User;
import com.example.team6_backend.service.UserService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {
    private final UserService userService;
    public UserController(UserService userService) {
        this.userService = userService;
    }
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public Integer update(@RequestBody User user) {
        return userService.updateUser(user);
    }

}
