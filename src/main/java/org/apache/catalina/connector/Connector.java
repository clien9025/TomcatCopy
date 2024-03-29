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
package org.apache.catalina.connector;

import org.apache.catalina.Globals;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.LifecycleState;
import org.apache.catalina.Service;
import org.apache.catalina.core.AprStatus;
import org.apache.catalina.util.LifecycleMBeanBase;
import org.apache.coyote.AbstractProtocol;
import org.apache.coyote.Adapter;
import org.apache.coyote.ProtocolHandler;
import org.apache.coyote.UpgradeProtocol;
import org.apache.coyote.http11.AbstractHttp11JsseProtocol;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.IntrospectionUtils;
import org.apache.tomcat.util.buf.B2CConverter;
import org.apache.tomcat.util.buf.CharsetUtil;
import org.apache.tomcat.util.buf.EncodedSolidusHandling;
import org.apache.tomcat.util.net.SSLHostConfig;
import org.apache.tomcat.util.net.openssl.OpenSSLImplementation;
import org.apache.tomcat.util.res.StringManager;

import javax.management.ObjectName;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashSet;


/**
 * Implementation of a Coyote connector.
 *
 * @author Craig R. McClanahan
 * @author Remy Maucherat
 */
public class Connector extends LifecycleMBeanBase {


    private static final Log log = LogFactory.getLog(Connector.class);


    public static final String INTERNAL_EXECUTOR_NAME = "Internal";


    // ------------------------------------------------------------ Constructor

    /**
     * Defaults to using HTTP/1.1 NIO implementation.
     */
    public Connector() {
        this("HTTP/1.1");
    }


    public Connector(String protocol) {
        configuredProtocol = protocol;
        ProtocolHandler p = null;
        try {
            p = ProtocolHandler.create(protocol);
        } catch (Exception e) {
            log.error(sm.getString("coyoteConnector.protocolHandlerInstantiationFailed"), e);
        }
        if (p != null) {
            protocolHandler = p;
            protocolHandlerClassName = protocolHandler.getClass().getName();
        } else {
            protocolHandler = null;
            protocolHandlerClassName = protocol;
        }
        // Default for Connector depends on this system property
        setThrowOnFailure(Boolean.getBoolean("org.apache.catalina.startup.EXIT_ON_INIT_FAILURE"));
    }


    public Connector(ProtocolHandler protocolHandler) {
        protocolHandlerClassName = protocolHandler.getClass().getName();
        configuredProtocol = protocolHandlerClassName;
        this.protocolHandler = protocolHandler;
        // Default for Connector depends on this system property
        setThrowOnFailure(Boolean.getBoolean("org.apache.catalina.startup.EXIT_ON_INIT_FAILURE"));
    }


    // ----------------------------------------------------- Instance Variables

    /**
     * The <code>Service</code> we are associated with (if any).
     */
    protected Service service = null;


    /**
     * If this is <code>true</code> the '\' character will be permitted as a path delimiter. If not specified, the
     * default value of <code>false</code> will be used.
     */
    protected boolean allowBackslash = false;


    /**
     * Do we allow TRACE ?
     */
    protected boolean allowTrace = false;


    /**
     * Default timeout for asynchronous requests (ms).
     */
    protected long asyncTimeout = 30000;


    /**
     * The "enable DNS lookups" flag for this Connector.
     */
    protected boolean enableLookups = false;


    /**
     * If this is <code>true</code> then a call to <code>Response.getWriter()</code> if no character encoding has been
     * specified will result in subsequent calls to <code>Response.getCharacterEncoding()</code> returning
     * <code>ISO-8859-1</code> and the <code>Content-Type</code> response header will include a
     * <code>charset=ISO-8859-1</code> component. (SRV.15.2.22.1) If not specified, the default specification compliant
     * value of <code>true</code> will be used.
     */
    protected boolean enforceEncodingInGetWriter = true;


    /**
     * Is generation of X-Powered-By response header enabled/disabled?
     */
    protected boolean xpoweredBy = false;


    /**
     * The server name to which we should pretend requests to this Connector were directed. This is useful when
     * operating Tomcat behind a proxy server, so that redirects get constructed accurately. If not specified, the
     * server name included in the <code>Host</code> header is used.
     */
    protected String proxyName = null;


