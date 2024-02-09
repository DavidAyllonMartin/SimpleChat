package org.ielena.simplechat.client;

import org.ielena.simplechat.temporal_common.Message;
import org.ielena.simplechat.temporal_common.MessageType;

import java.io.IOException;

public class ClientListener extends Thread{
    //Attributes
    private Client client;
    private boolean isClosed;

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
        while (!isClosed){
            try {
                Message message = (Message) client.getIn().readObject();
                checkDisconnect(message);
                client.processServerInput(message);
            } catch (IOException | ClassNotFoundException e) {
                isClosed = true;
                //e.printStackTrace();
            }
        }
    }

    private void checkDisconnect(Message message) {
        if (message.getMessageType() == MessageType.DISCONNECT && message.getUser().equals(client.getUser())){
            isClosed = true;
        }
    }
}
