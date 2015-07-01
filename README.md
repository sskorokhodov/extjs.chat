# Simple chat

Allows to send messages to all connected clients and receive all messages from
them. The only constraint is user name which is required and must consist of 
less than 64 characters.

Current user name is displayed in chat's header in brackets.

To enter chat open `http://CHAT_SERVER_HOST:CHAT_SERVER_PORT`.


## Installation

1. Check if all requirements satisfied.
2. Download latest build [here](https://bitbucket.org/sskorokhodov/extjs.chat/downloads/chat.zip).
3. Extract archive.
4. Put appropriate settings into `chat.properties`.
    - ensure you have database to connect
5. In terminal go to application directory and run `java -jar chat.jar`.


### Requirements

- Java 1.8+
- MySQL 5.1+ (must be running before application starts)


## Settings

All chat settings are kept in `chat.properties` file in the directory where
chat's .jar file is located.

Parameters:

`jdbcUri` - URI representing database to connect. Database from URI must exist.
E.g. `jdbc:mysql://localhost/chat?user=root&characterEncoding=utf8`.

`port` - HTTP server port. Defaults to `8080`.