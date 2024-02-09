package org.ielena.simplechat.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import org.ielena.simplechat.client.Client;
import org.ielena.simplechat.temporal_common.Message;

import java.io.IOException;

public class ChatController {
    private Client client;
    private static ChatController controller;

    @FXML
    private VBox messageContainer;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private TextArea messageBox;

    public ChatController() {
        controller = this;
    }

    public static ChatController getController(){
        return controller;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public void initialize() {
        // Configurar el scroll pane para que siempre muestre el contenido en la parte inferior
        scrollPane.vvalueProperty().bind(messageContainer.heightProperty());
    }


    public void sendMethod(KeyEvent event) throws IOException {
        if (event.getCode() == KeyCode.ENTER) {
            sendButtonAction();
        }
    }


    public void sendButtonAction() throws IOException {
        String msg = messageBox.getText();
        if (!messageBox.getText().trim().isEmpty()) {
            Message message = new Message(client.getUser(), msg);
            client.sendMessage(message);
            messageBox.clear();
        }
    }

    public void processMessage(Message message){
        Platform.runLater(() -> {
            Label labelMensaje = new Label(message.getUser().getUsername() + ": " + message.getMessage());
            messageContainer.getChildren().add(labelMensaje);
        });
    }
}
