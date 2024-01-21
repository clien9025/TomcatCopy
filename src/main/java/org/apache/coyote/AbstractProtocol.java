package org.apache.coyote;


import org.apache.juli.logging.Log;
import org.apache.tomcat.util.net.AbstractEndpoint;
import org.apache.tomcat.util.net.SocketWrapperBase;

import javax.management.MBeanRegistration;
import javax.management.MBeanServer;
import javax.management.ObjectName;

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


    /*
     * When Tomcat expects data from the client, this is the time Tomcat will wait for that data to arrive before
     * closing the connection.
     */
    public int getConnectionTimeout() {
        return endpoint.getConnectionTimeout();
    }

    public void setConnectionTimeout(int timeout) {
        endpoint.setConnectionTimeout(timeout);
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


    public int getPort() {
        return endpoint.getPort();
    }

    public void setPort(int port) {
        endpoint.setPort(port);
    }


    public int getPortOffset() {
        return endpoint.getPortOffset();
    }



    // -------------------------------------------------------- Abstract methods

    /**
     * Concrete implementations need to provide access to their logger to be used by the abstract classes.
     *
     * @return the logger
     */
    protected Log getLog(){
        throw new UnsupportedOperationException();
    }


    /**
     * Obtain the prefix to be used when construction a name for this protocol handler. The name will be
     * prefix-address-port.
     *
     * @return the prefix
     */
    protected String getNamePrefix(){
        throw new UnsupportedOperationException();
    }


    /**
     * Obtain the name of the protocol, (Http, Ajp, etc.). Used with JMX.
     *
     * @return the protocol name
     */
    protected String getProtocolName(){
        throw new UnsupportedOperationException();
    }


    /**
     * Find a suitable handler for the protocol negotiated at the network layer.
     *
     * @param name The name of the requested negotiated protocol.
     *
     * @return The instance where {@link UpgradeProtocol#getAlpnName()} matches the requested protocol
     */
    protected UpgradeProtocol getNegotiatedProtocol(String name){
        throw new UnsupportedOperationException();
    }


    /**
     * Find a suitable handler for the protocol upgraded name specified. This is used for direct connection protocol
     * selection.
     *
     * @param name The name of the requested negotiated protocol.
     *
     * @return The instance where {@link UpgradeProtocol#getAlpnName()} matches the requested protocol
     */
    protected UpgradeProtocol getUpgradeProtocol(String name){
        throw new UnsupportedOperationException();
    }


    /**
     * Create and configure a new Processor instance for the current protocol implementation.
     *
     * @return A fully configured Processor instance that is ready to use
     */
    protected Processor createProcessor(){
        throw new UnsupportedOperationException();
    }


    protected Processor createUpgradeProcessor(SocketWrapperBase<?> socket, UpgradeToken upgradeToken){
        throw new UnsupportedOperationException();
    }


    // ----------------------------------------------------- JMX related methods

    protected String domain;
    protected ObjectName oname;
    protected MBeanServer mserver;

    public ObjectName getObjectName() {
        return oname;
    }

    public String getDomain() {
        return domain;
    }

    @Override
    public ObjectName preRegister(MBeanServer server, ObjectName name) throws Exception {
        oname = name;
        mserver = server;
        domain = name.getDomain();
        return name;
    }

    @Override
    public void postRegister(Boolean registrationDone) {
        // NOOP
    }

    @Override
    public void preDeregister() throws Exception {
        // NOOP
    }

    @Override
    public void postDeregister() {
        // NOOP
    }
}
