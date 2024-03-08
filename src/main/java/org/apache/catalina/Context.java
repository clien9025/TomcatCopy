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
package org.apache.catalina;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import jakarta.servlet.ServletContainerInitializer;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletRegistration;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletSecurityElement;
import jakarta.servlet.descriptor.JspConfigDescriptor;

import org.apache.catalina.deploy.NamingResourcesImpl;
import org.apache.tomcat.ContextBind;
import org.apache.tomcat.InstanceManager;
import org.apache.tomcat.JarScanner;
import org.apache.tomcat.util.descriptor.web.ApplicationParameter;
import org.apache.tomcat.util.descriptor.web.ErrorPage;
import org.apache.tomcat.util.descriptor.web.FilterDef;
import org.apache.tomcat.util.descriptor.web.FilterMap;
import org.apache.tomcat.util.descriptor.web.LoginConfig;
import org.apache.tomcat.util.descriptor.web.SecurityConstraint;
import org.apache.tomcat.util.file.ConfigFileLoader;
import org.apache.tomcat.util.file.ConfigurationSource.Resource;
import org.apache.tomcat.util.http.CookieProcessor;

/**
 * A <b>Context</b> is a Container that represents a servlet context, and
 * therefore an individual web application, in the Catalina servlet engine.
 * It is therefore useful in almost every deployment of Catalina (even if a
 * Connector attached to a web server (such as Apache) uses the web server's
 * facilities to identify the appropriate Wrapper to handle this request.
 * It also provides a convenient mechanism to use Interceptors that see
 * every request processed by this particular web application.
 * <p>
 * The parent Container attached to a Context is generally a Host, but may
 * be some other implementation, or may be omitted if it is not necessary.
 * <p>
 * The child containers attached to a Context are generally implementations
 * of Wrapper (representing individual servlet definitions).
 * <p>
 *
 * @author Craig R. McClanahan
 */
public interface Context extends Container, ContextBind {


    // ----------------------------------------------------- Manifest Constants

    /**
     * Container event for adding a welcome file.
     */
    String ADD_WELCOME_FILE_EVENT = "addWelcomeFile";

    /**
     * Container event for removing a wrapper.
     */
    String REMOVE_WELCOME_FILE_EVENT = "removeWelcomeFile";

    /**
     * Container event for clearing welcome files.
     */
    String  CLEAR_WELCOME_FILES_EVENT = "clearWelcomeFiles";

    /**
     * Container event for changing the ID of a session.
     */
    String CHANGE_SESSION_ID_EVENT = "changeSessionId";


    /**
     * Prefix for resource lookup.
     */
    String WEBAPP_PROTOCOL = "webapp:";


    // ------------------------------------------------------------- Properties

    /**
     * Returns <code>true</code> if requests mapped to servlets without
     * "multipart config" to parse multipart/form-data requests anyway.
     *
     * @return <code>true</code> if requests mapped to servlets without
     *    "multipart config" to parse multipart/form-data requests,
     *    <code>false</code> otherwise.
     */
    default boolean getAllowCasualMultipartParsing() {
        throw new UnsupportedOperationException();
    }


    /**
     * Set to <code>true</code> to allow requests mapped to servlets that
     * do not explicitly declare @MultipartConfig or have
     * &lt;multipart-config&gt; specified in web.xml to parse
     * multipart/form-data requests.
     *
     * @param allowCasualMultipartParsing <code>true</code> to allow such
     *        casual parsing, <code>false</code> otherwise.
     */
    default void setAllowCasualMultipartParsing(boolean allowCasualMultipartParsing) {
        throw new UnsupportedOperationException();
    }


    /**
     * Obtain the registered application event listeners.
     *
     * @return An array containing the application event listener instances for
     *         this web application in the order they were specified in the web
     *         application deployment descriptor
     */
    default Object[] getApplicationEventListeners() {
        throw new UnsupportedOperationException();
    }


    /**
     * Store the set of initialized application event listener objects,
     * in the order they were specified in the web application deployment
     * descriptor, for this application.
     *
     * @param listeners The set of instantiated listener objects.
     */
    default void setApplicationEventListeners(Object listeners[]) {
        throw new UnsupportedOperationException();
    }


    /**
     * Obtain the registered application lifecycle listeners.
     *
     * @return An array containing the application lifecycle listener instances
     * for this web application in the order they were specified in the
     * web application deployment descriptor
     */
    default Object[] getApplicationLifecycleListeners() {
        throw new UnsupportedOperationException();
    }


    /**
     * Store the set of initialized application lifecycle listener objects,
     * in the order they were specified in the web application deployment
     * descriptor, for this application.
     *
     * @param listeners The set of instantiated listener objects.
     */
    default void setApplicationLifecycleListeners(Object listeners[]) {
        throw new UnsupportedOperationException();
    }


    /**
     * Obtain the character set name to use with the given Locale. Note that
     * different Contexts may have different mappings of Locale to character
     * set.
     *
     * @param locale The locale for which the mapped character set should be
     *               returned
     * @return The name of the character set to use with the given Locale
     */
    default String getCharset(Locale locale) {
        throw new UnsupportedOperationException();
    }


    /**
     * Return the URL of the XML descriptor for this context.
     *
     * @return The URL of the XML descriptor for this context
     */
    default URL getConfigFile() {
        throw new UnsupportedOperationException();
    }


    /**
     * Set the URL of the XML descriptor for this context.
     *
     * @param configFile The URL of the XML descriptor for this context.
     */
    default void setConfigFile(URL configFile) {
        throw new UnsupportedOperationException();
    }


    /**
     * Return the "correctly configured" flag for this Context.
     *
     * @return <code>true</code> if the Context has been correctly configured,
     * otherwise <code>false</code>
     */
    default boolean getConfigured() {
        throw new UnsupportedOperationException();
    }


    /**
     * Set the "correctly configured" flag for this Context.  This can be
     * set to false by startup listeners that detect a fatal configuration
     * error to avoid the application from being made available.
     *
     * @param configured The new correctly configured flag
     */
    default void setConfigured(boolean configured) {
        throw new UnsupportedOperationException();
    }


    /**
     * Return the "use cookies for session ids" flag.
     *
     * @return <code>true</code> if it is permitted to use cookies to track
     * session IDs for this web application, otherwise
     * <code>false</code>
     */
    default boolean getCookies() {
        throw new UnsupportedOperationException();
    }


    /**
     * Set the "use cookies for session ids" flag.
     *
     * @param cookies The new flag
     */
    default void setCookies(boolean cookies) {
        throw new UnsupportedOperationException();
    }


    /**
     * Gets the name to use for session cookies. Overrides any setting that
     * may be specified by the application.
     *
     * @return The value of the default session cookie name or null if not
     * specified
     */
    default String getSessionCookieName() {
        throw new UnsupportedOperationException();
    }


    /**
     * Sets the name to use for session cookies. Overrides any setting that
     * may be specified by the application.
     *
     * @param sessionCookieName   The name to use
     */
    default void setSessionCookieName(String sessionCookieName) {
        throw new UnsupportedOperationException();
    }


    /**
     * Gets the value of the use HttpOnly cookies for session cookies flag.
     *
     * @return <code>true</code> if the HttpOnly flag should be set on session
     * cookies
     */
    default boolean getUseHttpOnly() {
        throw new UnsupportedOperationException();
    }


    /**
     * Sets the use HttpOnly cookies for session cookies flag.
     *
     * @param useHttpOnly   Set to <code>true</code> to use HttpOnly cookies
     *                          for session cookies
     */
    default void setUseHttpOnly(boolean useHttpOnly) {
        throw new UnsupportedOperationException();
    }


    /**
     * Gets the domain to use for session cookies. Overrides any setting that
     * may be specified by the application.
     *
     * @return The value of the default session cookie domain or null if not
     * specified
     */
    default String getSessionCookieDomain() {
        throw new UnsupportedOperationException();
    }


