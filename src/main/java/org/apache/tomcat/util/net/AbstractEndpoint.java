package org.apache.tomcat.util.net;

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

    public void setMinSpareThreads(int minSpareThreads) {
        this.minSpareThreads = minSpareThreads;
        Executor executor = this.executor;
        if (internalExecutor && executor instanceof ThreadPoolExecutor) {
            // The internal executor should always be an instance of
            // org.apache.tomcat.util.threads.ThreadPoolExecutor but it may be
            // null if the endpoint is not running.
            // This check also avoids various threading issues.
            ((ThreadPoolExecutor) executor).setCorePoolSize(minSpareThreads);
        }
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
