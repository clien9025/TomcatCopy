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
package org.apache.catalina.core;

import java.io.PrintStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.management.ListenerNotFoundException;
import javax.management.MBeanNotificationInfo;
import javax.management.Notification;
import javax.management.NotificationBroadcasterSupport;
import javax.management.NotificationEmitter;
import javax.management.NotificationFilter;
import javax.management.NotificationListener;
import javax.management.ObjectName;

import jakarta.servlet.MultipartConfigElement;
import jakarta.servlet.Servlet;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.UnavailableException;
import jakarta.servlet.annotation.MultipartConfig;

import org.apache.catalina.Container;
import org.apache.catalina.ContainerServlet;
import org.apache.catalina.Context;
import org.apache.catalina.Globals;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.LifecycleState;
import org.apache.catalina.Wrapper;
import org.apache.catalina.security.SecurityUtil;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.InstanceManager;
import org.apache.tomcat.PeriodicEventListener;
import org.apache.tomcat.util.ExceptionUtils;
import org.apache.tomcat.util.log.SystemLogHandler;
import org.apache.tomcat.util.modeler.Registry;
import org.apache.tomcat.util.modeler.Util;

/**
 * Standard implementation of the <b>Wrapper</b> interface that represents an individual servlet definition. No child
 * Containers are allowed, and the parent Container must be a Context.
 *
 * @author Craig R. McClanahan
 * @author Remy Maucherat
 */
public class StandardWrapper extends ContainerBase implements ServletConfig, Wrapper/*, NotificationEmitter*/ {

    private final Log log = LogFactory.getLog(StandardWrapper.class); // must not be static

    protected static final String[] DEFAULT_SERVLET_METHODS = new String[]{"GET", "HEAD", "POST"};

    // ----------------------------------------------------------- Constructors


    /**
     * Create a new StandardWrapper component with the default basic Valve.
     */
    public StandardWrapper() {

        super();
        swValve = new StandardWrapperValve();
        pipeline.setBasic(swValve);
        broadcaster = new NotificationBroadcasterSupport();

    }


    // ----------------------------------------------------- Instance Variables


    /**
     * The date and time at which this servlet will become available (in milliseconds since the epoch), or zero if the
     * servlet is available. If this value equals Long.MAX_VALUE, the unavailability of this servlet is considered
     * permanent.
     */
    protected long available = 0L;

    /**
     * The broadcaster that sends j2ee notifications.
     */
    protected final NotificationBroadcasterSupport broadcaster;

    /**
     * The count of allocations that are currently active.
     */
    protected final AtomicInteger countAllocated = new AtomicInteger(0);


    /**
     * The facade associated with this wrapper.
     */
    protected final StandardWrapperFacade facade = new StandardWrapperFacade(this);


    /**
     * The (single) possibly uninitialized instance of this servlet.
     */
    protected volatile Servlet instance = null;


    /**
     * Flag that indicates if this instance has been initialized
     */
    protected volatile boolean instanceInitialized = false;


    /**
     * The load-on-startup order value (negative value means load on first call) for this servlet.
     */
    protected int loadOnStartup = -1;


    /**
     * Mappings associated with the wrapper.
     */
    protected final ArrayList<String> mappings = new ArrayList<>();


    /**
     * The initialization parameters for this servlet, keyed by parameter name.
     */
    protected HashMap<String, String> parameters = new HashMap<>();


    /**
     * The security role references for this servlet, keyed by role name used in the servlet. The corresponding value is
     * the role name of the web application itself.
     */
    protected HashMap<String, String> references = new HashMap<>();


    /**
     * The run-as identity for this servlet.
     */
    protected String runAs = null;

    /**
     * The notification sequence number.
     */
    protected long sequenceNumber = 0;

    /**
     * The fully qualified servlet class name for this servlet.
     */
    protected String servletClass = null;


    /**
     * Are we unloading our servlet instance at the moment?
     */
    protected volatile boolean unloading = false;


    /**
     * Wait time for servlet unload in ms.
     */
    protected long unloadDelay = 2000;


    /**
     * True if this StandardWrapper is for the JspServlet
     */
    protected boolean isJspServlet;


    /**
     * The ObjectName of the JSP monitoring mbean
     */
    protected ObjectName jspMonitorON;


    /**
     * Should we swallow System.out
     */
    protected boolean swallowOutput = false;

    // To support jmx attributes
    protected StandardWrapperValve swValve;
    protected long loadTime = 0;
    protected int classLoadTime = 0;

    /**
     * Multipart config
     */
    protected MultipartConfigElement multipartConfigElement = null;

    /**
     * Async support
     */
    protected boolean asyncSupported = false;

    /**
     * Enabled
     */
    protected boolean enabled = true;

    private boolean overridable = false;

    /**
     * Static class array used when the SecurityManager is turned on and <code>Servlet.init</code> is invoked.
     */
    protected static Class<?>[] classType = new Class[]{ServletConfig.class};

    private final ReentrantReadWriteLock parametersLock = new ReentrantReadWriteLock();

    private final ReentrantReadWriteLock mappingsLock = new ReentrantReadWriteLock();

