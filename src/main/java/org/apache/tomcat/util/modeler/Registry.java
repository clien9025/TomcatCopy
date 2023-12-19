package org.apache.tomcat.util.modeler;

public class Registry {


    public static synchronized void disableRegistry() {
//        if (registry == null) {
//            registry = new NoDescriptorRegistry();
//        } else if (!(registry instanceof NoDescriptorRegistry)) {
//            log.warn(sm.getString("registry.noDisable"));
//        }
        throw new UnsupportedOperationException();
    }

}
