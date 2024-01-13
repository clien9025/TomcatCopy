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
package org.apache.catalina.core;


import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;

import javax.management.ObjectName;

import org.apache.catalina.Container;
import org.apache.catalina.Engine;
import org.apache.catalina.Executor;
import org.apache.catalina.JmxEnabled;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.LifecycleState;
import org.apache.catalina.Server;
import org.apache.catalina.Service;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.mapper.Mapper;
import org.apache.catalina.mapper.MapperListener;
import org.apache.catalina.util.LifecycleMBeanBase;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.res.StringManager;


/**
 * Standard implementation of the <code>Service</code> interface. The associated Container is generally an instance of
 * Engine, but this is not required.
 *
 * @author Craig R. McClanahan
 */

public class StandardService extends LifecycleMBeanBase implements Service {



    private static final Log log = LogFactory.getLog(StandardService.class);
    private static final StringManager sm = StringManager.getManager(StandardService.class);



    // ----------------------------------------------------- Instance Variables

    /**
     * The set of Connectors associated with this Service.
     */
    protected Connector connectors[] = new Connector[0];
    private final Object connectorsLock = new Object();

    /**
     * The list of executors held by the service.
     */
//    protected final ArrayList<Executor> executors = new ArrayList<>();

    private Engine engine = null;

//    private ClassLoader parentClassLoader = null;


    /**
     * The property change support for this component.
     */
    protected final PropertyChangeSupport support = new PropertyChangeSupport(this);


    /**
     * The name of this service.
     */
    private String name = null;

    /**
     * The <code>Server</code> that owns this Service, if any.
     */
    private Server server = null;


    /**
     * Mapper.
     */
    protected final Mapper mapper = new Mapper();


    /**
     * Mapper listener.
     */
    protected final MapperListener mapperListener = new MapperListener(this);


    // ------------------------------------------------------------- Properties

    public long getGracefulStopAwaitMillis() {
//        return gracefulStopAwaitMillis;
        throw new UnsupportedOperationException();
    }


    public void setGracefulStopAwaitMillis(long gracefulStopAwaitMillis) {
//        this.gracefulStopAwaitMillis = gracefulStopAwaitMillis;
        throw new UnsupportedOperationException();
    }


    @Override
    public Mapper getMapper() {
        return mapper;
    }

    @Override
    public Engine getContainer() {
        return engine;
    }

    /**
     * 负责设置与服务（StandardService）关联的引擎（Engine）。在 Tomcat 中，Engine 是处理所有请求的最高级别容器。
     * 每个 Service 只能有一个 Engine，这个方法用于确保这种关系被正确管理。主要负责设置服务的容器引擎，并管理旧引擎和新引擎之间的过渡。
     * @param engine The new Engine
     */
    @Override
    public void setContainer(Engine engine) {
        /* 1. 移除旧的引擎（如果存在） */
        // 如果服务已经有一个关联的引擎(oldEngine)，它会首先将这个旧引擎的服务设置为null，表示旧引擎不再关联此服务
        Engine oldEngine = this.engine;
        if (oldEngine != null) {
//            oldEngine.setService(null);
            throw new UnsupportedOperationException();
        }
        /* 2. 设置新引擎的服务 */
        this.engine = engine;
        if (this.engine != null) {
            this.engine.setService(this);
        }
        /* 3. 启动新引擎 */
        /* 如果服务当前的状态是可用的，并且新的Engine不为空，则尝试启动新的Engine */
        if (getState().isAvailable()) {
            if (this.engine != null) {
//                try {
//                    this.engine.start();
//                } catch (LifecycleException e) {
//                    log.error(sm.getString("standardService.engine.startFailed"), e);
//                }
                throw new UnsupportedOperationException();
            }
            /* 4. 管理MapperListener */
            // Restart MapperListener to pick up new engine.
            // 为了使新的 Engine 生效，需要停止并重新启动 MapperListener。MapperListener 负责路由请求到正确的目标
            try {
                mapperListener.stop();
            } catch (LifecycleException e) {
                log.error(sm.getString("standardService.mapperListener.stopFailed"), e);
            }
            try {
                mapperListener.start();
            } catch (LifecycleException e) {
                log.error(sm.getString("standardService.mapperListener.startFailed"), e);
            }
            /* 5. 停止旧引擎 */
            if (oldEngine != null) {
                try {
                    oldEngine.stop();
                } catch (LifecycleException e) {
                    log.error(sm.getString("standardService.engine.stopFailed"), e);
                }
            }
        }
        /* 6. 通知属性更改监听器 */
        // Report this property change to interested listeners
        support.firePropertyChange("container", oldEngine, this.engine);
    }


    /**
     * Set the name of this Service.
     *
     * @param name The new service name
     */
    @Override
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Set the <code>Server</code> with which we are associated (if any).
     *
     * @param server The server that owns this Service
     */
    @Override
    public void setServer(Server server) {
        this.server = server;
    }



    // --------------------------------------------------------- Public Methods

    /**
     * Add a new Connector to the set of defined Connectors, and associate it with this Service's Container.
     *
     * @param connector The Connector to be added
     */
    @Override
    public void addConnector(Connector connector) {

        synchronized (connectorsLock) {
            connector.setService(this);
            Connector results[] = new Connector[connectors.length + 1];
            System.arraycopy(connectors, 0, results, 0, connectors.length);
            results[connectors.length] = connector;
            connectors = results;
        }

        try {
            if (getState().isAvailable()) {
                // todo 这个 start 里面没有实现
                connector.start();
            }
        } catch (LifecycleException e) {
            throw new IllegalArgumentException(sm.getString("standardService.connector.startFailed", connector), e);
        }

        // Report this property change to interested listeners
        support.firePropertyChange("connector", null, connector);
    }

    /**
     * Find and return the set of Connectors associated with this Service.
     *
     * 查找并返回与此服务关联的连接器集（数组）
     */
    @Override
    public Connector[] findConnectors() {
        synchronized (connectorsLock) {
            // shallow copy
            return connectors.clone();// 返回复制的那一份 connectors（数组）
        }
    }


    @Override
    protected String getDomainInternal() {
        String domain = null;
        Container engine = getContainer();

        // Use the engine name first
        if (engine != null) {
            domain = engine.getName();
        }

        // No engine or no engine name, use the service name
        if (domain == null) {
            domain = getName();
        }

        // No service name, return null which will trigger the use of the
        // default
        return domain;
    }

}
