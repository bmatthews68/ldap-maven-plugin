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

package com.btmatthews.maven.plugins.ldap.unboundid;

import com.btmatthews.maven.plugins.ldap.TestUtils;
import com.btmatthews.utils.monitor.Logger;
import com.btmatthews.utils.monitor.Server;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;

import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.startsWith;
import static org.mockito.Mockito.*;

/**
 * @author <a href="mailto:brian@btmatthews.com">Brian Matthews</a>
 * @since 1.1.1
 */
public class TestUnboundIDServer {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void testRunStop() throws IOException {
        final int port = TestUtils.getUnusedPort(10389);
        final Logger logger = mock(Logger.class);
        final Server server = new UnboundIDServer();

        server.configure("root", "dc=btmatthews,dc=com", logger);
        server.configure("authDn", "uid=admin,ou=system", logger);
        server.configure("passwd", "secret", logger);
        server.configure("ldapPort", port, logger);
        server.configure("useSchema", false, logger);
        server.configure("ldifFile", new File("target/test-classes/com/btmatthews/maven/plugins/ldap/unboundid/initial.ldif"), logger);
        server.configure("workingDirectory", folder.newFolder(), logger);
        server.start(logger);
        server.stop(logger);

        verify(logger).logInfo(eq("Configured root DN for directory server: dc=btmatthews,dc=com"));
        verify(logger).logInfo(eq("Configured admin identity for directory server: uid=admin,ou=system"));
        verify(logger).logInfo(eq("Configured admin credentials for directory server: secret"));
        verify(logger).logInfo(eq("Configured TCP port for directory server: " + port));
        verify(logger).logInfo(startsWith("Configured LDIF seed data source for directory server: "));
        verify(logger).logInfo(startsWith("Configured working directory for directory server: "));
        verify(logger).logInfo(eq("Starting UnboundID server"));
        verify(logger).logInfo(eq("Started UnboundID server"));
        verify(logger).logInfo(eq("Stopping UnboundID server"));
        verify(logger).logInfo(eq("Stopped UnboundID server"));
        verifyNoMoreInteractions(logger);
    }
}
