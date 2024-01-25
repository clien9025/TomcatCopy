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
package org.apache.tomcat.util.threads;

import org.apache.tomcat.util.security.PrivilegedSetAccessControlContext;
import org.apache.tomcat.util.security.PrivilegedSetTccl;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Simple task thread factory to use to create threads for an executor
 * implementation.
 */
public class TaskThreadFactory implements ThreadFactory {

    private final ThreadGroup group;
    private final AtomicInteger threadNumber = new AtomicInteger(1);
    private final String namePrefix;
    private final boolean daemon;
    private final int threadPriority;

    public TaskThreadFactory(String namePrefix, boolean daemon, int priority) {
        SecurityManager s = System.getSecurityManager();
        group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
        this.namePrefix = namePrefix;
        this.daemon = daemon;
        this.threadPriority = priority;
    }

    /**
     * 这个 newThread 方法在创建新线程时提供了对线程行为的详细控制，包括名称、优先级、是否守护线程、上下文类加载器和安全设置。
     * 这对于在复杂应用程序（如 Web 容器）中管理线程行为至关重要，尤其是在涉及到类加载器和安全性方面。
     *
     * @param r a runnable to be executed by new thread instance
     * @return
     */
    @Override
    public Thread newThread(Runnable r) {
        /* 1. */
        // 创建一个 TaskThread 对象 t，它是 Thread 的一个子类。TaskThread 的构造函数接收线程组 group、要执行的任务 r 和线程名，
        // 其中线程名由前缀 namePrefix 和递增的线程编号组成
        // 线程编号通过 threadNumber.getAndIncrement() 获取，这是一个原子操作，确保每个线程都有唯一的编号
        TaskThread t = new TaskThread(group, r, namePrefix + threadNumber.getAndIncrement());
        /* 2. 设置线程属性 */
        // 设置线程 t 是否为守护线程（由 daemon 变量决定）
        t.setDaemon(daemon);
        // 设置线程的优先级为 threadPriority
        t.setPriority(threadPriority);
        /* 3. 设置上下文类加载器 */
        // 如果启用了安全性（Constants.IS_SECURITY_ENABLED 为 true），则使用特权操作（PrivilegedAction）来设置线程的
        // 上下文类加载器和访问控制上下文。
        // 这是为了避免在有安全管理器的环境中出现权限问题，并确保不会错误地保留对 Web 应用程序类加载器的引用
        if (Constants.IS_SECURITY_ENABLED) {
            // Set the context class loader of newly created threads to be the
            // class loader that loaded this factory. This avoids retaining
            // references to web application class loaders and similar.
            PrivilegedAction<Void> pa = new PrivilegedSetTccl(
                    t, getClass().getClassLoader());
            AccessController.doPrivileged(pa);

            // This method may be triggered from an InnocuousThread. Ensure that
            // the thread inherits an appropriate AccessControlContext
            pa = new PrivilegedSetAccessControlContext(t);
            AccessController.doPrivileged(pa);
        } else {
            /* 4. 如果安全未启用 */
            // 如果没有启用安全性，直接将线程的上下文类加载器设置为当前类的类加载器
            t.setContextClassLoader(getClass().getClassLoader());
        }
        /* 5. 返回新创建的线程 */
        // 方法最后返回创建的 TaskThread 实例
        return t;
    }
}
