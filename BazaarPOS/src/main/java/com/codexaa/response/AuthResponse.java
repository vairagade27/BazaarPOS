package com.codexaa.response;

import com.codexaa.dto.UserDto;
import lombok.Data;

@Data
public class AuthResponse {


    private String jwt;
    private String  message;
    private UserDto userDto;
}
