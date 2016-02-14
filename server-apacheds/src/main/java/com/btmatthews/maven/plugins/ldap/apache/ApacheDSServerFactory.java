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

package com.btmatthews.maven.plugins.ldap.apache;

import com.btmatthews.utils.monitor.Server;
import com.btmatthews.utils.monitor.ServerFactory;

/**
 * Factory that creates instances of {@link ApacheDSServer}.
 *
 * @author <a href="mailto:brian@btmatthews.com">Brian Matthews</a>
 * @since 1.1.0
 */
public class ApacheDSServerFactory implements ServerFactory {

    /**
     * Return the name of the servers created by this factory.
     *
     * @return Always returns {@code apacheds}.
     */
    public String getServerName() {
        return "apacheds";
    }

    /**
     * Create new instance of {@link ApacheDSServer}.
     *
     * @return The newly create instance of {@link ApacheDSServer}.
     */
    public Server createServer() {
        return new ApacheDSServer();
    }
}
