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

import com.unboundid.ldap.sdk.*;
import com.unboundid.ldif.LDIFChangeRecord;
import com.unboundid.ldif.LDIFException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * @author <a href="mailto:brian@btmatthews.com">Brian Matthews</a>
 * @since 1.2.0
 */
public abstract class AbstractFormatHandler implements FormatHandler {

    /**
     * @param connection
     * @param inputStream
     * @param ignoreErrors If {@code true}
     * @param logger       Used to log information or error messages.
     */
    @Override
    public final void load(final LDAPInterface connection,
                           final InputStream inputStream,
                           final boolean ignoreErrors,
                           final FormatLogger logger) {
        final FormatReader reader = openReader(inputStream, logger);
        if (reader != null) {
            try {
                for (; ; ) {
                    try {
                        final LDIFChangeRecord record = reader.nextRecord();
                        if (record == null) {
                            break;
                        } else {
                            record.processChange(connection);
                        }
                    } catch (final LDIFException e) {
                        if (!ignoreErrors || !e.mayContinueReading()) {
                            break;
                        }
                    } catch (final LDAPException e) {
                        if (!ignoreErrors) {
                            break;
                        }
                    }
                }
            } catch (final IOException e) {
            } finally {
                try {
                    reader.close();
                } catch (final IOException e) {
                }
            }
        }
    }

    /**
     * @param connection   The connection to the LDAP directory server.
     * @param base         The base DN from which to start the search.
     * @param filter       Query used to filter the directory entries.
     * @param outputStream The output stream to which the directory entries are to be written.
     * @param logger       Used to log information or error messages.
     */
    @Override
    public final void dump(final LDAPInterface connection,
                           final String base,
                           final String filter,
                           final OutputStream outputStream,
                           final FormatLogger logger) {
        final FormatWriter ldapWriter = createWriter(outputStream, logger);
        try {
            try {
                final SearchRequest request = new SearchRequest(base, SearchScope.SUB, Filter.create(filter));
                final SearchResult result = connection.search(request);
                if (result.getResultCode() == ResultCode.SUCCESS) {
                    final List<SearchResultEntry> entries = result.getSearchEntries();
                    if (entries != null) {
                        for (final SearchResultEntry entry : entries) {
                            ldapWriter.printEntry(entry);
                        }
                    }
                } else {
                    logger.logError("Search operation failed");
                }
            } catch (final LDAPException e) {
                logger.logError("", e);
            } finally {
                ldapWriter.close();
            }
        } catch (final IOException e) {
            logger.logError("", e);
        }
    }

    /**
     * @param outputStream
     * @param logger       Used to log information or error messages.
     * @return
     */
    protected abstract FormatWriter createWriter(OutputStream outputStream, FormatLogger logger);

    /**
     * @param inputStream
     * @param logger       Used to log information or error messages.
     * @return
     */
    protected abstract FormatReader openReader(InputStream inputStream, FormatLogger logger);
}
