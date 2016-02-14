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

import com.btmatthews.maven.plugins.ldap.FormatWriter;
import com.unboundid.ldap.sdk.Entry;
import com.unboundid.util.ByteStringBuffer;

import java.io.IOException;
import java.io.OutputStream;

/**
 * This {@link FormatWriter} writes LDAP entries to an underlying output stream.
 *
 * @author <a href="mailto:brian@btmatthews.com">Brian Matthews</a>
 * @since 1.2.0
 */
public final class LDIFFormatWriter implements FormatWriter {

    /**
     * The underlying output stream.
     */
    private final OutputStream ldifOutputStream;
    /**
     * The system dependent end of line marker.
     */
    private final byte[] eol;
    /**
     * Used by the {@link #printEntry(com.unboundid.ldap.sdk.Entry)} method to determine if the
     * first entry has already been output.
     */
    private boolean first = true;

    /**
     * Initialize the {@link LDIFFormatWriter} by keeping a reference to the underlying output stream.
     *
     * @param outputStream The underlying output stream.
     */
    public LDIFFormatWriter(final OutputStream outputStream) {
        ldifOutputStream = outputStream;
        final String eolString = System.getProperty("line.separator", "\n");
        eol = eolString.getBytes();
    }

    /**
     * Write the LDAP entry to the underlying output stream in LDIF format.
     *
     * @param entry The directory entry.
     * @throws IOException If there was a problem writing to the underlying output stream.
     */
    public void printEntry(final Entry entry) throws IOException {
        if (entry != null) {
            final ByteStringBuffer buffer = new ByteStringBuffer();
            entry.toLDIF(buffer, 77);
            if (!first) {
                ldifOutputStream.write(eol);
            } else {
                first = false;
            }
            ldifOutputStream.write(buffer.toByteArray());
        }
    }

    /**
     * Close the {@link LDIFFormatWriter} which does not require any processing.
     */
    public void close() {
    }
}
