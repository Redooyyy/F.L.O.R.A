package com.example.flora.Features.Auth.ViewModel;

import com.example.flora.Features.Auth.exception.AuthException;
import com.example.flora.Features.Auth.model.User;
import com.example.flora.Features.Auth.service.AuthService;

import java.util.List;

public class AuthViewModel {

    private final AuthService authService;

    public AuthViewModel(AuthService authService) {
        this.authService = authService;
    }

    public User login(String email, String password) {
        return authService.login(email, password);
    }

    public User signup(String email, String password) {
        return authService.signUp(email, password);
    }

    public List<String> searchUsers(String query) {
        return authService.searchUsers(query);
    }
}