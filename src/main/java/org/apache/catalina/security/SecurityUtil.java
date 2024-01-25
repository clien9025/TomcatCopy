package org.apache.catalina.security;

import org.apache.catalina.Globals;

public class SecurityUtil {

    /**
     * 这个方法用于判断是否同时启用了安全管理器（SecurityManager）和包级别的保护。
     * 它首先检查 packageDefinitionEnabled 变量是否为 true，然后检查 Globals.IS_SECURITY_ENABLED（一个表示是否启用安全管理器的静态变量）是否为 true。
     * 如果两者都为 true，则返回 true，表示包级别的保护被启用。
     * 否则，返回 false
     */
    private static final boolean packageDefinitionEnabled =
            (System.getProperty("package.definition") == null && System.getProperty("package.access") == null) ? false :
                    true;


    /**
     * Return the <code>SecurityManager</code> only if Security is enabled AND package protection mechanism is enabled.
     *
     * 这个方法用于判断是否同时启用了安全管理器（SecurityManager）和包级别的保护
     * @return <code>true</code> if package level protection is enabled
     */
    public static boolean isPackageProtectionEnabled() {
        if (packageDefinitionEnabled && Globals.IS_SECURITY_ENABLED) {
            return true;
        }
        return false;
    }
}
