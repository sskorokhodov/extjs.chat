package org.test.chat;

import com.google.gson.Gson;
import net.jcip.annotations.ThreadSafe;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Objects;

@ThreadSafe
public class ChatLogServlet extends HttpServlet {

    private final Gson gson = new Gson();

    private final ChatLogTable chatLogTable;

    public ChatLogServlet(ChatLogTable chatLogTable) {
        this.chatLogTable = Objects.requireNonNull(chatLogTable);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_OK);
        int limit = Integer.parseInt(request.getParameter("limit"));
        Iterable<ChatMessage> messages = readLastMessages(limit);
        messages.forEach(System.out::println);
        String json = gson.toJson(messages);
        response.getWriter().println(json);
    }

    private Iterable<ChatMessage> readLastMessages(int limit) throws ServletException {
        try {
            return chatLogTable.readLast(limit);
        } catch (SQLException e) {
            throw new ServletException("can't read last " + limit + " messages", e);
        }
    }
}