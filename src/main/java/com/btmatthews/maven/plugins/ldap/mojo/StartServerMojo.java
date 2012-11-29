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

import com.btmatthews.maven.plugins.ldap.apache.ApacheDSServer;
import com.btmatthews.utils.monitor.Monitor;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * This Mojo implements the run goal which launches an embedded LDAP
 * server using Apache Directory Server.
 *
 * @author <a href="mailto:brian.matthews@btmatthews.com">Brian Matthews</a>
 * @version 1.0
 */
@Mojo(name = "run", defaultPhase = LifecyclePhase.TEST_COMPILE)
public final class StartServerMojo extends AbstractServerMojo {

    /**
     * If {@code true} the LDAP server is run as a daemon.
     */
    @Parameter(property = "ldap.daemon", defaultValue = "false")
    private boolean daemon;

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

    /**
     * Create and configure monitor and server objects for the embedded ApacheDS server. Start the server then run
     * the monitor. If the embedded server is to be run as a daemon then a thread is created to run the monitor.
     * Otherwise, the monitor runs on the main thread.
     *
     * @throws MojoFailureException If there was an error launching the embedded then
     */
    public void execute() throws MojoFailureException {
        final Monitor monitor = new Monitor(getMonitorKey(), getMonitorPort());
        final ApacheDSServer server = new ApacheDSServer();
        server.setWorkingDirectory(new File(outputDirectory, "ldap"));
        server.setRoot(rootDn);
        server.setLdifFile(ldifFile);
        server.setLdapPort(ldapPort);
        server.start(this);
        if (daemon) {
            new Thread(new Runnable() {
                public void run() {
                    monitor.runMonitor(server, StartServerMojo.this);
                }
            }).start();
        } else {
            monitor.runMonitor(server, this);
        }
    }
}



