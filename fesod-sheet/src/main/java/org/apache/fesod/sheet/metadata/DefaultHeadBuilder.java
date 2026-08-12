/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.fesod.sheet.metadata;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import org.apache.commons.lang3.Validate;

/**
 * Standard implementation of {@code HeadBuilder}.
 */
class DefaultHeadBuilder implements HeadBuilder {

    private final List<List<String>> columns;
    private final List<String> prefixes;

    DefaultHeadBuilder() {
        this(new ArrayList<>(), new ArrayList<>());
    }

    DefaultHeadBuilder(List<List<String>> columns, List<String> prefixes) {
        this.columns = columns;
        this.prefixes = prefixes;
    }

    static List<List<String>> define(Consumer<HeadBuilder> headBuilderConsumer) {
        Validate.notNull(headBuilderConsumer, "headBuilderConsumer must not be null");

        DefaultHeadBuilder builder = new DefaultHeadBuilder();
        headBuilderConsumer.accept(builder);
        return builder.toHead();
    }

    /**
     * Define a single column with a fixed head names (containing at least one column).
     *
     * @param headName the first header name (must not be {@code null})
     * @param subHeadNames optional sublevel header names
     * @return this for builder chains
     */
    @Override
    public HeadBuilder column(String headName, String... subHeadNames) {
        Validate.notNull(headName, "header name must not be null");

        int initialCapacity = prefixes.size() + ((subHeadNames == null) ? 0 : subHeadNames.length) + 1;
        List<String> current = new ArrayList<>(initialCapacity);
        current.addAll(prefixes);
        current.add(headName);

        if (subHeadNames != null) {
            Validate.noNullElements(subHeadNames, "sub-header names must not contain null elements");
            Collections.addAll(current, subHeadNames);
        }
        columns.add(current);
        return this;
    }

    /**
     * Define a single column with a repeating header name at multiple levels.
     *
     * @param headName the header name to repeat (must not be {@code null})
     * @param repeat the number of times to repeat the name
     * @return this for builder chains
     */
    @Override
    public HeadBuilder column(String headName, int repeat) {
        Validate.notNull(headName, "header name must not be null");
        Validate.isTrue(repeat > 0, "header repeat must be greater than 0");

        int initialCapacity = prefixes.size() + repeat;
        List<String> current = new ArrayList<>(initialCapacity);
        current.addAll(prefixes);

        for (int i = 0; i < repeat; i++) {
            current.add(headName);
        }

        columns.add(current);
        return this;
    }

    /**
     * Declare multiple shared parent header names to group sub-headers.
     *
     * @param parentHeadNames the shared parent header names (must not be {@code null} or empty)
     * @param subHeadBuilderConsumer the consumer to define sub-headers
     * @return this for builder chains
     */
    @Override
    public HeadBuilder columns(List<String> parentHeadNames, Consumer<HeadBuilder> subHeadBuilderConsumer) {
        Validate.notEmpty(parentHeadNames, "parent header names must not be null or empty");
        Validate.noNullElements(parentHeadNames, "parent header names must not contain null elements");
        Validate.notNull(subHeadBuilderConsumer, "subHeadBuilderConsumer must not be null");

        int previousSize = this.prefixes.size();
        this.prefixes.addAll(parentHeadNames);

        subHeadBuilderConsumer.accept(this);

        while (this.prefixes.size() > previousSize) {
            this.prefixes.remove(this.prefixes.size() - 1);
        }
        return this;
    }

    /**
     * Return the heads.
     *
     * @return the list of head
     */
    List<List<String>> toHead() {
        return columns;
    }
}
