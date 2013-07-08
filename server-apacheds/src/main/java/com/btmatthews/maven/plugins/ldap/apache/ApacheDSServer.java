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

package com.btmatthews.maven.plugins.ldap.apache;

import javax.naming.NamingException;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.btmatthews.maven.plugins.ldap.AbstractLDAPServer;
import com.btmatthews.utils.monitor.Logger;
import org.apache.directory.server.core.CoreSession;
import org.apache.directory.server.core.DefaultDirectoryService;
import org.apache.directory.server.core.DirectoryService;
import org.apache.directory.server.core.authn.AuthenticationInterceptor;
import org.apache.directory.server.core.entry.DefaultServerEntry;
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
import org.apache.directory.server.protocol.shared.transport.TcpTransport;
import org.apache.directory.shared.ldap.entry.Entry;
import org.apache.directory.shared.ldap.entry.Modification;
import org.apache.directory.shared.ldap.exception.LdapNameNotFoundException;
import org.apache.directory.shared.ldap.ldif.LdifEntry;
import org.apache.directory.shared.ldap.ldif.LdifReader;
import org.apache.directory.shared.ldap.name.LdapDN;

/**
 * Implements an embedded ApacheDS LDAP apache. This code is based on the sample available at:
 * http://svn.apache.org/repos/asf/directory/documentation/samples/trunk/embedded-sample
 *
 * @author <a href="mailto:brian@btmatthews.com">Brian Matthews</a>
 * @since 1.1.0
 */
public final class ApacheDSServer extends AbstractLDAPServer {

    /**
     * The LDAP directory service.
     */
    private DirectoryService service;

    /**
     * The server that listens for LDAP requests and forwards them to the directory service.
     */
    private LdapServer server;

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
            partition.setSuffix(getRoot());
            service.setInterceptors(list);
            service.setWorkingDirectory(getWorkingDirectory());
            service.addPartition(partition);
            service.setExitVmOnShutdown(false);
            service.setShutdownHookEnabled(false);
            service.getChangeLog().setEnabled(false);
            service.startup();

            server = new LdapServer();
            server.setTransports(new TcpTransport("localhost", getServerPort()));
            server.setDirectoryService(service);
            server.start();

            createRoot(partition);
            if (getLdifFile() != null) {
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
            final LdapDN dn = new LdapDN(getRoot());
            final String dc = getRoot().substring(3, getRoot().indexOf(','));
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
        final CoreSession coreSession = service.getAdminSession();
        final InputStream in = new FileInputStream(getLdifFile());
        try {
            for (final LdifEntry ldifEntry : new LdifReader(new BufferedReader(new InputStreamReader(in)))) {
                final LdapDN dn = ldifEntry.getDn();
                if (ldifEntry.isEntry()) {
                    final Entry entry = ldifEntry.getEntry();
                    try {
                        coreSession.lookup(dn);
                    } catch (Exception e) {
                        try {
                            coreSession.add(new DefaultServerEntry(coreSession.getDirectoryService().getRegistries(), entry));
                        } catch (final NamingException e1) {
                        }
                    }
                } else {
                    final List<Modification> items = ldifEntry.getModificationItems();
                    try {
                        coreSession.modify(dn, items);
                    } catch (final NamingException e) {
                    }
                }
            }
        } finally {
            in.close();
        }
    }
}