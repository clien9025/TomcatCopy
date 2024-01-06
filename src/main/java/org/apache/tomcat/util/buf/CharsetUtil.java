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

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

public class CharsetUtil {

    private CharsetUtil() {
        // Utility class. Hide default constructor.
    }


    public static boolean isAsciiSuperset(Charset charset) {
        // Bytes 0x00 to 0x7F must decode to the first 128 Unicode characters
        CharsetDecoder decoder = charset.newDecoder();// 这行代码创建了一个解码器，用于将字节解码为字符
        ByteBuffer inBytes = ByteBuffer.allocate(1);// 创建了一个容量为1的字节缓冲区，用于存放将要检查的字节
        CharBuffer outChars;
        for (int i = 0; i < 128; i++) {
            // 清除和放置字节
            inBytes.clear();
            inBytes.put((byte) i);// 十进制
            inBytes.flip();// 准备缓冲区以从头开始读取数据
            try {
                // 将单个字节（inBytes）解码成 outChars(字符) 并存储在CharBuffer outChars中。这个过程是:字节--->成对应的字符
                outChars = decoder.decode(inBytes);
            } catch (CharacterCodingException e) {
                return false;
            }
            try {
                // 检查解码后的字符是否与期望的ASCII字符匹配
                if (outChars.get() != i) {
                    return false;
                }
            } catch (BufferUnderflowException e) {
                return false;
            }
        }

        return true;
    }
}
