package org.apache.catalina.core;

import org.apache.catalina.LifecycleException;
import org.apache.catalina.LifecycleState;
import org.apache.catalina.Server;
import org.apache.catalina.Service;
import org.apache.catalina.deploy.NamingResourcesImpl;
import org.apache.catalina.mbeans.MBeanFactory;
import org.apache.catalina.startup.Catalina;
import org.apache.catalina.util.LifecycleMBeanBase;
import org.apache.catalina.util.ServerInfo;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.ExceptionUtils;
import org.apache.tomcat.util.buf.StringCache;
import org.apache.tomcat.util.modeler.Registry;
import org.apache.tomcat.util.res.StringManager;
import org.apache.tomcat.util.threads.TaskThreadFactory;

import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.naming.Context;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.Random;
import java.util.concurrent.*;

public final class StandardServer extends LifecycleMBeanBase implements Server {

    private static final Log log = LogFactory.getLog(StandardServer.class);
    private static final StringManager sm = StringManager.getManager(StandardServer.class);


    // ------------------------------------------------------------ Constructor

    /**
     * Construct a default instance of this class.
     */
    public StandardServer() {

        super();

        globalNamingResources = new NamingResourcesImpl();
        globalNamingResources.setContainer(this);

        if (isUseNaming()) {
            namingContextListener = new NamingContextListener();
            addLifecycleListener(namingContextListener);
        } else {
            namingContextListener = null;
        }

    }


    // ----------------------------------------------------- Instance Variables


    /**
     * Global naming resources context.
     */
    private javax.naming.Context globalNamingContext = null;


    /**
     * Global naming resources.
     */
    private NamingResourcesImpl globalNamingResources = null;


    /**
     * The naming context listener for this web application.
     */
    private final NamingContextListener namingContextListener;


    /**
     * The port number on which we wait for shutdown commands.
     */
    private int port = 8005;

    private int portOffset = 0;

    /**
     * The address on which we wait for shutdown commands.
     */
    private String address = "localhost";


    /**
     * A random number generator that is <strong>only</strong> used if the shutdown command string is longer than 1024
     * characters.
     */
    private Random random = null;


    /**
     * The set of Services associated with this Server.
     */
    private Service[] services = new Service[0];
    private final Object servicesLock = new Object();


    /**
     * The shutdown command string we are looking for.
     */
    private String shutdown = "SHUTDOWN";

    /**
     * The property change support for this component.
     * 这行代码创建了一个PropertyChangeSupport实例。这个实例是一个工具，
     * 用来帮助任何对象（通常是Java Bean）管理它的属性变更监听器（Property Change Listeners）。
     * 任何时候该对象的属性被改变，都可以通过这个support对象来通知所有感兴趣的监听器。
     */
    final PropertyChangeSupport support = new PropertyChangeSupport(this);

    private volatile boolean stopAwait = false;

    private Catalina catalina = null;

    private ClassLoader parentClassLoader = null;

    /**
     * Thread that currently is inside our await() method.
     */
    private volatile Thread awaitThread = null;

    /**
     * Server socket that is used to wait for the shutdown command.
     */
    private volatile ServerSocket awaitSocket = null;

    private File catalinaHome = null;

    private File catalinaBase = null;

    private final Object namingToken = new Object();

    /**
     * The number of threads available to process utility tasks in this service.
     */
    private int utilityThreads = 2;

    /**
     * The utility threads daemon flag.
     */
    private boolean utilityThreadsAsDaemon = false;

    /**
     * Utility executor with scheduling capabilities.
     */
    private ScheduledThreadPoolExecutor utilityExecutor = null;
    private final Object utilityExecutorLock = new Object();

    /**
     * Utility executor wrapper.
     */
    private ScheduledExecutorService utilityExecutorWrapper = null;


    /**
     * Controller for the periodic lifecycle event.
     */
    private ScheduledFuture<?> periodicLifecycleEventFuture = null;
    private ScheduledFuture<?> monitorFuture;


    /**
     * The lifecycle event period in seconds.
     */
    private int periodicEventDelay = 10;

//----------------------------------------------------------------------------------------------------------


    // ------------------------------------------------------------- Properties

    @Override
    public Object getNamingToken() {
        return namingToken;
    }


    /**
     * Return the global naming resources context.
     */
    @Override
    public javax.naming.Context getGlobalNamingContext() {
        return this.globalNamingContext;
    }


    /**
     * Set the global naming resources context.
     *
     * @param globalNamingContext The new global naming resource context
     */
    public void setGlobalNamingContext(javax.naming.Context globalNamingContext) {
        this.globalNamingContext = globalNamingContext;
    }


