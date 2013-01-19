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

import com.btmatthews.maven.plugins.ldap.FormatReader;
import com.unboundid.ldap.sdk.ChangeType;
import com.unboundid.ldif.LDIFChangeRecord;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;

import static junit.framework.Assert.assertNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.*;

/**
 * Unit test the {@link LDIFFormatReader}.
 *
 * @author <a href="mailto:brian@btmatthews.com">Brian Matthews</a>
 * @since 1.2.0
 */
public class TestLDIFFormatReader {

    /**
     * Verify that the {@link IOException} thrown by the {@link InputStream} is propagated.
     *
     * @throws Exception Expecting an {@link IOException} all other exceptions are unexpected.
     */
    @Test(expected = IOException.class)
    public void handlesIOException() throws Exception {
        final InputStream inputStream = mock(InputStream.class);
        when(inputStream.available()).thenReturn(1000);
        doThrow(IOException.class).when(inputStream).read(any(byte[].class), anyInt(), anyInt());
        final FormatReader reader = new LDIFFormatReader(inputStream);
        reader.nextRecord();
    }

    /**
     * Verify the {@link LDIFFormatReader} behaves correctly when loading an empty LDIF file.
     *
     * @throws Exception If there was an error in the test case.
     */
    @Test
    public void canReadEmptyFile() throws Exception {
        final InputStream inputStream = TestLDIFFormatReader.class.getResourceAsStream("empty.ldif");
        assertNotNull(inputStream);
        final FormatReader reader = new LDIFFormatReader(inputStream);
        final LDIFChangeRecord first = reader.nextRecord();
        assertNull(first);
        reader.close();
        inputStream.close();
    }

    /**
     * Verify the {@link LDIFFormatReader} behaves correctly when loading a LDIF file with a single
     * change record.
     *
     * @throws Exception If there was an error in the test case.
     */
    @Test
    public void canReadFileWithOneItem() throws Exception {
        final InputStream inputStream = TestLDIFFormatReader.class.getResourceAsStream("one.ldif");
        assertNotNull(inputStream);
        final FormatReader reader = new LDIFFormatReader(inputStream);
        final LDIFChangeRecord first = reader.nextRecord();
        assertNotNull(first);
        assertEquals("ou=People,dc=btmatthews,dc=com", first.getDN());
        assertEquals(ChangeType.ADD, first.getChangeType());
        final LDIFChangeRecord second = reader.nextRecord();
        Assert.assertNull(second);
        reader.close();
        inputStream.close();
    }

    /**
     * Verify the {@link LDIFFormatReader} behaves correctly when loading a LDIF file with two
     * change records.
     *
     * @throws Exception If there was an error in the test case.
     */
    @Test
    public void canReadFileWithTwoItems() throws Exception {
        final InputStream inputStream = TestLDIFFormatReader.class.getResourceAsStream("two.ldif");
        assertNotNull(inputStream);
        final FormatReader reader = new LDIFFormatReader(inputStream);
        final LDIFChangeRecord first = reader.nextRecord();
        assertNotNull(first);
        assertEquals("ou=People,dc=btmatthews,dc=com", first.getDN());
        assertEquals(ChangeType.ADD, first.getChangeType());
        final LDIFChangeRecord second = reader.nextRecord();
        assertNotNull(second);
        assertEquals("cn=Bart Simpson,ou=People,dc=btmatthews,dc=com", second.getDN());
        assertEquals(ChangeType.ADD, second.getChangeType());
        final LDIFChangeRecord third = reader.nextRecord();
        Assert.assertNull(third);
        reader.close();
        inputStream.close();
    }

}
