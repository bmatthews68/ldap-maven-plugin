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

import junit.framework.TestCase;

import org.apache.maven.plugin.Mojo;

/**
 * Unit tests for the dsml-dumper plugin goal.
 * 
 * @author <a href="mailto:brian.matthews@btmatthews.com">Brian Matthews</a>
 * @version 1.0
 */
public final class TestDSMLDumperMojo extends AbstractLDAPMojoTest {
	/**
	 * The default constructor.
	 */
	public TestDSMLDumperMojo() {
	}

	/**
	 * Test the configuration for the dsml-dump goal.
	 * 
	 * @throws Exception
	 *             If something unexpected happens.
	 */
	public void testDSMLDumper() throws Exception {
		final Mojo mojo = this.getMojo("dsml-dump");
		TestCase.assertNotNull(mojo);
		this.assertEquals(mojo, AbstractLDAPMojoTest.VERSION_PROPERTY,
				AbstractLDAPMojoTest.VERSION);
		this.assertEquals(mojo, AbstractLDAPMojoTest.HOST_PROPERTY,
				AbstractLDAPMojoTest.LOCALHOST);
		this.assertEquals(mojo, AbstractLDAPMojoTest.PORT_PROPERTY,
				AbstractLDAPMojoTest.PORT);
		this.assertEquals(mojo, AbstractLDAPMojoTest.AUTH_DN_PROPERTY,
				AbstractLDAPMojoTest.DN);
		this.assertEquals(mojo, AbstractLDAPMojoTest.PASSWD_PROPERTY,
				AbstractLDAPMojoTest.PASSWD);
		mojo.execute();
	}
}
