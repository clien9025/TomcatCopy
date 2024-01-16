/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.tomcat.util.modeler;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.management.ManagementFactory;
import java.net.URL;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.management.DynamicMBean;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanInfo;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanRegistration;
import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.modeler.modules.ModelerSource;
import org.apache.tomcat.util.res.StringManager;

/**
 * Registry for modeler MBeans.
 * <p>
 * This is the main entry point into modeler. It provides methods to create and
 * manipulate model mbeans and simplify their use.
 * <p>
 * This class is itself an mbean.
 *
 * @author Craig R. McClanahan
 * @author Costin Manolache
 */
public class Registry implements RegistryMBean, MBeanRegistration {
    /**
     * The Log instance to which we will write our log messages.
     */
    private static final Log log = LogFactory.getLog(Registry.class);
    private static final StringManager sm = StringManager.getManager(Registry.class);

    // Support for the factory methods

    /**
     * The registry instance created by our factory method the first time it is
     * called.
     */
    private static Registry registry = null;

    // Per registry fields

    /**
     * The <code>MBeanServer</code> instance that we will use to register
     * management beans.
     */
    private volatile MBeanServer server = null;
    private final Object serverLock = new Object();

    /**
     * The set of ManagedBean instances for the beans this registry knows about,
     * keyed by name.
     */
    private Map<String, ManagedBean> descriptors = new HashMap<>();

    /**
     * List of managed beans, keyed by class name
     */
    private Map<String, ManagedBean> descriptorsByClass = new HashMap<>();

    // map to avoid duplicated searching or loading descriptors
    private Map<String, URL> searchedPaths = new HashMap<>();

    private Object guard;

    // Id - small ints to use array access. No reset on stop()
    // Used for notifications
    private final Hashtable<String, Hashtable<String, Integer>> idDomains = new Hashtable<>();
    private final Hashtable<String, int[]> ids = new Hashtable<>();


    // -------------------- Static methods --------------------
    // Factories

    /**
     * Factory method to create (if necessary) and return our
     * <code>Registry</code> instance.
     * <p>
     * 这是一个静态同步方法，用于创建（如果尚未存在）并返回 Registry 的实例
     *
     * @param key   Unused
     * @param guard Prevent access to the registry by untrusted components
     * @return the registry
     * @since 1.1
     */
    public static synchronized Registry getRegistry(Object key, Object guard) {
        // 如果 Registry 实例尚未创建，它会创建一个新的 Registry 实例。
        // 此外，还有一个安全检查，以确保只有被授权的组件能够访问 Registry。
        if (registry == null) {
            registry = new Registry();
            registry.guard = guard;
        }
        /*这段代码检查了 Registry 实例的 guard 对象：
        如果 guard 不为 null（意味着有一种安全机制被设置），并且传入的 guard 参数与 Registry 实例的 guard 不匹配，那么方法会返回 null。
        这样的检查确保了只有提供正确“钥匙”（即 guard 对象）的调用者才能访问 Registry 实例
        三种情况：
                1. 本身设置了钥匙，传入的钥匙和本身的钥匙不匹配 ----> 进入内部代码（不安全就不给你 registry 注册对象）
                2. 本身设置了钥匙，传入的钥匙和本身的钥匙匹配   ----> 不进入内部代码（安全给你 registry 注册对象）
                3. 本身没设置钥匙，传入的钥匙为空（传入也没钥匙）----> 大家都没 守卫 “guard”（半斤八两，直接给你 registry 注册对象）
        */
        if (registry.guard != null && registry.guard != guard) {
            return null;
        }
        return registry;
    }


    // 这是个 synchronized 方法
    public static synchronized void disableRegistry() {
        // 注册表为 null 时，创建一个无描述的注册表
        if (registry == null) {
            registry = new NoDescriptorRegistry();
        } else if (!(registry instanceof NoDescriptorRegistry)) {// 如果 registry 不属于 NoDescriptorRegistry 类的话
            log.warn(sm.getString("registry.noDisable"));// 警告 log
        }
//        throw new UnsupportedOperationException();
    }


    // -------------------- Generic methods --------------------

    /**
     * Lifecycle method - clean up the registry metadata. Called from
     * resetMetadata().
     *
     * @since 1.1
     */
    @Override
    public void stop() {
//        descriptorsByClass = new HashMap<>();
//        descriptors = new HashMap<>();
//        searchedPaths = new HashMap<>();
        throw new UnsupportedOperationException();
    }


