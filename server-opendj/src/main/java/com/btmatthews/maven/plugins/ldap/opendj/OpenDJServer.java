/*
 * Copyright 2012-2013 Brian Thomas Matthews
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

package com.btmatthews.maven.plugins.ldap.opendj;

import com.btmatthews.maven.plugins.ldap.AbstractLDAPServer;
import com.btmatthews.utils.monitor.Logger;
import org.apache.commons.io.IOUtils;
import org.opends.messages.Message;
import org.opends.server.api.Backend;
import org.opends.server.backends.MemoryBackend;
import org.opends.server.config.ConfigException;
import org.opends.server.core.DirectoryServer;
import org.opends.server.types.*;
import org.opends.server.util.EmbeddedUtils;
import org.opends.server.util.LDIFException;
import org.opends.server.util.LDIFReader;
import org.opends.server.util.StaticUtils;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @author <a href="mailto:brian@btmatthews.com">Brian Matthews</a>
 * @since 1.1.1
 */
public final class OpenDJServer extends AbstractLDAPServer {

    private static boolean ensureDirectoryExists(final File parent, final String child) {
        return ensureDirectoryExists(new File(parent, child));
    }

    private static boolean ensureDirectoryExists(final File directory) {
        return directory.exists() ||directory.mkdirs();
    }

    @Override
    public void start(final Logger logger) {
        try {
            logger.logInfo("Starting OpenDJ server");
            if (ensureDirectoryExists(getWorkingDirectory(), "locks")
                    && ensureDirectoryExists(getWorkingDirectory(), "logs")
                    && !EmbeddedUtils.isRunning()) {
                copyTree("opendj/", getWorkingDirectory());
                final DirectoryEnvironmentConfig envConfig = new DirectoryEnvironmentConfig();
                envConfig.setServerRoot(getWorkingDirectory());
                envConfig.setConfigFile(new File(getWorkingDirectory(), "config/config.ldif"));
                envConfig.setDisableConnectionHandlers(false);
                envConfig.setMaintainConfigArchive(false);
                logger.logInfo("Starting server...");
                EmbeddedUtils.startServer(envConfig);
                logger.logInfo("Initializing backend...");
                final Backend backend = initializeTestBackend(DN.decode(getRoot()));
                logger.logInfo("Loading...");
                loadLdif(backend);
                logger.logInfo("Started OpenDJ server");
            } else {
                logger.logInfo("OpenDJ server is already running");
            }
        } catch (final InitializationException e) {
            logger.logError("Error starting OpenDJ server", e);
        } catch (final ConfigException e) {
            logger.logError("Error starting OpenDJ server", e);
        } catch (final IOException e) {
            logger.logError("Error starting OpenDJ server", e);
        } catch (final URISyntaxException e) {
            logger.logError("Error starting OpenDJ server", e);
        } catch (final DirectoryException e) {
            logger.logError("Error starting OpenDJ server", e);
        }
    }

    @Override
    public void stop(final Logger logger) {
        logger.logInfo("Stopping OpenDJ server");
        EmbeddedUtils.stopServer(OpenDJServer.class.getName(), Message.EMPTY);
        logger.logInfo("Stopped OpenDJ server");
    }

    private void loadLdif(final Backend backend)
            throws DirectoryException, IOException {
        final LDIFImportConfig importConfig = new LDIFImportConfig(getLdifFile().openStream());
        importConfig.setAppendToExistingData(true);
        importConfig.setReplaceExistingEntries(true);
        importConfig.setCompressed(false);
        importConfig.setEncrypted(false);
        importConfig.setValidateSchema(false);
        importConfig.setSkipDNValidation(false);
        importConfig.writeRejectedEntries(System.err);
        final LDIFReader reader = new LDIFReader(importConfig);
        boolean keepReading = true;
        do {
            try {
                final Entry entry = reader.readEntry();
                if (entry == null) {
                    keepReading = false;
                } else {
                    try {
                        backend.addEntry(entry, null);
                    } catch (final CanceledOperationException e) {
                    }
                }
            } catch (final LDIFException e) {
                if (!e.canContinueReading()) {
                    keepReading = false;
                }
            }
        } while (keepReading);
    }

    private Backend initializeTestBackend(final DN baseDN)
            throws DirectoryException, ConfigException, InitializationException {
        final MemoryBackend memoryBackend = new MemoryBackend();
        memoryBackend.setBackendID("default");
        memoryBackend.setBaseDNs(new DN[]{baseDN});
        memoryBackend.supportsControl("1.2.840.113556.1.4.473");
        memoryBackend.initializeBackend();
        final Entry e = StaticUtils.createEntry(baseDN);
        memoryBackend.addEntry(e, null);
        DirectoryServer.registerBackend(memoryBackend);
        return memoryBackend;
    }

    private void copyTree(final String path, final File destinationFolder) throws URISyntaxException, IOException {
        final ClassLoader classLoader = OpenDJServer.class.getClassLoader();
        URL dirURL = classLoader.getResource(path);
        if (dirURL != null && dirURL.getProtocol().equals("file")) {
            copyFolder(new File(dirURL.toURI()), destinationFolder);
        } else {
            if (dirURL == null) {
                dirURL = classLoader.getResource("com/btmatthews/maven/plugins/ldap/opendj/OpenDJServer.class");
            }
            if (dirURL != null && dirURL.getProtocol().equals("jar")) {
                final String jarPath = dirURL.getPath().substring(5, dirURL.getPath().indexOf("!"));
                final JarFile jar = new JarFile(URLDecoder.decode(jarPath, "UTF-8"));
                copyJarFolder(jar, path, destinationFolder);
            }
        }
    }

    private void copyFolder(final File sourceFolder,
                            final File destinationFolder) throws IOException {
        if (ensureDirectoryExists(destinationFolder)) {
            for (final String filename : sourceFolder.list()) {
                final File sourceFile = new File(sourceFolder, filename);
                final File destinationFile = new File(destinationFolder, filename);
                if (sourceFile.isDirectory()) {
                    copyFolder(sourceFile, destinationFile);
                } else {
                    IOUtils.copy(new FileInputStream(sourceFile), new FileOutputStream(destinationFile));
                }
            }
        }
    }

    private void copyJarFolder(final JarFile jar,
                               final String path,
                               final File destinationFolder) throws IOException {
        if (ensureDirectoryExists(destinationFolder)) {
            final Enumeration<JarEntry> entries = jar.entries();
            while (entries.hasMoreElements()) {
                final JarEntry entry = entries.nextElement();
                final String name = entry.getName();
                if (name.startsWith(path)) {
                    final File destination = new File(destinationFolder, name.substring(path.length()));
                    if (entry.isDirectory()) {
                        ensureDirectoryExists(destination);
                    } else {
                        if (ensureDirectoryExists(destination.getParentFile())) {
                            final InputStream inputStream = jar.getInputStream(entry);
                            try {
                                IOUtils.copy(inputStream, new FileOutputStream(destination));
                            } finally {
                                inputStream.close();
                            }
                        }
                    }
                }
            }
        }
    }
}
