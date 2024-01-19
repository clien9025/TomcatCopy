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
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.LongAdder;

import jakarta.servlet.DispatcherType;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.Servlet;
import jakarta.servlet.ServletException;
import jakarta.servlet.UnavailableException;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.catalina.Container;
import org.apache.catalina.Context;
import org.apache.catalina.Globals;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.connector.ClientAbortException;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.valves.ValveBase;
import org.apache.coyote.CloseNowException;
import org.apache.tomcat.util.ExceptionUtils;
//import org.apache.tomcat.util.buf.MessageBytes;
import org.apache.tomcat.util.log.SystemLogHandler;
import org.apache.tomcat.util.res.StringManager;

/**
 * Valve that implements the default basic behavior for the <code>StandardWrapper</code> container implementation.
 *
 * @author Craig R. McClanahan
 */
final class StandardWrapperValve extends ValveBase {

    private static final StringManager sm = StringManager.getManager(StandardWrapperValve.class);


    // ------------------------------------------------------ Constructor

    StandardWrapperValve() {
        super(true);
    }


    // ----------------------------------------------------- Instance Variables

    // Some JMX statistics. This valve is associated with a StandardWrapper.
    // We expose the StandardWrapper as JMX ( j2eeType=Servlet ). The fields
    // are here for performance.
    private final LongAdder processingTime = new LongAdder();
    private volatile long maxTime;
    private volatile long minTime = Long.MAX_VALUE;
    private final AtomicInteger requestCount = new AtomicInteger(0);
    private final AtomicInteger errorCount = new AtomicInteger(0);
}
