package org.test.chat;

import net.jcip.annotations.ThreadSafe;

import java.sql.*;
import java.util.Objects;

@ThreadSafe
class ChatLogger {

    private final EventServer eventServer = EventServer.getEventServer();

    private final EventServer.Listener listener = e -> log((ChatMessage) e);

    private final MessagesTable messagesTable;

    ChatLogger(MessagesTable messagesTable) {
        this.messagesTable = Objects.requireNonNull(messagesTable);
        eventServer.subscribe(ChatEvent.NEW_MESSAGE.name(), listener);
    }

    private void log(ChatMessage message) {
        try {
            messagesTable.insert(message);
        } catch (SQLException e) {
            throw new RuntimeException("can't insert message: " + message, e);
        }
    }

    void dispose() {
        eventServer.unsubscribe(ChatEvent.NEW_MESSAGE.name(), listener);
    }
}