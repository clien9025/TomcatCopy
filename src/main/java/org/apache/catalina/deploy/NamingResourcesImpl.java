package org.apache.catalina.deploy;

import org.apache.catalina.LifecycleException;
import org.apache.catalina.LifecycleListener;
import org.apache.catalina.LifecycleState;
import org.apache.catalina.util.LifecycleMBeanBase;
import org.apache.tomcat.util.descriptor.web.ContextEnvironment;
import org.apache.tomcat.util.descriptor.web.ContextResource;
import org.apache.tomcat.util.descriptor.web.ContextResourceLink;
import org.apache.tomcat.util.descriptor.web.NamingResources;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.io.Serializable;

public class NamingResourcesImpl extends LifecycleMBeanBase implements Serializable, NamingResources {
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
//        return null;
        throw new UnsupportedOperationException();
    }

    @Override
    public String getStateName() {
//        return null;
        throw new UnsupportedOperationException();
    }

    @Override
    public void addEnvironment(ContextEnvironment ce) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void removeEnvironment(String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addResource(ContextResource cr) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void removeResource(String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addResourceLink(ContextResourceLink crl) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void removeResourceLink(String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object getContainer() {
//        return null;
        throw new UnsupportedOperationException();
    }
}
