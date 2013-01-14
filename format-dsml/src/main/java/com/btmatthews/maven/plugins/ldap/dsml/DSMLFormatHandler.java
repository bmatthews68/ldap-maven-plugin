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

package com.btmatthews.maven.plugins.ldap.dsml;

import com.btmatthews.maven.plugins.ldap.AbstractFormatHandler;
import com.btmatthews.maven.plugins.ldap.FormatLogger;
import com.btmatthews.maven.plugins.ldap.FormatReader;
import com.btmatthews.maven.plugins.ldap.FormatWriter;
import org.dom4j.DocumentException;
import org.jaxen.JaxenException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public final class DSMLFormatHandler extends AbstractFormatHandler {
    /**
     * Create the LDAP writer that will dump LDAP entries to a DSML file.
     *
     * @param outputStream The target output stream.
     * @param logger       Used to log information or error messages.
     * @return A {@link DSMLWriter} object.
     */
    @Override
    protected FormatWriter createWriter(final OutputStream outputStream,
                                        final FormatLogger logger) {
        try {
            return new DSMLWriter(outputStream);
        } catch (final IOException e) {
            logger.logError("Could not create and intialise the DSML writer", e);
        }
        return null;
    }

    /**
     * Create the LDAP reader that will load LDAP entries from a DSML file.
     *
     * @param inputStream The file input stream.
     * @param logger      Used to log information or error messages.
     * @return A {@link DSMLReader} object.
     */
    @Override
    protected FormatReader openReader(final InputStream inputStream,
                                      final FormatLogger logger) {
        try {
            return new DSMLReader(inputStream);
        } catch (final DocumentException e) {
            logger.logError("Error parsing DSML file", e);
        } catch (final JaxenException e) {
            logger.logError("Error processing DSML file", e);
        } catch (final IOException e) {
            logger.logError("Error reading DSML file", e);
        }
        return null;
    }
}