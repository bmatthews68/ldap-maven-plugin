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
 * Created with IntelliJ IDEA.
 * User: Brian
 * Date: 12/01/13
 * Time: 04:06
 * To change this template use File | Settings | File Templates.
 */
public class TestDSMLReader {

    @Test
    public void canReadValidDSMLWithNamespace() throws Exception {
        verifySuccess("ns.dsml");
    }

    @Test

    public void canReadValidDSMLWithDefaultNamespace() throws Exception {
        verifySuccess("defns.dsml");
    }

    @Test
    public void canReadValidDSMLWithNonStandardNamespacePrefix() throws Exception {
        verifySuccess("nonstd.dsml");
    }

    public void verifySuccess(final String resourceName) throws Exception {
        final InputStream inputStream = TestDSMLReader.class.getResourceAsStream(resourceName);
        final DSMLReader reader = new DSMLReader(inputStream);
        final LDIFChangeRecord first = reader.nextRecord();
        final LDIFChangeRecord second = reader.nextRecord();
        assertNotNull(first);
        assertEquals("uid=msimpson1,ou=People,dc=btmatthews,dc=com", first.getDN());
        assertNull(second);
    }

}
