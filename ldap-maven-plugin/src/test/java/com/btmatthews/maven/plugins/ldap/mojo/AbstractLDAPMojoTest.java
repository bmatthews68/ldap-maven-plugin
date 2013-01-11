/*
 * Copyright 2008-2012 Brian Thomas Matthews
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

import static org.mockito.MockitoAnnotations.initMocks;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

import com.btmatthews.maven.plugins.ldap.LDAPServer;
import com.btmatthews.maven.plugins.ldap.TestUtils;
import com.btmatthews.maven.plugins.ldap.apache.ApacheDSServer;
import com.btmatthews.utils.monitor.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mock;

/**
 * Abstract base class for Mojo test cases belonging to the LDAP Maven Plugin.
 *
 * @author <a href="mailto:brian.matthews@btmatthews.com">Brian Matthews</a>
 * @version 1.0
 */
public abstract class AbstractLDAPMojoTest {
    /**
     * The LDAP protocol version.
     */
    protected static final int VERSION = 3;

    /**
     * The host name of the LDAP apache.
     */
    protected static final String LOCALHOST = "localhost";

    /**
     * The port number of the LDAP apache.
     */
    protected static final int PORT = 10389;

    /**
     * The distinguished name used for authentication.
     */
    protected static final String DN = "uid=admin,ou=System";

    /**
     * The password used for authentication.
     */
    protected static final String PASSWD = "secret";

    @Mock
    private Logger logger;

    @Rule
    public TemporaryFolder workingDir = new TemporaryFolder();

    private ApacheDSServer server;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        server = new ApacheDSServer();
        server.configure(LDAPServer.ROOT, "dc=btmatthews,dc=com", logger);
        server.configure(LDAPServer.LDAP_PORT, Integer.valueOf(10389), logger);
        server.configure(LDAPServer.WORK_DIR, workingDir.getRoot(), logger);
        server.configure(LDAPServer.LDIF_FILE, TestUtils.getURL("com/btmatthews/maven/plugins/ldap/initial.ldif"), logger);
        server.start(logger);
    }

    @After
    public void tearDown() {
        server.stop(logger);
        workingDir.delete();
    }
}
