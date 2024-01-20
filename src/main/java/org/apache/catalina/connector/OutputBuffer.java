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
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.HashMap;
import java.util.Map;

import jakarta.servlet.WriteListener;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.catalina.Globals;
import org.apache.coyote.ActionCode;
import org.apache.coyote.CloseNowException;
import org.apache.coyote.Response;
import org.apache.tomcat.util.buf.B2CConverter;
import org.apache.tomcat.util.buf.C2BConverter;
import org.apache.tomcat.util.res.StringManager;

/**
 * The buffer used by Tomcat response. This is a derivative of the Tomcat 3.3 OutputBuffer, with the removal of some of
 * the state handling (which in Coyote is mostly the Processor's responsibility).
 *
 * @author Costin Manolache
 * @author Remy Maucherat
 */
public class OutputBuffer extends Writer {

    private static final StringManager sm = StringManager.getManager(OutputBuffer.class);

    public static final int DEFAULT_BUFFER_SIZE = 8 * 1024;

    /**
     * Encoder cache.
     */
    private final Map<Charset,C2BConverter> encoders = new HashMap<>();


    /**
     * Default buffer size.
     */
    private final int defaultBufferSize;

    // ----------------------------------------------------- Instance Variables

    /**
     * The byte buffer.
     */
    private ByteBuffer bb;


    /**
     * The char buffer.
     */
    private final CharBuffer cb;


    /**
     * State of the output buffer.
     */
    private boolean initial = true;


    /**
     * Number of bytes written.
     */
    private long bytesWritten = 0;


    /**
     * Number of chars written.
     */
    private long charsWritten = 0;


    /**
     * Flag which indicates if the output buffer is closed.
     */
    private volatile boolean closed = false;


    /**
     * Do a flush on the next operation.
     */
    private boolean doFlush = false;


    /**
     * Current char to byte converter.
     */
    protected C2BConverter conv;


    /**
     * Associated Coyote response.
     */
    private Response coyoteResponse;


    /**
     * Suspended flag. All output bytes will be swallowed if this is true.
     */
    private volatile boolean suspended = false;


    // ----------------------------------------------------------- Constructors

    /**
     * Create the buffer with the specified initial size.
     *
     * @param size Buffer size to use
     */
    public OutputBuffer(int size) {
        defaultBufferSize = size;
        bb = ByteBuffer.allocate(size);
        clear(bb);
        cb = CharBuffer.allocate(size);
        clear(cb);
    }


    // ------------------------------------------------------------- Properties

    /**
     * Associated Coyote response.
     *
     * @param coyoteResponse Associated Coyote response
     */
    public void setResponse(Response coyoteResponse) {
        this.coyoteResponse = coyoteResponse;
    }


    /**
     * Is the response output suspended ?
     *
     * @return suspended flag value
     */
    public boolean isSuspended() {
        return this.suspended;
    }


    /**
     * Set the suspended flag.
     *
     * @param suspended New suspended flag value
     */
    public void setSuspended(boolean suspended) {
        this.suspended = suspended;
    }


    /**
     * Is the response output closed ?
     *
     * @return closed flag value
     */
    public boolean isClosed() {
        return this.closed;
    }



    // --------------------------------------------------------- Public Methods

    /**
     * Recycle the output buffer.
     */
    public void recycle() {

//        initial = true;
//        bytesWritten = 0;
//        charsWritten = 0;
//
//        if (bb.capacity() > 16 * defaultBufferSize) {
//            // Discard buffers which are too large
//            bb = ByteBuffer.allocate(defaultBufferSize);
//        }
//        clear(bb);
//        clear(cb);
//        closed = false;
//        suspended = false;
//        doFlush = false;
//
//        if (conv != null) {
//            conv.recycle();
//            conv = null;
//        }
        throw new UnsupportedOperationException();
    }