    /**
     * The server port to which we should pretend requests to this Connector were directed. This is useful when
     * operating Tomcat behind a proxy server, so that redirects get constructed accurately. If not specified, the port
     * number specified by the <code>port</code> property is used.
     */
    protected int proxyPort = 0;


    /**
     * The flag that controls recycling of the facades of the request processing objects. If set to <code>true</code>
     * the object facades will be discarded when the request is recycled. If the security manager is enabled, this
     * setting is ignored and object facades are always discarded.
     */
    protected boolean discardFacades = true;


    /**
     * The redirect port for non-SSL to SSL redirects.
     */
    protected int redirectPort = 443;


    /**
     * The request scheme that will be set on all requests received through this connector.
     */
    protected String scheme = "http";


    /**
     * The secure connection flag that will be set on all requests received through this connector.
     */
    protected boolean secure = false;


    /**
     * The string manager for this package.
     */
    protected static final StringManager sm = StringManager.getManager(Connector.class);


    /**
     * The maximum number of cookies permitted for a request. Use a value less than zero for no limit. Defaults to 200.
     */
    private int maxCookieCount = 200;

    /**
     * The maximum number of parameters (GET plus POST) which will be automatically parsed by the container. 10000 by
     * default. The default Tomcat server.xml configures a lower default of 1000. A value of less than 0 means no limit.
     */
    protected int maxParameterCount = 10000;

    /**
     * Maximum size of a POST which will be automatically parsed by the container. 2 MiB by default.
     */
    protected int maxPostSize = 2 * 1024 * 1024;


    /**
     * Maximum size of a POST which will be saved by the container during authentication. 4 KiB by default
     */
    protected int maxSavePostSize = 4 * 1024;

    /**
     * Comma-separated list of HTTP methods that will be parsed according to POST-style rules for
     * application/x-www-form-urlencoded request bodies.
     */
    protected String parseBodyMethods = "POST";

    /**
     * A Set of methods determined by {@link #parseBodyMethods}.
     */
    protected HashSet<String> parseBodyMethodsSet;


    /**
     * Flag to use IP-based virtual hosting.
     */
    protected boolean useIPVHosts = false;


    /**
     * Coyote Protocol handler class name. See {@link #Connector()} for current default.
     */
    protected final String protocolHandlerClassName;


    /**
     * Name of the protocol that was configured.
     */
    protected final String configuredProtocol;


    /**
     * Coyote protocol handler.
     */
    protected final ProtocolHandler protocolHandler;


    /**
     * Coyote adapter.
     */
    protected Adapter adapter = null;


    /**
     * The URI encoding in use.
     */
    private Charset uriCharset = StandardCharsets.UTF_8;


    /**
     * The behavior when an encoded solidus (slash) is submitted.
     */
    private EncodedSolidusHandling encodedSolidusHandling = EncodedSolidusHandling.REJECT;


    /**
     * URI encoding as body.
     */
    protected boolean useBodyEncodingForURI = false;


    private boolean rejectSuspiciousURIs;

    /*9999999999999*/

    // ------------------------------------------------------------- Properties

    /**
     * Return a property from the protocol handler.
     *
     * @param name the property name
     * @return the property value
     */
    public Object getProperty(String name) {
        if (protocolHandler == null) {
            return null;
        }
        return IntrospectionUtils.getProperty(protocolHandler, name);
    }


    /**
     * Set a property on the protocol handler.
     *
     * @param name  the property name
     * @param value the property value
     * @return <code>true</code> if the property was successfully set
     */
    public boolean setProperty(String name, String value) {
        if (protocolHandler == null) {
            return false;
        }
        return IntrospectionUtils.setProperty(protocolHandler, name, value);
    }


    /**
     * @return the <code>Service</code> with which we are associated (if any).
     */
    public Service getService() {
        return this.service;
    }


    /**
     * Set the <code>Service</code> with which we are associated (if any).
     *
     * @param service The service that owns this Engine
     */
    public void setService(Service service) {
        this.service = service;
    }


    /**
     * @return <code>true</code> if backslash characters are allowed in URLs. Default value is <code>false</code>.
     */
    public boolean getAllowBackslash() {
        return allowBackslash;
    }


    /**
     * Set the allowBackslash flag.
     *
     * @param allowBackslash the new flag value
     */
    public void setAllowBackslash(boolean allowBackslash) {
        this.allowBackslash = allowBackslash;
    }


    /**
     * @return <code>true</code> if the TRACE method is allowed. Default value is <code>false</code>.
     */
    public boolean getAllowTrace() {
        return this.allowTrace;
    }


