package org.ielena.simplechat.client;

import org.ielena.simplechat.temporal_common.Message;

import java.io.IOException;

public class ClientListener extends Thread{
    //Attributes
    private Client client;

    //Constructors
    public ClientListener(Client client) {
        this.client = client;
    }

    //Getters and setters

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    //Methods

    @Override
    public void run() {
        while (client.getSocket().isConnected()){
            try {
                Message message = (Message) client.getIn().readObject();
                client.processMessage(message);
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