    /**
     * Register a bean by creating a modeler mbean and adding it to the
     * MBeanServer.
     * <p>
     * If metadata is not loaded, we'll look up and read a file named
     * "mbeans-descriptors.ser" or "mbeans-descriptors.xml" in the same package
     * or parent.
     * <p>
     * If the bean is an instance of DynamicMBean. it's metadata will be
     * converted to a model mbean and we'll wrap it - so modeler services will
     * be supported
     * <p>
     * If the metadata is still not found, introspection will be used to extract
     * it automatically.
     * <p>
     * If an mbean is already registered under this name, it'll be first
     * unregistered.
     * <p>
     * If the component implements MBeanRegistration, the methods will be
     * called. If the method has a method "setRegistry" that takes a
     * RegistryMBean as parameter, it'll be called with the current registry.
     *
     * @param bean  Object to be registered
     * @param oname Name used for registration
     * @param type  The type of the mbean, as declared in mbeans-descriptors. If
     *              null, the name of the class will be used. This can be used as
     *              a hint or by subclasses.
     * @throws Exception if a registration error occurred
     * @since 1.1
     */
    @Override
    public void registerComponent(Object bean, String oname, String type) throws Exception {
//        registerComponent(bean, new ObjectName(oname), type);
        throw new UnsupportedOperationException();
    }

    /**
     * Register a component
     * <p>
     * 这段代码定义了一个名为 registerComponent 的方法，用于在 Java Management Extensions (JMX)
     * 系统中注册一个组件（称为 "bean"）。
     * <p>
     * 总体来说，registerComponent 方法的作用是将 Java 对象（bean）作为 MBean 注册到 JMX 服务器。
     * 这允许该对象通过 JMX 进行管理和监控，是在创建可监控和可管理的 Java 应用程序中的一个重要步骤，尤其是在企业级应用或大型系统中
     *
     * @param bean  The bean
     * @param oname The object name
     * @param type  The registry type
     * @throws Exception Error registering component
     */
    public void registerComponent(Object bean, ObjectName oname, String type) throws Exception {
        /* 1. 记录日志 */
        // 如果启用了调试模式，将会记录一个日志信息，指出正在注册的组件的名称（oname）。
        if (log.isDebugEnabled()) {
            log.debug("Managed= " + oname);
        }
        /* 2. 检查传入的 bean 是否为空 */
        // 如果传入的 bean 是 null，则记录一个错误日志，并终止方法执行。
        if (bean == null) {
            log.error(sm.getString("registry.nullBean", oname));
            return;
        }
        /* 3. 注册逻辑 */
        try {
            // 如果 type 参数为 null，则使用 bean 的类名作为其类型
            if (type == null) {
                type = bean.getClass().getName();
            }
            // 调用 findManagedBean 方法来查找或创建一个 ManagedBean，这是一个对 bean 进行封装和管理的对象。
            ManagedBean managed = findManagedBean(null, bean.getClass(), type);
            // 使用 ManagedBean 的 createMBean 方法创建一个 DynamicMBean 实例，这是一个可以在 JMX 中注册的动态 MBean。
            // The real mbean is created and registered
            DynamicMBean mbean = managed.createMBean(bean);
            // 检查通过 oname 指定的 MBean 是否已在 MBean 服务器上注册。如果已注册，则先注销该 MBean
            if (getMBeanServer().isRegistered(oname)) {
                if (log.isDebugEnabled()) {
                    log.debug("Unregistering existing component " + oname);
                }
                getMBeanServer().unregisterMBean(oname);
            }
            // 注册新的 DynamicMBean 实例到 MBean 服务器
            getMBeanServer().registerMBean(mbean, oname);
        } catch (Exception ex) {
            log.error(sm.getString("registry.registerError", oname), ex);
            throw ex;
        }
    }


    /**
     * Unregister a component. This is just a helper that avoids exceptions by
     * checking if the mbean is already registered
     * <p>
     * 这个方法用于从 MBean 服务器注销指定的 MBean。MBeans 是一种 JMX 技术中用于管理资源的组件。
     *
     * @param oname The bean name
     */
    public void unregisterComponent(ObjectName oname) {
        try {
            if (oname != null && getMBeanServer().isRegistered(oname)) {
                getMBeanServer().unregisterMBean(oname);
            }
        } catch (Throwable t) {
            log.error(sm.getString("registry.unregisterError"), t);
        }
    }

