/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.coyote;

import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletConnection;

import org.apache.tomcat.util.buf.B2CConverter;
import org.apache.tomcat.util.buf.MessageBytes;
import org.apache.tomcat.util.buf.UDecoder;
import org.apache.tomcat.util.http.MimeHeaders;
import org.apache.tomcat.util.http.Parameters;
import org.apache.tomcat.util.http.ServerCookies;
import org.apache.tomcat.util.http.parser.MediaType;
import org.apache.tomcat.util.net.ApplicationBufferHandler;
import org.apache.tomcat.util.res.StringManager;

/**
 * This is a low-level, efficient representation of a server request. Most fields are GC-free, expensive operations are
 * delayed until the user code needs the information. Processing is delegated to modules, using a hook mechanism. This
 * class is not intended for user code - it is used internally by tomcat for processing the request in the most
 * efficient way. Users ( servlets ) can access the information using a facade, which provides the high-level view of
 * the request. Tomcat defines a number of attributes:
 * <ul>
 * <li>"org.apache.tomcat.request" - allows access to the low-level request object in trusted applications
 * </ul>
 *
 * @author James Duncan Davidson [duncan@eng.sun.com]
 * @author James Todd [gonzo@eng.sun.com]
 * @author Jason Hunter [jch@eng.sun.com]
 * @author Harish Prabandham
 * @author Alex Cruikshank [alex@epitonic.com]
 * @author Hans Bergsten [hans@gefionsoftware.com]
 * @author Costin Manolache
 * @author Remy Maucherat
 */
public final class Request {

    private static final StringManager sm = StringManager.getManager(Request.class);

    // Expected maximum typical number of cookies per request.
    private static final int INITIAL_COOKIE_SIZE = 4;

    /*
     * At 100,000 requests a second there are enough IDs here for ~3,000,000 years before it overflows (and then we have
     * another 3,000,000 years before it gets back to zero).
     *
     * Local testing shows that 5, 10, 50, 500 or 1000 threads can obtain 60,000,000+ IDs a second from a single
     * AtomicLong. That is about about 17ns per request. It does not appear that the introduction of this counter will
     * cause a bottleneck for request processing.
     */
    private static final AtomicLong requestIdGenerator = new AtomicLong(0);

    // ----------------------------------------------------------- Constructors

    public Request() {
        parameters.setQuery(queryMB);
        parameters.setURLDecoder(urlDecoder);
    }


    // ----------------------------------------------------- Instance Variables

    private int serverPort = -1;
    private final MessageBytes serverNameMB = MessageBytes.newInstance();

    private int remotePort;
    private int localPort;

    private final MessageBytes schemeMB = MessageBytes.newInstance();

    private final MessageBytes methodMB = MessageBytes.newInstance();
    private final MessageBytes uriMB = MessageBytes.newInstance();
    private final MessageBytes decodedUriMB = MessageBytes.newInstance();
    private final MessageBytes queryMB = MessageBytes.newInstance();
    private final MessageBytes protoMB = MessageBytes.newInstance();

    private volatile String requestId = Long.toString(requestIdGenerator.getAndIncrement());

    // remote address/host
    private final MessageBytes remoteAddrMB = MessageBytes.newInstance();
    private final MessageBytes peerAddrMB = MessageBytes.newInstance();
    private final MessageBytes localNameMB = MessageBytes.newInstance();
    private final MessageBytes remoteHostMB = MessageBytes.newInstance();
    private final MessageBytes localAddrMB = MessageBytes.newInstance();

    private final MimeHeaders headers = new MimeHeaders();
    private final Map<String, String> trailerFields = new HashMap<>();

    /**
     * Path parameters
     */
    private final Map<String, String> pathParameters = new HashMap<>();

    /**
     * Notes.
     */
    private final Object notes[] = new Object[Constants.MAX_NOTES];


    /**
     * Associated input buffer.
     */
    private InputBuffer inputBuffer = null;


    /**
     * URL decoder.
     */
    private final UDecoder urlDecoder = new UDecoder();


    /**
     * HTTP specific fields. (remove them ?)
     */
    private long contentLength = -1;
    private MessageBytes contentTypeMB = null;
    private Charset charset = null;
    // Retain the original, user specified character encoding so it can be
    // returned even if it is invalid
    private String characterEncoding = null;

    /**
     * Is there an expectation ?
     */
    private boolean expectation = false;

    private final ServerCookies serverCookies = new ServerCookies(INITIAL_COOKIE_SIZE);
    private final Parameters parameters = new Parameters();

    private final MessageBytes remoteUser = MessageBytes.newInstance();
    private boolean remoteUserNeedsAuthorization = false;
    private final MessageBytes authType = MessageBytes.newInstance();
    private final HashMap<String, Object> attributes = new HashMap<>();

    private Response response;
    private volatile ActionHook hook;

    private long bytesRead = 0;
    // Time of the request - useful to avoid repeated calls to System.currentTime
    private long startTimeNanos = -1;
    private long threadId = 0;
    private int available = 0;

    private final RequestInfo reqProcessorMX = new RequestInfo(this);

    private boolean sendfile = true;

    /**
     * Holds request body reading error exception.
     */
    private Exception errorException = null;

