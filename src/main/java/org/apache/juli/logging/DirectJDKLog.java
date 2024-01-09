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


    @Override
    public final void debug(Object message, Throwable t) {
        log(Level.FINE, String.valueOf(message), t);
    }

    @Override
    public final void fatal(Object message, Throwable t) {
        log(Level.SEVERE, String.valueOf(message), t);
    }

    // from commons logging. This would be my number one reason why java.util.logging
    // is bad - design by committee can be really bad ! The impact on performance of
    // using java.util.logging - and the ugliness if you need to wrap it - is far
    // worse than the unfriendly and uncommon default format for logs.

    private void log(Level level, String msg, Throwable ex) {
        if (logger.isLoggable(level)) {
            // Hack (?) to get the stack trace.
            Throwable dummyException=new Throwable();
            StackTraceElement locations[]=dummyException.getStackTrace();
            // Caller will be the third element
            String cname = "unknown";
            String method = "unknown";
            if (locations != null && locations.length >2) {
                StackTraceElement caller = locations[2];
                cname = caller.getClassName();
                method = caller.getMethodName();
            }
            if (ex==null) {
                logger.logp(level, cname, method, msg);
            } else {
                logger.logp(level, cname, method, msg, ex);
            }
        }
    }

}
