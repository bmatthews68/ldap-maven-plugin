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

package com.btmatthews.maven.plugins.ldap.dsml;

import com.unboundid.ldif.LDIFChangeRecord;
import org.junit.Test;

import java.io.InputStream;

import static org.junit.Assert.*;

/**
 * Unit test the {@link DSMLFormatReader}.
 *
 * @author <a href="mailto:brian@btmatthews.com">Brian Matthews</a>
 * @since 1.2.0
 */
public class TestDSMLReader {

    /**
     * Verify that we can load from DSML file that uses the namespace with a namespace prefix.
     *
     * @throws Exception If there was an unexpected problem executing the test case.
     */
    @Test
    public void canReadValidDSMLWithNamespace() throws Exception {
        verifySuccess("ns.dsml");
    }

    /**
     * Verify that we can load from DSML file that uses namespace as the default namespace.
     *
     * @throws Exception If there was an unexpected problem executing the test case.
     */
    @Test
    public void canReadValidDSMLWithDefaultNamespace() throws Exception {
        verifySuccess("defns.dsml");
    }

    /**
     * Verify that we can load from DSML file that uses a namespace and non-standard namespace prefix.
     *
     * @throws Exception If there was an unexpected problem executing the test case.
     */
    @Test
    public void canReadValidDSMLWithNonStandardNamespacePrefix() throws Exception {
        verifySuccess("nonstd.dsml");
    }

    /**
     * Helper method that runs the test case for a individual source DSML file.
     *
     * @param resourceName The name of the source DSML file.
     * @throws Exception If there was an unexpected problem executing the test case.
     */
    private void verifySuccess(final String resourceName) throws Exception {
        final InputStream inputStream = TestDSMLReader.class.getResourceAsStream(resourceName);
        final DSMLFormatReader reader = new DSMLFormatReader(inputStream);
        final LDIFChangeRecord first = reader.nextRecord();
        final LDIFChangeRecord second = reader.nextRecord();
        assertNotNull(first);
        assertEquals("uid=msimpson1,ou=People,dc=btmatthews,dc=com", first.getDN());
        assertNull(second);
    }
}
