package org.apache.catalina.core;

import jakarta.servlet.*;
import jakarta.servlet.descriptor.JspConfigDescriptor;
import org.apache.catalina.security.SecurityUtil;
import org.apache.tomcat.util.ExceptionUtils;

import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zhanyang
 */
public class ApplicationContextFacade implements ServletContext {

    private final ApplicationContext context;

    /**
     * Cache Class object used for reflection.
     */
    private final Map<String,Class<?>[]> classCache;


    /**
     * Cache method object.
     */
    private final Map<String,Method> objectCache;



    /**
     * Cache method object.
     */
//    private final Map<String, Method> objectCache;

//    private void initClassCache() {
//        Class<?>[] clazz = new Class[] { String.class };
//        classCache.put("getContext", clazz);
//        classCache.put("getMimeType", clazz);
//        classCache.put("getResourcePaths", clazz);
//        classCache.put("getResource", clazz);
//        classCache.put("getResourceAsStream", clazz);
//        classCache.put("getRequestDispatcher", clazz);
//        classCache.put("getNamedDispatcher", clazz);
//        classCache.put("getServlet", clazz);
//        classCache.put("setInitParameter", new Class[] { String.class, String.class });
//        classCache.put("createServlet", new Class[] { Class.class });
//        classCache.put("addServlet", new Class[] { String.class, String.class });
//        classCache.put("createFilter", new Class[] { Class.class });
//        classCache.put("addFilter", new Class[] { String.class, String.class });
//        classCache.put("createListener", new Class[] { Class.class });
//        classCache.put("addListener", clazz);
//        classCache.put("getFilterRegistration", clazz);
//        classCache.put("getServletRegistration", clazz);
//        classCache.put("getInitParameter", clazz);
//        classCache.put("setAttribute", new Class[] { String.class, Object.class });
//        classCache.put("removeAttribute", clazz);
//        classCache.put("getRealPath", clazz);
//        classCache.put("getAttribute", clazz);
//        classCache.put("log", clazz);
//        classCache.put("setSessionTrackingModes", new Class[] { Set.class });
//        classCache.put("addJspFile", new Class[] { String.class, String.class });
//        classCache.put("declareRoles", new Class[] { String[].class });
//        classCache.put("setSessionTimeout", new Class[] { int.class });
//        classCache.put("setRequestCharacterEncoding", new Class[] { String.class });
//        classCache.put("setResponseCharacterEncoding", new Class[] { String.class });
//    }

    public ApplicationContextFacade(ApplicationContext context) {
        // todo 这个 super(); 方法进不去？
        super();
        this.context = context;

        classCache = new HashMap<>();
        objectCache = new ConcurrentHashMap<>();
//        initClassCache();
    }

    @Override
    public String getContextPath() {
        throw new UnsupportedOperationException();
    }

    @Override
    public ServletContext getContext(String uripath) {
//        return null;
        throw new UnsupportedOperationException();
    }

    @Override
    public int getMajorVersion() {
//        return 0;
        throw new UnsupportedOperationException();
    }

    @Override
    public int getMinorVersion() {
//        return 0;
        throw new UnsupportedOperationException();
    }

    @Override
    public int getEffectiveMajorVersion() {
//        return 0;
        throw new UnsupportedOperationException();
    }

    @Override
    public int getEffectiveMinorVersion() {
//        return 0;
        throw new UnsupportedOperationException();
    }

