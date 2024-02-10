package org.ielena.simplechat.client;

import org.ielena.simplechat.temporal_common.Message;
import org.ielena.simplechat.temporal_common.MessageType;

import java.io.IOException;

public class ClientListener extends Thread {
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

    public boolean isClosed() {
        return isClosed;
    }

    public void setClosed(boolean closed) {
        isClosed = closed;
    }

    //Methods
    @Override
    public void run() {
        while (!isClosed) {
            try {
                Message message = (Message) client.getClientInputStream().readObject();
                checkDisconnect(message);
                client.processServerInput(message);
            } catch (IOException | ClassNotFoundException e) {
                isClosed = true;
                //e.printStackTrace();
            }
        }
    }

    private void checkDisconnect(Message message) {
        if (message.getMessageType() == MessageType.DISCONNECT && message.getUser().equals(client.getUser())) {
            isClosed = true;
        }
    }
}
