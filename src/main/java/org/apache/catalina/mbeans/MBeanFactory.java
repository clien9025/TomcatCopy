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

import java.io.File;
import java.net.InetAddress;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.apache.catalina.Container;
import org.apache.catalina.Context;
import org.apache.catalina.Engine;
import org.apache.catalina.Host;
import org.apache.catalina.JmxEnabled;
import org.apache.catalina.Realm;
import org.apache.catalina.Server;
import org.apache.catalina.Service;
import org.apache.catalina.Valve;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.core.StandardEngine;
import org.apache.catalina.core.StandardHost;
import org.apache.catalina.core.StandardService;
import org.apache.catalina.loader.WebappLoader;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.res.StringManager;


/**
 * @author Amy Roh
 */
public class MBeanFactory {

    private static final Log log = LogFactory.getLog(MBeanFactory.class);

    protected static final StringManager sm = StringManager.getManager(MBeanFactory.class);

    /**
     * The <code>MBeanServer</code> for this application.
     */
    private static final MBeanServer mserver = MBeanUtils.createServer();


    // ------------------------------------------------------------- Attributes

    /**
     * The container (Server/Service) for which this factory was created.
     */
    private Object container;


    // ------------------------------------------------------------- Operations

    /**
     * Set the container that this factory was created for.
     *
     * @param container The associated container
     */
    public void setContainer(Object container) {
        this.container = container;
    }


    /**
     * Little convenience method to remove redundant code when retrieving the path string
     *
     * @param t path string
     *
     * @return empty string if t==null || t.equals("/")
     */
    private String getPathStr(String t) {
        if (t == null || t.equals("/")) {
            return "";
        }
        return t;
    }


}