    @Override
    public String getMimeType(String file) {
//        return null;
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<String> getResourcePaths(String path) {
//        return null;
        throw new UnsupportedOperationException();
    }

    @Override
    public URL getResource(String path) throws MalformedURLException {
//        return null;
        throw new UnsupportedOperationException();
    }

    @Override
    public InputStream getResourceAsStream(String path) {
//        return null;
        throw new UnsupportedOperationException();
    }

    @Override
    public RequestDispatcher getRequestDispatcher(String path) {
//        return null;
        throw new UnsupportedOperationException();
    }

    @Override
    public RequestDispatcher getNamedDispatcher(String name) {
//        return null;
        throw new UnsupportedOperationException();
    }

    @Override
    public void log(String msg) {
        if (SecurityUtil.isPackageProtectionEnabled()) {
            doPrivileged("log", new Object[] { msg });
        } else {
            context.log(msg);
        }
        throw new UnsupportedOperationException();

    }

    @Override
    public void log(String message, Throwable throwable) {
        throw new UnsupportedOperationException();
    }

    /**
     * Use reflection to invoke the requested method. Cache the method object to speed up the process
     *
     * @param methodName The method to call.
     * @param params     The arguments passed to the called method.
     */
    private Object doPrivileged(final String methodName, final Object[] params) {
//        try {
//            return invokeMethod(context, methodName, params);
//        } catch (Throwable t) {
//            ExceptionUtils.handleThrowable(t);
//            throw new RuntimeException(t.getMessage(), t);
//        }
        throw new UnsupportedOperationException();
    }

    /**
     * Use reflection to invoke the requested method. Cache the method object to speed up the process
     *
     * @param appContext The ApplicationContext object on which the method will be invoked
     * @param methodName The method to call.
     * @param params     The arguments passed to the called method.
     */
    private Object invokeMethod(ApplicationContext appContext, final String methodName, Object[] params)
            throws Throwable {

//        try {
//            Method method = objectCache.get(methodName);
//            if (method == null) {
//                method = appContext.getClass().getMethod(methodName, classCache.get(methodName));
//                objectCache.put(methodName, method);
//            }
//
//            return executeMethod(method, appContext, params);
//        } catch (Exception ex) {
//            handleException(ex);
//            return null;
//        } finally {
//            params = null;
//        }
        throw new UnsupportedOperationException();
    }


    /**
     * Executes the method of the specified <code>ApplicationContext</code>
     *
     * @param method  The method object to be invoked.
     * @param context The ApplicationContext object on which the method will be invoked
     * @param params  The arguments passed to the called method.
     */
    private Object executeMethod(final Method method, final ApplicationContext context, final Object[] params)
            throws PrivilegedActionException, IllegalAccessException, InvocationTargetException {

//        if (SecurityUtil.isPackageProtectionEnabled()) {
//            return AccessController.doPrivileged(new PrivilegedExecuteMethod(method, context, params));
//        } else {
//            return method.invoke(context, params);
//        }
        throw new UnsupportedOperationException();
    }

    /**
     * Throw the real exception.
     *
     * @param ex The current exception
     */
    private void handleException(Exception ex) throws Throwable {

//        Throwable realException;
//
//        if (ex instanceof PrivilegedActionException) {
//            ex = ((PrivilegedActionException) ex).getException();
//        }
//
//        if (ex instanceof InvocationTargetException) {
//            realException = ex.getCause();
//            if (realException == null) {
//                realException = ex;
//            }
//        } else {
//            realException = ex;
//        }
//
//        throw realException;
        throw new UnsupportedOperationException();
    }

    @Override
    public String getRealPath(String path) {
//        return null;
        throw new UnsupportedOperationException();
    }

    @Override
    public String getServerInfo() {
//        return null;
        throw new UnsupportedOperationException();
    }

    @Override
    public String getInitParameter(String name) {
//        return null;
        throw new UnsupportedOperationException();
    }

    @Override
    public Enumeration<String> getInitParameterNames() {
//        return null;
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean setInitParameter(String name, String value) {
//        return false;
        throw new UnsupportedOperationException();
    }

    @Override
    public Object getAttribute(String name) {
//        return null;
        throw new UnsupportedOperationException();
    }

    @Override
    public Enumeration<String> getAttributeNames() {
//        return null;
        throw new UnsupportedOperationException();
    }

    @Override
    public void setAttribute(String name, Object object) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void removeAttribute(String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getServletContextName() {
//        return null;
        throw new UnsupportedOperationException();
    }

    @Override
    public ServletRegistration.Dynamic addServlet(String servletName, String className) {
//        return null;
        throw new UnsupportedOperationException();
    }

    @Override
    public ServletRegistration.Dynamic addServlet(String servletName, Servlet servlet) {
//        return null;
        throw new UnsupportedOperationException();
    }

    @Override
    public ServletRegistration.Dynamic addServlet(String servletName, Class<? extends Servlet> servletClass) {
//        return null;
        throw new UnsupportedOperationException();
    }

    @Override
    public ServletRegistration.Dynamic addJspFile(String servletName, String jspFile) {
//        return null;
        throw new UnsupportedOperationException();
    }

    @Override
    public <T extends Servlet> T createServlet(Class<T> clazz) throws ServletException {
//        return null;
        throw new UnsupportedOperationException();
    }

    @Override
    public ServletRegistration getServletRegistration(String servletName) {
//        return null;
        throw new UnsupportedOperationException();
    }

    @Override
    public Map<String, ? extends ServletRegistration> getServletRegistrations() {
//        return null;
        throw new UnsupportedOperationException();
    }

    @Override
    public FilterRegistration.Dynamic addFilter(String filterName, String className) {
//        return null;
        throw new UnsupportedOperationException();
    }

    @Override
    public FilterRegistration.Dynamic addFilter(String filterName, Filter filter) {
//        return null;
        throw new UnsupportedOperationException();
    }

    @Override
    public FilterRegistration.Dynamic addFilter(String filterName, Class<? extends Filter> filterClass) {
//        return null;
        throw new UnsupportedOperationException();
    }

    @Override
    public <T extends Filter> T createFilter(Class<T> clazz) throws ServletException {
//        return null;
        throw new UnsupportedOperationException();
    }

    @Override
    public FilterRegistration getFilterRegistration(String filterName) {
//        return null;
        throw new UnsupportedOperationException();
    }

    @Override
    public Map<String, ? extends FilterRegistration> getFilterRegistrations() {
//        return null;
        throw new UnsupportedOperationException();
    }

    @Override
    public SessionCookieConfig getSessionCookieConfig() {
//        return null;
        throw new UnsupportedOperationException();
    }

    @Override
    public void setSessionTrackingModes(Set<SessionTrackingMode> sessionTrackingModes) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<SessionTrackingMode> getDefaultSessionTrackingModes() {
//        return null;
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<SessionTrackingMode> getEffectiveSessionTrackingModes() {
//        return null;
        throw new UnsupportedOperationException();
    }

    @Override
    public void addListener(String className) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T extends EventListener> void addListener(T t) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addListener(Class<? extends EventListener> listenerClass) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T extends EventListener> T createListener(Class<T> clazz) throws ServletException {
//        return null;
        throw new UnsupportedOperationException();
    }

    @Override
    public JspConfigDescriptor getJspConfigDescriptor() {
//        return null;
        throw new UnsupportedOperationException();
    }

    @Override
    public ClassLoader getClassLoader() {
//        return null;
        throw new UnsupportedOperationException();
    }

    @Override
    public void declareRoles(String... roleNames) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getVirtualServerName() {
//        return null;
        throw new UnsupportedOperationException();
    }

    @Override
    public int getSessionTimeout() {
//        return 0;
        throw new UnsupportedOperationException();
    }

    @Override
    public void setSessionTimeout(int sessionTimeout) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getRequestCharacterEncoding() {
//        return null;
        throw new UnsupportedOperationException();
    }

    @Override
    public void setRequestCharacterEncoding(String encoding) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getResponseCharacterEncoding() {
//        return null;
        throw new UnsupportedOperationException();
    }

    @Override
    public void setResponseCharacterEncoding(String encoding) {
        throw new UnsupportedOperationException();
    }

    private static class PrivilegedExecuteMethod implements PrivilegedExceptionAction<Object> {

        private final Method method;
        private final ApplicationContext context;
        private final Object[] params;

        PrivilegedExecuteMethod(Method method, ApplicationContext context, Object[] params) {
            this.method = method;
            this.context = context;
            this.params = params;
        }

        @Override
        public Object run() throws Exception {
            return method.invoke(context, params);
        }
    }
}
