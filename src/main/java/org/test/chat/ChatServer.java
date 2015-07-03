package org.test.chat;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.Properties;

public class ChatServer {

    private static final String JDBC_URI;

    private static final int HTTP_SERVER_PORT;

    static {

        final String PROPERTIES_FILE_NAME = "chat.properties";

        final String JDBC_URI_PROPERTY_NAME = "jdbcUri";

        final String PORT_PROPERTY_NAME = "port";

        final int DEFAULT_PORT = 8080;

        try {
            Properties properties = new Properties();
            FileInputStream fileInputStream = new FileInputStream(PROPERTIES_FILE_NAME);
            properties.load(fileInputStream);
            JDBC_URI = Objects.requireNonNull(
                    properties.getProperty(JDBC_URI_PROPERTY_NAME), JDBC_URI_PROPERTY_NAME + " == null");
            HTTP_SERVER_PORT = Integer.parseInt(
                    properties.getProperty(PORT_PROPERTY_NAME, Integer.toString(DEFAULT_PORT)));
        } catch (IOException e) {
            throw new RuntimeException("can't read '" + PROPERTIES_FILE_NAME + "' config file", e);
        }
    }

    private static final int LOG_SIZE = 100;

    private static final String STATIC_RESOURCES_PATH = "web/";

    private static final String WELCOME_FILE_PATH = "index.html";

    private static final String CHAT_LOG_RESOURCE_PATH = "/chat-log";

    private static final String SEND_RESOURCE_PATH = "/send";

    private static final String CHAT_RESOURCE_PATH = "/chat";

    private static final String STREAM_RESOURCE_PATH = "/stream";

    public static void main(String[] args) throws Exception {
        final Server server = new Server();
        final ServerConnector connector = new ServerConnector(server);
        connector.setPort(HTTP_SERVER_PORT);
        server.setConnectors(new Connector[]{connector});

        final ServletContextHandler resourceContextHandler = new ServletContextHandler();
        final ResourceHandler resourceHandler = new ResourceHandler();
        resourceHandler.setResourceBase(STATIC_RESOURCES_PATH);
        resourceHandler.setWelcomeFiles(new String[]{WELCOME_FILE_PATH});
        resourceContextHandler.setHandler(resourceHandler);

        final ServletContextHandler servletContextHandler = new ServletContextHandler();
        servletContextHandler.addServlet(new ServletHolder(new MessageStreamServlet()), STREAM_RESOURCE_PATH);
        servletContextHandler.addServlet(new ServletHolder(new ChatServlet(LOG_SIZE)), CHAT_RESOURCE_PATH);
        servletContextHandler.addServlet(new ServletHolder(new SendMessageServlet()), SEND_RESOURCE_PATH);
        ChatLogTable chatLogTable = new ChatLogTable(JDBC_URI);
        servletContextHandler.addServlet(new ServletHolder(new ChatLogServlet(chatLogTable)), CHAT_LOG_RESOURCE_PATH);

        final HandlerCollection handlers = new HandlerCollection();
        handlers.setHandlers(new Handler[]{resourceContextHandler, servletContextHandler, new DefaultHandler()});
        server.setHandler(handlers);

        final ChatLogger logWriter = new ChatLogger(chatLogTable);

        server.start();
        server.join();

        logWriter.dispose();
    }
}