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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

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
import org.opends.server.types.DN;
import org.opends.server.types.DirectoryEnvironmentConfig;
import org.opends.server.types.DirectoryException;
import org.opends.server.types.Entry;
import org.opends.server.types.InitializationException;
import org.opends.server.types.LDIFImportConfig;
import org.opends.server.types.LDIFImportResult;
import org.opends.server.util.EmbeddedUtils;
import org.opends.server.util.StaticUtils;

/**
 * @author <a href="mailto:brian@btmatthews.com">Brian Matthews</a>
 * @since 1.1.1
 */
public final class OpenDJServer extends AbstractLDAPServer {

    @Override
    public void start(final Logger logger) {
        try {
            logger.logInfo("Starting OpenDJ server");
            new File(getWorkingDirectory(), "locks").mkdirs();
            new File(getWorkingDirectory(), "logs").mkdirs();
            if (!EmbeddedUtils.isRunning()) {
                DirectoryEnvironmentConfig envConfig = new DirectoryEnvironmentConfig();
                envConfig.setServerRoot(getWorkingDirectory());
                final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
                final File source = new File(classLoader.getResource("opendj/config/config.ldif").toURI());
                copyFolder(source.getParentFile().getParentFile(), getWorkingDirectory());
                envConfig.setConfigFile(new File(getWorkingDirectory(), "config/config.ldif"));
                envConfig.setDisableConnectionHandlers(false);
                envConfig.setMaintainConfigArchive(false);
                EmbeddedUtils.startServer(envConfig);
                loadLdif("default", DN.decode(getRoot()));
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

    private void copyFolder(final File sourceFolder,
                            final File destinationFolder) throws IOException {
        if (!destinationFolder.exists()) {
            destinationFolder.mkdirs();
        }
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

    private void loadLdif(final String backendID,
                          final DN baseDN)
            throws InitializationException,ConfigException, FileNotFoundException, DirectoryException,
            URISyntaxException {
        final LDIFImportConfig importConfig = new LDIFImportConfig(new FileInputStream(getLdifFile()));
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
