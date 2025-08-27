package com.formatoweb.taxi.controllers;

import com.formatoweb.taxi.dto.user.*;
import com.formatoweb.taxi.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping ("/users")
public class UserController {
    @Autowired
    private UserService userService;

    /*@PostMapping()
    public ResponseEntity<UserResponse> create(@RequestBody CreateUserRequest createUserRequest){
        UserResponse user = userService.create(createUserRequest);
        return ResponseEntity.ok(user);
    }*/

    @GetMapping(value = "/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id){
        try{
            UserResponse response = userService.findById(id);
            return ResponseEntity.ok(response);
        }catch (RuntimeException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "message", e.getMessage(), "statusCode", HttpStatus.NOT_FOUND.value()
            ));
        }
    }

    @PutMapping(value = "/upload/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @ModelAttribute UpdateUserRequest request){
        try{
            UserResponse response = userService.userUpdateWithImage(id, request);
            return ResponseEntity.ok(response);
        }catch (RuntimeException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "message", e.getMessage(), "statusCode", HttpStatus.NOT_FOUND.value()
            ));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "message", e.getMessage(), "statusCode", HttpStatus.INTERNAL_SERVER_ERROR.value()
            ));
        }
    }
}
