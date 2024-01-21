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

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.naming.NamingException;
import javax.security.auth.Subject;

import jakarta.servlet.AsyncContext;
import jakarta.servlet.DispatcherType;
import jakarta.servlet.FilterChain;
import jakarta.servlet.MultipartConfigElement;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletConnection;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletRequestAttributeEvent;
import jakarta.servlet.ServletRequestAttributeListener;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.SessionTrackingMode;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletMapping;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpUpgradeHandler;
import jakarta.servlet.http.Part;
import jakarta.servlet.http.PushBuilder;

import org.apache.catalina.Container;
import org.apache.catalina.Context;
import org.apache.catalina.Globals;
import org.apache.catalina.Host;
import org.apache.catalina.Manager;
import org.apache.catalina.Realm;
import org.apache.catalina.Session;
import org.apache.catalina.TomcatPrincipal;
import org.apache.catalina.Wrapper;
//import org.apache.catalina.core.ApplicationFilterChain;
//import org.apache.catalina.core.ApplicationMapping;
//import org.apache.catalina.core.ApplicationPart;
//import org.apache.catalina.core.ApplicationPushBuilder;
//import org.apache.catalina.core.ApplicationSessionCookieConfig;
//import org.apache.catalina.core.AsyncContextImpl;
//import org.apache.catalina.mapper.MappingData;
//import org.apache.catalina.util.ParameterMap;
//import org.apache.catalina.util.RequestUtil;
//import org.apache.catalina.util.TLSUtil;
import org.apache.catalina.core.AsyncContextImpl;
import org.apache.catalina.util.ParameterMap;
import org.apache.catalina.util.URLEncoder;
import org.apache.coyote.ActionCode;
import org.apache.coyote.UpgradeToken;
import org.apache.coyote.http11.upgrade.InternalHttpUpgradeHandler;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.InstanceManager;
import org.apache.tomcat.util.ExceptionUtils;
import org.apache.tomcat.util.buf.B2CConverter;
import org.apache.tomcat.util.buf.ByteChunk;
import org.apache.tomcat.util.buf.EncodedSolidusHandling;
//import org.apache.tomcat.util.buf.MessageBytes;
import org.apache.tomcat.util.buf.StringUtils;
import org.apache.tomcat.util.buf.UDecoder;
import org.apache.tomcat.util.http.CookieProcessor;
import org.apache.tomcat.util.http.FastHttpDateFormat;
//import org.apache.tomcat.util.http.Parameters;
//import org.apache.tomcat.util.http.Parameters.FailReason;
//import org.apache.tomcat.util.http.Rfc6265CookieProcessor;
//import org.apache.tomcat.util.http.ServerCookie;
import org.apache.tomcat.util.http.ServerCookies;
//import org.apache.tomcat.util.http.fileupload.FileItem;
//import org.apache.tomcat.util.http.fileupload.FileUpload;
//import org.apache.tomcat.util.http.fileupload.disk.DiskFileItemFactory;
//import org.apache.tomcat.util.http.fileupload.impl.InvalidContentTypeException;
//import org.apache.tomcat.util.http.fileupload.impl.SizeException;
//import org.apache.tomcat.util.http.fileupload.servlet.ServletRequestContext;
//import org.apache.tomcat.util.http.parser.AcceptLanguage;
//import org.apache.tomcat.util.http.parser.Upgrade;
import org.apache.tomcat.util.net.SSLSupport;
import org.apache.tomcat.util.res.StringManager;
import org.ietf.jgss.GSSCredential;
import org.ietf.jgss.GSSException;

/**
 * Wrapper object for the Coyote request.
 *
 * @author Remy Maucherat
 * @author Craig R. McClanahan
 */
public class Request/* implements HttpServletRequest*/ {

    private static final String HTTP_UPGRADE_HEADER_NAME = "upgrade";

    private static final Log log = LogFactory.getLog(Request.class);

    /**
     * Create a new Request object associated with the given Connector.
     *
     * @param connector The Connector with which this Request object will always be associated. In normal usage this
     *                      must be non-null. In some test scenarios, it may be possible to use a null Connector without
     *                      triggering an NPE.
     */
    public Request(Connector connector) {
        this.connector = connector;
    }


    // ------------------------------------------------------------- Properties


    /**
     * Coyote request.
     */
    protected org.apache.coyote.Request coyoteRequest;

    /**
     * Set the Coyote request.
     *
     * @param coyoteRequest The Coyote request
     */
    public void setCoyoteRequest(org.apache.coyote.Request coyoteRequest) {
        this.coyoteRequest = coyoteRequest;
        inputBuffer.setRequest(coyoteRequest);
    }

