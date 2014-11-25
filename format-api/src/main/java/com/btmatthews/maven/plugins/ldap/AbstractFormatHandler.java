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
 * Abstract base class for {@link FormatHandler} objects that import/export directory entries to/from LDAP directory
 * servers in LDIF and DSML format.
 *
 * @author <a href="mailto:brian@btmatthews.com">Brian Matthews</a>
 * @since 1.2.0
 */
public abstract class AbstractFormatHandler implements FormatHandler {

    /**
     * Reads directory entries from the input stream and loads them in the LDAP directory server.
     *
     * @param connection         The connection to the LDAP directory server.
     * @param inputStream        The input stream from which directory entries will be read.
     * @param ignoreErrors       If {@code true} then loading will continue if an error occurs.
     * @param logger             Used to log information or error messages.
     * @param throwLdapException Used to throw Ldap exception
     */
    @Override
    public final void load(final LDAPInterface connection,
                           final InputStream inputStream,
                           final boolean ignoreErrors,
                           final FormatLogger logger, 
                           final boolean throwLdapException) throws LDIFException,LDAPException{
        final FormatReader reader = openReader(inputStream, logger);
        if (reader != null) {
            try {
                boolean keepReading = true;
                do {
                    try {
                        final LDIFChangeRecord record = reader.nextRecord();
                        if (record == null) {
                            keepReading = false;
                        } else {
                            record.processChange(connection);
                        }
                    } catch (final LDIFException e) {
                        logger.logError("Error parsing directory entry read from the input stream", e);
                        if (!ignoreErrors || !e.mayContinueReading()) {
                            keepReading = false;
                            if (throwLdapException){
                                throw e;
                            }
                        }
                    } catch (final LDAPException e) {
                        logger.logError("Error loading directory entry into the LDAP directory server", e);
                        if (!ignoreErrors) {
                            keepReading = false;
                            if (throwLdapException){
                                throw e;
                            }
                        }
                    }
                } while (keepReading);
            } catch (final IOException e) {
                logger.logError("I/O error reading directory entry from input stream", e);
            } finally {
                try {
                    reader.close();
                } catch (final IOException e) {
                    logger.logError("I/O error closing the input stream reader", e);
                }
            }
        }
    }

    /**
     * Dump the results of a search against the LDAP directory server to an output stream.
     *
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
        if (ldapWriter == null) {
            logger.logError("Error creating writer for output stream");
        } else {
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
                        } else {
                            logger.logInfo("Search did not return any directory entries");
                        }
                    } else {
                        logger.logError("Search operation failed");
                    }
                } catch (final LDAPException e) {
                    logger.logError("Error searching the LDAP directory", e);
                } finally {
                    ldapWriter.close();
                }
            } catch (final IOException e) {
                logger.logError("Error writing directory entry to the output stream", e);
            }
        }
    }

    /**
     * Create a writer that formats the directory entry before writing it to the output stream.
     *
     * @param outputStream The target output stream.
     * @param logger       Used to log information or error messages.
     * @return A {@link FormatWriter} object.
     */
    protected abstract FormatWriter createWriter(OutputStream outputStream, FormatLogger logger);

    /**
     * Create a reader to parse the directory entries as they are read from the input stream.
     *
     * @param inputStream The source input stream.
     * @param logger      Used to log information or error messages.
     * @return A {@link FormatReader} object.
     */
    protected abstract FormatReader openReader(InputStream inputStream, FormatLogger logger);
}
