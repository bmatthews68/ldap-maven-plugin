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


import com.btmatthews.ldapunit.DirectoryServerConfiguration;
import com.btmatthews.ldapunit.DirectoryServerRule;
import org.apache.maven.plugin.logging.SystemStreamLog;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;

import static org.codehaus.plexus.util.ReflectionUtils.setVariableValueInObject;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests for the dump plugin goal.
 *
 * @author <a href="mailto:brian.matthews@btmatthews.com">Brian Matthews</a>
 * @since 1.2.0
 */
@DirectoryServerConfiguration(ldifFile = "com/btmatthews/maven/plugins/ldap/mojo/initial.ldif")
public final class TestDumpMojo {

    /**
     * Temporary folder in which the mojo will dump data.
     */
    @Rule
    public final TemporaryFolder outputDirectory = new TemporaryFolder();

    @Rule
    public final DirectoryServerRule directoryServerRule = new DirectoryServerRule();

    /**
     * The mojo that implements the dump goal.
     */
    public final DumpMojo mojo = new DumpMojo();

    /**
     * Prepare for test case execution by creating and initialising the test case fixtures and mock objects.
     *
     * @throws Exception If there was a problem configuring the mojo.
     */
    @Before
    public void setUp() throws Exception {
        mojo.setLog(new SystemStreamLog());
        setVariableValueInObject(mojo, "outputDirectory", outputDirectory.getRoot());
        setVariableValueInObject(mojo, "host", "localhost");
        setVariableValueInObject(mojo, "port", 10389);
        setVariableValueInObject(mojo, "authDn", "uid=admin,ou=system");
        setVariableValueInObject(mojo, "passwd", "secret");
        setVariableValueInObject(mojo, "searchBase", "dc=btmatthews,dc=com");
        setVariableValueInObject(mojo, "searchFilter", "(objectclass=*)");
    }

    /**
     * Test the configuration for the dump goal.
     *
     * @throws Exception If something unexpected happens.
     */
    @Test
    public void dumpDSML() throws Exception {
        setVariableValueInObject(mojo, "filename", "dump.dsml");
        setVariableValueInObject(mojo, "format", "dsml");
        mojo.execute();
        assertTrue(new File(outputDirectory.getRoot(), "dump.dsml").exists());
    }

    /**
     * Test the configuration for the dump goal.
     *
     * @throws Exception If something unexpected happens.
     */
    @Test
    public void dumpLDIF() throws Exception {
        setVariableValueInObject(mojo, "filename", "dump.ldif");
        setVariableValueInObject(mojo, "format", "ldif");
        mojo.execute();
        assertTrue(new File(outputDirectory.getRoot(), "dump.ldif").exists());
    }
}
