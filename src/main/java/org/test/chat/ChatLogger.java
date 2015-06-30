package org.test.chat;

import net.jcip.annotations.ThreadSafe;

import java.sql.*;
import java.util.Objects;

@ThreadSafe
public class ChatLogger {

    static {
        MySqlUtil.initialize();
    }

    private final EventServer eventServer = EventServer.getEventServer();

    private final EventServer.Listener listener = e -> log((ChatMessage) e);

    private final ChatLogTable chatLogTable;

    public ChatLogger(ChatLogTable chatLogTable) {
        this.chatLogTable = Objects.requireNonNull(chatLogTable);
        eventServer.subscribe(ChatEvent.CHAT_MESSAGE.name(), listener);
    }

    private void log(ChatMessage message) {
        try {
            chatLogTable.insert(message);
        } catch (SQLException e) {
            throw new RuntimeException("can't insert message:\n" + message, e);
        }
    }

    void dispose() {
        eventServer.unsubscribe(ChatEvent.CHAT_MESSAGE.name(), listener);
    }
}