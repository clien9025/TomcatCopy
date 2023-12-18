package org.apache.catalina.util;

import org.apache.catalina.Globals;
import org.apache.catalina.JmxEnabled;
import org.apache.catalina.LifecycleException;

import javax.management.MBeanServer;
import javax.management.ObjectName;

public abstract class LifecycleMBeanBase extends LifecycleBase
        implements JmxEnabled {


    /* Cache components of the MBean registration. */
    private String domain = null;
//    private ObjectName oname = null;


    /**
     * Method implemented by sub-classes to identify the domain in which MBeans
     * should be registered.
     *
     * @return  The name of the domain to use to register MBeans.
     */
    protected abstract String getDomainInternal();


    /**
     * 源码这里是实现了，并且类型是 final
     * @return
     */
    @Override
    public final ObjectName getObjectName() {
//        return oname;
        throw new UnsupportedOperationException();
    }


    /**
     * Obtain the domain under which this component will be / has been
     * registered.
     */
    @Override
    public final String getDomain() {
        if (domain == null) {
            domain = getDomainInternal();
        }

        if (domain == null) {
            domain = Globals.DEFAULT_MBEAN_DOMAIN;
        }

        return domain;
    }

    /**
     * Specify the domain under which this component should be registered. Used
     * with components that cannot (easily) navigate the component hierarchy to
     * determine the correct domain to use.
     */
    @Override
    public final void setDomain(String domain) {
        this.domain = domain;
    }


    /**
     * Allows the object to be registered with an alternative
     * {@link MBeanServer} and/or {@link ObjectName}.
     */
    @Override
    public final ObjectName preRegister(MBeanServer server, ObjectName name)
            throws Exception {

//        this.oname = name;
//        this.domain = name.getDomain().intern();
//
//        return oname;
        throw new UnsupportedOperationException();
    }
    /**
     * Sub-classes wishing to perform additional initialization should override
     * this method, ensuring that super.initInternal() is the first call in the
     * overriding method.
     */
    @Override
    protected void initInternal() throws LifecycleException {
//        // If oname is not null then registration has already happened via
//        // preRegister().
//        if (oname == null) {
//            oname = register(this, getObjectNameKeyProperties());
//        }
        throw new UnsupportedOperationException();
    }

    /**
     * Not used - NOOP.
     */
    @Override
    public final void postRegister(Boolean registrationDone) {
        // NOOP
    }

    /**
     * Not used - NOOP.
     */
    @Override
    public final void postDeregister() {
        // NOOP
    }


    /**
     * Not used - NOOP.
     */
    @Override
    public final void preDeregister() throws Exception {
        // NOOP
    }


    /**
     * Allow sub-classes to specify the key properties component of the
     * {@link ObjectName} that will be used to register this component.
     *
     * @return  The string representation of the key properties component of the
     *          desired {@link ObjectName}
     */
    protected abstract String getObjectNameKeyProperties();




}
