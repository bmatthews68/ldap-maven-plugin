/*
 * Copyright 2008-2016 Brian Thomas Matthews
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

import com.btmatthews.maven.plugins.ldap.FormatHandler;
import com.btmatthews.maven.plugins.ldap.dsml.DSMLFormatHandler;
import com.btmatthews.maven.plugins.ldap.ldif.LDIFFormatHandler;
import com.unboundid.ldap.sdk.LDAPConnection;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.*;

/**
 * This is the abstract base class for all Mojos in the ldap-maven-plugin plugin
 * that dump content from the directory server. Concrete classes must implement
 * the getLDAPWriter() method to return the writer that will format the output
 * appropriately.
 *
 * @author <a href="mailto:brian@btmatthews.com">Brian Matthews</a>
 * @since 1.2.0
 */
@Mojo(name = "dump")
public final class DumpMojo extends AbstractLDAPMojo {
    /**
     * Handler used to dump LDAP directory entries to DSML files.
     */
    private final FormatHandler dsmlFormatHandler = new DSMLFormatHandler();
    /**
     * Handler used to dump LDAP directory entries to LDIF files.
     */
    private final FormatHandler ldifFormatHandler = new LDIFFormatHandler();
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
     * The output file format.
     * <ul>
     * <li>ldif</li>
     * <li>dsml</li>
     * </ul>
     */
    @Parameter(defaultValue = "ldif")
    private String format;

    /**
     * Execute the plugin goal by dumping the matching directory entries to a file in the specified format.
     *
     * @throws MojoExecutionException If something unexpected happens.
     */
    public final void execute() throws MojoExecutionException {
        final File outputFile = new File(outputDirectory, filename);
        if (outputDirectory.exists() || outputDirectory.mkdirs()) {
            try {
                final OutputStream outputStream = new FileOutputStream(outputFile);
                try {
                    final LDAPConnection connection = connect();
                    try {
                        final FormatHandler handler = getFormatHandler();
                        handler.dump(connection, searchBase, searchFilter, outputStream, this);
                    } finally {
                        connection.close();
                    }
                } finally {
                    try {
                        outputStream.close();
                    } catch (final IOException e) {
                    }
                }
            } catch (final FileNotFoundException e) {
            }
        }
    }

    /**
     * Get the appropriate format handler based on the output file format.
     *
     * @return The appropriate file handler or {@code null} if the output file format is not supported.
     */
    private FormatHandler getFormatHandler() {
        if (format.equals("dsml")) {
            return dsmlFormatHandler;
        } else if (format.equals("ldif")) {
            return ldifFormatHandler;
        } else {
            return null;
        }
    }
}
