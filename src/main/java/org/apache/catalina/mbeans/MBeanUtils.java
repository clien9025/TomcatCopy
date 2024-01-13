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
package org.apache.catalina.mbeans;

import java.util.Set;

import javax.management.DynamicMBean;
import javax.management.MBeanException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import org.apache.catalina.Container;
import org.apache.catalina.Context;
import org.apache.catalina.Loader;
import org.apache.catalina.Server;
import org.apache.catalina.util.ContextName;
import org.apache.tomcat.util.descriptor.web.ContextEnvironment;
import org.apache.tomcat.util.descriptor.web.ContextResource;
import org.apache.tomcat.util.descriptor.web.ContextResourceLink;
import org.apache.tomcat.util.modeler.ManagedBean;
import org.apache.tomcat.util.modeler.Registry;
import org.apache.tomcat.util.res.StringManager;


/**
 * Public utility methods in support of the server side MBeans implementation.
 *
 * @author Craig R. McClanahan
 * @author Amy Roh
 */
public class MBeanUtils {

    // ------------------------------------------------------- Static Variables

    protected static final StringManager sm = StringManager.getManager(MBeanUtils.class);

    /**
     * The set of exceptions to the normal rules used by <code>createManagedBean()</code>. The first element of each
     * pair is a class name, and the second element is the managed bean name.
     */
    private static final String exceptions[][] = { { "org.apache.catalina.users.MemoryGroup", "Group" },
            { "org.apache.catalina.users.MemoryRole", "Role" }, { "org.apache.catalina.users.MemoryUser", "User" },
            { "org.apache.catalina.users.GenericGroup", "Group" }, { "org.apache.catalina.users.GenericRole", "Role" },
            { "org.apache.catalina.users.GenericUser", "User" } };


    /**
     * The configuration information registry for our managed beans.
     */
    private static Registry registry = createRegistry();


    /**
     * The <code>MBeanServer</code> for this application.
     */
    private static MBeanServer mserver = createServer();




    // --------------------------------------------------------- Static Methods


    /**
     * Create and configure (if necessary) and return the registry of managed object descriptions.
     *
     * @return the singleton registry
     */
    public static synchronized Registry createRegistry() {
        if (registry == null) {
            registry = Registry.getRegistry(null, null);
            ClassLoader cl = MBeanUtils.class.getClassLoader();

            registry.loadDescriptors("org.apache.catalina.mbeans", cl);
            registry.loadDescriptors("org.apache.catalina.authenticator", cl);
            registry.loadDescriptors("org.apache.catalina.core", cl);
            registry.loadDescriptors("org.apache.catalina", cl);
            registry.loadDescriptors("org.apache.catalina.deploy", cl);
            registry.loadDescriptors("org.apache.catalina.loader", cl);
            registry.loadDescriptors("org.apache.catalina.realm", cl);
            registry.loadDescriptors("org.apache.catalina.session", cl);
            registry.loadDescriptors("org.apache.catalina.startup", cl);
            registry.loadDescriptors("org.apache.catalina.users", cl);
            registry.loadDescriptors("org.apache.catalina.ha", cl);
            registry.loadDescriptors("org.apache.catalina.connector", cl);
            registry.loadDescriptors("org.apache.catalina.valves", cl);
            registry.loadDescriptors("org.apache.catalina.storeconfig", cl);
            registry.loadDescriptors("org.apache.tomcat.util.descriptor.web", cl);
        }
        return registry;
    }

    /**
     * Create and configure (if necessary) and return the <code>MBeanServer</code> with which we will be registering our
     * <code>DynamicMBean</code> implementations.
     *
     * @return the singleton MBean server
     */
    public static synchronized MBeanServer createServer() {
        if (mserver == null) {
            mserver = Registry.getRegistry(null, null).getMBeanServer();
        }
        return mserver;
    }


}
