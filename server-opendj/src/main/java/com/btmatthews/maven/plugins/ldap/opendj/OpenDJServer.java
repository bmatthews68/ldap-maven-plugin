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
import org.opends.server.admin.std.server.BackendCfg;
import org.opends.server.api.Backend;
import org.opends.server.backends.MemoryBackend;
import org.opends.server.config.ConfigException;
import org.opends.server.core.DirectoryServer;
import org.opends.server.core.LockFileManager;
import org.opends.server.tools.BackendToolUtils;
import org.opends.server.types.*;
import org.opends.server.util.EmbeddedUtils;
import org.opends.server.util.StaticUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:brian@btmatthews.com">Brian Matthews</a>
 * @since 1.1.1
 */
public final class OpenDJServer extends AbstractLDAPServer {

    private static boolean ensureDirectoryExists(final File parent, final String child) {
        return ensureDirectoryExists(new File(parent, child));
    }

    private static boolean ensureDirectoryExists(final File directory) {
        if (directory.exists()) {
            return true;
        } else {
            return directory.mkdirs();
        }
    }

    @Override
    public void start(final Logger logger) {
        try {
            logger.logInfo("Starting OpenDJ server");
            if (ensureDirectoryExists(getWorkingDirectory(), "locks")
                    && ensureDirectoryExists(getWorkingDirectory(), "logs")
                    && !EmbeddedUtils.isRunning()) {
                final DirectoryEnvironmentConfig envConfig = new DirectoryEnvironmentConfig();
                envConfig.setServerRoot(getWorkingDirectory());
                final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
                final URL url = classLoader.getResource("opendj/config/config.ldif");
                if (url != null) {
                    final File source = new File(url.toURI());
                    copyFolder(source.getParentFile().getParentFile(), getWorkingDirectory());
                    envConfig.setConfigFile(new File(getWorkingDirectory(), "config/config.ldif"));
                    envConfig.setDisableConnectionHandlers(false);
                    envConfig.setMaintainConfigArchive(false);
                    EmbeddedUtils.startServer(envConfig);
                    loadLdif("default", DN.decode(getRoot()));
                    logger.logInfo("Started OpenDJ server");
                } else {
                    logger.logError("Cannot locate OpenDJ configuration resources");
                }
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

    private void loadLdif(final String backendID,
                          final DN baseDN)
            throws InitializationException, ConfigException, DirectoryException,
            URISyntaxException, IOException {
        final LDIFImportConfig importConfig = new LDIFImportConfig(getLdifFile().openStream());
        importConfig.setAppendToExistingData(true);
        importConfig.setReplaceExistingEntries(true);
        importConfig.setCompressed(false);
        importConfig.setEncrypted(false);
        importConfig.setValidateSchema(false);
        importConfig.setSkipDNValidation(false);
        final ArrayList<Backend> backendList = new ArrayList<Backend>();
        final ArrayList<BackendCfg> entryList = new ArrayList<BackendCfg>();
        final ArrayList<List<DN>> dnList = new ArrayList<List<DN>>();
        BackendToolUtils.getBackends(backendList, entryList, dnList);
        Backend backend = null;
        for (final Backend b : backendList) {
            if (backendID.equals(b.getBackendID())) {
                backend = b;
                break;
            }
        }
        if (backend == null) {
            backend = initializeTestBackend(true, baseDN, backendID);
            LDIFImportResult result = backend.importLDIF(importConfig);
            System.out.println("OpenDJ LDIF import result " + result);

        } else {
            String lockFile = LockFileManager.getBackendLockFileName(backend);
            StringBuilder failureReason = new StringBuilder();
            if (!LockFileManager.acquireExclusiveLock(lockFile, failureReason)) {
                throw new RuntimeException("OpenDJ cannot get lock the backend " + backend.getBackendID()
                        + " " + failureReason);
            }
            LDIFImportResult result = backend.importLDIF(importConfig);
            System.out.println("OpenDJ LDIF import result " + result);
            lockFile = LockFileManager.getBackendLockFileName(backend);
            failureReason = new StringBuilder();
            if (!LockFileManager.releaseLock(lockFile, failureReason)) {
                throw new RuntimeException("OpenDJ cannot release the lock the backend " + backend.
                        getBackendID() + " " + failureReason);
            }
        }
    }

    public Backend initializeTestBackend(final boolean createBaseEntry,
                                         final DN baseDN,
                                         final String backendId)
            throws DirectoryException, ConfigException, InitializationException {
        final MemoryBackend memoryBackend = new MemoryBackend();
        memoryBackend.setBackendID(backendId);
        memoryBackend.setBaseDNs(new DN[]{baseDN});
        memoryBackend.supportsControl("1.2.840.113556.1.4.473");
        memoryBackend.initializeBackend();
        DirectoryServer.registerBackend(memoryBackend);
        memoryBackend.clearMemoryBackend();
        if (createBaseEntry) {
            final Entry e = StaticUtils.createEntry(baseDN);
            memoryBackend.addEntry(e, null);
        }
        return memoryBackend;
    }
}