    /**
     * Return the global naming resources.
     */
    @Override
    public NamingResourcesImpl getGlobalNamingResources() {
        return this.globalNamingResources;
    }


    /**
     * Set the global naming resources.
     *
     * @param globalNamingResources The new global naming resources
     */
    @Override
    public void setGlobalNamingResources(NamingResourcesImpl globalNamingResources) {

        NamingResourcesImpl oldGlobalNamingResources = this.globalNamingResources;
        this.globalNamingResources = globalNamingResources;
        this.globalNamingResources.setContainer(this);
        support.firePropertyChange("globalNamingResources", oldGlobalNamingResources, this.globalNamingResources);

    }


    /**
     * Report the current Tomcat Server Release number
     *
     * @return Tomcat release identifier
     */
    public String getServerInfo() {
        return ServerInfo.getServerInfo();
    }


    /**
     * Return the current server built timestamp
     *
     * @return server built timestamp.
     */
    public String getServerBuilt() {
        return ServerInfo.getServerBuilt();
    }


    /**
     * Return the current server's version number.
     *
     * @return server's version number.
     */
    public String getServerNumber() {
        return ServerInfo.getServerNumber();
    }


    /**
     * Return the port number we listen to for shutdown commands.
     */
    @Override
    public int getPort() {
        return this.port;
    }


    /**
     * Set the port number we listen to for shutdown commands.
     *
     * @param port The new port number
     */
    @Override
    public void setPort(int port) {
        this.port = port;
    }


    @Override
    public int getPortOffset() {
        return portOffset;
    }


    @Override
    public void setPortOffset(int portOffset) {
        if (portOffset < 0) {
            throw new IllegalArgumentException(
                    sm.getString("standardServer.portOffset.invalid", Integer.valueOf(portOffset)));
        }
        this.portOffset = portOffset;
    }


    @Override
    public int getPortWithOffset() {
        // Non-positive port values have special meanings and the offset should
        // not apply.
        int port = getPort();
        if (port > 0) {
            return port + getPortOffset();
        } else {
            return port;
        }
    }


    /**
     * Return the address on which we listen to for shutdown commands.
     */
    @Override
    public String getAddress() {
        return this.address;
    }


    /**
     * Set the address on which we listen to for shutdown commands.
     *
     * @param address The new address
     */
    @Override
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * Return the shutdown command string we are waiting for.
     */
    @Override
    public String getShutdown() {
        return this.shutdown;
    }


    /**
     * Set the shutdown command we are waiting for.
     *
     * @param shutdown The new shutdown command
     */
    @Override
    public void setShutdown(String shutdown) {
        this.shutdown = shutdown;
    }


    /**
     * Return the outer Catalina startup/shutdown component if present.
     */
    @Override
    public Catalina getCatalina() {
        return catalina;
    }


    /**
     * Set the outer Catalina startup/shutdown component if present.
     */
    @Override
    public void setCatalina(Catalina catalina) {
        this.catalina = catalina;
    }


    @Override
    public int getUtilityThreads() {
        return utilityThreads;
    }


    /**
     * Handles the special values.
     */
    private static int getUtilityThreadsInternal(int utilityThreads) {
//        int result = utilityThreads;
//        if (result <= 0) {
//            result = Runtime.getRuntime().availableProcessors() + result;
//            if (result < 2) {
//                result = 2;
//            }
//        }
//        return result;
        throw new UnsupportedOperationException();
    }


    @Override
    public void setUtilityThreads(int utilityThreads) {
//        // Use local copies to ensure thread safety
//        int oldUtilityThreads = this.utilityThreads;
//        if (getUtilityThreadsInternal(utilityThreads) < getUtilityThreadsInternal(oldUtilityThreads)) {
//            return;
//        }
//        this.utilityThreads = utilityThreads;
//        synchronized (utilityExecutorLock) {
//            if (oldUtilityThreads != utilityThreads && utilityExecutor != null) {
//                reconfigureUtilityExecutor(getUtilityThreadsInternal(utilityThreads));
//            }
//        }
        throw new UnsupportedOperationException();
    }


