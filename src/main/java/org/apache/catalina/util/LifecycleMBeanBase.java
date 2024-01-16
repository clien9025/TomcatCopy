package org.apache.catalina.util;

import org.apache.catalina.Globals;
import org.apache.catalina.JmxEnabled;
import org.apache.catalina.LifecycleException;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.modeler.Registry;
import org.apache.tomcat.util.res.StringManager;

import javax.management.MBeanServer;
import javax.management.ObjectName;

public abstract class LifecycleMBeanBase extends LifecycleBase
        implements JmxEnabled {


    private static final Log log = LogFactory.getLog(LifecycleMBeanBase.class);

    private static final StringManager sm =
            StringManager.getManager("org.apache.catalina.util");


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
     * <p>
     * domain ：域，领域；
     * 指定应该在其下注册此组件的域。用于那些不能（轻易地）通过组件层次结构来确定使用哪个正确域的组件。
     * <p>
     * 解释：
     * 这个注释说明了该方法（或属性）的作用是为了指定一个组件在 JMX（Java Management Extensions）中应该注册的域名。
     * 这通常用于那些不能简单地通过它们的组件层次结构来确定自己应该注册到哪个域的情况。在 JMX 中，每个被管理的资源（MBean）都需要在
     * 一个特定的域下进行注册，这样可以组织和区分不同的资源。对于一些复杂的组件，可能不容易直接确定它们应该属于哪个域，因为它们可能嵌套
     * 在多层的组件结构中。这个注释所描述的功能允许开发者显式地为这些组件指定一个域名，而不是依赖于组件结构来自动确定。这样做有助于确保组件
     * 被正确地注册到适当的域中，这对于管理和监控应用程序中的各种组件非常重要。
     */
    @Override
    public final void setDomain(String domain) {
        this.domain = domain;
    }

    /**
     * Utility method to enable sub-classes to easily register additional
     * components that don't implement {@link JmxEnabled} with an MBean server.
     * <br>
     * Note: This method should only be used once {@link #initInternal()} has
     * been called and before {@link #destroyInternal()} has been called.
     * <p>
     * 这段代码定义了一个名为 register 的方法，它的主要功能是帮助子类轻松地将额外的组件注册到 MBean 服务器，
     * 即使这些组件没有实现 JmxEnabled 接口。
     *
     * @param obj                     The object the register
     * @param objectNameKeyProperties The key properties component of the
     *                                object name to use to register the
     *                                object
     * @return The name used to register the object
     */
    protected final ObjectName register(Object obj,
                                        String objectNameKeyProperties) {
        /* 参数说明
        Object obj：需要注册的对象。
        String objectNameKeyProperties：用于构造对象名称的关键属性。这些属性将被用来在 MBean 服务器上唯一标识该对象。
        * */

        /* 1. 构造对象名称 */
        // Construct an object name with the right domain
        // 使用当前组件的域名（通过 getDomain() 获取）作为基础，然后添加传入的 objectNameKeyProperties，构造出一个完整的对象名称。
        StringBuilder name = new StringBuilder(getDomain());
        name.append(':');
        name.append(objectNameKeyProperties);

        ObjectName on = null;
        /* 2. 注册对象到 MBean 服务器 */
        // 创建一个 ObjectName 实例，它代表了注册到 MBean 服务器的唯一标识符。调用
        // Registry.getRegistry(null, null).registerComponent(obj, on, null) 将对象 obj 与其 ObjectName 注册到
        // MBean 服务器上。这允许该对象通过 JMX 进行管理。
        try {
            on = new ObjectName(name.toString());
            Registry.getRegistry(null, null).registerComponent(obj, on, null);
        } catch (Exception e) {
            log.warn(sm.getString("lifecycleMBeanBase.registerFail", obj, name), e);
        }
        /* 3. 返回创建的 ObjectName 实例，这是对象在 MBean 服务器上的注册名称 */
        return on;
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
     * <p>
     * 如果子类需要进行附加的初始化工作，应该重写 initInternal 方法。
     * 重要的是，在子类的重写方法中，必须首先调用父类的 initInternal 方法（即 super.initInternal()），
     * 这是为了保证父类的初始化逻辑得到正确执行，从而确保整个对象的初始化过程是完整和正确的。
     * <p>
     * 这段代码定义了一个 initInternal 方法，通常用于组件或服务的初始化过程。它的主要作用是在初始化时注册该组件
     * 或服务到一个管理系统（如 JMX - Java Management Extensions）。
     * <p>
     * 总体而言，这个 initInternal 方法的主要职责是在组件的初始化阶段确保其被注册到相应的管理系统中，如 JMX。
     * 这样做允许该组件之后可以被管理系统监控和管理，这在许多企业级应用和服务中是非常重要的功能。
     */
    @Override
    protected void initInternal() throws LifecycleException {
        /* 1. 检查是否已注册 */
        // 首先检查 oname（表示此组件或服务的唯一标识符）是否为 null。如果 oname 不为 null，
        // 这意味着该组件已经通过 preRegister() 方法在之前被注册过，因此不需要再次注册。
        // If oname is not null then registration has already happened via
        // preRegister().
        if (oname == null) {
            /* 2. 执行注册 */
            // 如果 oname 是 null，则说明该组件尚未注册。这时，调用 register 方法来进行注册。
            // register 方法的第一个参数是 this，表示当前对象（即该组件或服务本身），第二个参数是 getObjectNameKeyProperties()
            // 的返回值，这通常是一个字符串，用于构建该组件的唯一标识符
            /* 3. 保存注册结果 */
            // register 方法的返回值被赋给 oname，这样 oname 将包含此组件在管理系统中的唯一标识符。
            oname = register(this, getObjectNameKeyProperties());
        }
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
     *
     * @param objectNameKeyProperties The key properties component of the
     *                                object name to use to unregister the
     *                                object
     */
    protected final void unregister(String objectNameKeyProperties) {
        // Construct an object name with the right domain
        StringBuilder name = new StringBuilder(getDomain());
        name.append(':');
        name.append(objectNameKeyProperties);
        Registry.getRegistry(null, null).unregisterComponent(name.toString());
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