    /**
     * Sets the domain to use for session cookies. Overrides any setting that
     * may be specified by the application.
     *
     * @param sessionCookieDomain   The domain to use
     */
    default void setSessionCookieDomain(String sessionCookieDomain) {
        throw new UnsupportedOperationException();
    }


    /**
     * Gets the path to use for session cookies. Overrides any setting that
     * may be specified by the application.
     *
     * @return The value of the default session cookie path or null if not
     * specified
     */
    default String getSessionCookiePath() {
        throw new UnsupportedOperationException();
    }


    /**
     * Sets the path to use for session cookies. Overrides any setting that
     * may be specified by the application.
     *
     * @param sessionCookiePath   The path to use
     */
    default void setSessionCookiePath(String sessionCookiePath) {
        throw new UnsupportedOperationException();
    }


    /**
     * Is a / added to the end of the session cookie path to ensure browsers,
     * particularly IE, don't send a session cookie for context /foo with
     * requests intended for context /foobar.
     *
     * @return <code>true</code> if the slash is added, otherwise
     * <code>false</code>
     */
    default boolean getSessionCookiePathUsesTrailingSlash() {
        throw new UnsupportedOperationException();
    }


    /**
     * Configures if a / is added to the end of the session cookie path to
     * ensure browsers, particularly IE, don't send a session cookie for context
     * /foo with requests intended for context /foobar.
     *
     * @param sessionCookiePathUsesTrailingSlash <code>true</code> if the
     *                                           slash is should be added,
     *                                           otherwise <code>false</code>
     */
    default void setSessionCookiePathUsesTrailingSlash(
            boolean sessionCookiePathUsesTrailingSlash) {
        throw new UnsupportedOperationException();
    }


    /**
     * Return the "allow crossing servlet contexts" flag.
     *
     * @return <code>true</code> if cross-contest requests are allowed from this
     * web applications, otherwise <code>false</code>
     */
    default boolean getCrossContext() {
        throw new UnsupportedOperationException();
    }


    /**
     * Return the alternate Deployment Descriptor name.
     *
     * @return the name
     */
    default String getAltDDName() {
        throw new UnsupportedOperationException();
    }


    /**
     * Set an alternate Deployment Descriptor name.
     *
     * @param altDDName The new name
     */
    default void setAltDDName(String altDDName) {
        throw new UnsupportedOperationException();
    } ;


    /**
     * Set the "allow crossing servlet contexts" flag.
     *
     * @param crossContext The new cross contexts flag
     */
    default void setCrossContext(boolean crossContext) {
        throw new UnsupportedOperationException();
    }


    /**
     * Return the deny-uncovered-http-methods flag for this web application.
     *
     * @return The current value of the flag
     */
    default boolean getDenyUncoveredHttpMethods() {
        throw new UnsupportedOperationException();
    }


    /**
     * Set the deny-uncovered-http-methods flag for this web application.
     *
     * @param denyUncoveredHttpMethods The new deny-uncovered-http-methods flag
     */
    default void setDenyUncoveredHttpMethods(boolean denyUncoveredHttpMethods) {
        throw new UnsupportedOperationException();
    }


    /**
     * Return the display name of this web application.
     *
     * @return The display name
     */
    default String getDisplayName() {
        throw new UnsupportedOperationException();
    }


    /**
     * Set the display name of this web application.
     *
     * @param displayName The new display name
     */
    default void setDisplayName(String displayName) {
        throw new UnsupportedOperationException();
    }


    /**
     * Get the distributable flag for this web application.
     *
     * @return The value of the distributable flag for this web application.
     */
    default boolean getDistributable() {
        throw new UnsupportedOperationException();
    }


    /**
     * Set the distributable flag for this web application.
     *
     * @param distributable The new distributable flag
     */
    default void setDistributable(boolean distributable) {
        throw new UnsupportedOperationException();
    }


    /**
     * Obtain the document root for this Context.
     *
     * @return An absolute pathname or a relative (to the Host's appBase)
     * pathname.
     */
    default String getDocBase() {
        throw new UnsupportedOperationException();
    }


    /**
     * Set the document root for this Context. This can be either an absolute
     * pathname or a relative pathname. Relative pathnames are relative to the
     * containing Host's appBase.
     *
     * @param docBase The new document root
     */
    default void setDocBase(String docBase) {
        throw new UnsupportedOperationException();
    }


    /**
     * Return the URL encoded context path
     *
     * @return The URL encoded (with UTF-8) context path
     */
    default String getEncodedPath() {
        throw new UnsupportedOperationException();
    }


    /**
     * Determine if annotations parsing is currently disabled
     *
     * @return {@code true} if annotation parsing is disabled for this web
     * application
     */
    default boolean getIgnoreAnnotations() {
        throw new UnsupportedOperationException();
    }


    /**
     * Set the boolean on the annotations parsing for this web
     * application.
     *
     * @param ignoreAnnotations The boolean on the annotations parsing
     */
    default void setIgnoreAnnotations(boolean ignoreAnnotations) {
        throw new UnsupportedOperationException();
    }


    /**
     * @return the login configuration descriptor for this web application.
     */
    default LoginConfig getLoginConfig() {
        throw new UnsupportedOperationException();
    }


    /**
     * Set the login configuration descriptor for this web application.
     *
     * @param config The new login configuration
     */
    default void setLoginConfig(LoginConfig config) {
        throw new UnsupportedOperationException();
    }


    /**
     * @return the naming resources associated with this web application.
     */
    default NamingResourcesImpl getNamingResources() {
        throw new UnsupportedOperationException();
    }


    /**
     * Set the naming resources for this web application.
     *
     * @param namingResources The new naming resources
     */
    default void setNamingResources(NamingResourcesImpl namingResources) {
        throw new UnsupportedOperationException();
    }


    /**
     * @return the context path for this web application.
     */
    default String getPath() {
        throw new UnsupportedOperationException();
    }


    /**
     * Set the context path for this web application.
     *
     * @param path The new context path
     */
    default void setPath(String path) {
        throw new UnsupportedOperationException();
    }


    /**
     * @return the public identifier of the deployment descriptor DTD that is
     * currently being parsed.
     */
    default String getPublicId() {
        throw new UnsupportedOperationException();
    }


    /**
     * Set the public identifier of the deployment descriptor DTD that is
     * currently being parsed.
     *
     * @param publicId The public identifier
     */
    default void setPublicId(String publicId) {
        throw new UnsupportedOperationException();
    }


    /**
     * @return the reloadable flag for this web application.
     */
    default boolean getReloadable() {
        throw new UnsupportedOperationException();
    }


    /**
     * Set the reloadable flag for this web application.
     *
     * @param reloadable The new reloadable flag
     */
    default void setReloadable(boolean reloadable) {
        throw new UnsupportedOperationException();
    }


    /**
     * @return the override flag for this web application.
     */
    default boolean getOverride() {
        throw new UnsupportedOperationException();
    }


    /**
     * Set the override flag for this web application.
     *
     * @param override The new override flag
     */
    default void setOverride(boolean override) {
        throw new UnsupportedOperationException();
    }


    /**
     * @return the privileged flag for this web application.
     */
    default boolean getPrivileged() {
        throw new UnsupportedOperationException();
    }


    /**
     * Set the privileged flag for this web application.
     *
     * @param privileged The new privileged flag
     */
    default void setPrivileged(boolean privileged) {
        throw new UnsupportedOperationException();
    }


    /**
     * @return the Servlet context for which this Context is a facade.
     */
    // 看到自己的 org.apache.catalina.core.StandardContext 类里面竟然没有
    // 重写 getServletContext() 这个方法，我觉得很奇怪，就来看看，原来在接口里面实现了(default)。
    ServletContext getServletContext();

