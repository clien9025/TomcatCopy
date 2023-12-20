package org.apache.naming;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ContextAccessController {

    // -------------------------------------------------------------- Variables

    /**
     * Catalina context names on which writing is not allowed.
     */
    private static final Map<Object,Object> readOnlyContexts = new ConcurrentHashMap<>();


    /**
     * Security tokens repository.
     */
    private static final Map<Object,Object> securityTokens = new ConcurrentHashMap<>();


    /**
     * Check a submitted security token.
     *
     * @param name Name of the Catalina context
     * @param token Submitted security token
     *
     * @return <code>true</code> if the submitted token is equal to the token
     *         in the repository or if no token is present in the repository.
     *         Otherwise, <code>false</code>
     */
    public static boolean checkSecurityToken
    (Object name, Object token) {
//        Object refToken = securityTokens.get(name);
//        return (refToken == null || refToken.equals(token));
        throw new UnsupportedOperationException();
    }
}
