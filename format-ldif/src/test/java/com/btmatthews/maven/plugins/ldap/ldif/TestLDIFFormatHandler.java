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

package com.btmatthews.maven.plugins.ldap.ldif;

import com.btmatthews.maven.plugins.ldap.AddRequestMatcher;
import com.btmatthews.maven.plugins.ldap.FormatHandler;
import com.btmatthews.maven.plugins.ldap.FormatLogger;
import com.unboundid.ldap.sdk.LDAPInterface;
import com.unboundid.ldap.sdk.SearchRequest;
import com.unboundid.ldap.sdk.SearchResult;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mock;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import static com.btmatthews.maven.plugins.ldap.FormatTestUtils.createSearchResult;
import static com.btmatthews.maven.plugins.ldap.FormatTestUtils.createSearchResultEntry;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Unit test the {@link LDIFFormatHandler}.
 *
 * @author <a href="mailto:brian@btmatthews.com">Brian Matthews</a>
 * @since 1.2.0
 */
public class TestLDIFFormatHandler {
    /**
     * Temporary folder used for dump files.
     */
    @Rule
    public TemporaryFolder outputFolder = new TemporaryFolder();
    /**
     * Mock the LDAP directory server connection.
     */
    @Mock
    private LDAPInterface connection;
    /**
     * Mock the logger.
     */
    @Mock
    private FormatLogger logger;
    /**
     * The {@link LDIFFormatHandler} being tested.
     */
    private FormatHandler formatHandler;

    /**
     * Prepare for unit test execution by creating and initializing the mock objects and test
     * fixtures.
     */
    @Before
    public void setUp() {
        initMocks(this);
        formatHandler = new LDIFFormatHandler();
    }

    /**
     * Verify the that the
     * {@link LDIFFormatHandler#load(com.unboundid.ldap.sdk.LDAPInterface, java.io.InputStream, boolean, com.btmatthews.maven.plugins.ldap.FormatLogger)}
     * behaves correctly when importing an empty LDIF file.
     */
    @Test
    public void canLoadEmptyLDIFFile()  throws Exception {
        final InputStream inputStream = TestLDIFFormatHandler.class.getResourceAsStream("empty.ldif");
        formatHandler.load(connection, inputStream, true, logger, false);
        verifyNoMoreInteractions(connection, logger);
    }

    /**
     * Verify that the
     * {@link LDIFFormatHandler#load(com.unboundid.ldap.sdk.LDAPInterface, java.io.InputStream, boolean, com.btmatthews.maven.plugins.ldap.FormatLogger)}
     * behaves correctly when imporing an LDIF file containing a single LDIF change record.
     *
     * @throws Exception If there was an unexpected error executing the test case.
     */
    @Test
    public void canLoadLDIFFileWithOneChangeRecord() throws Exception {
        final InputStream inputStream = TestLDIFFormatHandler.class.getResourceAsStream("one.ldif");
        formatHandler.load(connection, inputStream, true, logger, false);
        verify(connection).add(argThat(new AddRequestMatcher("ou=People,dc=btmatthews,dc=com", "ou", "People", "objectclass", "organizationalUnit")));
        verifyNoMoreInteractions(connection, logger);
    }

    /**
     * Verify that the
     * {@link LDIFFormatHandler#load(com.unboundid.ldap.sdk.LDAPInterface, java.io.InputStream, boolean, com.btmatthews.maven.plugins.ldap.FormatLogger)}
     * behaves correctly when imporing an LDIF file containing two LDIF change records.
     *
     * @throws Exception If there was an unexpected error executing the test case.
     */
    @Test
    public void canLoadLDIFFileWithTwoChangeRecords() throws Exception {
        final InputStream inputStream = TestLDIFFormatHandler.class.getResourceAsStream("two.ldif");
        formatHandler.load(connection, inputStream, true, logger, false);
        verify(connection).add(argThat(new AddRequestMatcher(
                "ou=People,dc=btmatthews,dc=com",
                "ou", "People",
                "objectclass", "organizationalUnit")));
        verify(connection).add(argThat(new AddRequestMatcher(
                "cn=Bart Simpson,ou=People,dc=btmatthews,dc=com",
                "cn", "Bart Simpson",
                "sn", "Simpson",
                "givenName", "Bart",
                "uid", "bsimpson",
                "objectclass", "inetOrgPerson")));
        verifyNoMoreInteractions(connection, logger);
    }

    /**
     * Verify that an empty LDIF file is created when dumping an empty search result.
     *
     * @throws Exception If there was a problem executing the test case.
     */
    @Test
    public void noDataInDump() throws Exception {
        final SearchResult results = createSearchResult();
        when(connection.search(any(SearchRequest.class))).thenReturn(results);

        final File outputFile = outputFolder.newFile();
        final OutputStream outputStream = new FileOutputStream(outputFile);
        formatHandler.dump(connection, "dc=btmatthews,dc=com", "(objectclass=*)", outputStream, logger);
        assertTrue(outputFile.exists());
        assertEquals(0, outputFile.length());
    }

    /**
     * Verify that that the LDIF file is created correctly when dumping search result with one entry.
     *
     * @throws Exception If there was a problem executing the test case.
     */
    @Test
    public void oneItemInDump() throws Exception {
        final SearchResult result = createSearchResult(
                createSearchResultEntry(
                        "ou=People,dc=btmatthews,dc=com",
                        "ou", "People",
                        "objectclass", "organizationalUnit"));
        when(connection.search(any(SearchRequest.class))).thenReturn(result);
        final File outputFile = outputFolder.newFile();
        final OutputStream outputStream = new FileOutputStream(outputFile);
        formatHandler.dump(connection, "dc=btmatthews,dc=com", "(objectclass=*)", outputStream, logger);
        assertTrue(outputFile.exists());
        // TODO Find a good way to check file contents
    }

    /**
     * Verify that that the LDIF file is created correctly when dumping search result with two entries.
     *
     * @throws Exception If there was a problem executing the test case.
     */
    @Test
    public void twoItemsInDump() throws Exception {
        final SearchResult result = createSearchResult(
                createSearchResultEntry(
                        "ou=People,dc=btmatthews,dc=com",
                        "ou", "People",
                        "objectclass", "organizationalUnit"),
                createSearchResultEntry(
                        "cn=Bart Simpson,ou=People,dc=btmatthews,dc=com",
                        "cn", "Bart Simpson",
                        "sn", "Simpson",
                        "givenName", "Bart",
                        "uid", "bsimpson",
                        "objectclass", "inetOrgPerson"));
        when(connection.search(any(SearchRequest.class))).thenReturn(result);
        final File outputFile = outputFolder.newFile();
        final OutputStream outputStream = new FileOutputStream(outputFile);
        formatHandler.dump(connection, "dc=btmatthews,dc=com", "(objectclass=*)", outputStream, logger);
        assertTrue(outputFile.exists());
        // TODO Find a good way to check file contents
    }
}
