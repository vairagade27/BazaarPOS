package com.codexaa.service.impl;

import com.codexaa.config.JwtProvider;
import com.codexaa.domain.UserRole;
import com.codexaa.dto.UserDto;
import com.codexaa.exception.UserExceptions;
import com.codexaa.model.User;
import com.codexaa.repository.UserRepository;
import com.codexaa.response.AuthResponse;
import com.codexaa.service.AuthService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static com.codexaa.mapper.UserMapper.mapToDto;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final CustomUserImplementation customUserDetailsService;

    public AuthServiceImpl(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtProvider jwtProvider,
            CustomUserImplementation customUserDetailsService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtProvider = jwtProvider;
        this.customUserDetailsService = customUserDetailsService;
    }

    // ================= REGISTER =================
    @Override
    public AuthResponse register(UserDto userDto) throws UserExceptions {

        if (userRepository.findByEmail(userDto.getEmail()) != null) {
            throw new UserExceptions("Email already registered");
        }

        if (userDto.getRole() == UserRole.ROLE_ADMIN) {
            throw new UserExceptions("Admin role is not allowed");
        }

        User user = new User();
        user.setEmail(userDto.getEmail());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setFullName(userDto.getFullName());
        user.setPhone(userDto.getPhone());
        user.setRole(userDto.getRole());
        user.setCreatedAt(LocalDateTime.now());

        User savedUser = userRepository.save(user);

        UserDetails userDetails =
                customUserDetailsService.loadUserByUsername(savedUser.getEmail());

        Authentication authentication =
                new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = jwtProvider.generateToken(authentication);

        return buildAuthResponse(savedUser, jwt, "User registered successfully");
    }


    @Override
    public AuthResponse login(UserDto userDto) throws UserExceptions {

        Authentication authentication =
                authenticate(userDto.getEmail(), userDto.getPassword());

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = jwtProvider.generateToken(authentication);

        User user = userRepository.findByEmail(userDto.getEmail());
        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);

        return buildAuthResponse(user, jwt, "Login successful");
    }


    private Authentication authenticate(String email, String password)
            throws UserExceptions {

        UserDetails userDetails =
                customUserDetailsService.loadUserByUsername(email);

        if (userDetails == null) {
            throw new UserExceptions("User doesn't exist");
        }

        if (!passwordEncoder.matches(password, userDetails.getPassword())) {
            throw new UserExceptions("Invalid password");
        }

        return new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );
    }

    // ================= COMMON RESPONSE BUILDER =================
    private AuthResponse buildAuthResponse(User user, String jwt, String message) {

        UserDto dto = mapToDto(user);

        AuthResponse response = new AuthResponse();
        response.setJwt(jwt);
        response.setMessage(message);
        response.setUserDto(dto);

        return response;
    }

    // ================= DTO MAPPER =================

}
