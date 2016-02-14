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

package com.btmatthews.maven.plugins.ldap.ldif;

import com.btmatthews.maven.plugins.ldap.FormatWriter;
import com.unboundid.ldap.sdk.Entry;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.io.IOException;
import java.io.OutputStream;

import static com.btmatthews.maven.plugins.ldap.FormatTestUtils.createEntry;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Unit test the {@link LDIFFormatWriter}.
 *
 * @author <a href="mailto:brian@btmatthews.com">Brian Matthews</a>
 * @since 1.2.0
 */
public class TestLDIFFormatWriter {

    /**
     * Mock the underlying output stream.
     */
    @Mock
    private OutputStream outputStream;
    /**
     * The test fixture is an {@link LDIFFormatWriter}.
     */
    private FormatWriter formatWriter;

    /**
     * Prepare for test case execution by creating and initializing the mock objects and test fixtures.
     */
    @Before
    public void setUp() {
        initMocks(this);
        formatWriter = new LDIFFormatWriter(outputStream);
    }

    /**
     * Verify that any {@link IOException}s thrown by {@link LDIFFormatWriter#printEntry(com.unboundid.ldap.sdk.Entry)}
     * get propagated to the caller.
     *
     * @throws Exception If was an {@link IOException} which is expected and will be ingored by JUnit or there was
     *                   some unexpected problem executing the test case.
     */
    @Test(expected = IOException.class)
    public void propagatesIOExceptionFromOutputStream() throws Exception {
        doThrow(IOException.class).when(outputStream).write(any(byte[].class));
        final Entry entry = createEntry("ou=People,dc=btmatthews,dc=com", "ou", "People", "objectclass", "organizationalUnit");
        formatWriter.printEntry(entry);
    }

    /**
     * Verify that the {@link LDIFFormatWriter#printEntry(com.unboundid.ldap.sdk.Entry)} operation writes to the
     * underlying output stream.
     *
     * @throws Exception If there was a problem executing the test case.
     */
    @Test
    public void writesEntryToOuputStream() throws Exception {
        final Entry entry = createEntry("ou=People,dc=btmatthews,dc=com", "ou", "People", "objectclass", "organizationalUnit");
        formatWriter.printEntry(entry);
        verify(outputStream).write(any(byte[].class));
        verifyNoMoreInteractions(outputStream);
    }

    /**
     * Verify that the close operation does not close the underlying output stream.
     *
     * @throws Exception If there was a problem executing the test case.
     */
    @Test
    public void closeDoesNotAffectOutputStream() throws Exception {
        formatWriter.close();
        verifyNoMoreInteractions(outputStream);
    }

}
