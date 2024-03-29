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
package org.apache.catalina.startup;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import jakarta.servlet.Servlet;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;

import org.apache.catalina.Container;
import org.apache.catalina.Context;
import org.apache.catalina.Engine;
import org.apache.catalina.Globals;
import org.apache.catalina.Host;
import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleEvent;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.LifecycleListener;
import org.apache.catalina.Realm;
import org.apache.catalina.Server;
import org.apache.catalina.Service;
import org.apache.catalina.Wrapper;
import org.apache.catalina.authenticator.NonLoginAuthenticator;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.core.*;
import org.apache.catalina.realm.GenericPrincipal;
import org.apache.catalina.realm.RealmBase;
import org.apache.catalina.security.SecurityClassLoad;
import org.apache.catalina.util.ContextName;
import org.apache.catalina.util.IOTools;
import org.apache.tomcat.util.ExceptionUtils;
import org.apache.tomcat.util.descriptor.web.LoginConfig;
import org.apache.tomcat.util.file.ConfigFileLoader;
import org.apache.tomcat.util.file.ConfigurationSource;
import org.apache.tomcat.util.modeler.Registry;
import org.apache.tomcat.util.res.StringManager;

// TODO: lazy init for the temp dir - only when a JSP is compiled or
// get temp dir is called we need to create it. This will avoid the
// need for the baseDir

// TODO: allow contexts without a base dir - i.e.
// only programmatic. This would disable the default servlet.

/**
 * Minimal tomcat starter for embedding/unit tests.
 *
 * <p>
 * Tomcat supports multiple styles of configuration and
 * startup - the most common and stable is server.xml-based,
 * implemented in org.apache.catalina.startup.Bootstrap.
 *
 * <p>
 * This class is for use in apps that embed tomcat.
 *
 * <p>
 * Requirements:
 * <ul>
 *   <li>all tomcat classes and possibly servlets are in the classpath.
 *       (for example all is in one big jar, or in eclipse CP, or in
 *        any other combination)</li>
 *
 *   <li>we need one temporary directory for work files</li>
 *
 *   <li>no config file is required. This class provides methods to
 *       use if you have a webapp with a web.xml file, but it is
 *       optional - you can use your own servlets.</li>
 * </ul>
 *
 * <p>
 * There are a variety of 'add' methods to configure servlets and webapps. These
 * methods, by default, create a simple in-memory security realm and apply it.
 * If you need more complex security processing, you can define a subclass of
 * this class.
 *
 * <p>
 * This class provides a set of convenience methods for configuring web
 * application contexts; all overloads of the method <code>addWebapp()</code>.
 * These methods are equivalent to adding a web application to the Host's
 * appBase (normally the webapps directory). These methods create a Context,
 * configure it with the equivalent of the defaults provided by
 * <code>conf/web.xml</code> (see {@link #initWebappDefaults(String)} for
 * details) and add the Context to a Host. These methods do not use a global
 * default web.xml; rather, they add a {@link LifecycleListener} to configure
 * the defaults. Any WEB-INF/web.xml and META-INF/context.xml packaged with the
 * application will be processed normally. Normal web fragment and
 * {@link jakarta.servlet.ServletContainerInitializer} processing will be applied.
 *
 * <p>
 * In complex cases, you may prefer to use the ordinary Tomcat API to create
 * webapp contexts; for example, you might need to install a custom Loader
 * before the call to {@link Host#addChild(Container)}. To replicate the basic
 * behavior of the <code>addWebapp</code> methods, you may want to call two
 * methods of this class: {@link #noDefaultWebXmlPath()} and
 * {@link #getDefaultWebXmlListener()}.
 *
 * <p>
 * {@link #getDefaultWebXmlListener()} returns a {@link LifecycleListener} that
 * adds the standard DefaultServlet, JSP processing, and welcome files. If you
 * add this listener, you must prevent Tomcat from applying any standard global
 * web.xml with ...
 *
 * <p>
 * {@link #noDefaultWebXmlPath()} returns a dummy pathname to configure to
 * prevent {@link ContextConfig} from trying to apply a global web.xml file.
 *
 * <p>
 * This class provides a main() and few simple CLI arguments,
 * see setters for doc. It can be used for simple tests and
 * demo.
 *
 * @author Costin Manolache
 * @see <a href="https://gitbox.apache.org/repos/asf?p=tomcat.git;a=blob;f=test/org/apache/catalina/startup/TestTomcat.java">TestTomcat</a>
 */
public class Tomcat {

    private static final StringManager sm = StringManager.getManager(Tomcat.class);

    // Some logging implementations use weak references for loggers so there is
    // the possibility that logging configuration could be lost if GC runs just
    // after Loggers are configured but before they are used. The purpose of
    // this Map is to retain strong references to explicitly configured loggers
    // so that configuration is not lost.
    private final Map<String, Logger> pinnedLoggers = new HashMap<>();

    protected Server server;

    protected int port = 8080;
    protected String hostname = "localhost";
    protected String basedir;

    private final Map<String, String> userPass = new HashMap<>();
    private final Map<String, List<String>> userRoles = new HashMap<>();
    private final Map<String, Principal> userPrincipals = new HashMap<>();

    private boolean addDefaultWebXmlToWebapp = true;

    public Tomcat() {
        ExceptionUtils.preload();
    }

    /**
     * Tomcat requires that the base directory is set because the defaults for
     * a number of other locations, such as the work directory, are derived from
     * the base directory. This should be the first method called.
     * <p>
     * If this method is not called then Tomcat will attempt to use these
     * locations in the following order:
     * <ol>
     *  <li>if set, the catalina.base system property</li>
     *  <li>if set, the catalina.home system property</li>
     *  <li>The user.dir system property (the directory where Java was run from)
     *      where a directory named tomcat.$PORT will be created. $PORT is the
     *      value configured via {@link #setPort(int)} which defaults to 8080 if
     *      not set</li>
     * </ol>
     * The user should ensure that the file permissions for the base directory
     * are appropriate.
     * <p>
     * TODO: disable work dir if not needed ( no jsp, etc ).
     *
     * @param basedir The Tomcat base folder on which all others will be derived
     */
    public void setBaseDir(String basedir) {
        this.basedir = basedir;
    }

