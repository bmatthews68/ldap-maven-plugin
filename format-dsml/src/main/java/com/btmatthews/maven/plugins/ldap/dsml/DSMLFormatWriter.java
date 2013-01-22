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

import com.btmatthews.maven.plugins.ldap.FormatWriter;
import com.unboundid.ldap.sdk.Attribute;
import com.unboundid.ldap.sdk.Entry;
import com.unboundid.util.ByteStringBuffer;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

/**
 * A {@link FormatWriter} that outputs LDAP directory entries to DSML formatted files.
 *
 * @author <a href="mailto:brian@btmatthews.com">Brian Matthews</a>
 * @since 1.2.0
 */
public final class DSMLFormatWriter implements FormatWriter {

    /**
     * The underlying output stream.
     */
    private final OutputStream ldifOutputStream;
    /**
     * The end of line marker.
     */
    private final String eol;

    /**
     * Create {@link PrintWriter} that will be used to write the directory entries to the output stream and write the
     * opening &lt;dsml:dsml;&gt and &lt;dsml:directory-entry;&gt tags.
     *
     * @param outputStream The file output stream.
     * @throws IOException If there was a problem writing the openng tags.
     */
    public DSMLFormatWriter(final OutputStream outputStream) throws IOException {
        ldifOutputStream = outputStream;
        eol = System.getProperty("line.separator", "\n");
        final ByteStringBuffer buffer = new ByteStringBuffer();
        buffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        buffer.append(eol);
        buffer.append("<dsml:dsml xmlns:dsml=\"http://www.dsml.org/DSML\">");
        buffer.append(eol);
        buffer.append("\t<dsml:directory-entries>");
        buffer.append(eol);
        ldifOutputStream.write(buffer.toByteArray());
    }

    /**
     * Writes an individual LDAP directory entry to the DSML file.
     *
     * @param entry The directory entry.
     * @throws IOException If there was a problem writing to the underlying output stream.
     */
    @Override
    public void printEntry(final Entry entry) throws IOException {
        final ByteStringBuffer buffer = new ByteStringBuffer();
        buffer.append("\t\t<dsml:entry dn=\"");
        buffer.append(entry.getDN());
        buffer.append("\">");
        buffer.append(eol);
        final String[] values = entry.getAttributeValues("objectclass");
        if (values != null) {
            buffer.append("\t\t\t<dsml:objectclass>");
            buffer.append(eol);
            for (final String value : values) {
                buffer.append("\t\t\t\t<dsml:oc-value>");
                buffer.append(value);
                buffer.append("</dsml:oc-value>");
                buffer.append(eol);
            }
            buffer.append("\t\t\t</dsml:objectclass>");
            buffer.append(eol);
        }
        for (final Attribute attribute : entry.getAttributes()) {
            final String name = attribute.getName();
            if (!name.equals("objectclass")) {
                buffer.append("\t\t\t<dsml:attr name=\"");
                buffer.append(name);
                buffer.append("\">");
                buffer.append(eol);
                for (final String value : attribute.getValues()) {
                    buffer.append("\t\t\t\t<dsml:value>");
                    buffer.append(value);
                    buffer.append("</dsml:value>");
                    buffer.append(eol);
                }
                buffer.append("\t\t\t</dsml:attr>");
                buffer.append(eol);
            }
        }
        buffer.append("\t\t</dsml:entry>");
        buffer.append(eol);
        ldifOutputStream.write(buffer.toByteArray());
    }

    /**
     * Close the {@link FormatWriter} writing the closing tags.
     *
     * @throws IOException If there was a problem writing to the underlying output stream.
     */
    @Override
    public void close() throws IOException {
        final ByteStringBuffer buffer = new ByteStringBuffer();
        buffer.append("\t</dsml:directory-entries>\n");
        buffer.append(eol);
        buffer.append("</dsml:dsml>");
        buffer.append(eol);
        ldifOutputStream.write(buffer.toByteArray());
    }
}