    /*
     * Callers must be holding utilityExecutorLock
     */
    private void reconfigureUtilityExecutor(int threads) {
//        // The ScheduledThreadPoolExecutor doesn't use MaximumPoolSize, only CorePoolSize is available
//        if (utilityExecutor != null) {
//            utilityExecutor.setCorePoolSize(threads);
//        } else {
//            ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(threads,
//                    new TaskThreadFactory("Catalina-utility-", utilityThreadsAsDaemon, Thread.MIN_PRIORITY));
//            scheduledThreadPoolExecutor.setKeepAliveTime(10, TimeUnit.SECONDS);
//            scheduledThreadPoolExecutor.setRemoveOnCancelPolicy(true);
//            scheduledThreadPoolExecutor.setExecuteExistingDelayedTasksAfterShutdownPolicy(false);
//            utilityExecutor = scheduledThreadPoolExecutor;
//            utilityExecutorWrapper = new org.apache.tomcat.util.threads.ScheduledThreadPoolExecutor(utilityExecutor);
//        }
        throw new UnsupportedOperationException();
    }


    /**
     * Get if the utility threads are daemon threads.
     *
     * @return the threads daemon flag
     */
    public boolean getUtilityThreadsAsDaemon() {
        return utilityThreadsAsDaemon;
    }


    /**
     * Set the utility threads daemon flag. The default value is true.
     *
     * @param utilityThreadsAsDaemon the new thread daemon flag
     */
    public void setUtilityThreadsAsDaemon(boolean utilityThreadsAsDaemon) {
        this.utilityThreadsAsDaemon = utilityThreadsAsDaemon;
    }


    /**
     * @return The period between two lifecycle events, in seconds
     */
    public int getPeriodicEventDelay() {
        return periodicEventDelay;
    }


    /**
     * Set the new period between two lifecycle events in seconds.
     *
     * @param periodicEventDelay The period in seconds, negative or zero will disable events
     */
    public void setPeriodicEventDelay(int periodicEventDelay) {
        this.periodicEventDelay = periodicEventDelay;
    }

    // --------------------------------------------------------- Server Methods


    /**
     * Add a new Service to the set of defined Services.
     *
     * 将新的服务器添加到已经定义的服务器集
     * @param service The Service to be added
     */
    @Override
    public void addService(Service service) {

        service.setServer(this);

        // 新建一个 Object 对象当锁，每一个 StandardServer 对象只有这么一个实例 servicesLock
        // private final Object servicesLock = new Object();
        synchronized (servicesLock) {
            /* 将传入的 service 放入到原来的服务器集合里面 */
            // private Service[] services = new Service[0];
            Service results[] = new Service[services.length + 1];
            System.arraycopy(services, 0, results, 0, services.length);
            results[services.length] = service;
            services = results;

            // 如果 LifecycleState 枚举类产生的实例里面的 available 属性是 true 时
            if (getState().isAvailable()) {
//                try {
//                    service.start();
//                } catch (LifecycleException e) {
//                    // Ignore
//                }
                throw new UnsupportedOperationException();
            }

            // Report this property change to interested listeners
            /* 这行代码使用PropertyChangeSupport对象触发一个属性变更事件。
            它通知所有注册的监听器，名为"service"的属性已经从null变成了service对象的新值。
            这是在某个服务被添加到系统时常见的操作，可能表示服务的状态或配置发生了变化。 */
            support.firePropertyChange("service", null, service);
        }
    }



    public void stopAwait() {
//        stopAwait = true;
//        Thread t = awaitThread;
//        if (t != null) {
//            ServerSocket s = awaitSocket;
//            if (s != null) {
//                awaitSocket = null;
//                try {
//                    s.close();
//                } catch (IOException e) {
//                    // Ignored
//                }
//            }
//            t.interrupt();
//            try {
//                t.join(1000);
//            } catch (InterruptedException e) {
//                // Ignored
//            }
//        }
        throw new UnsupportedOperationException();
    }


    @Override
    public void await() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Service findService(String name) {
//        return null;
        throw new UnsupportedOperationException();
    }

    @Override
    public Service[] findServices() {
        return services;
    }

    @Override
    public void removeService(Service service) {
        throw new UnsupportedOperationException();
    }

    @Override
    public File getCatalinaBase() {
        if (catalinaBase != null) {
            return catalinaBase;
        }

        catalinaBase = getCatalinaHome();
        return catalinaBase;
    }


    @Override
    public void setCatalinaBase(File catalinaBase) {
        this.catalinaBase = catalinaBase;
    }


    @Override
    public File getCatalinaHome() {
        return catalinaHome;
    }


    @Override
    public void setCatalinaHome(File catalinaHome) {
        this.catalinaHome = catalinaHome;
    }


    // --------------------------------------------------------- Public Methods