    /**
     * Set the port for the default connector. The default connector will
     * only be created if getConnector is called.
     *
     * @param port The port number
     */
    public void setPort(int port) {
        this.port = port;
    }

    /**
     * The the hostname of the default host, default is
     * 'localhost'.
     *
     * @param s The default host name
     */
    public void setHostname(String s) {
        hostname = s;
    }


    /**
     * This is equivalent to adding a web application to a Host's appBase
     * (usually Tomcat's webapps directory). By default, the equivalent of the
     * default web.xml will be applied to the web application (see
     * {@link #initWebappDefaults(String)}). This may be prevented by calling
     * {@link #setAddDefaultWebXmlToWebapp(boolean)} with {@code false}. Any
     * <code>WEB-INF/web.xml</code> and <code>META-INF/context.xml</code>
     * packaged with the application will always be processed and normal web
     * fragment and {@link jakarta.servlet.ServletContainerInitializer}
     * processing will always be applied.
     *
     * @param contextPath The context mapping to use, "" for root context.
     * @param docBase     Base directory for the context, for static files. Must
     *                    exist and be an absolute path.
     * @return the deployed context
     */
    public Context addWebapp(String contextPath, String docBase) {
        return addWebapp(getHost(), contextPath, docBase);
    }


    /**
     * Copy the specified WAR file to the Host's appBase and then call
     * {@link #addWebapp(String, String)} with the newly copied WAR. The WAR
     * will <b>NOT</b> be removed from the Host's appBase when the Tomcat
     * instance stops. Note that {@link ExpandWar} provides utility methods that
     * may be used to delete the WAR and/or expanded directory if required.
     *
     * @param contextPath The context mapping to use, "" for root context.
     * @param source      The location from which the WAR should be copied
     * @return The deployed Context
     * @throws IOException If an I/O error occurs while copying the WAR file
     *                     from the specified URL to the appBase
     */
    public Context addWebapp(String contextPath, URL source) throws IOException {

//        ContextName cn = new ContextName(contextPath, null);
//
//        // Make sure a conflicting web application has not already been deployed
//        Host h = getHost();
//        if (h.findChild(cn.getName()) != null) {
//            throw new IllegalArgumentException(sm.getString("tomcat.addWebapp.conflictChild",
//                    source, contextPath, cn.getName()));
//        }
//
//        // Make sure appBase does not contain a conflicting web application
//        File targetWar = new File(h.getAppBaseFile(), cn.getBaseName() + ".war");
//        File targetDir = new File(h.getAppBaseFile(), cn.getBaseName());
//
//        if (targetWar.exists()) {
//            throw new IllegalArgumentException(sm.getString("tomcat.addWebapp.conflictFile",
//                    source, contextPath, targetWar.getAbsolutePath()));
//        }
//        if (targetDir.exists()) {
//            throw new IllegalArgumentException(sm.getString("tomcat.addWebapp.conflictFile",
//                    source, contextPath, targetDir.getAbsolutePath()));
//        }
//
//        // Should be good to copy the WAR now
//        URLConnection uConn = source.openConnection();
//
//        try (InputStream is = uConn.getInputStream();
//             OutputStream os = new FileOutputStream(targetWar)) {
//            IOTools.flow(is, os);
//        }
//
//        return addWebapp(contextPath, targetWar.getAbsolutePath());
        throw new UnsupportedOperationException();
    }


    /**
     * Add a context - programmatic mode, no default web.xml used. This means
     * that there is no JSP support (no JSP servlet), no default servlet and
     * no web socket support unless explicitly enabled via the programmatic
     * interface. There is also no
     * {@link jakarta.servlet.ServletContainerInitializer} processing and no
     * annotation processing. If a
     * {@link jakarta.servlet.ServletContainerInitializer} is added
     * programmatically, there will still be no scanning for
     * {@link jakarta.servlet.annotation.HandlesTypes} matches.
     *
     * <p>
     * API calls equivalent with web.xml:
     *
     * <pre>{@code
     *  // context-param
     *  ctx.addParameter("name", "value");
     *
     *
     *  // error-page
     *  ErrorPage ep = new ErrorPage();
     *  ep.setErrorCode(500);
     *  ep.setLocation("/error.html");
     *  ctx.addErrorPage(ep);
     *
     *  ctx.addMimeMapping("ext", "type");
     * }</pre>
     *
     *
     * <p>
     * Note: If you reload the Context, all your configuration will be lost. If
     * you need reload support, consider using a LifecycleListener to provide
     * your configuration.
     *
     * <p>
     * TODO: add the rest
     *
     * @param contextPath The context mapping to use, "" for root context.
     * @param docBase     Base directory for the context, for static files.
     *                    Must exist, relative to the server home
     * @return the deployed context
     */
    public Context addContext(String contextPath, String docBase) {
        return addContext(getHost(), contextPath, docBase);
    }

    /**
     * Equivalent to &lt;servlet&gt;&lt;servlet-name&gt;&lt;servlet-class&gt;.
     *
     * <p>
     * In general it is better/faster to use the method that takes a
     * Servlet as param - this one can be used if the servlet is not
     * commonly used, and want to avoid loading all deps.
     * ( for example: jsp servlet )
     * <p>
     * You can customize the returned servlet, ex:
     * <pre>
     *    wrapper.addInitParameter("name", "value");
     *  </pre>
     *
     * @param contextPath  Context to add Servlet to
     * @param servletName  Servlet name (used in mappings)
     * @param servletClass The class to be used for the Servlet
     * @return The wrapper for the servlet
     */
    public Wrapper addServlet(String contextPath,
                              String servletName,
                              String servletClass) {
        Container ctx = getHost().findChild(contextPath);
        return addServlet((Context) ctx, servletName, servletClass);
    }

