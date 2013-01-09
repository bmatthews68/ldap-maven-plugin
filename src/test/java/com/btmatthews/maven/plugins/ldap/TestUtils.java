package com.btmatthews.maven.plugins.ldap;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * Created with IntelliJ IDEA.
 * User: Brian
 * Date: 09/01/13
 * Time: 11:21
 * To change this template use File | Settings | File Templates.
 */
public class TestUtils {

    public static final File getFile(final String filename) throws URISyntaxException {
        final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        final URL url = classLoader.getResource(filename);
        return new File(url.toURI());
    }
}
