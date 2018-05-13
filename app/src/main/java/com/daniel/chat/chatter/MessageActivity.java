package com.daniel.chat.chatter;

import java.util.Date;

public class MessageActivity {
    private String message;
    private String user;
    private long time;

    public MessageActivity(String message, String user) {
        this.message = message;
        this.user = user;
        time = new Date().getTime();
    }

    public MessageActivity() {
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