    /**
     * Set the allowTrace flag, to disable or enable the TRACE HTTP method.
     *
     * @param allowTrace The new allowTrace flag
     */
    public void setAllowTrace(boolean allowTrace) {
        this.allowTrace = allowTrace;
    }


    /**
     * @return the default timeout for async requests in ms.
     */
    public long getAsyncTimeout() {
        return asyncTimeout;
    }


    /**
     * Set the default timeout for async requests.
     *
     * @param asyncTimeout The new timeout in ms.
     */
    public void setAsyncTimeout(long asyncTimeout) {
        this.asyncTimeout = asyncTimeout;
    }


    /**
     * @return <code>true</code> if the object facades are discarded, either when the discardFacades value is
     * <code>true</code> or when the security manager is enabled.
     */
    public boolean getDiscardFacades() {
        return discardFacades || Globals.IS_SECURITY_ENABLED;
    }


    /**
     * Set the recycling strategy for the object facades.
     *
     * @param discardFacades the new value of the flag
     */
    public void setDiscardFacades(boolean discardFacades) {
        this.discardFacades = discardFacades;
    }


    /**
     * @return the "enable DNS lookups" flag.
     */
    public boolean getEnableLookups() {
        return this.enableLookups;
    }


    /**
     * Set the "enable DNS lookups" flag.
     *
     * @param enableLookups The new "enable DNS lookups" flag value
     */
    public void setEnableLookups(boolean enableLookups) {
        this.enableLookups = enableLookups;
    }


    /**
     * @return <code>true</code> if a default character encoding will be set when calling Response.getWriter()
     */
    public boolean getEnforceEncodingInGetWriter() {
        return enforceEncodingInGetWriter;
    }


    /**
     * Set the enforceEncodingInGetWriter flag.
     *
     * @param enforceEncodingInGetWriter the new flag value
     */
    public void setEnforceEncodingInGetWriter(boolean enforceEncodingInGetWriter) {
        this.enforceEncodingInGetWriter = enforceEncodingInGetWriter;
    }


    public int getMaxCookieCount() {
        return maxCookieCount;
    }


    public void setMaxCookieCount(int maxCookieCount) {
        this.maxCookieCount = maxCookieCount;
    }


    /**
     * @return the maximum number of parameters (GET plus POST) that will be automatically parsed by the container. A
     * value of less than 0 means no limit.
     */
    public int getMaxParameterCount() {
        return maxParameterCount;
    }


    /**
     * Set the maximum number of parameters (GET plus POST) that will be automatically parsed by the container. A value
     * of less than 0 means no limit.
     *
     * @param maxParameterCount The new setting
     */
    public void setMaxParameterCount(int maxParameterCount) {
        this.maxParameterCount = maxParameterCount;
    }


    /**
     * @return the maximum size of a POST which will be automatically parsed by the container.
     */
    public int getMaxPostSize() {
        return maxPostSize;
    }


    /**
     * Maximum size of a POST which will be automatically parsed by the container. 2 MiB by default.
     * 容器将自动解析的 POST 的最大大小。默认为 2 MiB。
     */

    /**
     * Set the maximum size of a POST which will be automatically parsed by the container.
     * 设置 -----> 容器将自动解析的 POST 的最大大小(如下面一行注释)
     * 作用：
     * 允许用户设置服务器容器（如Tomcat）将自动解析的POST请求体的最大大小。这是一个配置设置，
     * 可以帮助控制接收大量数据的能力，防止由于处理过大的POST请求而导致的性能问题或安全风险。
     *
     * @param maxPostSize The new maximum size in bytes of a POST which will be automatically parsed by the container
     *                    容器将自动解析的POST请求的新最大大小（以字节为单位）
     */
    public void setMaxPostSize(int maxPostSize) {
        this.maxPostSize = maxPostSize;
    }


    /**
     * @return the maximum size of a POST which will be saved by the container during authentication.
     */
    public int getMaxSavePostSize() {
        return maxSavePostSize;
    }


    /**
     * Set the maximum size of a POST which will be saved by the container during authentication.
     *
     * @param maxSavePostSize The new maximum size in bytes of a POST which will be saved by the container during
     *                        authentication.
     */
    public void setMaxSavePostSize(int maxSavePostSize) {
        this.maxSavePostSize = maxSavePostSize;
        setProperty("maxSavePostSize", String.valueOf(maxSavePostSize));
    }


