package org.apache.catalina.connector;

import org.apache.catalina.LifecycleException;
import org.apache.catalina.LifecycleListener;
import org.apache.catalina.LifecycleState;
import org.apache.catalina.util.LifecycleMBeanBase;

import javax.management.MBeanServer;
import javax.management.ObjectName;

public class Connector extends LifecycleMBeanBase {
    @Override
    public String getDomain() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setDomain(String domain) {
        throw new UnsupportedOperationException();
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
    public void addLifecycleListener(LifecycleListener listener) {
        throw new UnsupportedOperationException();
    }

    @Override
    public LifecycleListener[] findLifecycleListeners() {
//        return new LifecycleListener[0];
        throw new UnsupportedOperationException();
    }

    @Override
    public void removeLifecycleListener(LifecycleListener listener) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void init() throws LifecycleException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void start() throws LifecycleException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void stop() throws LifecycleException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void destroy() throws LifecycleException {
        throw new UnsupportedOperationException();
    }

    @Override
    public LifecycleState getState() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getStateName() {
        throw new UnsupportedOperationException();
    }
}
