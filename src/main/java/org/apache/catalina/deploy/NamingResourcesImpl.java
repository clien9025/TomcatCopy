package org.apache.catalina.deploy;

import org.apache.catalina.*;
import org.apache.catalina.util.LifecycleMBeanBase;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.descriptor.web.*;
import org.apache.tomcat.util.res.StringManager;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class NamingResourcesImpl extends LifecycleMBeanBase implements Serializable, NamingResources {



    private static final long serialVersionUID = 1L;

    private static final Log log = LogFactory.getLog(NamingResourcesImpl.class);

    private static final StringManager sm = StringManager.getManager(NamingResourcesImpl.class);

    private volatile boolean resourceRequireExplicitRegistration = false;

    // ----------------------------------------------------------- Constructors


    /**
     * Create a new NamingResources instance.
     */
    public NamingResourcesImpl() {
        // NOOP
    }


    // ----------------------------------------------------- Instance Variables


    /**
     * Associated container object.
     */
    private Object container = null;


    /**
     * Set of naming entries, keyed by name.
     */
    private final Set<String> entries = new HashSet<>();


    /**
     * The EJB resource references for this web application, keyed by name.
     */
    private final Map<String, ContextEjb> ejbs = new HashMap<>();


    /**
     * The environment entries for this web application, keyed by name.
     */
    private final Map<String,ContextEnvironment> envs = new HashMap<>();


    /**
     * The local EJB resource references for this web application, keyed by name.
     */
    private final Map<String,ContextLocalEjb> localEjbs = new HashMap<>();


    /**
     * The message destination references for this web application, keyed by name.
     */
    private final Map<String,MessageDestinationRef> mdrs = new HashMap<>();


    /**
     * The resource environment references for this web application, keyed by name.
     */
    private final HashMap<String,ContextResourceEnvRef> resourceEnvRefs = new HashMap<>();


    /**
     * The resource references for this web application, keyed by name.
     */
    private final HashMap<String,ContextResource> resources = new HashMap<>();


    /**
     * The resource links for this web application, keyed by name.
     */
    private final HashMap<String,ContextResourceLink> resourceLinks = new HashMap<>();


    /**
     * The web service references for this web application, keyed by name.
     */
    private final HashMap<String,ContextService> services = new HashMap<>();


    /**
     * The transaction for this webapp.
     */
    private ContextTransaction transaction = null;


    /**
     * The property change support for this component.
     */
    protected final PropertyChangeSupport support = new PropertyChangeSupport(this);




    // ------------------------------------------------------------- Properties


    /**
     * @return the container with which the naming resources are associated.
     */
    @Override
    public Object getContainer() {
        return container;
    }


    /**
     * Set the container with which the naming resources are associated.
     *
     * @param container the associated with the resources
     */
    public void setContainer(Object container) {
        this.container = container;
    }


    /**
     * Set the transaction object.
     *
     * @param transaction the transaction descriptor
     */
    public void setTransaction(ContextTransaction transaction) {
        this.transaction = transaction;
    }


    /**
     * @return the transaction object.
     */
    public ContextTransaction getTransaction() {
        return transaction;
    }


/* ------------------------------------------------------------------------------------------------------- */

    @Override
    protected String getDomainInternal() {
        // Use the same domain as our associated container if we have one
        Object c = getContainer();

        if (c instanceof JmxEnabled) {
            return ((JmxEnabled) c).getDomain();
        }

        return null;
    }

    @Override
    protected String getObjectNameKeyProperties() {
        Object c = getContainer();
        if (c instanceof Container) {
            return "type=NamingResources" + ((Container) c).getMBeanKeyProperties();
        }
        // Server or just unknown
        return "type=NamingResources";
    }

    @Override
    protected void startInternal() throws LifecycleException {
//        fireLifecycleEvent(CONFIGURE_START_EVENT, null);
//        setState(LifecycleState.STARTING);
        throw new UnsupportedOperationException();
    }


    @Override
    public void addLifecycleListener(LifecycleListener listener) {
        throw new UnsupportedOperationException();
    }

    @Override
    public LifecycleListener[] findLifecycleListeners() {
//        return new LifecycleListener[0];
        throw new UnsupportedOperationException();
    }

    @Override
    public void removeLifecycleListener(LifecycleListener listener) {
        throw new UnsupportedOperationException();
    }


    @Override
    public LifecycleState getState() {
//        return null;
        throw new UnsupportedOperationException();
    }

    @Override
    public String getStateName() {
//        return null;
        throw new UnsupportedOperationException();
    }

    @Override
    public void addEnvironment(ContextEnvironment ce) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void removeEnvironment(String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addResource(ContextResource cr) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void removeResource(String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addResourceLink(ContextResourceLink crl) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void removeResourceLink(String name) {
        throw new UnsupportedOperationException();
    }

}
