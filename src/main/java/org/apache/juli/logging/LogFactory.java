package org.apache.juli.logging;

//@ServiceConsumer(value=Log.class)
public class LogFactory {

    /**
     * Convenience method to return a named logger, without the application
     * having to care about factories.
     *
     * @param clazz Class from which a log name will be derived
     *
     * @return A log instance with a name of clazz.getName()
     *
     * @exception LogConfigurationException if a suitable <code>Log</code>
     *  instance cannot be returned
     */
    public static Log getLog(Class<?> clazz)
            throws LogConfigurationException {
//        return getFactory().getInstance(clazz);
        throw new UnsupportedOperationException();
    }
}
