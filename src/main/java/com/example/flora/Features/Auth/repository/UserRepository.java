package com.example.flora.Features.Auth.repository;

import com.example.flora.Features.Auth.model.User;

import java.util.List;

public interface UserRepository {
    void saveUser(User user);
    User findByID(Integer id);
    User findByEmail(String email);
    boolean userExist(String email);
    List<String> searchByUsernameLike(String query);
    User findByUsername(String username);
}