    /**
     * @return the HTTP methods which will support body parameters parsing
     */
    public String getParseBodyMethods() {
        return this.parseBodyMethods;
    }


    /**
     * Set list of HTTP methods which should allow body parameter parsing. This defaults to <code>POST</code>.
     * <p>
     * 设置哪些 HTTP 方法允许解析请求体中的参数
     *
     * @param methods Comma separated list of HTTP method names
     */
    public void setParseBodyMethods(String methods) {

        HashSet<String> methodSet = new HashSet<>();

        if (null != methods) {
            methodSet.addAll(Arrays.asList(methods.split("\\s*,\\s*")));
        }
        // 如果 methodSet 包含 "TRACE" 方法，方法会抛出 IllegalArgumentException 异常。
        // 这是因为出于安全原因，通常不允许在 TRACE 请求中解析请求体。
        if (methodSet.contains("TRACE")) {
            throw new IllegalArgumentException(sm.getString("coyoteConnector.parseBodyMethodNoTrace"));
        }

        this.parseBodyMethods = methods;
        this.parseBodyMethodsSet = methodSet;
    }


    protected boolean isParseBodyMethod(String method) {
        return parseBodyMethodsSet.contains(method);
    }


    /**
     * @return the port number on which this connector is configured to listen for requests. The special value of 0
     * means select a random free port when the socket is bound.
     * <p>
     * 翻译：
     * 方法的注释指出，它返回连接器配置的端口号。如果返回值为0，表示当套接字绑定时，将选择一个随机的空闲端口。
     * <p>
     * 用于获取当前连接器（Connector）配置的端口号。
     * <p>
     * getPort 方法用于获取网络连接器配置的端口号。该方法首先尝试不使用反射的快速途径，如果失败，再尝试使用反射。如果这两种方法都失败，
     * 可能是由于配置了无效的协议，此时方法返回 -1。
     * 这种实现方式提供了灵活性，可以处理基于 AbstractProtocol 和非 AbstractProtocol 的自定义协议处理器。
     */
    public int getPort() {
        /* 1. 快速检查常用协议处理器 */
        // 首先，方法尝试一个快速途径来获取端口号，这适用于大多数情况，因为它不使用反射，因此更快。
        // Try shortcut that should work for nearly all uses first as it does
        // not use reflection and is therefore faster.
        // 检查 protocolHandler（一种处理网络协议的对象）是否是 AbstractProtocol 类的一个实例。
        // 如果是，直接从这个 AbstractProtocol 实例调用 getPort 方法来获取端口号。
        if (protocolHandler instanceof AbstractProtocol<?>) {
            return ((AbstractProtocol<?>) protocolHandler).getPort();
        }
        /* 2. 后备方案 */
        // Fall back for custom protocol handlers not based on AbstractProtocol
        // 如果 protocolHandler 不是基于 AbstractProtocol 的，方法转而使用反射（通过调用 getProperty("port")）来获取端口号。
        Object port = getProperty("port");
        if (port instanceof Integer) {
            // 检查通过反射获取的 port 是否为 Integer 类型的实例。如果是，将其转换为 int 类型并返回。
            return ((Integer) port).intValue();
        }
        // Usually means an invalid protocol has been configured
        // 如果以上步骤都失败（通常意味着配置了无效的协议），方法返回 -1 作为错误指示。
        return -1;
    }


    /**
     * Set the port number on which we listen for requests.
     *
     * @param port The new port number
     */
    public void setPort(int port) {
        setProperty("port", String.valueOf(port));
    }

    /**
     * 获取与当前连接器（Connector）关联的协议处理器的端口偏移量
     *
     * @return
     */
    public int getPortOffset() {
        /* 1. 快速路径（不使用反射） */
        // 如果 protocolHandler 是 AbstractProtocol 的实例，那么直接从这个实例调用 getPortOffset 方法来获取端口偏移量。
        // 这是一种不需要使用反射的直接方法调用，通常更快。
        // Try shortcut that should work for nearly all uses first as it does
        // not use reflection and is therefore faster.
        if (protocolHandler instanceof AbstractProtocol<?>) {
            return ((AbstractProtocol<?>) protocolHandler).getPortOffset();
        }
        /* 2. 后备方案（使用反射） */
        // 如果 protocolHandler 不是 AbstractProtocol 的实例，则使用反射来尝试获取端口偏移量。
        // Fall back for custom protocol handlers not based on AbstractProtocol
        Object port = getProperty("portOffset");
        if (port instanceof Integer) {
            return ((Integer) port).intValue();
        }
        // 如果以上步骤都失败（通常表示配置了无效的协议），方法返回 0。这意味着没有有效的端口偏移量或者默认的偏移量为0。
        // Usually means an invalid protocol has been configured.
        return 0;
    }


