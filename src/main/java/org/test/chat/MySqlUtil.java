package org.test.chat;

import net.jcip.annotations.ThreadSafe;

@ThreadSafe
class MySqlUtil {

    static void initialize() {
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException("can't initialize mysql jdbc driver", e);
        }
    }
}
