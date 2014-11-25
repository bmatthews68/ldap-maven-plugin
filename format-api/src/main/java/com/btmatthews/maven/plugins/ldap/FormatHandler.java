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
import com.unboundid.ldif.LDIFException;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * The interface that is implemented by objects that handle the DSML and LDIF file formats.
 *
 * @author <a href="mailto:brian@btmatthews.com">Brian Matthews</a>
 * @since 1.2.0
 */
public interface FormatHandler {

    /**
     * Reads directory entries from the input stream and loads them in the LDAP directory server.
     *
     * @param connection   The connection to the LDAP directory server.
     * @param inputStream  The input stream from which directory entries will be read.
     * @param ignoreErrors If {@code true} then loading will continue if an error occurs.
     * @param logger       Used to log information or error messages.
     */
    void load(LDAPInterface connection, InputStream inputStream, boolean ignoreErrors, FormatLogger logger, boolean throwLdapException) throws LDIFException,LDAPException;

    /**
     * Dump the results of a search against the LDAP directory server to an output stream.
     *
     * @param connection   The connection to the LDAP directory server.
     * @param base         The base DN from which to start the search.
     * @param filter       Query used to filter the directory entries.
     * @param outputStream The output stream to which the directory entries are to be written.
     * @param logger       Used to log information or error messages.
     */
    void dump(LDAPInterface connection, String base, String filter, OutputStream outputStream, FormatLogger logger);
}