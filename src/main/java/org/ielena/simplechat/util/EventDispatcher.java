package org.ielena.simplechat.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventDispatcher {
    private final Map<String, List<EventListener>> eventListeners = new HashMap<>();

    public void registerListener(String eventType, EventListener listener) {
        List<EventListener> listeners = eventListeners.getOrDefault(eventType, new ArrayList<>());
        listeners.add(listener);
        eventListeners.put(eventType, listeners);
    }

    public void dispatchEvent(String eventType, Object eventData) {
        List<EventListener> listeners = eventListeners.getOrDefault(eventType, new ArrayList<>());
        for (EventListener listener : listeners) {
            listener.onEvent(eventType, eventData);
        }
    }
}
