package org.apache.coyote;


import org.apache.tomcat.util.net.AbstractEndpoint;

import javax.management.MBeanRegistration;

public abstract class AbstractProtocol<S> implements ProtocolHandler, MBeanRegistration {

    /**
     * Endpoint that provides low-level network I/O - must be matched to the ProtocolHandler implementation
     * (ProtocolHandler using NIO, requires NIO Endpoint etc.).
     */
    private final AbstractEndpoint<S, ?> endpoint;

    public AbstractProtocol(AbstractEndpoint<S, ?> endpoint) {
        this.endpoint = endpoint;
//        ConnectionHandler<S> cHandler = new ConnectionHandler<>(this);
//        getEndpoint().setHandler(cHandler);
//        setHandler(cHandler);
//        setConnectionLinger(Constants.DEFAULT_CONNECTION_LINGER);
//        setTcpNoDelay(Constants.DEFAULT_TCP_NO_DELAY);

//        throw new UnsupportedOperationException();
    }

    // ----------------------------------------------- Accessors for sub-classes

    protected AbstractEndpoint<S, ?> getEndpoint() {
        return endpoint;
    }

    public void setMaxThreads(int maxThreads) {
        endpoint.setMaxThreads(maxThreads);
    }

    public void setMinSpareThreads(int minSpareThreads) {
        endpoint.setMinSpareThreads(minSpareThreads);
    }

    public void setMaxConnections(int maxConnections) {// 传入8192
        endpoint.setMaxConnections(maxConnections);
    }

    public void setAcceptCount(int acceptCount) {
        endpoint.setAcceptCount(acceptCount);
    }
    /**
     * The maximum number of idle processors that will be retained in the cache and re-used with a subsequent request.
     * The default is 200. A value of -1 means unlimited. In the unlimited case, the theoretical maximum number of
     * cached Processor objects is {@link #getMaxConnections()} although it will usually be closer to
     * {@link #getMaxThreads()}.
     */
    protected int processorCache = 200;

    public int getProcessorCache() {
        return this.processorCache;
    }

    public void setProcessorCache(int processorCache) {
        this.processorCache = processorCache;
    }
}
