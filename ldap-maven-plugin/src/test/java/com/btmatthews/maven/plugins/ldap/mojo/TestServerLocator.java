package com.btmatthews.maven.plugins.ldap.mojo;

import com.btmatthews.maven.plugins.ldap.apache.ApacheDSServerFactory;
import com.btmatthews.maven.plugins.ldap.opendj.OpenDJServerFactory;
import com.btmatthews.maven.plugins.ldap.unboundid.UnboundIDServerFactory;
import com.btmatthews.utils.monitor.Logger;
import com.btmatthews.utils.monitor.ServerFactory;
import com.btmatthews.utils.monitor.ServerFactoryLocator;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Created with IntelliJ IDEA.
 * User: Brian
 * Date: 11/01/13
 * Time: 16:33
 * To change this template use File | Settings | File Templates.
 */
public class TestServerLocator {

    @Mock
    private Logger logger;

    private ServerFactoryLocator locator;

    @Before
    public void setUp() {
        initMocks(this);
        locator = new ServerFactoryLocator(logger, Thread.currentThread().getContextClassLoader());
    }

    @Test
    public void canLocateApacheDSServerFactory() {
        final ServerFactory factory = locator.getFactory("apacheds");
        assertNotNull(factory);
        assertTrue(factory instanceof ApacheDSServerFactory);
    }

    @Test
    public void canLocateOpenDJServerFactory() {
        final ServerFactory factory = locator.getFactory("opendj");
        assertNotNull(factory);
        assertTrue(factory instanceof OpenDJServerFactory);
    }

    @Test
    public void canLocateUnboundIDServerFactory() {
        final ServerFactory factory = locator.getFactory("unboundid");
        assertNotNull(factory);
        assertTrue(factory instanceof UnboundIDServerFactory);
    }
}
