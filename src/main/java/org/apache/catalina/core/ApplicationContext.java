package org.apache.catalina.core;

import jakarta.servlet.*;
import jakarta.servlet.descriptor.JspConfigDescriptor;
import org.apache.catalina.Engine;
import org.apache.catalina.LifecycleState;
import org.apache.catalina.Wrapper;
import org.apache.tomcat.util.res.StringManager;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

/**
 * @author zhanyang
 */
public class ApplicationContext implements ServletContext {
    private Map<String, Object> attributes;
    private Map<String, String> parameters;
    private static final StringManager sm = StringManager.getManager(ApplicationContext.class);

    private final StandardContext context;

//    public ApplicationContext() {
//        this.attributes = new HashMap<>();
//        this.parameters = new HashMap<>();
//    }

    public ApplicationContext(StandardContext context) {
        super();
        this.context = context;
//        this.service = ((Engine) context.getParent().getParent()).getService();
//        this.sessionCookieConfig = new ApplicationSessionCookieConfig(context);
//
//        // Populate session tracking modes
//        populateSessionTrackingModes();
        throw new UnsupportedOperationException();
    }

    // 实现 ServletContext 必要的方法
    @Override
    public Object getAttribute(String name) {
//        return attributes.get(name);
        throw new UnsupportedOperationException();
    }

    @Override
    public Enumeration<String> getAttributeNames() {
//        if (this.attributes != null) {
//            return Collections.enumeration(this.attributes.keySet());
//        } else {
//            // Return an empty enumeration to avoid null pointer exception
//            return Collections.enumeration(Collections.emptySet());
//        }
        throw new UnsupportedOperationException();
    }

    @Override
    public void setAttribute(String name, Object object) {
//        attributes.put(name, object);
        throw new UnsupportedOperationException();
    }

    @Override
    public void removeAttribute(String s) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getServletContextName() {
        throw new UnsupportedOperationException();
    }

    @Override
    public ServletRegistration.Dynamic addServlet(String s, String s1) {
//        return null;
        throw new UnsupportedOperationException();
    }

    @Override
    public ServletRegistration.Dynamic addServlet(String s, Class<? extends Servlet> aClass) {
//        return null;
        throw new UnsupportedOperationException();
    }

    @Override
    public ServletRegistration.Dynamic addServlet(String servletName, Servlet servlet) {
        // todo 这里需要自己实现，并且调用了 ApplicationServletRegistration
//        ApplicationServletRegistration dynamic = new ApplicationServletRegistration();
//        return dynamic;
        return addServlet(servletName, null, servlet, null);
    }

    // todo 源码是这样实现的（上面三个 addServlet 都调用了这个方法）
    private ServletRegistration.Dynamic addServlet(String servletName, String servletClass, Servlet servlet,
                                                   Map<String,String> initParams) throws IllegalStateException {

        if (servletName == null || servletName.equals("")) {
            throw new IllegalArgumentException(sm.getString("applicationContext.invalidServletName", servletName));
        }

        // TODO Spec breaking enhancement to ignore this restriction
        checkState("applicationContext.addServlet.ise");

        Wrapper wrapper = (Wrapper) context.findChild(servletName);

        // Assume a 'complete' ServletRegistration is one that has a class and
        // a name
//        if (wrapper == null) {
//            wrapper = context.createWrapper();
//            wrapper.setName(servletName);
//            context.addChild(wrapper);
//        } else {
//            if (wrapper.getName() != null && wrapper.getServletClass() != null) {
//                if (wrapper.isOverridable()) {
//                    wrapper.setOverridable(false);
//                } else {
//                    return null;
//                }
//            }
        throw new UnsupportedOperationException();
        }

    private void checkState(String messageKey) {
//        if (!context.getState().equals(LifecycleState.STARTING_PREP)) {
//            throw new IllegalStateException(sm.getString(messageKey, getContextPath()));
//        }
    }


