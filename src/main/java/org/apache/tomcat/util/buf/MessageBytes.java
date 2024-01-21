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
package org.apache.tomcat.util.buf;

import java.io.IOException;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.Locale;

import org.apache.tomcat.util.res.StringManager;

/**
 * This class is used to represent a subarray of bytes in an HTTP message. It represents all request/response elements.
 * The byte/char conversions are delayed and cached. Everything is recyclable.
 * <p>
 * The object can represent a byte[], a char[], or a (sub) String. All operations can be made in case sensitive mode or
 * not.
 *
 * @author dac@eng.sun.com
 * @author James Todd [gonzo@eng.sun.com]
 * @author Costin Manolache
 */
public final class MessageBytes implements Cloneable, Serializable {

    private static final long serialVersionUID = 1L;

    private static final StringManager sm = StringManager.getManager(MessageBytes.class);

    // primary type ( whatever is set as original value )
    private int type = T_NULL;

    public static final int T_NULL = 0;
    /**
     * getType() is T_STR if the the object used to create the MessageBytes was a String.
     */
    public static final int T_STR = 1;
    /**
     * getType() is T_BYTES if the the object used to create the MessageBytes was a byte[].
     */
    public static final int T_BYTES = 2;
    /**
     * getType() is T_CHARS if the the object used to create the MessageBytes was a char[].
     */
    public static final int T_CHARS = 3;

    public static final char[] EMPTY_CHAR_ARRAY = new char[0];

    private int hashCode = 0;
    // did we compute the hashcode ?
    private boolean hasHashCode = false;

    // Internal objects to represent array + offset, and specific methods
    private final ByteChunk byteC = new ByteChunk();
    private final CharChunk charC = new CharChunk();

    // String
    private String strValue;

    /**
     * Creates a new, uninitialized MessageBytes object. Use static newInstance() in order to allow future hooks.
     */
    private MessageBytes() {
    }

    /**
     * Construct a new MessageBytes instance.
     *
     * @return the instance
     */
    public static MessageBytes newInstance() {
        return factory.newInstance();
    }



    // -------------------- Future may be different --------------------

    private static final MessageBytesFactory factory = new MessageBytesFactory();

    private static class MessageBytesFactory {
        protected MessageBytesFactory() {
        }

        public MessageBytes newInstance() {
            return new MessageBytes();
        }
    }
}
