/*
 * Copyright 2012 Brian Thomas Matthews
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

import com.btmatthews.utils.monitor.Monitor;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;

/**
 * This Mojo implements the stop-apache goal which terminates an embedded LDAP
 * apache.
 *
 * @author <a href="mailto:brian.matthews@btmatthews.com">Brian Matthews</a>
 * @version 1.0
 */
@Mojo(name = "stop")
public final class StopServerMojo extends AbstractServerMojo {

    /**
     * Stop a running ApacheDS server by sending a {@code stop} command to the monitor that is controlling that server.
     *
     * @throws MojoFailureException If there was an error stopping the embedded ApacheDS server.
     */
    @Override
    public void execute() throws MojoFailureException {
        final Monitor monitor = new Monitor(getMonitorKey(), getMonitorPort());
        monitor.sendCommand("stop", this);
    }
}
