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

import static org.junit.Assert.assertTrue;

import netscape.ldap.LDAPConnection;
import netscape.ldap.LDAPException;
import org.junit.Test;

/**
 * A simple integration test to verify that the embedded LDAP directory server is running.
 *
 * @author <a href="mailto:brian@btmatthews.com">Brian Matthews</a>
 * @since 1.1.0
 */
public class ITestCheckRunning {

    /**
     * Check to see if the embedded LDAP server started.
     *
     * @throws Exception If the test case fails.
     */
    @Test
    public void testLDAPRunning() throws Exception {
        final LDAPConnection connection = new LDAPConnection();
        connection.setConnectTimeout(5);
        int i = 0;
        while (i < 3) {
            try {
                connection.connect(3, "localhost", 10389, "uid=admin,ou=system", "secret");
                break;
            } catch (LDAPException e) {
                i++;
            }
        }
        assertTrue(i < 3);
        connection.disconnect();
    }
}