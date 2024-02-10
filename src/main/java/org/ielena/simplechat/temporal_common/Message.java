package org.ielena.simplechat.temporal_common;

import java.io.Serializable;

public class Message implements Serializable {
    private String message;
    private User user;
    private MessageType messageType;
    private Destination destination;

    public Message(MessageType messageType, User user, String message) {
        setMessageType(messageType);
        setUser(user);
        setMessage(message);
    }
    public Message(MessageType messageType, User user, String message, Destination destination){
        this(messageType, user, message);
        setDestination(destination);
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

    public Destination getDestination() {
        return destination;
    }

    public void setDestination(Destination destination) {
        this.destination = destination;
    }
}

