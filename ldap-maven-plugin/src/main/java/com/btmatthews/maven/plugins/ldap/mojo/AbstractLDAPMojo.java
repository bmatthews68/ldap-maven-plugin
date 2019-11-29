/*
 * Copyright 2008-2016 Brian Thomas Matthews
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

import com.btmatthews.maven.plugins.ldap.FormatLogger;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPException;
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
public abstract class AbstractLDAPMojo extends AbstractMojo implements FormatLogger {
    /**
     * The default host name.
     */
    private static final String DEFAULT_HOST = "localhost";
    /**
     * The default port number for LDAP servers.
     */
    private static final int DEFAULT_PORT = 389;
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
    @Parameter(defaultValue = "5000")
    private int connectionTimeout = 5000;
    /**
     * The maximum number of connection attempts before failing.
     */
    @Parameter(defaultValue = "3")
    private int connectionRetries = 3;
    /**
     * To skip execution of the plugin
     */
    @Parameter(defaultValue = "false")
    private boolean skip;

    /**
     * Connect to the LDAP directory server. The connection attempt will be retried {@link #connectionRetries} times
     * and the connection time is set to {@link #connectionTimeout}.
     *
     * @return The connection object.
     * @throws MojoExecutionException If the connection to the LDAP directory server failed.
     */
    protected final LDAPConnection connect() throws MojoExecutionException {
        final LDAPConnection connection = new LDAPConnection();
        int i = 0;
        while (i < connectionRetries) {
            long start = System.currentTimeMillis();
            try {
                this.getLog().info("Attempting to connect to LDAP directory server (" + host + ":" + port + ")");
                connection.connect(host, port, connectionTimeout);
                break;
            } catch (final LDAPException e) {
                final String message = "Could not connect to LDAP directory server (" + host + ":" + port + ")";
                this.getLog().error(message, e);
                if (i++ < connectionRetries) {
                    long time = System.currentTimeMillis() - start;
                    if (time < connectionTimeout) {
                        try {
                            Thread.sleep(connectionTimeout - time);
                        } catch (final InterruptedException e1) {
                            throw new MojoExecutionException(message, e1);
                        }
                    }
                } else {
                    throw new MojoExecutionException(message, e);
                }
            }
        }
        try {
            connection.bind(authDn, passwd);
        } catch (final LDAPException e) {
            throw new MojoExecutionException("Could not bind to LDAP directory server as " + authDn, e);
        }
        return connection;
    }

    public boolean isSkip(){
        return this.skip;
    }

    /**
     * Write a information message to the Maven log.
     *
     * @param message The information message.
     */
    public void logInfo(final String message) {
        getLog().info(message);
    }

    /**
     * Write an error message to the Maven log.
     *
     * @param message The error message.
     */
    public void logError(final String message) {
        getLog().error(message);
    }

    /**
     * Write an error message and stack trace to the Maven log.
     *
     * @param message   The error message.
     * @param exception The exception containing the stack trace.
     */
    public void logError(final String message, final Throwable exception) {
        getLog().error(message, exception);
    }
}
