package com.daniel.chat.chatter;

import java.util.Date;

public class Messages {
    private String user;
    private String message;
    private long time;

    public Messages(String message, String user) {
        this.user = user;
        this.message = message;
        time = new Date().getTime();
    }

    public Messages() {
    }

    public String getUser() { return user; }

    public void setUser(String user) { this.user = user; }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
