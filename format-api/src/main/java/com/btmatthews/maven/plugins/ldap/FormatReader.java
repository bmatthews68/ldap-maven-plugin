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

import com.unboundid.ldif.LDIFChangeRecord;
import com.unboundid.ldif.LDIFException;

import java.io.IOException;

/**
 * Implemented by objects that read change records from DSML or LDIF files.
 *
 * @suthor <a href="mailto:brian@btmatthews.com">Brian Matthews</a>
 * @since 1.2.0
 */
public interface FormatReader {

    /**
     * Read the next change record from the underlying input stream.
     *
     * @return The next change record or {@code null} if the end of the input stream has been reached.
     * @throws IOException   If there was an error reading from the input stream.
     * @throws LDIFException If there was an error parsing the data read from the input stream.
     */
    LDIFChangeRecord nextRecord() throws IOException, LDIFException;

    /**
     * Close the reader but not the underlying input stream.
     *
     * @throws IOException If there was an error closing the reader.
     */
    void close() throws IOException;
}
