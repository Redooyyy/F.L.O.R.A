package com.example.flora.Features.Auth.service;

import com.example.flora.Features.Auth.model.User;
import com.example.flora.Features.Auth.repository.UserRepository;

public class AuthService {
    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    public User signUp(String email, String password){
        if(userRepository.userExist(email)) System.out.println("User with this email already exists"); //TODO: Must replace with custom exception
        else {
            User user = new User(email, password);
            userRepository.saveUser(user);
            return user;
        }
        return null;
    }

    public User login(String email, String password){
        User user = userRepository.findByEmail(email);
        if(user != null && user.getPassword().equals(password)) return user;
        else return null; //TODO: Must throw an exception / -_- string(for wrong password test)
    }
}
