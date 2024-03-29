package org.ielena.simplechat.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import org.ielena.simplechat.temporal_common.Destination;
import org.ielena.simplechat.temporal_common.Message;

import java.util.ArrayList;
import java.util.List;

public class UserFragmentController {
    @FXML
    private Label userName;
    @FXML
    private AnchorPane container;
    private Destination destination;
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

    public Destination getDestination() {
        return destination;
    }

    public void setDestination(Destination destination) {
        this.destination = destination;
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
        ChatController.getController().changeActiveDestination(destination, messages);
    }

    public void setData(Destination destination) {
        userName.setText(destination.getDestinationName());
    }
}
