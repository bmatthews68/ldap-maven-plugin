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

import com.btmatthews.maven.plugins.ldap.LDAPServer;
import com.btmatthews.utils.monitor.Server;
import com.btmatthews.utils.monitor.ServerFactory;

import static org.mockito.Mockito.mock;

/**
 * A server factory that creates a mock LDAP server.
 *
 * @author <a href="mailto:brian@btmatthews.com">Brian Matthews</a>
 * @since 1.2.0
 */
public class MockServerFactory implements ServerFactory {

    /**
     * Get the server name.
     *
     * @return Always returns {@code mock}.
     */
    public String getServerName() {
        return "mock";
    }

    /**
     * Create a mock {@link LDAPServer}.
     *
     * @return A mock {@link LDAPServer}.
     */
    public Server createServer() {
        return mock(LDAPServer.class);
    }
}
