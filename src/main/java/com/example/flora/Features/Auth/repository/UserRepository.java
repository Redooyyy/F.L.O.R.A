package com.example.flora.Features.Auth.repository;

import com.example.flora.Features.Auth.model.User;

public interface UserRepository {
    void saveUser(User user);
    User findByID(Integer id);
    User findByEmail(String email);
    boolean userExist(String email);
}
