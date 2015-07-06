package org.test.chat;

import net.jcip.annotations.ThreadSafe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ThreadSafe
class MySqlUtil {

    private static final Logger log = LoggerFactory.getLogger(MySqlUtil.class);

    static void initialize() {
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            log.error("can't initialize mysql jdbc driver", e);
            throw new RuntimeException("can't initialize mysql jdbc driver", e);
        }
    }
}
