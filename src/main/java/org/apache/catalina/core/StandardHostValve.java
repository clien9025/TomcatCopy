package org.apache.catalina.core;

import org.apache.catalina.valves.ValveBase;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.res.StringManager;

/**
 * Valve that implements the default basic behavior for the <code>StandardHost</code> container implementation.
 * <p>
 * <b>USAGE CONSTRAINT</b>: This implementation is likely to be useful only when processing HTTP requests.
 *
 * @author Craig R. McClanahan
 * @author Remy Maucherat
 */
final class StandardHostValve extends ValveBase {



    private static final Log log = LogFactory.getLog(StandardHostValve.class);
    private static final StringManager sm = StringManager.getManager(StandardHostValve.class);
}
