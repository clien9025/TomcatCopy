package org.apache.catalina.core;

import org.apache.catalina.valves.ValveBase;

/**
 * Valve that implements the default basic behavior for the <code>StandardEngine</code> container implementation.
 * <p>
 * <b>USAGE CONSTRAINT</b>: This implementation is likely to be useful only when processing HTTP requests.
 *
 * @author Craig R. McClanahan
 */
final class StandardEngineValve extends ValveBase {

    // ------------------------------------------------------ Constructor
    StandardEngineValve() {
        super(true);
    }
}
