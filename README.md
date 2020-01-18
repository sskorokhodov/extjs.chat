# Simple chat

Allows sending messages to all the connected clients and receive all their
messages. The only constraint is the user name which is required and must be
less than 64 characters.

The current user name is displayed in the chat header in brackets.

To join the chat open `http://CHAT_SERVER_HOST:CHAT_SERVER_PORT` in browser.

## Installation

1. Check if all the requirements are satisfied.
2. Download the latest build [here][latest-build].
3. Extract the archive.
4. Configure the server by editing `chat.properties`.
5. Ensure the database is up and running.
5. Run the server `java -jar chat.jar`.

### Requirements

- Java 1.8+
- MySQL 5.1+ (must be running before the server)


## Settings

The chat configuration file is `etc/chat.properties`.

Parameters:

`jdbcUri` - The DB connection URI.
E.g., `jdbc:mysql://localhost/chat?user=root&characterEncoding=utf8`.

`port` - HTTP server port. Defaults to `8080`.

[latest-build]: https://bitbucket.org/sskorokhodov/extjs.chat/downloads/chat.zip
