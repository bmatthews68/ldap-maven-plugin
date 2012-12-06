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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

import netscape.ldap.LDAPConnection;
import netscape.ldap.LDAPEntry;
import netscape.ldap.LDAPException;
import netscape.ldap.LDAPSearchResults;
import netscape.ldap.LDAPv2;
import netscape.ldap.util.LDAPWriter;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * This is the abstract base class for all Mojos in the ldap-maven-plugin plugin
 * that dump content from the directory server. Concrete classes must implement
 * the getLDAPWriter() method to return the writer that will format the output
 * appropriately.
 *
 * @author <a href="mailto:brian@btmatthews.com">Brian Matthews</a>
 * @since 1.0.0
 */
public abstract class AbstractLDAPDumperMojo extends AbstractLDAPMojo {
    /**
     * The search base.
     */
    @Parameter(required = true)
    private String searchBase;

    /**
     * The search filter.
     */
    @Parameter(defaultValue = "(objectclass=*)", required = true)
    private String searchFilter;

    /**
     * The target output directory.
     */
    @Parameter(defaultValue = "${project.build.directory}", required = true)
    private File outputDirectory;

    /**
     * The output file name.
     */
    @Parameter(required = true)
    private String filename;

    /**
     * Execute the plugin goal.
     *
     * @throws MojoExecutionException If something unexpected happens.
     */
    public final void execute() throws MojoExecutionException {
        final LDAPConnection connection = this.connect();
        try {
            final PrintWriter writer = this.getPrintWriter();
            final LDAPWriter ldapWriter = this.openLDAPWriter(writer);
            this.dump(connection, ldapWriter);
            this.closeLDAPWriter(writer, ldapWriter);
            writer.close();
        } finally {
            try {
                connection.disconnect();
            } catch (LDAPException e) {
                final String message = "Error disconnecting from the LDAP directory server";
                this.getLog().warn(message, e);
            }
        }
    }

    /**
     * Create the LDAP writer that will dump LDAP entries in appropriate format.
     *
     * @param writer The writer for the target output stream.
     * @return The LDAP writer.
     */
    protected abstract LDAPWriter openLDAPWriter(final PrintWriter writer);

    /**
     * Close the LDAP wrtier that was returned by openLDAPWriter.
     *
     * @param writer     The writer for the target output stream.
     * @param ldapWriter The LDAP writer.
     */
    protected abstract void closeLDAPWriter(final PrintWriter writer,
                                            final LDAPWriter ldapWriter);

    /**
     * Search the LDAP directory and dump the contents to a file.
     *
     * @param connection The connection to the LDAP directory server.
     * @param writer     The writer that is used to dump the content.
     * @throws MojoExecutionException If an error occurred generating the output file content.
     */
    private void dump(final LDAPConnection connection, final LDAPWriter writer)
            throws MojoExecutionException {
        try {
            final LDAPSearchResults results = connection.search(
                    this.searchBase, LDAPv2.SCOPE_SUB, this.searchFilter, null,
                    false);
            while (results.hasMoreElements()) {
                final LDAPEntry entry = results.next();
                this.getLog().info("Dumping: " + entry.getDN());
                writer.printEntry(entry);
            }
        } catch (LDAPException e) {
            final String message = "Error communicating with the LDAP directory";
            this.getLog().error(message, e);
            throw new MojoExecutionException(message, e);
        } catch (IOException e) {
            final String message = "Error dumping to the contents of the LDAP directory";
            this.getLog().error(message, e);
            throw new MojoExecutionException(message, e);
        }
    }

    /**
     * Create the output file and return a print writer encapsulating that file.
     *
     * @return The print writer.
     * @throws MojoExecutionException If the output file could not be created.
     */
    private PrintWriter getPrintWriter() throws MojoExecutionException {
        final File file;
        file = new File(this.outputDirectory, this.filename);
        try {
            file.createNewFile();
            final OutputStream outputStream = new FileOutputStream(file);
            return new PrintWriter(outputStream);
        } catch (IOException e) {
            final String message = "I/O error creating output file: "
                    + file.getAbsolutePath();
            this.getLog().error(message, e);
            throw new MojoExecutionException(message, e);
        }
    }
}
