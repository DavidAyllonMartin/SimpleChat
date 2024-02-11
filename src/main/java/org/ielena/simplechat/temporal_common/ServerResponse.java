package org.ielena.simplechat.temporal_common;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public class ServerResponse implements Serializable {
    private boolean connected;
    private List<User> users;
    private List<Channel> channels;

    public ServerResponse(boolean connected, List<User> users, List<Channel> channels) {
        setConnected(connected);
        setUsers(users);
        setChannels(channels);
    }

    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public List<Channel> getChannels() {
        return channels;
    }

    public void setChannels(List<Channel> channels) {
        this.channels = channels;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ServerResponse serverResponse = (ServerResponse) o;

        if (connected != serverResponse.connected) return false;
        return Objects.equals(users, serverResponse.users);
    }

    @Override
    public int hashCode() {
        int result = (connected ? 1 : 0);
        result = 31 * result + (users != null ? users.hashCode() : 0);
        return result;
    }
}
