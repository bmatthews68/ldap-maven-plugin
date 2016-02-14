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

import com.btmatthews.maven.plugins.ldap.TestUtils;
import com.btmatthews.utils.monitor.Logger;
import com.btmatthews.utils.monitor.Monitor;
import com.btmatthews.utils.monitor.MonitorObserver;
import com.btmatthews.utils.monitor.Server;
import org.apache.maven.plugin.MojoFailureException;
import org.junit.Test;

import java.util.Timer;
import java.util.TimerTask;

import static org.codehaus.plexus.util.ReflectionUtils.setVariableValueInObject;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the Mojo that implements the stop goal.
 *
 * @author <a href="mailto:brian@btmatthews.com">Brian Matthews</a>
 * @since 1.1.0
 */
public class TestStopMojo {

    /**
     * Start a mock server and verify that the {@link StopLDAPMojo} signals it to shutdown.
     *
     * @throws Exception If the test case failed.
     */
    @Test
    public void testStop() throws Exception {
        final int port = TestUtils.getUnusedPort(10389);
        final Server server = mock(Server.class);
        when(server.isStarted(any(Logger.class))).thenReturn(true);
        final MonitorObserver observer = mock(MonitorObserver.class);
        final Logger logger = mock(Logger.class);
        final Monitor monitor = new Monitor("ldap", port);
        final Thread monitorThread = monitor.runMonitorDaemon(server, logger, observer);
        final Timer timer = new Timer();
        timer.schedule(new StopLDAPTask(timer, port), 5000L);
        monitorThread.join(15000L);
        verify(server).start(same(logger));
        verify(logger).logInfo(eq("Waiting for command from client"));
        verify(logger).logInfo(eq("Receiving command from client"));
        verify(server).stop(same(logger));
        validateMockitoUsage();
    }

    public static class StopLDAPTask extends TimerTask {

        private final Timer timer;
        private final int port;

        public StopLDAPTask(final Timer timer, final int port) {
            this.timer = timer;
            this.port = port;
        }

        @Override
        public void run() {
            try {
                System.out.println("Sending stop");
                final StopLDAPMojo mojo = new StopLDAPMojo();
                setVariableValueInObject(mojo, "monitorPort", port);
                setVariableValueInObject(mojo, "monitorKey", "ldap");
                mojo.execute();
            } catch (final Exception e) {
                timer.schedule(new StopLDAPTask(timer, port), 100L);
            }
        }
    }
}
