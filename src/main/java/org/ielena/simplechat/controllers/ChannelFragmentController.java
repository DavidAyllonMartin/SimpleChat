package org.ielena.simplechat.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import org.ielena.simplechat.temporal_common.Channel;
import org.ielena.simplechat.temporal_common.Destination;
import org.ielena.simplechat.temporal_common.Message;

import java.util.ArrayList;
import java.util.List;

public class ChannelFragmentController {
    @FXML
    private Label userName;
    @FXML
    private AnchorPane container;
    @FXML
    private HBox channelHBox;
    @FXML
    private Button createChannelButton;
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

    public void addMessage(Message message) {
        messages.add(message);
    }

    public void onItemClicked(MouseEvent mouseEvent) {
        ChatController.getController().changeActiveDestination(destination, messages);
    }

    public void setData(Destination destination) {
        userName.setText(destination.getDestinationName());
    }

    public void onSuscribeClicked(MouseEvent mouseEvent) {
        channelHBox.getChildren().remove(createChannelButton);
        container.addEventHandler(MouseEvent.MOUSE_CLICKED, this::onItemClicked);
        container.setStyle("-fx-background-color: #B3D9FF;");
        container.setOnMouseEntered(e -> {
            container.setStyle("-fx-background-color: #B3D9FF;");
        });

        container.setOnMouseExited(e -> {
            container.setStyle("-fx-background-color: #dce6ff;");
        });
        Channel channel = (Channel) destination;
        ChatController.getController().suscribeChannel(channel);
    }
}
