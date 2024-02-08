package org.ielena.simplechat.temporal_common;

import java.io.Serializable;

public class Message implements Serializable {
    private User user;
    private String message;

    public Message(User user, String message) {
        this.user = user;
        this.message = message;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

