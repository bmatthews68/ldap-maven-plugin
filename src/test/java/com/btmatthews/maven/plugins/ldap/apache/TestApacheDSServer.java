package com.btmatthews.maven.plugins.ldap.apache;

import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

import com.btmatthews.utils.monitor.Logger;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mock;

/**
 * Created with IntelliJ IDEA.
 * User: Brian
 * Date: 24/11/12
 * Time: 23:06
 * To change this template use File | Settings | File Templates.
 */
public class TestApacheDSServer {

    private ApacheDSServer server;

    @Mock
    private Logger logger;

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        server = new ApacheDSServer();
        server.setRoot("dc=btmatthews,dc=com");
        server.setWorkingDirectory(folder.newFolder());
        server.setLdapPort(10389);
    }

    @Test
    public void testRunStop() {
        server.start(logger);
        verify(logger).logInfo("Starting ApacheDS server");
        verify(logger).logInfo("Started ApacheDS server");
        server.stop(logger);
        verify(logger).logInfo("Stopping ApacheDS server");
        verify(logger).logInfo("Stopped ApacheDS server");
    }
}
