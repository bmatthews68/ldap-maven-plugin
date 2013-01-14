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

import java.io.InputStream;

/**
 * Interface for LDAP directory entry sources.
 *
 * @author <a href="mailto:brian@btmatthews.com">Brian Matthews</a>
 * @since 1.2.0
 */
public interface Source {

    /**
     * Open the resource or file and return the input stream. If the path is prefixed with classpath: then it is treated
     * as a classpath resource. Otherwise, it is assumed to be a file system path.
     *
     * @return The input stream or {@code null} if the file or resource cannot be found.
     */
    InputStream open();
}
