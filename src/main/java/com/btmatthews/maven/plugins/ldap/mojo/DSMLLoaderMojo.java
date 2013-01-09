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

package com.btmatthews.maven.plugins.ldap.mojo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import netscape.ldap.LDAPAttribute;
import netscape.ldap.LDAPAttributeSet;
import netscape.ldap.LDAPConnection;
import netscape.ldap.LDAPEntry;
import netscape.ldap.LDAPException;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

/**
 * Implement the goal that loads a DSML file.
 *
 * @author <a href="mailto:brian.matthews@btmatthews.com">Brian Matthews</a>
 * @version 1.0
 */
@Mojo(name = "dsml-load")
public final class DSMLLoaderMojo extends AbstractLDAPMojo {
    /**
     * The DSML files to be processed.
     */
    @Parameter(required = true)
    private File[] dsmlFiles;

    /**
     * Indicates if the plugin should continue if there is an error. The default
     * is to halt on error.
     */
    @Parameter(defaultValue = "false")
    private boolean continueOnError;

    /**
     * Execute the plugin goal.
     *
     * @throws org.apache.maven.plugin.MojoExecutionException
     *          If something unexpected happens.
     */
    public void execute() throws MojoExecutionException {
        // Connect to the LDAP directory server

        final LDAPConnection connection = this.connect();

        // Process the DMSL files

        try {
            for (int i = 0; i < this.dsmlFiles.length; ++i) {
                try {
                    this.getLog().info("Processing " + this.dsmlFiles[i]);
                    final SAXReader reader = new SAXReader();
                    final Document document = reader.read(new FileInputStream(this.dsmlFiles[i]));
                    for (final Node entryNode : (List<Node>)document.selectNodes("//dsml/directory-entries/entry")) {

                        final String dn = entryNode.valueOf("@dn");
                        final LDAPAttributeSet attributeSet = new LDAPAttributeSet();

                        final List<Node> objectClassList = (List<Node>)entryNode.selectNodes("objectclass/oc-value");
                        final String[] objectClasses = new String[objectClassList.size()];
                        for (int j = 0; j < objectClasses.length; ++j) {
                            objectClasses[j] = objectClassList.get(j).getStringValue();
                        }
                        attributeSet.add(new LDAPAttribute("objectclass", objectClasses));
                        for (final Node attributeNode : (List<Node>)entryNode.selectNodes("attr")) {
                            final String attributeName = attributeNode.valueOf("@name");
                            final List<Node> attributeValueNodes = attributeNode.selectNodes("value");
                            switch (attributeValueNodes.size()) {
                                case 0:
                                    break;
                                case 1: {
                                    final String attributeValue = attributeValueNodes.get(0).getStringValue();
                                    attributeSet.add(new LDAPAttribute(attributeName, attributeValue));
                                    break;
                                }
                                default: {
                                    final String[] attributeValues = new String[attributeValueNodes.size()];
                                    for (int j = 0; j < attributeValueNodes.size(); ++j) {
                                        attributeValues[j] = attributeValueNodes.get(j).getStringValue();
                                    }
                                    attributeSet.add(new LDAPAttribute(attributeName, attributeValues));
                                    break;
                                }
                            }
                        }

                        final LDAPEntry entry = new LDAPEntry(dn, attributeSet);
                        connection.add(entry);
                    }
                } catch (LDAPException e) {
                    if (!this.continueOnError) {
                        throw new MojoExecutionException("Error processing: " + this.dsmlFiles[i], e);
                    } else {
                        this.getLog().warn("Ignoring error processing: " + this.dsmlFiles[i], e);
                    }
                } catch (FileNotFoundException e) {
                    if (!this.continueOnError) {
                        throw new MojoExecutionException("File not found: " + this.dsmlFiles[i], e);
                    } else {
                        this.getLog().warn("Skipping missing file: " + this.dsmlFiles[i], e);
                    }
                } catch (IOException e) {
                    if (!this.continueOnError) {
                        throw new MojoExecutionException("Error reading from: " + this.dsmlFiles[i], e);
                    } else {
                        this.getLog().warn("Ignoring error reading from: " + this.dsmlFiles[i], e);
                    }
                } catch (final DocumentException e) {
                    if (!this.continueOnError) {
                        throw new MojoExecutionException("Error parsing: " + this.dsmlFiles[i], e);
                    } else {
                        this.getLog().warn("Ignoring error parsing: " + this.dsmlFiles[i], e);
                    }
                }
            }
        } finally {

            // Disconnect from the LDAP Server

            try {
                connection.disconnect();
            } catch (LDAPException e) {
                this.getLog().warn("Ignoring error disconnecting from the LDAP server", e);
            }
        }
    }
}
