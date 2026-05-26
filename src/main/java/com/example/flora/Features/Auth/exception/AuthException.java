package com.example.flora.Features.Auth.exception;


public class AuthException extends RuntimeException {

    public enum Reason {
        USER_ALREADY_EXISTS,
        INVALID_CREDENTIALS,
        SERVER_ERROR
    }

    private final Reason reason;

    public AuthException(Reason reason, String message) {
        super(message);
        this.reason = reason;
    }

    public Reason getReason() {
        return reason;
    }
}