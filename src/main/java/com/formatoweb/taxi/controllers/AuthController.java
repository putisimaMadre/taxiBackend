package com.formatoweb.taxi.controllers;

import com.formatoweb.taxi.dto.user.CreateUserRequest;
import com.formatoweb.taxi.dto.user.CreateUserResponse;
import com.formatoweb.taxi.dto.user.LoginRequest;
import com.formatoweb.taxi.dto.user.LoginResponse;
import com.formatoweb.taxi.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private UserService userService;

    @PostMapping(value = "/register")
    public ResponseEntity<?> create(@RequestBody CreateUserRequest createUserRequest){
        try{
            CreateUserResponse user = userService.create(createUserRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(user);
        }catch (RuntimeException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "message", e.getMessage(), "statusCode", HttpStatus.BAD_REQUEST.value()
            ));
        }
    }

    @PostMapping(value = "/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest){
        try{
            LoginResponse response = userService.login(loginRequest);
            return ResponseEntity.ok(response);
        }catch (RuntimeException e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                    "message", e.getMessage(), "statusCode", HttpStatus.UNAUTHORIZED.value()
            ));
        }
    }
}
