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

package com.btmatthews.maven.plugins.ldap;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;

/**
 * @author <a href="mailto:brian@btmatthews.com">Brian Matthews</a>
 * @since 1.2.0
 */
public class TestUtils {

    private static int DEFAULT_PORT_RANGE_END = 49152;

    /**
     * Get a {@link URL} for referencing a file on the classpath.
     *
     * @param filename The filename.
     * @return The {@link URL}.
     */
    public static URL getURL(final String filename) {
        final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        return classLoader.getResource(filename);
    }

    /**
     * Get a {@link File} for referencing a file on the classpath.
     *
     * @param filename The filename.
     * @return The {@link File}.
     */
    public static File getFile(final String filename) throws URISyntaxException {
        final URL url = getURL(filename);
        return new File(url.toURI());
    }

    public static int[] getUnusedPorts(final int count, final int start) {
        return getUnusedPorts(count, start, DEFAULT_PORT_RANGE_END);
    }

    public static int[] getUnusedPorts(final int count, final int start, final int end) {
        final int[] ports = new int[count];
        int port = start;
        int index = 0;
        while (index < count && port < end) {
            port = getUnusedPort(port, end);
            if (port != -1) {
                ports[index++] = port++;
            } else {
                return Arrays.copyOf(ports, index);
            }
        }
        return ports;
    }

    public static int getUnusedPort(final int start) {
        return getUnusedPort(start, DEFAULT_PORT_RANGE_END);
    }

    public static int getUnusedPort(final int start, final int end) {
        int port = start;
        while (port < end) {
            final Socket socket = new Socket();
            try {
                socket.connect(new InetSocketAddress("localhost", port), 0);
                socket.setSoLinger(false, 0);
            } catch (final IOException e) {
                return port;
            } finally {
                try {
                    socket.close();
                } catch (final IOException e) {
                }
            }
            port++;
        }
        return -1;
    }
}
