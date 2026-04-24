package com.example.flora.Features.Auth.model;

public class User {
    private final String email;
    private String username;
    private Integer id;
    private String password;

    public User(String email, String password){
        this.email = email;
        this.password = password;
        generateUniqueUsername(email);
    }

    public User(String email, String password, Integer id){
        this.email = email;
        this.password = password;
        this.id = id;
        generateUniqueUsername(email);
    }

    //getters
    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public Integer getId() {
        return id;
    }

    //setters
    //for setting id from sql after saving(allow only at once)
    public void setId(Integer id) {
        if(this.id ==null) this.id = id;
        else System.out.println("Already ID assigned");

    }
    //for change password
    public void setPassword(String password) {
        this.password = password;
    }

    //TODO: must re-write logic after DB implementation
    private void generateUniqueUsername(String email){
        this.username = email.substring(0,email.indexOf('@'));
    }
}
