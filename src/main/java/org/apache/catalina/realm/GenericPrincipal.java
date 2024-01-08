package org.apache.catalina.realm;

import org.apache.catalina.TomcatPrincipal;
import org.ietf.jgss.GSSCredential;

import javax.security.auth.login.LoginContext;
import java.io.Serializable;
import java.security.Principal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Generic implementation of <strong>java.security.Principal</strong> that is available for use by <code>Realm</code>
 * implementations.
 *
 * @author Craig R. McClanahan
 */
public class GenericPrincipal implements TomcatPrincipal, Serializable {

    private static final long serialVersionUID = 1L;


    // ----------------------------------------------------------- Constructors

    /**
     * Construct a new Principal, associated with the specified Realm, for the specified username, with no roles.
     *
     * @param name The username of the user represented by this Principal
     */
    public GenericPrincipal(String name) {
        this(name, null);
    }

    /**
     * Construct a new Principal, associated with the specified Realm, for the specified username, with the specified
     * role names (as Strings).
     *
     * @param name  The username of the user represented by this Principal
     * @param roles List of roles (must be Strings) possessed by this user
     */
    public GenericPrincipal(String name, List<String> roles) {
        this(name, roles, null);
    }

    /**
     * Construct a new Principal, associated with the specified Realm, for the specified username, with the specified
     * role names (as Strings).
     *
     * @param name     The username of the user represented by this Principal
     * @param password Unused
     * @param roles    List of roles (must be Strings) possessed by this user
     *
     * @deprecated This method will be removed in Tomcat 11 onwards
     */
    @Deprecated
    public GenericPrincipal(String name, String password, List<String> roles) {
        this(name, roles, null);
    }

    /**
     * Construct a new Principal, associated with the specified Realm, for the specified username, with the specified
     * role names (as Strings).
     *
     * @param name          The username of the user represented by this Principal
     * @param roles         List of roles (must be Strings) possessed by this user
     * @param userPrincipal - the principal to be returned from the request getUserPrincipal call if not null; if null,
     *                          this will be returned
     */
    public GenericPrincipal(String name, List<String> roles, Principal userPrincipal) {
        this(name, roles, userPrincipal, null);
    }

    /**
     * Construct a new Principal, associated with the specified Realm, for the specified username, with the specified
     * role names (as Strings).
     *
     * @param name          The username of the user represented by this Principal
     * @param password      Unused
     * @param roles         List of roles (must be Strings) possessed by this user
     * @param userPrincipal - the principal to be returned from the request getUserPrincipal call if not null; if null,
     *                          this will be returned
     *
     * @deprecated This method will be removed in Tomcat 11 onwards
     */
    @Deprecated
    public GenericPrincipal(String name, String password, List<String> roles, Principal userPrincipal) {
        this(name, roles, userPrincipal, null);
    }

    /**
     * Construct a new Principal, associated with the specified Realm, for the specified username, with the specified
     * role names (as Strings).
     *
     * @param name          The username of the user represented by this Principal
     * @param roles         List of roles (must be Strings) possessed by this user
     * @param userPrincipal - the principal to be returned from the request getUserPrincipal call if not null; if null,
     *                          this will be returned
     * @param loginContext  - If provided, this will be used to log out the user at the appropriate time
     */
    public GenericPrincipal(String name, List<String> roles, Principal userPrincipal, LoginContext loginContext) {
        this(name, roles, userPrincipal, loginContext, null, null);
    }

    /**
     * Construct a new Principal, associated with the specified Realm, for the specified username, with the specified
     * role names (as Strings).
     *
     * @param name          The username of the user represented by this Principal
     * @param password      Unused
     * @param roles         List of roles (must be Strings) possessed by this user
     * @param userPrincipal - the principal to be returned from the request getUserPrincipal call if not null; if null,
     *                          this will be returned
     * @param loginContext  - If provided, this will be used to log out the user at the appropriate time
     *
     * @deprecated This method will be removed in Tomcat 11 onwards
     */
    @Deprecated
    public GenericPrincipal(String name, String password, List<String> roles, Principal userPrincipal,
                            LoginContext loginContext) {
        this(name, roles, userPrincipal, loginContext, null, null);
    }

    /**
     * Construct a new Principal, associated with the specified Realm, for the specified username, with the specified
     * role names (as Strings).
     *
     * @param name          The username of the user represented by this Principal
     * @param roles         List of roles (must be Strings) possessed by this user
     * @param userPrincipal - the principal to be returned from the request getUserPrincipal call if not null; if null,
     *                          this will be returned
     * @param loginContext  - If provided, this will be used to log out the user at the appropriate time
     * @param gssCredential - If provided, the user's delegated credentials
     * @param attributes    - If provided, additional attributes associated with this Principal
     */
    public GenericPrincipal(String name, List<String> roles, Principal userPrincipal, LoginContext loginContext,
                            GSSCredential gssCredential, Map<String,Object> attributes) {
//        super();
//        this.name = name;
//        this.userPrincipal = userPrincipal;
//        if (roles == null) {
//            this.roles = new String[0];
//        } else {
//            this.roles = roles.toArray(new String[0]);
//            if (this.roles.length > 1) {
//                Arrays.sort(this.roles);
//            }
//        }
//        this.loginContext = loginContext;
//        this.gssCredential = gssCredential;
//        this.attributes = attributes != null ? Collections.unmodifiableMap(attributes) : null;
        throw new UnsupportedOperationException();
    }


    /**
     * Construct a new Principal, associated with the specified Realm, for the specified username, with the specified
     * role names (as Strings).
     *
     * @param name          The username of the user represented by this Principal
     * @param password      Unused
     * @param roles         List of roles (must be Strings) possessed by this user
     * @param userPrincipal - the principal to be returned from the request getUserPrincipal call if not null; if null,
     *                          this will be returned
     * @param loginContext  - If provided, this will be used to log out the user at the appropriate time
     * @param gssCredential - If provided, the user's delegated credentials
     *
     * @deprecated This method will be removed in Tomcat 11 onwards
     */
    @Deprecated
    public GenericPrincipal(String name, String password, List<String> roles, Principal userPrincipal,
                            LoginContext loginContext, GSSCredential gssCredential) {
        this(name, roles, userPrincipal, loginContext, gssCredential, null);
    }


    // -------------------------------------------------------------- Properties

    /**
     * The username of the user represented by this Principal.
     */
    protected final String name;

    @Override
    public String getName() {
        return this.name;
    }

}
