package org.ielena.simplechat.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import org.ielena.simplechat.RunClient;
import org.ielena.simplechat.client.Client;
import org.ielena.simplechat.temporal_common.User;

import java.io.IOException;
import java.net.Socket;

public class LoginController {
    //Attributes
    @FXML
    private TextField usernameText, portText, serverText;

    private Stage primaryStage;

    //Getters and setters
    public Stage getPrimaryStage() {
        return primaryStage;
    }
    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    //Methods

    @FXML
    public void onConnectClick() throws IOException {

        Client client = login();


        FXMLLoader fxmlLoader = new FXMLLoader(RunClient.class.getResource("views/chat-view.fxml"));
        Scene scene;
        try {
            scene = new Scene(fxmlLoader.load());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        ChatController chatController = fxmlLoader.getController();
        chatController.setClient(client);
        client.setChatController(chatController);

        primaryStage.setScene(scene);

        primaryStage.show();
    }

    private Client login() throws IOException {
        String userName = usernameText.getText();
        int port = Integer.parseInt(portText.getText());
        String server = serverText.getText();

        User user = new User(userName);

        Client client = new Client(new Socket(server, port), user);
        client.getOut().writeObject(user);

        return client;
    }

    public void sendMethod(KeyEvent event) throws IOException {
        if (event.getCode() == KeyCode.ENTER) {
            onConnectClick();
        }
    }
}
