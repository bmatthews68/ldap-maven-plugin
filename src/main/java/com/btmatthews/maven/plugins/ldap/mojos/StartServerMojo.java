/*
 * Copyright 2008-2011 Brian Thomas Matthews
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

package com.btmatthews.maven.plugins.ldap.mojos;

import java.util.HashSet;

import org.apache.directory.server.core.DefaultDirectoryService;
import org.apache.directory.server.core.DirectoryService;
import org.apache.directory.server.core.entry.ServerEntry;
import org.apache.directory.server.core.partition.Partition;
import org.apache.directory.server.core.partition.impl.btree.jdbm.JdbmIndex;
import org.apache.directory.server.core.partition.impl.btree.jdbm.JdbmPartition;
import org.apache.directory.server.xdbm.Index;
import org.apache.directory.shared.ldap.name.LdapDN;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoFailureException;

/**
 * This Mojo implements the start-server goal which launches an embedded LDAP
 * server using Apache Directory Server.
 * 
 * @goal start-server
 * @author <a href="mailto:brian.matthews@btmatthews.com">Brian Matthews</a>
 * @version 1.0
 */
public class StartServerMojo extends AbstractMojo {

	public void execute() throws MojoFailureException {

		try {
			final DirectoryService service = new DefaultDirectoryService();
			service.getChangeLog().setEnabled(false);
			final Partition apachePartition = addPartition(service, "apache",
					"dc=apache,dc=org");
			addIndex(apachePartition, "objectClass", "ou", "uid");
			service.startup();
			if (!service.getAdminSession()
					.exists(apachePartition.getSuffixDn())) {
				final LdapDN dnApache = new LdapDN("dc=Apache,dc=Org");
				final ServerEntry entryApache = service.newEntry(dnApache);
				entryApache.add("objectClass", "top", "domain",
						"extensibleObject");
				entryApache.add("dc", "Apache");
				service.getAdminSession().add(entryApache);
			}
		} catch (final Exception exception) {
			throw new MojoFailureException("", exception);
		}
	}

	/**
	 * Add a new partition to the server.
	 * 
	 * @param partitionId
	 *            The partition Id.
	 * @param partitionDn
	 *            The partition DN.
	 * @return The newly added partition
	 * @throws Exception
	 *             If the partition cannot be added.
	 */
	private Partition addPartition(final DirectoryService service,
			final String partitionId, final String partitionDn)
			throws Exception {
		final Partition partition = new JdbmPartition();
		partition.setId(partitionId);
		partition.setSuffix(partitionDn);
		service.addPartition(partition);
		return partition;
	}

	/**
	 * Add a new set of indices on the given attributes.
	 * 
	 * @param partition
	 *            The partition on which we want to add indices.
	 * @param attrs
	 *            The list of attributes to index.
	 */
	private void addIndex(final Partition partition, final String... attrs) {
		final HashSet<Index<?, ServerEntry>> indexedAttributes = new HashSet<Index<?, ServerEntry>>();
		for (final String attribute : attrs) {
			final JdbmIndex<String, ServerEntry> index = new JdbmIndex<String, ServerEntry>(
					attribute);
			indexedAttributes.add(index);
		}
		((JdbmPartition) partition).setIndexedAttributes(indexedAttributes);
	}

}
