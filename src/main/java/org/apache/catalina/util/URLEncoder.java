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
package org.apache.catalina.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.BitSet;

/**
 * This class is very similar to the java.net.URLEncoder class.
 * <p>
 * Unfortunately, with java.net.URLEncoder there is no way to specify to the
 * java.net.URLEncoder which characters should NOT be encoded.
 * <p>
 * This code was moved from DefaultServlet.java
 * <p>
 * 这段代码是Tomcat中org.apache.catalina.util.URLEncoder类的实现。它提供了一个定制化的URL编码功能，
 * 与Java标准库中的java.net.URLEncoder类相似，但提供了更多控制。主要特点和功能包括：
 * <p>
 * 定制安全字符集：允许指定哪些字符被认为是安全的，不需要进行URL编码。
 * 特殊处理空格字符：提供了将空格字符编码为加号（+）的选项。
 * 支持不同字符集：能够根据指定的字符集对路径进行编码。
 * <p>
 * 这个类定义了两个预设的 编码器 DEFAULT 和 QUERY，分别用于 URI路径 和 查询字符串的编码，它们预设了不同的安全字符集。
 *
 * @author Craig R. McClanahan
 * @author Remy Maucherat
 */
public final class URLEncoder implements Cloneable {

    private static final char[] hexadecimal =
            {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    public static final URLEncoder DEFAULT = new URLEncoder();
    public static final URLEncoder QUERY = new URLEncoder();

    static {
        /*
         * Encoder for URI paths, so from the spec:
         *
         * pchar = unreserved / pct-encoded / sub-delims / ":" / "@"
         *
         * unreserved  = ALPHA / DIGIT / "-" / "." / "_" / "~"
         *
         * sub-delims = "!" / "$" / "&" / "'" / "(" / ")"
         *              / "*" / "+" / "," / ";" / "="
         */
        // ALPHA and DIGIT are always treated as safe characters
        // Add the remaining unreserved characters
        DEFAULT.addSafeCharacter('-');
        DEFAULT.addSafeCharacter('.');
        DEFAULT.addSafeCharacter('_');
        DEFAULT.addSafeCharacter('~');
        // Add the sub-delims
        DEFAULT.addSafeCharacter('!');
        DEFAULT.addSafeCharacter('$');
        DEFAULT.addSafeCharacter('&');
        DEFAULT.addSafeCharacter('\'');
        DEFAULT.addSafeCharacter('(');
        DEFAULT.addSafeCharacter(')');
        DEFAULT.addSafeCharacter('*');
        DEFAULT.addSafeCharacter('+');
        DEFAULT.addSafeCharacter(',');
        DEFAULT.addSafeCharacter(';');
        DEFAULT.addSafeCharacter('=');
        // Add the remaining literals
        DEFAULT.addSafeCharacter(':');
        DEFAULT.addSafeCharacter('@');
        // Add '/' so it isn't encoded when we encode a path
        DEFAULT.addSafeCharacter('/');

        /*
         * Encoder for query strings
         * https://www.w3.org/TR/html5/forms.html#application/x-www-form-urlencoded-encoding-algorithm
         * 0x20 ' ' -> '+'
         * 0x2A, 0x2D, 0x2E, 0x30 to 0x39, 0x41 to 0x5A, 0x5F, 0x61 to 0x7A as-is
         * '*',  '-',  '.',  '0'  to '9',  'A'  to 'Z',  '_',  'a'  to 'z'
         * Also '=' and '&' are not encoded
         * Everything else %nn encoded
         */
        // Special encoding for space
        QUERY.setEncodeSpaceAsPlus(true);
        // Alpha and digit are safe by default
        // Add the other permitted characters
        QUERY.addSafeCharacter('*');
        QUERY.addSafeCharacter('-');
        QUERY.addSafeCharacter('.');
        QUERY.addSafeCharacter('_');
        QUERY.addSafeCharacter('=');
        QUERY.addSafeCharacter('&');
    }

    //Array containing the safe characters set.
    private final BitSet safeCharacters;

    private boolean encodeSpaceAsPlus = false;


    public URLEncoder() {
        this(new BitSet(256));

        for (char i = 'a'; i <= 'z'; i++) {
            addSafeCharacter(i);
        }
        for (char i = 'A'; i <= 'Z'; i++) {
            addSafeCharacter(i);
        }
        for (char i = '0'; i <= '9'; i++) {
            addSafeCharacter(i);
        }
    }


    private URLEncoder(BitSet safeCharacters) {
        this.safeCharacters = safeCharacters;
    }


    public void addSafeCharacter(char c) {
        safeCharacters.set(c);
    }


    public void removeSafeCharacter(char c) {
        safeCharacters.clear(c);
    }


    public void setEncodeSpaceAsPlus(boolean encodeSpaceAsPlus) {
        this.encodeSpaceAsPlus = encodeSpaceAsPlus;
    }


    /**
     * URL encodes the provided path using the given character set.
     *
     * @param path      The path to encode
     * @param charset   The character set to use to convert the path to bytes
     *
     * @return The encoded path
     */
    public String encode(String path, Charset charset) {

        int maxBytesPerChar = 10;
        StringBuilder rewrittenPath = new StringBuilder(path.length());
        ByteArrayOutputStream buf = new ByteArrayOutputStream(maxBytesPerChar);
        OutputStreamWriter writer = new OutputStreamWriter(buf, charset);

        for (int i = 0; i < path.length(); i++) {
            int c = path.charAt(i);
            if (safeCharacters.get(c)) {
                rewrittenPath.append((char)c);
            } else if (encodeSpaceAsPlus && c == ' ') {
                rewrittenPath.append('+');
            } else {
                // convert to external encoding before hex conversion
                try {
                    writer.write((char)c);
                    writer.flush();
                } catch(IOException e) {
                    buf.reset();
                    continue;
                }
                byte[] ba = buf.toByteArray();
                for (byte toEncode : ba) {
                    // Converting each byte in the buffer
                    rewrittenPath.append('%');
                    int low = toEncode & 0x0f;
                    int high = (toEncode & 0xf0) >> 4;
                    rewrittenPath.append(hexadecimal[high]);
                    rewrittenPath.append(hexadecimal[low]);
                }
                buf.reset();
            }
        }
        return rewrittenPath.toString();
    }


    @Override
    public Object clone() {
        URLEncoder result = new URLEncoder((BitSet) safeCharacters.clone());
        result.setEncodeSpaceAsPlus(encodeSpaceAsPlus);
        return result;
    }
}
