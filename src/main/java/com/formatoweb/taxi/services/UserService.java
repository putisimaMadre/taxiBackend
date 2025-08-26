package com.formatoweb.taxi.services;

import com.formatoweb.taxi.dto.user.CreateUserRequest;
import com.formatoweb.taxi.models.Role;
import com.formatoweb.taxi.models.User;
import com.formatoweb.taxi.models.UserHasRoles;
import com.formatoweb.taxi.repositories.RoleRepository;
import com.formatoweb.taxi.repositories.UserHasRolesRepository;
import com.formatoweb.taxi.repositories.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserHasRolesRepository userHasRolesRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public User create(CreateUserRequest createUserRequest){
        if (userRepository.existsByEmail(createUserRequest.email)){
            throw new RuntimeException("El correo ya esta registrado");
        }
        User user = new User();
        user.setName(createUserRequest.name);
        user.setLastname(createUserRequest.lastname);
        user.setPhone(createUserRequest.phone);
        user.setEmail(createUserRequest.email);
        String encryptedPassword = passwordEncoder.encode(createUserRequest.password);
        user.setPassword(encryptedPassword);
        User savedUser = userRepository.save(user);
        Role clientRole = roleRepository.findById("CLIENT").orElseThrow(
                ()->new RuntimeException("El rol de cliente no existe")
        );
        UserHasRoles userHasRoles = new UserHasRoles(savedUser, clientRole);
        userHasRolesRepository.save(userHasRoles);
        return savedUser;
    }
}
