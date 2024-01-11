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
package org.apache.catalina.valves;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.util.ErrorPageSupport;
import org.apache.catalina.util.IOTools;
import org.apache.tomcat.util.ExceptionUtils;
import org.apache.tomcat.util.descriptor.web.ErrorPage;
import org.apache.tomcat.util.res.StringManager;

/**
 * <p>
 * Implementation of a Valve that outputs HTML error pages.
 * </p>
 * <p>
 * This Valve should be attached at the Host level, although it will work if attached to a Context.
 * </p>
 * <p>
 * HTML code from the Cocoon 2 project.
 * </p>
 *
 * @author Remy Maucherat
 * @author Craig R. McClanahan
 * @author <a href="mailto:nicolaken@supereva.it">Nicola Ken Barozzi</a> Aisa
 * @author <a href="mailto:stefano@apache.org">Stefano Mazzocchi</a>
 * @author Yoav Shapira
 */
public class ErrorReportValve extends ValveBase {

    private boolean showReport = true;

    private boolean showServerInfo = true;

    private final ErrorPageSupport errorPageSupport = new ErrorPageSupport();


    // ------------------------------------------------------ Constructor

    public ErrorReportValve() {
        super(true);
    }



    /**
     * Enables/Disables server info on error pages
     *
     * @param showServerInfo <code>true</code> to show server info
     */
    public void setShowServerInfo(boolean showServerInfo) {
        this.showServerInfo = showServerInfo;
    }

    /**
     * Enables/Disables full error reports
     *
     * @param showReport <code>true</code> to show full error data
     */
    public void setShowReport(boolean showReport) {
        this.showReport = showReport;
    }

}
