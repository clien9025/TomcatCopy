/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.catalina.authenticator;

import java.io.IOException;
import java.security.Principal;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;

import jakarta.security.auth.message.AuthException;
import jakarta.security.auth.message.AuthStatus;
import jakarta.security.auth.message.MessageInfo;
import jakarta.security.auth.message.config.AuthConfigFactory;
import jakarta.security.auth.message.config.AuthConfigProvider;
import jakarta.security.auth.message.config.RegistrationListener;
import jakarta.security.auth.message.config.ServerAuthConfig;
import jakarta.security.auth.message.config.ServerAuthContext;
import jakarta.servlet.DispatcherType;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.catalina.Authenticator;
import org.apache.catalina.Contained;
import org.apache.catalina.Container;
import org.apache.catalina.Context;
import org.apache.catalina.Globals;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.Realm;
import org.apache.catalina.Session;
import org.apache.catalina.TomcatPrincipal;
import org.apache.catalina.Valve;
import org.apache.catalina.authenticator.jaspic.MessageInfoImpl;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.filters.CorsFilter;
import org.apache.catalina.realm.GenericPrincipal;
import org.apache.catalina.valves.ValveBase;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.ExceptionUtils;
import org.apache.tomcat.util.descriptor.web.FilterDef;
import org.apache.tomcat.util.descriptor.web.FilterMap;
import org.apache.tomcat.util.descriptor.web.LoginConfig;
import org.apache.tomcat.util.descriptor.web.SecurityConstraint;
import org.apache.tomcat.util.http.FastHttpDateFormat;
import org.apache.tomcat.util.res.StringManager;

/**
 * Basic implementation of the <b>Valve</b> interface that enforces the <code>&lt;security-constraint&gt;</code>
 * elements in the web application deployment descriptor. This functionality is implemented as a Valve so that it can be
 * omitted in environments that do not require these features. Individual implementations of each supported
 * authentication method can subclass this base class as required.
 * <p>
 * <b>USAGE CONSTRAINT</b>: When this class is utilized, the Context to which it is attached (or a parent Container in a
 * hierarchy) must have an associated Realm that can be used for authenticating users and enumerating the roles to which
 * they have been assigned.
 * <p>
 * <b>USAGE CONSTRAINT</b>: This Valve is only useful when processing HTTP requests. Requests of any other type will
 * simply be passed through.
 *
 * @author Craig R. McClanahan
 */
public abstract class AuthenticatorBase extends ValveBase implements Authenticator, RegistrationListener {

    private final Log log = LogFactory.getLog(AuthenticatorBase.class); // must not be static

    /**
     * "Expires" header always set to Date(1), so generate once only
     */
    private static final String DATE_ONE = FastHttpDateFormat.formatDate(1);

    /**
     * The string manager for this package.
     */
    protected static final StringManager sm = StringManager.getManager(AuthenticatorBase.class);

    /**
     * Authentication header
     */
    protected static final String AUTH_HEADER_NAME = "WWW-Authenticate";

    /**
     * Default authentication realm name.
     */
    protected static final String REALM_NAME = "Authentication required";
}
