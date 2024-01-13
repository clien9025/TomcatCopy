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

import java.io.File;
import java.util.concurrent.ScheduledExecutorService;

import org.apache.catalina.deploy.NamingResourcesImpl;
import org.apache.catalina.startup.Catalina;

/**
 * A <code>Server</code> element represents the entire Catalina
 * servlet container.  Its attributes represent the characteristics of
 * the servlet container as a whole.  A <code>Server</code> may contain
 * one or more <code>Services</code>, and the top level set of naming
 * resources.
 * <p>
 * Normally, an implementation of this interface will also implement
 * <code>Lifecycle</code>, such that when the <code>start()</code> and
 * <code>stop()</code> methods are called, all of the defined
 * <code>Services</code> are also started or stopped.
 * <p>
 * In between, the implementation must open a server socket on the port number
 * specified by the <code>port</code> property.  When a connection is accepted,
 * the first line is read and compared with the specified shutdown command.
 * If the command matches, shutdown of the server is initiated.
 *
 * @author Craig R. McClanahan
 */
public interface Server extends Lifecycle {

    // ------------------------------------------------------------- Properties

    /**
     * @return the global naming resources.
     */
    default NamingResourcesImpl getGlobalNamingResources() {
        throw new UnsupportedOperationException();
    }


    /**
     * Set the global naming resources.
     *
     * @param globalNamingResources The new global naming resources
     */
    default void setGlobalNamingResources
    (NamingResourcesImpl globalNamingResources) {
        throw new UnsupportedOperationException();
    }


    /**
     * @return the global naming resources context.
     */
    default javax.naming.Context getGlobalNamingContext() {
        throw new UnsupportedOperationException();
    }


    /**
     * @return the port number we listen to for shutdown commands.
     * @see #getPortOffset()
     * @see #getPortWithOffset()
     */
    default int getPort() {
        throw new UnsupportedOperationException();
    }


    /**
     * Set the port number we listen to for shutdown commands.
     *
     * @param port The new port number
     * @see #setPortOffset(int)
     */
    default void setPort(int port) {
        throw new UnsupportedOperationException();
    }

    /**
     * Get the number that offsets the port used for shutdown commands.
     * For example, if port is 8005, and portOffset is 1000,
     * the server listens at 9005.
     *
     * @return the port offset
     */
    default int getPortOffset() {
        throw new UnsupportedOperationException();
    }

    /**
     * Set the number that offsets the server port used for shutdown commands.
     * For example, if port is 8005, and you set portOffset to 1000,
     * connector listens at 9005.
     *
     * @param portOffset sets the port offset
     */
    default void setPortOffset(int portOffset) {
        throw new UnsupportedOperationException();
    }

    /**
     * Get the actual port on which server is listening for the shutdown commands.
     * If you do not set port offset, port is returned. If you set
     * port offset, port offset + port is returned.
     *
     * @return the port with offset
     */
    default int getPortWithOffset() {
        throw new UnsupportedOperationException();
    }

    /**
     * @return the address on which we listen to for shutdown commands.
     */
    default String getAddress() {
        throw new UnsupportedOperationException();
    }


    /**
     * Set the address on which we listen to for shutdown commands.
     *
     * @param address The new address
     */
    default void setAddress(String address) {
        throw new UnsupportedOperationException();
    }


    /**
     * @return the shutdown command string we are waiting for.
     */
    default String getShutdown() {
        throw new UnsupportedOperationException();
    }


    /**
     * Set the shutdown command we are waiting for.
     *
     * @param shutdown The new shutdown command
     */
    default void setShutdown(String shutdown) {
        throw new UnsupportedOperationException();
    }


    /**
     * @return the parent class loader for this component. If not set, return
     * {@link #getCatalina()} {@link Catalina#getParentClassLoader()}. If
     * catalina has not been set, return the system class loader.
     */
    default ClassLoader getParentClassLoader() {
        throw new UnsupportedOperationException();
    }


    /**
     * Set the parent class loader for this server.
     *
     * @param parent The new parent class loader
     */
    default void setParentClassLoader(ClassLoader parent) {
        throw new UnsupportedOperationException();
    }


    /**
     * @return the outer Catalina startup/shutdown component if present.
     */
    default Catalina getCatalina() {
        throw new UnsupportedOperationException();
    }

    /**
     * Set the outer Catalina startup/shutdown component if present.
     *
     * @param catalina the outer Catalina component
     */
    default void setCatalina(Catalina catalina) {
        throw new UnsupportedOperationException();
    }


    /**
     * @return the configured base (instance) directory. Note that home and base
     * may be the same (and are by default). If this is not set the value
     * returned by {@link #getCatalinaHome()} will be used.
     */
    default File getCatalinaBase() {
        throw new UnsupportedOperationException();
    }

    /**
     * Set the configured base (instance) directory. Note that home and base
     * may be the same (and are by default).
     *
     * @param catalinaBase the configured base directory
     */
    default void setCatalinaBase(File catalinaBase) {
        throw new UnsupportedOperationException();
    }


    /**
     * @return the configured home (binary) directory. Note that home and base
     * may be the same (and are by default).
     */
    default File getCatalinaHome() {
        throw new UnsupportedOperationException();
    }

    /**
     * Set the configured home (binary) directory. Note that home and base
     * may be the same (and are by default).
     *
     * @param catalinaHome the configured home directory
     */
    default void setCatalinaHome(File catalinaHome) {
        throw new UnsupportedOperationException();
    }


    /**
     * Get the utility thread count.
     *
     * @return the thread count
     */
    default int getUtilityThreads() {
        throw new UnsupportedOperationException();
    }


    /**
     * Set the utility thread count.
     *
     * @param utilityThreads the new thread count
     */
    default void setUtilityThreads(int utilityThreads) {
        throw new UnsupportedOperationException();
    }


    // --------------------------------------------------------- Public Methods


    /**
     * Add a new Service to the set of defined Services.
     *
     * @param service The Service to be added
     */
    default void addService(Service service) {
        throw new UnsupportedOperationException();
    }


    /**
     * Wait until a proper shutdown command is received, then return.
     */
    default void await() {
        throw new UnsupportedOperationException();
    }


    /**
     * Find the specified Service
     *
     * @param name Name of the Service to be returned
     * @return the specified Service, or <code>null</code> if none exists.
     */
    default Service findService(String name) {
        throw new UnsupportedOperationException();
    }


    /**
     * @return the set of Services defined within this Server.
     */
    default Service[] findServices() {
        throw new UnsupportedOperationException();
    }


    /**
     * Remove the specified Service from the set associated from this
     * Server.
     *
     * @param service The Service to be removed
     */
    default void removeService(Service service) {
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
     * @return the utility executor managed by the Service.
     */
    default ScheduledExecutorService getUtilityExecutor() {
        throw new UnsupportedOperationException();
    }

}
