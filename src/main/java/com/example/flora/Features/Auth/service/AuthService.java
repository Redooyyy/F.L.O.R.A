package com.example.flora.Features.Auth.service;

import com.example.flora.Features.Auth.exception.AuthException;
import com.example.flora.Features.Auth.model.User;
import com.example.flora.Features.Auth.repository.UserRepository;

import java.util.List;

public class AuthService {
    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User signUp(String email, String password) {
        if (userRepository.userExist(email)) {
            throw new AuthException(
                    AuthException.Reason.USER_ALREADY_EXISTS,
                    "An account with this email already exists."
            );
        }
        try {
            User user = new User(email, password);
            userRepository.saveUser(user);
            return user;
        } catch (RuntimeException e) {
            throw new AuthException(AuthException.Reason.SERVER_ERROR,
                    "Registration failed. Please try again.");
        }
    }

    public User login(String email, String password) {
        User user;
        try {
            user = userRepository.findByEmail(email);
        } catch (RuntimeException e) {
            throw new AuthException(AuthException.Reason.SERVER_ERROR,
                    "Login failed. Please try again.");
        }
        if (user == null || !user.getPassword().equals(password)) {
            throw new AuthException(AuthException.Reason.INVALID_CREDENTIALS,
                    "Incorrect email or password.");
        }
        return user;
    }

    public List<String> searchUsers(String query) {
        return userRepository.searchByUsernameLike(query);
    }
}