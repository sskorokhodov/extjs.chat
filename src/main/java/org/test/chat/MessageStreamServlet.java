package org.test.chat;

import com.google.gson.Gson;
import net.jcip.annotations.NotThreadSafe;
import net.jcip.annotations.ThreadSafe;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.eclipse.jetty.websocket.servlet.WebSocketServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ThreadSafe
class MessageStreamServlet extends WebSocketServlet {

    private static final Logger log = LoggerFactory.getLogger(MessageStreamServlet.class);

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
            log.debug("got connection: {}", session);
            listener = e -> {
                try {
                    session.getRemote().sendStringByFuture(gson.toJson(e));
                } catch (Throwable t) {
                    log.error("can't send WS message to remote client, message: {}, session: {}", e, session, t);
                    session.close();
                }
            };
            eventServer.subscribe(ChatEvent.CHAT_MESSAGE.name(), listener);
        }

        @OnWebSocketClose
        public void onClose(int statusCode, String reason) {
            log.debug("connection closed: {} - {}", statusCode, reason);
            if (listener != null) {
                eventServer.unsubscribe(ChatEvent.CHAT_MESSAGE.name(), listener);
                listener = null;
            }
        }
    }
}