    /**
     * @return the default session timeout (in minutes) for this
     * web application.
     */
    default int getSessionTimeout() {
        throw new UnsupportedOperationException();
    }


    /**
     * Set the default session timeout (in minutes) for this
     * web application.
     *
     * @param timeout The new default session timeout
     */
    default void setSessionTimeout(int timeout) {
        throw new UnsupportedOperationException();
    }


    /**
     * Returns <code>true</code> if remaining request data will be read
     * (swallowed) even the request violates a data size constraint.
     *
     * @return <code>true</code> if data will be swallowed (default),
     * <code>false</code> otherwise.
     */
    default boolean getSwallowAbortedUploads() {
        throw new UnsupportedOperationException();
    }


    /**
     * Set to <code>false</code> to disable request data swallowing
     * after an upload was aborted due to size constraints.
     *
     * @param swallowAbortedUploads <code>false</code> to disable
     *                              swallowing, <code>true</code> otherwise (default).
     */
    default void setSwallowAbortedUploads(boolean swallowAbortedUploads) {
        throw new UnsupportedOperationException();
    }

    /**
     * @return the value of the swallowOutput flag.
     */
    default boolean getSwallowOutput() {
        throw new UnsupportedOperationException();
    }


    /**
     * Set the value of the swallowOutput flag. If set to true, the system.out
     * and system.err will be redirected to the logger during a servlet
     * execution.
     *
     * @param swallowOutput The new value
     */
    default void setSwallowOutput(boolean swallowOutput) {
        throw new UnsupportedOperationException();
    }


    /**
     * @return the Java class name of the Wrapper implementation used
     * for servlets registered in this Context.
     */
    default String getWrapperClass() {
        throw new UnsupportedOperationException();
    }


    /**
     * Set the Java class name of the Wrapper implementation used
     * for servlets registered in this Context.
     *
     * @param wrapperClass The new wrapper class
     */
    default void setWrapperClass(String wrapperClass) {
        throw new UnsupportedOperationException();
    }


    /**
     * Will the parsing of web.xml and web-fragment.xml files for this Context
     * be performed by a namespace aware parser?
     *
     * @return true if namespace awareness is enabled.
     */
    default boolean getXmlNamespaceAware() {
        throw new UnsupportedOperationException();
    }


    /**
     * Controls whether the parsing of web.xml and web-fragment.xml files for
     * this Context will be performed by a namespace aware parser.
     *
     * @param xmlNamespaceAware true to enable namespace awareness
     */
    default void setXmlNamespaceAware(boolean xmlNamespaceAware) {
        throw new UnsupportedOperationException();
    }


    /**
     * Will the parsing of web.xml and web-fragment.xml files for this Context
     * be performed by a validating parser?
     *
     * @return true if validation is enabled.
     */
    default boolean getXmlValidation() {
        throw new UnsupportedOperationException();
    }


    /**
     * Controls whether the parsing of web.xml and web-fragment.xml files
     * for this Context will be performed by a validating parser.
     *
     * @param xmlValidation true to enable xml validation
     */
    default void setXmlValidation(boolean xmlValidation) {
        throw new UnsupportedOperationException();
    }


    /**
     * Will the parsing of web.xml, web-fragment.xml, *.tld, *.jspx, *.tagx and
     * tagplugin.xml files for this Context block the use of external entities?
     *
     * @return true if access to external entities is blocked
     */
    default boolean getXmlBlockExternal() {
        throw new UnsupportedOperationException();
    }


    /**
     * Controls whether the parsing of web.xml, web-fragment.xml, *.tld, *.jspx,
     * *.tagx and tagplugin.xml files for this Context will block the use of
     * external entities.
     *
     * @param xmlBlockExternal true to block external entities
     */
    default void setXmlBlockExternal(boolean xmlBlockExternal) {
        throw new UnsupportedOperationException();
    }


    /**
     * Will the parsing of *.tld files for this Context be performed by a
     * validating parser?
     *
     * @return true if validation is enabled.
     */
    default boolean getTldValidation() {
        throw new UnsupportedOperationException();
    }


    /**
     * Controls whether the parsing of *.tld files for this Context will be
     * performed by a validating parser.
     *
     * @param tldValidation true to enable xml validation
     */
    default void setTldValidation(boolean tldValidation) {
        throw new UnsupportedOperationException();
    }


    /**
     * Get the Jar Scanner to be used to scan for JAR resources for this
     * context.
     *
     * @return The Jar Scanner configured for this context.
     */
    default JarScanner getJarScanner() {
        throw new UnsupportedOperationException();
    }

    /**
     * Set the Jar Scanner to be used to scan for JAR resources for this
     * context.
     *
     * @param jarScanner The Jar Scanner to be used for this context.
     */
    default void setJarScanner(JarScanner jarScanner) {
        throw new UnsupportedOperationException();
    }

    /**
     * @return the {@link Authenticator} that is used by this context. This is
     * always non-{@code null} for a started Context
     */
    default Authenticator getAuthenticator() {
        throw new UnsupportedOperationException();
    }

    /**
     * Set whether or not the effective web.xml for this context should be
     * logged on context start.
     *
     * @param logEffectiveWebXml set to <code>true</code> to log the complete
     *                           web.xml that will be used for the webapp
     */
    default void setLogEffectiveWebXml(boolean logEffectiveWebXml) {
        throw new UnsupportedOperationException();
    }

    /**
     * Should the effective web.xml for this context be logged on context start?
     *
     * @return true if the reconstructed web.xml that will be used for the
     * webapp should be logged
     */
    default boolean getLogEffectiveWebXml() {
        throw new UnsupportedOperationException();
    }

    /**
     * @return the instance manager associated with this context.
     */
    default InstanceManager getInstanceManager() {
        throw new UnsupportedOperationException();
    }

    /**
     * Set the instance manager associated with this context.
     *
     * @param instanceManager the new instance manager instance
     */
    default void setInstanceManager(InstanceManager instanceManager) {
        throw new UnsupportedOperationException();
    }

    /**
     * Sets the regular expression that specifies which container provided SCIs
     * should be filtered out and not used for this context. Matching uses
     * {@link java.util.regex.Matcher#find()} so the regular expression only has
     * to match a sub-string of the fully qualified class name of the container
     * provided SCI for it to be filtered out.
     *
     * @param containerSciFilter The regular expression against which the fully
     *                           qualified class name of each container provided
     *                           SCI should be checked
     */
    default void setContainerSciFilter(String containerSciFilter) {
        throw new UnsupportedOperationException();
    }

    /**
     * Obtains the regular expression that specifies which container provided
     * SCIs should be filtered out and not used for this context. Matching uses
     * {@link java.util.regex.Matcher#find()} so the regular expression only has
     * to match a sub-string of the fully qualified class name of the container
     * provided SCI for it to be filtered out.
     *
     * @return The regular expression against which the fully qualified class
     * name of each container provided SCI will be checked
     */
    default String getContainerSciFilter() {
        throw new UnsupportedOperationException();
    }


    /**
     * @return the value of the parallel annotation scanning flag.  If true,
     * it will dispatch scanning to the utility executor.
     * @deprecated This method will be removed in Tomcat 11 onwards
     */
    @Deprecated
    default boolean isParallelAnnotationScanning() {
//        return getParallelAnnotationScanning();
        throw new UnsupportedOperationException();
    }

    /**
     * @return the value of the parallel annotation scanning flag.  If true,
     * it will dispatch scanning to the utility executor.
     */
    default boolean getParallelAnnotationScanning() {
        throw new UnsupportedOperationException();
    }

    /**
     * Set the parallel annotation scanning value.
     *
     * @param parallelAnnotationScanning new parallel annotation scanning flag
     */
    default void setParallelAnnotationScanning(boolean parallelAnnotationScanning) {
        throw new UnsupportedOperationException();
    }


