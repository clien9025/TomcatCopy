package org.apache.catalina.core;

import org.apache.catalina.LifecycleException;
import org.apache.catalina.Server;
import org.apache.catalina.Service;
import org.apache.catalina.deploy.NamingResourcesImpl;
import org.apache.catalina.startup.Catalina;
import org.apache.catalina.util.LifecycleMBeanBase;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.naming.Context;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.util.concurrent.ScheduledExecutorService;

public final class StandardServer extends LifecycleMBeanBase implements Server {

    private int port = 8005;

    /**
     * The set of Services associated with this Server.
     */
    private Service[] services = new Service[0];
    private final Object servicesLock = new Object();


    /**
     * The property change support for this component.
     * 这行代码创建了一个PropertyChangeSupport实例。这个实例是一个工具，
     * 用来帮助任何对象（通常是Java Bean）管理它的属性变更监听器（Property Change Listeners）。
     * 任何时候该对象的属性被改变，都可以通过这个support对象来通知所有感兴趣的监听器。
     */
    final PropertyChangeSupport support = new PropertyChangeSupport(this);

    /**
     * Server socket that is used to wait for the shutdown command.
     */
    private File catalinaHome = null;
    private File catalinaBase = null;


    /**
     * Obtain the MBean domain for this server. The domain is obtained using the following search order:
     * <ol>
     * <li>Name of first {@link org.apache.catalina.Engine}.</li>
     * <li>Name of first {@link Service}.</li>
     * </ol>
     */
    @Override
    protected String getDomainInternal() {

//        String domain = null;
//
//        Service[] services = findServices();
//        if (services.length > 0) {
//            Service service = services[0];
//            if (service != null) {
//                domain = service.getDomain();
//            }
//        }
//        return domain;
        throw new UnsupportedOperationException();
    }


    @Override
    public NamingResourcesImpl getGlobalNamingResources() {
//        return null;
        throw new UnsupportedOperationException();
    }

    @Override
    public void setGlobalNamingResources(NamingResourcesImpl globalNamingResources) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Context getGlobalNamingContext() {
//        return null;
        throw new UnsupportedOperationException();
    }

    @Override
    public int getPort() {
//        return 0;
        throw new UnsupportedOperationException();
    }

    @Override
    public void setPort(int port) {
        this.port = port;
    }

    @Override
    public int getPortOffset() {
//        return 0;
        throw new UnsupportedOperationException();
    }

    @Override
    public void setPortOffset(int portOffset) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getPortWithOffset() {
//        return 0;
        throw new UnsupportedOperationException();
    }

    @Override
    public String getAddress() {
//        return null;
        throw new UnsupportedOperationException();
    }

    @Override
    public void setAddress(String address) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getShutdown() {
//        return null;
        throw new UnsupportedOperationException();
    }

    @Override
    public void setShutdown(String shutdown) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ClassLoader getParentClassLoader() {
//        return null;
        throw new UnsupportedOperationException();
    }

    @Override
    public void setParentClassLoader(ClassLoader parent) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Catalina getCatalina() {
//        return null;
        throw new UnsupportedOperationException();
    }

    @Override
    public void setCatalina(Catalina catalina) {
        throw new UnsupportedOperationException();
    }

    @Override
    public File getCatalinaBase() {
//        return null;
        throw new UnsupportedOperationException();
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

    @Override
    public int getUtilityThreads() {
//        return 0;
        throw new UnsupportedOperationException();
    }

    @Override
    public void setUtilityThreads(int utilityThreads) {
        throw new UnsupportedOperationException();
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
    public Object getNamingToken() {
//        return null;
        throw new UnsupportedOperationException();
    }

    @Override
    public ScheduledExecutorService getUtilityExecutor() {
//        return null;
        throw new UnsupportedOperationException();
    }
}
