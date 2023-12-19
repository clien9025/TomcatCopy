package org.apache.catalina.security;

/**
 * Static class used to preload java classes when using the Java SecurityManager so that the defineClassInPackage
 * RuntimePermission does not trigger an AccessControlException.
 *
 * @author Glenn L. Nielsen
 */
public final class SecurityClassLoad {


    public static void securityClassLoad(ClassLoader loader) throws Exception {
        securityClassLoad(loader, true);
    }

    static void securityClassLoad(ClassLoader loader, boolean requireSecurityManager) throws Exception {

//        if (requireSecurityManager && System.getSecurityManager() == null) {
//            return;
//        }
//
//        loadCorePackage(loader);
//        loadCoyotePackage(loader);
//        loadLoaderPackage(loader);
//        loadRealmPackage(loader);
//        loadServletsPackage(loader);
//        loadSessionPackage(loader);
//        loadUtilPackage(loader);
//        loadJakartaPackage(loader);
//        loadConnectorPackage(loader);
//        loadTomcatPackage(loader);
        throw new UnsupportedOperationException();
    }
}
