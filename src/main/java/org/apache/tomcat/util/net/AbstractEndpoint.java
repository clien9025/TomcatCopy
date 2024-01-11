package org.apache.tomcat.util.net;

import org.apache.tomcat.util.threads.LimitLatch;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @param <S> The type used by the socket wrapper associated with this endpoint.
 *            May be the same as U.
 * @param <U> The type of the underlying socket used by this endpoint. May be
 *            the same as S.
 *
 * @author Mladen Turk
 * @author Remy Maucherat
 */
public abstract class AbstractEndpoint<S,U> {

    /**
     * External Executor based thread pool.
     * 私有成员变量executor
     */
    private Executor executor = null;

    /**
     * Are we using an internal executor
     * 受保护的 volatile 布尔类型  成员变量
     * 这个变量可能用于标识是否使用内部的执行器
     */
    protected volatile boolean internalExecutor = true;


    /**
     * Maximum amount of worker threads.
     * 只有在一个使用内部ThreadPoolExecutor的情况下，才允许动态地调整其最大线程数。
     */
    private int maxThreads = 200;

    /**
     * SSL engine.
     */
//    private boolean SSLEnabled = false;
//    public boolean isSSLEnabled() { return SSLEnabled; }
//    public void setSSLEnabled(boolean SSLEnabled) { this.SSLEnabled = SSLEnabled; }

    private int minSpareThreads = 10;



    /**
     * Server socket port.
     */
    private int port = -1;
    public int getPort() { return port; }
    public void setPort(int port ) { this.port=port; }


    /**
     * Allows the server developer to specify the acceptCount (backlog) that
     * should be used for server sockets. By default, this value
     * is 100.
     * 允许服务器开发人员指定应用于服务器套接字的acceptCount（积压）。默认情况下，该值为 100。
     */
    private int acceptCount = 100;
    public void setAcceptCount(int acceptCount) { if (acceptCount > 0) {
        this.acceptCount = acceptCount;
    } }

    /**
     * 设置线程池的最大线程数
     * @param maxThreads
     */
    public void setMaxThreads(int maxThreads) {
        this.maxThreads = maxThreads;
        Executor executor = this.executor;// 获取当前对象的executor实例
        /*只有当internalExecutor为true（表明使用内部执行器）并且executor是ThreadPoolExecutor的实例时，
        才会执行内部代码块。这确保了只有在使用内部线程池执行器且该执行器是ThreadPoolExecutor类型时，才进行后续操作。*/
        if (internalExecutor && executor instanceof ThreadPoolExecutor) {
            // The internal executor should always be an instance of
            // org.apache.tomcat.util.threads.ThreadPoolExecutor but it may be
            // null if the endpoint is not running.
            // This check also avoids various threading issues.
            ((ThreadPoolExecutor) executor).setMaximumPoolSize(maxThreads);
        }
    }

    /**
     * 目的是设置线程池的最小空闲线程数，并适当地调整内部的ThreadPoolExecutor的核心池大小。
     * @param minSpareThreads
     */
    public void setMinSpareThreads(int minSpareThreads) {
        this.minSpareThreads = minSpareThreads;
        Executor executor = this.executor;
        /* 首先，internalExecutor应为true，表示当前对象使用的是内部执行器；其次，executor应该是ThreadPoolExecutor的实例。
        这确保了只有在使用内部的ThreadPoolExecutor时，后续代码才会执行。 */
        if (internalExecutor && executor instanceof ThreadPoolExecutor) {
            // The internal executor should always be an instance of
            // org.apache.tomcat.util.threads.ThreadPoolExecutor but it may be
            // null if the endpoint is not running.
            // This check also avoids various threading issues.
            ((ThreadPoolExecutor) executor).setCorePoolSize(minSpareThreads);
        }
    }

    private int maxConnections = 8*1024;
    /**
     * counter for nr of connections handled by an endpoint
     * connectionLimitLatch 用于控制和限制服务器端点能够处理的最大并发连接数。
     * 通过这种方式，它有助于避免服务器过载并维持稳定高效的运行状态。
     *
     * LimitLatch 维护一个计数器来跟踪当前活跃的连接数。它被初始化为最大连接数（maxConnections）。
     *
     * 线程获取许可：当一个新的连接请求到来时，线程会尝试从 LimitLatch 获取许可。如果当前活跃的连接数未达到maxConnections限制，则
     * LimitLatch 会允许这个线程继续，并将计数器减一。
     *
     * 等待和释放：如果当前的连接数已经达到最大限制，则新的请求线程将等待，直到其他线程释放许可（即连接关闭），
     * LimitLatch 的计数器随之增加，新的线程可以获取许可继续执行。
     */
    private volatile LimitLatch connectionLimitLatch = null;

