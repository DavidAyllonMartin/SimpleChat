package org.ielena.simplechat.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.ielena.simplechat.RunClient;
import org.ielena.simplechat.client.Client;
import org.ielena.simplechat.temporal_common.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ChatController {
    //Attributes
    private static ChatController controller;
    private final HashMap<User, UserFragmentController> connectedUsers = new HashMap<>();
    private final HashMap<Channel, ChannelFragmentController> createdChannels = new HashMap<>();
    private final List<Message> generalMessages = new ArrayList<>();
    private Destination activeDestination;
    private Client client;
    @FXML
    private VBox roomsList, channelList, messageContainer;
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private TextArea messageBox;
    @FXML
    private Label onlineCountLabel, chatWith, connectedUser;
    @FXML
    private TextField channelTextField;

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

    public void setChannelList(List<Channel> channels) {
        channels.forEach(channel -> {
            try {
                createChannel(channel);
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
            System.out.println("Mensaje enviado");
            if (activeDestination == null){
                generalMessages.add(message);
            }else if (activeDestination instanceof User user){
                UserFragmentController userController = connectedUsers.get(user);
                userController.addMessage(message);
            }else if (activeDestination instanceof Channel channel){
                ChannelFragmentController channelController = createdChannels.get(channel);
                channelController.addMessage(message);
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
        Destination destination = message.getDestination();
        if (destination == null){
            generalMessages.add(message);
            if (activeDestination == null){
                writeMessageInChatPanel(message);
            }

        }else if (destination instanceof User){
            UserFragmentController userController = connectedUsers.get(source);
            userController.addMessage(message);
            if (source.equals(activeDestination)){
                writeMessageInChatPanel(message);
            }

        }else if (destination instanceof Channel channel){

            ChannelFragmentController channelController = createdChannels.get(channel);
            channelController.addMessage(message);
            if (channel.equals(activeDestination)){
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
            this.chatWith.setText("Chat with " + destination.getDestinationName());
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
        controller.setDestination(user);
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

    public void onCreateChannelClicked(MouseEvent mouseEvent) {
        String channelName = channelTextField.getText().trim();
        channelTextField.clear();
        if (!channelName.isEmpty()){
            Channel channel = new Channel(channelName);
            Message message = new Message(MessageType.CREATE_CHANNEL, client.getUser(), null, channel);
            try {
                client.sendMessage(message);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void createChannel(Channel channel) throws IOException {
        FXMLLoader item = new FXMLLoader(RunClient.class.getResource("views/channel-fragment.fxml"));
        AnchorPane anchorPane = item.load();
        ChannelFragmentController controller = item.getController();
        controller.setDestination(channel);
        controller.setData(channel);
        Platform.runLater(() -> {
                    channelList.getChildren().add(anchorPane);
                }
        );
        createdChannels.put(channel, controller);
    }

    public void suscribeChannel(Channel channel) {
        User user = client.getUser();
        Message message = new Message(MessageType.SUSCRIBE_CHANNEL, user, null, channel);
        try {
            client.sendMessage(message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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

    public TextField getChannelTextField() {
        return channelTextField;
    }

    public void setChannelTextField(TextField channelTextField) {
        this.channelTextField = channelTextField;
    }


}
