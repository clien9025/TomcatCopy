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
//        endpoint.setAcceptCount(acceptCount);
        throw new UnsupportedOperationException();
    }
    public void setProcessorCache(int processorCache) {
//        this.processorCache = processorCache;
        throw new UnsupportedOperationException();
    }
}
