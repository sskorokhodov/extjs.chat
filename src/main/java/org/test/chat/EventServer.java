package org.test.chat;

import net.jcip.annotations.ThreadSafe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@ThreadSafe
class EventServer {

    private static final Logger log = LoggerFactory.getLogger(EventServer.class);

    private static final EventServer eventServer = new EventServer();

    static EventServer getEventServer() {
        return eventServer;
    }

    private EventServer() {
    }

    private final Map<String, Set<Listener>> eventToListeners = new ConcurrentHashMap<>();

    void publish(String eventName, Object event) {
        Objects.requireNonNull(eventName, "eventName == null");
        Objects.requireNonNull(event, "event == null");
        Set<Listener> ls = eventToListeners.get(eventName);
        if (ls != null) {
            ls.forEach(l -> {
                try {
                    l.process(event);
                } catch (RuntimeException e) {
                    log.error("error while processing message with listener {}", l, e);
                }
            });
        }
    }

    void subscribe(String eventName, Listener listener) {
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

    void unsubscribe(String eventName, Listener listener) {
        Objects.requireNonNull(eventName, "eventName == null");
        Objects.requireNonNull(listener, "listener == null");
        eventToListeners.computeIfPresent(eventName, (e, ls) -> {
            ls.remove(listener);
            return ls.isEmpty() ? null : ls;
        });
    }

    @FunctionalInterface
    interface Listener {

        void process(Object event);
    }
}
