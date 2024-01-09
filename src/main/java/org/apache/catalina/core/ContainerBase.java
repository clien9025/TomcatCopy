package org.apache.catalina.core;

import org.apache.catalina.*;
import org.apache.catalina.util.LifecycleMBeanBase;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.res.StringManager;

import java.beans.PropertyChangeSupport;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Abstract implementation of the <b>Container</b> interface, providing common functionality required by nearly every
 * implementation. Classes extending this base class must may implement a replacement for <code>invoke()</code>.
 * <p>
 * All subclasses of this abstract base class will include support for a Pipeline object that defines the processing to
 * be performed for each request received by the <code>invoke()</code> method of this class, utilizing the "Chain of
 * Responsibility" design pattern. A subclass should encapsulate its own processing functionality as a
 * <code>Valve</code>, and configure this Valve into the pipeline by calling <code>setBasic()</code>.
 * <p>
 * This implementation fires property change events, per the JavaBeans design pattern, for changes in singleton
 * properties. In addition, it fires the following <code>ContainerEvent</code> events to listeners who register
 * themselves with <code>addContainerListener()</code>:
 * <table border=1>
 * <caption>ContainerEvents fired by this implementation</caption>
 * <tr>
 * <th>Type</th>
 * <th>Data</th>
 * <th>Description</th>
 * </tr>
 * <tr>
 * <td><code>addChild</code></td>
 * <td><code>Container</code></td>
 * <td>Child container added to this Container.</td>
 * </tr>
 * <tr>
 * <td><code>{@link #getPipeline() pipeline}.addValve</code></td>
 * <td><code>Valve</code></td>
 * <td>Valve added to this Container.</td>
 * </tr>
 * <tr>
 * <td><code>removeChild</code></td>
 * <td><code>Container</code></td>
 * <td>Child container removed from this Container.</td>
 * </tr>
 * <tr>
 * <td><code>{@link #getPipeline() pipeline}.removeValve</code></td>
 * <td><code>Valve</code></td>
 * <td>Valve removed from this Container.</td>
 * </tr>
 * <tr>
 * <td><code>start</code></td>
 * <td><code>null</code></td>
 * <td>Container was started.</td>
 * </tr>
 * <tr>
 * <td><code>stop</code></td>
 * <td><code>null</code></td>
 * <td>Container was stopped.</td>
 * </tr>
 * </table>
 * Subclasses that fire additional events should document them in the class comments of the implementation class.
 *
 * @author Craig R. McClanahan
 */
public abstract class ContainerBase extends LifecycleMBeanBase implements Container {

    private static final Log log = LogFactory.getLog(ContainerBase.class);

    /**
     * Perform addChild with the permissions of this class. addChild can be called with the XML parser on the stack,
     * this allows the XML parser to have fewer privileges than Tomcat.
     */
    protected class PrivilegedAddChild implements PrivilegedAction<Void> {

        private final Container child;

        PrivilegedAddChild(Container child) {
            this.child = child;
        }

        @Override
        public Void run() {
            addChildInternal(child);
            return null;
        }

    }


    // ----------------------------------------------------- Instance Variables

    /**
     * The child Containers belonging to this Container, keyed by name.
     */
    protected final HashMap<String, Container> children = new HashMap<>();


    /**
     * The processor delay for this component.
     */
    protected int backgroundProcessorDelay = -1;


    /**
     * The Pipeline object with which this Container is associated.
     */
    protected final Pipeline pipeline = new StandardPipeline(this);

    /**
     * Lock used to control access to the Realm.
     * 锁用于控制对 Realm 的访问
     */
    private final ReadWriteLock realmLock = new ReentrantReadWriteLock();

    /**
     * The Realm with which this Container is associated.
     */
    private volatile Realm realm = null;

    /**
     * The string manager for this package.
     */
    protected static final StringManager sm = StringManager.getManager(ContainerBase.class);
    /**
     * The human-readable name of this Container.
     */
    protected String name = null;

    /**
     * The parent Container to which this Container is a child.
     */
    protected Container parent = null;

    /**
     * The property change support for this component.
     */
    protected final PropertyChangeSupport support = new PropertyChangeSupport(this);

    /**
     * Will children be started automatically when they are added.
     */
    protected boolean startChildren = true;

    /**
     * The container event listeners for this Container. Implemented as a CopyOnWriteArrayList since listeners may
     * invoke methods to add/remove themselves or other listeners and with a ReadWriteLock that would trigger a
     * deadlock.
     */
    protected final List<ContainerListener> listeners = new CopyOnWriteArrayList<>();

    /**
     * The parent class loader to be configured when we install a Loader.
     */
    protected ClassLoader parentClassLoader = null;


    // ------------------------------------------------------------- Properties


    /**
     * Set a name string (suitable for use by humans) that describes this Container. Within the set of child containers
     * belonging to a particular parent, Container names must be unique.
     *
     * @param name New name of this container
     * @throws IllegalStateException if this Container has already been added to the children of a parent Container
     *                               (after which the name may not be changed)
     *                               <p>
     *                               这个方法的作用是提供一个标准的方式来设置容器的名称，并确保关联的监听器能够被通知名称的更改。
     *                               这是Java Beans属性更改监听机制的一个典型应用，允许开发者对容器对象的名称属性更改进行监控，
     *                               例如，用于更新配置界面、日志记录或其他需要响应属性更改的场景
     */
    @Override
    public void setName(String name) {
        if (name == null) {
            throw new IllegalArgumentException(sm.getString("containerBase.nullName"));
        }
        String oldName = this.name;
        this.name = name;
        // 使用 PropertyChangeSupport对象support，调用 firePropertyChange 方法来通知监听器属性已更改。
        // 事件的属性名是 "name"，旧值是 oldName，新值是新设置的 this.name。
        support.firePropertyChange("name", oldName, this.name);
    }


    /**
     * Return the Container for which this Container is a child, if there is one. If there is no defined parent, return
     * <code>null</code>.
     */
    @Override
    public Container getParent() {
        return parent;
    }


    /**
     * Set the parent Container to which this Container is being added as a child. This Container may refuse to become
     * attached to the specified Container by throwing an exception.
     *
     * @param container Container to which this Container is being added as a child
     * @throws IllegalArgumentException if this Container refuses to become attached to the specified Container
     */
    @Override
    public void setParent(Container container) {

        Container oldParent = this.parent;
        this.parent = container;
        support.firePropertyChange("parent", oldParent, this.parent);

    }

    /**
     * Set the parent class loader (if any) for this web application. This call is meaningful only
     * <strong>before</strong> a Loader has been configured, and the specified value (if non-null) should be passed as
     * an argument to the class loader constructor.
     *
     * @param parent The new parent class loader
     */
    @Override
    public void setParentClassLoader(ClassLoader parent) {
        ClassLoader oldParentClassLoader = this.parentClassLoader;
        this.parentClassLoader = parent;
        support.firePropertyChange("parentClassLoader", oldParentClassLoader, this.parentClassLoader);

    }

    /**
     * Set the delay between the invocation of the execute method on this container and its children.
     *
     * @param delay The delay in seconds between the invocation of backgroundProcess methods
     */
    @Override
    public void setBackgroundProcessorDelay(int delay) {
        backgroundProcessorDelay = delay;
    }

    /**
     * Set the Realm with which this Container is associated.
     *
     * @param realm The newly associated Realm
     */
    @Override
    public void setRealm(Realm realm) {
        // private final ReadWriteLock realmLock = new ReentrantReadWriteLock();
        Lock l = realmLock.writeLock();
        l.lock();
        try {
            // Change components if necessary
            Realm oldRealm = this.realm;
            if (oldRealm == realm) {
                return;
            }
            this.realm = realm;

            // Stop the old component if necessary
            if (getState().isAvailable() && (oldRealm instanceof Lifecycle)) {
//                try {
//                    ((Lifecycle) oldRealm).stop();
//                } catch (LifecycleException e) {
//                    log.error(sm.getString("containerBase.realm.stop"), e);
//                }
                // todo 这里我知道是什么意思，而且后面的方法没实现，这里的代码暂时没有被调用
                throw new UnsupportedOperationException();
            }

            // Start the new component if necessary
            if (realm != null) {
                realm.setContainer(this);
            }
            if (getState().isAvailable() && (realm instanceof Lifecycle)) {
//                try {
//                    ((Lifecycle) realm).start();
//                } catch (LifecycleException e) {
//                    log.error(sm.getString("containerBase.realm.start"), e);
//                }
                // todo 这里我知道是什么意思，而且后面的方法没实现，这里的代码暂时没有被调用
                throw new UnsupportedOperationException();
            }

            // Report this property change to interested listeners
            support.firePropertyChange("realm", oldRealm, this.realm);
        } finally {
            l.unlock();
        }

    }


    /**
     * Return a name string (suitable for use by humans) that describes this Container. Within the set of child
     * containers belonging to a particular parent, Container names must be unique.
     */
    @Override
    public String getName() {
        return name;
    }


    // ------------------------------------------------------ Container Methods


    /**
     * Add a new child Container to those associated with this Container, if supported. Prior to adding this Container
     * to the set of children, the child's <code>setParent()</code> method must be called, with this Container as an
     * argument. This method may thrown an <code>IllegalArgumentException</code> if this Container chooses not to be
     * attached to the specified Container, in which case it is not added
     *
     * @param child New child Container to be added
     * @throws IllegalArgumentException if this exception is thrown by the <code>setParent()</code> method of the
     *                                  child Container
     * @throws IllegalArgumentException if the new child does not have a name unique from that of existing children
     *                                  of this Container
     * @throws IllegalStateException    if this Container does not support child Containers
     */
    @Override
    public void addChild(Container child) {
        /* 这部分代码检查是否启用了Java安全管理器。如果启用了安全管理器，它将使用特权操作PrivilegedAddChild来添加子容器，
        这是在受限环境（如Web应用服务器）中执行敏感操作的常用模式。否则，它直接调用addChildInternal */
        // public static final boolean IS_SECURITY_ENABLED = (System.getSecurityManager() != null);
        if (Globals.IS_SECURITY_ENABLED) {
            PrivilegedAction<Void> dp = new PrivilegedAddChild(child);
            AccessController.doPrivileged(dp);
        } else {
            addChildInternal(child);
        }
    }

    /**
     * 这段代码负责将一个新的子容器安全、有效地添加到一个父容器中。它涵盖了安全检查、唯一性验证、设置父子关系、容器事件通知以及
     * 条件性地启动子容器等一系列步骤。在整个过程中，它还通过异常处理来管理错误情况，确保操作的健壮性。
     * @param child
     */
    private void addChildInternal(Container child) {
        /* 1. 日志记录 */
        // 这行代码在调试模式下记录添加子容器的操作
        if (log.isDebugEnabled()) {
            log.debug("Add child " + child + " " + this);
        }
        /* 2. 同步代码块 */
        // 这个同步块确保了在修改children这个存储所有子容器的HashMap时的线程安全
        // protected final HashMap<String,Container> children = new HashMap<>();
        synchronized (children) {
            /* 3. 检查唯一性 */
            // 在同步块内，代码首先检查是否已存在具有相同名字的子容器。
            // 如果存在，抛出IllegalArgumentException异常，表示子容器的名字必须唯一
            if (children.get(child.getName()) != null) {
                throw new IllegalArgumentException(sm.getString("containerBase.child.notUnique", child.getName()));
            }
            /* 4. 设置父容器 */
            // 设置当前容器为 child (child 也是一个容器) 的 父容器
            child.setParent(this); // May throw IAE
            /* 5. 添加到子容器集合 */
            // 将 child (子容器) 添加到 当前容器 （ContainerBase的实例） 的 子容器集合（HashMap<String,Container> 类型的 children） 中
            children.put(child.getName(), child);
        }
        /* 6. 触发容器事件 */
        // 触发一个事件，通知其他组件有新的子容器被添加。这在Tomcat的内部事件系统中用于各种管理和监控目的。
        // String ADD_CHILD_EVENT = "addChild";
        fireContainerEvent(ADD_CHILD_EVENT, child);

        /* 7. 启动子容器 */
        // Start child
        // Don't do this inside sync block - start can be a slow process and
        // locking the children object can cause problems elsewhere
        // 在同步块外部，代码尝试启动新添加的子容器。这是在确保了子容器已成功添加并且不在同步块内进行的，以避免长时间的锁定
        try {
            // 如果 当前容器的状态是可用的 或 正在准备启动 ，并且 startChildren 标志为true（表示需要启动子容器），那么就调用子容器的start方法
            if ((getState().isAvailable() || LifecycleState.STARTING_PREP.equals(getState())) && startChildren) {
                child.start();
            }
        } catch (LifecycleException e) {
            // 如果 子容器 启动失败，抛出IllegalStateException异常
            throw new IllegalStateException(sm.getString("containerBase.child.start"), e);
        }
    }

    /**
     * Return the set of children Containers associated with this Container. If this Container has no children, a
     * zero-length array is returned.
     */
    @Override
    public Container[] findChildren() {
        synchronized (children) {
            /* 这行代码是在将 children这 个 HashMap 中所有的值（即所有的子容器对象）收集到一个新的 Container 数组中，并返回这个数组。
            这里的 children.values() 方法返回了一个 Collection<Container>，表示所有的子容器。
            toArray(new Container[0])是将这个集合转换成一个数组。这里传入的new Container[0]是指定了目标数组的类型，
            即 Container 类型的数组。如果 children 是空的，这个方法就会返回一个长度为0的 Container 数组。
            这种方式是一种常见的集合到数组的转换方法，在Java中广泛使用。*/
            return children.values().toArray(new Container[0]);
        }
    }


    // ------------------------------------------------------ Protected Methods

    /**
     * Notify all container event listeners that a particular event has occurred for this Container. The default
     * implementation performs this notification synchronously using the calling thread.
     * <p>
     * 它在 Tomcat 的容器内被用来通知所有 注册的容器事件监听器 ，指定类型的事件已经发生。
     * <p>
     * 这个 fireContainerEvent 方法的作用是允许容器在发生特定事件（如添加或删除子容器、启动或停止容器等）时，
     * 通知所有感兴趣的监听器。监听器可以是任何关心容器状态变化的组件，它们通过实现ContainerListener接口并注册到容器上来接收事件通知。
     *
     * @param type Event type
     * @param data Event data
     */
    @Override
    public void fireContainerEvent(String type, Object data) {
        /* 1. 检查监听器数量 */
        // 这行代码检查是否有注册的事件监听器。如果没有监听器注册，方法直接返回，不做任何操作。
        if (listeners.size() < 1) {
            return;
        }
        /* 2. 创建事件对象 */
        // 这行代码创建了一个新的ContainerEvent对象。这个事件对象包含了三个部分的信息：
        // 发生事件的容器（this），事件的类型（type），以及与事件相关的数据（data）。
        ContainerEvent event = new ContainerEvent(this, type, data);

        /* 3. 通知所有监听器 */
        // 方法遍历所有注册的容器事件监听器，并对每一个监听器调用其containerEvent方法，传入之前创建的事件对象。
        // 这一步是同步进行的，使用的是调用 fireContainerEvent 方法的线程。
        // Note for each uses an iterator internally so this is safe
        for (ContainerListener listener : listeners) {
            // 这个方法是ContainerListener接口的一部分，任何实现了这个接口的类都必须提供这个方法的具体实现。
            listener.containerEvent(event);
        }
    }

}
