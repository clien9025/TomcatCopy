package org.apache.tomcat.util;

public class ExceptionUtils {

    /**
     * Checks whether the supplied Throwable is one that needs to be
     * rethrown and swallows all others.
     * @param t the Throwable to check
     */
    public static void handleThrowable(Throwable t) {
//        if (t instanceof ThreadDeath) {
//            throw (ThreadDeath) t;
//        }
//        if (t instanceof StackOverflowError) {
//            // Swallow silently - it should be recoverable
//            return;
//        }
//        if (t instanceof VirtualMachineError) {
//            throw (VirtualMachineError) t;
//        }
//        // All other instances of Throwable will be silently swallowed
        throw new UnsupportedOperationException();
    }

    /**
     * NO-OP method provided to enable simple pre-loading of this class. Since
     * the class is used extensively in error handling, it is prudent to
     * pre-load it to avoid any failure to load this class masking the true
     * problem during error handling.
     */
    public static void preload() {
        // NO-OP
    }
}