    /**
     *      定义了一个 setMaxConnections 方法来更新最大连接数，并相应地更新或初始化一个 LimitLatch 对象用于实际执行这一限制。
     *      这段代码的作用是控制端点能处理的最大并发连接数。（用来设置和管理服务器端点的最大连接数的）
     *      当最大连接数被更新时，它相应地调整或初始化一个LimitLatch来实施这个限制。这有助于避免服务器过载，保持稳定和高效的运行。
     * @param maxCon
     */
    public void setMaxConnections(int maxCon) {
        this.maxConnections = maxCon;
        LimitLatch latch = this.connectionLimitLatch;// 传入进来的 NioEndpoint 的 connectionLimitLatch 属性是 null
        if (latch != null) {// 检查LimitLatch是否已经初始化
            /* 如果maxCon等于-1，表示不限制连接数，那么调用 releaseConnectionLatch()方法释放（或关闭）计数器。*/
            // Update the latch that enforces this（强制更新这个锁）
            /* 在许多编程约定中，特定的数值被用作标志来表示特殊情况。在这种情况下，maxCon == -1被用作一个标志来指示需要进行特殊的操作。
            这种模式在处理配置选项时非常普遍，尤其是在涉及到开/关或存在/不存在限制的情况。*/
            if (maxCon == -1) {// 这个 maxCon 是个传入值。在这个上下文中，它表示移除对并发连接数的限制，允许无限数量的连接
                releaseConnectionLatch();
            } else {
                /* 如果maxCon不等于-1，那么设置latch的限制为maxCon
                *  如果maxCon不等于-1，代码将这个值 maxCon 视为新的有效的最大连接数，并更新LimitLatch以强制执行这个新限制。*/
                latch.setLimit(maxCon);
            }
        } else if (maxCon > 0) {
            // Latch是null且maxCon大于0的情况：这种情况通常发生在LimitLatch对象尚未初始化时。
            // 1. 当端点（Endpoint）对象首次创建时，connectionLimitLatch可能被初始化为null。
            //    在这种情况下，LimitLatch尚未被设置或使用，表示没有当前的限制。
            // 2. 在端点的生命周期中，可能会有配置更改，这可能导致LimitLatch被置为null。
            //    例如，如果之前决定不限制连接数（可能将LimitLatch设为null），然后更新配置以启用限制。
            /* 如果latch是null且maxCon大于0，表示需要初始化计数器，那么调用initializeConnectionLatch()方法进行初始化 */
            initializeConnectionLatch();
        }
    }
    public int getMaxConnections() { return this.maxConnections; }

    /**
     * 释放（或关闭）计数器。
     */
    private void releaseConnectionLatch() {
        LimitLatch latch = connectionLimitLatch;
        if (latch!=null) {
            latch.releaseAll();
        }
        connectionLimitLatch = null;
    }

    protected LimitLatch initializeConnectionLatch() {
        if (maxConnections==-1) {
            return null;
        }
        if (connectionLimitLatch==null) {
            connectionLimitLatch = new LimitLatch(getMaxConnections());
        }
        return connectionLimitLatch;
    }

    /**
     * Max keep alive requests
     */
    private int maxKeepAliveRequests=100; // as in Apache HTTPD server
    public void setMaxKeepAliveRequests(int maxKeepAliveRequests) {
        this.maxKeepAliveRequests = maxKeepAliveRequests;
    }

    public interface Handler<S> {

        /**
         * Different types of socket states to react upon.
         */
        enum SocketState {
            // TODO Add a new state to the AsyncStateMachine and remove
            //      ASYNC_END (if possible)
            OPEN, CLOSED, LONG, ASYNC_END, SENDFILE, UPGRADING, UPGRADED, ASYNC_IO, SUSPENDED
        }


        /**
         * Process the provided socket with the given current status.
         *
         * @param socket The socket to process
         * @param status The current socket status
         *
         * @return The state of the socket after processing
         */
        SocketState process(SocketWrapperBase<S> socket,
                            SocketEvent status);


        /**
         * Obtain the GlobalRequestProcessor associated with the handler.
         *
         * @return the GlobalRequestProcessor
         */
        Object getGlobal();


        /**
         * Release any resources associated with the given SocketWrapper.
         *
         * @param socketWrapper The socketWrapper to release resources for
         */
        void release(SocketWrapperBase<S> socketWrapper);


        /**
         * Inform the handler that the endpoint has stopped accepting any new
         * connections. Typically, the endpoint will be stopped shortly
         * afterwards but it is possible that the endpoint will be resumed so
         * the handler should not assume that a stop will follow.
         */
        void pause();


        /**
         * Recycle resources associated with the handler.
         */
        void recycle();
    }
}
