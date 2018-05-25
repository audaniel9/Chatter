package com.daniel.chat.chatter;

public class Users {
    private String username;
    private String email;

    public Users(String username) {
        this.username = username;
    }

    public Users(String username, String email) {
        this.username = username;
        this.email = email;
    }

    public Users() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}