    private final ReentrantReadWriteLock referencesLock = new ReentrantReadWriteLock();


    // ------------------------------------------------------------- Properties

    @Override
    public boolean isOverridable() {
        return overridable;
    }

    @Override
    public void setOverridable(boolean overridable) {
        this.overridable = overridable;
    }

    /**
     * Return the available date/time for this servlet, in milliseconds since the epoch. If this date/time is
     * Long.MAX_VALUE, it is considered to mean that unavailability is permanent and any request for this servlet will
     * return an SC_NOT_FOUND error. If this date/time is in the future, any request for this servlet will return an
     * SC_SERVICE_UNAVAILABLE error. If it is zero, the servlet is currently available.
     */
    @Override
    public long getAvailable() {
        return this.available;
    }


    /**
     * Set the available date/time for this servlet, in milliseconds since the epoch. If this date/time is
     * Long.MAX_VALUE, it is considered to mean that unavailability is permanent and any request for this servlet will
     * return an SC_NOT_FOUND error. If this date/time is in the future, any request for this servlet will return an
     * SC_SERVICE_UNAVAILABLE error.
     * <p>
     * 如果 available 被设置为 Long.MAX_VALUE（long 类型的最大值），这通常被视为 servlet 永久不可用。
     * 在这种情况下，任何对这个 servlet 的请求都会返回一个 SC_NOT_FOUND（资源未找到）错误。
     * 如果 available 是一个未来的时间点，
     * 任何对这个 servlet 的请求将会返回一个 SC_SERVICE_UNAVAILABLE（服务不可用）错误，直到达到那个时间点。
     *
     * @param available The new available date/time
     */
    @Override
    public void setAvailable(long available) {
        long oldAvailable = this.available;
        // 如果传入的 available 时间大于当前系统时间 (System.currentTimeMillis())，这意味着 servlet 在将来的某个时间点才会变得可用。
        // 在这种情况下，this.available 被设置为传入的 available 时间。
        if (available > System.currentTimeMillis()) {
            this.available = available;
        } else {
            // 如果传入的 available 时间小于或等于当前系统时间，或者方法没有接收到一个具体的未来时间，
            // this.available 被设置为 0，意味着 servlet 立即可用。
            this.available = 0L;
        }
        support.firePropertyChange("available", Long.valueOf(oldAvailable), Long.valueOf(this.available));
    }


    /**
     * @return the number of active allocations of this servlet.
     */
    public int getCountAllocated() {
        return this.countAllocated.get();
    }


    /**
     * @return the load-on-startup order value (negative value means load on first call).
     * <p>
     * 启动时加载顺序值（负值表示首次调用时加载）
     * getLoadOnStartup 方法是用来获取servlet的加载顺序值的。在Web应用中，这个值用来确定servlet的启动顺序。
     * 方法中的 loadOnStartup 变量是这个类的一个成员变量，它存储了servlet的加载顺序值。
     * 如果值是一个正数或者 0，那么这个值越小，servlet的加载优先级越高。
     * 如果值是负数，这表示servlet不会在Web应用程序启动时加载，而是在它第一次被请求时才加载。
     * <p>
     * 但是，对于 JspServlet，如果 loadOnStartup 是 -1，则返回 Integer.MAX_VALUE。尽管 Integer.MAX_VALUE 是一个非常大的数，
     * 但在这种特殊情况下，它意味着 JspServlet 应该总是被预加载，因为它在注册JMX时需要使用，
     * 这个特殊的返回值是为了确保JspServlet能在正确的时机被加载。
     */
    @Override
    public int getLoadOnStartup() {
        /*方法中有一个条件判断 if (isJspServlet && loadOnStartup == -1)：
        isJspServlet 是一个布尔类型的成员变量，用于判断当前servlet是否是JspServlet。
        loadOnStartup == -1 检查这个servlet是否被配置为在首次调用时加载（而不是在Web应用启动时加载）
        */

        if (isJspServlet && loadOnStartup == -1) {
            /*如果这个servlet是JspServlet，并且 loadOnStartup 被设置为 -1，那么方法返回 Integer.MAX_VALUE。
            这意味着JspServlet总是应该在启动时加载，因为它在注册JMX（例如，注册JSP监控mbean时）时需要预加载。*/
            /*
             * JspServlet must always be preloaded, because its instance is used during registerJMX (when registering
             * the JSP monitoring mbean)
             */
            return Integer.MAX_VALUE;
        } else {
            return this.loadOnStartup;
        }
    }


    /**
     * Set the load-on-startup order value (negative value means load on first call).
     *
     * @param value New load-on-startup value
     */
    @Override
    public void setLoadOnStartup(int value) {

        int oldLoadOnStartup = this.loadOnStartup;
        this.loadOnStartup = value;
        support.firePropertyChange("loadOnStartup", Integer.valueOf(oldLoadOnStartup),
                Integer.valueOf(this.loadOnStartup));

    }