    public void setPortOffset(int portOffset) {
        setProperty("portOffset", String.valueOf(portOffset));
    }


    public int getPortWithOffset() {
        int port = getPort();
        // Zero is a special case and negative values are invalid
        if (port > 0) {
            return port + getPortOffset();
        }
        return port;
    }


    /**
     * @return the port number on which this connector is listening to requests. If the special value for
     * {@link #getPort} of zero is used then this method will report the actual port bound.
     */
    public int getLocalPort() {
//        return ((Integer) getProperty("localPort")).intValue();
        throw new UnsupportedOperationException();
    }


    /**
     * @return the Coyote protocol handler in use.
     */
    public String getProtocol() {
        return configuredProtocol;
    }


    /**
     * @return the class name of the Coyote protocol handler in use.
     */
    public String getProtocolHandlerClassName() {
        return this.protocolHandlerClassName;
    }


    /**
     * @return the protocol handler associated with the connector.
     */
    public ProtocolHandler getProtocolHandler() {
        return this.protocolHandler;
    }


    /**
     * @return the proxy server name for this Connector.
     */
    public String getProxyName() {
        return this.proxyName;
    }


    /**
     * Set the proxy server name for this Connector.
     *
     * @param proxyName The new proxy server name
     */
    public void setProxyName(String proxyName) {

        if (proxyName != null && proxyName.length() > 0) {
            this.proxyName = proxyName;
        } else {
            this.proxyName = null;
        }
    }


    /**
     * @return the proxy server port for this Connector.
     */
    public int getProxyPort() {
        return this.proxyPort;
    }


    /**
     * Set the proxy server port for this Connector.
     *
     * @param proxyPort The new proxy server port
     */
    public void setProxyPort(int proxyPort) {
        this.proxyPort = proxyPort;
    }


    /**
     * @return the port number to which a request should be redirected if it comes in on a non-SSL port and is subject
     * to a security constraint with a transport guarantee that requires SSL.
     */
    public int getRedirectPort() {
        return this.redirectPort;
    }


    /**
     * Set the redirect port number.
     *
     * @param redirectPort The redirect port number (non-SSL to SSL)
     */
    public void setRedirectPort(int redirectPort) {
        this.redirectPort = redirectPort;
    }


    public int getRedirectPortWithOffset() {
        return getRedirectPort() + getPortOffset();
    }


    /**
     * @return the scheme that will be assigned to requests received through this connector. Default value is "http".
     */
    public String getScheme() {
        return this.scheme;
    }


    /**
     * Set the scheme that will be assigned to requests received through this connector.
     *
     * @param scheme The new scheme
     */
    public void setScheme(String scheme) {
        this.scheme = scheme;
    }


    /**
     * @return the secure connection flag that will be assigned to requests received through this connector. Default
     * value is "false".
     */
    public boolean getSecure() {
        return this.secure;
    }


    /**
     * Set the secure connection flag that will be assigned to requests received through this connector.
     *
     * @param secure The new secure connection flag
     */
    public void setSecure(boolean secure) {
        this.secure = secure;
        setProperty("secure", Boolean.toString(secure));
    }


    /**
     * @return the name of character encoding to be used for the URI using the original case.
     */
    public String getURIEncoding() {
        return uriCharset.name();
    }


    /**
     * @return The Charset to use to convert raw URI bytes (after %nn decoding) to characters. This will never be null
     */
    public Charset getURICharset() {
        return uriCharset;
    }


