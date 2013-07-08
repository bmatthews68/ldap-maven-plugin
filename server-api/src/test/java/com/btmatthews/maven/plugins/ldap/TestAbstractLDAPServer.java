/*
 * Copyright 2013 Brian Thomas Matthews
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

package com.btmatthews.maven.plugins.ldap;

import com.btmatthews.utils.monitor.Logger;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Unit test the abstract base class for the directory server implementations.
 *
 * @author <a href="mailto:brian@btmatthews.com">Brian Matthews</a>
 * @since 1.2.0
 */
public class TestAbstractLDAPServer {

    /**
     * Mock the logger object so that we can verify behaviour.
     */
    @Mock
    private Logger logger;

    /**
     * The test class.
     */
    private LDAPServer server;

    /**
     * Prepare for unit test execution.
     */
    @Before
    public void setUp() {
        initMocks(this);
        server = new AbstractLDAPServer() {
        };
    }

    /**
     * Verify the configuration of the root DN.
     */
    @Test
    public void configureRoot() {
        server.configure("root", "dc=btmatthews,dc=com", logger);
        verify(logger).logInfo("Configured root DN for directory server: dc=btmatthews,dc=com");
        assertEquals("dc=btmatthews,dc=com", server.getRoot());
    }

    /**
     * Verify the configuration of the authentication identifier.
     */
    @Test
    public void configureAuthDN() {
        server.configure("authDn", "uid=admin,ou=system", logger);
        verify(logger).logInfo("Configured admin identity for directory server: uid=admin,ou=system");
        assertEquals("uid=admin,ou=system", server.getAuthDn());
    }

    /**
     * Verify the configuration of the authentication credentials.
     */
    @Test
    public void configurePassword() {
        server.configure("passwd", "secret", logger);
        verify(logger).logInfo("Configured admin credentials for directory server: secret");
        assertEquals("secret", server.getPasswd());
    }

    /**
     * Verify the configuration of the working directory.
     */
    @Test
    public void configureWorkingDirectory() {
        final File workDir = mock(File.class);
        when(workDir.toString()).thenReturn("tmp");
        server.configure("workingDirectory", workDir, logger);
        verify(logger).logInfo("Configured working directory for directory server: tmp");
        assertSame(workDir, server.getWorkingDirectory());
    }

    /**
     * Verify the configuration of the LDIF file containing seed data.
     *
     * @throws MalformedURLException If the URL is invalid.
     */
    @Test
    public void configureLDIFFile() throws MalformedURLException {
        final File ldifFile = new File("initial.ldif");
        server.configure("ldifFile", ldifFile, logger);
        verify(logger).logInfo("Configured LDIF seed data source for directory server: initial.ldif");
        assertSame(ldifFile, server.getLdifFile());
    }

    /**
     * Verify the configuration of the LDAP port.
     */
    @Test
    public void configureLDAPPort() {
        server.configure("ldapPort", 10389, logger);
        verify(logger).logInfo("Configured TCP port for directory server: 10389");
        assertEquals(10389, server.getServerPort());
    }
}