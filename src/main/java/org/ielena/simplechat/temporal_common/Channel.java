package org.ielena.simplechat.temporal_common;

import java.io.Serializable;
import java.util.Objects;

public class Channel implements Serializable, Destination{
    private String channelName;

    public Channel(String channelName) {
        this.channelName = channelName;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Channel channel = (Channel) o;

        return Objects.equals(channelName, channel.channelName);
    }

    @Override
    public int hashCode() {
        return channelName != null ? channelName.hashCode() : 0;
    }

    @Override
    public String getDestinationName() {
        return getChannelName();
    }
}
