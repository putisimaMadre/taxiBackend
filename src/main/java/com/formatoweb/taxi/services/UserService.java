package com.formatoweb.taxi.services;

import com.formatoweb.taxi.dto.user.*;
import com.formatoweb.taxi.dto.role.RoleDto;
import com.formatoweb.taxi.dto.user.mapper.UserMapper;
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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
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

    @Autowired
    private UserMapper userMapper;

    @Transactional
    public LoginResponse create(CreateUserRequest createUserRequest){
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

        String token = jwtUtil.generateToken(user);
        List<Role> roles = roleRepository.findAllByUserHasRoles_User_Id(savedUser.getId());

        LoginResponse response = new LoginResponse();
        response.setToken("Bearer " + token);
        response.setUser(userMapper.toUserResponse(user, roles));

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

        LoginResponse response = new LoginResponse();
        response.setToken("Bearer " + token);
        response.setUser(userMapper.toUserResponse(user, roles));

        return response;
    }

    @Transactional
    public UserResponse findById(Long id) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new RuntimeException("El email o password no son validos"));

        List<Role> roles = roleRepository.findAllByUserHasRoles_User_Id(user.getId());

        return userMapper.toUserResponse(user, roles);
    }

    @Transactional
    public UserResponse userUpdateWithImage(Long id, UpdateUserRequest request) throws IOException {
        User user = userRepository.findById(id).orElseThrow(
                () -> new RuntimeException("El email o password no son validos"));

        if (request.getName() != null){
            user.setName(request.getName());
        }
        if (request.getLastname() != null){
            user.setLastname(request.getLastname());
        }
        if (request.getPhone() != null){
            user.setPhone(request.getPhone());
        }
        if (request.getFile() != null && !request.getFile().isEmpty()){
            String uploadDir = "uploads/users/" + user.getId();
            String filename = request.getFile().getOriginalFilename();
            String filePath = Paths.get(uploadDir, filename).toString();

            Files.createDirectories(Paths.get(uploadDir));
            Files.copy(request.getFile().getInputStream(), Paths.get(filePath), StandardCopyOption.REPLACE_EXISTING);
            user.setImage("/"+filePath.replace("\\", "/"));
        }

        userRepository.save(user);

        List<Role> roles = roleRepository.findAllByUserHasRoles_User_Id(user.getId());


        return userMapper.toUserResponse(user, roles);
    }
}
