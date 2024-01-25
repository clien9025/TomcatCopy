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
package org.apache.tomcat.util.security;

import java.security.PrivilegedAction;

/**
 * 这个类 PrivilegedSetTccl 在 Apache Tomcat 的 org.apache.tomcat.util.security 包中，
 * 用于在特权操作中设置线程的上下文类加载器（Thread Context Class Loader，简称 TCCL）。
 * <p>
 * 类实现了 Java 安全 API 中的 PrivilegedAction<Void> 接口。这允许它的 run 方法在一个特权块中执行，这在处理安全管理器时是必要的。
 * <p>
 * 使用 PrivilegedAction 来执行某些操作是常见的，特别是当涉及到涉及安全权限的操作时。设置线程的上下文类加载器是这些操作之一，
 * 因为它允许线程以特定的类加载器作为基准来加载类和资源。这在容器中管理不同 Web 应用程序的类加载时尤其重要。
 */
public class PrivilegedSetTccl implements PrivilegedAction<Void> {
    /**
     * 要设置为当前线程上下文类加载器的 ClassLoader 实例
     */
    private final ClassLoader cl;
    /**
     * 需要设置上下文类加载器的线程。
     */
    private final Thread t;

    @Deprecated
    public PrivilegedSetTccl(ClassLoader cl) {
        this(Thread.currentThread(), cl);
    }

    public PrivilegedSetTccl(Thread t, ClassLoader cl) {
        this.t = t;
        this.cl = cl;
    }


    @Override
    public Void run() {
        // 调用 t.setContextClassLoader(cl) 将传入的类加载器 cl 设置为线程 t 的上下文类加载器。
        // 这个操作在特权块中执行，有助于避免安全管理器导致的权限问题
        t.setContextClassLoader(cl);
        return null;
    }
}