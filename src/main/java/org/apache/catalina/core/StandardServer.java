package org.apache.catalina.core;

import org.apache.catalina.Server;
import org.apache.catalina.Service;
import org.apache.catalina.deploy.NamingResourcesImpl;
import org.apache.catalina.startup.Catalina;
import org.apache.catalina.util.LifecycleMBeanBase;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.naming.Context;
import java.io.File;
import java.util.concurrent.ScheduledExecutorService;

public final class StandardServer extends LifecycleMBeanBase implements Server {

    private int port = 8005;


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
        throw new UnsupportedOperationException();
    }

    @Override
    public File getCatalinaHome() {
//        return null;
        throw new UnsupportedOperationException();
    }

    @Override
    public void setCatalinaHome(File catalinaHome) {
        throw new UnsupportedOperationException();
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

    @Override
    public void addService(Service service) {
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
//        return new Service[0];
        throw new UnsupportedOperationException();
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
