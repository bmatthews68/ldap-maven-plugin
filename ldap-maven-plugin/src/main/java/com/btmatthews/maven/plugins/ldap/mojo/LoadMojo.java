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

package com.btmatthews.maven.plugins.ldap.mojo;

import com.btmatthews.maven.plugins.ldap.FormatHandler;
import com.btmatthews.maven.plugins.ldap.dsml.DSMLFormatHandler;
import com.btmatthews.maven.plugins.ldap.ldif.LDIFFormatHandler;
import com.unboundid.ldap.sdk.LDAPConnection;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.IOException;
import java.io.InputStream;

/**
 * Implement the goal that loads a LDIF or DSML file into the LDAP directory server.
 *
 * @author <a href="mailto:brian.matthews@btmatthews.com">Brian Matthews</a>
 * @since 1.2.0
 */
@Mojo(name = "load")
public final class LoadMojo extends AbstractLDAPMojo {

    /**
     * Handler used to load LDAP directory entries from DSML files.
     */
    private final FormatHandler dsmlFormatHandler = new DSMLFormatHandler();
    /**
     * Handler used to load LDAP directory entries from LDIF files.
     */
    private final FormatHandler ldifFormatHandler = new LDIFFormatHandler();
    /**
     * The LDIF and DSML files to be processed.
     */
    @SuppressWarnings("MismatchedReadAndWriteOfArray")
    @Parameter(required = true)
    private Source[] sources;
    /**
     * Indicates if the plugin should continue if there is an error. The default
     * is to halt on error.
     */
    @Parameter(defaultValue = "false")
    private boolean continueOnError;

    /**
     * Execute the plugin goal iterating over the list of source files and loading the LDAP directory entries from
     * each file using the appropriate handler.
     *
     * @throws MojoExecutionException If there was an error executing the plugin goal.
     */
    public void execute() throws MojoExecutionException {
        if (!isSkip()){
            final LDAPConnection connection = connect();
            try {
                for (final Source source : sources) {
                    try {
                        getLog().info("Processing input source: " + source);
                        final FormatHandler handler = getFormatHandler(source);
                        if (handler == null) {
                            getLog().warn("No handler for input source: " + source);
                        } else {
                            final InputStream inputStream = source.open();
                            if (inputStream == null) {
                                if (!this.continueOnError) {
                                    throw new MojoExecutionException("Cannot open source for reading: " + source);
                                } else {
                                    getLog().warn("Skipping source that could not be opened for reading: " + source);
                                }
                            } else {
                                try {
                                    handler.load(connection, source.open(), continueOnError, this);
                                } finally {
                                    inputStream.close();
                                }
                            }
                        }
                    } catch (final IOException e) {
                        if (!this.continueOnError) {
                            throw new MojoExecutionException("Error closing input source: " + source, e);
                        } else {
                            this.getLog().warn("Ignoring error closing input source: " + source, e);
                        }
                    }
                }
            } finally {
                connection.close();
            }
        }
    }

    /**
     * Determine which format handler to use for a source file. If the source file is DSML then {@link #dsmlFormatHandler}
     * will be used and if it is LDIF then {@link #ldifFormatHandler}.
     *
     * @param source Describes the source file.
     * @return The appropriate format handler or {@code null} if the source is not supported.
     */
    private FormatHandler getFormatHandler(final Source source) {
        if (source instanceof Dsml) {
            return dsmlFormatHandler;
        } else if (source instanceof Ldif) {
            return ldifFormatHandler;
        } else {
            return null;
        }
    }
}
