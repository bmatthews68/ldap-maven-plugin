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

package com.btmatthews.maven.plugins.ldap.mojo;

import com.btmatthews.maven.plugins.ldap.apache.ApacheDSServerFactory;
import com.btmatthews.maven.plugins.ldap.unboundid.UnboundIDServerFactory;
import com.btmatthews.utils.monitor.Logger;
import com.btmatthews.utils.monitor.ServerFactory;
import com.btmatthews.utils.monitor.ServerFactoryLocator;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Unit test the server factory locator.
 *
 * @author <a href="mailto:brian@btmatthews.com">Brian Matthews</a>
 * @since 1.2.0
 */
public class TestServerLocator {

    /**
     * Used to mock the logging object.
     */
    @Mock
    private Logger logger;
    /**
     * The server factory locator.
     */
    private ServerFactoryLocator locator;

    /**
     * Prepare for test case execution by creating the test fixtures and mock objects.
     */
    @Before
    public void setUp() {
        initMocks(this);
        locator = new ServerFactoryLocator(logger, Thread.currentThread().getContextClassLoader());
    }

    /**
     * Verify that the server factory locator can locate the mock server factory.
     */
    @Test
    public void canLocateMockServerFactory() {
        final ServerFactory factory = locator.getFactory("mock");
        assertNotNull(factory);
        assertTrue(factory instanceof MockServerFactory);
    }

    /**
     * Verify that the server factory locator can locate ApacheDS server factory.
     */
    @Test
    public void canLocateApacheDSServerFactory() {
        final ServerFactory factory = locator.getFactory("apacheds");
        assertNotNull(factory);
        assertTrue(factory instanceof ApacheDSServerFactory);

    }

    /**
     * Verify that the server factory locator can locate the UnboundID server factory.
     */
    @Test
    public void canLocateUnboundIDServerFactory() {
        final ServerFactory factory = locator.getFactory("unboundid");
        assertNotNull(factory);
        assertTrue(factory instanceof UnboundIDServerFactory);
    }
}
