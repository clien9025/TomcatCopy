package org.apache.catalina.util;

import org.apache.catalina.Globals;
import org.apache.catalina.JmxEnabled;
import org.apache.catalina.LifecycleException;
import org.apache.tomcat.util.modeler.Registry;

import javax.management.MBeanServer;
import javax.management.ObjectName;

public abstract class LifecycleMBeanBase extends LifecycleBase
        implements JmxEnabled {


    /**
     * Method implemented by sub-classes to identify the domain in which MBeans
     * should be registered.
     *
     * @return The name of the domain to use to register MBeans.
     */
    protected String getDomainInternal() {
        throw new UnsupportedOperationException();
    }


    /**
     * 源码这里是实现了，并且类型是 final
     *
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
     * @return The string representation of the key properties component of the
     * desired {@link ObjectName}
     */
    protected String getObjectNameKeyProperties() {
        throw new UnsupportedOperationException();
    }

    /* Cache components of the MBean registration. */
    private String domain = null;
    private ObjectName oname = null;

    /**
     * Sub-classes wishing to perform additional clean-up should override this
     * method, ensuring that super.destroyInternal() is the last call in the
     * overriding method.
     * <p>
     * 这个方法通常用于生命周期管理，特别是在需要销毁对象和清理资源的时候
     * 总之，destroyInternal 方法是一个生命周期管理方法，专门用于在对象生命周期的销毁阶段执行清理工作。
     * 该方法的设计允许在不改变基本销毁逻辑的情况下，通过子类扩展来增加额外的清理操作
     */
    @Override
    protected void destroyInternal() throws LifecycleException {
        // 在此实现中，它调用 unregister(oname) 方法，其中 oname 是一个 ObjectName 类型的变量，代表需要注销的 JMX 组件。
        unregister(oname);
    }

    /**
     * Utility method to enable sub-classes to easily unregister additional
     * components that don't implement {@link JmxEnabled} with an MBean server.
     * <br>
     * Note: This method should only be used once {@link #initInternal()} has
     * been called and before {@link #destroyInternal()} has been called.
     * <p>
     * 这是一个辅助方法，用于帮助子类轻松地从 MBean 服务器注销不实现 JmxEnabled 接口的组件。
     *
     * @param on The name of the component to unregister
     */
    protected final void unregister(ObjectName on) {
        Registry.getRegistry(null, null).unregisterComponent(on);
    }


}