    /**
     * Static version of {@link #addServlet(String, String, String)}
     *
     * @param ctx          Context to add Servlet to
     * @param servletName  Servlet name (used in mappings)
     * @param servletClass The class to be used for the Servlet
     * @return The wrapper for the servlet
     */
    public static Wrapper addServlet(Context ctx,
                                     String servletName,
                                     String servletClass) {
//        // will do class for name and set init params
//        Wrapper sw = ctx.createWrapper();
//        if (sw == null) {
//            return null;
//        }
//        sw.setServletClass(servletClass);
//        sw.setName(servletName);
//        ctx.addChild(sw);
//
//        return sw;
        throw new UnsupportedOperationException();
    }

    /**
     * Add an existing Servlet to the context with no class.forName or
     * initialisation.
     *
     * @param contextPath Context to add Servlet to
     * @param servletName Servlet name (used in mappings)
     * @param servlet     The Servlet to add
     * @return The wrapper for the servlet
     */
    public Wrapper addServlet(String contextPath,
                              String servletName,
                              Servlet servlet) {
        Container ctx = getHost().findChild(contextPath);
        return addServlet((Context) ctx, servletName, servlet);
    }

    /**
     * Static version of {@link #addServlet(String, String, Servlet)}.
     *
     * @param ctx         Context to add Servlet to
     * @param servletName Servlet name (used in mappings)
     * @param servlet     The Servlet to add
     * @return The wrapper for the servlet
     */
    public static Wrapper addServlet(Context ctx,
                                     String servletName,
                                     Servlet servlet) {
//        // will do class for name and set init params
//        Wrapper sw = new ExistingStandardWrapper(servlet);
//        sw.setName(servletName);
//        ctx.addChild(sw);
//
//        return sw;
        throw new UnsupportedOperationException();
    }


    /**
     * Initialize the server given the specified configuration source.
     * The server will be loaded according to the Tomcat configuration
     * files contained in the source (server.xml, web.xml, context.xml,
     * SSL certificates, etc).
     * If no configuration source is specified, it will use the default
     * locations for these files.
     *
     * @param source The configuration source
     */
    public void init(ConfigurationSource source) {
        init(source, null);
    }

    /**
     * Initialize the server given the specified configuration source.
     * The server will be loaded according to the Tomcat configuration
     * files contained in the source (server.xml, web.xml, context.xml,
     * SSL certificates, etc).
     * If no configuration source is specified, it will use the default
     * locations for these files.
     *
     * @param source            The configuration source
     * @param catalinaArguments The arguments that should be passed to Catalina
     */
    public void init(ConfigurationSource source, String[] catalinaArguments) {
//        ConfigFileLoader.setSource(source);
//        addDefaultWebXmlToWebapp = false;
//        Catalina catalina = new Catalina();
//        // Load the Catalina instance with the regular configuration files
//        // from specified source
//        if (catalinaArguments == null) {
//            catalina.load();
//        } else {
//            catalina.load(catalinaArguments);
//        }
//        // Retrieve and set the server
//        server = catalina.getServer();
        throw new UnsupportedOperationException();
    }


    /**
     * Initialize the server.
     *
     * @throws LifecycleException Init error
     */
    public void init() throws LifecycleException {
        getServer();
        server.init();
    }


    /**
     * Start the server.
     *
     * @throws LifecycleException Start error
     */
    public void start() throws LifecycleException {
        getServer();
        server.start();
    }

    /**
     * Stop the server.
     *
     * @throws LifecycleException Stop error
     */
    public void stop() throws LifecycleException {
        getServer();
        server.stop();
    }


    /**
     * Destroy the server. This object cannot be used once this method has been
     * called.
     *
     * @throws LifecycleException Destroy error
     */
    public void destroy() throws LifecycleException {
        getServer();
        server.destroy();
        // Could null out objects here
    }

    /**
     * Add a user for the in-memory realm. All created apps use this
     * by default, can be replaced using setRealm().
     *
     * @param user The user name
     * @param pass The password
     */
    public void addUser(String user, String pass) {
        userPass.put(user, pass);
    }

    /**
     * Add a role to a user.
     *
     * @param user The user name
     * @param role The role name
     * @see #addUser(String, String)
     */
    public void addRole(String user, String role) {
        userRoles.computeIfAbsent(user, k -> new ArrayList<>()).add(role);
    }

    // ------- Extra customization -------
    // You can tune individual Tomcat objects, using internal APIs

    /**
     * Get the default HTTP connector that is used by the embedded
     * Tomcat. It is first configured connector in the service.
     * If there's no connector defined, it will create and add a default
     * connector using the port and address specified in this Tomcat
     * instance, and return it for further customization.
     *
     * @return The connector object
     */
    public Connector getConnector() {
//        Service service = getService();
//        if (service.findConnectors().length > 0) {
//            return service.findConnectors()[0];
//        }
//        // The same as in standard Tomcat configuration.
//        // This creates a NIO HTTP connector.
//        Connector connector = new Connector("HTTP/1.1");
//        connector.setPort(port);
//        service.addConnector(connector);
//        return connector;
        throw new UnsupportedOperationException();
    }

    /**
     * Set the specified connector in the service, if it is not already
     * present.
     * <p>
     * 作用：
     * 是将指定的连接器（Connector）实例添加到服务（Service）中，前提是这个连接器尚未被添加。
     * 这个方法的主要作用是确保服务中不会重复添加相同的连接器实例。
     * 它首先检查服务中是否已经存在指定的连接器，如果不存在，则将其添加到服务中。
     * 这样做可以避免重复配置和潜在的资源浪费，确保每个连接器只被添加一次。
     *
     * @param connector The connector instance to add
     */
    public void setConnector(Connector connector) {
        Service service = getService();
        boolean found = false;
        for (Connector serviceConnector : service.findConnectors()) {
            if (connector == serviceConnector) {
                found = true;
                break;
            }
        }
        if (!found) {
            service.addConnector(connector);
        }
    }

    /**
     * Get the service object. Can be used to add more
     * connectors and few other global settings.
     *
     * @return The service
     */
    public Service getService() {
        return getServer().findServices()[0];
    }

    /**
     * Sets the current host - all future webapps will
     * be added to this host. When tomcat starts, the
     * host will be the default host.
     *
     * @param host The current host
     */
    public void setHost(Host host) {
//        Engine engine = getEngine();
//        boolean found = false;
//        for (Container engineHost : engine.findChildren()) {
//            if (engineHost == host) {
//                found = true;
//                break;
//            }
//        }
//        if (!found) {
//            engine.addChild(host);
//        }
        throw new UnsupportedOperationException();
    }

