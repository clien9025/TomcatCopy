package org.apache.catalina.core;

import jakarta.servlet.*;
import jakarta.servlet.descriptor.JspConfigDescriptor;
import org.apache.catalina.*;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.deploy.NamingResourcesImpl;
import org.apache.catalina.util.CharsetMapper;
import org.apache.catalina.util.ErrorPageSupport;
import org.apache.catalina.util.URLEncoder;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.InstanceManager;
import org.apache.tomcat.JarScanner;
import org.apache.tomcat.util.ExceptionUtils;
import org.apache.tomcat.util.descriptor.XmlIdentifiers;
import org.apache.tomcat.util.descriptor.web.*;
import org.apache.tomcat.util.http.CookieProcessor;
import org.apache.tomcat.util.scan.StandardJarScanner;

import javax.management.*;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class StandardContext extends ContainerBase implements Context, NotificationEmitter {

    private static final Log log = LogFactory.getLog(StandardContext.class);

    private Boolean failCtxIfServletStartFails;


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

    /**
     * The document root for this web application.
     */
    private String docBase = null;

    /**
     * The Locale to character set mapper for this application.
     */
    private CharsetMapper charsetMapper = null;

    /**
     * The Java class name of the CharsetMapper class to be created.
     */
    private String charsetMapperClass = "org.apache.catalina.util.CharsetMapper";

    private boolean createUploadTargets = false;

    /**
     * The Jar scanner to use to search for Jars that might contain configuration information such as TLDs or
     * web-fragment.xml files.
     */
    private JarScanner jarScanner = null;


    /**
     * The Loader implementation with which this Container is associated.
     */
    private Loader loader = null;
    private final ReadWriteLock loaderLock = new ReentrantReadWriteLock();


    /**
     * The ordered set of ServletContainerInitializers for this web application.
     */
    private Map<ServletContainerInitializer, Set<Class<?>>> initializers = new LinkedHashMap<>();

    private final ErrorPageSupport errorPageSupport = new ErrorPageSupport();

    /**
     * The public identifier of the DTD for the web application deployment descriptor version we are currently parsing.
     * This is used to support relaxed validation rules when processing version 2.2 web.xml files.
     * <p>
     * 此变量用于存储当前正在解析的 Web 应用部署描述符的公共标识符（DTD的公共标识符）。
     */
    private String publicId = null;


    // ----------------------------------------------------- Context Properties

    @Override
    public void setCreateUploadTargets(boolean createUploadTargets) {
        this.createUploadTargets = createUploadTargets;
    }


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
        /* 1. 检查和修改传入的路径 */
        // 这个条件检查是否path是null或者等于"/"。如果是，将invalid设置为true（表示路径无效），
        // 并将类的path成员设置为空字符串""。这个条件处理了空路径或仅根路径的情况。
        if (path == null || path.equals("/")) {
            invalid = true;
            this.path = "";
            // 这个条件检查是否path是空字符串或者以"/"开头。如果满足，直接将传入的path赋值给类的path成员。
            // 这里，空字符串被视为有效路径，而以"/"开头的路径也被视为有效。
        } else if (path.isEmpty() || path.startsWith("/")) {
            this.path = path;
        } else {
            // 这个块是当path既不是null、"/"、空字符串，也不是以"/"开头时的情况。
            // 在这种情况下，将invalid设置为true（因为路径被认为是无效的或不标准的），并在路径前加上"/"，使其成为一个以"/"开头的路径。
            invalid = true;
            this.path = "/" + path;
        }
        // 如果路径以"/"结尾，则移除结尾的"/"，因为通常URL路径不以斜杠结束
        if (this.path.endsWith("/")) {
            invalid = true;
            this.path = this.path.substring(0, this.path.length() - 1);
        }
        /* 2. 记录无效路径的警告 */
        // 如果路径被修改（认为是无效的），记录一个警告日志
        if (invalid) {
            log.warn(sm.getString("standardContext.pathInvalid", path, this.path));
        }
        /* 3. 使用URLEncoder进行URL编码 */
        // 使用URLEncoder.DEFAULT实例对修改后的路径进行URL编码。这意味着将路径中的非安全字符转换为百分号编码（例如空格变为%20）
        encodedPath = URLEncoder.DEFAULT.encode(this.path, StandardCharsets.UTF_8);
        /* 4. 设置对象名称 */
        // 如果对象的名称还未设置，使用处理后的路径作为对象的名称
        if (getName() == null) {
//            setName(this.path);
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public void setDocBase(String docBase) {
        this.docBase = docBase;
    }

    /**
     * @return the Locale to character set mapper for this Context.
     * <p>
     * 其主要功能是返回与 当前上下文（Context）相关联的 CharsetMapper 实例。
     */
    public CharsetMapper getCharsetMapper() {

        // Create a mapper the first time it is requested（第一次请求时创建映射器）
        // private CharsetMapper charsetMapper = null;
        /* 1. 检查是否已经存在一个charsetMapper实例 */
        // 如果this.charsetMapper为null，说明还没有创建CharsetMapper实例。
        if (this.charsetMapper == null) {
            try {
                /* 2. 创建CharsetMapper实例 */
                // 如果没有现有的实例，代码会尝试通过反射来创建一个新的CharsetMapper对象。
                // 动态加载对应的类,使用反射调用该类的无参构造函数来创建一个新实例
                Class<?> clazz = Class.forName(charsetMapperClass);
                this.charsetMapper = (CharsetMapper) clazz.getConstructor().newInstance();
            } catch (Throwable t) {
                /* 3. 异常处理 */
                // 使用自己实现的异常工具
                ExceptionUtils.handleThrowable(t);
                // 如果在创建过程中出现任何错误（如类未找到、构造函数无法访问、实例化时出错等），捕获这些异常并处理。
                this.charsetMapper = new CharsetMapper();
            }
        }

        return this.charsetMapper;

    }


    @Override
    public JarScanner getJarScanner() {
        if (jarScanner == null) {
            jarScanner = new StandardJarScanner();
        }
        return jarScanner;
    }


    @Override
    public void setJarScanner(JarScanner jarScanner) {
        this.jarScanner = jarScanner;
    }


    /**
     * Add an error page for the specified error or Java exception.
     * <p>
     * 它用于向一个容器中添加错误页面定义.
     *
     * @param errorPage The error page definition to be added
     */
    @Override
    public void addErrorPage(ErrorPage errorPage) {
        /* 1. 参数验证 */
        // 首先检查传递给方法的 errorPage 对象是否为 null。如果是 null，则抛出 IllegalArgumentException 异常。
        // Validate the input parameters
        if (errorPage == null) {
            throw new IllegalArgumentException(sm.getString("standardContext.errorPage.required"));
        }
        /* 2. 位置验证和调整 */
        String location = errorPage.getLocation();
        // 接着，检查错误页面的位置（由 errorPage.getLocation() 返回）。
        // 如果这个位置不为空且不是以斜杠（/）开头，则根据是否符合 Servlet 2.2 规范进行不同处理
        if ((location != null) && !location.startsWith("/")) {
            // 如果符合 Servlet 2.2（由 isServlet22() 方法判断），则在位置前添加斜杠，
            // 并可能记录一条调试级别的日志（如果日志记录器的调试模式被激活）。
            if (isServlet22()) {
                if (log.isDebugEnabled()) {
                    log.debug(sm.getString("standardContext.errorPage.warning", location));
                }
                errorPage.setLocation("/" + location);
            } else {
                // 如果不符合 Servlet 2.2，抛出 IllegalArgumentException 异常
                throw new IllegalArgumentException(sm.getString("standardContext.errorPage.error", location));
            }
        }
        /* 3. 添加错误页面 */
        // 将 errorPage 对象添加到 errorPageSupport 的内部结构中（可能是一个列表或映射），用于存储和管理错误页面
        errorPageSupport.add(errorPage);
        /* 4. 触发容器事件 */
        // 方法的最后，触发一个名为 "addErrorPage" 的容器事件，并将 errorPage 作为事件参数传递
        fireContainerEvent("addErrorPage", errorPage);
    }


    /**
     * Are we processing a version 2.2 deployment descriptor?
     * <p>
     * 这个方法用于判断当前处理的是否是一个遵循 Servlet 2.2 规范的部署描述符。方法返回 true 表示当前的描述符符合 Servlet 2.2 规范。
     * <p>
     * 实现方式是通过比较 publicId 变量的值与 XmlIdentifiers.WEB_22_PUBLIC 常量是否相等。
     * 如果相等，说明当前处理的 web.xml 文件是基于 Servlet 2.2 规范的，从而可能需要应用一些特定的解析规则或兼容性处理。
     * <p>
     * 在上下文中，这段代码可能用于一个解析器或处理器，该解析器或处理器负责解析和处理 Web 应用的部署描述符。通过检查
     * 部署描述符的版本（在这个例子中是通过 publicId 来判断），解析器可以确定应该采用哪个版本的规则来处理描述符，从而确保兼容性和正确性。
     *
     * @return <code>true</code> if running a legacy Servlet 2.2 application
     */
    @Override
    public boolean isServlet22() {
        return XmlIdentifiers.WEB_22_PUBLIC.equals(publicId);
    }


    /**
     * Add a ServletContainerInitializer instance to this web application.
     *
     * @param sci     The instance to add
     * @param classes The classes in which the initializer expressed an interest
     */
    @Override
    public void addServletContainerInitializer(ServletContainerInitializer sci, Set<Class<?>> classes) {
        initializers.put(sci, classes);
    }


    /**
     * Add a Locale Encoding Mapping (see Sec 5.4 of Servlet spec 2.4)
     *
     * @param locale   locale to map an encoding for
     * @param encoding encoding to be used for a give locale
     */
    /**
     * Add a Locale Encoding Mapping (see Sec 5.4 of Servlet spec 2.4)
     *
     * @param locale   locale to map an encoding for
     * @param encoding encoding to be used for a give locale
     */
    @Override
    public void addLocaleEncodingMappingParameter(String locale, String encoding) {
        getCharsetMapper().addCharsetMappingFromDeploymentDescriptor(locale, encoding);
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
     * 自己在 org.apache.catalina.Context 里面取消接口里面的实现
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


    @Override
    public Loader getLoader() {
        Lock readLock = loaderLock.readLock();
        readLock.lock();
        try {
            return loader;
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public void setLoader(Loader loader) {

        Lock writeLock = loaderLock.writeLock();
        writeLock.lock();
        Loader oldLoader = null;
        try {
            // Change components if necessary
            oldLoader = this.loader;
            if (oldLoader == loader) {
                return;
            }
            this.loader = loader;

            // Stop the old component if necessary
            if (getState().isAvailable() && (oldLoader != null) && (oldLoader instanceof Lifecycle)) {
//                try {
//                    ((Lifecycle) oldLoader).stop();
//                } catch (LifecycleException e) {
//                    log.error(sm.getString("standardContext.setLoader.stop"), e);
//                }
                throw new UnsupportedOperationException();
            }

            // Start the new component if necessary
            if (loader != null) {
                loader.setContext(this);
            }
            if (getState().isAvailable() && (loader != null) && (loader instanceof Lifecycle)) {
//                try {
//                    ((Lifecycle) loader).start();
//                } catch (LifecycleException e) {
//                    log.error(sm.getString("standardContext.setLoader.start"), e);
//                }
                throw new UnsupportedOperationException();
            }
        } finally {
            writeLock.unlock();
        }

        // Report this property change to interested listeners
        support.firePropertyChange("loader", oldLoader, loader);
    }

    // ------------------------------------------------------ Public Properties

    public Boolean getFailCtxIfServletStartFails() {
        return failCtxIfServletStartFails;
    }

    public void setFailCtxIfServletStartFails(Boolean failCtxIfServletStartFails) {
        Boolean oldFailCtxIfServletStartFails = this.failCtxIfServletStartFails;
        this.failCtxIfServletStartFails = failCtxIfServletStartFails;
        support.firePropertyChange("failCtxIfServletStartFails", oldFailCtxIfServletStartFails,
                failCtxIfServletStartFails);
    }

}
