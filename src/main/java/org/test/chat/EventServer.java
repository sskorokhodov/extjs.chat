package org.test.chat;

import net.jcip.annotations.ThreadSafe;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@ThreadSafe
public class EventServer {

    private static final EventServer eventServer = new EventServer();

    public static EventServer getEventServer() {
        return eventServer;
    }

    private EventServer() {
    }

    private final Map<String, Set<Listener>> eventToListeners = new ConcurrentHashMap<>();

    public void publish(String eventName, Object event) {
        Objects.requireNonNull(eventName, "eventName == null");
        Set<Listener> ls = eventToListeners.get(eventName);
        if (ls != null) {
            ls.forEach(l -> {
                try {
                    l.process(event);
                } catch (RuntimeException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    public void subscribe(String eventName, Listener listener) {
        Objects.requireNonNull(eventName, "eventName == null");
        Objects.requireNonNull(listener, "listener == null");
        eventToListeners.compute(eventName, (e, ls) -> {
            if (ls == null) {
                ls = Collections.newSetFromMap(new ConcurrentHashMap<>());
            }
            ls.add(listener);
            return ls;
        });
    }

    public void unsubscribe(String eventName, Listener listener) {
        Objects.requireNonNull(eventName, "eventName == null");
        Objects.requireNonNull(listener, "listener == null");
        eventToListeners.computeIfPresent(eventName, (e, ls) -> {
            ls.remove(listener);
            return ls.isEmpty() ? null : ls;
        });
    }

    @FunctionalInterface
    public interface Listener {

        void process(Object event);
    }
}