    /**
     * Get the Coyote request.
     *
     * @return the Coyote request object
     */
    public org.apache.coyote.Request getCoyoteRequest() {
        return this.coyoteRequest;
    }


    // ----------------------------------------------------- Variables

    /**
     * The string manager for this package.
     */
    protected static final StringManager sm = StringManager.getManager(Request.class);


    /**
     * The set of cookies associated with this Request.
     */
    protected Cookie[] cookies = null;


    /**
     * The default Locale if none are specified.
     */
    protected static final Locale defaultLocale = Locale.getDefault();


    /**
     * The attributes associated with this Request, keyed by attribute name.
     */
    private final Map<String,Object> attributes = new ConcurrentHashMap<>();


    /**
     * Flag that indicates if SSL attributes have been parsed to improve performance for applications (usually
     * frameworks) that make multiple calls to {@link Request#getAttributeNames()}.
     */
    protected boolean sslAttributesParsed = false;


    /**
     * The preferred Locales associated with this Request.
     */
    protected final ArrayList<Locale> locales = new ArrayList<>();


    /**
     * Internal notes associated with this request by Catalina components and event listeners.
     */
    private final transient HashMap<String,Object> notes = new HashMap<>();


    /**
     * Authentication type.
     */
    protected String authType = null;


    /**
     * The current dispatcher type.
     */
    protected DispatcherType internalDispatcherType = null;


    /**
     * The associated input buffer.
     */
    protected final InputBuffer inputBuffer = new InputBuffer();


    /**
     * ServletInputStream.
     */
    protected CoyoteInputStream inputStream = new CoyoteInputStream(inputBuffer);


    /**
     * Reader.
     */
    protected CoyoteReader reader = new CoyoteReader(inputBuffer);


    /**
     * Using stream flag.
     */
    protected boolean usingInputStream = false;


    /**
     * Using reader flag.
     */
    protected boolean usingReader = false;


    /**
     * User principal.
     */
    protected Principal userPrincipal = null;


    /**
     * Request parameters parsed flag.
     */
    protected boolean parametersParsed = false;


    /**
     * Cookie headers parsed flag. Indicates that the cookie headers have been parsed into ServerCookies.
     */
    protected boolean cookiesParsed = false;


    /**
     * Cookie parsed flag. Indicates that the ServerCookies have been converted into user facing Cookie objects.
     */
    protected boolean cookiesConverted = false;


    /**
     * Secure flag.
     */
    protected boolean secure = false;


    /**
     * The Subject associated with the current AccessControlContext
     */
    protected transient Subject subject = null;


    /**
     * Post data buffer.
     */
    protected static final int CACHED_POST_LEN = 8192;
    protected byte[] postData = null;


    /**
     * Hash map used in the getParametersMap method.
     */
    protected ParameterMap<String,String[]> parameterMap = new ParameterMap<>();


    /**
     * The parts, if any, uploaded with this request.
     */
    protected Collection<Part> parts = null;


    /**
     * The exception thrown, if any when parsing the parts.
     */
    protected Exception partsParseException = null;


    /**
     * The currently active session for this request.
     */
    protected Session session = null;


    /**
     * The current request dispatcher path.
     */
    protected Object requestDispatcherPath = null;


    /**
     * Was the requested session ID received in a cookie?
     */
    protected boolean requestedSessionCookie = false;


    /**
     * The requested session ID (if any) for this request.
     */
    protected String requestedSessionId = null;


    /**
     * Was the requested session ID received in a URL?
     */
    protected boolean requestedSessionURL = false;


    /**
     * Was the requested session ID obtained from the SSL session?
     */
    protected boolean requestedSessionSSL = false;


    /**
     * Parse locales.
     */
    protected boolean localesParsed = false;


    /**
     * Local port
     */
    protected int localPort = -1;

    /**
     * Remote address.
     */
    protected String remoteAddr = null;


    /**
     * Connection peer address.
     */
    protected String peerAddr = null;


    /**
     * Remote host.
     */
    protected String remoteHost = null;


    /**
     * Remote port
     */
    protected int remotePort = -1;

    /**
     * Local address
     */
    protected String localAddr = null;


    /**
     * Local address
     */
    protected String localName = null;

    /**
     * AsyncContext
     */
    private volatile AsyncContextImpl asyncContext = null;

    protected Boolean asyncSupported = null;

    private HttpServletRequest applicationRequest = null;


    // --------------------------------------------------------- Public Methods




    // -------------------------------------------------------- Request Methods

    /**
     * Associated Catalina connector.
     */
    protected final Connector connector;


}
