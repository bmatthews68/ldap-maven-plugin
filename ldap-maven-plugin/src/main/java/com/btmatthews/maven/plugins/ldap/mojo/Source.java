package com.btmatthews.maven.plugins.ldap.mojo;

import java.io.InputStream;

/**
 * Created with IntelliJ IDEA.
 * User: Brian
 * Date: 14/01/13
 * Time: 02:06
 * To change this template use File | Settings | File Templates.
 */
public interface Source {

    InputStream open();
}
