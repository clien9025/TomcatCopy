/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.tomcat.util.collections;

/**
 * This is intended as a (mostly) GC-free alternative to
 * {@link java.util.Stack} when the requirement is to create a pool of re-usable
 * objects with no requirement to shrink the pool. The aim is to provide the
 * bare minimum of required functionality as quickly as possible with minimum
 * garbage.
 * <p>
 * 是一个自定义栈的实现，用于管理泛型对象 T 的池。这个类是为了提供一个高效且几乎不产生垃圾收集（GC）负担的数据结构，
 * 适用于对象池的场景，特别是不需要缩减池大小的情况。
 * <p>
 * 这个类提供了一个同步的栈结构，用于存储和检索对象。
 * 它是 java.util.Stack 的一个高效替代品，专注于快速操作和最小化垃圾生成。
 *
 * @param <T> The type of object managed by this stack
 */
public class SynchronizedStack<T> {

    public static final int DEFAULT_SIZE = 128;
    private static final int DEFAULT_LIMIT = -1;

    private int size;
    private final int limit;

    /*
     * Points to the next available object in the stack
     */
    private int index = -1;
    /**
     * 类中使用了一个数组来存储栈中的元素，并提供了基本的栈操作，如 push（入栈）、pop（出栈）和 clear（清空栈）
     */
    private Object[] stack;


    public SynchronizedStack() {
        this(DEFAULT_SIZE, DEFAULT_LIMIT);
    }

    /**
     * 这个构造函数接受 size（栈的初始大小）和 limit（栈的最大大小限制）作为参数。
     * 如果提供的 size 大于 limit，则使用 limit 作为栈的大小。
     *
     * @param size
     * @param limit
     */
    public SynchronizedStack(int size, int limit) {
        if (limit > -1 && size > limit) {
            this.size = limit;
        } else {
            this.size = size;
        }
        this.limit = limit;
        stack = new Object[this.size];
    }

    /**
     * 此方法将对象 obj 添加到栈中。
     * @param obj
     * @return
     */
    public synchronized boolean push(T obj) {
        index++;
        // 如果栈已满（达到 size），且未达到最大限制 limit 或没有设置限制（limit 为 -1），则扩展栈的大小
        if (index == size) {
            if (limit == -1 || size < limit) {
                expand();
            } else {
                // 如果栈已满且达到了限制，则不添加对象并返回 false。
                index--;
                return false;
            }
        }
        stack[index] = obj;
        return true;
    }

    /**
     * 此方法从栈中移除并返回顶部元素
     * @return
     */
    @SuppressWarnings("unchecked")
    public synchronized T pop() {
        // 如果栈为空（index 为 -1），则返回 null。
        if (index == -1) {
            return null;
        }
        // 方法使用泛型转换来返回正确类型的对象。
        T result = (T) stack[index];
        stack[index--] = null;
        return result;
    }

    /**
     * 此方法清空栈中的所有元素
     */
    public synchronized void clear() {
        // 它遍历栈中的元素并将它们设置为 null，然后重置 index。
        if (index > -1) {
            for (int i = 0; i < index + 1; i++) {
                stack[i] = null;
            }
        }
        index = -1;
    }

    /**
     * 这是一个私有方法，用于在栈空间不足时增加栈的大小。
     */
    private void expand() {
        // 新的栈大小是当前大小的两倍，但不会超过设置的最大限制 limit。
        int newSize = size * 2;
        if (limit != -1 && newSize > limit) {
            newSize = limit;
        }
        // 通过创建一个新的更大的数组并将旧数组的内容复制过去来实现扩展
        Object[] newStack = new Object[newSize];
        System.arraycopy(stack, 0, newStack, 0, size);
        // This is the only point where garbage is created by throwing away the
        // old array. Note it is only the array, not the contents, that becomes
        // garbage.
        stack = newStack;
        size = newSize;
    }
}
