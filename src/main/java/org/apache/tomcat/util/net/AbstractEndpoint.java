package org.apache.tomcat.util.net;

import java.util.concurrent.Executor;

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
     * Maximum amount of worker threads.
     */
    private int maxThreads = 200;
    public void setMaxThreads(int maxThreads) {
//        this.maxThreads = maxThreads;
//        Executor executor = this.executor;
//        if (internalExecutor && executor instanceof ThreadPoolExecutor) {
//            // The internal executor should always be an instance of
//            // org.apache.tomcat.util.threads.ThreadPoolExecutor but it may be
//            // null if the endpoint is not running.
//            // This check also avoids various threading issues.
//            ((ThreadPoolExecutor) executor).setMaximumPoolSize(maxThreads);
//        }
        throw new UnsupportedOperationException();
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
