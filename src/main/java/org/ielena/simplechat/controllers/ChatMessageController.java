package org.ielena.simplechat.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.ielena.simplechat.temporal_common.Message;

public class ChatMessageController {

    @FXML
    private Label usernameLabel;

    @FXML
    private Label messageBodyLabel;

    public void setUsername(String username) {
        usernameLabel.setText(username);
    }

    public void setMessageBody(String messageBody) {
        messageBodyLabel.setText(messageBody);
    }

    public void processMessage(Message message){
        setUsername(message.getUser().getUsername());
        setMessageBody(message.getMessage());
    }
}

