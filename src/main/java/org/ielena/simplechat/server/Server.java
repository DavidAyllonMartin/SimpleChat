package org.ielena.simplechat.server;

import org.ielena.simplechat.temporal_common.Channel;
import org.ielena.simplechat.temporal_common.User;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class Server {
    public static final int PORT = 6000;
    private static final ConcurrentHashMap<User, ObjectOutputStream> users = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<Channel, List<User>> channels = new ConcurrentHashMap<>();

    public static void main(String[] args) {

        try (ServerSocket listener = new ServerSocket(PORT)){
            System.out.println("Servidor conectado");

            while (!listener.isClosed()){
                new ServerHandler(listener.accept()).start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    protected static ConcurrentHashMap<User, ObjectOutputStream> getUsers() {
        return users;
    }
    protected static ConcurrentHashMap<Channel, List<User>> getChannels(){
        return channels;
    }
}
