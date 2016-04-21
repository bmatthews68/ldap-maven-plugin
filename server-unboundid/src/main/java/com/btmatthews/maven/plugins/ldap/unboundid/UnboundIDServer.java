/*
 * Copyright 2013-2016 Brian Thomas Matthews
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

package com.btmatthews.maven.plugins.ldap.unboundid;

import com.btmatthews.maven.plugins.ldap.AbstractLDAPServer;
import com.btmatthews.utils.monitor.Logger;
import com.unboundid.ldap.listener.InMemoryDirectoryServer;
import com.unboundid.ldap.listener.InMemoryDirectoryServerConfig;
import com.unboundid.ldap.listener.InMemoryListenerConfig;
import com.unboundid.ldap.sdk.Attribute;
import com.unboundid.ldap.sdk.DN;
import com.unboundid.ldap.sdk.Entry;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldif.LDIFReader;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Implements an in-memory LDAP directory server using the
 * <a href="https://www.unboundid.com/products/ldap-sdk/docs/in-memory-directory-server.php">UnboundID SDK</a>.
 *
 * @author <a href="mailto:brian@btmatthews.com">Brian Matthews</a>
 * @since 1.1.1
 */
public final class UnboundIDServer extends AbstractLDAPServer {

    /**
     * The default object classes used for the root DN entry.
     */
    private static final String[] DEFAULT_ROOT_OBJECT_CLASSES = { "domain", "top" };

    /**
     * The in-memory instance of the UnboundID directory server.
     */
    private InMemoryDirectoryServer server;

    /**
     * Configure and start the embedded UnboundID server creating the root DN and loading the LDIF seed data.
     *
     * @param logger Used to log informational and error messages.
     */
    @Override
    public void start(final Logger logger) {
        try {
            logger.logInfo("Starting UnboundID server");
            final InMemoryListenerConfig listenerConfig = InMemoryListenerConfig.createLDAPConfig("default", getServerPort());
            final InMemoryDirectoryServerConfig config = new InMemoryDirectoryServerConfig(new DN(getRoot()));
            config.setListenerConfigs(listenerConfig);
            if (getAuthDn() != null) {
                config.addAdditionalBindCredentials(getAuthDn(), getPasswd());
            }
            server = new InMemoryDirectoryServer(config);
            String[] objectClasses = getObjectClasses();
            if (objectClasses == null) {
                objectClasses = DEFAULT_ROOT_OBJECT_CLASSES;
            }
            server.add(new Entry(getRoot(), new Attribute("objectclass", objectClasses)));
            if (getLdifFile() != null) {
                final InputStream in = new FileInputStream(getLdifFile());
                try {
                    final LDIFReader reader = new LDIFReader(in);
                    server.importFromLDIF(false, reader);
                } finally {
                    in.close();
                }
            }
            server.startListening();
            logger.logInfo("Started UnboundID server");
        } catch (final LDAPException e) {
            e.printStackTrace();
            logger.logError("Could not launch embedded UnboundID directory server", e);
        } catch (final IOException e) {
            e.printStackTrace();
            logger.logError("Could not launch embedded UnboundID directory server", e);
        }
    }

    /**
     * Shutdown the the embedded UnboundID server.
     *
     * @param logger Used to log informational and error messages.
     */
    @Override
    public void stop(final Logger logger) {
        logger.logInfo("Stopping UnboundID server");
        server.shutDown(true);
        logger.logInfo("Stopped UnboundID server");
    }
}
