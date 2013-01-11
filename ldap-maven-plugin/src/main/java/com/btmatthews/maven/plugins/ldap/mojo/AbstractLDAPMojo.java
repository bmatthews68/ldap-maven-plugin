/*
 * Copyright 2008-2012 Brian Thomas Matthews
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.btmatthews.maven.plugins.ldap.mojo;

import netscape.ldap.LDAPConnection;
import netscape.ldap.LDAPException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * This is the abstract base class for all Mojos in this the ldap-maven-plugin
 * plugin. It defines the properties for the LDAP directory server connection
 * and provides a method to connect to the LDAP directory server.
 *
 * @author <a href="mailto:brian@btmatthews.com">Brian Matthews</a>
 * @since 1.0.0
 */
public abstract class AbstractLDAPMojo extends AbstractMojo {
    /**
     * The default LDAP protocol version.
     */
    private static final int DEFAULT_VERSION = 3;

    /**
     * The default host name.
     */
    private static final String DEFAULT_HOST = "localhost";

    /**
     * The default port number for LDAP servers.
     */
    private static final int DEFAULT_PORT = 389;

    /**
     * The LDAP protocol version. Defaults to 3.
     */
    @Parameter(defaultValue = "3")
    private int version = AbstractLDAPMojo.DEFAULT_VERSION;

    /**
     * The host name of the LDAP directory server. Defaults to localhost.
     */
    @Parameter(defaultValue = "localhost")
    private String host = AbstractLDAPMojo.DEFAULT_HOST;

    /**
     * The port number of the LDAP directory server. Defaults to 389.
     */
    @Parameter(defaultValue = "389")
    private int port = AbstractLDAPMojo.DEFAULT_PORT;

    /**
     * The distinguished name used if authentication is required.
     */
    @Parameter(required = true)
    private String authDn;

    /**
     * The password used if authentication is required.
     */
    @Parameter(required = true)
    private String passwd;

    /**
     * The connection timeout.
     */
    @Parameter(defaultValue = "5")
    private int connectionTimeout = 5;

    /**
     * The maximum number of connection attempts before failing.
     */
    @Parameter(defaultValue = "3")
    private int connectionRetries = 3;

    /**
     * Connect to the LDAP directory server. The connection attempt will be retried {@link #connectionRetries} times
     * and the connection time is set to {@link #connectionTimeout}.
     *
     * @return The connection object.
     * @throws MojoExecutionException If the connection to the LDAP directory server failed.
     */
    protected final LDAPConnection connect() throws MojoExecutionException {
        String lastMessage = null;
        LDAPException lastError = null;
        final LDAPConnection connection = new LDAPConnection();
        connection.setConnectTimeout(connectionTimeout);
        int i = 0;
        while (i < connectionRetries) {
            try {
                this.getLog().info("Attempting to connect ot LDAP Server (" + host + ":" + port + ")");
                connection.connect(version, host, port, authDn, passwd);
                break;
            } catch (final LDAPException e) {
                i++;
                lastError = e;
                lastMessage = "Could not connect to LDAP Server (" + host + ":" + port + ")";
                this.getLog().error(lastMessage, lastError);
            }
        }
        if (i >= connectionRetries) {
            throw new MojoExecutionException(lastMessage, lastError);
        }
        return connection;
    }
}