    /**
     * Add a property change listener to this component.
     *
     * @param listener The listener to add
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {

//        support.addPropertyChangeListener(listener);
        throw new UnsupportedOperationException();
    }


    /**
     * Remove a property change listener from this component.
     *
     * @param listener The listener to remove
     */
    public void removePropertyChangeListener(PropertyChangeListener listener) {

//        support.removePropertyChangeListener(listener);
        throw new UnsupportedOperationException();
    }


    /**
     * Return a String representation of this component.
     */
    @Override
    public String toString() {
        return "StandardServer[" + getPort() + ']';
    }


    /**
     * Write the configuration information for this entire <code>Server</code> out to the server.xml configuration file.
     *
     * @exception InstanceNotFoundException                   if the managed resource object cannot be found
     * @exception MBeanException                              if the initializer of the object throws an exception, or
     *                                                            persistence is not supported
     * @exception javax.management.RuntimeOperationsException if an exception is reported by the persistence mechanism
     */
    public synchronized void storeConfig() throws InstanceNotFoundException, MBeanException {
//        try {
//            // Note: Hard-coded domain used since this object is per Server/JVM
//            ObjectName sname = new ObjectName("Catalina:type=StoreConfig");
//            MBeanServer server = Registry.getRegistry(null, null).getMBeanServer();
//            if (server.isRegistered(sname)) {
//                server.invoke(sname, "storeConfig", null, null);
//            } else {
//                log.error(sm.getString("standardServer.storeConfig.notAvailable", sname));
//            }
//        } catch (Throwable t) {
//            ExceptionUtils.handleThrowable(t);
//            log.error(sm.getString("standardServer.storeConfig.error"), t);
//        }
        throw new UnsupportedOperationException();
    }


    /**
     * Write the configuration information for <code>Context</code> out to the specified configuration file.
     *
     * @param context the context which should save its configuration
     *
     * @exception InstanceNotFoundException                   if the managed resource object cannot be found
     * @exception MBeanException                              if the initializer of the object throws an exception or
     *                                                            persistence is not supported
     * @exception javax.management.RuntimeOperationsException if an exception is reported by the persistence mechanism
     */
    public synchronized void storeContext(org.apache.catalina.Context context) throws InstanceNotFoundException, MBeanException {
//        try {
//            // Note: Hard-coded domain used since this object is per Server/JVM
//            ObjectName sname = new ObjectName("Catalina:type=StoreConfig");
//            MBeanServer server = Registry.getRegistry(null, null).getMBeanServer();
//            if (server.isRegistered(sname)) {
//                server.invoke(sname, "store", new Object[] { context }, new String[] { "java.lang.String" });
//            } else {
//                log.error(sm.getString("standardServer.storeConfig.notAvailable", sname));
//            }
//        } catch (Throwable t) {
//            ExceptionUtils.handleThrowable(t);
//            log.error(sm.getString("standardServer.storeConfig.contextError", context.getName()), t);
//        }
        throw new UnsupportedOperationException();
    }


    /**
     * @return <code>true</code> if naming should be used.
     */
    private boolean isUseNaming() {
        boolean useNaming = true;
        // Reading the "catalina.useNaming" environment variable
        String useNamingProperty = System.getProperty("catalina.useNaming");
        if (useNamingProperty != null && useNamingProperty.equals("false")) {
            useNaming = false;
        }
        return useNaming;
    }


    /**
     * Start nested components ({@link Service}s) and implement the requirements of
     * {@link org.apache.catalina.util.LifecycleBase#startInternal()}.
     *
     * @exception LifecycleException if this component detects a fatal error that prevents this component from being
     *                                   used
     */
    @Override
    protected void startInternal() throws LifecycleException {

        fireLifecycleEvent(CONFIGURE_START_EVENT, null);
        setState(LifecycleState.STARTING);

        // Initialize utility executor
        synchronized (utilityExecutorLock) {
            reconfigureUtilityExecutor(getUtilityThreadsInternal(utilityThreads));
            register(utilityExecutor, "type=UtilityExecutor");
        }

        globalNamingResources.start();

        // Start our defined Services
        synchronized (servicesLock) {
            for (Service service : services) {
                service.start();
            }
        }

        if (periodicEventDelay > 0) {
            monitorFuture = getUtilityExecutor().scheduleWithFixedDelay(this::startPeriodicLifecycleEvent, 0, 60,
                    TimeUnit.SECONDS);
        }
    }


