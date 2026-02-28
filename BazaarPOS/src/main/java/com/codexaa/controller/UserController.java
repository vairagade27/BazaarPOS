package com.codexaa.controller;

import com.codexaa.dto.UserDto;
import com.codexaa.exception.UserExceptions;
import com.codexaa.model.User;
import com.codexaa.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;


    @GetMapping("/profile")
    public ResponseEntity<UserDto> getUserProfile() throws Exception, UserExceptions {

        User user = userService.getCurrentUser();

        return ResponseEntity.ok(mapToDto(user));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long id) throws Exception {

        User user = userService.getUserById(id);

        return ResponseEntity.ok(mapToDto(user));
    }

    private UserDto mapToDto(User user) {

        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setFullName(user.getFullName());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole());
        dto.setPhone(user.getPhone());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());
        dto.setLastlogin(user.getLastLogin());

        return dto;
    }
}
