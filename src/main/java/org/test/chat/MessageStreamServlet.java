package org.test.chat;

import com.google.gson.Gson;
import net.jcip.annotations.NotThreadSafe;
import net.jcip.annotations.ThreadSafe;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.eclipse.jetty.websocket.servlet.*;

import java.util.Objects;

@ThreadSafe
class MessageStreamServlet extends WebSocketServlet {

    private static final EventServer eventServer = EventServer.getEventServer();

    @Override
    public void configure(WebSocketServletFactory factory) {
        factory.setCreator((req, resp) -> new StreamSocket());
    }

    @NotThreadSafe
    @WebSocket
    public static class StreamSocket {

        private final Gson gson = new Gson();

        private EventServer.Listener listener;

        @OnWebSocketConnect
        public void onConnect(Session session) {
            System.out.printf("Got connect: %s%n", session);
            listener = e -> {
                try {
                    session.getRemote().sendStringByFuture(gson.toJson(e));
                } catch (Throwable t) {
                    session.close();
                    t.printStackTrace();
                }
            };
            eventServer.subscribe(ChatEvent.CHAT_MESSAGE.name(), listener);
        }

        @OnWebSocketClose
        public void onClose(int statusCode, String reason) {
            System.out.printf("Connection closed: %d - %s%n", statusCode, reason);
            if (listener != null) {
                eventServer.unsubscribe(ChatEvent.CHAT_MESSAGE.name(), listener);
                listener = null;
            }
        }
    }
}
