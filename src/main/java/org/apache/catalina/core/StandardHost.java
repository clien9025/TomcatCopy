package org.apache.catalina.core;

import org.apache.catalina.Host;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

import java.util.Locale;

/**
 * Standard implementation of the <b>Host</b> interface. Each child container must be a Context implementation to
 * process the requests directed to a particular web application.
 *
 * @author Craig R. McClanahan
 * @author Remy Maucherat
 */
public class StandardHost extends ContainerBase implements Host {
    private static final Log log = LogFactory.getLog(StandardHost.class);



    // ----------------------------------------------------------- Constructors


    /**
     * Create a new StandardHost component with the default basic Valve.
     */
    public StandardHost() {

        super();
        pipeline.setBasic(new StandardHostValve());

    }


    // ----------------------------------------------------- Instance Variables

    /**
     * The auto deploy flag for this Host.
     */
    private boolean autoDeploy = true;



    // ------------------------------------------------------------- Properties


    /**
     * Set the canonical, fully qualified, name of the virtual host this Container represents.
     *
     * @param name Virtual host name
     *
     * @exception IllegalArgumentException if name is null
     */
    @Override
    public void setName(String name) {

        if (name == null) {
            throw new IllegalArgumentException(sm.getString("standardHost.nullName"));
        }

        name = name.toLowerCase(Locale.ENGLISH); // Internally all names are lower case

        String oldName = this.name;
        this.name = name;
        support.firePropertyChange("name", oldName, this.name);

    }



    /**
     * @return the canonical, fully qualified, name of the virtual host this Container represents.
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * Set the auto deploy flag value for this host.
     *
     * @param autoDeploy The new auto deploy flag
     */
    @Override
    public void setAutoDeploy(boolean autoDeploy) {

        boolean oldAutoDeploy = this.autoDeploy;
        this.autoDeploy = autoDeploy;
        support.firePropertyChange("autoDeploy", oldAutoDeploy, this.autoDeploy);

    }


    @Override
    protected String getObjectNameKeyProperties() {

        StringBuilder keyProperties = new StringBuilder("type=Host");
        keyProperties.append(getMBeanKeyProperties());

        return keyProperties.toString();
    }

}
