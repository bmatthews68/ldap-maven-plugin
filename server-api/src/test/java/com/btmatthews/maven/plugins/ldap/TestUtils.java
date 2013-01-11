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

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * @author <a href="mailto:brian@btmatthews.com">Brian Matthews</a>
 * @since 1.2.0
 */
public class TestUtils {

    /**
     * Get a {@link URL} for referencing a file on the classpath.
     *
     * @param filename The filename.
     * @return The {@link URL}.
     */
    public static final URL getURL(final String filename) {
        final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        return classLoader.getResource(filename);
    }

    /**
     * Get a {@link File} for referencing a file on the classpath.
     *
     * @param filename The filename.
     * @return The {@link File}.
     */
    public static final File getFile(final String filename) throws URISyntaxException {
        final URL url = getURL(filename);
        return new File(url.toURI());
    }
}
