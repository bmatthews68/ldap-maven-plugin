/*
 * Copyright 2012 Brian Thomas Matthews
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

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import com.btmatthews.utils.monitor.mojo.AbstractRunMojo;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * This Mojo implements the run goal which launches an embedded LDAP
 * server using Apache Directory Server.
 *
 * @author <a href="mailto:brian.matthews@btmatthews.com">Brian Matthews</a>
 * @since 1.1.0
 */
@Mojo(name = "run", defaultPhase = LifecyclePhase.PRE_INTEGRATION_TEST)
public final class RunLDAPMojo extends AbstractRunMojo {

    /**
     * The root DN for the LDAP server.
     */
    @Parameter(property = "ldap.root", required = true)
    private String rootDn;

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

    @Override
    public String getServerType() {
        return "apacheds";
    }

    @Override
    public Map<String, Object> getServerConfig() {
        final Map<String, Object> config = new HashMap<String, Object>();
        config.put("root", rootDn);
        config.put("workingDirectory", new File(outputDirectory, "ldap"));
        config.put("ldifFile", ldifFile);
        config.put("ldapPort", Integer.valueOf(ldapPort));
        return config;
    }
}



