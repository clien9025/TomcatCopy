package org.apache.tomcat.util.file;

public class ConfigFileLoader {

    private static ConfigurationSource source;

    /**
     * Get the configured configuration source. If none has been configured,
     * a default source based on the calling directory will be used.
     * @return the configuration source in use
     */
    public static final ConfigurationSource getSource() {
//        if (source == null) {
//            return ConfigurationSource.DEFAULT;
//        }
//        return source;
        throw new UnsupportedOperationException();
    }

    /**
     * Set the configuration source used by Tomcat to locate various
     * configuration resources.
     * @param source The source
     */
    public static final void setSource(ConfigurationSource source) {
        if (ConfigFileLoader.source == null) {
            ConfigFileLoader.source = source;
        }
    }

    private ConfigFileLoader() {
        // Hide the constructor
    }
}
