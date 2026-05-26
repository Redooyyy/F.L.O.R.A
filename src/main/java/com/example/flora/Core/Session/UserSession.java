package com.example.flora.Core.session;

import com.example.flora.Features.Auth.model.User;

import java.sql.PreparedStatement;

public class UserSession {
    private static User currentUser;

    public static void setUser(User user) {
        currentUser = user;
    }

    public static User getUser() {
        return currentUser;
    }

    public static void clear() {
        currentUser = null;
    }

    public static boolean isLoggedIn() {
        return currentUser != null;
    }


}