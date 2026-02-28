package com.codexaa.dto;

import com.codexaa.domain.UserRole;
import lombok.Data;


import java.time.LocalDateTime;
@Data
public class UserDto {


    private Long id;

    private String fullName;

    private String email;
 private  String password;
    private String phone;
    private UserRole role;

    private LocalDateTime createdAt;
    private  LocalDateTime updatedAt;
    private LocalDateTime lastlogin;
}
