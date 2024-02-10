package org.ielena.simplechat.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.ielena.simplechat.RunClient;
import org.ielena.simplechat.client.Client;
import org.ielena.simplechat.temporal_common.Destination;
import org.ielena.simplechat.temporal_common.Message;
import org.ielena.simplechat.temporal_common.MessageType;
import org.ielena.simplechat.temporal_common.User;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ChatController {
    //Attributes
    private static ChatController controller;
    private final HashMap<User, UserFragmentController> connectedUsers = new HashMap<>();
    private List<Message> generalMessages = new ArrayList<>();
    private Destination activeDestination;
    private Client client;
    @FXML
    private VBox roomsList;
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox messageContainer;
    @FXML
    private TextArea messageBox;
    @FXML
    private Label onlineCountLabel, chatWith, connectedUser;

    //Constructors
    public ChatController() {
        controller = this;
    }

    //Getters and setters
    public static ChatController getController() {
        return controller;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Destination getActiveDestination() {
        return activeDestination;
    }

    public void setActiveDestination(Destination activeDestination) {
        this.activeDestination = activeDestination;
    }

    public void setUserList(List<User> users) {
        users.forEach(user -> {
            try {
                createUserFragment(user);
                increaseOnlineCount();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }


    //Methods
    public void initialize() {
        // Configurar el scroll pane para que siempre muestre el contenido en la parte inferior
        scrollPane.vvalueProperty().bind(messageContainer.heightProperty());
        RunClient.primaryStage.setOnCloseRequest(e -> {
            client.close();
            Platform.exit();
        });
    }

    public void sendButtonAction() throws IOException {
        String msg = messageBox.getText().trim();
        if (!msg.isEmpty()) {
            Message message = new Message(MessageType.MESSAGE, client.getUser(), msg, activeDestination);
            client.sendMessage(message);
            if (activeDestination == null){
                generalMessages.add(message);
            }else {
                User user = (User) activeDestination;
                UserFragmentController controller = connectedUsers.get(activeDestination);
                controller.addMessage(message);
            }
            writeMessageInChatPanel(message);
            messageBox.clear();
        }
    }

    public void sendButtonActionWithKeyboard(KeyEvent event) throws IOException {
        if (event.getCode() == KeyCode.ENTER) {
            sendButtonAction();
        }
    }

    public void receiveMessage(Message message) {
        User source = message.getUser();
        User destination = (User) message.getDestination();
        if (destination == null){
            generalMessages.add(message);
            if (activeDestination == null){
                writeMessageInChatPanel(message);
            }
        }else{
            UserFragmentController userFragmentController = connectedUsers.getOrDefault(source, null);
            userFragmentController.addMessage(message);
            if (source.equals(activeDestination)){
                writeMessageInChatPanel(message);
            }
        }

    }

    public void writeMessageInChatPanel(Message message) {
        FXMLLoader item = new FXMLLoader(RunClient.class.getResource("views/chat_message.fxml"));
        try {
            HBox hBox = item.load();
            ChatMessageController controller = item.getController();
            controller.processMessage(message);

            Platform.runLater(() -> {
                messageContainer.getChildren().add(hBox);
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void processConnectedUser(Message message) throws IOException {
        User user = message.getUser();
        createUserFragment(user);
        increaseOnlineCount();
    }

    public void processDisconnectedUser(Message message) {
        User user = message.getUser();
        UserFragmentController controller = connectedUsers.get(user);
        Platform.runLater(() -> {
                    roomsList.getChildren().remove(controller.getContainer());
                }
        );
        decreaseOnlineCount();
    }

    public void changeActiveDestination(Destination destination, List<Message> messages){
        if (!destination.equals(activeDestination)){
            setActiveDestination(destination);
            User user = (User) destination;
            this.chatWith.setText("Chat with " + user.getUsername());
            Platform.runLater(
                    () -> {
                        messageContainer.getChildren().clear();
                    }
            );

            messages.forEach(this::writeMessageInChatPanel);
        }
    }

    private void createUserFragment(User user) throws IOException {
        FXMLLoader item = new FXMLLoader(RunClient.class.getResource("views/user-fragment.fxml"));
        AnchorPane anchorPane = item.load();
        UserFragmentController controller = item.getController();
        controller.setUser(user);
        controller.setData(user);
        Platform.runLater(() -> {
                    roomsList.getChildren().add(anchorPane);
                }
        );
        connectedUsers.put(user, controller);
    }

    private void increaseOnlineCount() {
        Platform.runLater(() -> {
            Integer onlineCount = Integer.valueOf(onlineCountLabel.getText());
            onlineCount++;
            onlineCountLabel.setText(String.valueOf(onlineCount));
        });
    }

    private void decreaseOnlineCount() {
        Platform.runLater(() -> {
            Integer onlineCount = Integer.valueOf(onlineCountLabel.getText());
            onlineCount--;
            onlineCountLabel.setText(String.valueOf(onlineCount));
        });
    }

    public void onGeneralChatClicked(MouseEvent mouseEvent) {
        setActiveDestination(null);
        chatWith.setText("General Chat");
        Platform.runLater(
                () -> {
                    messageContainer.getChildren().clear();
                }
        );

        generalMessages.forEach(this::writeMessageInChatPanel);
    }


    public Label getChatWith() {
        return chatWith;
    }

    public void setChatWith(Label chatWith) {
        this.chatWith = chatWith;
    }

    public Label getConnectedUser() {
        return connectedUser;
    }

    public void setConnectedUser(Label connectedUser) {
        this.connectedUser = connectedUser;
    }
}
