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

import com.unboundid.ldap.sdk.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Helper methods used by test cases.
 *
 * @author <a href="mailto:brian@btmatthews.com">Brian Matthews</a>
 * @since 1.2.0
 */
public class FormatTestUtils {
    /**
     * Create a LDAP directory entry from the distinguished name an a set of attributes passed as name value pairs.
     *
     * @param dn                  The distinguished name.
     * @param attributeNameValues Thr attribute names and values.
     * @return An {@link Entry} object.
     */
    public static Entry createEntry(final String dn,
                                    final String... attributeNameValues) {
        final List<Attribute> attributes = new LinkedList<Attribute>();
        for (int i = 0; i < attributeNameValues.length; i += 2) {
            attributes.add(new Attribute(attributeNameValues[i], attributeNameValues[i + 1]));
        }
        return new Entry(dn, attributes);
    }

    /**
     * Create a search result entry from the distinguished name an a set of attributes passed as name value pairs.
     *
     * @param dn                  The distinguished name.
     * @param attributeNameValues Thr attribute names and values.
     * @return An {@link SearchResultEntry} object.
     */
    public static SearchResultEntry createSearchResultEntry(final String dn, final String... attributeNameValues) {
        final Entry entry = createEntry(dn, attributeNameValues);
        return new SearchResultEntry(entry);
    }

    /**
     * Create an empty search result.
     *
     * @return A {@link SearchResult} object.
     */
    public static SearchResult createSearchResult() {
        return createSearchResult(new ArrayList<SearchResultEntry>());
    }

    /**
     * Create a search result with the search result entries passed as a {@link List}.
     *
     * @param entries A list of individual search result entries.
     * @return A {@link SearchResult} object.
     */
    public static SearchResult createSearchResult(final List<SearchResultEntry> entries) {
        return new SearchResult(0, ResultCode.SUCCESS, null, null, null, entries, null, entries.size(), 0, null);
    }

    /**
     * Create a search result with the search result entries passed as a variant argument list.
     *
     * @param entries The individual search result entries.
     * @return A {@link SearchResult} object.
     */
    public static SearchResult createSearchResult(final SearchResultEntry... entries) {
        return createSearchResult(Arrays.asList(entries));
    }
}