    // -------------------- Metadata --------------------
    // methods from 1.0

    /**
     * Add a new bean metadata to the set of beans known to this registry. This
     * is used by internal components.
     *
     * @param bean The managed bean to be added
     * @since 1.0
     */
    // todo finished
    public void addManagedBean(ManagedBean bean) {
        // XXX Use group + name
        // private Map<String, ManagedBean> descriptors = new HashMap<>();
        // (此注册表所了解的 Bean 的 ManagedBean 实例集是按 name 键入。)
        descriptors.put(bean.getName(), bean);
        if (bean.getType() != null) {
            // private Map<String, ManagedBean> descriptorsByClass = new HashMap<>();(托管 bean 列表，以类名作为键)
            descriptorsByClass.put(bean.getType(), bean);
        }
    }


    // -------------------- Helpers --------------------

    /**
     * Factory method to create (if necessary) and return our
     * <code>MBeanServer</code> instance.
     * <p>
     * 这个方法用于创建（如果尚未存在）并返回一个 MBeanServer 实例。MBeanServer 是用于注册和管理 MBeans 的服务器。
     *
     * @return the MBean server
     */
    public MBeanServer getMBeanServer() {
        if (server == null) {
            synchronized (serverLock) {
                if (server == null) {
                    long t1 = System.currentTimeMillis();
                    if (MBeanServerFactory.findMBeanServer(null).size() > 0) {
                        // 如果 server 实例尚未初始化，方法会尝试先从已存在的 MBean 服务器中查找并使用它。
                        server = MBeanServerFactory.findMBeanServer(null).get(0);
                        if (log.isDebugEnabled()) {
                            log.debug("Using existing MBeanServer " + (System.currentTimeMillis() - t1));
                        }
                    } else {
                        // 如果没有找到，则创建一个新的 MBean 服务器。这个方法使用了
                        // 双重检查锁定模式（double-checked locking）来确保 server 实例的线程安全创建。
                        server = ManagementFactory.getPlatformMBeanServer();
                        if (log.isDebugEnabled()) {
                            log.debug("Created MBeanServer" + (System.currentTimeMillis() - t1));
                        }
                    }
                }
            }
        }
        return server;
    }

    /**
     * Find or load metadata.
     *
     * @param bean      The bean
     * @param beanClass The bean class
     * @param type      The registry type
     * @return the managed bean
     * @throws Exception An error occurred
     */
    public ManagedBean findManagedBean(Object bean, Class<?> beanClass, String type)
            throws Exception {
        /* 1. 参数处理 */
        // 如果 bean 不为空且 beanClass 为空，则使用 bean.getClass() 来获取对象的类。
        if (bean != null && beanClass == null) {
            beanClass = bean.getClass();
        }
        // 如果 type 为空，则将 beanClass 的全名作为 type。
        if (type == null) {
            type = beanClass.getName();
        }
        /* 2. 查找现有的描述符 */
        // 首先尝试查找已经存在的与该 type 相关的 ManagedBean。
        // first look for existing descriptor
        ManagedBean managed = findManagedBean(type);
        /* 3. 在包中搜索描述符 */
        // Search for a descriptor in the same package
        if (managed == null) {
            // check package and parent packages
            if (log.isDebugEnabled()) {
                log.debug("Looking for descriptor ");
            }
            // 如果没有找到现有的 ManagedBean，则调用 findDescriptor 方法在 beanClass 所在的包以及其父包中搜索描述符。
            findDescriptor(beanClass, type);
            // 搜索完成后，再次尝试找到与该 type 相关的 ManagedBean
            managed = findManagedBean(type);
        }
        /* 4. 使用内省作为后备机制 */
        // Still not found - use introspection
        if (managed == null) {
            if (log.isDebugEnabled()) {
                log.debug("Introspecting ");
            }

            // introspection
            // 如果仍然没有找到 ManagedBean，则使用内省（introspection）作为后备方法。这通常涉及到分析类的结构，
            // 如其字段和方法，以动态地生成管理信息。
            load("MbeansDescriptorsIntrospectionSource", beanClass, type);
            // 内省完成后，再次尝试找到与该 type 相关的 ManagedBean
            managed = findManagedBean(type);
            /* 5. 处理找不到的情况 */
            // 如果即使在内省之后仍未找到 ManagedBean，则记录一条警告日志，并返回 null。
            if (managed == null) {
                log.warn(sm.getString("registry.noTypeMetadata", type));
                return null;
            }
            /* 6. 添加新的 ManagedBean */
            // 如果通过内省找到了 ManagedBean，则设置其名称并将其添加到某个管理容器中。
            managed.setName(type);
            addManagedBean(managed);
        }
        /* 7. 返回 ManagedBean */
        // 返回找到或创建的 ManagedBean
        return managed;
    }

