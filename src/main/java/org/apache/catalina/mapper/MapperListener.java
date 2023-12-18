package org.apache.catalina.mapper;

import org.apache.catalina.Service;
import org.apache.tomcat.util.res.StringManager;

public class MapperListener {



    // ----------------------------------------------------- Instance Variables
    /**
     * Associated mapper.
     */
    private final Mapper mapper;

    /**
     * Associated service
     */
    private final Service service;

//
//    /**
//     * The string manager for this package.
//     */
//    private static final StringManager sm = StringManager.getManager(Constants.Package);
//
//    /**
//     * The domain (effectively the engine) this mapper is associated with
//     */
//    private final String domain = null;

    /**
     * Create mapper listener.
     *
     * @param service The service this listener is associated with
     */
    public MapperListener(Service service) {
        this.service = service;
        this.mapper = service.getMapper();
    }
}