    /**
     * Set the URI encoding to be used for the URI.
     * 用来设置URI编码
     * 这通常是Web服务器和应用服务器中处理请求的一个重要部分。
     * URI（统一资源标识符）编码用于确保传输过程中的特殊字符被正确处理。
     * <p>
     * 参数：String URIEncoding - 这是要设置的新URI编码，例如 "UTF-8"。
     * 目的：该方法旨在将服务器用于解析请求URI的字符集更改为指定的编码。
     *
     * @param URIEncoding The new URI character encoding.
     */
    // 传入的 URIEncoding 是 getUriEncoding().name()=“UTF-8”(暂时调用)
    public void setURIEncoding(String URIEncoding) {
        try {
            Charset charset = B2CConverter.getCharset(URIEncoding);
            // 检查得到的字符集是否是ASCII的超集。这通常是必需的，因为HTTP协议基于ASCII，确保字符集兼容ASCII有助于防止解析错误。
            if (!CharsetUtil.isAsciiSuperset(charset)) {
                log.error(sm.getString("coyoteConnector.notAsciiSuperset", URIEncoding, uriCharset.name()));
                return;
            }
            uriCharset = charset;
        } catch (UnsupportedEncodingException e) {
            // 如果 UnsupportedEncodingException 异常被抛出（表示提供的编码名称无效或不受支持），则记录一个错误。
            log.error(sm.getString("coyoteConnector.invalidencoding", URIEncoding, uriCharset.name()), e);
        }
    }


    /**
     * @return the true if the entity body encoding should be used for the URI.
     */
    public boolean getUseBodyEncodingForURI() {
        return this.useBodyEncodingForURI;
    }


    /**
     * Set if the entity body encoding should be used for the URI.
     *
     * @param useBodyEncodingForURI The new value for the flag.
     */
    public void setUseBodyEncodingForURI(boolean useBodyEncodingForURI) {
        this.useBodyEncodingForURI = useBodyEncodingForURI;
    }

    /**
     * Indicates whether the generation of an X-Powered-By response header for Servlet-generated responses is enabled or
     * disabled for this Connector.
     *
     * @return <code>true</code> if generation of X-Powered-By response header is enabled, false otherwise
     */
    public boolean getXpoweredBy() {
        return xpoweredBy;
    }


    /**
     * Enables or disables the generation of an X-Powered-By header (with value Servlet/2.5) for all servlet-generated
     * responses returned by this Connector.
     *
     * @param xpoweredBy true if generation of X-Powered-By response header is to be enabled, false otherwise
     */
    public void setXpoweredBy(boolean xpoweredBy) {
        this.xpoweredBy = xpoweredBy;
    }


    /**
     * Enable the use of IP-based virtual hosting.
     *
     * @param useIPVHosts <code>true</code> if Hosts are identified by IP, <code>false</code> if Hosts are identified by
     *                    name.
     */
    public void setUseIPVHosts(boolean useIPVHosts) {
        this.useIPVHosts = useIPVHosts;
    }


    /**
     * Test if IP-based virtual hosting is enabled.
     *
     * @return <code>true</code> if IP vhosts are enabled
     */
    public boolean getUseIPVHosts() {
        return useIPVHosts;
    }


    public String getExecutorName() {
        Object obj = protocolHandler.getExecutor();
        if (obj instanceof org.apache.catalina.Executor) {
            return ((org.apache.catalina.Executor) obj).getName();
        }
        return INTERNAL_EXECUTOR_NAME;
    }


    public void addSslHostConfig(SSLHostConfig sslHostConfig) {
        protocolHandler.addSslHostConfig(sslHostConfig);
    }


    public SSLHostConfig[] findSslHostConfigs() {
        return protocolHandler.findSslHostConfigs();
    }


    public void addUpgradeProtocol(UpgradeProtocol upgradeProtocol) {
        protocolHandler.addUpgradeProtocol(upgradeProtocol);
    }


    public UpgradeProtocol[] findUpgradeProtocols() {
        return protocolHandler.findUpgradeProtocols();
    }


    public String getEncodedSolidusHandling() {
        return encodedSolidusHandling.getValue();
    }


    public void setEncodedSolidusHandling(String encodedSolidusHandling) {
        this.encodedSolidusHandling = EncodedSolidusHandling.fromString(encodedSolidusHandling);
    }


    public EncodedSolidusHandling getEncodedSolidusHandlingInternal() {
        return encodedSolidusHandling;
    }


    public boolean getRejectSuspiciousURIs() {
        return rejectSuspiciousURIs;
    }


    public void setRejectSuspiciousURIs(boolean rejectSuspiciousURIs) {
        this.rejectSuspiciousURIs = rejectSuspiciousURIs;
    }

    /*9999999999999*/

    // --------------------------------------------------------- Public Methods

