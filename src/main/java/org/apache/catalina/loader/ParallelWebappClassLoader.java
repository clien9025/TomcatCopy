package org.apache.catalina.loader;

import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.compat.JreCompat;

public class ParallelWebappClassLoader extends WebappClassLoaderBase {



    private static final Log log = LogFactory.getLog(ParallelWebappClassLoader.class);

    static {
        if (!JreCompat.isGraalAvailable()) {
            if (!registerAsParallelCapable()) {
                log.warn(sm.getString("webappClassLoaderParallel.registrationFailed"));
            }
        }
    }

    public ParallelWebappClassLoader() {
        super();
    }


    public ParallelWebappClassLoader(ClassLoader parent) {
        super(parent);
    }
}
