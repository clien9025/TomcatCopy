package org.apache.catalina.valves;

import org.apache.catalina.Contained;
import org.apache.catalina.Container;
import org.apache.catalina.Valve;
import org.apache.catalina.util.LifecycleMBeanBase;
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


//    /**
//     * Container log
//     */
//    protected Log containerLog = null;
//
//
//    /**
//     * The next Valve in the pipeline this Valve is a component of.
//     */
//    protected Valve next = null;


    /**
     * The next Valve in the pipeline this Valve is a component of.
     */
    protected Valve next = null;

    @Override
    public void setContainer(Container container) {
        this.container = container;
    }

    @Override
    public Valve getNext() {
        return next;
    }


    @Override
    public void setNext(Valve valve) {
        this.next = valve;
    }
}
