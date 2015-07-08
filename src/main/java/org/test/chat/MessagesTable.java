package org.test.chat;

import net.jcip.annotations.ThreadSafe;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

@ThreadSafe
class MessagesTable {

    static {
        MySqlUtil.initialize();
    }

    private static final String TABLE_NAME = "messages";

    private static final String USER_FIELD_NAME = "user";

    private static final String TEXT_FIELD_NAME = "text";

    private static final String CREATE_TABLE_QUERY_STRING =
            "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                    "id BIGINT not null AUTO_INCREMENT," +
                    USER_FIELD_NAME + " VARCHAR(64) NOT NULL," +
                    TEXT_FIELD_NAME + " TEXT NOT NULL, " +
                    "PRIMARY KEY (id)" +
                    ") CHARSET utf8;";

    private static final String READ_LAST_QUERY_STRING =
            "SELECT " + USER_FIELD_NAME + ", " + TEXT_FIELD_NAME + " from (" +
                    "SELECT id, " + USER_FIELD_NAME + ", " + TEXT_FIELD_NAME +
                    " FROM " + TABLE_NAME + " ORDER BY id DESC LIMIT ?) sub" +
                    " ORDER BY id ASC;";

    private static final String INSERT_QUERY_STRING =
            "INSERT INTO " + TABLE_NAME + " (" +
                    USER_FIELD_NAME + ", " +
                    TEXT_FIELD_NAME +
                    ") VALUES (?, ?);";

    private final String connectionString;

    MessagesTable(String connectionString) throws SQLException {
        this.connectionString = Objects.requireNonNull(connectionString, "connectionString == null");
        ensureTableExists(connectionString);
    }

    private static void ensureTableExists(String connectionString) throws SQLException {
        try (Connection connection = DriverManager.getConnection(connectionString);
             PreparedStatement statement = connection.prepareStatement(CREATE_TABLE_QUERY_STRING)) {
            statement.execute();
        }
    }

    Iterable<ChatMessage> readLast(int limit) throws SQLException {
        if (limit < 0) {
            throw new IllegalArgumentException("limit < 0");
        } else if (limit == 0) {
            return new ArrayList<>(0);
        }
        try (Connection connection = DriverManager.getConnection(connectionString);
             PreparedStatement statement = connection.prepareStatement(READ_LAST_QUERY_STRING)) {
            statement.setInt(1, limit);
            ResultSet rs = statement.executeQuery();
            boolean hasRow = rs.first();
            Collection<ChatMessage> messages = new ArrayList<>();
            while (hasRow) {
                String user = rs.getString(USER_FIELD_NAME);
                String text = rs.getString(TEXT_FIELD_NAME);
                messages.add(new ChatMessage(user, text));
                hasRow = rs.next();
            }
            return messages;
        }
    }

    void insert(ChatMessage message) throws SQLException {
        Objects.requireNonNull(message, "message == null");
        try (Connection connection = DriverManager.getConnection(connectionString);
             PreparedStatement statement = connection.prepareStatement(INSERT_QUERY_STRING)) {
            statement.setString(1, message.user);
            statement.setString(2, message.text);
            statement.execute();
        }
    }
}