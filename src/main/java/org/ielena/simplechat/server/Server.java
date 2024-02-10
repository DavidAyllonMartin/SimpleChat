package org.ielena.simplechat.server;

import org.ielena.simplechat.temporal_common.User;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.util.HashMap;

public class Server {
    public static final int PORT = 6000;
    private static HashMap<User, ObjectOutputStream> users = new HashMap<>();

    public static void main(String[] args) {

        try (ServerSocket listener = new ServerSocket(PORT)){
            System.out.println("Servidor conectado");

            while (!listener.isClosed()){
                new ServerHandler(listener.accept()).start();
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    protected static synchronized HashMap<User, ObjectOutputStream> getUsers() {
        return users;
    }
}
