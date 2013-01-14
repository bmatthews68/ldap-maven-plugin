package com.btmatthews.maven.plugins.ldap.mojo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * Created with IntelliJ IDEA.
 * User: Brian
 * Date: 14/01/13
 * Time: 02:16
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractSource implements Source {

    private static final String CLASSPATH_PREFIX = "classpath:";
    private static final int CLASSPATH_PREFIX_LENGTH = 10;
    private String path;

    protected AbstractSource(final String path) {
        this.path = path;
    }

    @Override
    public InputStream open() {
        if (path.startsWith(CLASSPATH_PREFIX)) {
            final ClassLoader loader = Thread.currentThread().getContextClassLoader();
            return loader.getResourceAsStream(path.substring(CLASSPATH_PREFIX_LENGTH));
        } else {
            final File file = new File(path);
            try {
                return new FileInputStream(file);
            } catch (final FileNotFoundException e) {
                return null;
            }
        }
    }

    @Override
    public String toString() {
        return path;
    }
}
