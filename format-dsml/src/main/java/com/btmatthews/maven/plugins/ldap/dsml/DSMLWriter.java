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

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

/**
 * @author <a href="mailto:brian@btmatthews.com">Brian Matthews</a>
 * @since 1.2.0
 */
public final class DSMLWriter implements FormatWriter {

    /**
     * Used to write to the output stream.
     */
    private final PrintWriter writer;

    /**
     * Create {@link PrintWriter} that will be used to write the directory entries to the output stream and write the
     * opening &lt;dsml:dsml;&gt and &lt;dsml:directory-entry;&gt tags.
     *
     * @param outputStream The file output stream.
     * @throws IOException If there was a problem writing the openng tags.
     */
    public DSMLWriter(final OutputStream outputStream) throws IOException {
        writer = new PrintWriter(outputStream);
        writer.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        writer.println("<dsml:dsml xmlns:dsml=\"http://www.dsml.org/DSML\">");
        writer.println("\t<dsml:directory-entries>");
    }


    @Override
    public void printEntry(final Entry entry) {
        StringBuilder buffer = new StringBuilder("\t\t<dsml:entry dn=\"");
        buffer.append(entry.getDN());
        buffer.append("\">");
        writer.println(buffer.toString());
        final String[] values = entry.getAttributeValues("objectclass");
        if (values != null) {
            writer.println("\t\t\t<dsml:objectclass>");
            for (final String value : values) {
                buffer = new StringBuilder("\t\t\t\t<dsml:oc-value>");
                buffer.append(value);
                buffer.append("</dsml:oc-value>");
                writer.println(buffer.toString());
            }
            writer.println("\t\t\t</dsml:objectclass>");
        }
        for (final Attribute attribute : entry.getAttributes()) {
            final String name = attribute.getName();
            if (!name.equals("objectclass")) {
                buffer = new StringBuilder("\t\t\t<dsml:attr name=\"");
                buffer.append(name);
                buffer.append("\">");
                writer.println(buffer.toString());
                for (String value : attribute.getValues()) {
                    buffer = new StringBuilder("\t\t\t\t<dsml:value>");
                    buffer.append(value);
                    buffer.append("</dsml:value>");
                    writer.println(buffer.toString());
                }
                writer.println("\t\t\t</dsml:attr>");
            }
        }
        writer.println("\t\t</dsml:entry>");
    }

    @Override
    public void close() {
        try {
            writer.println("\t</dsml:directory-entries>\n");
            writer.println("</dsml:dsml>");
        } finally {
            writer.close();
        }
    }
}
