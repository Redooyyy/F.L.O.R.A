package com.example.flora.Features.Auth.repository;

import com.example.flora.Features.Auth.model.User;

import java.sql.Connection;

public class UserRepositoryImpl implements UserRepository{

    private Connection connection;
    //For dependency injection
    public UserRepositoryImpl(Connection connection){
        this.connection = connection;
    }

    @Override
    public void saveUser(User user) {
        //TODO: save to db via mysql query
    }

    @Override
    public User findByID(Integer id) {
        //TODO: find id via mysql query
        return null;
    }

    @Override
    public User findByEmail(String email) {
        //TODO: find email via mysql query
        return null;
    }

    @Override
    public boolean userExist(String email) {
        //TODO: find user exist or not [ you can find with query or if you have enough IQ then you'll use existing function daahhhhh -_- ]
        return false;
    }
}
