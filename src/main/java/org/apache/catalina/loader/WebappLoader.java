package org.apache.catalina.loader;

import org.apache.catalina.Context;
import org.apache.catalina.Loader;
import org.apache.catalina.util.LifecycleMBeanBase;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.res.StringManager;

import java.beans.PropertyChangeSupport;

/**
 * Classloader implementation which is specialized for handling web applications in the most efficient way, while being
 * Catalina aware (all accesses to resources are made through {@link org.apache.catalina.WebResourceRoot}). This class
 * loader supports detection of modified Java classes, which can be used to implement auto-reload support.
 * <p>
 * This class loader is configured via the Resources children of its Context prior to calling <code>start()</code>. When
 * a new class is required, these Resources will be consulted first to locate the class. If it is not present, the
 * system class loader will be used instead.
 *
 * @author Craig R. McClanahan
 * @author Remy Maucherat
 */
public class WebappLoader extends LifecycleMBeanBase implements Loader {


    private static final Log log = LogFactory.getLog(WebappLoader.class);

    // ----------------------------------------------------- Instance Variables


    /**
     * The Context with which this Loader has been associated.
     */
    private Context context = null;

    /**
     * The string manager for this package.
     */
    protected static final StringManager sm = StringManager.getManager(WebappLoader.class);

    /**
     * The class loader being managed by this Loader component.
     */
    private WebappClassLoaderBase classLoader = null;



    /**
     * The Java class name of the ClassLoader implementation to be used. This class should extend WebappClassLoaderBase,
     * otherwise, a different loader implementation must be used.
     */
    private String loaderClass = ParallelWebappClassLoader.class.getName();


    /**
     * The "follow standard delegation model" flag that will be used to configure our ClassLoader.
     */
    private boolean delegate = false;


    /**
     * The property change support for this component.
     */
    protected final PropertyChangeSupport support = new PropertyChangeSupport(this);



    // ------------------------------------------------------------- Properties

    /**
     * Return the Java class loader to be used by this Container.
     */
    @Override
    public ClassLoader getClassLoader() {
        return classLoader;
    }


    @Override
    public Context getContext() {
        return context;
    }


    @Override
    public void setContext(Context context) {

        if (this.context == context) {
            return;
        }

        if (getState().isAvailable()) {
            throw new IllegalStateException(sm.getString("webappLoader.setContext.ise"));
        }

        // Process this property change
        Context oldContext = this.context;
        this.context = context;
        support.firePropertyChange("context", oldContext, this.context);
    }


    /**
     * Set the ClassLoader class name.
     *
     * @param loaderClass The new ClassLoader class name
     */
    public void setLoaderClass(String loaderClass) {
        this.loaderClass = loaderClass;
    }

    /**
     * Set the ClassLoader instance, without relying on reflection This method will also invoke
     * {@link #setLoaderClass(String)} with {@code loaderInstance.getClass().getName()} as an argument
     *
     * @param loaderInstance The new ClassLoader instance to use
     */
    public void setLoaderInstance(WebappClassLoaderBase loaderInstance) {
        this.classLoader = loaderInstance;
        setLoaderClass(loaderInstance.getClass().getName());
    }

    /**
     * Set the "follow standard delegation model" flag used to configure our ClassLoader.
     *
     * @param delegate The new flag
     */
    @Override
    public void setDelegate(boolean delegate) {
        boolean oldDelegate = this.delegate;
        this.delegate = delegate;
        support.firePropertyChange("delegate", Boolean.valueOf(oldDelegate), Boolean.valueOf(this.delegate));
    }


    // --------------------------------------------------------- Public Methods
}
