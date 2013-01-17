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

/**
 * The API used by {@link FormatHandler}, {@link FormatReader} and {@link FormatWriter} implementations to log
 * information and error messages.
 *
 * @suthor <a href="mailto:brian@btmatthews.com">Brian Matthews</a>
 * @since 1.2.0
 */
public interface FormatLogger {

    /**
     * Write an information message to the log file.
     *
     * @param message The message.
     */
    void logInfo(String message);

    /**
     * Write an error message to the log file.
     *
     * @param message The message.
     */
    void logError(String message);

    /**
     * Write an error message with an exception stack trace to the log file.
     *
     * @param message   The message.
     * @param exception The exception.
     */
    void logError(String message, Throwable exception);
}