    /**
     * 它的主要作用是获取与当前服务相关联的Host对象。在Tomcat中，Host代表一个虚拟主机，是容器（Container）的一种类型。
     * <p>
     * 总的来说，这个getHost方法的作用是确保有一个与服务相关联的Host对象。如果服务的引擎已经有了Host子容器，它就返回第一个；
     * 如果没有，它就创建一个新的StandardHost，设置其名称，添加到引擎中，然后返回它。这样，无论如何调用者都能得到一个Host对象。
     *
     * @return
     */
    public Host getHost() {
        /* 1. 获取引擎 */
        // 这行代码调用getEngine方法来获取当前服务关联的引擎(Engine)对象
        Engine engine = getEngine();
        /* 2. 尝试从引擎获取已有的Host */
        // 首先调用 engine.findChildren() 来获取引擎的所有 子容器，即它所管理的所有 Host。
        // 如果 Engine （引擎）至少有一个 Host 子容器（即至少有一个Host），方法将返回第一个Host对象。这里假设第一个子容器就是我们想要的Host。
        if (engine.findChildren().length > 0) {
            return (Host) engine.findChildren()[0];
        }

        /* 3. 如果没有现成的Host，创建一个新的 */
        // 这行创建了一个新的StandardHost对象，StandardHost是Host接口的一种具体实现
        Host host = new StandardHost();
        // 设置新创建的Host的名称。这里的hostname可能是一个先前定义的变量或属性，代表虚拟主机的名称。
        host.setName(hostname);
        // 将新创建的Host添加为引擎的子容器。
        getEngine().addChild(host);
        return host;
    }

    /**
     * Access to the engine, for further customization.
     *
     * @return The engine
     */
    public Engine getEngine() {
        Service service = getServer().findServices()[0];
        if (service.getContainer() != null) {
            return service.getContainer();
        }
        Engine engine = new StandardEngine();
        engine.setName("Tomcat");
        engine.setDefaultHost(hostname);
        engine.setRealm(createDefaultRealm());
        service.setContainer(engine);
        return engine;
    }

    /**
     * Get the server object. You can add listeners and few more
     * customizations. JNDI is disabled by default.
     *
     * @return The Server
     */
    public Server getServer() {

        if (server != null) {
            return server;
        }

        System.setProperty("catalina.useNaming", "false");

        server = new StandardServer();

        initBaseDir();

        // Set configuration source
        ConfigFileLoader.setSource(new CatalinaBaseConfigurationSource(new File(basedir), null));

        // todo 这里为什么是 -1
        server.setPort(-1);

        Service service = new StandardService();
        service.setName("Tomcat");
        server.addService(service);
        return server;
    }

    /**
     * @param host        The host in which the context will be deployed
     * @param contextPath The context mapping to use, "" for root context.
     * @param dir         Base directory for the context, for static files.
     *                    Must exist, relative to the server home
     * @return the deployed context
     * @see #addContext(String, String)
     */
    public Context addContext(Host host, String contextPath, String dir) {
        return addContext(host, contextPath, contextPath, dir);
    }

    /**
     * @param host        The host in which the context will be deployed
     * @param contextPath The context mapping to use, "" for root context.
     * @param contextName The context name
     * @param dir         Base directory for the context, for static files.
     *                    Must exist, relative to the server home
     * @return the deployed context
     * @see #addContext(String, String)
     */
    public Context addContext(Host host, String contextPath, String contextName,
                              String dir) {
//        silence(host, contextName);
//        Context ctx = createContext(host, contextPath);
//        ctx.setName(contextName);
//        ctx.setPath(contextPath);
//        ctx.setDocBase(dir);
//        ctx.addLifecycleListener(new FixContextListener());
//
//        if (host == null) {
//            getHost().addChild(ctx);
//        } else {
//            host.addChild(ctx);
//        }
//        return ctx;
        throw new UnsupportedOperationException();
    }


    /**
     * This is equivalent to adding a web application to a Host's appBase
     * (usually Tomcat's webapps directory). By default, the equivalent of the
     * default web.xml will be applied to the web application (see
     * {@link #initWebappDefaults(String)}). This may be prevented by calling
     * {@link #setAddDefaultWebXmlToWebapp(boolean)} with {@code false}. Any
     * <code>WEB-INF/web.xml</code> and <code>META-INF/context.xml</code>
     * packaged with the application will always be processed and normal web
     * fragment and {@link jakarta.servlet.ServletContainerInitializer} processing
     * will always be applied.
     *
     * @param host        The host in which the context will be deployed
     * @param contextPath The context mapping to use, "" for root context.
     * @param docBase     Base directory for the context, for static files. Must
     *                    exist and be an absolute path.
     * @return the deployed context
     */
    public Context addWebapp(Host host, String contextPath, String docBase) {
//        LifecycleListener listener = null;
//        try {
//            Class<?> clazz = Class.forName(getHost().getConfigClass());
//            listener = (LifecycleListener) clazz.getConstructor().newInstance();
//        } catch (ReflectiveOperationException e) {
//            // Wrap in IAE since we can't easily change the method signature to
//            // to throw the specific checked exceptions
//            throw new IllegalArgumentException(e);
//        }
//
//        return addWebapp(host, contextPath, docBase, listener);
        throw new UnsupportedOperationException();
    }


