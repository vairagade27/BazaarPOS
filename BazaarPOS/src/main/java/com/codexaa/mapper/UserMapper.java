package com.codexaa.mapper;

import com.codexaa.dto.UserDto;
import com.codexaa.model.User;

public class UserMapper {

    public static UserDto mapToDto(User user) {

        UserDto dto = new UserDto();

        dto.setId(user.getId());
        dto.setFullName(user.getFullName());
        dto.setEmail(user.getEmail());
        dto.setPhone(user.getPhone());
        dto.setRole(user.getRole());

        // ✅ Fix: Map branchId and storeId
        dto.setBranchId(user.getBranch() != null ? user.getBranch().getId() : null);
        dto.setStoreId(user.getStore() != null ? user.getStore().getId() : null);

        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());
        dto.setLastlogin(user.getLastLogin());

        return dto;
    }

    public static User toEntity(UserDto userDto){

        User createUser = new User();

        createUser.setEmail(userDto.getEmail());
        createUser.setFullName(userDto.getFullName());
        createUser.setRole(userDto.getRole());
        createUser.setPhone(userDto.getPhone());

        // ❌ Do NOT set createdAt / updatedAt manually
        // ❌ Do NOT set lastLogin manually
        // These are system controlled fields

        createUser.setPassword(userDto.getPassword());

        return createUser;
    }
}