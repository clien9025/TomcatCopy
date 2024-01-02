package org.apache.coyote.http11;

import org.apache.coyote.Adapter;
import org.apache.coyote.UpgradeProtocol;
import org.apache.tomcat.util.net.AbstractEndpoint;
import org.apache.tomcat.util.net.NioChannel;
import org.apache.tomcat.util.net.NioEndpoint;
import org.apache.tomcat.util.net.SSLHostConfig;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledExecutorService;

/**
 * HTTP/1.1 protocol implementation using NIO.
 */
public class Http11NioProtocol extends AbstractHttp11JsseProtocol<NioChannel> {

    public Http11NioProtocol() {
        this(new NioEndpoint());
    }
    public Http11NioProtocol(AbstractEndpoint<NioChannel, ?> endpoint) {
        super(endpoint);
    }

    @Override
    public ObjectName preRegister(MBeanServer server, ObjectName name) throws Exception {
        throw new UnsupportedOperationException();
    }

    @Override
    public void postRegister(Boolean registrationDone) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void preDeregister() throws Exception {
        throw new UnsupportedOperationException();
    }

    @Override
    public void postDeregister() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Adapter getAdapter() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setAdapter(Adapter adapter) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Executor getExecutor() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setExecutor(Executor executor) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ScheduledExecutorService getUtilityExecutor() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setUtilityExecutor(ScheduledExecutorService utilityExecutor) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void init() throws Exception {
        throw new UnsupportedOperationException();
    }

    @Override
    public void start() throws Exception {
        throw new UnsupportedOperationException();
    }

    @Override
    public void pause() throws Exception {
        throw new UnsupportedOperationException();
    }

    @Override
    public void resume() throws Exception {
        throw new UnsupportedOperationException();
    }

    @Override
    public void stop() throws Exception {
        throw new UnsupportedOperationException();
    }

    @Override
    public void destroy() throws Exception {
        throw new UnsupportedOperationException();
    }

    @Override
    public void closeServerSocketGraceful() {
        throw new UnsupportedOperationException();
    }

    @Override
    public long awaitConnectionsClose(long waitMillis) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isSendfileSupported() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addSslHostConfig(SSLHostConfig sslHostConfig) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addSslHostConfig(SSLHostConfig sslHostConfig, boolean replace) {
        throw new UnsupportedOperationException();
    }

    @Override
    public SSLHostConfig[] findSslHostConfigs() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addUpgradeProtocol(UpgradeProtocol upgradeProtocol) {
        throw new UnsupportedOperationException();
    }

    @Override
    public UpgradeProtocol[] findUpgradeProtocols() {
        throw new UnsupportedOperationException();
    }
}
