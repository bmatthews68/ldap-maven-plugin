package com.btmatthews.maven.plugins.ldap;

import java.io.File;

import com.btmatthews.utils.monitor.AbstractServer;
import com.btmatthews.utils.monitor.Logger;

/**
 * @author <a href="mailto:brian@btmatthews.com">Brian Matthews</a>
 * @since 1.1.1
 */
public abstract class AbstractLDAPServer extends AbstractServer {

    public final static String ROOT = "root";

    public final static String AUTH_DN = "authDn";

    public final static String PASSWD = "passwd";

    public final static String WORK_DIR = "workingDirectory";

    public final static String LDIF_FILE = "ldifFilE";

    public final static String LDAP_PORT = "ldapPort";

    /**
     * The root DN of the LDAP directory.
     */
    private String root;

    private String authDn;

    private String passwd;

    /**
     * The working directory used by the LDAP directory service to store directory data.
     */
    private File workingDirectory;

    /**
     * The LDIF file used to seed the LDAP directory.
     */
    private File ldifFile;

    /**
     * The TCP port on which the server is listening for LDAP traffic.
     */
    private int serverPort;

    /**
     * Used to configure the root DN of the LDAP directory, the working directory used by the directory service to
     * store the directory data, the LDIF file used to seed the directory or the TCP port number on which the server
     * will listening for LDAP traffic.
     *
     * @param name   The name of the property to configure.
     * @param value  The value of the property being configured.
     * @param logger Used to log error and information messages.
     */
    @Override
    public void configure(final String name, final Object value, final Logger logger) {
        if (ROOT.equals(name)) {
            root = (String)value;
            logger.logInfo("Configured root DN for directory server: " + root);
        } else if (AUTH_DN.equals(name)) {
            authDn = (String)value;
            logger.logInfo("Configured admin identity for directory server: " + authDn);
        } else if (PASSWD.equals(name)) {
            passwd = (String)passwd;
            logger.logInfo("Configured admin credentials for directory server: " + passwd);
        } else if (WORK_DIR.equals(name)) {
            workingDirectory = (File)value;
            logger.logInfo("Configured working directory for directory server: " + workingDirectory);
        } else if (LDIF_FILE.equals(name)) {
            ldifFile = (File)value;
            logger.logInfo("Configured LDIF seed data source for directory server: " + ldifFile);
        } else if (LDAP_PORT.equals(name)) {
            serverPort = (Integer)value;
            logger.logInfo("Configured TCP port for directory server: " + serverPort);
        }
    }

    protected final String getRoot() {
        return root;
    }

    protected final String getAuthDn() {
        return authDn;
    }

    protected final String getPasswd() {
        return passwd;
    }

    protected final File getWorkingDirectory() {
        return workingDirectory;
    }

    protected final File getLdifFile() {
        return ldifFile;
    }

    protected final int getServerPort() {
        return serverPort;
    }
}
