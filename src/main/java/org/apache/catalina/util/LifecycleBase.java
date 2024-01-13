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
     * <p>
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
     * {@code false}
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
     *
     * @param listener The listener to add
     *                 <p>
     *                 这个方法提供了一种机制，允许外部代码注册LifecycleListener对象。当调用此方法时，它将传入的listener添加到lifecycleListeners列表中。
     *                 这使得其他对象可以注册监听器，以便在当前对象的生命周期发生变化时接收通知。比如，当对象启动或停止时，这些注册的监听器可能会被通知。
     */
    @Override
    public void addLifecycleListener(LifecycleListener listener) {
        lifecycleListeners.add(listener);
    }

    /**
     * Allow sub classes to fire {@link Lifecycle} events.
     *
     * @param type Event type
     * @param data Data associated with event.
     */
    protected void fireLifecycleEvent(String type, Object data) {
        LifecycleEvent event = new LifecycleEvent(this, type, data);
        for (LifecycleListener listener : lifecycleListeners) {
            listener.lifecycleEvent(event);
        }
    }


    @Override
    public final synchronized void init() throws LifecycleException {
        if (!state.equals(LifecycleState.NEW)) {
            invalidTransition(BEFORE_INIT_EVENT);
        }

        try {
            setStateInternal(LifecycleState.INITIALIZING, null, false);
            initInternal();
            setStateInternal(LifecycleState.INITIALIZED, null, false);
        } catch (Throwable t) {
            handleSubClassException(t, "lifecycleBase.initFail", toString());
        }
    }


    @Override
    public final synchronized void start() throws LifecycleException {

        if (LifecycleState.STARTING_PREP.equals(state) || LifecycleState.STARTING.equals(state) ||
                LifecycleState.STARTED.equals(state)) {

            if (log.isDebugEnabled()) {
                Exception e = new LifecycleException();
                log.debug(sm.getString("lifecycleBase.alreadyStarted", toString()), e);
            } else if (log.isInfoEnabled()) {
                log.info(sm.getString("lifecycleBase.alreadyStarted", toString()));
            }

            return;
        }

        if (state.equals(LifecycleState.NEW)) {
            init();
        } else if (state.equals(LifecycleState.FAILED)) {
            stop();
        } else if (!state.equals(LifecycleState.INITIALIZED) &&
                !state.equals(LifecycleState.STOPPED)) {
            invalidTransition(BEFORE_START_EVENT);
        }

        try {
            setStateInternal(LifecycleState.STARTING_PREP, null, false);
            startInternal();
            if (state.equals(LifecycleState.FAILED)) {
                // This is a 'controlled' failure. The component put itself into the
                // FAILED state so call stop() to complete the clean-up.
                stop();
            } else if (!state.equals(LifecycleState.STARTING)) {
                // Shouldn't be necessary but acts as a check that sub-classes are
                // doing what they are supposed to.
                invalidTransition(AFTER_START_EVENT);
            } else {
                setStateInternal(LifecycleState.STARTED, null, false);
            }
        } catch (Throwable t) {
            // This is an 'uncontrolled' failure so put the component into the
            // FAILED state and throw an exception.
            handleSubClassException(t, "lifecycleBase.startFail", toString());
        }
    }

    /**
     * Sub-classes must ensure that the state is changed to
     * {@link LifecycleState#STARTING} during the execution of this method.
     * Changing state will trigger the {@link Lifecycle#START_EVENT} event.
     * <p>
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
        /* 1. 检查当前状态 */
        // 如果当前状态 (state) 是 STOPPING_PREP、STOPPING 或 STOPPED，则表示组件已经在停止过程中或已停止。
        // 此时，根据日志级别，记录相应的调试或信息日志，并直接返回
        if (LifecycleState.STOPPING_PREP.equals(state) || LifecycleState.STOPPING.equals(state) ||
                LifecycleState.STOPPED.equals(state)) {

            if (log.isDebugEnabled()) {
                Exception e = new LifecycleException();
                log.debug(sm.getString("lifecycleBase.alreadyStopped", toString()), e);
            } else if (log.isInfoEnabled()) {
                log.info(sm.getString("lifecycleBase.alreadyStopped", toString()));
            }

            return;
        }
        /* 2. 处理 NEW 状态 */
        // 如果状态是 NEW，则直接将状态设置为 STOPPED 并返回。这意味着如果组件尚未启动，它会被直接标记为已停止
        if (state.equals(LifecycleState.NEW)) {
            state = LifecycleState.STOPPED;
            return;
        }
        /* 3. 检查状态合法性 */
        // 如果当前状态既不是 STARTED 也不是 FAILED，则调用 invalidTransition 方法处理非法的状态转换。
        if (!state.equals(LifecycleState.STARTED) && !state.equals(LifecycleState.FAILED)) {
            invalidTransition(BEFORE_STOP_EVENT);
        }
        /* 4. 状态转换和停止处理 */
        // 如果当前状态是 FAILED，则触发 BEFORE_STOP_EVENT 事件，但不改变状态到 STOPPING_PREP（通常用于标记开始停止过程）。
        try {
            if (state.equals(LifecycleState.FAILED)) {
                // Don't transition to STOPPING_PREP as that would briefly mark the
                // component as available but do ensure the BEFORE_STOP_EVENT is
                // fired
                fireLifecycleEvent(BEFORE_STOP_EVENT, null);
            } else {
                // 如果不是 FAILED 状态，将状态设置为 STOPPING_PREP。
                setStateInternal(LifecycleState.STOPPING_PREP, null, false);
            }
            // 调用 stopInternal() 方法执行实际的停止逻辑。
            stopInternal();

            /* 5. 检查状态和状态转换 */
            // 检查停止后的状态，确保它是 STOPPING 或 FAILED，否则处理非法状态转换。
            // Shouldn't be necessary but acts as a check that sub-classes are
            // doing what they are supposed to.
            if (!state.equals(LifecycleState.STOPPING) && !state.equals(LifecycleState.FAILED)) {
                invalidTransition(AFTER_STOP_EVENT);
            }
            // 将状态设置为 STOPPED
            setStateInternal(LifecycleState.STOPPED, null, false);
        } catch (Throwable t) {
            /* 6. 异常处理 */
            handleSubClassException(t, "lifecycleBase.stopFail", toString());
        } finally {
            /* 7. 处理单次使用组件的特殊情况 */
            // 如果这个类实现了 Lifecycle.SingleUse 接口，则在停止逻辑完成后调用 destroy 方法
            if (this instanceof Lifecycle.SingleUse) {
                // Complete stop process first
                setStateInternal(LifecycleState.STOPPED, null, false);
                destroy();
            }
        }
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

    @Override
    public final synchronized void destroy() throws LifecycleException {
        /* 1. 处理 FAILED 状态 */
        // 如果对象的当前状态 (state) 是 FAILED，则首先尝试调用 stop() 方法来进行清理。
        // 如果 stop() 方法抛出异常，该异常将被捕获并记录，但不会阻止销毁流程的继续
        if (LifecycleState.FAILED.equals(state)) {
            try {
                // Triggers clean-up
                stop();
            } catch (LifecycleException e) {
                // Just log. Still want to destroy.
                log.error(sm.getString("lifecycleBase.destroyStopFail", toString()), e);
            }
        }
        /* 2. 检查是否已经在销毁或已销毁 */
        // 如果当前状态是 DESTROYING 或 DESTROYED，则根据日志级别记录相应的消息，并直接返回。这防止了重复销毁的尝试
        if (LifecycleState.DESTROYING.equals(state) || LifecycleState.DESTROYED.equals(state)) {
            if (log.isDebugEnabled()) {
                Exception e = new LifecycleException();
                log.debug(sm.getString("lifecycleBase.alreadyDestroyed", toString()), e);
            } else if (log.isInfoEnabled() && !(this instanceof Lifecycle.SingleUse)) {
                // Rather than have every component that might need to call
                // destroy() check for SingleUse, don't log an info message if
                // multiple calls are made to destroy()
                log.info(sm.getString("lifecycleBase.alreadyDestroyed", toString()));
            }

            return;
        }
        /* 3. 状态检查 */
        // 检查当前状态是否为 STOPPED、FAILED、NEW 或 INITIALIZED 之一。如果不是，表示状态转换非法，并通过调用 invalidTransition 方法处理。
        if (!state.equals(LifecycleState.STOPPED) && !state.equals(LifecycleState.FAILED) &&
                !state.equals(LifecycleState.NEW) && !state.equals(LifecycleState.INITIALIZED)) {
            invalidTransition(BEFORE_DESTROY_EVENT);
        }
        /* 4. 设置状态并调用 destroyInternal */
        // 将状态设置为 DESTROYING。
        // 调用 destroyInternal() 方法，这是一个抽象方法，由子类具体实现，用于执行实际的销毁逻辑。
        // 完成销毁后，将状态设置为 DESTROYED
        try {
            setStateInternal(LifecycleState.DESTROYING, null, false);
            destroyInternal();
            setStateInternal(LifecycleState.DESTROYED, null, false);
        } catch (Throwable t) {
            handleSubClassException(t, "lifecycleBase.destroyFail", toString());
        }
    }


    /**
     * Sub-classes implement this method to perform any instance destruction
     * required.
     *
     * @throws LifecycleException If the destruction fails
     */
    protected void destroyInternal() throws LifecycleException {
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

    @Override
    public String getStateName() {
        return getState().toString();
    }


    /**
     * Provides a mechanism for sub-classes to update the component state.
     * Calling this method will automatically fire any associated
     * {@link Lifecycle} event. It will also check that any attempted state
     * transition is valid for a sub-class.
     *
     * @param state The new state for this component
     * @throws LifecycleException when attempting to set an invalid state
     */
    protected synchronized void setState(LifecycleState state) throws LifecycleException {
        setStateInternal(state, null, true);
    }


    /**
     * Provides a mechanism for sub-classes to update the component state.
     * Calling this method will automatically fire any associated
     * {@link Lifecycle} event. It will also check that any attempted state
     * transition is valid for a sub-class.
     *
     * @param state The new state for this component
     * @param data  The data to pass to the associated {@link Lifecycle} event
     * @throws LifecycleException when attempting to set an invalid state
     */
    protected synchronized void setState(LifecycleState state, Object data)
            throws LifecycleException {
        setStateInternal(state, data, true);
    }

    /**
     * 这段代码定义了一个名为 setStateInternal 的私有同步方法，它用于管理一个对象的生命周期状态。
     * 这个方法是一个典型的状态管理机制，在像Web服务器这样的复杂系统中常见
     * <p>
     * 这个方法是生命周期管理的关键部分，确保对象的状态转换是有序和合规的。它通过一系列检查来防止非法的状态转换，
     * 并在状态发生变化时触发相应的事件，这对于维护系统的稳定性和预测性非常重要。通过同步机制，该方法还确保了状态变化的线程安全。
     *
     * @param state
     * @param data
     * @param check
     * @throws LifecycleException
     */
    private synchronized void setStateInternal(LifecycleState state, Object data, boolean check)
            throws LifecycleException {
        /* 1. 日志记录 */
        // 如果启用了调试日志，该方法会记录当前对象和欲设置的状态
        if (log.isDebugEnabled()) {
            log.debug(sm.getString("lifecycleBase.setState", this, state));
        }
        /* 2. 状态检查 */
        // 如果 check 参数为 true，该方法会执行一系列检查以确保状态转换是合法的
        if (check) {
            // Must have been triggered by one of the abstract methods (assume
            // code in this class is correct)
            // null is never a valid state
            // 首先，确认 state 不是 null，因为 null 不是一个有效的状态
            if (state == null) {
                invalidTransition("null");
                // Unreachable code - here to stop eclipse complaining about
                // a possible NPE further down the method
                return;
            }
            // 然后，检查状态转换是否符合预定的规则。例如，允许从 STARTING_PREP 状态转换到 STARTING，
            // 从 STOPPING_PREP 状态转换到 STOPPING，以及在任何时候都可以转换到 FAILED 状态。
            // 如果尝试进行不允许的状态转换，则调用 invalidTransition 方法处理

            // Any method can transition to failed
            // startInternal() permits STARTING_PREP to STARTING
            // stopInternal() permits STOPPING_PREP to STOPPING and FAILED to
            // STOPPING
            /*这里的条件是在确认允许的状态转换。条件前面的 ! 取反操作符意味着，如果状态转换不符合上述任一规则，即进入 if 语句内部。
           因此，如果尝试进行不允许的状态转换，代码将执行 if 语句内部的逻辑，这通常是调用 invalidTransition 方法来处理非法的状态转换*/
            if (!(state == LifecycleState.FAILED ||
                    (this.state == LifecycleState.STARTING_PREP &&
                            state == LifecycleState.STARTING) ||
                    (this.state == LifecycleState.STOPPING_PREP &&
                            state == LifecycleState.STOPPING) ||
                    (this.state == LifecycleState.FAILED &&
                            state == LifecycleState.STOPPING))) {
                // No other transition permitted
                invalidTransition(state.name());
            }
        }
        /* 3. 设置状态 */
        // 将对象的状态设置为提供的新状态
        this.state = state;
        /* 4. 触发生命周期事件 */
        // 如果新状态对应一个生命周期事件（通过 state.getLifecycleEvent() 获取），
        // 则调用 fireLifecycleEvent 方法来触发该事件，并传递相关数据
        String lifecycleEvent = state.getLifecycleEvent();
        if (lifecycleEvent != null) {
            fireLifecycleEvent(lifecycleEvent, data);
        }
    }

    /**
     * 无效转换（情况如下）：
     * invalidTransition("null");
     *
     * @param type
     * @throws LifecycleException
     */
    private void invalidTransition(String type) throws LifecycleException {
        String msg = sm.getString("lifecycleBase.invalidTransition", type, toString(), state);
        throw new LifecycleException(msg);
    }

    private void handleSubClassException(Throwable t, String key, Object... args) throws LifecycleException {
        /* 1. 设置状态为 FAILED */
        // 这表示当处理一个子类抛出的异常时，生命周期管理器将内部状态设置为 FAILED
        setStateInternal(LifecycleState.FAILED, null, false);
        /* 2. 处理传入的异常 */
        // 这个步骤处理传入的 Throwable 对象。如果 Throwable 是 Error 的实例，它将被抛出
        ExceptionUtils.handleThrowable(t);
        /* 3. 构造和记录错误消息 */
        // 这里使用提供的键 (key) 和参数 (args) 来构造一个错误消息
        String msg = sm.getString(key, args);
        /* 4. 根据配置抛出或记录异常 */
        // 如果 getThrowOnFailure() 返回 true，则抛出一个 LifecycleException（如果 Throwable 不是 LifecycleException 的实例，
        // 会创建一个新的 LifecycleException）
        if (getThrowOnFailure()) {
            if (!(t instanceof LifecycleException)) {
                t = new LifecycleException(msg, t);
            }
            throw (LifecycleException) t;
        } else {
            // 如果 getThrowOnFailure() 返回 false，则记录错误信息，但不抛出异常
            // getThrowOnFailure 意思是失败时抛出
            log.error(msg, t);
        }
    }
}
