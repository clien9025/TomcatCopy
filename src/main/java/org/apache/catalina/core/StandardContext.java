package org.apache.catalina.core;

import jakarta.servlet.*;
import jakarta.servlet.descriptor.JspConfigDescriptor;
import org.apache.catalina.*;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.deploy.NamingResourcesImpl;
import org.apache.juli.logging.Log;
import org.apache.tomcat.InstanceManager;
import org.apache.tomcat.JarScanner;
import org.apache.tomcat.util.descriptor.web.*;
import org.apache.tomcat.util.http.CookieProcessor;

import javax.management.*;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.net.URL;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class StandardContext extends ContainerBase implements Context, NotificationEmitter {

    protected ApplicationContext context = null;

    private String altDDName = null;


    @Override
    public void removeNotificationListener(NotificationListener listener, NotificationFilter filter, Object handback) throws ListenerNotFoundException {

    }

    @Override
    public void addNotificationListener(NotificationListener listener, NotificationFilter filter, Object handback) throws IllegalArgumentException {

    }

    @Override
    public void removeNotificationListener(NotificationListener listener) throws ListenerNotFoundException {

    }

    @Override
    public MBeanNotificationInfo[] getNotificationInfo() {
        return new MBeanNotificationInfo[0];
    }

    /**
     *  自己在 org.apache.catalina.Context 里面取消接口里面的实现
     * （删除了 ServletContext getServletContext(); 前面的 default）
     */
    @Override
    public ServletContext getServletContext() {
//        /*
//         *  This method is called (multiple times) during context start which is single threaded so there is concurrency
//         *  issue here.
//         */
//        if (context == null) {
//            context = new ApplicationContext(this);
//            if (altDDName != null) {
//                context.setAttribute(Globals.ALT_DD_ATTR, altDDName);
//            }
//        }
//        return context.getFacade();
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean getMapperDirectoryRedirectEnabled() {
        return false;
    }
}
