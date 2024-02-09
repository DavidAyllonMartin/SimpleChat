package org.ielena.simplechat.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import org.ielena.simplechat.RunClient;
import org.ielena.simplechat.client.Client;
import org.ielena.simplechat.temporal_common.User;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.InvalidParameterException;

public class LoginController {
    private static final int MAX_USERNAME_LENGTH = 20;
    //Attributes
    @FXML
    private TextField usernameText, portText, serverText;
    private Stage primaryStage;
    private Client client;

    //Getters and setters
    public Stage getPrimaryStage() {
        return primaryStage;
    }
    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    //Methods

    public void initialize() {

        // Validador que solo permite nombres de usuario alfanuméricos con longitud específica
        usernameText.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("^[a-zA-Z0-9_]*$")) {
                usernameText.setText(oldValue);
            }

            if(newValue.length() > MAX_USERNAME_LENGTH) {
                usernameText.setText(oldValue);
            }
        });

        // Validador que solo permite números enteros
        portText.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                portText.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
    }

    @FXML
    public void onConnectClick() throws IOException {
        try {

            prepareClient();

            boolean isConnected = login(client);

            if (isConnected) {
                openChatWindow();
            } else {
                showError("Error de conexión", "El nombre de usuario ya está registrado en el servidor.");
            }

        }catch (InvalidParameterException e){
            showError("Error en el nombre", "El campo nombre no puede estar vacío");
        }catch (ConnectException | IllegalArgumentException e) {
            showError("Error de conexión", "No se pudo establecer la conexión en el puerto seleccionado");
        }catch (UnknownHostException e){
            showError("Error en el host", "No se reconoce el host");
        }
    }

    private void openChatWindow() {
        FXMLLoader fxmlLoader = new FXMLLoader(RunClient.class.getResource("views/chat-view.fxml"));
        Scene scene;
        try {
            scene = new Scene(fxmlLoader.load());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        ChatController chatController = fxmlLoader.getController();
        chatController.setClient(client);

        primaryStage.setScene(scene);

        primaryStage.show();
    }

    private void prepareClient() throws IOException {
        String userName = usernameText.getText();
        int port = Integer.parseInt(portText.getText());
        String server = serverText.getText();
        if (userName.isEmpty()){
            throw new InvalidParameterException();
        }
        User user = new User(userName);
        if (client == null){
            Socket socket = new Socket(server, port);
            client = new Client(socket, user);
        }else {
            client.setUser(user);
        }
    }

    private boolean login(Client client) throws IOException {

        ObjectOutputStream clientOutputStream = client.getOut();
        ObjectInputStream clientInputStream = client.getIn();
        User user = client.getUser();

        clientOutputStream.writeObject(user);
        clientOutputStream.flush();

        boolean isConnectionEstablished = clientInputStream.readBoolean();

        if (isConnectionEstablished){
            client.startListener();
        }

        return isConnectionEstablished;
    }


    public void sendMethod(KeyEvent event) throws IOException {
        if (event.getCode() == KeyCode.ENTER) {
            onConnectClick();
        }
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
