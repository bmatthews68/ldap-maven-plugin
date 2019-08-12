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

package com.btmatthews.maven.plugins.ldap.apache;

import com.btmatthews.maven.plugins.ldap.AbstractLDAPServer;
import com.btmatthews.utils.monitor.Logger;
import org.apache.directory.api.ldap.model.entry.Entry;
import org.apache.directory.api.ldap.model.exception.LdapException;
import org.apache.directory.api.ldap.model.name.Dn;
import org.apache.directory.server.core.api.DirectoryService;
import org.apache.directory.server.core.api.InstanceLayout;
import org.apache.directory.server.core.api.interceptor.Interceptor;
import org.apache.directory.server.core.api.partition.Partition;
import org.apache.directory.server.core.authn.AuthenticationInterceptor;
import org.apache.directory.server.core.exception.ExceptionInterceptor;
import org.apache.directory.server.core.factory.DefaultDirectoryServiceFactory;
import org.apache.directory.server.core.normalization.NormalizationInterceptor;
import org.apache.directory.server.core.operational.OperationalAttributeInterceptor;
import org.apache.directory.server.core.partition.impl.avl.AvlPartition;
import org.apache.directory.server.core.referral.ReferralInterceptor;
import org.apache.directory.server.core.subtree.SubentryInterceptor;
import org.apache.directory.server.ldap.LdapServer;
import org.apache.directory.server.protocol.shared.store.LdifFileLoader;
import org.apache.directory.server.protocol.shared.transport.TcpTransport;

import java.util.ArrayList;
import java.util.List;

/**
 * Implements an embedded ApacheDS LDAP apache.
 *
 * @author <a href="mailto:brian@btmatthews.com">Brian Matthews</a>
 * @since 1.1.0
 */
public final class ApacheDSServer extends AbstractLDAPServer {

    /**
     * The LDAP directory service.
     */
    private DirectoryService directoryService;
    /**
     * The server that listens for LDAP requests and forwards them to the directory service.
     */
    private LdapServer ldapServer;


    /**
     * Configure and start the embedded ApacheDS server creating the root DN and loading the LDIF seed data.
     *
     * @param logger Used to log informational and error messages.
     */
    @Override
    public void start(final Logger logger) {
        try {
            logger.logInfo("Starting ApacheDS server");
            final String INSTANCE_NAME = "rootPartition";
            DefaultDirectoryServiceFactory factory = new DefaultDirectoryServiceFactory();
            factory.init(INSTANCE_NAME);
            directoryService = factory.getDirectoryService();

            final List<Interceptor> list = new ArrayList<Interceptor>();
            NormalizationInterceptor normalizationInterceptor = new NormalizationInterceptor();
            normalizationInterceptor.init(directoryService);
            list.add(normalizationInterceptor);

            AuthenticationInterceptor authenticationInterceptor = new AuthenticationInterceptor();
            authenticationInterceptor.init(directoryService);
            list.add(authenticationInterceptor);

            ReferralInterceptor referralInterceptor = new ReferralInterceptor();
            referralInterceptor.init(directoryService);
            list.add(referralInterceptor);

            ExceptionInterceptor exceptionInterceptor = new ExceptionInterceptor();
            exceptionInterceptor.init(directoryService);
            list.add(exceptionInterceptor);

            OperationalAttributeInterceptor operationalAttributeInterceptor = new OperationalAttributeInterceptor();
            operationalAttributeInterceptor.init(directoryService);
            list.add(operationalAttributeInterceptor);

            SubentryInterceptor subentryInterceptor = new SubentryInterceptor();
            subentryInterceptor.init(directoryService);
            list.add(subentryInterceptor);
            directoryService.setInterceptors(list);


            directoryService.getChangeLog().setEnabled(false);
            directoryService.setShutdownHookEnabled(true);

            InstanceLayout il = new InstanceLayout(getWorkingDirectory());
            directoryService.setInstanceLayout(il);

            AvlPartition partition = new AvlPartition(
                    directoryService.getSchemaManager());
            partition.setId(INSTANCE_NAME);
            partition.setSuffixDn(new Dn(directoryService.getSchemaManager(),
                    getRoot()));
            partition.initialize();
            directoryService.addPartition(partition);

            ldapServer = new LdapServer();
            ldapServer.setTransports(new TcpTransport(getServerPort()));
            ldapServer.setDirectoryService(directoryService);

            directoryService.startup();
            ldapServer.start();

            createRoot(partition);
            loadLdifFile();

            logger.logInfo("Started ApacheDS server");
        } catch (final Exception e) {
            logger.logError("Error starting ApacheDS server e");
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
            ldapServer.stop();
            directoryService.shutdown();
            logger.logInfo("Stopped ApacheDS server");
        } catch (final Exception e) {
            logger.logError("Error stopping ApacheDS server", e);
        }
    }

    @Override
    public boolean isStarted(final Logger logger) {
        return ldapServer != null && ldapServer.isStarted();
    }

    @Override
    public boolean isStopped(final Logger logger) {
        return ldapServer == null || !ldapServer.isStarted();
    }

    /**
     * Create the root DN.
     *
     * @param partition The partition in which to create the root DN.
     * @throws Exception If there was an error creating the root DN.
     */

    private void createRoot(final Partition partition) throws Exception {
        try {
            directoryService.getAdminSession().lookup(partition.getSuffixDn());
        } catch (final LdapException e) {
            final Dn dn = new Dn(getRoot());
            final String dc = getRoot().substring(3, getRoot().indexOf(','));
            final Entry entry = directoryService.newEntry(dn);
            entry.add("objectClass", "top", "domain", "extensibleObject");
            entry.add("dc", dc);
            directoryService.getAdminSession().add(entry);
        }
    }

    /**
     * Load the LDIF file used to seed the directory.
     *
     * @throws Exception If there was an error.
     */
    private void loadLdifFile() throws Exception {
        new LdifFileLoader(directoryService.getAdminSession(), getLdifFile(), null)
                .execute();
    }
}
