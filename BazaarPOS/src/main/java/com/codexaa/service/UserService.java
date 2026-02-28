package com.codexaa.service;


import com.codexaa.exception.UserExceptions;
import com.codexaa.model.User;

import java.util.List;

public interface UserService {
    User getUserFromJwtToken(String token) throws UserExceptions;
    User getCurrentUser() throws UserExceptions;
    User getUserByEmail(String email) throws UserExceptions;
    User getUserById(Long id);
    List<User> getAllUser();

}
