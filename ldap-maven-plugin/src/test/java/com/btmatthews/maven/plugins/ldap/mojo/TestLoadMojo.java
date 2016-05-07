/*
 * Copyright 2008-2011 Brian Thomas Matthews
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

import static org.codehaus.plexus.util.ReflectionUtils.setVariableValueInObject;

/**
 * Unit tests for the load plugin goal.
 *
 * @author <a href="mailto:brian.matthews@terranua.com">Brian Matthews</a>
 * @version 1.0
 */
@DirectoryServerConfiguration(ldifFile = "com/btmatthews/maven/plugins/ldap/mojo/initial.ldif")
public final class TestLoadMojo {

    /**
     * The mojo that implements the load goal.
     */
    private final LoadMojo mojo = new LoadMojo();

    @Rule
    public final DirectoryServerRule directoryServerRule = new DirectoryServerRule();

    /**
     * Prepare for test case execution by creating and initialising test fixtures and mock objects.
     *
     * @throws Exception If there was a problem configuring the mojo.
     */
    @Before
    public void setUp() throws Exception {
        mojo.setLog(new SystemStreamLog());
        setVariableValueInObject(mojo, "host", "localhost");
        setVariableValueInObject(mojo, "port", 10389);
        setVariableValueInObject(mojo, "authDn", "uid=admin,ou=system");
        setVariableValueInObject(mojo, "passwd", "secret");
    }

    /**
     * Verify that we can load DSML file using the namespace with a namespace prefix.
     *
     * @throws Exception If the mojo execution failed.
     */
    @Test
    public void loadDSMLWithNamespace() throws Exception {
        setVariableValueInObject(mojo, "sources", new Source[]{new Dsml("classpath:com/btmatthews/maven/plugins/ldap/mojo/add.dsml")});
        setVariableValueInObject(mojo, "continueOnError", Boolean.FALSE);
        mojo.execute();
    }

    /**
     * Verify that we can load DSML file without a namespace.
     *
     * @throws Exception If the mojo execution failed.
     */
    @Test
    public void loadDSMLWithoutNamespace() throws Exception {
        setVariableValueInObject(mojo, "sources", new Source[]{new Dsml("classpath:com/btmatthews/maven/plugins/ldap/mojo/add1.dsml")});
        setVariableValueInObject(mojo, "continueOnError", Boolean.FALSE);
        mojo.execute();
    }

    /**
     * Verify that we can load LDIF file that adds an entry to the LDAP directory.
     *
     * @throws Exception If the mojo execution failed.
     */
    @Test
    public void testAddLDIF() throws Exception {
        setVariableValueInObject(mojo, "sources", new Source[]{new Ldif("classpath:com/btmatthews/maven/plugins/ldap/mojo/add.ldif")});
        setVariableValueInObject(mojo, "continueOnError", Boolean.FALSE);
        mojo.execute();
    }

    /**
     * Verify that we can load LDIF file that modifies an entry in the LDAP directory.
     *
     * @throws Exception If the mojo execution failed.
     */
    @Test
    public void testModifyLDIF() throws Exception {
        setVariableValueInObject(mojo, "sources", new Source[]{new Ldif("classpath:com/btmatthews/maven/plugins/ldap/mojo/modify.ldif")});
        setVariableValueInObject(mojo, "continueOnError", Boolean.FALSE);
        mojo.execute();
    }

    /**
     * Verify that we can load LDIF file that deletes an entry from the LDAP directory.
     *
     * @throws Exception If the mojo execution failed.
     */
    @Test
    public void testDeleteLDIF() throws Exception {
        setVariableValueInObject(mojo, "sources", new Source[]{new Ldif("classpath:com/btmatthews/maven/plugins/ldap/mojo/delete.ldif")});
        setVariableValueInObject(mojo, "continueOnError", Boolean.FALSE);
        mojo.execute();
    }
}