    /**
     * Create (or allocate) and return a Request object suitable for specifying the contents of a Request to the
     * responsible Container.
     *
     * @return a new Servlet request object
     */
    public Request createRequest() {
        return new Request(this);
    }


    /**
     * Create (or allocate) and return a Response object suitable for receiving the contents of a Response from the
     * responsible Container.
     *
     * @return a new Servlet response object
     */
    public Response createResponse() {
        int size = protocolHandler.getDesiredBufferSize();
        if (size > 0) {
            return new Response(size);
        } else {
            return new Response();
        }
    }


    protected String createObjectNameKeyProperties(String type) {

        Object addressObj = getProperty("address");

        StringBuilder sb = new StringBuilder("type=");
        sb.append(type);
        String id = (protocolHandler != null) ? protocolHandler.getId() : null;
        if (id != null) {
            // Maintain MBean name compatibility, even if not accurate
            sb.append(",port=0,address=");
            sb.append(ObjectName.quote(id));
        } else {
            sb.append(",port=");
            int port = getPortWithOffset();
            if (port > 0) {
                sb.append(port);
            } else {
                sb.append("auto-");
                sb.append(getProperty("nameIndex"));
            }
            String address = "";
            if (addressObj instanceof InetAddress) {
                address = ((InetAddress) addressObj).getHostAddress();
            } else if (addressObj != null) {
                address = addressObj.toString();
            }
            if (address.length() > 0) {
                sb.append(",address=");
                sb.append(ObjectName.quote(address));
            }
        }
        return sb.toString();
    }


    /**
     * Pause the connector.
     */
    public void pause() {
//        try {
//            if (protocolHandler != null) {
//                protocolHandler.pause();
//            }
//        } catch (Exception e) {
//            log.error(sm.getString("coyoteConnector.protocolHandlerPauseFailed"), e);
//        }
        throw new UnsupportedOperationException();
    }


    /**
     * Resume the connector.
     */
    public void resume() {
//        try {
//            if (protocolHandler != null) {
//                protocolHandler.resume();
//            }
//        } catch (Exception e) {
//            log.error(sm.getString("coyoteConnector.protocolHandlerResumeFailed"), e);
//        }
        throw new UnsupportedOperationException();
    }


    @Override
    protected void initInternal() throws LifecycleException {
        // super.initInternal() 调用了父类的 initInternal 方法，进行基础的初始化操作
        super.initInternal();
        /* 2. 检查协议处理器 */
        if (protocolHandler == null) {
            throw new LifecycleException(sm.getString("coyoteConnector.protocolHandlerInstantiationFailed"));
        }
        /* 3. 初始化适配器 */
        // Initialize adapter (adapter:适配器)
        // 创建一个 CoyoteAdapter 实例并将其分配给 adapter 成员变量。然后设置 protocolHandler 的适配器为此 adapter。
        // CoyoteAdapter 这个类负责处理 HTTP 请求和响应
        adapter = new CoyoteAdapter(this);
        protocolHandler.setAdapter(adapter);
        /* 4. 设置解析正文方法的默认值 */
        // 如果 parseBodyMethodsSet 为 null，调用 setParseBodyMethods(getParseBodyMethods()) 来设置默认的 HTTP 方法，
        // 这些方法在接收请求时将解析请求体
        /* 这个检查是为了确定是否已经有一个指定的方法集合。
        如果 parseBodyMethodsSet 不为 null，意味着已经有一个预定义的方法集合，无需再设置。
        如果 parseBodyMethodsSet 为 null，则需要设置默认的方法集合。
        */
        // Make sure parseBodyMethodsSet has a default
        if (null == parseBodyMethodsSet) {
            setParseBodyMethods(getParseBodyMethods());
        }
        /* 5. 配置 SSL 实现（如果使用 APR 和 OpenSSL） */
        // 如果 APR 可用并且配置了使用 OpenSSL，且 protocolHandler 是 AbstractHttp11JsseProtocol 的实例，
        // 方法将检查并可能设置 SSL 实现。
        /* APR（Apache Portable Runtime）是一组 C 语言库，旨在提供一个平台无关的 API，使得 Apache HTTP 服务器等软件能在
        各种操作系统上运行。在 Tomcat 中，APR 用于提供更高效的网络通信和 SSL 加密处理。*/
        if (AprStatus.isAprAvailable() && AprStatus.getUseOpenSSL() &&
                protocolHandler instanceof AbstractHttp11JsseProtocol) {
            AbstractHttp11JsseProtocol<?> jsseProtocolHandler = (AbstractHttp11JsseProtocol<?>) protocolHandler;
            // 如果启用了 SSL 但没有指定 SSL 实现名称，则将其设置为 OpenSSLImplementation。
            /* jsseProtocolHandler 是 AbstractHttp11JsseProtocol 类型的实例，它处理基于 JSSE
            （Java Secure Socket Extension）的 SSL/TLS 加密网络通信。它为 HTTP/1.1 协议提供了安全的网络传输功能*/
            if (jsseProtocolHandler.isSSLEnabled() && jsseProtocolHandler.getSslImplementationName() == null) {
                // OpenSSL is compatible with the JSSE configuration, so use it if APR is available
                jsseProtocolHandler.setSslImplementationName(OpenSSLImplementation.class.getName());
            }
        }

        try {
            protocolHandler.init();
        } catch (Exception e) {
            throw new LifecycleException(sm.getString("coyoteConnector.protocolHandlerInitializationFailed"), e);
        }
    }


