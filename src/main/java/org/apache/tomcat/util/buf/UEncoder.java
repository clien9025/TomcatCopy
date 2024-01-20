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
import java.nio.charset.StandardCharsets;
import java.util.BitSet;

/**
 * Efficient implementation of a UTF-8 encoder. This class is not thread safe - you need one encoder per thread. The
 * encoder will save and recycle the internal objects, avoiding garbage. You can add extra characters that you want
 * preserved, for example while encoding a URL you can add "/".
 *
 * @author Costin Manolache
 */
public final class UEncoder {

    public enum SafeCharsSet {
        WITH_SLASH("/"),
        DEFAULT("");

        private final BitSet safeChars;

        private BitSet getSafeChars() {
            return this.safeChars;
        }

        SafeCharsSet(String additionalSafeChars) {
            safeChars = initialSafeChars();
            for (char c : additionalSafeChars.toCharArray()) {
                safeChars.set(c);
            }
        }
    }

    // Not static - the set may differ ( it's better than adding
    // an extra check for "/", "+", etc
    private BitSet safeChars = null;
    private C2BConverter c2b = null;
    private ByteChunk bb = null;
    private CharChunk cb = null;
    private CharChunk output = null;

    /**
     * Create a UEncoder with an unmodifiable safe character set.
     *
     * @param safeCharsSet safe characters for this encoder
     */
    public UEncoder(SafeCharsSet safeCharsSet) {
        this.safeChars = safeCharsSet.getSafeChars();
    }







    // -------------------- Internal implementation --------------------

    private static BitSet initialSafeChars() {
//        BitSet initialSafeChars = new BitSet(128);
//        int i;
//        for (i = 'a'; i <= 'z'; i++) {
//            initialSafeChars.set(i);
//        }
//        for (i = 'A'; i <= 'Z'; i++) {
//            initialSafeChars.set(i);
//        }
//        for (i = '0'; i <= '9'; i++) {
//            initialSafeChars.set(i);
//        }
//        // safe
//        initialSafeChars.set('$');
//        initialSafeChars.set('-');
//        initialSafeChars.set('_');
//        initialSafeChars.set('.');
//
//        // Dangerous: someone may treat this as " "
//        // RFC1738 does allow it, it's not reserved
//        // initialSafeChars.set('+');
//        // extra
//        initialSafeChars.set('!');
//        initialSafeChars.set('*');
//        initialSafeChars.set('\'');
//        initialSafeChars.set('(');
//        initialSafeChars.set(')');
//        initialSafeChars.set(',');
//        return initialSafeChars;
        throw new UnsupportedOperationException();
    }
}