    // --------------------------------------------------------- Public Methods

    /**
     * Add a new Listener class name to the set of Listeners
     * configured for this application.
     *
     * @param listener Java class name of a listener class
     */
    default void addApplicationListener(String listener) {
        throw new UnsupportedOperationException();
    }


    /**
     * Add a new application parameter for this application.
     *
     * @param parameter The new application parameter
     */
    default void addApplicationParameter(ApplicationParameter parameter) {
        throw new UnsupportedOperationException();
    }


    /**
     * Add a security constraint to the set for this web application.
     *
     * @param constraint The security constraint that should be added
     */
    default void addConstraint(SecurityConstraint constraint) {
        throw new UnsupportedOperationException();
    }


    /**
     * Add an error page for the specified error or Java exception.
     *
     * @param errorPage The error page definition to be added
     */
    default void addErrorPage(ErrorPage errorPage) {
        throw new UnsupportedOperationException();
    }


    /**
     * Add a filter definition to this Context.
     *
     * @param filterDef The filter definition to be added
     */
    default void addFilterDef(FilterDef filterDef) {
        throw new UnsupportedOperationException();
    }


    /**
     * Add a filter mapping to this Context.
     *
     * @param filterMap The filter mapping to be added
     */
    default void addFilterMap(FilterMap filterMap) {
        throw new UnsupportedOperationException();
    }

    /**
     * Add a filter mapping to this Context before the mappings defined in the
     * deployment descriptor but after any other mappings added via this method.
     *
     * @param filterMap The filter mapping to be added
     * @throws IllegalArgumentException if the specified filter name
     *                                  does not match an existing filter definition, or the filter mapping
     *                                  is malformed
     */
    default void addFilterMapBefore(FilterMap filterMap) {
        throw new UnsupportedOperationException();
    }


    /**
     * Add a Locale Encoding Mapping (see Sec 5.4 of Servlet spec 2.4)
     *
     * @param locale   locale to map an encoding for
     * @param encoding encoding to be used for a give locale
     */
    default void addLocaleEncodingMappingParameter(String locale, String encoding) {
        throw new UnsupportedOperationException();
    }


    /**
     * Add a new MIME mapping, replacing any existing mapping for
     * the specified extension.
     *
     * @param extension Filename extension being mapped
     * @param mimeType  Corresponding MIME type
     */
    default void addMimeMapping(String extension, String mimeType) {
        throw new UnsupportedOperationException();
    }


    /**
     * Add a new context initialization parameter, replacing any existing
     * value for the specified name.
     *
     * @param name  Name of the new parameter
     * @param value Value of the new  parameter
     */
    default void addParameter(String name, String value) {
        throw new UnsupportedOperationException();
    }


    /**
     * Add a security role reference for this web application.
     *
     * @param role Security role used in the application
     * @param link Actual security role to check for
     */
    default void addRoleMapping(String role, String link) {
        throw new UnsupportedOperationException();
    }


    /**
     * Add a new security role for this web application.
     *
     * @param role New security role
     */
    default void addSecurityRole(String role) {
        throw new UnsupportedOperationException();
    }


    /**
     * Add a new servlet mapping, replacing any existing mapping for
     * the specified pattern.
     *
     * @param pattern URL pattern to be mapped
     * @param name Name of the corresponding servlet to execute
     */
    default void addServletMappingDecoded(String pattern, String name) {
        addServletMappingDecoded(pattern, name, false);
    }


    /**
     * Add a new servlet mapping, replacing any existing mapping for
     * the specified pattern.
     *
     * @param pattern     URL pattern to be mapped
     * @param name        Name of the corresponding servlet to execute
     * @param jspWildcard true if name identifies the JspServlet
     *                    and pattern contains a wildcard; false otherwise
     */
    default void addServletMappingDecoded(String pattern, String name,
                                          boolean jspWildcard) {
        throw new UnsupportedOperationException();
    }


    /**
     * Add a resource which will be watched for reloading by the host auto
     * deployer. Note: this will not be used in embedded mode.
     *
     * @param name Path to the resource, relative to docBase
     */
    default void addWatchedResource(String name) {
        throw new UnsupportedOperationException();
    }


    /**
     * Add a new welcome file to the set recognized by this Context.
     *
     * @param name New welcome file name
     */
    default void addWelcomeFile(String name) {
        throw new UnsupportedOperationException();
    }


    /**
     * Add the classname of a LifecycleListener to be added to each
     * Wrapper appended to this Context.
     *
     * @param listener Java class name of a LifecycleListener class
     */
    default void addWrapperLifecycle(String listener) {
        throw new UnsupportedOperationException();
    }


    /**
     * Add the classname of a ContainerListener to be added to each
     * Wrapper appended to this Context.
     *
     * @param listener Java class name of a ContainerListener class
     */
    default void addWrapperListener(String listener) {
        throw new UnsupportedOperationException();
    }


    /**
     * Factory method to create and return a new InstanceManager
     * instance. This can be used for framework integration or easier
     * configuration with custom Context implementations.
     *
     * @return the instance manager
     */
    default InstanceManager createInstanceManager() {
        throw new UnsupportedOperationException();
    }

    /**
     * Factory method to create and return a new Wrapper instance, of
     * the Java implementation class appropriate for this Context
     * implementation.  The constructor of the instantiated Wrapper
     * will have been called, but no properties will have been set.
     *
     * @return a newly created wrapper instance that is used to wrap a Servlet
     */
    default Wrapper createWrapper() {
        throw new UnsupportedOperationException();
    }


    /**
     * @return the set of application listener class names configured
     * for this application.
     */
    default String[] findApplicationListeners() {
        throw new UnsupportedOperationException();
    }


    /**
     * @return the set of application parameters for this application.
     */
    default ApplicationParameter[] findApplicationParameters() {
        throw new UnsupportedOperationException();
    }


    /**
     * @return the set of security constraints for this web application.
     * If there are none, a zero-length array is returned.
     */
    default SecurityConstraint[] findConstraints() {
        throw new UnsupportedOperationException();
    }


    /**
     * @param errorCode Error code to look up
     * @return the error page entry for the specified HTTP error code,
     * if any; otherwise return <code>null</code>.
     */
    default ErrorPage findErrorPage(int errorCode) {
        throw new UnsupportedOperationException();
    }


    /**
     * Find and return the ErrorPage instance for the specified exception's
     * class, or an ErrorPage instance for the closest superclass for which
     * there is such a definition.  If no associated ErrorPage instance is
     * found, return <code>null</code>.
     *
     * @param throwable The exception type for which to find an ErrorPage
     * @return the error page entry for the specified Java exception type,
     * if any; otherwise return {@code null}.
     */
    default ErrorPage findErrorPage(Throwable throwable) {
        throw new UnsupportedOperationException();
    }


    /**
     * @return the set of defined error pages for all specified error codes
     * and exception types.
     */
    default ErrorPage[] findErrorPages() {
        throw new UnsupportedOperationException();
    }


    /**
     * @param filterName Filter name to look up
     * @return the filter definition for the specified filter name, if any;
     * otherwise return <code>null</code>.
     */
    default FilterDef findFilterDef(String filterName) {
        throw new UnsupportedOperationException();
    }


    /**
     * @return the set of defined filters for this Context.
     */
    default FilterDef[] findFilterDefs() {
        throw new UnsupportedOperationException();
    }


    /**
     * @return the set of filter mappings for this Context.
     */
    default FilterMap[] findFilterMaps() {
        throw new UnsupportedOperationException();
    }


    /**
     * @param extension Extension to map to a MIME type
     * @return the MIME type to which the specified extension is mapped,
     * if any; otherwise return <code>null</code>.
     */
    default String findMimeMapping(String extension) {
        throw new UnsupportedOperationException();
    }


