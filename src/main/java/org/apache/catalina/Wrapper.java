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


import jakarta.servlet.MultipartConfigElement;
import jakarta.servlet.Servlet;
import jakarta.servlet.ServletException;
import jakarta.servlet.UnavailableException;


/**
 * A <b>Wrapper</b> is a Container that represents an individual servlet
 * definition from the deployment descriptor of the web application.  It
 * provides a convenient mechanism to use Interceptors that see every single
 * request to the servlet represented by this definition.
 * <p>
 * Implementations of Wrapper are responsible for managing the servlet life
 * cycle for their underlying servlet class, including calling init() and
 * destroy() at appropriate times.
 * <p>
 * The parent Container attached to a Wrapper will generally be an
 * implementation of Context, representing the servlet context (and
 * therefore the web application) within which this servlet executes.
 * <p>
 * Child Containers are not allowed on Wrapper implementations, so the
 * <code>addChild()</code> method should throw an
 * <code>IllegalArgumentException</code>.
 *
 * @author Craig R. McClanahan
 */
public interface Wrapper extends Container {

    /**
     * Container event for adding a wrapper.
     */
    String ADD_MAPPING_EVENT = "addMapping";

    /**
     * Container event for removing a wrapper.
     */
    String REMOVE_MAPPING_EVENT = "removeMapping";

    // ------------------------------------------------------------- Properties


    /**
     * @return the available date/time for this servlet, in milliseconds since
     * the epoch.  If this date/time is in the future, any request for this
     * servlet will return an SC_SERVICE_UNAVAILABLE error.  If it is zero,
     * the servlet is currently available.  A value equal to Long.MAX_VALUE
     * is considered to mean that unavailability is permanent.
     */
    default long getAvailable() {
        throw new UnsupportedOperationException();
    }


    /**
     * Set the available date/time for this servlet, in milliseconds since the
     * epoch.  If this date/time is in the future, any request for this servlet
     * will return an SC_SERVICE_UNAVAILABLE error.  A value equal to
     * Long.MAX_VALUE is considered to mean that unavailability is permanent.
     *
     * @param available The new available date/time
     */
    default void setAvailable(long available) {
        throw new UnsupportedOperationException();
    }


    /**
     * @return the load-on-startup order value (negative value means
     * load on first call).
     */
    default int getLoadOnStartup() {
        throw new UnsupportedOperationException();
    }


    /**
     * Set the load-on-startup order value (negative value means
     * load on first call).
     *
     * @param value New load-on-startup value
     */
    default void setLoadOnStartup(int value) {
        throw new UnsupportedOperationException();
    }


    /**
     * @return the run-as identity for this servlet.
     */
    default String getRunAs() {
        throw new UnsupportedOperationException();
    }


    /**
     * Set the run-as identity for this servlet.
     *
     * @param runAs New run-as identity value
     */
    default void setRunAs(String runAs) {
        throw new UnsupportedOperationException();
    }


    /**
     * @return the fully qualified servlet class name for this servlet.
     */
    default String getServletClass() {
        throw new UnsupportedOperationException();
    }


    /**
     * Set the fully qualified servlet class name for this servlet.
     *
     * @param servletClass Servlet class name
     */
    default void setServletClass(String servletClass) {
        throw new UnsupportedOperationException();
    }


    /**
     * Gets the names of the methods supported by the underlying servlet.
     * <p>
     * This is the same set of methods included in the Allow response header
     * in response to an OPTIONS request method processed by the underlying
     * servlet.
     *
     * @return Array of names of the methods supported by the underlying
     * servlet
     * @throws ServletException If the target servlet cannot be loaded
     */
    default String[] getServletMethods() throws ServletException {
        throw new UnsupportedOperationException();
    }


    /**
     * @return <code>true</code> if this Servlet is currently unavailable.
     */
    default boolean isUnavailable() {
        throw new UnsupportedOperationException();
    }


    /**
     * @return the associated Servlet instance.
     */
    default Servlet getServlet() {
        throw new UnsupportedOperationException();
    }


    /**
     * Set the associated Servlet instance
     *
     * @param servlet The associated Servlet
     */
    default void setServlet(Servlet servlet) {
        throw new UnsupportedOperationException();
    }

    // --------------------------------------------------------- Public Methods


    /**
     * Add a new servlet initialization parameter for this servlet.
     *
     * @param name  Name of this initialization parameter to add
     * @param value Value of this initialization parameter to add
     */
    default void addInitParameter(String name, String value) {
        throw new UnsupportedOperationException();
    }


    /**
     * Add a mapping associated with the Wrapper.
     *
     * @param mapping The new wrapper mapping
     */
    default void addMapping(String mapping) {
        throw new UnsupportedOperationException();
    }


    /**
     * Add a new security role reference record to the set of records for
     * this servlet.
     *
     * @param name Role name used within this servlet
     * @param link Role name used within the web application
     */
    default void addSecurityReference(String name, String link) {
        throw new UnsupportedOperationException();
    }


    /**
     * Allocate an initialized instance of this Servlet that is ready to have
     * its <code>service()</code> method called.  The previously initialized
     * instance may be returned immediately.
     *
     * @return a new Servlet instance
     * @throws ServletException if the Servlet init() method threw
     *                          an exception
     * @throws ServletException if a loading error occurs
     */
    default Servlet allocate() throws ServletException {
        throw new UnsupportedOperationException();
    }


