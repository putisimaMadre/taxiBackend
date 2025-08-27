package com.formatoweb.taxi.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.formatoweb.taxi.dto.role.RoleDto;
import lombok.Data;

import java.util.List;

@Data
public class UserResponse {
    public Long id;
    public String name;
    public String lastname;
    public String email;
    public String phone;
    public String image;

    @JsonProperty("notification_token")
    public String notificationToken;

    List<RoleDto> roles;
}