    /**
     * @return the extensions for which MIME mappings are defined.  If there
     * are none, a zero-length array is returned.
     */
    default String[] findMimeMappings() {
        throw new UnsupportedOperationException();
    }


    /**
     * @param name Name of the parameter to return
     * @return the value for the specified context initialization
     * parameter name, if any; otherwise return <code>null</code>.
     */
    default String findParameter(String name) {
        throw new UnsupportedOperationException();
    }


    /**
     * @return the names of all defined context initialization parameters
     * for this Context.  If no parameters are defined, a zero-length
     * array is returned.
     */
    default String[] findParameters() {
        throw new UnsupportedOperationException();
    }


    /**
     * For the given security role (as used by an application), return the
     * corresponding role name (as defined by the underlying Realm) if there
     * is one.  Otherwise, return the specified role unchanged.
     *
     * @param role Security role to map
     * @return The role name that was mapped to the specified role
     */
    default String findRoleMapping(String role) {
        throw new UnsupportedOperationException();
    }


    /**
     * @param role Security role to verify
     * @return <code>true</code> if the specified security role is defined
     * for this application; otherwise return <code>false</code>.
     */
    default boolean findSecurityRole(String role) {
        throw new UnsupportedOperationException();
    }


    /**
     * @return the security roles defined for this application.  If none
     * have been defined, a zero-length array is returned.
     */
    default String[] findSecurityRoles() {
        throw new UnsupportedOperationException();
    }


    /**
     * @param pattern Pattern for which a mapping is requested
     * @return the servlet name mapped by the specified pattern (if any);
     * otherwise return <code>null</code>.
     */
    default String findServletMapping(String pattern) {
        throw new UnsupportedOperationException();
    }


    /**
     * @return the patterns of all defined servlet mappings for this
     * Context.  If no mappings are defined, a zero-length array is returned.
     */
    default String[] findServletMappings() {
        throw new UnsupportedOperationException();
    }


    /**
     * @return the associated ThreadBindingListener.
     */
    default ThreadBindingListener getThreadBindingListener() {
        throw new UnsupportedOperationException();
    }


    /**
     * Get the associated ThreadBindingListener.
     *
     * @param threadBindingListener Set the listener that will receive
     *                              notifications when entering and exiting the application scope
     */
    default void setThreadBindingListener(ThreadBindingListener threadBindingListener) {
        throw new UnsupportedOperationException();
    }


    /**
     * @return the set of watched resources for this Context. If none are
     * defined, a zero length array will be returned.
     */
    default String[] findWatchedResources() {
        throw new UnsupportedOperationException();
    }


    /**
     * @param name Welcome file to verify
     * @return <code>true</code> if the specified welcome file is defined
     * for this Context; otherwise return <code>false</code>.
     */
    default boolean findWelcomeFile(String name) {
        throw new UnsupportedOperationException();
    }


    /**
     * @return the set of welcome files defined for this Context.  If none are
     * defined, a zero-length array is returned.
     */
    default String[] findWelcomeFiles() {
        throw new UnsupportedOperationException();
    }


    /**
     * @return the set of LifecycleListener classes that will be added to
     * newly created Wrappers automatically.
     */
    default String[] findWrapperLifecycles() {
        throw new UnsupportedOperationException();
    }


    /**
     * @return the set of ContainerListener classes that will be added to
     * newly created Wrappers automatically.
     */
    default String[] findWrapperListeners() {
        throw new UnsupportedOperationException();
    }


    /**
     * Notify all {@link jakarta.servlet.ServletRequestListener}s that a request
     * has started.
     *
     * @param request The request object that will be passed to the listener
     * @return <code>true</code> if the listeners fire successfully, else
     * <code>false</code>
     */
    default boolean fireRequestInitEvent(ServletRequest request) {
        throw new UnsupportedOperationException();
    }

    /**
     * Notify all {@link jakarta.servlet.ServletRequestListener}s that a request
     * has ended.
     *
     * @param request The request object that will be passed to the listener
     * @return <code>true</code> if the listeners fire successfully, else
     * <code>false</code>
     */
    default boolean fireRequestDestroyEvent(ServletRequest request) {
        throw new UnsupportedOperationException();
    }

    /**
     * Reload this web application, if reloading is supported.
     *
     * @throws IllegalStateException if the <code>reloadable</code>
     *                               property is set to <code>false</code>.
     */
    default void reload() {
        throw new UnsupportedOperationException();
    }


    /**
     * Remove the specified application listener class from the set of
     * listeners for this application.
     *
     * @param listener Java class name of the listener to be removed
     */
    default void removeApplicationListener(String listener) {
        throw new UnsupportedOperationException();
    }


    /**
     * Remove the application parameter with the specified name from
     * the set for this application.
     *
     * @param name Name of the application parameter to remove
     */
    default void removeApplicationParameter(String name) {
        throw new UnsupportedOperationException();
    }


    /**
     * Remove the specified security constraint from this web application.
     *
     * @param constraint Constraint to be removed
     */
    default void removeConstraint(SecurityConstraint constraint) {
        throw new UnsupportedOperationException();
    }


    /**
     * Remove the error page for the specified error code or
     * Java language exception, if it exists; otherwise, no action is taken.
     *
     * @param errorPage The error page definition to be removed
     */
    default void removeErrorPage(ErrorPage errorPage) {
        throw new UnsupportedOperationException();
    }


    /**
     * Remove the specified filter definition from this Context, if it exists;
     * otherwise, no action is taken.
     *
     * @param filterDef Filter definition to be removed
     */
    default void removeFilterDef(FilterDef filterDef) {
        throw new UnsupportedOperationException();
    }


    /**
     * Remove a filter mapping from this Context.
     *
     * @param filterMap The filter mapping to be removed
     */
    default void removeFilterMap(FilterMap filterMap) {
        throw new UnsupportedOperationException();
    }


    /**
     * Remove the MIME mapping for the specified extension, if it exists;
     * otherwise, no action is taken.
     *
     * @param extension Extension to remove the mapping for
     */
    default void removeMimeMapping(String extension) {
        throw new UnsupportedOperationException();
    }


    /**
     * Remove the context initialization parameter with the specified
     * name, if it exists; otherwise, no action is taken.
     *
     * @param name Name of the parameter to remove
     */
    default void removeParameter(String name) {
        throw new UnsupportedOperationException();
    }


    /**
     * Remove any security role reference for the specified name
     *
     * @param role Security role (as used in the application) to remove
     */
    default void removeRoleMapping(String role) {
        throw new UnsupportedOperationException();
    }


    /**
     * Remove any security role with the specified name.
     *
     * @param role Security role to remove
     */
    default void removeSecurityRole(String role) {
        throw new UnsupportedOperationException();
    }


    /**
     * Remove any servlet mapping for the specified pattern, if it exists;
     * otherwise, no action is taken.
     *
     * @param pattern URL pattern of the mapping to remove
     */
    default void removeServletMapping(String pattern) {
        throw new UnsupportedOperationException();
    }


    /**
     * Remove the specified watched resource name from the list associated
     * with this Context.
     *
     * @param name Name of the watched resource to be removed
     */
    default void removeWatchedResource(String name) {
        throw new UnsupportedOperationException();
    }


    /**
     * Remove the specified welcome file name from the list recognized
     * by this Context.
     *
     * @param name Name of the welcome file to be removed
     */
    default void removeWelcomeFile(String name) {
        throw new UnsupportedOperationException();
    }


    /**
     * Remove a class name from the set of LifecycleListener classes that
     * will be added to newly created Wrappers.
     *
     * @param listener Class name of a LifecycleListener class to be removed
     */
    default void removeWrapperLifecycle(String listener) {
        throw new UnsupportedOperationException();
    }


