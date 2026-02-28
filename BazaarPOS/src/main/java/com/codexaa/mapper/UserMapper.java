package com.codexaa.mapper;

import com.codexaa.dto.UserDto;
import com.codexaa.model.User;

public class UserMapper {
    public static  UserDto mapToDto(User user) {

        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setFullName(user.getFullName());
        dto.setEmail(user.getEmail());
        dto.setPhone(user.getPhone());
        dto.setRole(user.getRole());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());
        dto.setLastlogin(user.getLastLogin());

        return dto;
    }
}
