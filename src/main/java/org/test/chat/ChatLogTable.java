package org.test.chat;

import net.jcip.annotations.ThreadSafe;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

@ThreadSafe
public class ChatLogTable {

    static {
        MySqlUtil.initialize();
    }

    private static final String createTableQueryString =
            "CREATE TABLE IF NOT EXISTS log (" +
                    "id BIGINT not null AUTO_INCREMENT,"+
                    "name VARCHAR(64) NOT NULL," +
                    "message TEXT NOT NULL, " +
                    "PRIMARY KEY (id)" +
                    ") CHARSET utf8;";

    private static final String readLastQueryString =
            "SELECT name, message from (" +
                    "SELECT id, name, message FROM log ORDER BY id DESC LIMIT ?) sub " +
                    "ORDER BY id ASC;";

    private static final String insertQueryString = "INSERT INTO log (name, message) VALUES (?, ?);";

    private final String connectionString;

    public ChatLogTable(String connectionString) throws SQLException {
        ensureTableExists(connectionString);
        this.connectionString = Objects.requireNonNull(connectionString, "connectionString == null");
    }

    private static void ensureTableExists(String connectionString) throws SQLException {
        try (Connection connection = DriverManager.getConnection(connectionString);
             PreparedStatement statement = connection.prepareStatement(createTableQueryString)) {
            statement.execute();
        }
    }

    public Iterable<ChatMessage> readLast(int limit) throws SQLException {
        try (Connection connection = DriverManager.getConnection(connectionString);
             PreparedStatement statement = connection.prepareStatement(readLastQueryString)) {
            statement.setInt(1, limit);
            ResultSet rs = statement.executeQuery();
            boolean hasRow = rs.first();
            Collection<ChatMessage> messages = new ArrayList<>();
            while (hasRow) {
                String name = rs.getString("name");
                String text = rs.getString("message");
                messages.add(new ChatMessage(name, text));
                hasRow = rs.next();
            }
            return messages;
        }
    }

    public void insert(ChatMessage message) throws SQLException {
        try (Connection connection = DriverManager.getConnection(connectionString);
             PreparedStatement statement = connection.prepareStatement(insertQueryString)) {
            statement.setString(1, message.user);
            statement.setString(2, message.text);
            statement.execute();
        }
    }
}