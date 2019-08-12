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


import com.btmatthews.maven.plugins.ldap.LDAPServer;
import com.btmatthews.utils.monitor.mojo.AbstractRunMojo;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * This Mojo implements the run goal which launches an embedded LDAP
 * directory server.
 *
 * @author <a href="mailto:brian.matthews@btmatthews.com">Brian Matthews</a>
 * @since 1.1.0
 */
@Mojo(
        name = "run",
        defaultPhase = LifecyclePhase.PRE_INTEGRATION_TEST,
        configurator = "include-server-dependencies")
public final class RunLDAPMojo extends AbstractRunMojo {

    /**
     * The server can be one of:
     * <ul>
     * <li>apacheds</li> - Use ApacheDS 1.5.5
     * <li>unboundid</li> - Use UnboundID 3.1.0
     * <li>dependency-[serverType]</li> - Use custom server included as plugin dependency and identified by <b>serverType</b>
     * <li>dependency-[serverType]-[groupId]:[artifactId]:[version]</li> - Use custom server specified by the GAV coordinates and identified by <b>serverType</b>
     * </ul>
     */
    @Parameter(property = "ldap.type", defaultValue = "unboundid")
    private String serverType;
    /**
     * The identity of the admin account for tbe directory server.
     */
    @Parameter(property = "ldap.authDn", defaultValue = "uid=admin,ou=system")
    private String authDn;
    /**
     * The credentials for the admin account of the directory server.
     */
    @Parameter(property = "ldap.passwd", defaultValue = "secret")
    private String passwd;
    /**
     * The root DN for the LDAP server.
     */
    @Parameter(property = "ldap.root", required = true)
    private String rootDn;
    /**
     *
     */
    @Parameter(property = "ldap.objectClasses", required = false)
    private String objectClasses;
    /**
     * An optional LDIF file that can be used to seed the embedded LDAP server.
     */
    @Parameter(property = "ldap.ldif", required = false)
    private File ldifFile;
    /**
     * The port for the LDAP server.
     */
    @Parameter(property = "ldap.port", defaultValue = "389")
    private int ldapPort;
    /**
     * The build target directory.
     */
    @Parameter(defaultValue = "${project.build.directory}", required = true)
    private File outputDirectory;

    /**
     * Get the server type.
     *
     * @return The server type.
     */
    @Override
    public String getServerType() {
        if (serverType.startsWith("dependency-")) {
            return serverType.substring(11);
        }
        if (serverType.startsWith("gav-")) {
            int index = serverType.indexOf("-", 4);
            if (index > 0) {
                return serverType.substring(4, index);
            }
        }
        return serverType;
    }

    /**
     * Get the embedded LDAP directory server configuration.
     *
     * @return A {@link Map} containing the server configuration.
     */
    @Override
    public Map<String, Object> getServerConfig() {
        final Map<String, Object> config = new HashMap<String, Object>();
        config.put(LDAPServer.ROOT, rootDn);
        if (objectClasses != null) {
            config.put(LDAPServer.OBJECT_CLASSES, objectClasses.split(","));
        }
        config.put(LDAPServer.WORK_DIR, new File(outputDirectory, serverType));
        if (ldifFile != null) {
            config.put(LDAPServer.LDIF_FILE, ldifFile);
        }
        config.put(LDAPServer.LDAP_PORT, ldapPort);
        config.put(LDAPServer.AUTH_DN, authDn);
        config.put(LDAPServer.PASSWD, passwd);
        return config;
    }
}