    /**
     * Find and return the managed bean definition for the specified bean name,
     * if any; otherwise return <code>null</code>.
     *
     * @param name Name of the managed bean to be returned. Since 1.1, both
     *             short names or the full name of the class can be used.
     * @return the managed bean
     * @since 1.0
     */
    // todo finished
    public ManagedBean findManagedBean(String name) {
        // XXX Group ?? Use Group + Type
        // 从两个地方都找一边
        ManagedBean mb = descriptors.get(name);
        if (mb == null) {
            mb = descriptorsByClass.get(name);
        }
        return mb;
    }

    /**
     * Lookup the component descriptor in the package and in the parent
     * packages.
     */
    private void findDescriptor(Class<?> beanClass, String type) {
        /* 确定类型和类加载器 */
        // 如果没有提供 type，则使用 beanClass 的全名。然后，尝试获取 beanClass 的类加载器，
        // 如果失败，则使用当前线程的上下文类加载器或者当前类的类加载器。
        if (type == null) {
            type = beanClass.getName();
        }
        ClassLoader classLoader = null;
        if (beanClass != null) {
            classLoader = beanClass.getClassLoader();
        }
        if (classLoader == null) {
            classLoader = Thread.currentThread().getContextClassLoader();
        }
        if (classLoader == null) {
            classLoader = this.getClass().getClassLoader();
        }
        /* 遍历包结构 */
        // 从完整类名开始，逐级向上遍历父包（通过删除最后一个点号后的部分），直到不再有点号（.），表示已到达顶级包
        String className = type;
        String pkg = className;
        while (pkg.indexOf('.') > 0) {
            int lastComp = pkg.lastIndexOf('.');
            if (lastComp <= 0) {
                return;
            }
            pkg = pkg.substring(0, lastComp);
            if (searchedPaths.get(pkg) != null) {
                return;
            }
            /* 调用加载描述符的方法 */
            // 对于每个包，如果该包还没有被搜索过（检查 searchedPaths），则调用 loadDescriptors 方法尝试加载该包中的描述符。
            loadDescriptors(pkg, classLoader);
        }
    }

    /**
     * Load descriptors.
     * <p>
     * 这段代码定义了一个名为 load 的方法，其目的是从不同类型的源（如 URL、文件、输入流或类）加载描述符并返回一个 ObjectName 对象的列表。
     * 这些描述符通常与 Java Management Extensions (JMX) 中的 MBean（管理 Bean）相关。
     *
     * @param sourceType The source type
     * @param source     The bean
     * @param param      A type to load
     * @return List of descriptors
     * @throws Exception Error loading descriptors
     */
    public List<ObjectName> load(String sourceType, Object source, String param) throws Exception {
        /* 1. 日志记录 */
        // 如果启用了追踪日志，记录正在加载的源对象。
        if (log.isTraceEnabled()) {
            log.trace("load " + source);
        }
        String location = null;
        String type = null;
        Object inputsource = null;
        /* 2. 处理不同类型的源 */
        // 根据 source 对象的类型执行不同的操作
        // URL: 如果 source 是 URL 类型，解析 URL 并打开一个输入流。
        // File: 如果 source 是 File 类型，获取文件的绝对路径并打开一个文件输入流。
        // InputStream: 如果 source 是 InputStream 类型，直接使用该流。
        // Class<?>: 如果 `source` 是 `Class<?>` 类型，获取类的全名。
        if (source instanceof URL) {
            URL url = (URL) source;
            location = url.toString();
            type = param;
            inputsource = url.openStream();
            // 在处理 URL 类型的源时，如果 sourceType 未指定且源位置以 .xml 结尾，
            // 则将 sourceType 设置为 "MbeansDescriptorsDigesterSource"。
            if (sourceType == null && location.endsWith(".xml")) {
                sourceType = "MbeansDescriptorsDigesterSource";
            }
        } else if (source instanceof File) {
            location = ((File) source).getAbsolutePath();
            inputsource = new FileInputStream((File) source);
            type = param;
            // 在处理 File 类型的源时，如果 sourceType 未指定且源位置以 .xml 结尾，
            // 则将 sourceType 设置为 "MbeansDescriptorsDigesterSource"。
            if (sourceType == null && location.endsWith(".xml")) {
                sourceType = "MbeansDescriptorsDigesterSource";
            }
        } else if (source instanceof InputStream) {
            type = param;
            inputsource = source;
        } else if (source instanceof Class<?>) {
            location = ((Class<?>) source).getName();
            type = param;
            inputsource = source;
            // 如果 sourceType 未指定，则设置为 "MbeansDescriptorsIntrospectionSource"
            if (sourceType == null) {
                sourceType = "MbeansDescriptorsIntrospectionSource";
            }
        } else {
            throw new IllegalArgumentException(sm.getString("registry.invalidSource"));
        }
        /* 3. 默认源类型 */
        // 如果在上述步骤中 sourceType 仍未确定，则默认设置为 "MbeansDescriptorsDigesterSource"。
        if (sourceType == null) {
            sourceType = "MbeansDescriptorsDigesterSource";
        }
        /* 4. 获取模型源 */
        // 调用 getModelerSource 方法根据 sourceType 获取相应的 ModelerSource 实例。
        ModelerSource ds = getModelerSource(sourceType);
        /* 5. 加载和解析描述符 */
        // 使用 ModelerSource 实例的 loadDescriptors 方法加载和解析描述符，传入当前对象（this）、type 和处理后的输入源 inputsource。
        List<ObjectName> mbeans = ds.loadDescriptors(this, type, inputsource);

        return mbeans;
    }

