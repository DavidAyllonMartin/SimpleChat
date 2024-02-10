package org.ielena.simplechat.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import org.ielena.simplechat.temporal_common.Destination;
import org.ielena.simplechat.temporal_common.Message;
import org.ielena.simplechat.temporal_common.User;

import java.util.ArrayList;
import java.util.List;

public class UserFragmentController {
    @FXML
    private Label userName;
    @FXML
    private AnchorPane container;
    private Destination user;
    private List<Message> messages = new ArrayList<>();

    public Label getUserName() {
        return userName;
    }

    public void setUserName(Label userName) {
        this.userName = userName;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    public Destination getUser() {
        return user;
    }

    public void setUser(Destination user) {
        this.user = user;
    }

    public AnchorPane getContainer() {
        return container;
    }

    public void setContainer(AnchorPane container) {
        this.container = container;
    }

    public void addMessage(Message message){
        messages.add(message);
    }

    public void onItemClicked(MouseEvent mouseEvent) {
        ChatController.getController().changeActiveDestination(user, messages);
    }

    public void setData(User user) {
        userName.setText(user.getUsername());
    }
}
