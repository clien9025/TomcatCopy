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

import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CodingErrorAction;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.TreeMap;

import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

/**
 * This class implements a String cache for ByteChunk and CharChunk.
 *
 * @author Remy Maucherat
 */
public class StringCache {


    private static final Log log = LogFactory.getLog(StringCache.class);


    // ------------------------------------------------------- Static Variables


    /**
     * Enabled ?
     */
    protected static boolean byteEnabled = Boolean.getBoolean("tomcat.util.buf.StringCache.byte.enabled");


    protected static boolean charEnabled = Boolean.getBoolean("tomcat.util.buf.StringCache.char.enabled");


    protected static int trainThreshold =
            Integer.getInteger("tomcat.util.buf.StringCache.trainThreshold", 20000).intValue();


    protected static int cacheSize = Integer.getInteger("tomcat.util.buf.StringCache.cacheSize", 200).intValue();


    protected static final int maxStringSize =
            Integer.getInteger("tomcat.util.buf.StringCache.maxStringSize", 128).intValue();


    /**
     * Statistics hash map for byte chunk.
     */
    protected static final HashMap<ByteEntry,int[]> bcStats = new HashMap<>(cacheSize);


    /**
     * toString count for byte chunk.
     */
    protected static int bcCount = 0;


    /**
     * Cache for byte chunk.
     */
    protected static volatile ByteEntry[] bcCache = null;


    /**
     * Statistics hash map for char chunk.
     */
    protected static final HashMap<CharEntry,int[]> ccStats = new HashMap<>(cacheSize);


    /**
     * toString count for char chunk.
     */
    protected static int ccCount = 0;


    /**
     * Cache for char chunk.
     */
    protected static volatile CharEntry[] ccCache = null;


    /**
     * Access count.
     */
    protected static int accessCount = 0;


    /**
     * Hit count.
     */
    protected static int hitCount = 0;


    // ------------------------------------------------------------ Properties


    /**
     * @return Returns the cacheSize.
     */
    public int getCacheSize() {
        return cacheSize;
    }


    /**
     * @param cacheSize The cacheSize to set.
     */
    public void setCacheSize(int cacheSize) {
        StringCache.cacheSize = cacheSize;
    }


    /**
     * @return Returns the enabled.
     */
    public boolean getByteEnabled() {
        return byteEnabled;
    }


    /**
     * @param byteEnabled The enabled to set.
     */
    public void setByteEnabled(boolean byteEnabled) {
        StringCache.byteEnabled = byteEnabled;
    }


    /**
     * @return Returns the enabled.
     */
    public boolean getCharEnabled() {
        return charEnabled;
    }


    /**
     * @param charEnabled The enabled to set.
     */
    public void setCharEnabled(boolean charEnabled) {
        StringCache.charEnabled = charEnabled;
    }


    /**
     * @return Returns the trainThreshold.
     */
    public int getTrainThreshold() {
        return trainThreshold;
    }


    /**
     * @param trainThreshold The trainThreshold to set.
     */
    public void setTrainThreshold(int trainThreshold) {
        StringCache.trainThreshold = trainThreshold;
    }


    /**
     * @return Returns the accessCount.
     */
    public int getAccessCount() {
        return accessCount;
    }


    /**
     * @return Returns the hitCount.
     */
    public int getHitCount() {
        return hitCount;
    }


    // -------------------------------------------------- Public Static Methods




    // -------------------------------------------------- ByteEntry Inner Class

    private static class ByteEntry {

        private byte[] name = null;
        private Charset charset = null;
        private CodingErrorAction malformedInputAction = null;
        private CodingErrorAction unmappableCharacterAction = null;
        private String value = null;

        @Override
        public String toString() {
            return value;
        }

        @Override
        public int hashCode() {
            return Objects.hash(malformedInputAction, unmappableCharacterAction, value);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            ByteEntry other = (ByteEntry) obj;
            return Objects.equals(malformedInputAction, other.malformedInputAction) &&
                    Objects.equals(unmappableCharacterAction, other.unmappableCharacterAction) &&
                    Objects.equals(value, other.value);
        }
    }



    // -------------------------------------------------- CharEntry Inner Class


    private static class CharEntry {

        private char[] name = null;
        private String value = null;

        @Override
        public String toString() {
            return value;
        }

        @Override
        public int hashCode() {
            return value.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof CharEntry) {
                return value.equals(((CharEntry) obj).value);
            }
            return false;
        }

    }

}