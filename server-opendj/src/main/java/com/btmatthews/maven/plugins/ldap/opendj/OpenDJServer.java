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

package com.btmatthews.maven.plugins.ldap.opendj;

import com.btmatthews.maven.plugins.ldap.AbstractLDAPServer;
import com.btmatthews.utils.monitor.Logger;
import org.forgerock.opendj.ldap.*;
import org.forgerock.opendj.ldif.LDIFEntryReader;

import java.io.IOException;
import java.io.InputStream;

/**
 * {@link }http://opendj.forgerock.org/opendj-ldap-sdk-examples/xref/org/forgerock/opendj/examples/Server.html}
 *
 * @author <a href="mailto:brian@btmatthews.com">Brian Matthews</a>
 * @since 1.1.1
 */
public final class OpenDJServer extends AbstractLDAPServer {

    private LDAPListener listener;

    @Override
    public void start(final Logger logger) {
        try {
            logger.logInfo("Starting OpenDJ server");
            final InputStream inputStream = getLdifFile().openStream();
            final LDIFEntryReader reader = new LDIFEntryReader(inputStream);
            final MemoryBackend backend = new MemoryBackend(reader);
            final ServerConnectionFactory<LDAPClientContext, Integer> connectionHandler = Connections.newServerConnectionFactory(backend);
            final LDAPListenerOptions options = new LDAPListenerOptions().setBacklog(4096);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        listener = new LDAPListener("localhost", getServerPort(), connectionHandler, options);
                    } catch (final IOException e) {
                    }
                }
            }).start();
            logger.logInfo("Started OpenDJ server");
        } catch (final IOException e) {
            logger.logError("Error starting OpenDJ server", e);
        }
    }

    @Override
    public void stop(final Logger logger) {
        logger.logInfo("Stopping OpenDJ server");
        listener.close();
        logger.logInfo("Stopped OpenDJ server");
    }
}
