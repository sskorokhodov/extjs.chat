package org.test.chat;

import net.jcip.annotations.ThreadSafe;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@ThreadSafe
class SendMessageServlet extends HttpServlet {

    private final EventServer eventServer = EventServer.getEventServer();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_OK);
        String user = request.getParameter("user");
        String message = request.getParameter("msg");
        System.out.println("got message: " + message);
        ChatMessage chatMessage = new ChatMessage(user, message);
        eventServer.publish(ChatEvent.CHAT_MESSAGE.name(), chatMessage);
    }
}