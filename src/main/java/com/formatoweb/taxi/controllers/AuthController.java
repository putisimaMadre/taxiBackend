package com.formatoweb.taxi.controllers;

import com.formatoweb.taxi.dto.user.CreateUserRequest;
import com.formatoweb.taxi.models.User;
import com.formatoweb.taxi.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private UserService userService;

    @PostMapping()
    public ResponseEntity<User> create(@RequestBody CreateUserRequest createUserRequest){
        User user = userService.create(createUserRequest);
        return ResponseEntity.ok(user);
    }
}
