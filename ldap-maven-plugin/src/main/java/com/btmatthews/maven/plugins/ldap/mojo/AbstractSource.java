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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * The abstract base class for DSML or LDIF source files located on the class path or file system.
 *
 * @author <a href="mailto:brian@btmatthews.com">Brian Matthews</a>
 * @since 1.2.0
 */
public abstract class AbstractSource implements Source {

    /**
     * The prefix for class path resources.
     */
    private static final String CLASSPATH_PREFIX = "classpath:";
    /**
     * The length of the prefix for class path resources.
     */
    private static final int CLASSPATH_PREFIX_LENGTH = 10;
    /**
     * The location of the source on the class path or file system.
     */
    private String path;

    /**
     * Initialise the source setting the location of the source on the class path or file system.
     *
     * @param path The source location.
     */
    protected AbstractSource(final String path) {
        this.path = path;
    }

    /**
     * Open the resource or file and return the input stream. If the path is prefixed with classpath: then it is treated
     * as a classpath resource. Otherwise, it is assumed to be a file system path.
     *
     * @return The input stream or {@code null} if the file or resource cannot be found.
     */
    @Override
    public InputStream open() {
        if (path.startsWith(CLASSPATH_PREFIX)) {
            final ClassLoader loader = Thread.currentThread().getContextClassLoader();
            return loader.getResourceAsStream(path.substring(CLASSPATH_PREFIX_LENGTH));
        } else {
            final File file = new File(path);
            try {
                return new FileInputStream(file);
            } catch (final FileNotFoundException e) {
                return null;
            }
        }
    }

    /**
     * Convert the source to a string.
     *
     * @return Returns the value of {@link #path}.
     */
    @Override
    public final String toString() {
        return path;
    }
}
