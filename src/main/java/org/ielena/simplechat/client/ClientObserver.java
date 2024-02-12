package org.ielena.simplechat.client;

import org.ielena.simplechat.temporal_common.Message;

public interface ClientObserver {
    void receiveServerInput(Message message);
}
