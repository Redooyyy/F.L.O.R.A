package com.example.flora.Features.Auth.ViewModel;

import com.example.flora.Features.Auth.model.User;
import com.example.flora.Features.Auth.service.AuthService;

public class AuthViewModel {
    public AuthService authService; //TODO: Must inject dependency

    public User login(String email, String password){
        return authService.login(email,password);
    }

    public User signup(String email, String Password){
        return authService.signUp(email, Password);
    }
}
