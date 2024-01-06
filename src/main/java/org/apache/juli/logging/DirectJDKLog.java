package org.apache.juli.logging;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Hard-coded java.util.logging commons-logging implementation.
 */
class DirectJDKLog implements Log {


    public final Logger logger;

    static Log getInstance(String name) {
        return new DirectJDKLog( name );
//        throw new UnsupportedOperationException();
    }

    DirectJDKLog(String name ) {
        // Logger.getLogger(name) 结果是： org.apache.tomcat.util.modeler.Registry
        // Logger.getLogger 是 jdk 底层源码了，就是以 name 为 key 去取 logger 对象，
        // 有很多类型的 demand（请求）log，有系统类型的log，有默认的 log，有本身就有的 log，等等。
        logger= Logger.getLogger(name);
    }


    @Override
    public final boolean isDebugEnabled() {
        return logger.isLoggable(Level.FINE);
    }

}
