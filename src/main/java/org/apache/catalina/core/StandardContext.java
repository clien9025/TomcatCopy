package org.apache.catalina.core;

import jakarta.servlet.*;
import jakarta.servlet.descriptor.JspConfigDescriptor;
import org.apache.catalina.*;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.deploy.NamingResourcesImpl;
import org.apache.catalina.util.CharsetMapper;
import org.apache.catalina.util.URLEncoder;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.InstanceManager;
import org.apache.tomcat.JarScanner;
import org.apache.tomcat.util.ExceptionUtils;
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
     *
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

}