    @Override
    public ServletRegistration.Dynamic addJspFile(String s, String s1) {
//        return null;
        throw new UnsupportedOperationException();
    }

    @Override
    public <T extends Servlet> T createServlet(Class<T> aClass) throws ServletException {
//        return null;
        throw new UnsupportedOperationException();
    }

    @Override
    public ServletRegistration getServletRegistration(String s) {
//        return null;
        throw new UnsupportedOperationException();
    }

    @Override
    public Map<String, ? extends ServletRegistration> getServletRegistrations() {
//        return null;
        throw new UnsupportedOperationException();
    }

    @Override
    public FilterRegistration.Dynamic addFilter(String s, String s1) {
//        return null;
        throw new UnsupportedOperationException();
    }

    // todo 需要自己实现
    @Override
    public FilterRegistration.Dynamic addFilter(String s, Filter filter) {
        ApplicationFilterRegistration dynamic = new ApplicationFilterRegistration();
        return dynamic;
    }

    @Override
    public FilterRegistration.Dynamic addFilter(String s, Class<? extends Filter> aClass) {
//        return null;
        throw new UnsupportedOperationException();
    }

    @Override
    public <T extends Filter> T createFilter(Class<T> aClass) throws ServletException {
//        return null;
        throw new UnsupportedOperationException();
    }

    @Override
    public FilterRegistration getFilterRegistration(String s) {
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
    public void setSessionTrackingModes(Set<SessionTrackingMode> set) {
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
    public void addListener(String s) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T extends EventListener> void addListener(T t) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addListener(Class<? extends EventListener> aClass) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T extends EventListener> T createListener(Class<T> aClass) throws ServletException {
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
    public void declareRoles(String... strings) {
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
    public void setSessionTimeout(int i) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getRequestCharacterEncoding() {
//        return null;
        throw new UnsupportedOperationException();
    }

    @Override
    public void setRequestCharacterEncoding(String s) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getResponseCharacterEncoding() {
//        return null;
        throw new UnsupportedOperationException();
    }

    @Override
    public void setResponseCharacterEncoding(String s) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getContextPath() {
//        return null;
        throw new UnsupportedOperationException();
    }

    @Override
    public ServletContext getContext(String s) {
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
    public String getMimeType(String s) {
//        return null;
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<String> getResourcePaths(String s) {
//        return null;
        throw new UnsupportedOperationException();
    }

    @Override
    public URL getResource(String s) throws MalformedURLException {
//        return null;
        throw new UnsupportedOperationException();
    }

    @Override
    public InputStream getResourceAsStream(String s) {
//        return null;
        throw new UnsupportedOperationException();
    }

    // todo 可能出现问题的地方
    @Override
    public RequestDispatcher getRequestDispatcher(String s) {
        throw new UnsupportedOperationException();
    }

    @Override
    public RequestDispatcher getNamedDispatcher(String s) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void log(String s) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void log(String s, Throwable throwable) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getRealPath(String s) {
//        return null;
        throw new UnsupportedOperationException();
    }

    @Override
    public String getServerInfo() {
//        return null;
        throw new UnsupportedOperationException();
    }


    // 正确覆盖 ServletContext 接口中的 setInitParameter 方法
    @Override
    public boolean setInitParameter(String name, String value) {
//        if (parameters.containsKey(name)) {
//            return false; // 参数已存在，返回false
//        } else {
//            parameters.put(name, value);
//            return true; // 参数成功设置，返回true
//        }
        throw new UnsupportedOperationException();
    }


    // 用于内部设置初始化参数的方法
    public void addInitParameter(String name, String value) {
//        parameters.put(name, value);
        throw new UnsupportedOperationException();
    }

    // ServletContext 接口的实现
    @Override
    public String getInitParameter(String name) {
//        return parameters.get(name);
        throw new UnsupportedOperationException();
    }

    @Override
    public Enumeration<String> getInitParameterNames() {
//        return Collections.enumeration(parameters.keySet());
        throw new UnsupportedOperationException();
    }
}