    /**
     * This is equivalent to adding a web application to a Host's appBase
     * (usually Tomcat's webapps directory). By default, the equivalent of the
     * default web.xml will be applied to the web application (see
     * {@link #initWebappDefaults(String)}). This may be prevented by calling
     * {@link #setAddDefaultWebXmlToWebapp(boolean)} with {@code false}. Any
     * <code>WEB-INF/web.xml</code> and <code>META-INF/context.xml</code>
     * packaged with the application will always be processed and normal web
     * fragment and {@link jakarta.servlet.ServletContainerInitializer} processing
     * will always be applied.
     *
     * @param host        The host in which the context will be deployed
     * @param contextPath The context mapping to use, "" for root context.
     * @param docBase     Base directory for the context, for static files. Must
     *                    exist and be an absolute path.
     * @param config      Custom context configuration helper. Any configuration
     *                    will be in addition to equivalent of the default
     *                    web.xml configuration described above.
     * @return the deployed context
     */
    public Context addWebapp(Host host, String contextPath, String docBase,
                             LifecycleListener config) {

//        silence(host, contextPath);
//
//        Context ctx = createContext(host, contextPath);
//        ctx.setPath(contextPath);
//        ctx.setDocBase(docBase);
//
//        if (addDefaultWebXmlToWebapp) {
//            ctx.addLifecycleListener(getDefaultWebXmlListener());
//        }
//
//        ctx.setConfigFile(getWebappConfigFile(docBase, contextPath));
//
//        ctx.addLifecycleListener(config);
//
//        if (addDefaultWebXmlToWebapp && (config instanceof ContextConfig)) {
//            // prevent it from looking ( if it finds one - it'll have dup error )
//            ((ContextConfig) config).setDefaultWebXml(noDefaultWebXmlPath());
//        }
//
//        if (host == null) {
//            getHost().addChild(ctx);
//        } else {
//            host.addChild(ctx);
//        }
//
//        return ctx;
        throw new UnsupportedOperationException();
    }

    /**
     * Return a listener that provides the required configuration items for JSP
     * processing. From the standard Tomcat global web.xml. Pass this to
     * {@link Context#addLifecycleListener(LifecycleListener)} and then pass the
     * result of {@link #noDefaultWebXmlPath()} to
     * {@link ContextConfig#setDefaultWebXml(String)}.
     *
     * @return a listener object that configures default JSP processing.
     */
    public LifecycleListener getDefaultWebXmlListener() {
        return new DefaultWebXmlListener();
    }

    /**
     * @return a pathname to pass to
     * {@link ContextConfig#setDefaultWebXml(String)} when using
     * {@link #getDefaultWebXmlListener()}.
     */
    public String noDefaultWebXmlPath() {
//        return Constants.NoDefaultWebXml;
        throw new UnsupportedOperationException();
    }

    // ---------- Helper methods and classes -------------------

    /**
     * Create an in-memory realm. You can replace it for contexts with a real
     * one. The Realm created here will be added to the Engine by default and
     * may be replaced at the Engine level or over-ridden (as per normal Tomcat
     * behaviour) at the Host or Context level.
     *
     * @return a realm instance
     */
    protected Realm createDefaultRealm() {
        return new SimpleRealm();
    }

    /**
     * 在 Tomcat 中，Realm 类是用于实现身份验证和授权的关键组件。它提供了一种机制来验证用户身份（即身份验证）
     * 并控制用户访问应用程序中不同部分的权限（即授权）。简而言之，Realm 是 Tomcat 安全架构中
     * 用于存储和检索用户的安全数据（如用户名、密码和角色）的地方。
     * <p>
     * SimpleRealm 是 RealmBase 的一个子类，实现了特定的身份验证和授权逻辑
     * SimpleRealm 类中的这些实现提供了一种简单的方式来管理和验证用户的凭据。在实际的生产环境中，Realm 的实现可能会连接到数据库、
     * LDAP服务器或其他外部系统来检索用户凭据。在 Tomcat 中，可以配置不同类型的 Realm 来满足应用程序的特定安全需求。
     */
    private class SimpleRealm extends RealmBase {

        @Override
        protected String getPassword(String username) {
            // userPass 实例的初始化：private final Map<String, String> userPass = new HashMap<>();
            return userPass.get(username);
        }

        @Override
        protected Principal getPrincipal(String username) {
            // userPrincipals 实例的初始化：private final Map<String, Principal> userPrincipals = new HashMap<>();
            Principal p = userPrincipals.get(username);
            if (p == null) {
                /* 如果给定用户名的 Principal 对象不存在（即p为null），方法将进一步尝试从userPass映射中检索该用户的密码 */
                String pass = userPass.get(username);
                if (pass != null) {
                    /* 如果密码存在（即用户确实存在），则创建一个新的GenericPrincipal对象，代表这个用户。
                    GenericPrincipal是一个简单的Principal实现，通常包含用户的名称、密码和角色 */
                    // private final Map<String, List<String>> userRoles = new HashMap<>();
                    p = new GenericPrincipal(username,
                            userRoles.get(username));
                    /* 新创建的GenericPrincipal对象被放入userPrincipals映射中，以便下次可以直接检索 */
                    userPrincipals.put(username, p);
                }
            }
            return p;
        }
    }


    protected void initBaseDir() {
        String catalinaHome = System.getProperty(Globals.CATALINA_HOME_PROP);
        if (basedir == null) {
            basedir = System.getProperty(Globals.CATALINA_BASE_PROP);
        }
        if (basedir == null) {
            basedir = catalinaHome;
        }
        if (basedir == null) {
            // Create a temp dir.
            basedir = System.getProperty("user.dir") + "/tomcat." + port;
        }

        File baseFile = new File(basedir);
        if (baseFile.exists()) {
            if (!baseFile.isDirectory()) {
                throw new IllegalArgumentException(sm.getString("tomcat.baseDirNotDir", baseFile));
            }
        } else {
            if (!baseFile.mkdirs()) {
                // Failed to create base directory（创建目录失败）
                throw new IllegalStateException(sm.getString("tomcat.baseDirMakeFail", baseFile));
            }
            /*
             * If file permissions were going to be set on the newly created
             * directory, this is the place to do it. However, even simple
             * calls such as File.setReadable(boolean,boolean) behaves
             * differently on different platforms. Therefore, setBaseDir
             * documents that the user needs to do this.
             *
             * 解释：
             * 如果要在新创建的目录上设置文件权限，那么就是在这里。然而，即使像
             * File.setReadable(boolean,boolean)这样简单的调用在不同的平台上也有不同的表现。
             * 因此，需要使用setBaseDir文档来完成此操作。
             */
        }
        try {
            baseFile = baseFile.getCanonicalFile();
        } catch (IOException e) {
            baseFile = baseFile.getAbsoluteFile();
        }
        server.setCatalinaBase(baseFile);
        System.setProperty(Globals.CATALINA_BASE_PROP, baseFile.getPath());// 塞值到 map 里面
        basedir = baseFile.getPath();// 获取路径

        if (catalinaHome == null) {
            server.setCatalinaHome(baseFile);
        } else {
            // todo 这个地方上次没调进来
//            File homeFile = new File(catalinaHome);
//            if (!homeFile.isDirectory() && !homeFile.mkdirs()) {
//                // Failed to create home directory
//                throw new IllegalStateException(sm.getString("tomcat.homeDirMakeFail", homeFile));
//            }
//            try {
//                homeFile = homeFile.getCanonicalFile();
//            } catch (IOException e) {
//                homeFile = homeFile.getAbsoluteFile();
//            }
//            server.setCatalinaHome(homeFile);
            throw new UnsupportedOperationException();
        }
        System.setProperty(Globals.CATALINA_HOME_PROP,
                server.getCatalinaHome().getPath());
//            throw new UnsupportedOperationException();
    }


