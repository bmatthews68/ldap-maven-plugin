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

package com.btmatthews.maven.plugins.ldap.mojo;

import static org.codehaus.plexus.util.ReflectionUtils.setVariableValueInObject;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.validateMockitoUsage;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

import java.util.Timer;
import java.util.TimerTask;

import com.btmatthews.utils.monitor.Logger;
import com.btmatthews.utils.monitor.Monitor;
import com.btmatthews.utils.monitor.MonitorObserver;
import com.btmatthews.utils.monitor.Server;
import org.codehaus.plexus.util.ReflectionUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

/**
 * Unit tests for the Mojo that implements the stop goal.
 *
 * @author <a href="mailto:brian@btmatthews.com">Brian Matthews</a>
 * @since 1.1.0
 */
public class TestStopMojo {

    /**
     * Mock for the LDAP server.
     */
    @Mock
    private Server server;

    /**
     * Mock for the logger.
     */
    @Mock
    private Logger logger;

    /**
     * Mock the observer.
     */
    @Mock
    private MonitorObserver observer;

    /**
     * Prepare for test case execution by initialising the mocks.
     */
    @Before
    public void setUp() {
        initMocks(this);
    }

    /**
     * Start a mock server and verify that the {@link StopLDAPMojo} signals it to shutdown.
     *
     * @throws Exception If the test case failed.
     */
    @Test
    public void testStop() throws Exception {
        final Server server = Mockito.mock(Server.class);
        Mockito.when(server.isStarted(Mockito.any(Logger.class))).thenReturn(true);
        final Logger logger = Mockito.mock(Logger.class);
        final Monitor monitor = new Monitor("ldap", 12389);
        final Thread monitorThread = monitor.runMonitorDaemon(server, logger, observer);
        final StopLDAPMojo mojo = new StopLDAPMojo();
        setVariableValueInObject(mojo, "monitorPort", 12389);
        setVariableValueInObject(mojo, "monitorKey", "ldap");
        final Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    mojo.execute();
                } catch (final Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }, 5000L);
        monitorThread.join(15000L);
        verify(server).start(same(logger));
        verify(logger).logInfo(eq("Waiting for command from client"));
        verify(logger).logInfo(eq("Receiving command from client"));
        verify(server).stop(same(logger));
        validateMockitoUsage();
    }
}
