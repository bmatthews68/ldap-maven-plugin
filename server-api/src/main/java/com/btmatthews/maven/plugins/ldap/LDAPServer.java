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

import com.btmatthews.utils.monitor.Server;

import java.io.File;

/**
 * The interface that must be implemented by LDAP servers.
 *
 * @author <a href="mailto:brian@btmatthews.com">Brian Matthews</a>
 * @since 1.1.1
 */
public interface LDAPServer extends Server {

    /**
     * Name of the parameter that specifies the root distinguished name.
     */
    String ROOT = "root";
    /**
     * The name of the parameter that specifies the identity used to authenticate connections.
     */
    String AUTH_DN = "authDn";
    /**
     * The name of the parameter that specifies the credentials used for authentication.
     */
    String PASSWD = "passwd";
    /**
     * The name of the parameter that specifies a working/scratch directory that can be used by the plugin.
     */
    String WORK_DIR = "workingDirectory";
    /**
     * The name of the parameter that specifies an LDIF file containing seed data.
     */
    String LDIF_FILE = "ldifFile";
    /**
     * The name of the parameter that specifies the port on which the LDAP service will listen for traffic.
     */
    String LDAP_PORT = "ldapPort";

    /**
     * Get the configured directory root.
     *
     * @return The directory root.
     */
    String getRoot();

    /**
     * Get the distinguished name of the admin account.
     *
     * @return The distinguished name of the admin account.
     */
    String getAuthDn();

    /**
     * Get the password for the admin account.
     *
     * @return The password for the admin account.
     */
    String getPasswd();

    /**
     * Get the working directory used to create temporary files.
     *
     * @return The working directory.
     */
    File getWorkingDirectory();

    /**
     * Get the LDIF file that contains the data used to seed the directory.
     *
     * @return The LDIF file.
     */
    File getLdifFile();

    /**
     * Get the port on which the LDAP server is listening.
     *
     * @return The port.
     */
    int getServerPort();
}