    static final String[] silences = new String[]{
            "org.apache.coyote.http11.Http11NioProtocol",
            "org.apache.catalina.core.StandardService",
            "org.apache.catalina.core.StandardEngine",
            "org.apache.catalina.startup.ContextConfig",
            "org.apache.catalina.core.ApplicationContext",
            "org.apache.catalina.core.AprLifecycleListener"
    };

    private boolean silent = false;

    /**
     * Controls if the loggers will be silenced or not.
     *
     * @param silent <code>true</code> sets the log level to WARN for the
     *               loggers that log information on Tomcat start up. This
     *               prevents the usual startup information being logged.
     *               <code>false</code> sets the log level to the default
     *               level of INFO.
     */
    public void setSilent(boolean silent) {
//        this.silent = silent;
//        for (String s : silences) {
//            Logger logger = Logger.getLogger(s);
//            pinnedLoggers.put(s, logger);
//            if (silent) {
//                logger.setLevel(Level.WARNING);
//            } else {
//                logger.setLevel(Level.INFO);
//            }
//        }
        throw new UnsupportedOperationException();
    }

    private void silence(Host host, String contextPath) {
//        String loggerName = getLoggerName(host, contextPath);
//        Logger logger = Logger.getLogger(loggerName);
//        pinnedLoggers.put(loggerName, logger);
//        if (silent) {
//            logger.setLevel(Level.WARNING);
//        } else {
//            logger.setLevel(Level.INFO);
//        }
        throw new UnsupportedOperationException();
    }


    /**
     * By default, when calling addWebapp() to create a Context, the settings from
     * from the default web.xml are added to the context.  Calling this method with
     * a <code>false</code> value prior to calling addWebapp() allows to opt out of
     * the default settings. In that event you will need to add the configurations
     * yourself,  either programmatically or by using web.xml deployment descriptors.
     *
     * @param addDefaultWebXmlToWebapp <code>false</code> will prevent the class from
     *                                 automatically adding the default settings when
     *                                 calling addWebapp().
     *                                 <code>true</code> will add the default settings
     *                                 and is the default behavior.
     * @see #addWebapp(Host, String, String, LifecycleListener)
     */
    public void setAddDefaultWebXmlToWebapp(boolean addDefaultWebXmlToWebapp) {
        this.addDefaultWebXmlToWebapp = addDefaultWebXmlToWebapp;
    }


    /*
     * Uses essentially the same logic as {@link ContainerBase#logName()}.
     */
    private String getLoggerName(Host host, String contextName) {
//        if (host == null) {
//            host = getHost();
//        }
//        StringBuilder loggerName = new StringBuilder();
//        loggerName.append(ContainerBase.class.getName());
//        loggerName.append(".[");
//        // Engine name
//        loggerName.append(host.getParent().getName());
//        loggerName.append("].[");
//        // Host name
//        loggerName.append(host.getName());
//        loggerName.append("].[");
//        // Context name
//        if (contextName == null || contextName.equals("")) {
//            loggerName.append('/');
//        } else if (contextName.startsWith("##")) {
//            loggerName.append('/');
//            loggerName.append(contextName);
//        }
//        loggerName.append(']');
//
//        return loggerName.toString();
        throw new UnsupportedOperationException();
    }

    /**
     * Create the configured {@link Context} for the given <code>host</code>.
     * The default constructor of the class that was configured with
     * {@link StandardHost#setContextClass(String)} will be used
     *
     * @param host host for which the {@link Context} should be created, or
     *             <code>null</code> if default host should be used
     * @param url  path of the webapp which should get the {@link Context}
     * @return newly created {@link Context}
     */
    private Context createContext(Host host, String url) {
//        String defaultContextClass = StandardContext.class.getName();
//        String contextClass = StandardContext.class.getName();
//        if (host == null) {
//            host = this.getHost();
//        }
//        if (host instanceof StandardHost) {
//            contextClass = ((StandardHost) host).getContextClass();
//        }
//        try {
//            if (defaultContextClass.equals(contextClass)) {
//                return new StandardContext();
//            } else {
//                return (Context) Class.forName(contextClass).getConstructor()
//                        .newInstance();
//            }
//
//        } catch (ReflectiveOperationException  | IllegalArgumentException | SecurityException e) {
//            throw new IllegalArgumentException(sm.getString("tomcat.noContextClass", contextClass, host, url), e);
//        }
        throw new UnsupportedOperationException();
    }

    /**
     * Enables JNDI naming which is disabled by default. Server must implement
     * {@link Lifecycle} in order for the {@link NamingContextListener} to be
     * used.
     */
    public void enableNaming() {
//        // Make sure getServer() has been called as that is where naming is
//        // disabled
//        getServer();
//        server.addLifecycleListener(new NamingContextListener());
//
//        System.setProperty("catalina.useNaming", "true");
//
//        String value = "org.apache.naming";
//        String oldValue =
//                System.getProperty(javax.naming.Context.URL_PKG_PREFIXES);
//        if (oldValue != null) {
//            if (oldValue.contains(value)) {
//                value = oldValue;
//            } else {
//                value = value + ":" + oldValue;
//            }
//        }
//        System.setProperty(javax.naming.Context.URL_PKG_PREFIXES, value);
//
//        value = System.getProperty
//                (javax.naming.Context.INITIAL_CONTEXT_FACTORY);
//        if (value == null) {
//            System.setProperty
//                    (javax.naming.Context.INITIAL_CONTEXT_FACTORY,
//                            "org.apache.naming.java.javaURLContextFactory");
//        }
        throw new UnsupportedOperationException();
    }


