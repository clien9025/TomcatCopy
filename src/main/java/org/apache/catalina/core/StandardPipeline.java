package org.apache.catalina.core;

import org.apache.catalina.*;
import org.apache.catalina.util.LifecycleBase;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.ExceptionUtils;
import org.apache.tomcat.util.res.StringManager;

/**
 * Standard implementation of a processing <b>Pipeline</b> that will invoke a series of Valves that have been configured
 * to be called in order. This implementation can be used for any type of Container. <b>IMPLEMENTATION WARNING</b> -
 * This implementation assumes that no calls to <code>addValve()</code> or <code>removeValve</code> are allowed while a
 * request is currently being processed. Otherwise, the mechanism by which per-thread state is maintained will need to
 * be modified.
 *
 * @author Craig R. McClanahan
 */
public class StandardPipeline extends LifecycleBase implements Pipeline {
    private static final Log log = LogFactory.getLog(StandardPipeline.class);
    private static final StringManager sm = StringManager.getManager(StandardPipeline.class);

    // ----------------------------------------------------------- Constructors


    /**
     * Construct a new StandardPipeline instance with no associated Container.
     */
    public StandardPipeline() {

        this(null);

    }


    /**
     * Construct a new StandardPipeline instance that is associated with the specified Container.
     *
     * @param container The container we should be associated with
     */
    public StandardPipeline(Container container) {

        super();
        setContainer(container);

    }

    // ----------------------------------------------------- Instance Variables


    /**
     * The basic Valve (if any) associated with this Pipeline.
     */
    protected Valve basic = null;


    /**
     * The Container with which this Pipeline is associated.
     */
    protected Container container = null;


    /**
     * The first valve associated with this Pipeline.
     */
    protected Valve first = null;

    /**
     * Set the Container with which this Pipeline is associated.
     *
     * @param container The new associated container
     */
    @Override
    public void setContainer(Container container) {
        this.container = container;
    }

    /**
     * <p>
     * Set the Valve instance that has been distinguished as the basic Valve for this Pipeline (if any). Prior to
     * setting the basic Valve, the Valve's <code>setContainer()</code> will be called, if it implements
     * <code>Contained</code>, with the owning Container as an argument. The method may throw an
     * <code>IllegalArgumentException</code> if this Valve chooses not to be associated with this Container, or
     * <code>IllegalStateException</code> if it is already associated with a different Container.
     * </p>
     *
     * 通过这个方法，可以确保 Pipeline 中的基本 Valve 始终是最新的，并且在更换过程中旧的 Valve 会被正确地停止和解除关联，
     * 新的 Valve 被配置并启动。这是一个典型的在复杂组件中更换子组件的操作，需要保证整个过程的线程安全和状态一致性。
     *
     * setBasic 方法是 StandardPipeline 类的一个部分，它用于设置或替换基本Valve。这个方法确保新的基本Valve在添加到 Pipeline 之前
     * 和之后都处于正确的状态。它会停止旧的基本Valve（如果存在），启动新的Valve，并确保Pipeline的Valve链反映了这种更改。
     *
     * 在使用 setBasic 方法时，StandardPipeline 会先检查Valve是否已经与另一个 Container关联，
     * 如果是，可能会抛出 IllegalStateException。如果Valve选择不与当前的 Container 关联，
     * 则可能会抛出 IllegalArgumentException。这些检查是为了确保 Valve 与 Container 的关系是一致和正确的，避免配置错误导致的潜在问题。
     *
     * @param valve Valve to be distinguished as the basic Valve
     */
    @Override
    public void setBasic(Valve valve) {
        /* 1. 检查是否需要更换基本Valve*/
        // Change components if necessary（必要时更换组件）
        Valve oldBasic = this.basic;
        if (oldBasic == valve) {
            return;
        }
        /* 2. 停止并解除旧的基本Valve*/
        // Stop the old component if necessary（必要时停止就组件）
        if (oldBasic != null) {
//            /* 如果存在旧的基本Valve（oldBasic不为null），并且当前的状态允许（getState().isAvailable()返回true），
//            那么如果它实现了Lifecycle接口，尝试停止它 */
//            if (getState().isAvailable() && (oldBasic instanceof Lifecycle)) {
//                try {
//                    ((Lifecycle) oldBasic).stop();
//                } catch (LifecycleException e) {
//                    log.error(sm.getString("standardPipeline.basic.stop"), e);
//                }
//            }
//            /* 如果旧的基本Valve实现了Contained接口，解除其与当前Container的关联 */
//            if (oldBasic instanceof Contained) {
//                try {
//                    ((Contained) oldBasic).setContainer(null);
//                } catch (Throwable t) {
//                    ExceptionUtils.handleThrowable(t);
//                }
//            }
            throw new UnsupportedOperationException();
        }
        /* 3. 配置并启动新的基本Valve */
        // Start the new component if necessary(如果有必要的话，启动新组件)
        if (valve == null) {
            return;
        }
        /* 如果传入的valve不为null，检查它是否实现了Contained接口。如果是，将当前的Container设置为其容器 */
        if (valve instanceof Contained) {// 如果属于 Contained（接口）子类。（StandardEngineValve 是 Contained 的间接实现类）
            ((Contained) valve).setContainer(this.container);
        }
        /* 如果当前状态允许，并且valve实现了Lifecycle接口，尝试启动它 */
        if (getState().isAvailable() && valve instanceof Lifecycle) {
//            try {
//                ((Lifecycle) valve).start();
//            } catch (LifecycleException e) {
//                log.error(sm.getString("standardPipeline.basic.start"), e);
//                return;
//            }
            throw new UnsupportedOperationException();
        }
        /* 4. 更新Pipeline中的Valve链 */
        // Update the pipeline（更新管道）
        Valve current = first;
        // 遍历Pipeline中的Valve链，找到引用旧的基本Valve的位置。
        // 将引用旧的基本Valve的next引用更新为新的基本Valve。
        while (current != null) {
//            if (current.getNext() == oldBasic) {
//                current.setNext(valve);
//                break;
//            }
//            current = current.getNext();
            throw new UnsupportedOperationException();
        }
        /* 5. 更新基本Valve引用*/
        this.basic = valve;// 将类的basic成员变量更新为新的基本Valve

    }
}
