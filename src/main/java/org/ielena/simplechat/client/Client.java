package org.ielena.simplechat.client;

import org.ielena.simplechat.controllers.ChatController;
import org.ielena.simplechat.temporal_common.Message;
import org.ielena.simplechat.temporal_common.MessageType;
import org.ielena.simplechat.temporal_common.User;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Client {
    //Attributes
    private User user;
    private Socket socket;
    private ObjectInputStream clientInputStream;
    private ObjectOutputStream clientOutputStream;
    private ClientListener listener;

    //Constructors
    public Client(Socket socket, User user) throws IOException {
        setSocket(socket);
        setUser(user);
        setClientOutputStream(socket);
        setClientInputStream(socket);
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

    public ObjectInputStream getClientInputStream() {
        return clientInputStream;
    }

    public void setClientInputStream(Socket socket) throws IOException {
        this.clientInputStream = new ObjectInputStream(socket.getInputStream());
    }

    public ObjectOutputStream getClientOutputStream() {
        return clientOutputStream;
    }

    public void setClientOutputStream(Socket socket) throws IOException {
        this.clientOutputStream = new ObjectOutputStream(socket.getOutputStream());
    }

    //Methods
    public void processServerInput(Message message) {
        switch (message.getMessageType()) {
            case MESSAGE -> processMessageFromServer(message);
            case DISCONNECT -> userDisconnected(message);
            case CONNECT -> userConnected(message);
        }
    }

    private void processMessageFromServer(Message message) {
        ChatController.getController().receiveMessage(message);
    }

    private void userConnected(Message message) {
        if (!message.getUser().equals(user)) {
            try {
                ChatController.getController().processConnectedUser(message);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void userDisconnected(Message message) {
        if (!message.getUser().equals(user)) {
            ChatController.getController().processDisconnectedUser(message);
        }
    }

    public void sendMessage(Message message) throws IOException {
        this.clientOutputStream.writeObject(message);
        this.clientOutputStream.flush();
    }

    public void startListener() {
        ClientListener clientListener = new ClientListener(this);
        listener = clientListener;
        clientListener.start();
    }

    public void stopListener() throws IOException {
        //Aquí estoy parando el hilo a través del servidor, pero no me entusiasma esta idea tampoco
        //Estamos guardando la referencia del hilo en un atributo por si acaso
        Message message = new Message(MessageType.DISCONNECT, user, null);
        clientOutputStream.writeObject(message);
        clientOutputStream.flush();
    }

    public void close() {

        try {

            stopListener();

            if (clientOutputStream != null) {
                clientOutputStream.close();
            }
            if (clientInputStream != null) {
                clientInputStream.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
