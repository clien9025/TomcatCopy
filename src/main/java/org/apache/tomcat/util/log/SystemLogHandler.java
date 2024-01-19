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
package org.apache.tomcat.util.log;

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * This helper class may be used to do sophisticated redirection of
 * System.out and System.err on a per Thread basis.
 *
 * A stack is implemented per Thread so that nested startCapture
 * and stopCapture can be used.
 *
 * @author Remy Maucherat
 * @author Glenn L. Nielsen
 */
public class SystemLogHandler extends PrintStream {



    // ----------------------------------------------------------- Constructors


    /**
     * Construct the handler to capture the output of the given steam.
     *
     * @param wrapped The stream to capture
     */
    public SystemLogHandler(PrintStream wrapped) {
        super(wrapped);
        out = wrapped;
    }


    // ----------------------------------------------------- Instance Variables


    /**
     * Wrapped PrintStream.
     */
    private final PrintStream out;


    /**
     * Thread &lt;-&gt; CaptureLog associations.
     */
    private static final ThreadLocal<Deque<CaptureLog>> logs = new ThreadLocal<>();


    /**
     * Spare CaptureLog ready for reuse.
     */
    private static final Queue<CaptureLog> reuse = new ConcurrentLinkedQueue<>();
}
