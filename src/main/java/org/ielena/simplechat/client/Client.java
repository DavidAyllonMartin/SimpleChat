package org.ielena.simplechat.client;

import javafx.application.Platform;
import org.ielena.simplechat.controllers.ChatController;
import org.ielena.simplechat.temporal_common.Message;
import org.ielena.simplechat.temporal_common.User;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Client {
    //Attributes
    private User user;
    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private ChatController chatController;

    //Constructors
    public Client(Socket socket, User user) throws IOException {
        setSocket(socket);
        setUser(user);
        setOut(socket);
        setIn(socket);

        ClientListener clientListener = new ClientListener(this);
        clientListener.start();
    }

    //Getters and setters
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public ObjectInputStream getIn() {
        return in;
    }

    public void setIn(Socket socket) throws IOException {
        this.in = new ObjectInputStream(socket.getInputStream());
    }

    public ObjectOutputStream getOut() {
        return out;
    }

    public void setOut(Socket socket) throws IOException {
        this.out = new ObjectOutputStream(socket.getOutputStream());
    }

    public ChatController getChatController() {
        return chatController;
    }

    public void setChatController(ChatController chatController) {
        this.chatController = chatController;
    }

    //Methods
    public void sendMessage(Message message) throws IOException {
        this.out.writeObject(message);
    }

    public void processMessage(Message message){
        chatController.processMessage(message);
    }

    public void close() {
        try {
            if (out != null) {
                out.close();
            }
            if (in != null) {
                in.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
