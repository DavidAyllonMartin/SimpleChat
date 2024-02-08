package com.example.demo.controllers;

import com.example.demo.Message;
import com.example.demo.User;
import com.example.demo.client.ClientListener;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class ChatController {
    private User user;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    private ObservableList<String> observableList;

    @FXML
    private ListView<String> chatPane;

    @FXML
    private TextArea messageBox;

    public ObjectOutputStream getOut() {
        return out;
    }

    public void setOut(ObjectOutputStream out) {
        this.out = out;
    }

    public ObjectInputStream getIn() {
        return in;
    }

    public void setIn(ObjectInputStream in) {
        this.in = in;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void sendMethod(KeyEvent event) throws IOException {
        if (event.getCode() == KeyCode.ENTER) {
            sendButtonAction();
        }
    }

    public ObservableList<String> getObservableList() {
        return observableList;
    }

    public void setObservableList(ObservableList<String> observableList) {
        this.observableList = observableList;
    }

    public ListView<String> getChatPane() {
        return chatPane;
    }

    public void setChatPane(ListView<String> chatPane) {
        this.chatPane = chatPane;
    }

    public void sendButtonAction() throws IOException {
        String msg = messageBox.getText();
        Message message = new Message(this.user, msg);
        if (!messageBox.getText().isEmpty()) {
            ClientListener.send(message, this.out);
            messageBox.clear();
        }
    }

    public void processMessage(Message message){
        Platform.runLater(() -> {
            observableList.add(message.getMessage());
        });
    }
}
