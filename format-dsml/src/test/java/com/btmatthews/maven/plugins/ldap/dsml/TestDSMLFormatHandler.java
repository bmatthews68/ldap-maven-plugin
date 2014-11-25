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

import com.btmatthews.maven.plugins.ldap.AddRequestMatcher;
import com.btmatthews.maven.plugins.ldap.FormatHandler;
import com.btmatthews.maven.plugins.ldap.FormatLogger;
import com.unboundid.ldap.sdk.Entry;
import com.unboundid.ldap.sdk.LDAPInterface;
import com.unboundid.ldap.sdk.SearchRequest;
import com.unboundid.ldap.sdk.SearchResult;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mock;
import org.xml.sax.InputSource;

import java.io.*;

import static com.btmatthews.maven.plugins.ldap.FormatTestUtils.createSearchResult;
import static com.btmatthews.maven.plugins.ldap.FormatTestUtils.createSearchResultEntry;
import static org.custommonkey.xmlunit.XMLAssert.assertXMLEqual;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Unit test the {@link DSMLFormatHandler}.
 *
 * @author <a href="mailto:brian@btmatthews.com">Brian Matthews</a>
 * @since 1.2.0
 */
public class TestDSMLFormatHandler {

    /**
     *
     */
    @Rule
    public TemporaryFolder outputFolder = new TemporaryFolder();
    /**
     *
     */
    private FormatHandler formatHandler;
    /**
     *
     */
    @Mock
    private LDAPInterface connection;
    /**
     *
     */
    @Mock
    private FormatLogger logger;

    /**
     *
     */
    @Before
    public void setUp() {
        initMocks(this);
        formatHandler = new DSMLFormatHandler();
        XMLUnit.setIgnoreWhitespace(true);
        XMLUnit.setIgnoreComments(true);
        XMLUnit.setIgnoreAttributeOrder(true);
    }

    /**
     * @throws Exception
     */
    @Test
    public void loadFully() throws Exception {
        final InputStream inputStream = TestDSMLFormatHandler.class.getResourceAsStream("load.dsml");
        try {
            formatHandler.load(connection, inputStream, false, logger, false);
        } finally {
            inputStream.close();
        }
        verify(connection).add(argThat(new AddRequestMatcher(new Entry("ou=People,dc=btmatthews,dc=com"))));
        verify(connection).add(argThat(new AddRequestMatcher(new Entry("cn=Bart Simpson,ou=People,dc=btmatthews,dc=com"))));
    }

    /**
     * @throws Exception
     */
    @Test
    public void loadIgnoresErrors() throws Exception {
        final InputStream inputStream = TestDSMLFormatHandler.class.getResourceAsStream("haserrors.dsml");
        try {
            formatHandler.load(connection, inputStream, true, logger, false);
        } finally {
            inputStream.close();
        }
    }

    /**
     * @throws Exception
     */
    @Test
    public void loadStopsUponError() throws Exception {
        final InputStream inputStream = TestDSMLFormatHandler.class.getResourceAsStream("haserrors.dsml");
        try {
            formatHandler.load(connection, inputStream, false, logger, false);
        } finally {
            inputStream.close();
        }
    }

    /**
     * @throws Exception
     */
    @Test
    public void noDataInDump() throws Exception {
        final SearchResult results = createSearchResult();
        when(connection.search(any(SearchRequest.class))).thenReturn(results);

        final File outputFile = outputFolder.newFile();
        final OutputStream outputStream = new FileOutputStream(outputFile);
        formatHandler.dump(connection, "dc=btmatthews,dc=com", "(objectclass=*)", outputStream, logger);
        final InputStream expected = TestDSMLFormatHandler.class.getResourceAsStream("empty.dsml");
        assertXMLEqual(new InputSource(expected), new InputSource(new FileInputStream(outputFile)));
    }

    /**
     * Export a single directory entry to the DSML file.
     *
     * @throws Exception If there was an unexpected exception.
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
        final InputStream expected = TestDSMLFormatHandler.class.getResourceAsStream("one.dsml");
        assertXMLEqual(new InputSource(expected), new InputSource(new FileInputStream(outputFile)));
    }
}
