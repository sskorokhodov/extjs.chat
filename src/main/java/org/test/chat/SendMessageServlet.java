package org.test.chat;

import net.jcip.annotations.ThreadSafe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@ThreadSafe
class SendMessageServlet extends HttpServlet {

    private static final Logger log = LoggerFactory.getLogger(SendMessageServlet.class);

    private final EventServer eventServer = EventServer.getEventServer();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_OK);
        String user = request.getParameter("user");
        String text = request.getParameter("text");
        log.debug("got message, user: " + user + ", text: " + text);
        ChatMessage chatMessage = new ChatMessage(user, text);
        eventServer.publish(ChatEvent.NEW_MESSAGE.name(), chatMessage);
    }
}