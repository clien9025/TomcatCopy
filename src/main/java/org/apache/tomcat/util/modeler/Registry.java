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
     *
     * @param bean The bean
     * @param oname The object name
     * @param type The registry type
     * @throws Exception Error registering component
     */
    public void registerComponent(Object bean, ObjectName oname, String type) throws Exception {
        if (log.isDebugEnabled()) {
            log.debug("Managed= " + oname);
        }

        if (bean == null) {
            log.error(sm.getString("registry.nullBean", oname));
            return;
        }

        try {
            if (type == null) {
                type = bean.getClass().getName();
            }

            ManagedBean managed = findManagedBean(null, bean.getClass(), type);

            // The real mbean is created and registered
            DynamicMBean mbean = managed.createMBean(bean);

            if (getMBeanServer().isRegistered(oname)) {
                if (log.isDebugEnabled()) {
                    log.debug("Unregistering existing component " + oname);
                }
                getMBeanServer().unregisterMBean(oname);
            }

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
     * @param bean The bean
     * @param beanClass The bean class
     * @param type The registry type
     * @return the managed bean
     * @throws Exception An error occurred
     */
    public ManagedBean findManagedBean(Object bean, Class<?> beanClass, String type)
            throws Exception {

        if (bean != null && beanClass == null) {
            beanClass = bean.getClass();
        }

        if (type == null) {
            type = beanClass.getName();
        }

        // first look for existing descriptor
        ManagedBean managed = findManagedBean(type);

        // Search for a descriptor in the same package
        if (managed == null) {
            // check package and parent packages
            if (log.isDebugEnabled()) {
                log.debug("Looking for descriptor ");
            }
            findDescriptor(beanClass, type);

            managed = findManagedBean(type);
        }

        // Still not found - use introspection
        if (managed == null) {
            if (log.isDebugEnabled()) {
                log.debug("Introspecting ");
            }

            // introspection
            load("MbeansDescriptorsIntrospectionSource", beanClass, type);

            managed = findManagedBean(type);
            if (managed == null) {
                log.warn(sm.getString("registry.noTypeMetadata", type));
                return null;
            }
            managed.setName(type);
            addManagedBean(managed);
        }
        return managed;
    }

    /**
     * Find and return the managed bean definition for the specified bean name,
     * if any; otherwise return <code>null</code>.
     *
     * @param name Name of the managed bean to be returned. Since 1.1, both
     *            short names or the full name of the class can be used.
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
            loadDescriptors(pkg, classLoader);
        }
    }

    /**
     * Load descriptors.
     *
     * @param sourceType The source type
     * @param source The bean
     * @param param A type to load
     * @return List of descriptors
     * @throws Exception Error loading descriptors
     */
    public List<ObjectName> load(String sourceType, Object source, String param) throws Exception {
        if (log.isTraceEnabled()) {
            log.trace("load " + source);
        }
        String location = null;
        String type = null;
        Object inputsource = null;

        if (source instanceof URL) {
            URL url = (URL) source;
            location = url.toString();
            type = param;
            inputsource = url.openStream();
            if (sourceType == null && location.endsWith(".xml")) {
                sourceType = "MbeansDescriptorsDigesterSource";
            }
        } else if (source instanceof File) {
            location = ((File) source).getAbsolutePath();
            inputsource = new FileInputStream((File) source);
            type = param;
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
            if (sourceType == null) {
                sourceType = "MbeansDescriptorsIntrospectionSource";
            }
        } else {
            throw new IllegalArgumentException(sm.getString("registry.invalidSource"));
        }

        if (sourceType == null) {
            sourceType = "MbeansDescriptorsDigesterSource";
        }
        ModelerSource ds = getModelerSource(sourceType);
        List<ObjectName> mbeans = ds.loadDescriptors(this, type, inputsource);

        return mbeans;
    }

    /**
     * Lookup the component descriptor in the package and in the parent
     * packages.
     *
     * @param packageName The package name
     * @param classLoader The class loader
     */
    public void loadDescriptors(String packageName, ClassLoader classLoader) {
        String res = packageName.replace('.', '/');

        if (log.isTraceEnabled()) {
            log.trace("Finding descriptor " + res);
        }

        if (searchedPaths.get(packageName) != null) {
            return;
        }

        String descriptors = res + "/mbeans-descriptors.xml";
        URL dURL = classLoader.getResource(descriptors);

        if (dURL == null) {
            return;
        }

//        log.debug("Found " + dURL);
//        searchedPaths.put(packageName, dURL);
//        try {
//            load("MbeansDescriptorsDigesterSource", dURL, null);
//        } catch (Exception ex) {
//            log.error(sm.getString("registry.loadError", dURL));
//        }
        throw new UnsupportedOperationException();
    }


    private ModelerSource getModelerSource(String type) throws Exception {
        if (type == null) {
            type = "MbeansDescriptorsDigesterSource";
        }
        if (!type.contains(".")) {// 完整类名的构建
            type = "org.apache.tomcat.util.modeler.modules." + type;// 如果 type 中不包含点号（.），表示它不是一个完整的类名。此时，将其前缀设置为 "org.apache.tomcat.util.modeler.modules."，以构造一个完整的类名。这是基于假设 type 是一个简短的类名，且属于 Apache Tomcat 工具包中的模型模块
        }

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

