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


import static org.junit.Assert.assertTrue;

import java.io.File;

import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugin.logging.SystemStreamLog;
import org.codehaus.plexus.util.ReflectionUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * Unit tests for the dump plugin goal.
 *
 * @author <a href="mailto:brian.matthews@btmatthews.com">Brian Matthews</a>
 * @since 1.2.0
 */
public final class TestDumpMojo extends AbstractLDAPMojoTest {

    @Rule
    public TemporaryFolder outputDirectory = new TemporaryFolder();

    public DumpMojo mojo = new DumpMojo();

    @Before
    public void setUp() throws Exception {
        super.setUp();
        mojo.setLog(new SystemStreamLog());
        ReflectionUtils.setVariableValueInObject(mojo, "outputDirectory", outputDirectory.getRoot());
        ReflectionUtils.setVariableValueInObject(mojo, "host", LOCALHOST);
        ReflectionUtils.setVariableValueInObject(mojo, "port", Integer.valueOf(PORT));
        ReflectionUtils.setVariableValueInObject(mojo, "authDn", DN);
        ReflectionUtils.setVariableValueInObject(mojo, "passwd", PASSWD);
        ReflectionUtils.setVariableValueInObject(mojo, "searchBase", "dc=btmatthews,dc=com");
        ReflectionUtils.setVariableValueInObject(mojo, "searchFilter", "(objectclass=*)");
    }

    /**
     * Test the configuration for the dsml-dump goal.
     *
     * @throws Exception If something unexpected happens.
     */
    @Test
    public void dumpDSML() throws Exception {
        ReflectionUtils.setVariableValueInObject(mojo, "filename", "dump.dsml");
        ReflectionUtils.setVariableValueInObject(mojo, "format", "dsml");
        mojo.execute();
        assertTrue(new File(outputDirectory.getRoot(), "dump.dsml").exists());
    }

    /**
     * Test the configuration for the dsml-dump goal.
     *
     * @throws Exception If something unexpected happens.
     */
    @Test
    public void dumpLDIF() throws Exception {
        ReflectionUtils.setVariableValueInObject(mojo, "filename", "dump.ldif");
        ReflectionUtils.setVariableValueInObject(mojo, "format", "ldif");
        mojo.execute();
        assertTrue(new File(outputDirectory.getRoot(), "dump.ldif").exists());
    }
}
