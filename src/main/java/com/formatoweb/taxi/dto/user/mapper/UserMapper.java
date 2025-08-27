package com.formatoweb.taxi.dto.user.mapper;

import com.formatoweb.taxi.config.APIConfig;
import com.formatoweb.taxi.dto.role.RoleDto;
import com.formatoweb.taxi.dto.user.UserResponse;
import com.formatoweb.taxi.models.Role;
import com.formatoweb.taxi.models.User;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserMapper {
    public UserResponse toUserResponse(User user, List<Role> roles){
        List<RoleDto> rolesDtos = roles.stream()
                .map(role ->new RoleDto(role.getId(), role.getName(), role.getImage(), role.getRoute()))
                .toList();

        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setName(user.getName());
        response.setLastname(user.getLastname());
        response.setPhone(user.getPhone());
        response.setEmail(user.getEmail());
        response.setRoles(rolesDtos);

        if (user.getImage() != null){
            String imageUrl = APIConfig.BASE_URL + user.getImage();
            response.setImage(imageUrl);
        }
        return response;
    }
}
