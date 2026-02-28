package com.codexaa.controller;

import com.codexaa.dto.UserDto;
import com.codexaa.exception.UserExceptions;
import com.codexaa.response.AuthResponse;
import com.codexaa.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:5173")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(
            @Valid @RequestBody UserDto userDto
    ) throws UserExceptions {

        AuthResponse response = authService.register(userDto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }


    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody UserDto userDto
    ) throws UserExceptions {
        AuthResponse response = authService.login(userDto);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
