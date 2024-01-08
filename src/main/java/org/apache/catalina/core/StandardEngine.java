package org.apache.catalina.core;

import org.apache.catalina.*;

import java.util.Locale;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Standard implementation of the <b>Engine</b> interface. Each child container must be a Host implementation to process
 * the specific fully qualified host name of that virtual host.
 *
 * @author Craig R. McClanahan
 */
public class StandardEngine extends ContainerBase implements Engine {

    // ----------------------------------------------------------- Constructors

    /**
     * Create a new StandardEngine component with the default basic Valve.
     */
    public StandardEngine() {
        pipeline.setBasic(new StandardEngineValve());
        // By default, the engine will hold the reloading thread
        backgroundProcessorDelay = 10;
    }

    // ----------------------------------------------------- Instance Variables

    /**
     * Host name to use when no server host, or an unknown host, is specified in the request.
     */
    private String defaultHost = null;


    /**
     * The <code>Service</code> that owns this Engine, if any.
     */
    private Service service = null;

//
//    /**
//     * The JVM Route ID for this Tomcat instance. All Route ID's must be unique across the cluster.
//     */
//    private String jvmRouteId;
//
//    /**
//     * Default access log to use for request/response pairs where we can't ID the intended host and context.
//     */
//    private final AtomicReference<AccessLog> defaultAccessLog = new AtomicReference<>();

    // ------------------------------------------------------------- Properties

    /**
     * Set the default host.
     *
     * @param host The new default host
     */
    @Override
    public void setDefaultHost(String host) {

        String oldDefaultHost = this.defaultHost;
        if (host == null) {
            this.defaultHost = null;
        } else {
            this.defaultHost = host.toLowerCase(Locale.ENGLISH);
        }
        if (getState().isAvailable()) {
//            service.getMapper().setDefaultHostName(host);
            throw new UnsupportedOperationException();
        }
        support.firePropertyChange("defaultHost", oldDefaultHost, this.defaultHost);
    }

    /**
     * Set the <code>Service</code> with which we are associated (if any).
     *
     * @param service The service that owns this Engine
     */
    @Override
    public void setService(Service service) {
        this.service = service;
    }


    // --------------------------------------------------------- Public Methods


    /**
     * Add a child Container, only if the proposed child is an implementation of Host.
     *
     * @param child Child container to be added
     */
    @Override
    public void addChild(Container child) {

        if (!(child instanceof Host)) {
            throw new IllegalArgumentException(sm.getString("standardEngine.notHost"));
        }
        super.addChild(child);

    }


}