    /**
     * Provide default configuration for a context. This is broadly the
     * programmatic equivalent of the default web.xml and provides the following
     * features:
     * <ul>
     * <li>Default servlet mapped to "/"</li>
     * <li>JSP servlet mapped to "*.jsp" and ""*.jspx"</li>
     * <li>Session timeout of 30 minutes</li>
     * <li>MIME mappings (subset of those in conf/web.xml)</li>
     * <li>Welcome files</li>
     * </ul>
     * TODO: Align the MIME mappings with conf/web.xml - possibly via a common
     *       file.
     *
     * @param contextPath The path of the context to set the defaults for
     */
    public void initWebappDefaults(String contextPath) {
        Container ctx = getHost().findChild(contextPath);
        initWebappDefaults((Context) ctx);
    }


    /**
     * Static version of {@link #initWebappDefaults(String)}.
     *
     * @param ctx The context to set the defaults for
     */
    public static void initWebappDefaults(Context ctx) {
//        // Default servlet
//        Wrapper servlet = addServlet(
//                ctx, "default", "org.apache.catalina.servlets.DefaultServlet");
//        servlet.setLoadOnStartup(1);
//        servlet.setOverridable(true);
//
//        // JSP servlet (by class name - to avoid loading all deps)
//        servlet = addServlet(
//                ctx, "jsp", "org.apache.jasper.servlet.JspServlet");
//        servlet.addInitParameter("fork", "false");
//        servlet.setLoadOnStartup(3);
//        servlet.setOverridable(true);
//
//        // Servlet mappings
//        ctx.addServletMappingDecoded("/", "default");
//        ctx.addServletMappingDecoded("*.jsp", "jsp");
//        ctx.addServletMappingDecoded("*.jspx", "jsp");
//
//        // Sessions
//        ctx.setSessionTimeout(30);
//
//        // MIME type mappings
//        addDefaultMimeTypeMappings(ctx);
//
//        // Welcome files
//        ctx.addWelcomeFile("index.html");
//        ctx.addWelcomeFile("index.htm");
//        ctx.addWelcomeFile("index.jsp");
        throw new UnsupportedOperationException();
    }


    /**
     * Add the default MIME type mappings to the provide Context.
     *
     * @param context The web application to which the default MIME type
     *                mappings should be added.
     */
    public static void addDefaultMimeTypeMappings(Context context) {
//        Properties defaultMimeMappings = new Properties();
//        try (InputStream is = Tomcat.class.getResourceAsStream("MimeTypeMappings.properties")) {
//            defaultMimeMappings.load(is);
//            for (Map.Entry<Object, Object>  entry: defaultMimeMappings.entrySet()) {
//                context.addMimeMapping((String) entry.getKey(), (String) entry.getValue());
//            }
//        } catch (IOException e) {
//            throw new IllegalStateException(sm.getString("tomcat.defaultMimeTypeMappingsFail"), e);
//        }
        throw new UnsupportedOperationException();
    }


    /**
     * Fix startup sequence - required if you don't use web.xml.
     *
     * <p>
     * The start() method in context will set 'configured' to false - and
     * expects a listener to set it back to true.
     */
    public static class FixContextListener implements LifecycleListener {

        /**
         * 这个方法接收一个 LifecycleEvent 对象作为参数。
         * 这个 LifecycleEvent 对象包含了与事件相关的信息，包括 事件类型(type) 和 事件源(source)。
         *
         * @param event LifecycleEvent that has occurred
         */
        @Override
        public void lifecycleEvent(LifecycleEvent event) {
            try {
                // event.getLifecycle() 方法调用是从传入的 LifecycleEvent 对象中获取事件源，这个源本质上是引发事件的对象
                // 在这个场景下，预期的事件源是一个实现了 Lifecycle 接口的 Context 对象。
                // 因此，代码尝试将这个事件源从 Lifecycle 类型强制转换为 Context 类型。

                // 首先尝试将事件相关联的 Lifecycle 对象强制转换为 Context 类型。这表明该监听器主要关注与 Context 对象相关的事件。
                Context context = (Context) event.getLifecycle();
                if (event.getType().equals(Lifecycle.CONFIGURE_START_EVENT)) {
                    /* 处理配置开始事件 */
                    // 设置 Context 的 configured 属性为 true，标志着上下文已被配置。
                    context.setConfigured(true);

                    // Process annotations
                    // 调用 WebAnnotationSet.loadApplicationAnnotations(context) 方法来处理应用中的注解。
                    WebAnnotationSet.loadApplicationAnnotations(context);

                    // LoginConfig is required to process @ServletSecurity
                    // annotations
                    // 确保 Context 有一个 LoginConfig。如果没有，则创建一个新的 LoginConfig 实例，
                    // 并添加一个 NonLoginAuthenticator 阀到 Context 的管道中。
                    if (context.getLoginConfig() == null) {
                        context.setLoginConfig(new LoginConfig("NONE", null, null, null));
                        context.getPipeline().addValve(new NonLoginAuthenticator());
                    }
                }
            } catch (ClassCastException e) {
                // 类转换异常
                // Context context = (Context) event.getLifecycle(); 如果这个强制转换失败（意味着事件源不是 Context 类型的对象），
                // 则会抛出 ClassCastException。这是异常处理部分提到的“传入的 Lifecycle 对象不是 Context 类型”的情况。

                // 如果在处理过程中发生 ClassCastException，则代码中没有提供处理这个异常的逻辑。
                // 这通常意味着传入的 Lifecycle 对象不是 Context 类型。
            }
        }
    }


