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

package com.btmatthews.maven.plugins.ldap;

import com.unboundid.ldap.sdk.AddRequest;
import com.unboundid.ldap.sdk.Entry;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

import static com.btmatthews.maven.plugins.ldap.FormatTestUtils.createEntry;

/**
 * Used to match {@link AddRequest}.
 *
 * @author <a href="mailto:brian@btmatthews.com">Brian Matthews</a>
 * @since 1.2.0
 */
public class AddRequestMatcher extends BaseMatcher<AddRequest> {

    /**
     * The expected LDAP entry.
     */
    private Entry expected;

    /**
     * Initialise the matcher with the expected LDAP entry.
     *
     * @param entry The expected LDAP entry.
     */
    public AddRequestMatcher(final Entry entry) {
        expected = entry;
    }

    /**
     * Initialise the matcher with the expected distinguished name and attribute name value pairs.
     *
     * @param dn         The distinguished name.
     * @param nameValues The attribute name value pairs.
     */
    public AddRequestMatcher(final String dn, final String... nameValues) {
        this(createEntry(dn, nameValues));
    }

    /**
     * Match {@code item} to verify that it is an {@link AddRequest} with a DN and attribute values matching
     * those of the LDAP entry saved in the constructor.
     *
     * @param item The object being matched.
     * @return {@code true} if the object matches.
     */
    @Override
    public boolean matches(final Object item) {
        if (item instanceof AddRequest) {
            final AddRequest request = (AddRequest) item;
            if (expected.getDN().equals(request.getDN())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Describe this matching rule.
     *
     * @param description Used to build the description.
     */
    @Override
    public void describeTo(final Description description) {
    }
}
