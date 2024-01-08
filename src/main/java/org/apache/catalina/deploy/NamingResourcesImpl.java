package org.apache.catalina.deploy;

import org.apache.catalina.JmxEnabled;
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
    protected String getDomainInternal() {
//        // Use the same domain as our associated container if we have one
//        Object c = getContainer();
//
//        if (c instanceof JmxEnabled) {
//            return ((JmxEnabled) c).getDomain();
//        }
//
//        return null;
        throw new UnsupportedOperationException();
    }

    @Override
    protected void startInternal() throws LifecycleException {
//        fireLifecycleEvent(CONFIGURE_START_EVENT, null);
//        setState(LifecycleState.STARTING);
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
