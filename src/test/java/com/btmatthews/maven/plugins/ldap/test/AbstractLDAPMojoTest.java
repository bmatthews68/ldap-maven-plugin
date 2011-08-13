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

package com.btmatthews.maven.plugins.ldap.test;

import java.io.File;

import junit.framework.TestCase;

import org.apache.maven.plugin.Mojo;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.codehaus.plexus.PlexusTestCase;

/**
 * Abstract base class for Mojo test cases belonging to the LDAP Maven Plugin.
 * 
 * @author <a href="mailto:brian.matthews@btmatthews.com">Brian Matthews</a>
 * @version 1.0
 */
public abstract class AbstractLDAPMojoTest extends AbstractMojoTestCase {
	/**
	 * The LDAP protocol version.
	 */
	protected static final int VERSION = 3;

	/**
	 * The host name of the LDAP server.
	 */
	protected static final String LOCALHOST = "localhost";

	/**
	 * The port number of the LDAP server.
	 */
	protected static final int PORT = 10389;

	/**
	 * The distinguished name used for authentication.
	 */
	protected static final String DN = "uid=admin,ou=System";

	/**
	 * The password used for authentication.
	 */
	protected static final String PASSWD = "secret";

	/**
	 * The name of the property that holds the authentication password.
	 */
	protected static final String PASSWD_PROPERTY = "passwd";

	/**
	 * The name of the property that holds the authentication principal.
	 */
	protected static final String AUTH_DN_PROPERTY = "authDn";

	/**
	 * The name of the property that holds the LDAP server port.
	 */
	protected static final String PORT_PROPERTY = "port";

	/**
	 * The name of the property that holds the LDAP server host.
	 */
	protected static final String HOST_PROPERTY = "host";

	/**
	 * The name of the property that holds the LDAP protocol version.
	 */
	protected static final String VERSION_PROPERTY = "version";

	/**
	 * Exception reported if there is an invalid exception.
	 */
	protected static final String MOJO_EXCEPTION_EXPECTED = "Was expecting MojoExecutionException";

	/**
	 * The default constructor.
	 */
	protected AbstractLDAPMojoTest() {
	}

	/**
	 * Get the Mojo that implements the specified goal.
	 * 
	 * @param goal
	 *            The goal name.
	 * @return The Mojo that implements the specified goal.
	 * @throws Exception
	 *             If something unexpected happens.
	 */
	protected final Mojo getMojo(final String goal) throws Exception {
		final StringBuffer buffer = new StringBuffer("/target/test-classes/");
		buffer.append(this.getName());
		buffer.append("-plugin-config.xml");
		final File testPom = new File(PlexusTestCase.getBasedir(),
				buffer.toString());
		return this.lookupMojo(goal, testPom);
	}

	/**
	 * Assert that the value of a Mojo variable has the specified expected
	 * value.
	 * 
	 * @param mojo
	 *            The Mojo object.
	 * @param name
	 *            The name of the variable.
	 * @param expectedValue
	 *            The expected value of the variable.
	 * @throws Exception
	 *             If something unexpected happens.
	 */
	protected final void assertEquals(final Mojo mojo, final String name,
			final String expectedValue) throws Exception {
		TestCase.assertEquals(expectedValue,
				(String) this.getVariableValueFromObject(mojo, name));
	}

	/**
	 * Assert that the value of a Mojo variable has the specified expected
	 * value.
	 * 
	 * @param mojo
	 *            The Mojo object.
	 * @param name
	 *            The name of the variable.
	 * @param expectedValue
	 *            The expected value of the variable.
	 * @throws Exception
	 *             If something unexpected happens.
	 */
	protected final void assertEquals(final Mojo mojo, final String name,
			final int expectedValue) throws Exception {
		TestCase.assertEquals(expectedValue, ((Integer) this
				.getVariableValueFromObject(mojo, name)).intValue());
	}

	/**
	 * Assert that the value of a Mojo variable has the specified expected
	 * value.
	 * 
	 * @param mojo
	 *            The Mojo object.
	 * @param name
	 *            The name of the variable.
	 * @param expectedValue
	 *            The expected value of the variable.
	 * @throws Exception
	 *             If something unexpected happens.
	 */
	protected final void assertEquals(final Mojo mojo, final String name,
			final boolean expectedValue) throws Exception {
		TestCase.assertEquals(expectedValue, ((Boolean) this
				.getVariableValueFromObject(mojo, name)).booleanValue());
	}
}
