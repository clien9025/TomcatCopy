package org.apache.catalina.util;

import org.apache.catalina.*;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.ExceptionUtils;
import org.apache.tomcat.util.res.StringManager;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class LifecycleBase implements Lifecycle {


    private static final Log log = LogFactory.getLog(LifecycleBase.class);

    private static final StringManager sm = StringManager.getManager(LifecycleBase.class);

    /**
     * The list of registered LifecycleListeners for event notifications.
     * 这行代码声明了一个lifecycleListeners列表，用于存储LifecycleListener对象。这些监听器对象是用来接收生命周期事件通知的。
     *
     * 使用 CopyOnWriteArrayList 表示这个列表，这是一种线程安全的List实现。
     * 当列表被修改时（如添加或移除监听器），它会复制其内部数组，确保在迭代过程中不会发生
     * 并发修改异常（ConcurrentModificationException）。这对于那些其监听器列表可能在多线程环境中被修改的对象来说是非常有用的。
     */
    private final List<LifecycleListener> lifecycleListeners = new CopyOnWriteArrayList<>();
    /**
     * The current state of the source component.
     */
    private volatile LifecycleState state = LifecycleState.NEW;


    private boolean throwOnFailure = true;


    /**
     * Will a {@link LifecycleException} thrown by a sub-class during
     * {@link #initInternal()}, {@link #startInternal()},
     * {@link #stopInternal()} or {@link #destroyInternal()} be re-thrown for
     * the caller to handle or will it be logged instead?
     *
     * @return {@code true} if the exception will be re-thrown, otherwise
     *         {@code false}
     */
    public boolean getThrowOnFailure() {
        return throwOnFailure;
    }


    /**
     * Configure if a {@link LifecycleException} thrown by a sub-class during
     * {@link #initInternal()}, {@link #startInternal()},
     * {@link #stopInternal()} or {@link #destroyInternal()} will be re-thrown
     * for the caller to handle or if it will be logged instead.
     *
     * @param throwOnFailure {@code true} if the exception should be re-thrown,
     *                       otherwise {@code false}
     */
    public void setThrowOnFailure(boolean throwOnFailure) {
        this.throwOnFailure = throwOnFailure;
    }

    /**
     * 这段代码是实现了一个监听器注册机制，特别是用于生命周期事件的监听。
     * @param listener The listener to add
     *
     * 这个方法提供了一种机制，允许外部代码注册LifecycleListener对象。当调用此方法时，它将传入的listener添加到lifecycleListeners列表中。
     * 这使得其他对象可以注册监听器，以便在当前对象的生命周期发生变化时接收通知。比如，当对象启动或停止时，这些注册的监听器可能会被通知。
     */
    @Override
    public void addLifecycleListener(LifecycleListener listener) {
        lifecycleListeners.add(listener);
    }

    /**
     * Allow sub classes to fire {@link Lifecycle} events.
     *
     * @param type  Event type
     * @param data  Data associated with event.
     */
    protected void fireLifecycleEvent(String type, Object data) {
//        LifecycleEvent event = new LifecycleEvent(this, type, data);
//        for (LifecycleListener listener : lifecycleListeners) {
//            listener.lifecycleEvent(event);
//        }
        throw new UnsupportedOperationException();
    }


    @Override
    public final synchronized void start() throws LifecycleException {

//        if (LifecycleState.STARTING_PREP.equals(state) || LifecycleState.STARTING.equals(state) ||
//                LifecycleState.STARTED.equals(state)) {
//
//            if (log.isDebugEnabled()) {
//                Exception e = new LifecycleException();
//                log.debug(sm.getString("lifecycleBase.alreadyStarted", toString()), e);
//            } else if (log.isInfoEnabled()) {
//                log.info(sm.getString("lifecycleBase.alreadyStarted", toString()));
//            }
//
//            return;
//        }
//
//        if (state.equals(LifecycleState.NEW)) {
//            init();
//        } else if (state.equals(LifecycleState.FAILED)) {
//            stop();
//        } else if (!state.equals(LifecycleState.INITIALIZED) &&
//                !state.equals(LifecycleState.STOPPED)) {
//            invalidTransition(BEFORE_START_EVENT);
//        }
//
//        try {
//            setStateInternal(LifecycleState.STARTING_PREP, null, false);
//            startInternal();
//            if (state.equals(LifecycleState.FAILED)) {
//                // This is a 'controlled' failure. The component put itself into the
//                // FAILED state so call stop() to complete the clean-up.
//                stop();
//            } else if (!state.equals(LifecycleState.STARTING)) {
//                // Shouldn't be necessary but acts as a check that sub-classes are
//                // doing what they are supposed to.
//                invalidTransition(AFTER_START_EVENT);
//            } else {
//                setStateInternal(LifecycleState.STARTED, null, false);
//            }
//        } catch (Throwable t) {
//            // This is an 'uncontrolled' failure so put the component into the
//            // FAILED state and throw an exception.
//            handleSubClassException(t, "lifecycleBase.startFail", toString());
//        }
        throw new UnsupportedOperationException();
    }

    /**
     * Sub-classes must ensure that the state is changed to
     * {@link LifecycleState#STARTING} during the execution of this method.
     * Changing state will trigger the {@link Lifecycle#START_EVENT} event.
     *
     * If a component fails to start it may either throw a
     * {@link LifecycleException} which will cause it's parent to fail to start
     * or it can place itself in the error state in which case {@link #stop()}
     * will be called on the failed component but the parent component will
     * continue to start normally.
     *
     * @throws LifecycleException Start error occurred
     */
    protected void startInternal() throws LifecycleException {
        throw new UnsupportedOperationException();
    }

    @Override
    public final synchronized void stop() throws LifecycleException {

//        if (LifecycleState.STOPPING_PREP.equals(state) || LifecycleState.STOPPING.equals(state) ||
//                LifecycleState.STOPPED.equals(state)) {
//
//            if (log.isDebugEnabled()) {
//                Exception e = new LifecycleException();
//                log.debug(sm.getString("lifecycleBase.alreadyStopped", toString()), e);
//            } else if (log.isInfoEnabled()) {
//                log.info(sm.getString("lifecycleBase.alreadyStopped", toString()));
//            }
//
//            return;
//        }
//
//        if (state.equals(LifecycleState.NEW)) {
//            state = LifecycleState.STOPPED;
//            return;
//        }
//
//        if (!state.equals(LifecycleState.STARTED) && !state.equals(LifecycleState.FAILED)) {
//            invalidTransition(BEFORE_STOP_EVENT);
//        }
//
//        try {
//            if (state.equals(LifecycleState.FAILED)) {
//                // Don't transition to STOPPING_PREP as that would briefly mark the
//                // component as available but do ensure the BEFORE_STOP_EVENT is
//                // fired
//                fireLifecycleEvent(BEFORE_STOP_EVENT, null);
//            } else {
//                setStateInternal(LifecycleState.STOPPING_PREP, null, false);
//            }
//
//            stopInternal();
//
//            // Shouldn't be necessary but acts as a check that sub-classes are
//            // doing what they are supposed to.
//            if (!state.equals(LifecycleState.STOPPING) && !state.equals(LifecycleState.FAILED)) {
//                invalidTransition(AFTER_STOP_EVENT);
//            }
//
//            setStateInternal(LifecycleState.STOPPED, null, false);
//        } catch (Throwable t) {
//            handleSubClassException(t, "lifecycleBase.stopFail", toString());
//        } finally {
//            if (this instanceof Lifecycle.SingleUse) {
//                // Complete stop process first
//                setStateInternal(LifecycleState.STOPPED, null, false);
//                destroy();
//            }
//        }
        throw new UnsupportedOperationException();
    }

    /**
     * Sub-classes must ensure that the state is changed to
     * {@link LifecycleState#STOPPING} during the execution of this method.
     * Changing state will trigger the {@link Lifecycle#STOP_EVENT} event.
     *
     * @throws LifecycleException Stop error occurred
     */
    protected void stopInternal() throws LifecycleException {
        throw new UnsupportedOperationException();
    }


    /**
     * Sub-classes implement this method to perform any instance initialisation
     * required.
     *
     * @throws LifecycleException If the initialisation fails
     */
    protected void initInternal() throws LifecycleException {
        throw new UnsupportedOperationException();
    }

    @Override
    public LifecycleState getState() {
        // private volatile LifecycleState state = LifecycleState.NEW;
        /* 创建了一个 LifecycleState 类型的状态 state */
        return state;
    }

    private synchronized void setStateInternal(LifecycleState state, Object data, boolean check)
            throws LifecycleException {

//        if (log.isDebugEnabled()) {
//            log.debug(sm.getString("lifecycleBase.setState", this, state));
//        }
//
//        if (check) {
//            // Must have been triggered by one of the abstract methods (assume
//            // code in this class is correct)
//            // null is never a valid state
//            if (state == null) {
//                invalidTransition("null");
//                // Unreachable code - here to stop eclipse complaining about
//                // a possible NPE further down the method
//                return;
//            }
//
//            // Any method can transition to failed
//            // startInternal() permits STARTING_PREP to STARTING
//            // stopInternal() permits STOPPING_PREP to STOPPING and FAILED to
//            // STOPPING
//            if (!(state == LifecycleState.FAILED ||
//                    (this.state == LifecycleState.STARTING_PREP &&
//                            state == LifecycleState.STARTING) ||
//                    (this.state == LifecycleState.STOPPING_PREP &&
//                            state == LifecycleState.STOPPING) ||
//                    (this.state == LifecycleState.FAILED &&
//                            state == LifecycleState.STOPPING))) {
//                // No other transition permitted
//                invalidTransition(state.name());
//            }
//        }
//
//        this.state = state;
//        String lifecycleEvent = state.getLifecycleEvent();
//        if (lifecycleEvent != null) {
//            fireLifecycleEvent(lifecycleEvent, data);
//        }
        throw new UnsupportedOperationException();
    }

    private void invalidTransition(String type) throws LifecycleException {
//        String msg = sm.getString("lifecycleBase.invalidTransition", type, toString(), state);
//        throw new LifecycleException(msg);
        throw new UnsupportedOperationException();
    }

    private void handleSubClassException(Throwable t, String key, Object... args) throws LifecycleException {
//        setStateInternal(LifecycleState.FAILED, null, false);
//        ExceptionUtils.handleThrowable(t);
//        String msg = sm.getString(key, args);
//        if (getThrowOnFailure()) {
//            if (!(t instanceof LifecycleException)) {
//                t = new LifecycleException(msg, t);
//            }
//            throw (LifecycleException) t;
//        } else {
//            log.error(msg, t);
//        }
        throw new UnsupportedOperationException();
    }
}