    /**
     * Set the load-on-startup order value from a (possibly null) string. Per the specification, any missing or
     * non-numeric value is converted to a zero, so that this servlet will still be loaded at startup time, but in an
     * arbitrary order.
     *
     * @param value New load-on-startup value
     */
    public void setLoadOnStartupString(String value) {

        try {
            setLoadOnStartup(Integer.parseInt(value));
        } catch (NumberFormatException e) {
            setLoadOnStartup(0);
        }
    }

    /**
     * @return the load-on-startup value that was parsed
     */
    public String getLoadOnStartupString() {
        return Integer.toString(getLoadOnStartup());
    }


    /**
     * Set the parent Container of this Wrapper, but only if it is a Context.
     *
     * @param container Proposed parent Container
     */
    @Override
    public void setParent(Container container) {

        if ((container != null) && !(container instanceof Context)) {
            throw new IllegalArgumentException(sm.getString("standardWrapper.notContext"));
        }
        if (container instanceof StandardContext) {
            swallowOutput = ((StandardContext) container).getSwallowOutput();
            unloadDelay = ((StandardContext) container).getUnloadDelay();
        }
        super.setParent(container);

    }


    /**
     * @return the run-as identity for this servlet.
     */
    @Override
    public String getRunAs() {
        return this.runAs;
    }


    /**
     * Set the run-as identity for this servlet.
     *
     * @param runAs New run-as identity value
     */
    @Override
    public void setRunAs(String runAs) {

        String oldRunAs = this.runAs;
        this.runAs = runAs;
        support.firePropertyChange("runAs", oldRunAs, this.runAs);

    }


    /**
     * @return the fully qualified servlet class name for this servlet.
     */
    @Override
    public String getServletClass() {
        return this.servletClass;
    }


    /**
     * Set the fully qualified servlet class name for this servlet.
     *
     * @param servletClass Servlet class name
     */
    @Override
    public void setServletClass(String servletClass) {

        String oldServletClass = this.servletClass;
        this.servletClass = servletClass;
        support.firePropertyChange("servletClass", oldServletClass, this.servletClass);
        if (Constants.JSP_SERVLET_CLASS.equals(servletClass)) {
            isJspServlet = true;
        }
    }


    /**
     * Set the name of this servlet. This is an alias for the normal <code>Container.setName()</code> method, and
     * complements the <code>getServletName()</code> method required by the <code>ServletConfig</code> interface.
     *
     * @param name The new name of this servlet
     */
    public void setServletName(String name) {

        setName(name);

    }


    /**
     * @return <code>true</code> if the Servlet has been marked unavailable.
     */
    @Override
    public boolean isUnavailable() {
        // 用来判断servlet是否被启用的
        if (!isEnabled()) {
            // 如果 isEnabled() 返回 false（即 !isEnabled() 为 true），则方法返回 true，表示servlet不可用。
            return true;
        } else if (available == 0L) {
            return false;
        } else if (available <= System.currentTimeMillis()) {
            // 如果是，这意味着servlet的不可用状态已经过期，方法将 available 设置为 0L 并返回 false，表示servlet现在可用。
            available = 0L;
            return false;
        } else {
            return true;
        }

    }


    @Override
    public String[] getServletMethods() throws ServletException {

//        instance = loadServlet();
//
//        Class<? extends Servlet> servletClazz = instance.getClass();
//        if (!jakarta.servlet.http.HttpServlet.class.isAssignableFrom(servletClazz)) {
//            return DEFAULT_SERVLET_METHODS;
//        }
//
//        Set<String> allow = new HashSet<>();
//        allow.add("OPTIONS");
//
//        if (isJspServlet) {
//            allow.add("GET");
//            allow.add("HEAD");
//            allow.add("POST");
//        } else {
//            allow.add("TRACE");
//
//            Method[] methods = getAllDeclaredMethods(servletClazz);
//            for (int i = 0; methods != null && i < methods.length; i++) {
//                Method m = methods[i];
//
//                if (m.getName().equals("doGet")) {
//                    allow.add("GET");
//                    allow.add("HEAD");
//                } else if (m.getName().equals("doPost")) {
//                    allow.add("POST");
//                } else if (m.getName().equals("doPut")) {
//                    allow.add("PUT");
//                } else if (m.getName().equals("doDelete")) {
//                    allow.add("DELETE");
//                }
//            }
//        }
//
//        return allow.toArray(new String[0]);
        throw new UnsupportedOperationException();
    }


    /**
     * @return the associated servlet instance.
     */
    @Override
    public Servlet getServlet() {
        return instance;
    }


    /**
     * Set the associated servlet instance.
     */
    @Override
    public void setServlet(Servlet servlet) {
        instance = servlet;
    }// todo




    // --------------------------------------------------------- Public Methods



    /* ++++++++++++++++++++++++++++++++++++ 自动实现的方法（暂时占用下）+++++++++++++++++++++++++++++++++++++ */

    @Override
    public String getServletName() {
        return null;
    }

    @Override
    public ServletContext getServletContext() {
        return null;
    }

    @Override
    public String getInitParameter(String name) {
        return null;
    }

    @Override
    public Enumeration<String> getInitParameterNames() {
        return null;
    }







}
