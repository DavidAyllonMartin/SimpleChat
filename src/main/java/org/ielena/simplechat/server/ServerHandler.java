package org.ielena.simplechat.server;

import org.ielena.simplechat.temporal_common.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;
import java.util.Map;

public class ServerHandler extends Thread {

    //Atributes
    private Socket socket;
    private ObjectInputStream serverInputStream;
    private ObjectOutputStream serverOutputStream;

    //Constructor
    public ServerHandler(Socket socket) {
        setSocket(socket);
        System.out.println("Cliente conectado");
        try {
            setServerInputStream(socket);
            setServerOutputStream(socket);
            System.out.println("Streams creados");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    //Getters and Setters
    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public ObjectInputStream getServerInputStream() {
        return serverInputStream;
    }

    public void setServerInputStream(Socket socket) throws IOException {
        this.serverInputStream = new ObjectInputStream(socket.getInputStream());
    }

    public ObjectOutputStream getServerOutputStream() {
        return serverOutputStream;
    }

    public void setServerOutputStream(Socket socket) throws IOException {
        this.serverOutputStream = new ObjectOutputStream(socket.getOutputStream());
    }

    //Methods
    @Override
    public void run() {
        boolean isLogged;
        try {
            do {
                isLogged = login();
            } while (!isLogged);

        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        try {
            while (socket.isConnected()) {
                Message message = (Message) serverInputStream.readObject();
                processClientOutput(message);
            }
        } catch (IOException | ClassNotFoundException e) {
            //e.printStackTrace();
        } finally {
            close();
        }

    }

    private void processClientOutput(Message message) throws IOException {
        switch (message.getMessageType()) {
            case MESSAGE -> processMessage(message);
            case DISCONNECT -> processDisconnect(message);
            case CONNECT -> processConnect(message);
        }
    }

    private void processConnect(Message message) throws IOException {
        for (ObjectOutputStream out : Server.getUsers().values()) {
            out.writeObject(message);
            out.flush();
        }
    }

    private void processDisconnect(Message message) throws IOException {
        User user = message.getUser();
        Server.getUsers().remove(user);
        processMessage(message);
        close();
    }

    private void processMessage(Message message) throws IOException {
        User destination = (User) message.getDestination();
        ObjectOutputStream outputStream = Server.getUsers().get(destination);

        if (outputStream == null){
            User source = message.getUser();
            Server.getUsers().forEach((user, out) -> {
                if (!user.equals(source)) {
                    try {
                        out.writeObject(message);
                        out.flush();
                    } catch (Exception e) {
                        // Manejar la excepción
                    }
                }
            });
        }else {
            outputStream.writeObject(message);
            outputStream.flush();
        }

    }

    private boolean login() throws IOException, ClassNotFoundException {
        User user = (User) serverInputStream.readObject();
        if (Server.getUsers().containsKey(user)) {

            ServerResponse serverResponse = new ServerResponse(false, null);
            serverOutputStream.writeObject(serverResponse);
            serverOutputStream.flush();

            return false;

        } else {

            //Le mando a todos los que están conectados la notificación de conexión
            Message message = new Message(MessageType.CONNECT, user, null);
            processClientOutput(message);
            this.serverOutputStream.flush();

            //Le notifico que se conectó y le mando la lista de usuarios
            List<User> users = Server.getUsers().keySet().stream().toList();
            ServerResponse serverResponse = new ServerResponse(true, users);
            serverOutputStream.writeObject(serverResponse);
            serverOutputStream.flush();

            //Añado al usuario a los usuarios conectados
            Server.getUsers().put(user, serverOutputStream);
            System.out.println(user.getUsername() + " añadido");

            return true;

        }
    }

    public void close() {
        try {
            if (serverOutputStream != null) {
                serverOutputStream.close();
            }
            if (serverInputStream != null) {
                serverInputStream.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
