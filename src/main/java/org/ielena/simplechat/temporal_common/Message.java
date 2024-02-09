package org.ielena.simplechat.temporal_common;

import java.io.Serializable;

public class Message implements Serializable {
    private String message;
    private User user;
    private MessageType messageType;

    public Message(MessageType messageType, User user, String message) {
        this.message = message;
        this.user = user;
        this.messageType = messageType;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public MessageType getMessageType() {
        return messageType;
    }

    public void setMessageType(MessageType messageType) {
        this.messageType = messageType;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}

