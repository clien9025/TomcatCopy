package org.apache.catalina.valves;

import org.apache.catalina.*;
import org.apache.catalina.util.LifecycleMBeanBase;
import org.apache.catalina.util.ToStringUtil;
import org.apache.juli.logging.Log;
import org.apache.tomcat.util.res.StringManager;

/**
 * Convenience base class for implementations of the <b>Valve</b> interface. A subclass <strong>MUST</strong> implement
 * an <code>invoke()</code> method to provide the required functionality, and <strong>MAY</strong> implement the
 * <code>Lifecycle</code> interface to provide configuration management and lifecycle support.
 *
 * @author Craig R. McClanahan
 */
public abstract class ValveBase extends LifecycleMBeanBase implements Contained, Valve {


    protected static final StringManager sm = StringManager.getManager(ValveBase.class);


    // ------------------------------------------------------ Constructor

    public ValveBase() {
        this(false);
    }

    /**
     * 当创建 ValveBase 类的新实例时，可以指定这个实例是否支持异步请求。
     *
     * @param asyncSupported
     */
    public ValveBase(boolean asyncSupported) {
        this.asyncSupported = asyncSupported;
    }

    // ------------------------------------------------------ Instance Variables

    /**
     * Does this valve support Servlet 3+ async requests?
     * 这是一个受保护的布尔型实例变量 asyncSupported，它被用来标记这个Valve实例是否支持Servlet 3+的异步请求。
     * 如果一个Valve支持异步处理，那么它可以处理通过Servlet 3.0 API发起的异步请求。
     */
    protected boolean asyncSupported;


    /**
     * The Container whose pipeline this Valve is a component of.
     */
    protected Container container = null;


    /**
     * Container log
     */
    protected Log containerLog = null;


    /**
     * The next Valve in the pipeline this Valve is a component of.
     */
    protected Valve next = null;


    // -------------------------------------------------------------- Properties

    @Override
    public Container getContainer() {
        return container;
    }


    @Override
    public void setContainer(Container container) {
        this.container = container;
    }


    @Override
    public boolean isAsyncSupported() {
        return asyncSupported;
    }


    public void setAsyncSupported(boolean asyncSupported) {
        this.asyncSupported = asyncSupported;
    }


    @Override
    public Valve getNext() {
        return next;
    }


    @Override
    public void setNext(Valve valve) {
        this.next = valve;
    }


    // ---------------------------------------------------------- Public Methods

    /**
     * {@inheritDoc}
     * <p>
     * The default implementation is NO-OP.
     */
    @Override
    public void backgroundProcess() {
        // NOOP by default
    }


    @Override
    protected void initInternal() throws LifecycleException {
        super.initInternal();
        containerLog = getContainer().getLogger();
    }


    /**
     * Start this component and implement the requirements of
     * {@link org.apache.catalina.util.LifecycleBase#startInternal()}.
     *
     * @throws LifecycleException if this component detects a fatal error that prevents this component from being
     *                            used
     */
    @Override
    protected synchronized void startInternal() throws LifecycleException {
        setState(LifecycleState.STARTING);
    }


    /**
     * Stop this component and implement the requirements of
     * {@link org.apache.catalina.util.LifecycleBase#stopInternal()}.
     *
     * @throws LifecycleException if this component detects a fatal error that prevents this component from being
     *                            used
     */
    @Override
    protected synchronized void stopInternal() throws LifecycleException {
        setState(LifecycleState.STOPPING);
    }


    @Override
    public String toString() {
        return ToStringUtil.toString(this);
    }


    // -------------------- JMX and Registration --------------------

    /**
     * 用于为JMX管理的目的生成一个描述 Valve 对象在其容器中位置和类型的唯一字符串标识符。
     * 这在用于监控和管理Web容器（如Tomcat）中的组件时非常有用。
     *
     * @return
     */
    @Override
    public String getObjectNameKeyProperties() {
        StringBuilder name = new StringBuilder("type=Valve");

        Container container = getContainer();

        name.append(container.getMBeanKeyProperties());

        int seq = 0;
        /* 遍历管道中的阀门 */
        // Pipeline may not be present in unit testing
        Pipeline p = container.getPipeline();
        if (p != null) {
            for (Valve valve : p.getValves()) {
                // 如果在遍历过程中遇到 null 的 Valve 对象，则跳过继续
                // Skip null valves
                if (valve == null) {
                    continue;
                }
                // 如果遇到的 Valve 是当前对象（this），则停止遍历
                // Only compare valves in pipeline until we find this valve
                if (valve == this) {
                    break;
                }
                // 如果遇到的 Valve 类型与当前对象相同，增加 seq 的值
                if (valve.getClass() == this.getClass()) {
                    // Duplicate valve earlier in pipeline
                    // increment sequence number
                    seq++;
                }
            }
        }
        /* 添加序列号到名称 */
        // 如果 seq 大于0（表示管道中有相同类型的其他 Valve），则在名称字符串中追加序列号(数量)
        if (seq > 0) {
            name.append(",seq=");
            name.append(seq);
        }
        /* 提取并添加类名 */
        String className = this.getClass().getName();
        int period = className.lastIndexOf('.');
        if (period >= 0) {
            // 获取当前对象的类名并从中提取简短类名（去除包名）
            className = className.substring(period + 1);
        }
        // 将简短类名追加到名称字符串
        name.append(",name=");
        name.append(className);

        return name.toString();
    }


    @Override
    public String getDomainInternal() {
        Container c = getContainer();
        if (c == null) {
            return null;
        } else {
            return c.getDomain();
        }
    }
}
