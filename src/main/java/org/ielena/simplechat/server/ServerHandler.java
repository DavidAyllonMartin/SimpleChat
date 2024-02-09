package org.ielena.simplechat.server;

import org.ielena.simplechat.temporal_common.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ServerHandler extends Thread{

    //Atributes
    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;

    //Constructor
    public ServerHandler(Socket socket) {
        setSocket(socket);
        System.out.println("Cliente conectado");
        try {
            setIn(socket);
            setOut(socket);
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

    public ObjectInputStream getIn() {
        return in;
    }

    public void setIn(Socket socket) throws IOException {
        this.in = new ObjectInputStream(this.socket.getInputStream());
    }

    public ObjectOutputStream getOut() {
        return out;
    }

    public void setOut(Socket socket) throws IOException {
        this.out = new ObjectOutputStream(this.socket.getOutputStream());
    }

    //Methods
    @Override
    public void run() {
        boolean isLogged;
        try {
            do {
                isLogged = login();
            }while (!isLogged);

        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }


        while (socket.isConnected()){
            try {
                Message message = (Message) in.readObject();
                processClientOutput(message);
            } catch (IOException | ClassNotFoundException e) {
                close();
                //e.printStackTrace();
                break;
            }
        }
    }

    private void processClientOutput(Message message) throws IOException {
        switch (message.getMessageType()){
            case MessageType.MESSAGE:
                processMessage(message);
                break;
            case MessageType.DISCONNECT:
                processDisconnect(message);
                break;
            }
        }

    private void processDisconnect(Message message) throws IOException {
        User user = message.getUser();
        Server.users.remove(user);
        out.writeObject(message);
        out.flush();
        close();
    }

    private void processMessage(Message message) throws IOException {
        for (ObjectOutputStream out : Server.users.values()){
            out.writeObject(message);
            out.flush();
        }
    }

    private boolean login() throws IOException, ClassNotFoundException {
        User user = (User) in.readObject();
        if (Server.users.containsKey(user)){
            this.out.writeBoolean(false);
            this.out.flush();
            return false;
        }else {
            Server.users.put(user, out);
            System.out.println(user.getUsername() + " añadido");
            this.out.writeBoolean(true);
            this.out.flush();
            return true;
        }
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
