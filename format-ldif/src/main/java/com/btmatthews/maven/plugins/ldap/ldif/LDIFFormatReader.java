/*
 * Copyright 2013-2016 Brian Thomas Matthews
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
import com.unboundid.ldif.LDIFChangeRecord;
import com.unboundid.ldif.LDIFException;
import com.unboundid.ldif.LDIFReader;

import java.io.IOException;
import java.io.InputStream;

/**
 * This {@link FormatReader} reads LDIF change records from an underlying input stream.
 *
 * @author <a href="mailto:brian@btmatthews.com">Brian Matthews</a>
 * @since 1.2.0
 */
public final class LDIFFormatReader implements FormatReader {

    /**
     * The object that reads and parses LDIF change records from the
     * underlying input stream.
     */
    private final LDIFReader reader;

    /**
     * Initialize the {@link LDIFFormatReader} by creating an {@link LDIFReader} that
     * will read and parse LDIF change records from the {@code inputStream}.
     *
     * @param inputStream The underlying input stream.
     */
    public LDIFFormatReader(final InputStream inputStream) {
        reader = new LDIFReader(inputStream);
    }

    /**
     * Read the next change record from the underlying LDIF input stream.
     *
     * @return The next change record or {@code null} if the end of the input stream has been reached.
     * @throws IOException   If there was an error reading from the input stream.
     * @throws LDIFException If there was an error parsing the data read from the input stream.
     */
    public LDIFChangeRecord nextRecord() throws IOException, LDIFException {
        return reader.readChangeRecord();
    }

    /**
     * Close the {@link LDIFFormatReader} by closing the {@link LDIFReader} object used to
     * read and parse the LDIF change records.
     *
     * @throws IOException If there was a problem closing the {@link LDIFReader}.
     */
    public void close() throws IOException {
        reader.close();
    }
}
