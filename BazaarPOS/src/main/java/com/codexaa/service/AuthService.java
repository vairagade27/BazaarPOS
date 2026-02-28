package com.codexaa.service;

import com.codexaa.dto.UserDto;
import com.codexaa.exception.UserExceptions;
import com.codexaa.response.AuthResponse;

public interface AuthService {
    AuthResponse register(UserDto userDto) throws UserExceptions;
    AuthResponse login(UserDto userDto) throws UserExceptions;
}
