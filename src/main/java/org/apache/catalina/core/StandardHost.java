package org.apache.catalina.core;

import org.apache.catalina.Host;

import java.util.Locale;

/**
 * Standard implementation of the <b>Host</b> interface. Each child container must be a Context implementation to
 * process the requests directed to a particular web application.
 *
 * @author Craig R. McClanahan
 * @author Remy Maucherat
 */
public class StandardHost extends ContainerBase implements Host {



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

}
