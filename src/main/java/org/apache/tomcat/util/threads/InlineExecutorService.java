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

import java.util.List;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;

public class InlineExecutorService extends AbstractExecutorService {

    private volatile boolean shutdown;
    private volatile boolean taskRunning;
    private volatile boolean terminated;

    private final Object lock = new Object();

    @Override
    public void shutdown() {
        shutdown = true;
        synchronized (lock) {
            terminated = !taskRunning;
        }
    }

    @Override
    public List<Runnable> shutdownNow() {
        shutdown();
        return null;
    }

    @Override
    public boolean isShutdown() {
        return shutdown;
    }

    @Override
    public boolean isTerminated() {
        return terminated;
    }

    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        synchronized (lock) {
            /* 检查终止状态 */
            // 方法首先检查 terminated 变量的值。如果 terminated 为 true，表示服务已经终止，那么方法立即返回 true
            // 如果 terminated 为 false，表示服务尚未终止，方法继续执行
            if (terminated) {
                return true;
            }
            /* 等待终止或超时 */
            // 使用 lock.wait 方法使当前线程等待，直到另一个线程调用 lock.notifyAll/notify 或达到指定的超时时间。
            // 超时时间是通过 unit.toMillis(timeout) 计算得到的，这个调用将传入的时间单位转换为毫秒。
            lock.wait(unit.toMillis(timeout));
            return terminated;
        }
    }

    @Override
    public void execute(Runnable command) {
        synchronized (lock) {
            if (shutdown) {
                // 在同步块中，首先检查 shutdown 变量。如果 shutdown 为 true，表示服务已经关闭，
                // 此时抛出 RejectedExecutionException，拒绝执行新任务。
                throw new RejectedExecutionException();
            }
            // 如果 shutdown 为 false，表示服务仍然运行中，可以执行任务。
            // 在同步块内部，设置 taskRunning 变量为 true，表示有一个任务正在运行。
            taskRunning = true;
        }
        // 执行传入的 Runnable 命令
        command.run();
        /* 任务完成后的处理 */
        synchronized (lock) {
            // 设置 taskRunning 为 false，表示任务已经运行完成
            taskRunning = false;
            // 检查 shutdown 变量。如果此时服务正在关闭（shutdown 为 true），
            // 则将 terminated 设置为 true，表示服务已经完全终止。
            if (shutdown) {
                terminated = true;
                // 调用 lock.notifyAll() 唤醒所有因调用 awaitTermination 方法而等待的线程。
                lock.notifyAll();
            }
        }
    }
}
