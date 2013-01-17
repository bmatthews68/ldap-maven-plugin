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

package com.btmatthews.maven.plugins.ldap;

import com.unboundid.ldap.sdk.Entry;

import java.io.IOException;

/**
 * Implemented by objects that write directory entries to DSML or LDIF files.
 *
 * @author <a href="mailto:brian@btmatthews.com">Brian Matthews</a>
 * @since 1.2.0
 */
public interface FormatWriter {

    /**
     * Write the directory entry to the underlying output stream in the appropriate format.
     *
     * @param entry The directory entry.
     * @throws IOException If there was an error writing to the underlying output stream.
     */
    void printEntry(Entry entry) throws IOException;

    /**
     * Close the writer but not the underlying output stream.
     *
     * @throws IOException If there was a problem closing the writer.
     */
    void close() throws IOException;
}
