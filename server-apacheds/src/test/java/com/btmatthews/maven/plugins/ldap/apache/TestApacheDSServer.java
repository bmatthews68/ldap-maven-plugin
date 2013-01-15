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

import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

import com.btmatthews.maven.plugins.ldap.TestUtils;
import com.btmatthews.utils.monitor.Logger;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mock;

/**
 * @author <a href="mailto:brian@btmatthews.com">Brian Matthews</a>
 * @since 1.2.0
 */
public class TestApacheDSServer {

    private ApacheDSServer server;

    @Mock
    private Logger logger;

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        server = new ApacheDSServer();
        server.configure("root", "dc=btmatthews,dc=com", logger);
        server.configure("ldapPort", Integer.valueOf(10389), logger);
        server.configure("ldifFile", TestUtils.getURL("com/btmatthews/maven/plugins/ldap/initial.ldif"), logger);
        server.configure("workingDirectory", folder.newFolder(), logger);
    }

    @Test
    public void testRunStop() {
        server.start(logger);
        verify(logger).logInfo("Starting ApacheDS server");
        verify(logger).logInfo("Started ApacheDS server");
        server.stop(logger);
        verify(logger).logInfo("Stopping ApacheDS server");
        verify(logger).logInfo("Stopped ApacheDS server");
    }
}
