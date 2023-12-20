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
        /*
         *  This method is called (multiple times) during context start which is single threaded so there is concurrency
         *  issue here.
         * 翻译：
         * 此方法在上下文启动期间被调用(多次)，这是单线程的，因此这里存在并发问题。
         */
        // 判断 ApplicationContext 是否为空
        if (context == null) {
            // 若是为空，则把自己（StandardContext）作为实例传给 ApplicationContext （其实就是调用 ApplicationContext）
            context = new ApplicationContext(this);
            // todo String altDDName = null; 这个属性暂时还没跟到相关的逻辑
            if (altDDName != null) {
                context.setAttribute(Globals.ALT_DD_ATTR, altDDName);
            }
        }
        // ApplicationContext context 实例调用 getFacade() 方法
        return context.getFacade();
    }

    @Override
    public boolean getMapperDirectoryRedirectEnabled() {
        return false;
    }

}