    /*
     * State for non-blocking output is maintained here as it is the one point easily reachable from the
     * CoyoteInputStream and the CoyoteAdapter which both need access to state.
     */
    volatile ReadListener listener;
    // Ensures listener is only fired after a call is isReady()
    private boolean fireListener = false;
    // Tracks read registration to prevent duplicate registrations
    private boolean registeredForRead = false;
    // Lock used to manage concurrent access to above flags
    private final Object nonBlockingStateLock = new Object();

    public ReadListener getReadListener() {
        return listener;
    }

    public void setReadListener(ReadListener listener) {
//        if (listener == null) {
//            throw new NullPointerException(sm.getString("request.nullReadListener"));
//        }
//        if (getReadListener() != null) {
//            throw new IllegalStateException(sm.getString("request.readListenerSet"));
//        }
//        // Note: This class is not used for HTTP upgrade so only need to test
//        // for async
//        AtomicBoolean result = new AtomicBoolean(false);
//        action(ActionCode.ASYNC_IS_ASYNC, result);
//        if (!result.get()) {
//            throw new IllegalStateException(sm.getString("request.notAsync"));
//        }
//
//        this.listener = listener;
//
//        // The container is responsible for the first call to
//        // listener.onDataAvailable(). If isReady() returns true, the container
//        // needs to call listener.onDataAvailable() from a new thread. If
//        // isReady() returns false, the socket will be registered for read and
//        // the container will call listener.onDataAvailable() once data arrives.
//        // Must call isFinished() first as a call to isReady() if the request
//        // has been finished will register the socket for read interest and that
//        // is not required.
//        if (!isFinished() && isReady()) {
//            synchronized (nonBlockingStateLock) {
//                // Ensure we don't get multiple read registrations
//                registeredForRead = true;
//                // Need to set the fireListener flag otherwise when the
//                // container tries to trigger onDataAvailable, nothing will
//                // happen
//                fireListener = true;
//            }
//            action(ActionCode.DISPATCH_READ, null);
//            if (!isRequestThread()) {
//                // Not on a container thread so need to execute the dispatch
//                action(ActionCode.DISPATCH_EXECUTE, null);
//            }
//        }
        throw new UnsupportedOperationException();
    }

    public boolean isReady() {
//        // Assume read is not possible
//        boolean ready = false;
//        synchronized (nonBlockingStateLock) {
//            if (registeredForRead) {
//                fireListener = true;
//                return false;
//            }
//            ready = checkRegisterForRead();
//            fireListener = !ready;
//        }
//        return ready;
        throw new UnsupportedOperationException();
    }

    private boolean checkRegisterForRead() {
//        AtomicBoolean ready = new AtomicBoolean(false);
//        synchronized (nonBlockingStateLock) {
//            if (!registeredForRead) {
//                action(ActionCode.NB_READ_INTEREST, ready);
//                registeredForRead = !ready.get();
//            }
//        }
//        return ready.get();
        throw new UnsupportedOperationException();
    }

    public void onDataAvailable() throws IOException {
//        boolean fire = false;
//        synchronized (nonBlockingStateLock) {
//            registeredForRead = false;
//            if (fireListener) {
//                fireListener = false;
//                fire = true;
//            }
//        }
//        if (fire) {
//            listener.onDataAvailable();
//        }
        throw new UnsupportedEncodingException();
    }


    private final AtomicBoolean allDataReadEventSent = new AtomicBoolean(false);

    public boolean sendAllDataReadEvent() {
        return allDataReadEventSent.compareAndSet(false, true);
    }


    // ------------------------------------------------------------- Properties

    public MimeHeaders getMimeHeaders() {
        return headers;
    }


    public boolean isTrailerFieldsReady() {
//        AtomicBoolean result = new AtomicBoolean(false);
//        action(ActionCode.IS_TRAILER_FIELDS_READY, result);
//        return result.get();
        throw new UnsupportedOperationException();
    }


    public Map<String, String> getTrailerFields() {
        return trailerFields;
    }


    public UDecoder getURLDecoder() {
        return urlDecoder;
    }


    // -------------------- Request data --------------------

    public MessageBytes scheme() {
        return schemeMB;
    }

    public MessageBytes method() {
        return methodMB;
    }

    public MessageBytes requestURI() {
        return uriMB;
    }

    public MessageBytes decodedURI() {
        return decodedUriMB;
    }

    public MessageBytes queryString() {
        return queryMB;
    }

    public MessageBytes protocol() {
        return protoMB;
    }

    /**
     * Get the "virtual host", derived from the Host: header associated with this request.
     *
     * @return The buffer holding the server name, if any. Use isNull() to check if there is no value set.
     */
    public MessageBytes serverName() {
        return serverNameMB;
    }

    public int getServerPort() {
        return serverPort;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    public MessageBytes remoteAddr() {
        return remoteAddrMB;
    }

    public MessageBytes peerAddr() {
        return peerAddrMB;
    }

    public MessageBytes remoteHost() {
        return remoteHostMB;
    }

    public MessageBytes localName() {
        return localNameMB;
    }

    public MessageBytes localAddr() {
        return localAddrMB;
    }

    public int getRemotePort() {
        return remotePort;
    }

    public void setRemotePort(int port) {
        this.remotePort = port;
    }

    public int getLocalPort() {
        return localPort;
    }

    public void setLocalPort(int port) {
        this.localPort = port;
    }


    // -------------------- encoding/type --------------------
}