    /**
     * Fix reload - required if reloading and using programmatic configuration.
     * When a context is reloaded, any programmatic configuration is lost. This
     * listener sets the equivalent of conf/web.xml when the context starts.
     */
    public static class DefaultWebXmlListener implements LifecycleListener {
        @Override
        public void lifecycleEvent(LifecycleEvent event) {
//            if (Lifecycle.BEFORE_START_EVENT.equals(event.getType())) {
//                initWebappDefaults((Context) event.getLifecycle());
//            }
            throw new UnsupportedOperationException();
        }
    }


    /**
     * Helper class for wrapping existing servlets. This disables servlet
     * lifecycle and normal reloading, but also reduces overhead and provide
     * more direct control over the servlet.
     */
    public static class ExistingStandardWrapper extends StandardWrapper {
        private final Servlet existing;

        public ExistingStandardWrapper(Servlet existing) {
//            this.existing = existing;
//            this.asyncSupported = hasAsync(existing);
            throw new UnsupportedOperationException();
        }

        //
        private static boolean hasAsync(Servlet existing) {
//            boolean result = false;
//            Class<?> clazz = existing.getClass();
//            WebServlet ws = clazz.getAnnotation(WebServlet.class);
//            if (ws != null) {
//                result = ws.asyncSupported();
//            }
//            return result;
//        }
//
//        @Override
//        public synchronized Servlet loadServlet() throws ServletException {
//            if (!instanceInitialized) {
//                existing.init(facade);
//                instanceInitialized = true;
//            }
//            return existing;
//        }
//        @Override
//        public long getAvailable() {
//            return 0;
//        }
//        @Override
//        public boolean isUnavailable() {
//            return false;
//        }
//        @Override
//        public Servlet getServlet() {
//            return existing;
//        }
//        @Override
//        public String getServletClass() {
//            return existing.getClass().getName();
//        }
            throw new UnsupportedOperationException();
        }

        protected URL getWebappConfigFile(String path, String contextName) {
//        File docBase = new File(path);
//        if (docBase.isDirectory()) {
//            return getWebappConfigFileFromDirectory(docBase, contextName);
//        } else {
//            return getWebappConfigFileFromWar(docBase, contextName);
//        }
            throw new UnsupportedOperationException();
        }

        private URL getWebappConfigFileFromDirectory(File docBase, String contextName) {
//            URL result = null;
//            File webAppContextXml = new File(docBase, Constants.ApplicationContextXml);
//            if (webAppContextXml.exists()) {
//                try {
//                    result = webAppContextXml.toURI().toURL();
//                } catch (MalformedURLException e) {
//                    Logger.getLogger(getLoggerName(getHost(), contextName)).log(Level.WARNING,
//                            sm.getString("tomcat.noContextXml", docBase), e);
//                }
//            }
//            return result;
            throw new UnsupportedOperationException();
        }

        private URL getWebappConfigFileFromWar(File docBase, String contextName) {
//        URL result = null;
//        try (JarFile jar = new JarFile(docBase)) {
//            JarEntry entry = jar.getJarEntry(Constants.ApplicationContextXml);
//            if (entry != null) {
//                result = UriUtil.buildJarUrl(docBase, Constants.ApplicationContextXml);
//            }
//        } catch (IOException e) {
//            Logger.getLogger(getLoggerName(getHost(), contextName)).log(Level.WARNING,
//                    sm.getString("tomcat.noContextXml", docBase), e);
//        }
//        return result;
            throw new UnsupportedOperationException();
        }

        static {
//        // Graal native images don't load any configuration except the VM default
//        if (JreCompat.isGraalAvailable()) {
//            try (InputStream is = new FileInputStream(new File(System.getProperty("java.util.logging.config.file", "conf/logging.properties")))) {
//                LogManager.getLogManager().readConfiguration(is);
//            } catch (SecurityException | IOException e) {
//                // Ignore, the VM default will be used
//            }
//        }
        }

        /**
         * Main executable method for use with a Maven packager.
         *
         * @param args the command line arguments
         * @throws Exception if an error occurs
         */
        public static void main(String[] args) throws Exception {
//        // Process some command line parameters
//        String[] catalinaArguments = null;
//        for (int i = 0; i < args.length; i++) {
//            if (args[i].equals("--no-jmx")) {
//                Registry.disableRegistry();
//            } else if (args[i].equals("--catalina")) {
//                // This was already processed before
//                // Skip the rest of the arguments as they are for Catalina
//                ArrayList<String> result = new ArrayList<>();
//                for (int j = i + 1; j < args.length; j++) {
//                    result.add(args[j]);
//                }
//                catalinaArguments = result.toArray(new String[0]);
//                break;
//            }
//        }
//        SecurityClassLoad.securityClassLoad(Thread.currentThread().getContextClassLoader());
//        Tomcat tomcat = new Tomcat();
//        // Create a Catalina instance and let it parse the configuration files
//        // It will also set a shutdown hook to stop the Server when needed
//        // Use the default configuration source
//        tomcat.init(null, catalinaArguments);
//        boolean await = false;
//        String path = "";
//        // Process command line parameters
//        for (int i = 0; i < args.length; i++) {
//            if (args[i].equals("--war")) {
//                if (++i >= args.length) {
//                    throw new IllegalArgumentException(sm.getString("tomcat.invalidCommandLine", args[i - 1]));
//                }
//                File war = new File(args[i]);
//                tomcat.addWebapp(path, war.getAbsolutePath());
//            } else if (args[i].equals("--path")) {
//                if (++i >= args.length) {
//                    throw new IllegalArgumentException(sm.getString("tomcat.invalidCommandLine", args[i - 1]));
//                }
//                path = args[i];
//            } else if (args[i].equals("--await")) {
//                await = true;
//            } else if (args[i].equals("--no-jmx")) {
//                // This was already processed before
//            } else if (args[i].equals("--catalina")) {
//                // This was already processed before
//                // Skip the rest of the arguments as they are for Catalina
//                break;
//            } else {
//                throw new IllegalArgumentException(sm.getString("tomcat.invalidCommandLine", args[i]));
//            }
//        }
//        tomcat.start();
//        // Ideally the utility threads are non daemon
//        if (await) {
//            tomcat.getServer().await();
//        }
            throw new UnsupportedOperationException();
        }

    }
}

