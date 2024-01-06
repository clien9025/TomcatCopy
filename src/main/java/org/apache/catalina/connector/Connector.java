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

import org.apache.catalina.Service;
import org.apache.catalina.util.LifecycleMBeanBase;
import org.apache.coyote.AbstractProtocol;
import org.apache.coyote.ProtocolHandler;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.IntrospectionUtils;
import org.apache.tomcat.util.buf.B2CConverter;
import org.apache.tomcat.util.buf.CharsetUtil;
import org.apache.tomcat.util.res.StringManager;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;


/**
 * Implementation of a Coyote connector.
 *
 * @author Craig R. McClanahan
 * @author Remy Maucherat
 */
public class Connector extends LifecycleMBeanBase {



    // ----------------------------------------------------- Instance Variables

    /**
     * The <code>Service</code> we are associated with (if any).
     */
    protected Service service = null;

    private static final Log log = LogFactory.getLog(Connector.class);


    public static final String INTERNAL_EXECUTOR_NAME = "Internal";

    /**
     * Name of the protocol that was configured.
     */
    protected final String configuredProtocol;

    /**
     * The string manager for this package.
     */
    protected static final StringManager sm = StringManager.getManager(Connector.class);

    /**
     * Coyote protocol handler.
     */
    protected final ProtocolHandler protocolHandler;

    /**
     * Coyote Protocol handler class name. See {@link #Connector()} for current default.
     */
    protected final String protocolHandlerClassName;

    /**
     * The URI encoding in use.
     */
    private Charset uriCharset = StandardCharsets.UTF_8;

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


    // 暂时调用
    /**
     * @return the port number on which this connector is configured to listen for requests. The special value of 0
     *             means select a random free port when the socket is bound.
     */
    public int getPort() {
//        // Try shortcut that should work for nearly all uses first as it does
//        // not use reflection and is therefore faster.
//        if (protocolHandler instanceof AbstractProtocol<?>) {
//            return ((AbstractProtocol<?>) protocolHandler).getPort();
//        }
//        // Fall back for custom protocol handlers not based on AbstractProtocol
//        Object port = getProperty("port");
//        if (port instanceof Integer) {
//            return ((Integer) port).intValue();
//        }
//        // Usually means an invalid protocol has been configured
//        return -1;
        throw new UnsupportedOperationException();
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
     * Set a property on the protocol handler.
     *
     * @param name  the property name
     * @param value the property value
     *
     * @return <code>true</code> if the property was successfully set
     */
    public boolean setProperty(String name, String value) {
        if (protocolHandler == null) {
            return false;
        }
        return IntrospectionUtils.setProperty(protocolHandler, name, value);
    }

    // 暂时有用
    /**
     * @return the protocol handler associated with the connector.
     */
    public ProtocolHandler getProtocolHandler() {
        return this.protocolHandler;
    }


    /**
     * @return the port number on which this connector is listening to requests. If the special value for
     *             {@link #getPort} of zero is used then this method will report the actual port bound.
     */
    public int getLocalPort() {
//        return ((Integer) getProperty("localPort")).intValue();
        throw new UnsupportedOperationException();
    }


    /**
     * @return the scheme that will be assigned to requests received through this connector. Default value is "http".
     */
    public String getScheme() {
//        return this.scheme;
        throw new UnsupportedOperationException();
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
     * Set the URI encoding to be used for the URI.
     * 用来设置URI编码
     * 这通常是Web服务器和应用服务器中处理请求的一个重要部分。
     * URI（统一资源标识符）编码用于确保传输过程中的特殊字符被正确处理。
     *
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



}