    /**
     * Lookup the component descriptor in the package and in the parent
     * packages.
     * <p>
     * 在包和父包中查找组件描述符
     *
     * @param packageName The package name
     * @param classLoader The class loader
     */
    public void loadDescriptors(String packageName, ClassLoader classLoader) {
        /* 资源路径转换和日志记录 */
        // 将包名转换为资源路径（用斜线替换点号），并记录正在查找的描述符。
        String res = packageName.replace('.', '/');

        if (log.isTraceEnabled()) {
            log.trace("Finding descriptor " + res);
        }
        /* 检查是否已搜索 */
        // 如果已经搜索过该包，则直接返回
        if (searchedPaths.get(packageName) != null) {
            return;
        }
        /* 查找描述符文件 */
        // 构造描述符文件的路径，并使用类加载器查找该路径的资源（即描述符文件）。
        String descriptors = res + "/mbeans-descriptors.xml";
        URL dURL = classLoader.getResource(descriptors);
        /* 处理找到的描述符文件 */
        if (dURL == null) {
            return;
        }

        log.debug("Found " + dURL);
        // 如果找到描述符文件，则记录其 URL，将其添加到 searchedPaths 中，然后调用 load 方法加载并解析描述符文件。
        searchedPaths.put(packageName, dURL);
        try {
            load("MbeansDescriptorsDigesterSource", dURL, null);// todo 这里没被调用到
        } catch (Exception ex) {
            log.error(sm.getString("registry.loadError", dURL));
        }
    }

    /**
     * 根据指定的类型名称动态创建特定的 模型源(ModelerSource) 实例。
     *
     * @param type
     * @return
     * @throws Exception
     */
    private ModelerSource getModelerSource(String type) throws Exception {
        if (type == null) {
            type = "MbeansDescriptorsDigesterSource";
        }
        // 完整类名的构建
        if (!type.contains(".")) {
            type = "org.apache.tomcat.util.modeler.modules." + type;// 如果 type 中不包含点号（.），表示它不是一个完整的类名。此时，将其前缀设置为 "org.apache.tomcat.util.modeler.modules."，以构造一个完整的类名。这是基于假设 type 是一个简短的类名，且属于 Apache Tomcat 工具包中的模型模块
        }
        // 这里的 type 的值是:org.apache.tomcat.util.modeler.modules.MbeansDescriptorsIntrospectionSource
        Class<?> c = Class.forName(type);
        ModelerSource ds = (ModelerSource) c.getConstructor().newInstance();
        return ds;
    }


    // -------------------- Registration --------------------

    @Override
    public ObjectName preRegister(MBeanServer server, ObjectName name) throws Exception {
        synchronized (serverLock) {
            this.server = server;
        }
        return name;
    }


    @Override
    public void postRegister(Boolean registrationDone) {
    }


    @Override
    public void preDeregister() throws Exception {
    }


    @Override
    public void postDeregister() {
    }

}

