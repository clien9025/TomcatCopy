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
package org.apache.tomcat.util.http;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Queue;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * A thread safe wrapper around {@link SimpleDateFormat} that does not make use of ThreadLocal and - broadly - only
 * creates enough SimpleDateFormat objects to satisfy the concurrency requirements.
 */
public class ConcurrentDateFormat {

    private final String format;
    private final Locale locale;
    private final TimeZone timezone;
    private final Queue<SimpleDateFormat> queue = new ConcurrentLinkedQueue<>();

    public ConcurrentDateFormat(String format, Locale locale, TimeZone timezone) {
        this.format = format;
        this.locale = locale;
        this.timezone = timezone;
        SimpleDateFormat initial = createInstance();
        queue.add(initial);
    }

    public String format(Date date) {
        // 使用 queue.poll() 尝试从队列中获取一个 SimpleDateFormat 对象。
        SimpleDateFormat sdf = queue.poll();
        // 如果队列为空（queue.poll() 返回 null），则调用 createInstance() 方法创建一个新的 SimpleDateFormat 实例。
        if (sdf == null) {
            sdf = createInstance();
        }
        // 使用获取或创建的 SimpleDateFormat 实例格式化传入的日期。
        String result = sdf.format(date);
        // 将这个 SimpleDateFormat 实例放回队列中以备后续使用。
        queue.add(sdf);
        // 返回格式化后的日期字符串
        return result;
    }

    /**
     * 用于解析日期字符串
     *
     * @param source
     * @return
     * @throws ParseException
     */
    public Date parse(String source) throws ParseException {
        SimpleDateFormat sdf = queue.poll();
        if (sdf == null) {
            sdf = createInstance();
        }
        // parse 方法将字符串转换为 Date 对象。如果字符串格式不正确，将抛出 ParseException
        Date result = sdf.parse(source);
        // 设置 SimpleDateFormat 对象的时区为类的 timezone 属性
        sdf.setTimeZone(timezone);
        // 将 SimpleDateFormat 对象重新放入 queue 队列，以便以后重用
        queue.add(sdf);
        return result;
    }

    private SimpleDateFormat createInstance() {
        SimpleDateFormat sdf = new SimpleDateFormat(format, locale);
        sdf.setTimeZone(timezone);
        return sdf;
    }
}