    /**
     * Remove a class name from the set of ContainerListener classes that
     * will be added to newly created Wrappers.
     *
     * @param listener Class name of a ContainerListener class to be removed
     */
    default void removeWrapperListener(String listener) {
        throw new UnsupportedOperationException();
    }


    /**
     * @param path The path to the desired resource
     * @return the real path for a given virtual path, if possible; otherwise
     * return <code>null</code>.
     */
    default String getRealPath(String path) {
        throw new UnsupportedOperationException();
    }


    /**
     * @return the effective major version of the Servlet spec used by this
     * context.
     */
    default int getEffectiveMajorVersion() {
        throw new UnsupportedOperationException();
    }


    /**
     * Set the effective major version of the Servlet spec used by this
     * context.
     *
     * @param major Set the version number
     */
    default void setEffectiveMajorVersion(int major) {
        throw new UnsupportedOperationException();
    }


    /**
     * @return the effective minor version of the Servlet spec used by this
     * context.
     */
    default int getEffectiveMinorVersion() {
        throw new UnsupportedOperationException();
    }


    /**
     * Set the effective minor version of the Servlet spec used by this
     * context.
     *
     * @param minor Set the version number
     */
    default void setEffectiveMinorVersion(int minor) {
        throw new UnsupportedOperationException();
    }


    /**
     * @return the JSP configuration for this context.
     * Will be null if there is no JSP configuration.
     */
    default JspConfigDescriptor getJspConfigDescriptor() {
        throw new UnsupportedOperationException();
    }


    /**
     * Set the JspConfigDescriptor for this context.
     * A null value indicates there is not JSP configuration.
     *
     * @param descriptor the new JSP configuration
     */
    default void setJspConfigDescriptor(JspConfigDescriptor descriptor) {
        throw new UnsupportedOperationException();
    }


    /**
     * Add a ServletContainerInitializer instance to this web application.
     *
     * @param sci     The instance to add
     * @param classes The classes in which the initializer expressed an
     *                interest
     */
    default void addServletContainerInitializer(
            ServletContainerInitializer sci, Set<Class<?>> classes) {
        throw new UnsupportedOperationException();
    }


    /**
     * Is this Context paused whilst it is reloaded?
     *
     * @return <code>true</code> if the context has been paused
     */
    default boolean getPaused() {
        throw new UnsupportedOperationException();
    }


    /**
     * Is this context using version 2.2 of the Servlet spec?
     *
     * @return <code>true</code> for a legacy Servlet 2.2 webapp
     */
    default boolean isServlet22() {
        throw new UnsupportedOperationException();
    }


    /**
     * Notification that Servlet security has been dynamically set in a
     * {@link jakarta.servlet.ServletRegistration.Dynamic}
     *
     * @param registration           Servlet security was modified for
     * @param servletSecurityElement new security constraints for this Servlet
     * @return urls currently mapped to this registration that are already
     * present in web.xml
     */
    default Set<String> addServletSecurity(ServletRegistration.Dynamic registration,
                                           ServletSecurityElement servletSecurityElement) {
        throw new UnsupportedOperationException();
    }

    /**
     * Sets the (comma separated) list of Servlets that expect a resource to be
     * present. Used to ensure that welcome files associated with Servlets that
     * expect a resource to be present are not mapped when there is no resource.
     *
     * @param resourceOnlyServlets The Servlet names comma separated list
     */
    default void setResourceOnlyServlets(String resourceOnlyServlets) {
        throw new UnsupportedOperationException();
    }

    /**
     * Obtains the list of Servlets that expect a resource to be present.
     *
     * @return A comma separated list of Servlet names as used in web.xml
     */
    default String getResourceOnlyServlets() {
        throw new UnsupportedOperationException();
    }

    /**
     * Checks the named Servlet to see if it expects a resource to be present.
     *
     * @param servletName Name of the Servlet (as per web.xml) to check
     * @return <code>true</code> if the Servlet expects a resource,
     * otherwise <code>false</code>
     */
    default boolean isResourceOnlyServlet(String servletName) {
        throw new UnsupportedOperationException();
    }

    /**
     * @return the base name to use for WARs, directories or context.xml files
     * for this context.
     */
    default String getBaseName() {
        throw new UnsupportedOperationException();
    }

    /**
     * Set the version of this web application - used to differentiate
     * different versions of the same web application when using parallel
     * deployment.
     *
     * @param webappVersion The webapp version associated with the context,
     *                      which should be unique
     */
    default void setWebappVersion(String webappVersion) {
        throw new UnsupportedOperationException();
    }

    /**
     * @return The version of this web application, used to differentiate
     * different versions of the same web application when using parallel
     * deployment. If not specified, defaults to the empty string.
     */
    default String getWebappVersion() {
        throw new UnsupportedOperationException();
    }

    /**
     * Configure whether or not requests listeners will be fired on forwards for
     * this Context.
     *
     * @param enable <code>true</code> to fire request listeners when forwarding
     */
    default void setFireRequestListenersOnForwards(boolean enable) {
        throw new UnsupportedOperationException();
    }

    /**
     * @return whether or not requests listeners will be fired on forwards for
     * this Context.
     */
    default boolean getFireRequestListenersOnForwards() {
        throw new UnsupportedOperationException();
    }

    /**
     * Configures if a user presents authentication credentials, whether the
     * context will process them when the request is for a non-protected
     * resource.
     *
     * @param enable <code>true</code> to perform authentication even outside
     *               security constraints
     */
    default void setPreemptiveAuthentication(boolean enable) {
        throw new UnsupportedOperationException();
    }

    /**
     * @return if a user presents authentication credentials, will the
     * context will process them when the request is for a non-protected
     * resource.
     */
    default boolean getPreemptiveAuthentication() {
        throw new UnsupportedOperationException();
    }

    /**
     * Configures if a response body is included when a redirect response is
     * sent to the client.
     *
     * @param enable <code>true</code> to send a response body for redirects
     */
    default void setSendRedirectBody(boolean enable) {
        throw new UnsupportedOperationException();
    }

    /**
     * @return if the context is configured to include a response body as
     * part of a redirect response.
     */
    default boolean getSendRedirectBody() {
        throw new UnsupportedOperationException();
    }

    /**
     * @return the Loader with which this Context is associated.
     */
    default Loader getLoader() {
        throw new UnsupportedOperationException();
    }

    /**
     * Set the Loader with which this Context is associated.
     *
     * @param loader The newly associated loader
     */
    default void setLoader(Loader loader) {
        throw new UnsupportedOperationException();
    }

    /**
     * @return the Resources with which this Context is associated.
     */
    default WebResourceRoot getResources() {
        throw new UnsupportedOperationException();
    }

    /**
     * Set the Resources object with which this Context is associated.
     *
     * @param resources The newly associated Resources
     */
    default void setResources(WebResourceRoot resources) {
        throw new UnsupportedOperationException();
    }

    /**
     * @return the Manager with which this Context is associated.  If there is
     * no associated Manager, return <code>null</code>.
     */
    default Manager getManager() {
        throw new UnsupportedOperationException();
    }


    /**
     * Set the Manager with which this Context is associated.
     *
     * @param manager The newly associated Manager
     */
    default void setManager(Manager manager) {
        throw new UnsupportedOperationException();
    }

    /**
     * Sets the flag that indicates if /WEB-INF/classes should be treated like
     * an exploded JAR and JAR resources made available as if they were in a
     * JAR.
     *
     * @param addWebinfClassesResources The new value for the flag
     */
    default void setAddWebinfClassesResources(boolean addWebinfClassesResources) {
        throw new UnsupportedOperationException();
    }

    /**
     * @return the flag that indicates if /WEB-INF/classes should be treated like
     * an exploded JAR and JAR resources made available as if they were in a
     * JAR.
     */
    default boolean getAddWebinfClassesResources() {
        throw new UnsupportedOperationException();
    }