    /**
     * Decrement the allocation count for the servlet instance.
     *
     * @param servlet The servlet to be returned
     * @throws ServletException if a deallocation error occurs
     */
    default void deallocate(Servlet servlet) throws ServletException {
        throw new UnsupportedOperationException();
    }


    /**
     * @param name Name of the requested initialization parameter
     * @return the value for the specified initialization parameter name,
     * if any; otherwise return <code>null</code>.
     */
    default String findInitParameter(String name) {
        throw new UnsupportedOperationException();
    }


    /**
     * @return the names of all defined initialization parameters for this
     * servlet.
     */
    default String[] findInitParameters() {
        throw new UnsupportedOperationException();
    }


    /**
     * @return the mappings associated with this wrapper.
     */
    default String[] findMappings() {
        throw new UnsupportedOperationException();
    }


    /**
     * @param name Security role reference used within this servlet
     * @return the security role link for the specified security role
     * reference name, if any; otherwise return <code>null</code>.
     */
    default String findSecurityReference(String name) {
        throw new UnsupportedOperationException();
    }


    /**
     * @return the set of security role reference names associated with
     * this servlet, if any; otherwise return a zero-length array.
     */
    default String[] findSecurityReferences() {
        throw new UnsupportedOperationException();
    }


    /**
     * Increment the error count value used when monitoring.
     */
    default void incrementErrorCount() {
        throw new UnsupportedOperationException();
    }


    /**
     * Load and initialize an instance of this Servlet, if there is not already
     * at least one initialized instance.  This can be used, for example, to
     * load Servlets that are marked in the deployment descriptor to be loaded
     * at server startup time.
     *
     * @throws ServletException if the Servlet init() method threw
     *                          an exception or if some other loading problem occurs
     */
    default void load() throws ServletException {
        throw new UnsupportedOperationException();
    }


    /**
     * Remove the specified initialization parameter from this Servlet.
     *
     * @param name Name of the initialization parameter to remove
     */
    default void removeInitParameter(String name) {
        throw new UnsupportedOperationException();
    }


    /**
     * Remove a mapping associated with the wrapper.
     *
     * @param mapping The pattern to remove
     */
    default void removeMapping(String mapping) {
        throw new UnsupportedOperationException();
    }


    /**
     * Remove any security role reference for the specified role name.
     *
     * @param name Security role used within this servlet to be removed
     */
    default void removeSecurityReference(String name) {
        throw new UnsupportedOperationException();
    }


    /**
     * Process an UnavailableException, marking this Servlet as unavailable
     * for the specified amount of time.
     *
     * @param unavailable The exception that occurred, or <code>null</code>
     *                    to mark this Servlet as permanently unavailable
     */
    default void unavailable(UnavailableException unavailable) {
        throw new UnsupportedOperationException();
    }


    /**
     * Unload all initialized instances of this servlet, after calling the
     * <code>destroy()</code> method for each instance.  This can be used,
     * for example, prior to shutting down the entire servlet engine, or
     * prior to reloading all of the classes from the Loader associated with
     * our Loader's repository.
     *
     * @throws ServletException if an unload error occurs
     */
    default void unload() throws ServletException {
        throw new UnsupportedOperationException();
    }


    /**
     * @return the multi-part configuration for the associated Servlet. If no
     * multi-part configuration has been defined, then <code>null</code> will be
     * returned.
     */
    default MultipartConfigElement getMultipartConfigElement() {
        throw new UnsupportedOperationException();
    }


    /**
     * Set the multi-part configuration for the associated Servlet. To clear the
     * multi-part configuration specify <code>null</code> as the new value.
     *
     * @param multipartConfig The configuration associated with the Servlet
     */
    default void setMultipartConfigElement(
            MultipartConfigElement multipartConfig) {
        throw new UnsupportedOperationException();
    }

    /**
     * Does the associated Servlet support async processing? Defaults to
     * <code>false</code>.
     *
     * @return <code>true</code> if the Servlet supports async
     */
    default boolean isAsyncSupported() {
        throw new UnsupportedOperationException();
    }

    /**
     * Set the async support for the associated Servlet.
     *
     * @param asyncSupport the new value
     */
    default void setAsyncSupported(boolean asyncSupport) {
        throw new UnsupportedOperationException();
    }

    /**
     * Is the associated Servlet enabled? Defaults to <code>true</code>.
     *
     * @return <code>true</code> if the Servlet is enabled
     */
    default boolean isEnabled() {
        throw new UnsupportedOperationException();
    }

    /**
     * Sets the enabled attribute for the associated servlet.
     *
     * @param enabled the new value
     */
    default void setEnabled(boolean enabled) {
        throw new UnsupportedOperationException();
    }

    /**
     * Is the Servlet overridable by a ServletContainerInitializer?
     *
     * @return <code>true</code> if the Servlet can be overridden in a ServletContainerInitializer
     */
    default boolean isOverridable() {
        throw new UnsupportedOperationException();
    }

    /**
     * Sets the overridable attribute for this Servlet.
     *
     * @param overridable the new value
     */
    default void setOverridable(boolean overridable) {
        throw new UnsupportedOperationException();
    }
}
