/**
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package jakarta.security.auth.message.config;

import java.security.AccessController;
import java.security.Permission;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.security.Security;
import java.security.SecurityPermission;
import java.util.Map;

import jakarta.security.auth.message.module.ServerAuthModule;

public abstract class AuthConfigFactory {

    public static final String DEFAULT_FACTORY_SECURITY_PROPERTY = "authconfigprovider.factory";
    public static final String GET_FACTORY_PERMISSION_NAME = "getProperty.authconfigprovider.factory";
    public static final String SET_FACTORY_PERMISSION_NAME = "setProperty.authconfigprovider.factory";
    public static final String PROVIDER_REGISTRATION_PERMISSION_NAME = "setProperty.authconfigfactory.provider";

    /**
     * @deprecated Following JEP 411
     */
    @Deprecated(forRemoval = true)
    public static final SecurityPermission getFactorySecurityPermission =
            new SecurityPermission(GET_FACTORY_PERMISSION_NAME);

    /**
     * @deprecated Following JEP 411
     */
    @Deprecated(forRemoval = true)
    public static final SecurityPermission setFactorySecurityPermission =
            new SecurityPermission(SET_FACTORY_PERMISSION_NAME);

    /**
     * @deprecated Following JEP 411
     */
    @Deprecated(forRemoval = true)
    public static final SecurityPermission providerRegistrationSecurityPermission =
            new SecurityPermission(PROVIDER_REGISTRATION_PERMISSION_NAME);

    private static final String DEFAULT_JASPI_AUTHCONFIGFACTORYIMPL =
            "org.apache.catalina.authenticator.jaspic.AuthConfigFactoryImpl";

    private static volatile AuthConfigFactory factory;

    public AuthConfigFactory() {
    }

}