    /**
     * Close the output buffer. This tries to calculate the response size if the response has not been committed yet.
     *
     * @throws IOException An underlying IOException occurred
     */
    @Override
    public void close() throws IOException {

//        if (closed) {
//            return;
//        }
//        if (suspended) {
//            return;
//        }
//
//        // If there are chars, flush all of them to the byte buffer now as bytes are used to
//        // calculate the content-length (if everything fits into the byte buffer, of course).
//        if (cb.remaining() > 0) {
//            flushCharBuffer();
//        }
//
//        if ((!coyoteResponse.isCommitted()) && (coyoteResponse.getContentLengthLong() == -1)) {
//            // If this didn't cause a commit of the response, the final content
//            // length can be calculated.
//            if (!coyoteResponse.isCommitted()) {
//                coyoteResponse.setContentLength(bb.remaining());
//            }
//        }
//
//        if (coyoteResponse.getStatus() == HttpServletResponse.SC_SWITCHING_PROTOCOLS) {
//            doFlush(true);
//        } else {
//            doFlush(false);
//        }
//        closed = true;
//
//        // The request should have been completely read by the time the response
//        // is closed. Further reads of the input a) are pointless and b) really
//        // confuse AJP (bug 50189) so close the input buffer to prevent them.
//        Request req = (Request) coyoteResponse.getRequest().getNote(CoyoteAdapter.ADAPTER_NOTES);
//        req.inputBuffer.close();
//
//        coyoteResponse.action(ActionCode.CLOSE, null);
        throw new UnsupportedEncodingException();
    }


    /**
     * Flush bytes or chars contained in the buffer.
     *
     * @throws IOException An underlying IOException occurred
     */
    @Override
    public void flush() throws IOException {
//        doFlush(true);
        throw new UnsupportedEncodingException();
    }


    /**
     * Flush bytes or chars contained in the buffer.
     *
     * @param realFlush <code>true</code> if this should also cause a real network flush
     *
     * @throws IOException An underlying IOException occurred
     */
    protected void doFlush(boolean realFlush) throws IOException {

//        if (suspended) {
//            return;
//        }
//
//        try {
//            doFlush = true;
//            if (initial) {
//                coyoteResponse.sendHeaders();
//                initial = false;
//            }
//            if (cb.remaining() > 0) {
//                flushCharBuffer();
//            }
//            if (bb.remaining() > 0) {
//                flushByteBuffer();
//            }
//        } finally {
//            doFlush = false;
//        }
//
//        if (realFlush) {
//            coyoteResponse.action(ActionCode.CLIENT_FLUSH, null);
//            // If some exception occurred earlier, or if some IOE occurred
//            // here, notify the servlet with an IOE
//            if (coyoteResponse.isExceptionPresent()) {
//                throw new ClientAbortException(coyoteResponse.getErrorException());
//            }
//        }
        throw new UnsupportedEncodingException();

    }


    // ------------------------------------------------- Bytes Handling Methods

    /**
     * Sends the buffer data to the client output, checking the state of Response and calling the right interceptors.
     *
     * @param buf the ByteBuffer to be written to the response
     *
     * @throws IOException An underlying IOException occurred
     */
    public void realWriteBytes(ByteBuffer buf) throws IOException {

//        if (closed) {
//            return;
//        }
//
//        // If we really have something to write
//        if (buf.remaining() > 0) {
//            // real write to the adapter
//            try {
//                coyoteResponse.doWrite(buf);
//            } catch (CloseNowException e) {
//                // Catch this sub-class as it requires specific handling.
//                // Examples where this exception is thrown:
//                // - HTTP/2 stream timeout
//                // Prevent further output for this response
//                closed = true;
//                throw e;
//            } catch (IOException e) {
//                // An IOException on a write is almost always due to
//                // the remote client aborting the request. Wrap this
//                // so that it can be handled better by the error dispatcher.
//                coyoteResponse.setErrorException(e);
//                throw new ClientAbortException(e);
//            }
//        }
        throw new UnsupportedEncodingException();

    }


    public void write(byte b[], int off, int len) throws IOException {

//        if (suspended) {
//            return;
//        }
//
//        writeBytes(b, off, len);
        throw new UnsupportedEncodingException();

    }


    public void write(ByteBuffer from) throws IOException {

//        if (suspended) {
//            return;
//        }
//
//        writeBytes(from);
        throw new UnsupportedEncodingException();

    }


    private void writeBytes(byte b[], int off, int len) throws IOException {

//        if (closed) {
//            return;
//        }
//
//        append(b, off, len);
//        bytesWritten += len;
//
//        // if called from within flush(), then immediately flush
//        // remaining bytes
//        if (doFlush) {
//            flushByteBuffer();
//        }
        throw new UnsupportedEncodingException();

    }


    private void writeBytes(ByteBuffer from) throws IOException {

//        if (closed) {
//            return;
//        }
//
//        int remaining = from.remaining();
//        append(from);
//        bytesWritten += remaining;
//
//        // if called from within flush(), then immediately flush
//        // remaining bytes
//        if (doFlush) {
//            flushByteBuffer();
//        }
        throw new UnsupportedEncodingException();

    }


