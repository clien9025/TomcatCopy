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

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.SessionTrackingMode;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;

import org.apache.catalina.Context;
import org.apache.catalina.Session;
import org.apache.catalina.security.SecurityUtil;
import org.apache.catalina.util.SessionConfig;
import org.apache.coyote.ActionCode;
import org.apache.coyote.ContinueResponseTiming;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.buf.CharChunk;
import org.apache.tomcat.util.buf.UEncoder;
import org.apache.tomcat.util.buf.UEncoder.SafeCharsSet;
import org.apache.tomcat.util.buf.UriUtil;
import org.apache.tomcat.util.http.FastHttpDateFormat;
import org.apache.tomcat.util.http.MimeHeaders;
import org.apache.tomcat.util.http.parser.MediaTypeCache;
import org.apache.tomcat.util.res.StringManager;
import org.apache.tomcat.util.security.Escape;

/**
 * Wrapper object for the Coyote response.
 *
 * @author Remy Maucherat
 * @author Craig R. McClanahan
 */
public class Response/* implements HttpServletResponse*/ {

    private static final Log log = LogFactory.getLog(Response.class);
    protected static final StringManager sm = StringManager.getManager(Response.class);

    private static final MediaTypeCache MEDIA_TYPE_CACHE = new MediaTypeCache(100);

    // ----------------------------------------------------- Instance Variables

    public Response() {
        this(OutputBuffer.DEFAULT_BUFFER_SIZE);
    }


    public Response(int outputBufferSize) {
        outputBuffer = new OutputBuffer(outputBufferSize);
    }


    // ------------------------------------------------------------- Properties

    /**
     * Coyote response.
     */
    protected org.apache.coyote.Response coyoteResponse;

    /**
     * Set the Coyote response.
     *
     * @param coyoteResponse The Coyote response
     */
    public void setCoyoteResponse(org.apache.coyote.Response coyoteResponse) {
        this.coyoteResponse = coyoteResponse;
        outputBuffer.setResponse(coyoteResponse);
    }

    /**
     * @return the Coyote response.
     */
    public org.apache.coyote.Response getCoyoteResponse() {
        return this.coyoteResponse;
    }


    /**
     * @return the Context within which this Request is being processed.
     */
    public Context getContext() {
//        return request.getContext();
        throw new UnsupportedOperationException();
    }


    /**
     * The associated output buffer.
     */
    protected final OutputBuffer outputBuffer;


    /**
     * The associated output stream.
     */
    protected CoyoteOutputStream outputStream;


    /**
     * The associated writer.
     */
    protected CoyoteWriter writer;


    /**
     * The application commit flag.
     */
    protected boolean appCommitted = false;


    /**
     * The included flag.
     */
    protected boolean included = false;


    /**
     * The characterEncoding flag
     */
    private boolean isCharacterEncodingSet = false;


    /**
     * Using output stream flag.
     */
    protected boolean usingOutputStream = false;


    /**
     * Using writer flag.
     */
    protected boolean usingWriter = false;


    /**
     * URL encoder.
     */
    protected final UEncoder urlEncoder = new UEncoder(SafeCharsSet.WITH_SLASH);


    /**
     * Recyclable buffer to hold the redirect URL.
     */
    protected final CharChunk redirectURLCC = new CharChunk();


    /*
     * Not strictly required but it makes generating HTTP/2 push requests a lot easier if these are retained until the
     * response is recycled.
     */
    private final List<Cookie> cookies = new ArrayList<>();

    private HttpServletResponse applicationResponse = null;


    // --------------------------------------------------------- Public Methods


}