    /**
     * Begin processing requests via this Connector.
     *
     * @throws LifecycleException if a fatal startup error occurs
     */
    @Override
    protected void startInternal() throws LifecycleException {

//        // Validate settings before starting
//        String id = (protocolHandler != null) ? protocolHandler.getId() : null;
//        if (id == null && getPortWithOffset() < 0) {
//            throw new LifecycleException(
//                    sm.getString("coyoteConnector.invalidPort", Integer.valueOf(getPortWithOffset())));
//        }
//
//        setState(LifecycleState.STARTING);
//
//        // Configure the utility executor before starting the protocol handler
//        if (protocolHandler != null && service != null) {
//            protocolHandler.setUtilityExecutor(service.getServer().getUtilityExecutor());
//        }
//
//        try {
//            protocolHandler.start();
//        } catch (Exception e) {
//            // Includes NPE - protocolHandler will be null for invalid protocol if throwOnFailure is false
//            throw new LifecycleException(sm.getString("coyoteConnector.protocolHandlerStartFailed"), e);
//        }
        throw new UnsupportedOperationException();
    }


    /**
     * Terminate processing requests via this Connector.
     *
     * @throws LifecycleException if a fatal shutdown error occurs
     */
    @Override
    protected void stopInternal() throws LifecycleException {

//        setState(LifecycleState.STOPPING);
//
//        try {
//            if (protocolHandler != null) {
//                protocolHandler.stop();
//            }
//        } catch (Exception e) {
//            throw new LifecycleException(sm.getString("coyoteConnector.protocolHandlerStopFailed"), e);
//        }
//
//        // Remove the utility executor once the protocol handler has been stopped
//        if (protocolHandler != null) {
//            protocolHandler.setUtilityExecutor(null);
//        }
        throw new UnsupportedOperationException();
    }


    @Override
    protected void destroyInternal() throws LifecycleException {
//        try {
//            if (protocolHandler != null) {
//                protocolHandler.destroy();
//            }
//        } catch (Exception e) {
//            throw new LifecycleException(sm.getString("coyoteConnector.protocolHandlerDestroyFailed"), e);
//        }
//
//        if (getService() != null) {
//            getService().removeConnector(this);
//        }
//
//        super.destroyInternal();
        throw new UnsupportedOperationException();
    }


    /**
     * Provide a useful toString() implementation as it may be used when logging Lifecycle errors to identify the
     * component.
     */
    @Override
    public String toString() {
        // Not worth caching this right now
        StringBuilder sb = new StringBuilder("Connector[");
        String name = (String) getProperty("name");
        if (name == null) {
            sb.append(getProtocol());
            sb.append('-');
            String id = (protocolHandler != null) ? protocolHandler.getId() : null;
            if (id != null) {
                sb.append(id);
            } else {
                int port = getPortWithOffset();
                if (port > 0) {
                    sb.append(port);
                } else {
                    sb.append("auto-");
                    sb.append(getProperty("nameIndex"));
                }
            }
        } else {
            sb.append(name);
        }
        sb.append(']');
        return sb.toString();
    }


    // -------------------- JMX registration --------------------

    @Override
    protected String getDomainInternal() {
        Service s = getService();
        if (s == null) {
            return null;
        } else {
            return service.getDomain();
        }
    }

    @Override
    protected String getObjectNameKeyProperties() {
        return createObjectNameKeyProperties("Connector");
    }


}
