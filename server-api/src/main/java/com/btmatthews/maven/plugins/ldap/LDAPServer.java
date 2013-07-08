/*
 * Copyright 2012-2013 Brian Thomas Matthews
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

import java.io.File;
import java.net.URL;

import com.btmatthews.utils.monitor.AbstractServer;
import com.btmatthews.utils.monitor.Logger;
import com.btmatthews.utils.monitor.Server;

/**
 * @author <a href="mailto:brian@btmatthews.com">Brian Matthews</a>
 * @since 1.1.1
 */
public interface LDAPServer extends Server {

    public final static String ROOT = "root";

    public final static String AUTH_DN = "authDn";

    public final static String PASSWD = "passwd";

    public final static String WORK_DIR = "workingDirectory";

    public final static String LDIF_FILE = "ldifFile";

    public final static String LDAP_PORT = "ldapPort";

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
