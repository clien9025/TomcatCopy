/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.tomcat.util.net;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.security.DomainLoadStoreParameter;
import java.security.Key;
import java.security.KeyStore;
import java.security.cert.CRL;
import java.security.cert.CRLException;
import java.security.cert.CertPathParameters;
import java.security.cert.CertStore;
import java.security.cert.CertStoreParameters;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateFactory;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.CollectionCertStoreParameters;
import java.security.cert.PKIXBuilderParameters;
import java.security.cert.X509CertSelector;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.net.ssl.CertPathTrustManagerParameters;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.ManagerFactoryParameters;
import javax.net.ssl.SSLSessionContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509KeyManager;

import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.file.ConfigFileLoader;
//import org.apache.tomcat.util.net.jsse.JSSEKeyManager;
//import org.apache.tomcat.util.net.jsse.PEMFile;
import org.apache.tomcat.util.res.StringManager;
//import org.apache.tomcat.util.security.KeyStoreUtil;

/**
 * Common base class for {@link SSLUtil} implementations.
 */
public abstract class SSLUtilBase implements SSLUtil {

    private static final Log log = LogFactory.getLog(SSLUtilBase.class);
    private static final StringManager sm = StringManager.getManager(SSLUtilBase.class);

    public static final String DEFAULT_KEY_ALIAS = "tomcat";

    protected final SSLHostConfig sslHostConfig;
    protected final SSLHostConfigCertificate certificate;

    private final String[] enabledProtocols;
    private final String[] enabledCiphers;


    protected SSLUtilBase(SSLHostConfigCertificate certificate) {
        this(certificate, true);
    }


    protected SSLUtilBase(SSLHostConfigCertificate certificate, boolean warnTls13) {
//        this.certificate = certificate;
//        this.sslHostConfig = certificate.getSSLHostConfig();
//
//        // Calculate the enabled protocols
//        Set<String> configuredProtocols = sslHostConfig.getProtocols();
//        Set<String> implementedProtocols = getImplementedProtocols();
//        // If TLSv1.3 is not implemented and not explicitly requested we can
//        // ignore it. It is included in the defaults so it may be configured.
//        if (!implementedProtocols.contains(Constants.SSL_PROTO_TLSv1_3) &&
//                !sslHostConfig.isExplicitlyRequestedProtocol(Constants.SSL_PROTO_TLSv1_3)) {
//            configuredProtocols.remove(Constants.SSL_PROTO_TLSv1_3);
//        }
//        // Newer JREs are dropping support for SSLv2Hello. If it is not
//        // implemented and not explicitly requested we can ignore it. It is
//        // included in the defaults so it may be configured.
//        if (!implementedProtocols.contains(Constants.SSL_PROTO_SSLv2Hello) &&
//                !sslHostConfig.isExplicitlyRequestedProtocol(Constants.SSL_PROTO_SSLv2Hello)) {
//            configuredProtocols.remove(Constants.SSL_PROTO_SSLv2Hello);
//        }
//
//        List<String> enabledProtocols =
//                getEnabled("protocols", getLog(), warnTls13, configuredProtocols, implementedProtocols);
//        if (enabledProtocols.contains("SSLv3")) {
//            log.warn(sm.getString("sslUtilBase.ssl3"));
//        }
//        this.enabledProtocols = enabledProtocols.toArray(new String[0]);
//
//        if (enabledProtocols.contains(Constants.SSL_PROTO_TLSv1_3) &&
//                sslHostConfig.getCertificateVerification().isOptional() &&
//                !isTls13RenegAuthAvailable() && warnTls13) {
//            log.warn(sm.getString("sslUtilBase.tls13.auth"));
//        }
//
//        // Make TLS 1.3 renegotiation status visible further up the stack
//        sslHostConfig.setTls13RenegotiationAvailable(isTls13RenegAuthAvailable());
//
//        // Calculate the enabled ciphers
//        if (sslHostConfig.getCiphers().startsWith("PROFILE=")) {
//            // OpenSSL profiles
//            // TODO: sslHostConfig can query that with Panama, but skip for now
//            this.enabledCiphers = new String[0];
//        } else {
//            boolean warnOnSkip = !sslHostConfig.getCiphers().equals(SSLHostConfig.DEFAULT_TLS_CIPHERS);
//            List<String> configuredCiphers = sslHostConfig.getJsseCipherNames();
//            Set<String> implementedCiphers = getImplementedCiphers();
//            List<String> enabledCiphers =
//                    getEnabled("ciphers", getLog(), warnOnSkip, configuredCiphers, implementedCiphers);
//            this.enabledCiphers = enabledCiphers.toArray(new String[0]);
//        }
        throw new UnsupportedOperationException();
    }


}
