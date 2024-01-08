package org.apache.catalina.mapper;

public final class Mapper {

    // --------------------------------------------------------- Public Methods

    /**
     * Set default host.
     *
     * @param defaultHostName Default host name
     */
    public synchronized void setDefaultHostName(String defaultHostName) {
//        this.defaultHostName = renameWildcardHost(defaultHostName);
//        if (this.defaultHostName == null) {
//            defaultHost = null;
//        } else {
//            defaultHost = exactFind(hosts, this.defaultHostName);
//        }
        throw new UnsupportedOperationException();
    }
}
