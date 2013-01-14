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

import com.btmatthews.maven.plugins.ldap.FormatWriter;
import com.unboundid.ldap.sdk.Entry;
import com.unboundid.util.ByteStringBuffer;

import java.io.IOException;
import java.io.OutputStream;

/**
 * <a href="mailto:brian@btmatthews.com">Brian Matthews</a>
 * @since 1.2.0
 */
public final class LDIFFormatWriter implements FormatWriter {

    private final OutputStream ldifOutputStream;

    public LDIFFormatWriter(final OutputStream outputStream) {
        ldifOutputStream = outputStream;
    }

    @Override
    public void printEntry(final Entry entry) throws IOException {
        final ByteStringBuffer buffer = new ByteStringBuffer();
        entry.toLDIF(buffer, 77);
        ldifOutputStream.write(buffer.toByteArray());
    }

    @Override
    public void close() {
    }
}
