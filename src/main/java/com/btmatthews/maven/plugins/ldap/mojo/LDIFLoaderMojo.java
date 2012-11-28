/*
 * Copyright 2008-2012 Brian Thomas Matthews
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

package com.btmatthews.maven.plugins.ldap.mojo;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import netscape.ldap.LDAPAttribute;
import netscape.ldap.LDAPAttributeSet;
import netscape.ldap.LDAPConnection;
import netscape.ldap.LDAPEntry;
import netscape.ldap.LDAPException;
import netscape.ldap.util.LDIF;
import netscape.ldap.util.LDIFAddContent;
import netscape.ldap.util.LDIFAttributeContent;
import netscape.ldap.util.LDIFContent;
import netscape.ldap.util.LDIFModDNContent;
import netscape.ldap.util.LDIFModifyContent;
import netscape.ldap.util.LDIFRecord;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * Implement the goal that loads a LDIF file.
 *
 * @author <a href="mailto:brian.matthews@btmatthews.com">Brian Matthews</a>
 * @version 1.0
 */
@Mojo(name = "ldif-load")
public final class LDIFLoaderMojo extends AbstractLDAPMojo {
    /**
     * The LDIF files to be processed.
     */
    @Parameter(required = true)
    private File[] ldapFiles;

    /**
     * Indicates if the plugin should continue if there is an error. The default
     * is to halt on error.
     */
    @Parameter(defaultValue = "false")
    private boolean continueOnError;

    /**
     * Execute the plugin goal.
     *
     * @throws MojoExecutionException If something unexpected happens.
     */
    public void execute() throws MojoExecutionException {
        // Connect to the LDAP apache

        final LDAPConnection connection = this.connect();

        // Process the LDIF files

        try {
            for (int i = 0; i < this.ldapFiles.length; ++i) {
                try {
                    this.getLog().info("Processing " + this.ldapFiles[i]);
                    final InputStream inputStream = new FileInputStream(this.ldapFiles[i]);
                    final DataInputStream dataInputStream = new DataInputStream(inputStream);
                    final LDIF ldif = new LDIF(dataInputStream);
                    LDIFRecord record = ldif.nextRecord();
                    while (record != null) {
                        this.processRecord(connection, record);
                        record = ldif.nextRecord();
                    }
                } catch (LDAPException e) {
                    if (!this.continueOnError) {
                        throw new MojoExecutionException("Error processing: " + this.ldapFiles[i], e);
                    } else {
                        this.getLog().warn("Ignoring error processing: " + this.ldapFiles[i], e);
                    }
                } catch (FileNotFoundException e) {
                    if (!this.continueOnError) {
                        throw new MojoExecutionException("File not found: " + this.ldapFiles[i], e);
                    } else {
                        this.getLog().warn("Skipping missing file: " + this.ldapFiles[i], e);
                    }
                } catch (IOException e) {
                    if (!this.continueOnError) {
                        throw new MojoExecutionException("Error reading from: " + this.ldapFiles[i], e);
                    } else {
                        this.getLog().warn("Ignoring error reading from: " + this.ldapFiles[i], e);
                    }
                }
            }
        } finally {

            // Disconnect from the LDAP Server

            try {
                connection.disconnect();
            } catch (LDAPException e) {
                this.getLog().warn("Ignoring error disconnecting from the LDAP server", e);
            }
        }
    }

    /**
     * Process a parsed LDIF record. Delegate to the appropriate handler for
     * each operation.
     *
     * @param connection The connection to the LDAP apache.
     * @param record     The parsed record.
     * @throws LDAPException If there was an LDAP error.
     */
    private void processRecord(final LDAPConnection connection,
                               final LDIFRecord record) throws LDAPException {
        final LDIFContent content = record.getContent();
        final int contentType = content.getType();
        switch (contentType) {
            case LDIFContent.MODDN_CONTENT:
                this.renameEntry(connection, record.getDN(), (LDIFModDNContent) content);
                break;
            case LDIFContent.MODIFICATION_CONTENT:
                this.modifyEntry(connection, record.getDN(), (LDIFModifyContent) content);
                break;
            case LDIFContent.DELETE_CONTENT:
                this.deleteEntry(connection, record.getDN());
                break;
            case LDIFContent.ADD_CONTENT:
                this.addEntry(connection, record.getDN(), ((LDIFAddContent) content).getAttributes());
                break;
            default:
                this.addEntry(connection, record.getDN(), ((LDIFAttributeContent) content).getAttributes());
                break;
        }
    }

    /**
     * Handle an LDIF add record.
     *
     * @param connection The connection to the LDAP apache.
     * @param entryDn    The distinguished name of the record being added.
     * @param attributes The attributes.
     * @throws LDAPException If the operation failed.
     */
    private void addEntry(final LDAPConnection connection,
                          final String entryDn, final LDAPAttribute[] attributes) throws LDAPException {
        this.getLog().info("Add Entry: " + entryDn);
        final LDAPAttributeSet attributeSet = new LDAPAttributeSet(attributes);
        final LDAPEntry entry = new LDAPEntry(entryDn, attributeSet);
        connection.add(entry);
    }

    /**
     * Handle an LDIF delete record.
     *
     * @param connection The connection to the LDAP apache.
     * @param entryDn    The distinguished name of the record to be deleted.
     * @throws LDAPException If the operation failed.
     */
    private void deleteEntry(final LDAPConnection connection,
                             final String entryDn) throws LDAPException {
        this.getLog().info("Deleting " + entryDn);
        connection.delete(entryDn);
    }

    /**
     * Handle an LDIF modification record.
     *
     * @param connection The connection to the LDAP apache.
     * @param entryDn    The distinguished name of the record being modified.
     * @param content    The details of the modification operation.
     * @throws LDAPException If the operation failed.
     */
    private void modifyEntry(final LDAPConnection connection,
                             final String entryDn, final LDIFModifyContent content) throws LDAPException {
        this.getLog().info("Modify Entry: " + entryDn);
        connection.modify(entryDn, content.getModifications());
    }

    /**
     * Handle a LDIF distinguished name change operation.
     *
     * @param connection The connection to the LDAP apache.
     * @param entryDn    The distinguished name of the record being renamed.
     * @param content    The details of the name change operation.
     * @throws LDAPException If the operation failed.
     */
    private void renameEntry(final LDAPConnection connection,
                             final String entryDn, final LDIFModDNContent content) throws LDAPException {
        this.getLog().info("Rename Entry: " + entryDn);
        connection.rename(entryDn, content.getRDN(), content.getNewParent(), content.getDeleteOldRDN());
    }
}