    public void writeByte(int b) throws IOException {

//        if (suspended) {
//            return;
//        }
//
//        if (isFull(bb)) {
//            flushByteBuffer();
//        }
//
//        transfer((byte) b, bb);
//        bytesWritten++;
        throw new UnsupportedEncodingException();

    }


    // ------------------------------------------------- Chars Handling Methods


    /**
     * Convert the chars to bytes, then send the data to the client.
     *
     * @param from Char buffer to be written to the response
     *
     * @throws IOException An underlying IOException occurred
     */
    public void realWriteChars(CharBuffer from) throws IOException {

//        while (from.remaining() > 0) {
//            conv.convert(from, bb);
//            if (bb.remaining() == 0) {
//                // Break out of the loop if more chars are needed to produce any output
//                break;
//            }
//            if (from.remaining() > 0) {
//                flushByteBuffer();
//            } else if (conv.isUndeflow() && bb.limit() > bb.capacity() - 4) {
//                // Handle an edge case. There are no more chars to write at the
//                // moment but there is a leftover character in the converter
//                // which must be part of a surrogate pair. The byte buffer does
//                // not have enough space left to output the bytes for this pair
//                // once it is complete )it will require 4 bytes) so flush now to
//                // prevent the bytes for the leftover char and the rest of the
//                // surrogate pair yet to be written from being lost.
//                // See TestOutputBuffer#testUtf8SurrogateBody()
//                flushByteBuffer();
//            }
//        }
        throw new UnsupportedEncodingException();

    }

    @Override
    public void write(int c) throws IOException {

//        if (suspended) {
//            return;
//        }
//
//        if (isFull(cb)) {
//            flushCharBuffer();
//        }
//
//        transfer((char) c, cb);
//        charsWritten++;
        throw new UnsupportedEncodingException();

    }


    @Override
    public void write(char c[]) throws IOException {

//        if (suspended) {
//            return;
//        }
//
//        write(c, 0, c.length);
        throw new UnsupportedEncodingException();

    }


    @Override
    public void write(char c[], int off, int len) throws IOException {

//        if (suspended) {
//            return;
//        }
//
//        append(c, off, len);
//        charsWritten += len;
        throw new UnsupportedEncodingException();
    }


    /**
     * Append a string to the buffer
     */
    @Override
    public void write(String s, int off, int len) throws IOException {

//        if (suspended) {
//            return;
//        }
//
//        if (s == null) {
//            throw new NullPointerException(sm.getString("outputBuffer.writeNull"));
//        }
//
//        int sOff = off;
//        int sEnd = off + len;
//        while (sOff < sEnd) {
//            int n = transfer(s, sOff, sEnd - sOff, cb);
//            sOff += n;
//            if (sOff < sEnd && isFull(cb)) {
//                flushCharBuffer();
//            }
//        }
//
//        charsWritten += len;
        throw new UnsupportedEncodingException();
    }


    @Override
    public void write(String s) throws IOException {

//        if (suspended) {
//            return;
//        }
//
//        if (s == null) {
//            s = "null";
//        }
//        write(s, 0, s.length());
        throw new UnsupportedEncodingException();
    }


    public void checkConverter() throws IOException {
//        if (conv != null) {
//            return;
//        }
//
//        Charset charset = coyoteResponse.getCharset();
//
//        if (charset == null) {
//            if (coyoteResponse.getCharacterEncoding() != null) {
//                // setCharacterEncoding() was called with an invalid character set
//                // Trigger an UnsupportedEncodingException
//                charset = B2CConverter.getCharset(coyoteResponse.getCharacterEncoding());
//            }
//            charset = org.apache.coyote.Constants.DEFAULT_BODY_CHARSET;
//        }
//
//        conv = encoders.get(charset);
//
//        if (conv == null) {
//            conv = createConverter(charset);
//            encoders.put(charset, conv);
//        }
        throw new UnsupportedEncodingException();
    }


    private static C2BConverter createConverter(final Charset charset) throws IOException {
//        if (Globals.IS_SECURITY_ENABLED) {
//            try {
//                return AccessController.doPrivileged(new PrivilegedCreateConverter(charset));
//            } catch (PrivilegedActionException ex) {
//                Exception e = ex.getException();
//                if (e instanceof IOException) {
//                    throw (IOException) e;
//                } else {
//                    throw new IOException(ex);
//                }
//            }
//        } else {
//            return new C2BConverter(charset);
//        }
        throw new UnsupportedEncodingException();
    }


    // -------------------- BufferedOutputStream compatibility



    // todo 额外的
    private void clear(Buffer buffer) {
//        buffer.rewind().limit(0);
        throw new UnsupportedOperationException();
    }

}
