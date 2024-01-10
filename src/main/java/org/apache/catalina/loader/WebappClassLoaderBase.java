package org.apache.catalina.loader;

import org.apache.catalina.Lifecycle;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.res.StringManager;

import java.net.URL;
import java.net.URLClassLoader;
import java.security.AccessControlException;
import java.security.Policy;

/**
 * Specialized web application class loader.
 * <p>
 * This class loader is a full reimplementation of the <code>URLClassLoader</code> from the JDK. It is designed to be
 * fully compatible with a normal <code>URLClassLoader</code>, although its internal behavior may be completely
 * different.
 * <p>
 * <strong>IMPLEMENTATION NOTE</strong> - By default, this class loader follows the delegation model required by the
 * specification. The bootstrap class loader will be queried first, then the local repositories, and only then
 * delegation to the parent class loader will occur. This allows the web application to override any shared class except
 * the classes from J2SE. Special handling is provided from the JAXP XML parser interfaces, the JNDI interfaces, and the
 * classes from the servlet API, which are never loaded from the webapp repositories. The <code>delegate</code> property
 * allows an application to modify this behavior to move the parent class loader ahead of the local repositories.
 * <p>
 * <strong>IMPLEMENTATION NOTE</strong> - Due to limitations in Jasper compilation technology, any repository which
 * contains classes from the servlet API will be ignored by the class loader.
 * <p>
 * <strong>IMPLEMENTATION NOTE</strong> - The class loader generates source URLs which include the full JAR URL when a
 * class is loaded from a JAR file, which allows setting security permission at the class level, even when a class is
 * contained inside a JAR.
 * <p>
 * <strong>IMPLEMENTATION NOTE</strong> - Local repositories are searched in the order they are added via the initial
 * constructor.
 * <p>
 * <strong>IMPLEMENTATION NOTE</strong> - No check for sealing violations or security is made unless a security manager
 * is present.
 * <p>
 * <strong>IMPLEMENTATION NOTE</strong> - As of 8.0, this class loader implements {@link InstrumentableClassLoader},
 * permitting web application classes to instrument other classes in the same web application. It does not permit
 * instrumentation of system or container classes or classes in other web apps.
 *
 * @author Remy Maucherat
 * @author Craig R. McClanahan
 */
public abstract class WebappClassLoaderBase extends URLClassLoader/*
        implements Lifecycle, InstrumentableClassLoader, WebappProperties, PermissionCheck*/ {

    private static final Log log = LogFactory.getLog(WebappClassLoaderBase.class);



    // ------------------------------------------------------- Static Variables

    /**
     * The string manager for this package.
     */
    protected static final StringManager sm = StringManager.getManager(WebappClassLoaderBase.class);


    // ----------------------------------------------------- Instance Variables


    /**
     * The parent class loader.
     */
    protected final ClassLoader parent;

    /**
     * The bootstrap class loader used to load the JavaSE classes. In some implementations this class loader is always
     * <code>null</code> and in those cases {@link ClassLoader#getParent()} will be called recursively on the system
     * class loader and the last non-null result used.
     */
    private ClassLoader javaseClassLoader;


    /**
     * Instance of the SecurityManager installed.
     */
    protected final SecurityManager securityManager;


    // ----------------------------------------------------------- Constructors

    /**
     * Construct a new ClassLoader with no defined repositories and no parent ClassLoader.
     */
    protected WebappClassLoaderBase() {

        super(new URL[0]);

        ClassLoader p = getParent();
        if (p == null) {
            p = getSystemClassLoader();
        }
        this.parent = p;

        ClassLoader j = String.class.getClassLoader();
        if (j == null) {
            j = getSystemClassLoader();
            while (j.getParent() != null) {
                j = j.getParent();
            }
        }
        this.javaseClassLoader = j;

        securityManager = System.getSecurityManager();
        if (securityManager != null) {
            refreshPolicy();
        }
    }


    /**
     * Construct a new ClassLoader with no defined repositories and the given parent ClassLoader.
     * <p>
     * Method is used via reflection - see {@link WebappLoader#createClassLoader()}
     *
     * @param parent Our parent class loader
     */
    protected WebappClassLoaderBase(ClassLoader parent) {

        super(new URL[0], parent);

        ClassLoader p = getParent();
        if (p == null) {
            p = getSystemClassLoader();
        }
        this.parent = p;

        ClassLoader j = String.class.getClassLoader();
        if (j == null) {
            j = getSystemClassLoader();
            while (j.getParent() != null) {
                j = j.getParent();
            }
        }
        this.javaseClassLoader = j;

        securityManager = System.getSecurityManager();
        if (securityManager != null) {
            refreshPolicy();
        }
    }




    // ------------------------------------------------------ Protected Methods

    /**
     * Refresh the system policy file, to pick up eventual changes.
     */
    protected void refreshPolicy() {

        try {
            // The policy file may have been modified to adjust
            // permissions, so we're reloading it when loading or
            // reloading a Context
            Policy policy = Policy.getPolicy();
            policy.refresh();
        } catch (AccessControlException e) {
            // Some policy files may restrict this, even for the core,
            // so this exception is ignored
        }

    }
}
