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

package com.btmatthews.maven.plugins.ldap.dsml;

import com.btmatthews.maven.plugins.ldap.FormatReader;
import com.unboundid.ldap.sdk.Attribute;
import com.unboundid.ldif.LDIFAddChangeRecord;
import com.unboundid.ldif.LDIFChangeRecord;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.jaxen.JaxenException;
import org.jaxen.NamespaceContext;
import org.jaxen.SimpleNamespaceContext;
import org.jaxen.XPath;
import org.jaxen.dom4j.Dom4jXPath;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * @author <a href="mailto:brian@btmatthews.com">Brian Matthews</a>
 * @since 1.2.0
 */
public class DSMLReader implements FormatReader {

    /**
     * Iterates over the directory entries extracted from the .dsml file.
     */
    private final Iterator<Node> entryIterator;
    private final NamespaceContext namespaceContext;
    private final XPath objectClassXPath;
    private final XPath attrXPath;
    private final XPath attrValueXPath;

    /**
     * @param inputStream
     * @throws DocumentException
     * @throws IOException
     */
    public DSMLReader(final InputStream inputStream) throws DocumentException, IOException, JaxenException {
        final Map<String, String> map = new HashMap<String, String>();
        map.put("dsml", "http://www.dsml.org/DSML");
        namespaceContext = new SimpleNamespaceContext(map);
        final SAXReader reader = new SAXReader();
        final Document document = reader.read(inputStream);
        final XPath xpath = createXPath("/dsml[namespace-uri()='http://www.dsml.org/DSML']/dsml:directory-entries/dsml:entry");
        objectClassXPath = createXPath("dsml:objectclass/dsml:oc-value");
        attrXPath = createXPath("dsml:attr");
        attrValueXPath = createXPath("dsml:value");
        final List<Node> entries = (List<Node>) xpath.selectNodes(document);
        entryIterator = entries.iterator();
    }

    /**
     * Read the next change record from the underlying input stream.
     *
     * @return The next change record or {@code null} if the end of the input stream has been reached.
     */
    @Override
    public LDIFChangeRecord nextRecord() {
        if (entryIterator.hasNext()) {
            try {
                final Node entryNode = entryIterator.next();
                final String dn = entryNode.valueOf("@dn");
                final List<Attribute> attributes = new ArrayList<Attribute>();

                final List<Node> objectClassList = (List<Node>) objectClassXPath.selectNodes(entryNode);
                final String[] objectClasses = new String[objectClassList.size()];
                for (int j = 0; j < objectClasses.length; ++j) {
                    objectClasses[j] = objectClassList.get(j).getStringValue();
                }
                attributes.add(new Attribute("objectclass", objectClasses));
                for (final Node attributeNode : (List<Node>) attrXPath.selectNodes(entryNode)) {
                    final String attributeName = attributeNode.valueOf("@name");
                    final List<Node> attributeValueNodes = (List<Node>) attrValueXPath.selectNodes(attributeNode);
                    switch (attributeValueNodes.size()) {
                        case 0:
                            break;
                        case 1: {
                            final String attributeValue = attributeValueNodes.get(0).getStringValue();
                            attributes.add(new Attribute(attributeName, attributeValue));
                            break;
                        }
                        default: {
                            final String[] attributeValues = new String[attributeValueNodes.size()];
                            for (int j = 0; j < attributeValueNodes.size(); ++j) {
                                attributeValues[j] = attributeValueNodes.get(j).getStringValue();
                            }
                            attributes.add(new Attribute(attributeName, attributeValues));
                            break;
                        }
                    }
                }
                return new LDIFAddChangeRecord(dn, attributes);
            } catch (final JaxenException e) {
                return null;
            }
        } else {
            return null;
        }
    }

    @Override
    public void close() {
    }

    private XPath createXPath(final String xpathString) throws JaxenException {
        final XPath xpath = new Dom4jXPath(xpathString);
        xpath.setNamespaceContext(namespaceContext);
        return xpath;
    }
}
