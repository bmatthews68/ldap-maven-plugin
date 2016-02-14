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

package com.btmatthews.maven.plugins.ldap;

import com.btmatthews.utils.monitor.AbstractServer;
import com.btmatthews.utils.monitor.Logger;

import java.io.File;

/**
 * Abstract base class for {@link LDAPServer} implementations.
 *
 * @author <a href="mailto:brian@btmatthews.com">Brian Matthews</a>
 * @since 1.1.1
 */
public abstract class AbstractLDAPServer extends AbstractServer implements LDAPServer {

    /**
     * The root DN of the LDAP directory.
     */
    private String root;
    /**
     * The distinguished name of the admin account.
     */
    private String authDn;
    /**
     * The password for the admin account.
     */
    private String passwd;
    /**
     * The working directory used by the LDAP directory service to store directory data.
     */
    private File workingDirectory;
    /**
     * The LDIF file used to seed the LDAP directory.
     */
    private File ldifFile;
    /**
     * The TCP port on which the server is listening for LDAP traffic.
     */
    private int serverPort;

    /**
     * Used to configure the root DN of the LDAP directory, the working directory used by the directory service to
     * store the directory data, the LDIF file used to seed the directory or the TCP port number on which the server
     * will listening for LDAP traffic.
     *
     * @param name   The name of the property to configure.
     * @param value  The value of the property being configured.
     * @param logger Used to log error and information messages.
     */
    @Override
    public void configure(final String name, final Object value, final Logger logger) {
        if (ROOT.equals(name)) {
            if (value instanceof String) {
                root = (String) value;
                logger.logInfo("Configured root DN for directory server: " + root);
            }
        } else if (AUTH_DN.equals(name)) {
            if (value instanceof String) {
                authDn = (String) value;
                logger.logInfo("Configured admin identity for directory server: " + authDn);
            }
        } else if (PASSWD.equals(name)) {
            if (value instanceof String) {
                passwd = (String) value;
                logger.logInfo("Configured admin credentials for directory server: " + passwd);
            }
        } else if (WORK_DIR.equals(name)) {
            if (value instanceof File) {
                workingDirectory = (File) value;
                logger.logInfo("Configured working directory for directory server: " + workingDirectory);
            }
        } else if (LDIF_FILE.equals(name)) {
            if (value instanceof File) {
                ldifFile = (File) value;
                logger.logInfo("Configured LDIF seed data source for directory server: " + ldifFile);
            }
        } else if (LDAP_PORT.equals(name)) {
            if (value instanceof Integer) {
                serverPort = (Integer) value;
                logger.logInfo("Configured TCP port for directory server: " + serverPort);
            }
        }
    }

    /**
     * Get the configured directory root.
     *
     * @return The directory root.
     */
    public final String getRoot() {
        return root;
    }

    /**
     * Get the distinguished name of the admin account.
     *
     * @return The distinguished name of the admin account.
     */
    public final String getAuthDn() {
        return authDn;
    }

    /**
     * Get the password for the admin account.
     *
     * @return The password for the admin account.
     */
    public final String getPasswd() {
        return passwd;
    }

    /**
     * Get the working directory used to create temporary files.
     *
     * @return The working directory.
     */
    public final File getWorkingDirectory() {
        return workingDirectory;
    }

    /**
     * Get the LDIF file that contains the data used to seed the directory.
     *
     * @return The LDIF file.
     */
    public final File getLdifFile() {
        return ldifFile;
    }

    /**
     * Get the port on which the LDAP server is listening.
     *
     * @return The port.
     */
    public final int getServerPort() {
        return serverPort;
    }
}
