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
import org.ielena.simplechat.temporal_common.Channel;
import org.ielena.simplechat.temporal_common.ServerResponse;
import org.ielena.simplechat.temporal_common.User;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.InvalidParameterException;
import java.util.List;

public class LoginController {
    public static final String CONNECTION_ERROR = "Connection Error";
    public static final String USER_ALREADY_REGISTERED = "The username is already registered on the server.";
    public static final String NAME_ERROR = "Name Error";
    public static final String NAME_FIELD_EMPTY = "The name field cannot be empty.";
    public static final String CONNECTION_PORT_ERROR = "Could not establish connection on the selected port.";
    public static final String HOST_ERROR = "Host Error";
    public static final String UNKNOWN_HOST = "Unknown Host";
    private static final int MAX_USERNAME_LENGTH = 20;
    //Attributes
    @FXML
    private TextField usernameText, portText, serverText;
    private Stage stage;
    private Client client;

    //Getters and setters
    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    //Methods
    public void initialize() {

        // Validador que solo permite nombres de usuario alfanuméricos con longitud específica
        usernameText.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("^[a-zA-Z0-9_]*$")) {
                usernameText.setText(oldValue);
            }

            if (newValue.length() > MAX_USERNAME_LENGTH) {
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
            //Hay un bug que habría que corregir. Cuando el usuario intenta entrar con un nombre que ya existe no le dejamos, pero el socket se crea y lo estamos guardando
            //Luego le damos la oportunidad de corregir su error, pero también puede decidir cambiar el host o el puerto, y no lo estamos teniendo en cuenta.
            prepareClient();

            //Tuve muchos problemas intentando mandar la lista de usuarios a través del ObjectOutputStream y todavía no he conseguido averiguar por qué se corrompía el Stream
            //La solución que se me ocurrió fue crear un objeto que tuviera el boolean de si ha conectado o no y la lista de usuarios como atributos. No me encanta, pero funciona
            ServerResponse serverResponse = login(client);
            boolean isConnected = serverResponse.isConnected();
            if (isConnected) {
                openChatWindow(serverResponse.getUsers(), serverResponse.getChannels());
            } else {
                showError(CONNECTION_ERROR, USER_ALREADY_REGISTERED);
            }

        } catch (InvalidParameterException e) {
            showError(NAME_ERROR, NAME_FIELD_EMPTY);
        } catch (ConnectException | IllegalArgumentException e) {
            showError(CONNECTION_ERROR, CONNECTION_PORT_ERROR);
        } catch (UnknownHostException e) {
            showError(HOST_ERROR, UNKNOWN_HOST);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void onEnterButtonPressed(KeyEvent event) throws IOException {
        if (event.getCode() == KeyCode.ENTER) {
            onConnectClick();
        }
    }

    private ServerResponse login(Client client) throws IOException, ClassNotFoundException {

        ObjectOutputStream clientOutputStream = client.getClientOutputStream();
        ObjectInputStream clientInputStream = client.getClientInputStream();
        User user = client.getUser();

        clientOutputStream.writeObject(user);
        clientOutputStream.flush();

        ServerResponse serverResponse = (ServerResponse) clientInputStream.readObject();
        boolean isConnectionEstablished = serverResponse.isConnected();
        if (isConnectionEstablished) {
            client.startListener();
        }

        return serverResponse;
    }

    private void prepareClient() throws IOException {
        String userName = usernameText.getText();
        int port = Integer.parseInt(portText.getText());
        String server = serverText.getText();
        if (userName.isEmpty()) {
            throw new InvalidParameterException();
        }
        User user = new User(userName);
        if (client == null) {
            Socket socket = new Socket(server, port);
            client = new Client(socket, user);
        } else {
            client.setUser(user);
        }
    }

    private void openChatWindow(List<User> users, List<Channel> channels) {
        FXMLLoader fxmlLoader = new FXMLLoader(RunClient.class.getResource("views/chat-view.fxml"));
        Scene scene;
        try {
            scene = new Scene(fxmlLoader.load());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        ChatController chatController = fxmlLoader.getController();
        chatController.setClient(client);
        chatController.setUserList(users);
        chatController.setChannelList(channels);
        chatController.getConnectedUser().setText(client.getUser().getUsername());

        stage.setScene(scene);
        stage.show();
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
