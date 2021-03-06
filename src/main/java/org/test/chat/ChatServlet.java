package org.test.chat;

import com.google.gson.Gson;
import net.jcip.annotations.ThreadSafe;
import org.eclipse.jetty.util.ConcurrentArrayQueue;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Queue;

@ThreadSafe
class ChatServlet extends HttpServlet {

    private final Gson gson = new Gson();

    private final EventServer eventServer = EventServer.getEventServer();

    private final int logSize;

    private final Queue<ChatMessage> messages;

    private final EventServer.Listener listener = e -> onChatMessage((ChatMessage) e);

    ChatServlet(int logSize) {
        if (logSize < 0) {
            throw new IllegalArgumentException("logSize < 0");
        }
        this.logSize = logSize;
        this.messages = new ConcurrentArrayQueue<>();
        eventServer.subscribe(ChatEvent.NEW_MESSAGE.name(), listener);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_OK);
        String json = gson.toJson(messages.toArray());
        response.getWriter().println(json);
    }

    @Override
    public void destroy() {
        eventServer.unsubscribe(ChatEvent.NEW_MESSAGE.name(), listener);
    }

    private synchronized void onChatMessage(ChatMessage message) {
        if (messages.size() >= logSize) {
            messages.poll();
        }
        messages.add(message);
    }
}