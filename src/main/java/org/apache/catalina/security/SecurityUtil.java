package org.apache.catalina.security;

import org.apache.catalina.Globals;

public class SecurityUtil {

    private static final boolean packageDefinitionEnabled =
            (System.getProperty("package.definition") == null && System.getProperty("package.access") == null) ? false :
                    true;


    /**
     * Return the <code>SecurityManager</code> only if Security is enabled AND package protection mechanism is enabled.
     *
     * @return <code>true</code> if package level protection is enabled
     */
    public static boolean isPackageProtectionEnabled() {
        if (packageDefinitionEnabled && Globals.IS_SECURITY_ENABLED) {
            return true;
        }
        return false;
    }
}
