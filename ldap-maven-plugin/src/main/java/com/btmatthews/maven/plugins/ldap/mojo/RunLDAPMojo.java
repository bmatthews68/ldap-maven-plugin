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

package com.btmatthews.maven.plugins.ldap.mojo;


import com.btmatthews.maven.plugins.ldap.LDAPServer;
import com.btmatthews.utils.monitor.mojo.AbstractRunMojo;
import com.jcabi.aether.Aether;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.codehaus.classworlds.ClassRealm;
import org.codehaus.classworlds.ClassWorld;
import org.codehaus.classworlds.DuplicateRealmException;
import org.sonatype.aether.artifact.Artifact;
import org.sonatype.aether.repository.RemoteRepository;
import org.sonatype.aether.resolution.DependencyResolutionException;
import org.sonatype.aether.util.artifact.DefaultArtifact;

import java.io.File;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This Mojo implements the run goal which launches an embedded LDAP
 * directory server.
 *
 * @author <a href="mailto:brian.matthews@btmatthews.com">Brian Matthews</a>
 * @since 1.1.0
 */
@Mojo(name = "run", defaultPhase = LifecyclePhase.PRE_INTEGRATION_TEST)
public final class RunLDAPMojo extends AbstractRunMojo {

    /**
     * The server can be one of:
     * <ul>
     * <li>apacheds</li>
     * <li>opendj</li>
     * <li>unboundid</li>
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
    @Parameter(defaultValue = "${project.remoteProjectRepositories}")
    private List<RemoteRepository> projectRepos;
    @Parameter(defaultValue = "${settings.localRepository}")
    private File localRepo;

    /**
     * Get the server type.
     *
     * @return The server type.
     */
    @Override
    public String getServerType() {
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
        config.put(LDAPServer.WORK_DIR, new File(outputDirectory, serverType));
        if (ldifFile != null) {
            config.put(LDAPServer.LDIF_FILE, ldifFile);
        }
        config.put(LDAPServer.LDAP_PORT, ldapPort);
        config.put(LDAPServer.AUTH_DN, authDn);
        config.put(LDAPServer.PASSWD, passwd);
        return config;
    }

    @Override
    public void execute() throws MojoFailureException {
        try {
            final List<Artifact> artifacts = getServerArtifacts();
            addServerArtifactsClassPath(artifacts);
        } catch (final DependencyResolutionException e) {
        }
        super.execute();
    }

    /**
     * Resolve the artifact containing the server implementation and its transitive dependencies.
     *
     * @return A list of artifacts.
     */
    private List<Artifact> getServerArtifacts() throws DependencyResolutionException {
        final Aether aether = new Aether(projectRepos, localRepo);
        final Artifact artifact = new DefaultArtifact("com.btmatthews.maven.plugins.ldap", "server-" + serverType, "", "jar", "1.2.0-SNAPSHOT");
        return aether.resolve(artifact, "runtime");
    }

    /**
     * Add a list of artifacts to the runtime classpath.
     *
     * @param artifacts The list of artifacts.
     * @throws MojoFailureException If there was an error building the new runtime classpath.
     */
    private void addServerArtifactsClassPath(final List<Artifact> artifacts) throws MojoFailureException {
        try {
            final ClassWorld world = new ClassWorld();
            final ClassRealm realm = world.newRealm("ldap-maven-plugin", Thread.currentThread().getContextClassLoader());
            final ClassRealm serverRealm = realm.createChildRealm("server");
            for (final Artifact artifact : artifacts) {
                serverRealm.addConstituent(artifact.getFile().toURI().toURL());
            }
            Thread.currentThread().setContextClassLoader(serverRealm.getClassLoader());
        } catch (final DuplicateRealmException e) {
            getLog().error(e);
            throw new MojoFailureException(e.getMessage(), e);
        } catch (final MalformedURLException e) {
            getLog().error(e);
            throw new MojoFailureException(e.getMessage(), e);
        }
    }
}



