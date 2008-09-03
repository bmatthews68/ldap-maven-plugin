/*
 * Copyright 2008 Brian Thomas Matthews
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

package com.btmatthews.maven.plugins.ldap.mojos;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

import netscape.ldap.LDAPConnection;
import netscape.ldap.LDAPException;

/**
 * This is the abstract base class for all Mojos in this the ldap-maven-plugin
 * plugin. It defines the properties for the LDAP directory server connection
 * and provides a method to connect to the LDAP directory server.
 * 
 * @author <a href="mailto:brian@btmatthews.com">Brian Matthews</a>
 * @version 1.0
 */
public abstract class AbstractLDAPMojo
    extends AbstractMojo
{
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
     * 
     * @parameter default-value="3"
     */
    private int version = AbstractLDAPMojo.DEFAULT_VERSION;

    /**
     * The host name of the LDAP server. Defaults to localhost.
     * 
     * @parameter default-value="localhost"
     */
    private String host = AbstractLDAPMojo.DEFAULT_HOST;

    /**
     * The port number of the LDAP server. Defaults to 389.
     * 
     * @parameter default-value="389"
     */
    private int port = AbstractLDAPMojo.DEFAULT_PORT;

    /**
     * The distinguished name used if authentication is required.
     * 
     * @parameter
     */
    private String authDn;

    /**
     * The password used if authentication is required.
     * 
     * @parameter
     */
    private String passwd;

    /**
     * The default constructor.
     */
    public AbstractLDAPMojo()
    {
    }

    /**
     * Connect to the LDAP directory server.
     * 
     * @return The connection object.
     * @throws MojoExecutionException
     *             If the connection to the LDAP directory server failed.
     */
    protected final LDAPConnection connect()
        throws MojoExecutionException
    {
        try
        {
            final LDAPConnection connection = new LDAPConnection();
            connection.connect(this.version, this.host, this.port, this.authDn,
                this.passwd);
            return connection;
        }
        catch (LDAPException e)
        {
            final String message = "Could not connect to LDAP Server ("
                + this.host + ":" + this.port + ")";
            this.getLog().error(message, e);
            throw new MojoExecutionException(message, e);
        }
    }
}
