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
package org.apache.catalina.core;


import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import javax.management.ObjectName;
import javax.naming.NamingException;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;

import org.apache.catalina.Context;
import org.apache.catalina.Globals;
import org.apache.catalina.security.SecurityUtil;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.ExceptionUtils;
import org.apache.tomcat.util.descriptor.web.FilterDef;
import org.apache.tomcat.util.log.SystemLogHandler;
import org.apache.tomcat.util.modeler.Registry;
import org.apache.tomcat.util.modeler.Util;
import org.apache.tomcat.util.res.StringManager;


/**
 * Implementation of a <code>jakarta.servlet.FilterConfig</code> useful in managing the filter instances instantiated
 * when a web application is first started.
 *
 * @author Craig R. McClanahan
 */
public final class ApplicationFilterConfig implements /*FilterConfig, */Serializable {

    private static final long serialVersionUID = 1L;

    static final StringManager sm = StringManager.getManager(ApplicationFilterConfig.class);

    private transient Log log = LogFactory.getLog(ApplicationFilterConfig.class); // must not be static

    /**
     * Empty String collection to serve as the basis for empty enumerations.
     */
    private static final List<String> emptyString = Collections.emptyList();

    // ----------------------------------------------------------- Constructors


    /**
     * Construct a new ApplicationFilterConfig for the specified filter definition.
     *
     * @param context   The context with which we are associated
     * @param filterDef Filter definition for which a FilterConfig is to be constructed
     *
     * @exception ClassCastException     if the specified class does not implement the
     *                                       <code>jakarta.servlet.Filter</code> interface
     * @exception ClassNotFoundException if the filter class cannot be found
     * @exception IllegalAccessException if the filter class cannot be publicly instantiated
     * @exception InstantiationException if an exception occurs while instantiating the filter object
     * @exception ServletException       if thrown by the filter's init() method
     *
     * @throws NamingException          If a JNDI lookup fails
     * @throws SecurityException        If a security manager prevents the creation
     * @throws IllegalArgumentException If the provided configuration is not valid
     */
    ApplicationFilterConfig(Context context, FilterDef filterDef)
            throws ClassCastException, ReflectiveOperationException, ServletException, NamingException,
            IllegalArgumentException, SecurityException {

        super();

        this.context = context;
        this.filterDef = filterDef;
        // Allocate a new filter instance if necessary
        if (filterDef.getFilter() == null) {
            getFilter();
        } else {
            this.filter = filterDef.getFilter();
            context.getInstanceManager().newInstance(filter);
            initFilter();
        }
    }


    // ----------------------------------------------------- Instance Variables


    /**
     * The Context with which we are associated.
     */
    private final transient Context context;


    /**
     * The application Filter we are configured for.
     */
    private transient Filter filter = null;


    /**
     * The <code>FilterDef</code> that defines our associated Filter.
     */
    private final FilterDef filterDef;

    /**
     * JMX registration name
     */
    private ObjectName oname;




    // -------------------------------------------------------- Package Methods


    /**
     * Return the application Filter we are configured for.
     *
     * @exception ClassCastException     if the specified class does not implement the
     *                                       <code>jakarta.servlet.Filter</code> interface
     * @exception ClassNotFoundException if the filter class cannot be found
     * @exception IllegalAccessException if the filter class cannot be publicly instantiated
     * @exception InstantiationException if an exception occurs while instantiating the filter object
     * @exception ServletException       if thrown by the filter's init() method
     *
     * @throws NamingException              If a JNDI lookup fails
     * @throws ReflectiveOperationException If the creation of the filter fails
     * @throws SecurityException            If a security manager prevents the creation
     * @throws IllegalArgumentException     If the provided configuration is not valid
     */
    Filter getFilter() throws ClassCastException, ReflectiveOperationException, ServletException, NamingException,
            IllegalArgumentException, SecurityException {

//        // Return the existing filter instance, if any
//        if (this.filter != null) {
//            return this.filter;
//        }
//
//        // Identify the class loader we will be using
//        String filterClass = filterDef.getFilterClass();
//        this.filter = (Filter) context.getInstanceManager().newInstance(filterClass);
//
//        initFilter();
//
//        return this.filter;
        throw new UnsupportedOperationException();

    }

    private void initFilter() throws ServletException {
//        if (context instanceof StandardContext && context.getSwallowOutput()) {
//            try {
//                SystemLogHandler.startCapture();
//                filter.init(this);
//            } finally {
//                String capturedlog = SystemLogHandler.stopCapture();
//                if (capturedlog != null && capturedlog.length() > 0) {
//                    getServletContext().log(capturedlog);
//                }
//            }
//        } else {
//            filter.init(this);
//        }
//
//        // Expose filter via JMX
//        registerJMX();
        throw new UnsupportedOperationException();
    }
}
