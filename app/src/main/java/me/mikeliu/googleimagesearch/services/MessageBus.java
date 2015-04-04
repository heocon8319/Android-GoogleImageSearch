package me.mikeliu.googleimagesearch.services;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public final class MessageBus {
    public final static String E_NEW_SEARCH = "NewSearch";
    public final static String E_FETCH_MORE = "FetchMore";

    private static MessageBus _instance;
    private HashMap<String, Set<Listener>> _listeners;

    public MessageBus() {
        _listeners = new HashMap<>();
    }

    public void addListener(String event, Listener listener) {
        Set<Listener> set;

        if (_listeners.containsKey(event)) {
            set = _listeners.get(event);
        } else {
            set = new HashSet<>();
            _listeners.put(event, set);
        }

        set.add(listener);
    }

    public void removeListener(String event, Listener listener) {
        if (_listeners.containsKey(event)) {
            Set<Listener> set = _listeners.get(event);
            if (set.contains(listener)) {
                set.remove(listener);
            }
        }
    }

    public void sendEvent(String event, Object object) {
        if (_listeners.containsKey(event)) {
            Set<Listener> set = _listeners.get(event);
            for(Listener listener : set) {
                listener.notify(event, object);
            }
        }
    }

    public static MessageBus instance() {
        if (_instance == null) {
            _instance = new MessageBus();
        }

        return _instance;
    }

    public static interface Listener {
        public void notify(String event, Object object);
    }
}
