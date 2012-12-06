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

package com.btmatthews.maven.plugins.ldap.apache;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.btmatthews.utils.monitor.AbstractServer;
import com.btmatthews.utils.monitor.Logger;
import org.apache.directory.server.core.DefaultDirectoryService;
import org.apache.directory.server.core.DirectoryService;
import org.apache.directory.server.core.authn.AuthenticationInterceptor;
import org.apache.directory.server.core.entry.ServerEntry;
import org.apache.directory.server.core.exception.ExceptionInterceptor;
import org.apache.directory.server.core.interceptor.Interceptor;
import org.apache.directory.server.core.normalization.NormalizationInterceptor;
import org.apache.directory.server.core.operational.OperationalAttributeInterceptor;
import org.apache.directory.server.core.partition.Partition;
import org.apache.directory.server.core.partition.impl.btree.jdbm.JdbmPartition;
import org.apache.directory.server.core.referral.ReferralInterceptor;
import org.apache.directory.server.core.subtree.SubentryInterceptor;
import org.apache.directory.server.ldap.LdapServer;
import org.apache.directory.server.protocol.shared.store.LdifFileLoader;
import org.apache.directory.server.protocol.shared.transport.TcpTransport;
import org.apache.directory.shared.ldap.exception.LdapNameNotFoundException;
import org.apache.directory.shared.ldap.name.LdapDN;

/**
 * Implements an embedded ApacheDS LDAP apache. This code is based on the sample available at:
 * http://svn.apache.org/repos/asf/directory/documentation/samples/trunk/embedded-sample
 *
 * @author <a href="mailto:brian@btmatthews.com">Brian Matthews</a>
 * @since 1.1.0
 */
public final class ApacheDSServer extends AbstractServer {

    /**
     * The LDAP directory service.
     */
    private DirectoryService service;

    /**
     * The server that listens for LDAP requests and forwards them to the directory service.
     */
    private LdapServer server;

    /**
     * The root DN of the LDAP directory.
     */
    private String root;

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
        if ("root".equals(name)) {
            root = (String)value;
            logger.logInfo("Configured root DN for ApacheDS: " + root);
        } else if ("workingDirectory".equals(name)) {
            workingDirectory = (File)value;
            logger.logInfo("Configured working directory for ApacheDS: " + workingDirectory);
        } else if ("ldifFile".equals(name)) {
            ldifFile = (File)value;
            logger.logInfo("Configured LDIF seed data source for ApacheDS: " + ldifFile);
        } else if ("ldapPort".equals(name)) {
            serverPort = (Integer)value;
            logger.logInfo("Configured TCP port for ApacheDS: " + serverPort);
        }
    }

    /**
     * Configure and start the embedded ApacheDS server creating the root DN and loading the LDIF seed data.
     *
     * @param logger Used to log informational and error messages.
     */
    @Override
    public void start(final Logger logger) {
        try {
            logger.logInfo("Starting ApacheDS server");
            service = new DefaultDirectoryService();
            final List<Interceptor> list = new ArrayList<Interceptor>();
            list.add(new NormalizationInterceptor());
            list.add(new AuthenticationInterceptor());
            list.add(new ReferralInterceptor());
            list.add(new ExceptionInterceptor());
            list.add(new OperationalAttributeInterceptor());
            list.add(new SubentryInterceptor());
            final JdbmPartition partition = new JdbmPartition();
            partition.setId("rootPartition");
            partition.setSuffix(root);
            service.setWorkingDirectory(workingDirectory);
            service.addPartition(partition);
            service.setExitVmOnShutdown(false);
            service.setShutdownHookEnabled(false);
            service.getChangeLog().setEnabled(false);
            service.startup();

            server = new LdapServer();
            server.setTransports(new TcpTransport("localhost", serverPort));
            server.setDirectoryService(service);
            server.start();

            createRoot(partition);
            if (ldifFile != null) {
                loadLdifFile();
            }
            logger.logInfo("Started ApacheDS server");
        } catch (final Exception e) {
            logger.logError("Error starting ApacheDS server", e);
        }
    }

    /**
     * Shutdown the the embedded ApacheDS server.
     *
     * @param logger Used to log informational and error messages.
     */
    @Override
    public void stop(final Logger logger) {
        try {
            logger.logInfo("Stopping ApacheDS server");
            server.stop();
            service.shutdown();
            logger.logInfo("Stopped ApacheDS server");
        } catch (final Exception e) {
            logger.logError("Error stopping ApacheDS server", e);
        }
    }

    /**
     * Create the root DN.
     *
     * @param partition The partition in which to create the root DN.
     * @throws Exception If there was an error creating the root DN.
     */
    private void createRoot(final Partition partition) throws Exception {
        try {
            service.getAdminSession().lookup(partition.getSuffixDn());
        } catch (final LdapNameNotFoundException e) {
            final LdapDN dn = new LdapDN(root);
            final String dc = root.substring(3, root.indexOf(','));
            final ServerEntry entry = service.newEntry(dn);
            entry.add("objectClass", "top", "domain", "extensibleObject");
            entry.add("dc", dc);
            service.getAdminSession().add(entry);
        }
    }

    /**
     * Load the LDIF file used to seed the directory.
     *
     * @throws Exception If there was an error.
     */
    private void loadLdifFile() throws Exception {
        final LdifFileLoader loader = new LdifFileLoader(service.getAdminSession(), ldifFile.getAbsolutePath());
        loader.execute();
    }
}
