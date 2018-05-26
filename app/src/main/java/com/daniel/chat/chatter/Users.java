package com.daniel.chat.chatter;

public class Users {
    private String fullName;
    private String user;
    private String email;

    public Users() {
    }

    public Users(String fullName) {
        this.fullName = fullName;
    }

    public Users(String user, String email) {
        this.user = user;
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}