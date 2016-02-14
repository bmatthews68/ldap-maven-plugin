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

package com.btmatthews.maven.plugins.ldap.opendj;

import com.btmatthews.maven.plugins.ldap.AbstractLDAPServer;
import com.btmatthews.utils.monitor.Logger;
import org.forgerock.opendj.ldap.*;
import org.forgerock.opendj.ldif.LDIFEntryReader;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Implements an in-memory LDAP directory server using OpenDJ SDK. See
 * <a href="http://opendj.forgerock.org/opendj-ldap-sdk-examples/xref/org/forgerock/opendj/examples/Server.html">here</a>
 * for original code.
 *
 * @author <a href="mailto:brian@btmatthews.com">Brian Matthews</a>
 * @since 1.1.1
 */
public final class OpenDJServer extends AbstractLDAPServer {

    /**
     * The listener that accepts and dispatches LDAP requests.
     */
    private LDAPListener listener;

    /**
     * Start the OpenDJ in-memory directory server.
     *
     * @param logger Used for logging.
     */
    @Override
    public void start(final Logger logger) {
        try {
            logger.logInfo("Starting OpenDJ server");
            final MemoryBackend backend;
            if (getLdifFile() == null) {
                backend = new MemoryBackend();
            } else {
                final InputStream inputStream = new FileInputStream(getLdifFile());
                final LDIFEntryReader reader = new LDIFEntryReader(inputStream);
                backend = new MemoryBackend(reader);
            }
            final ServerConnectionFactory<LDAPClientContext, Integer> connectionHandler = Connections.newServerConnectionFactory(backend);
            final LDAPListenerOptions options = new LDAPListenerOptions().setBacklog(4096);
            listener = new LDAPListener("localhost", getServerPort(), connectionHandler, options);
            logger.logInfo("Started OpenDJ server");
        } catch (final IOException e) {
            logger.logError("Error starting OpenDJ server", e);
        }
    }

    /**
     * Stop the OpenDJ in-memory directory server.
     *
     * @param logger User for logging.
     */
    @Override
    public void stop(final Logger logger) {
        logger.logInfo("Stopping OpenDJ server");
        if (listener != null) {
            listener.close();
        }
        logger.logInfo("Stopped OpenDJ server");
    }
}
