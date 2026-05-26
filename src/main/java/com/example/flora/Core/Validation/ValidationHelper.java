package com.example.flora.Core.Validation;

import java.util.regex.Pattern;



public final class ValidationHelper {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[a-zA-Z0-9._%+\\-]+@[a-zA-Z0-9.\\-]+\\.[a-zA-Z]{2,}$");

    private static final int MIN_PASSWORD_LENGTH = 6;

    private ValidationHelper() {}



    public static boolean isEmpty(String value) {
        return value == null || value.isBlank();
    }

    public static boolean isValidEmail(String email) {
        if (isEmpty(email)) return false;
        return EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    public static boolean isValidPassword(String password) {
        return password != null && password.length() >= MIN_PASSWORD_LENGTH;
    }

    public static boolean passwordsMatch(String a, String b) {
        return a != null && a.equals(b);
    }



    public static String validateLoginFields(String email, String password) {
        if (isEmpty(email))            return Msg.EMAIL_EMPTY;
        if (!isValidEmail(email))      return Msg.EMAIL_INVALID;
        if (isEmpty(password))         return Msg.PASSWORD_EMPTY;
        if (!isValidPassword(password)) return Msg.PASSWORD_TOO_SHORT;
        return null;
    }



    public static String validateSignupFields(String email, String password,
                                              String confirmPassword, String fullname) {
        if (isEmpty(fullname))         return Msg.FULLNAME_EMPTY;
        String loginErr = validateLoginFields(email, password);
        if (loginErr != null)          return loginErr;
        if (isEmpty(confirmPassword))  return Msg.CONFIRM_EMPTY;
        if (!passwordsMatch(password, confirmPassword)) return Msg.PASSWORDS_MISMATCH;
        return null;
    }


    public static String validateRequiredField(String value, String fieldName) {
        if (isEmpty(value)) return fieldName + " cannot be empty.";
        return null;
    }



    public static final class Msg {
        public static final String FULLNAME_EMPTY       = "Full name is required.";
        public static final String EMAIL_EMPTY          = "Email address is required.";
        public static final String EMAIL_INVALID        = "Please enter a valid email address.";
        public static final String PASSWORD_EMPTY       = "Password is required.";
        public static final String PASSWORD_TOO_SHORT   = "Password must be at least 6 characters.";
        public static final String CONFIRM_EMPTY        = "Please confirm your password.";
        public static final String PASSWORDS_MISMATCH   = "Passwords do not match.";
        public static final String WRONG_CREDENTIALS    = "Incorrect email or password.";
        public static final String USER_EXISTS          = "An account with this email already exists.";
        public static final String SERVER_ERROR         = "Something went wrong. Please try again.";

        private Msg() {}
    }
}