    private void startPeriodicLifecycleEvent() {
//        if (periodicLifecycleEventFuture == null || periodicLifecycleEventFuture.isDone()) {
//            if (periodicLifecycleEventFuture != null && periodicLifecycleEventFuture.isDone()) {
//                // There was an error executing the scheduled task, get it and log it
//                try {
//                    periodicLifecycleEventFuture.get();
//                } catch (InterruptedException | ExecutionException e) {
//                    log.error(sm.getString("standardServer.periodicEventError"), e);
//                }
//            }
//            periodicLifecycleEventFuture =
//                    getUtilityExecutor().scheduleAtFixedRate(() -> fireLifecycleEvent(PERIODIC_EVENT, null),
//                            periodicEventDelay, periodicEventDelay, TimeUnit.SECONDS);
//        }
        throw new UnsupportedOperationException();
    }


    /**
     * Stop nested components ({@link Service}s) and implement the requirements of
     * {@link org.apache.catalina.util.LifecycleBase#stopInternal()}.
     *
     * @exception LifecycleException if this component detects a fatal error that needs to be reported
     */
    @Override
    protected void stopInternal() throws LifecycleException {

//        setState(LifecycleState.STOPPING);
//
//        if (monitorFuture != null) {
//            monitorFuture.cancel(true);
//            monitorFuture = null;
//        }
//        if (periodicLifecycleEventFuture != null) {
//            periodicLifecycleEventFuture.cancel(false);
//            periodicLifecycleEventFuture = null;
//        }
//
//        fireLifecycleEvent(CONFIGURE_STOP_EVENT, null);
//
//        // Stop our defined Services
//        for (Service service : services) {
//            service.stop();
//        }
//
//        synchronized (utilityExecutorLock) {
//            if (utilityExecutor != null) {
//                utilityExecutor.shutdownNow();
//                unregister("type=UtilityExecutor");
//                utilityExecutor = null;
//            }
//        }
//
//        globalNamingResources.stop();
//
//        stopAwait();
        throw new UnsupportedOperationException();
    }

    /**
     * Invoke a pre-startup initialization. This is used to allow connectors to bind to restricted ports under Unix
     * operating environments.
     */
    @Override
    protected void initInternal() throws LifecycleException {

        super.initInternal();

        // Register global String cache
        // Note although the cache is global, if there are multiple Servers
        // present in the JVM (may happen when embedding) then the same cache
        // will be registered under multiple names
        onameStringCache = register(new StringCache(), "type=StringCache");

        // Register the MBeanFactory
        MBeanFactory factory = new MBeanFactory();
        factory.setContainer(this);
        onameMBeanFactory = register(factory, "type=MBeanFactory");

        // Register the naming resources
        globalNamingResources.init();

        // Initialize our defined Services
        for (Service service : services) {
            service.init();
        }
    }

    @Override
    protected void destroyInternal() throws LifecycleException {
//        // Destroy our defined Services
//        for (Service service : services) {
//            service.destroy();
//        }
//
//        globalNamingResources.destroy();
//
//        unregister(onameMBeanFactory);
//
//        unregister(onameStringCache);
//
//        super.destroyInternal();
        throw new UnsupportedOperationException();
    }

    /**
     * Return the parent class loader for this component.
     */
    @Override
    public ClassLoader getParentClassLoader() {
//        if (parentClassLoader != null) {
//            return parentClassLoader;
//        }
//        if (catalina != null) {
//            return catalina.getParentClassLoader();
//        }
//        return ClassLoader.getSystemClassLoader();
        throw new UnsupportedOperationException();
    }

    /**
     * Set the parent class loader for this server.
     *
     * @param parent The new parent class loader
     */
    @Override
    public void setParentClassLoader(ClassLoader parent) {
        ClassLoader oldParentClassLoader = this.parentClassLoader;
        this.parentClassLoader = parent;
        support.firePropertyChange("parentClassLoader", oldParentClassLoader, this.parentClassLoader);
    }


    private ObjectName onameStringCache;
    private ObjectName onameMBeanFactory;

    /**
     * Obtain the MBean domain for this server. The domain is obtained using the following search order:
     * <ol>
     * <li>Name of first {@link org.apache.catalina.Engine}.</li>
     * <li>Name of first {@link Service}.</li>
     * </ol>
     */
    @Override
    protected String getDomainInternal() {

        String domain = null;

        Service[] services = findServices();
        if (services.length > 0) {
            Service service = services[0];
            if (service != null) {
                domain = service.getDomain();
            }
        }
        return domain;
    }


    @Override
    protected String getObjectNameKeyProperties() {
        return "type=Server";
    }

    @Override
    public ScheduledExecutorService getUtilityExecutor() {
        return utilityExecutorWrapper;
    }
}
