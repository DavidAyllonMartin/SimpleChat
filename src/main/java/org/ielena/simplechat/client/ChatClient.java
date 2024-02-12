package org.ielena.simplechat.client;

import org.ielena.simplechat.temporal_common.Message;
import org.ielena.simplechat.temporal_common.User;

public interface ChatClient {
    User getUser();
    void sendMessage(Message message);
    void close();
}