    /**
     * Add a post construct method definition for the given class, if there is
     * an existing definition for the specified class - IllegalArgumentException
     * will be thrown.
     *
     * @param clazz  Fully qualified class name
     * @param method Post construct method name
     * @throws IllegalArgumentException if the fully qualified class name or method name are
     *                                  <code>NULL</code>; if there is already post construct method
     *                                  definition for the given class
     */
    default void addPostConstructMethod(String clazz, String method) {
        throw new UnsupportedOperationException();
    }

    /**
     * Add a pre destroy method definition for the given class, if there is an
     * existing definition for the specified class - IllegalArgumentException
     * will be thrown.
     *
     * @param clazz  Fully qualified class name
     * @param method Post construct method name
     * @throws IllegalArgumentException if the fully qualified class name or method name are
     *                                  <code>NULL</code>; if there is already pre destroy method
     *                                  definition for the given class
     */
    default void addPreDestroyMethod(String clazz, String method) {
        throw new UnsupportedOperationException();
    }

    /**
     * Removes the post construct method definition for the given class, if it
     * exists; otherwise, no action is taken.
     *
     * @param clazz Fully qualified class name
     */
    default void removePostConstructMethod(String clazz) {
        throw new UnsupportedOperationException();
    }

    /**
     * Removes the pre destroy method definition for the given class, if it
     * exists; otherwise, no action is taken.
     *
     * @param clazz Fully qualified class name
     */
    default void removePreDestroyMethod(String clazz) {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns the method name that is specified as post construct method for
     * the given class, if it exists; otherwise <code>NULL</code> will be
     * returned.
     *
     * @param clazz Fully qualified class name
     * @return the method name that is specified as post construct method for
     * the given class, if it exists; otherwise <code>NULL</code> will
     * be returned.
     */
    default String findPostConstructMethod(String clazz) {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns the method name that is specified as pre destroy method for the
     * given class, if it exists; otherwise <code>NULL</code> will be returned.
     *
     * @param clazz Fully qualified class name
     * @return the method name that is specified as pre destroy method for the
     * given class, if it exists; otherwise <code>NULL</code> will be
     * returned.
     */
    default String findPreDestroyMethod(String clazz) {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns a map with keys - fully qualified class names of the classes that
     * have post construct methods and the values are the corresponding method
     * names. If there are no such classes an empty map will be returned.
     *
     * @return a map with keys - fully qualified class names of the classes that
     * have post construct methods and the values are the corresponding
     * method names.
     */
    default Map<String, String> findPostConstructMethods() {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns a map with keys - fully qualified class names of the classes that
     * have pre destroy methods and the values are the corresponding method
     * names. If there are no such classes an empty map will be returned.
     *
     * @return a map with keys - fully qualified class names of the classes that
     * have pre destroy methods and the values are the corresponding
     * method names.
     */
    default Map<String, String> findPreDestroyMethods() {
        throw new UnsupportedOperationException();
    }

    /**
     * @return the token necessary for operations on the associated JNDI naming
     * context.
     */
    default Object getNamingToken() {
        throw new UnsupportedOperationException();
    }

    /**
     * Sets the {@link CookieProcessor} that will be used to process cookies
     * for this Context.
     *
     * @param cookieProcessor The new cookie processor
     * @throws IllegalArgumentException If a {@code null} CookieProcessor is
     *                                  specified
     */
    default void setCookieProcessor(CookieProcessor cookieProcessor) {
        throw new UnsupportedOperationException();
    }

    /**
     * @return the {@link CookieProcessor} that will be used to process cookies
     * for this Context.
     */
    default CookieProcessor getCookieProcessor() {
        throw new UnsupportedOperationException();
    }

    /**
     * When a client provides the ID for a new session, should that ID be
     * validated? The only use case for using a client provided session ID is to
     * have a common session ID across multiple web applications. Therefore,
     * any client provided session ID should already exist in another web
     * application. If this check is enabled, the client provided session ID
     * will only be used if the session ID exists in at least one other web
     * application for the current host. Note that the following additional
     * tests are always applied, irrespective of this setting:
     * <ul>
     * <li>The session ID is provided by a cookie</li>
     * <li>The session cookie has a path of {@code /}</li>
     * </ul>
     *
     * @param validateClientProvidedNewSessionId {@code true} if validation should be applied
     */
    default void setValidateClientProvidedNewSessionId(boolean validateClientProvidedNewSessionId) {
        throw new UnsupportedOperationException();
    }

    /**
     * Will client provided session IDs be validated (see {@link
     * #setValidateClientProvidedNewSessionId(boolean)}) before use?
     *
     * @return {@code true} if validation will be applied. Otherwise, {@code
     * false}
     */
    default boolean getValidateClientProvidedNewSessionId() {
        throw new UnsupportedOperationException();
    }

    /**
     * If enabled, requests for a web application context root will be
     * redirected (adding a trailing slash) by the Mapper. This is more
     * efficient but has the side effect of confirming that the context path is
     * valid.
     *
     * @param mapperContextRootRedirectEnabled Should the redirects be enabled?
     */
    default void setMapperContextRootRedirectEnabled(boolean mapperContextRootRedirectEnabled) {
        throw new UnsupportedOperationException();
    }

    /**
     * Determines if requests for a web application context root will be
     * redirected (adding a trailing slash) by the Mapper. This is more
     * efficient but has the side effect of confirming that the context path is
     * valid.
     *
     * @return {@code true} if the Mapper level redirect is enabled for this
     * Context.
     */
    default boolean getMapperContextRootRedirectEnabled() {
        throw new UnsupportedOperationException();
    }

    /**
     * If enabled, requests for a directory will be redirected (adding a
     * trailing slash) by the Mapper. This is more efficient but has the
     * side effect of confirming that the directory is valid.
     *
     * @param mapperDirectoryRedirectEnabled Should the redirects be enabled?
     */
    default void setMapperDirectoryRedirectEnabled(boolean mapperDirectoryRedirectEnabled) {
        throw new UnsupportedOperationException();
    }

    /**
     * Determines if requests for a directory will be redirected (adding a
     * trailing slash) by the Mapper. This is more efficient but has the
     * side effect of confirming that the directory is valid.
     *
     * @return {@code true} if the Mapper level redirect is enabled for this
     *         Context.
     */
    boolean getMapperDirectoryRedirectEnabled();

    /**
     * Controls whether HTTP 1.1 and later location headers generated by a call
     * to {@link jakarta.servlet.http.HttpServletResponse#sendRedirect(String)}
     * will use relative or absolute redirects.
     * <p>
     * Relative redirects are more efficient but may not work with reverse
     * proxies that change the context path. It should be noted that it is not
     * recommended to use a reverse proxy to change the context path because of
     * the multiple issues it creates.
     * <p>
     * Absolute redirects should work with reverse proxies that change the
     * context path but may cause issues with the
     * {@link org.apache.catalina.filters.RemoteIpFilter} if the filter is
     * changing the scheme and/or port.
     *
     * @param useRelativeRedirects {@code true} to use relative redirects and
     *                             {@code false} to use absolute redirects
     */
    default void setUseRelativeRedirects(boolean useRelativeRedirects) {
        throw new UnsupportedOperationException();
    }

    /**
     * Will HTTP 1.1 and later location headers generated by a call to
     * {@link jakarta.servlet.http.HttpServletResponse#sendRedirect(String)} use
     * relative or absolute redirects.
     *
     * @return {@code true} if relative redirects will be used {@code false} if
     * absolute redirects are used.
     * @see #setUseRelativeRedirects(boolean)
     */
    default boolean getUseRelativeRedirects() {
        throw new UnsupportedOperationException();
    }

    /**
     * Are paths used in calls to obtain a request dispatcher expected to be
     * encoded? This affects both how Tomcat handles calls to obtain a request
     * dispatcher as well as how Tomcat generates paths used to obtain request
     * dispatchers internally.
     *
     * @param dispatchersUseEncodedPaths {@code true} to use encoded paths,
     *                                   otherwise {@code false}
     */
    default void setDispatchersUseEncodedPaths(boolean dispatchersUseEncodedPaths) {
        throw new UnsupportedOperationException();
    }

    /**
     * Are paths used in calls to obtain a request dispatcher expected to be
     * encoded? This applies to both how Tomcat handles calls to obtain a request
     * dispatcher as well as how Tomcat generates paths used to obtain request
     * dispatchers internally.
     *
     * @return {@code true} if encoded paths will be used, otherwise
     * {@code false}
     */
    default boolean getDispatchersUseEncodedPaths() {
        throw new UnsupportedOperationException();
    }

    /**
     * Set the default request body encoding for this web application.
     *
     * @param encoding The default encoding
     */
    default void setRequestCharacterEncoding(String encoding) {
        throw new UnsupportedOperationException();
    }

    /**
     * Get the default request body encoding for this web application.
     *
     * @return The default request body encoding
     */
    default String getRequestCharacterEncoding() {
        throw new UnsupportedOperationException();
    }

    /**
     * Set the default response body encoding for this web application.
     *
     * @param encoding The default encoding
     */
    default void setResponseCharacterEncoding(String encoding) {
        throw new UnsupportedOperationException();
    }

    /**
     * Get the default response body encoding for this web application.
     *
     * @return The default response body encoding
     */
    default String getResponseCharacterEncoding() {
        throw new UnsupportedOperationException();
    }

    /**
     * Configure if, when returning a context path from {@link
     * jakarta.servlet.http.HttpServletRequest#getContextPath()}, the return value
     * is allowed to contain multiple leading '/' characters.
     *
     * @param allowMultipleLeadingForwardSlashInPath The new value for the flag
     */
    default void setAllowMultipleLeadingForwardSlashInPath(
            boolean allowMultipleLeadingForwardSlashInPath) {
        throw new UnsupportedOperationException();
    }

    /**
     * When returning a context path from {@link
     * jakarta.servlet.http.HttpServletRequest#getContextPath()}, is it allowed to
     * contain multiple leading '/' characters?
     *
     * @return <code>true</code> if multiple leading '/' characters are allowed,
     * otherwise <code>false</code>
     */
    default boolean getAllowMultipleLeadingForwardSlashInPath() {
        throw new UnsupportedOperationException();
    }


    default void incrementInProgressAsyncCount() {
        throw new UnsupportedOperationException();
    }


    default void decrementInProgressAsyncCount() {
        throw new UnsupportedOperationException();
    }


    /**
     * Configure whether Tomcat will attempt to create an upload target used by
     * this web application if it does not exist when the web application
     * attempts to use it.
     *
     * @param createUploadTargets {@code true} if Tomcat should attempt to
     *                            create the upload target, otherwise {@code false}
     */
    default void setCreateUploadTargets(boolean createUploadTargets) {
        throw new UnsupportedOperationException();
    }


    /**
     * Will Tomcat attempt to create an upload target used by this web
     * application if it does not exist when the web application attempts to use
     * it?
     *
     * @return {@code true} if Tomcat will attempt to create an upload target
     * otherwise {@code false}
     */
    default boolean getCreateUploadTargets() {
        throw new UnsupportedOperationException();
    }


    /**
     * If this is <code>true</code>, every request that is associated with a
     * session will cause the session's last accessed time to be updated
     * regardless of whether or not the request explicitly accesses the session.
     * If <code>org.apache.catalina.STRICT_SERVLET_COMPLIANCE</code> is set to
     * <code>true</code>, the default of this setting will be <code>true</code>,
     * else the default value will be <code>false</code>.
     *
     * @return the flag value
     */
    default boolean getAlwaysAccessSession() {
        throw new UnsupportedOperationException();
    }


    /**
     * Set the session access behavior.
     *
     * @param alwaysAccessSession the new flag value
     */
    default void setAlwaysAccessSession(boolean alwaysAccessSession) {
        throw new UnsupportedOperationException();
    }


    /**
     * If this is <code>true</code> then the path passed to
     * <code>ServletContext.getResource()</code> or
     * <code>ServletContext.getResourceAsStream()</code> must start with
     * &quot;/&quot;. If <code>false</code>, code like
     * <code>getResource("myfolder/myresource.txt")</code> will work as Tomcat
     * will prepend &quot;/&quot; to the provided path.
     * If <code>org.apache.catalina.STRICT_SERVLET_COMPLIANCE</code> is set to
     * <code>true</code>, the default of this setting will be <code>true</code>,
     * else the default value will be <code>false</code>.
     *
     * @return the flag value
     */
    default boolean getContextGetResourceRequiresSlash() {
        throw new UnsupportedOperationException();
    }


    /**
     * Allow using <code>ServletContext.getResource()</code> or
     * <code>ServletContext.getResourceAsStream()</code> without
     * a leading &quot;/&quot;.
     *
     * @param contextGetResourceRequiresSlash the new flag value
     */
    default void setContextGetResourceRequiresSlash(boolean contextGetResourceRequiresSlash) {
        throw new UnsupportedOperationException();
    }


    /**
     * If this is <code>true</code> then any wrapped request or response
     * object passed to an application dispatcher will be checked to ensure that
     * it has wrapped the original request or response.
     * If <code>org.apache.catalina.STRICT_SERVLET_COMPLIANCE</code> is set to
     * <code>true</code>, the default of this setting will be <code>true</code>,
     * else the default value will be <code>false</code>.
     *
     * @return the flag value
     */
    default boolean getDispatcherWrapsSameObject() {
        throw new UnsupportedOperationException();
    }


    /**
     * Allow disabling the object wrap check in the request dispatcher.
     *
     * @param dispatcherWrapsSameObject the new flag value
     */
    default void setDispatcherWrapsSameObject(boolean dispatcherWrapsSameObject) {
        throw new UnsupportedOperationException();
    }


    /**
     * Find configuration file with the specified path, first looking into the
     * webapp resources, then delegating to
     * <code>ConfigFileLoader.getSource().getResource</code>. The
     * <code>WEBAPP_PROTOCOL</code> constant prefix is used to denote webapp
     * resources.
     * @param name The resource name
     * @return the resource
     * @throws IOException if an error occurs or if the resource does not exist
     */
    default Resource findConfigFileResource(String name) throws IOException {
//        if (name.startsWith(WEBAPP_PROTOCOL)) {
//            String path = name.substring(WEBAPP_PROTOCOL.length());
//            WebResource resource = getResources().getResource(path);
//            if (resource.canRead() && resource.isFile()) {
//                InputStream stream = resource.getInputStream();
//                try {
//                    return new Resource(stream, resource.getURL().toURI());
//                } catch (URISyntaxException e) {
//                    stream.close();
//                }
//            }
//            throw new FileNotFoundException(name);
//        }
//        return ConfigFileLoader.getSource().getResource(name);
        throw new UnsupportedOperationException();
    }


    /**
     * @return <code>true</code> if the resources archive lookup will
     * use a bloom filter.
     * @deprecated This method will be removed in Tomcat 11 onwards.
     * Use {@link WebResourceRoot#getArchiveIndexStrategy()}
     */
    @Deprecated
    default boolean getUseBloomFilterForArchives() {
        throw new UnsupportedOperationException();
    }

    /**
     * Set bloom filter flag value.
     *
     * @param useBloomFilterForArchives The new fast class path scan flag
     * @deprecated This method will be removed in Tomcat 11 onwards
     * Use {@link WebResourceRoot#setArchiveIndexStrategy(String)}
     */
    @Deprecated
    default void setUseBloomFilterForArchives(boolean useBloomFilterForArchives) {
        throw new UnsupportedOperationException();
    }
}
