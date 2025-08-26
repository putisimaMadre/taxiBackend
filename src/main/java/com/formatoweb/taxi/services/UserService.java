package com.formatoweb.taxi.services;

import com.formatoweb.taxi.dto.user.CreateUserRequest;
import com.formatoweb.taxi.dto.user.CreateUserResponse;
import com.formatoweb.taxi.dto.role.RoleDto;
import com.formatoweb.taxi.dto.user.LoginRequest;
import com.formatoweb.taxi.dto.user.LoginResponse;
import com.formatoweb.taxi.models.Role;
import com.formatoweb.taxi.models.User;
import com.formatoweb.taxi.models.UserHasRoles;
import com.formatoweb.taxi.repositories.RoleRepository;
import com.formatoweb.taxi.repositories.UserHasRolesRepository;
import com.formatoweb.taxi.repositories.UserRepository;

import com.formatoweb.taxi.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

    @Autowired
    private JwtUtil jwtUtil;

    @Transactional
    public CreateUserResponse create(CreateUserRequest createUserRequest){
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

        CreateUserResponse response = new CreateUserResponse();
        response.setId(savedUser.getId());
        response.setName(savedUser.getName());
        response.setLastname(savedUser.getLastname());
        response.setImage(savedUser.getImage());
        response.setPhone(savedUser.getPhone());
        response.setEmail(savedUser.getEmail());

        List<Role> roles = roleRepository.findAllByUserHasRoles_User_Id(savedUser.getId());

        List<RoleDto> rolesDtos = roles.stream()
                        .map(role ->new RoleDto(role.getId(), role.getName(), role.getImage(), role.getRoute()))
                                .toList();
        response.setRoles(rolesDtos);
        return response;
    }

    @Transactional
    public LoginResponse login(LoginRequest request){
        User user = userRepository.findByEmail(request.getEmail()).orElseThrow(
                ()-> new RuntimeException("El email o password no son validos"));
        if(!passwordEncoder.matches(request.getPassword(), user.getPassword())){
            throw new RuntimeException("El email o password no son validos");
        }
        String token = jwtUtil.generateToken(user);
        List<Role> roles = roleRepository.findAllByUserHasRoles_User_Id(user.getId());
        List<RoleDto> rolesDtos = roles.stream()
                .map(role ->new RoleDto(role.getId(), role.getName(), role.getImage(), role.getRoute()))
                .toList();

        CreateUserResponse createUserResponse = new CreateUserResponse();
        createUserResponse.setId(user.getId());
        createUserResponse.setName(user.getName());
        createUserResponse.setLastname(user.getLastname());
        createUserResponse.setImage(user.getImage());
        createUserResponse.setPhone(user.getPhone());
        createUserResponse.setEmail(user.getEmail());
        createUserResponse.setRoles(rolesDtos);

        LoginResponse response = new LoginResponse();
        response.setToken("Bearer " + token);
        response.setUser(createUserResponse);

        return response;
    }

    @Transactional
    public CreateUserResponse findById(Long id) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new RuntimeException("El email o password no son validos"));

        List<Role> roles = roleRepository.findAllByUserHasRoles_User_Id(user.getId());
        List<RoleDto> rolesDtos = roles.stream()
                .map(role ->new RoleDto(role.getId(), role.getName(), role.getImage(), role.getRoute()))
                .toList();

        CreateUserResponse createUserResponse = new CreateUserResponse();
        createUserResponse.setId(user.getId());
        createUserResponse.setName(user.getName());
        createUserResponse.setLastname(user.getLastname());
        createUserResponse.setImage(user.getImage());
        createUserResponse.setPhone(user.getPhone());
        createUserResponse.setEmail(user.getEmail());
        createUserResponse.setRoles(rolesDtos);

        return createUserResponse;
    }
}
