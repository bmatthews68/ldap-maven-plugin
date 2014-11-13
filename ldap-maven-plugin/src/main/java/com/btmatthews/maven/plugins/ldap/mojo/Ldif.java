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

/**
 * Represents LDIF files located on the class path or file system.
 *
 * @author <a href="mailto:brian@btmatthews.com">Brian Matthews</a>
 * @since 1.2.0
 */
public final class Ldif extends AbstractSource {

    /**
     * Initialise the LDIF source.
     *
     * @param path The location of the LDIF file on the class path or file system.
     */
    public Ldif(final String path) {
        super(path);
    }

    public Ldif(){
    	super(null);
    }
}
