package org.apache.catalina.core;

import jakarta.servlet.*;
import jakarta.servlet.descriptor.JspConfigDescriptor;
import org.apache.catalina.*;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.deploy.NamingResourcesImpl;
import org.apache.catalina.util.URLEncoder;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.InstanceManager;
import org.apache.tomcat.JarScanner;
import org.apache.tomcat.util.descriptor.web.*;
import org.apache.tomcat.util.http.CookieProcessor;

import javax.management.*;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class StandardContext extends ContainerBase implements Context, NotificationEmitter {

    private static final Log log = LogFactory.getLog(StandardContext.class);



    // ----------------------------------------------------- Instance Variables

    protected ApplicationContext context = null;

    private String altDDName = null;

    /**
     * The display name of this web application.
     */
    private String displayName = null;

    /**
     * Unencoded path for this web application.
     */
    private String path = null;

    /**
     * Encoded path.
     */
    private String encodedPath = null;


    // ----------------------------------------------------- Context Properties


    /**
     * Set the display name of this web application.
     *
     * @param displayName The new display name
     */
    @Override
    public void setDisplayName(String displayName) {

        String oldDisplayName = this.displayName;
        this.displayName = displayName;
        support.firePropertyChange("displayName", oldDisplayName, this.displayName);
    }

    /**
     * Set the context path for this Context.
     *
     * @param path The new context path
     */
    @Override
    public void setPath(String path) {
        boolean invalid = false;
        if (path == null || path.equals("/")) {
            invalid = true;
            this.path = "";
        } else if (path.isEmpty() || path.startsWith("/")) {
            this.path = path;
        } else {
            invalid = true;
            this.path = "/" + path;
        }
        if (this.path.endsWith("/")) {
            invalid = true;
            this.path = this.path.substring(0, this.path.length() - 1);
        }
        if (invalid) {
            log.warn(sm.getString("standardContext.pathInvalid", path, this.path));
        }
        encodedPath = URLEncoder.DEFAULT.encode(this.path, StandardCharsets.UTF_8);
        if (getName() == null) {
//            setName(this.path);
            throw new UnsupportedOperationException();
        }
    }

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
//                context.setAttribute(Globals.ALT_DD_ATTR, altDDName);
                throw new UnsupportedOperationException();
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
