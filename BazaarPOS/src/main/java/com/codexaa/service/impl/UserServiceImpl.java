package com.codexaa.service.impl;

import com.codexaa.config.JwtProvider;
import com.codexaa.exception.UserExceptions;
import com.codexaa.model.User;
import com.codexaa.repository.UserRepository;
import com.codexaa.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;

    @Override
    public User getUserFromJwtToken(String token) throws UserExceptions {
        String email = jwtProvider.getEmailFromToken(token);
        User user = userRepository.findByEmail(email);

        if (user == null) {
            throw new UserExceptions("Invalid Token");
        }
        return user;
    }

    @Override
    public User getCurrentUser() throws UserExceptions {
        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            throw new UserExceptions("No authentication found");
        }

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email);

        if (user == null) {
            throw new UserExceptions("User Not Found");
        }

        return user;
    }

    @Override
    public User getUserByEmail(String email) throws UserExceptions {
        User user = userRepository.findByEmail(email);

        if (user == null) {
            throw new UserExceptions("User Not Found");
        }

        return user;
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User Not Found"));
    }

    @Override
    public List<User> getAllUser() {
        return userRepository.findAll();
    }
}
