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

/*
 * This file is part of the Apache Fesod (Incubating) project, which was derived from Alibaba EasyExcel.
 *
 * Copyright (C) 2018-2024 Alibaba Group Holding Ltd.
 */

package org.apache.fesod.common.util;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * Utility methods for creating and manipulating {@link Map} instances ({@link HashMap},
 * {@link LinkedHashMap}, {@link TreeMap} and bounded caches).
 *
 * <p>All factory methods return a <i>mutable</i> map unless otherwise noted.
 *
 * <p>This is a utility class and cannot be instantiated.
 */
public class MapUtils {

    /**
     * Utility classes must never be instantiated.
     */
    private MapUtils() {}

    /**
     * A {@link LinkedHashMap} bounded to {@code maxEntries} entries, evicting the eldest entry on
     * overflow (FIFO).
     *
     * <p>Insertion order is preserved and access-order is disabled, so {@link #get(Object)} does
     * not mutate the underlying linked list — this keeps reads cheap and side-effect free on hot
     * paths.
     *
     * @param <K> the type of keys maintained by this map
     * @param <V> the type of mapped values
     */
    private static final class BoundedLinkedHashMap<K, V> extends LinkedHashMap<K, V> {
        private static final long serialVersionUID = 1L;

        private final int maxEntries;

        private BoundedLinkedHashMap(int maxEntries) {
            super(capacity(maxEntries), 0.75f, false);
            this.maxEntries = maxEntries;
        }

        @Override
        protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
            return size() > maxEntries;
        }
    }

    /**
     * Creates an empty {@code Map} that retains at most {@code maxEntries} entries, evicting the
     * eldest on overflow (FIFO). Handy for thread-local caches keyed by an unbounded input set.
     *
     * <p>The returned map preserves insertion order and is <i>not</i> synchronized; callers must
     * provide their own concurrency control if the map is shared across threads.
     *
     * @param <K> the type of keys maintained by the returned map
     * @param <V> the type of mapped values
     * @param maxEntries the maximum number of entries to retain; must be positive
     * @return a new, empty bounded {@code Map}
     * @throws IllegalArgumentException if the initial capacity derived from {@code maxEntries} is
     *                                  negative
     */
    public static <K, V> Map<K, V> newBoundedMap(int maxEntries) {
        return new BoundedLinkedHashMap<>(maxEntries);
    }

    /**
     * Creates a <i>mutable</i>, empty {@code HashMap} instance with the default initial capacity.
     *
     * @param <K> the type of keys maintained by the returned map
     * @param <V> the type of mapped values
     * @return a new, empty {@code HashMap}
     */
    public static <K, V> HashMap<K, V> newHashMap() {
        return new HashMap<>(16);
    }

    /**
     * Creates a <i>mutable</i>, empty {@code TreeMap} instance using the natural ordering of its
     * keys.
     *
     * @param <K> the type of keys maintained by the returned map; must be
     *            {@link Comparable naturally orderable}
     * @param <V> the type of mapped values
     * @return a new, empty {@code TreeMap}
     */
    public static <K extends Comparable, V> TreeMap<K, V> newTreeMap() {
        return new TreeMap<>();
    }

    /**
     * Creates a {@code HashMap} instance with a high enough initial capacity that it <i>should</i>
     * hold {@code expectedSize} entries without resizing. The result is not guaranteed and the map
     * may be inadvertently oversized.
     *
     * @param <K> the type of keys maintained by the returned map
     * @param <V> the type of mapped values
     * @param expectedSize the number of entries you expect to add to the returned map
     * @return a new, empty {@code HashMap} with enough capacity to hold {@code expectedSize} entries
     *         without resizing
     * @throws IllegalArgumentException if {@code expectedSize} is negative
     */
    public static <K, V> HashMap<K, V> newHashMapWithExpectedSize(int expectedSize) {
        return new HashMap<>(capacity(expectedSize));
    }

    /**
     * Creates a <i>mutable</i>, empty, insertion-ordered {@code LinkedHashMap} instance.
     *
     * @param <K> the type of keys maintained by the returned map
     * @param <V> the type of mapped values
     * @return a new, empty {@code LinkedHashMap}
     */
    public static <K, V> LinkedHashMap<K, V> newLinkedHashMap() {
        return new LinkedHashMap<>();
    }

    /**
     * Creates a {@code LinkedHashMap} instance with a high enough initial capacity that it
     * <i>should</i> hold {@code expectedSize} entries without resizing. The result is not guaranteed
     * and the map may be inadvertently oversized.
     *
     * @param <K> the type of keys maintained by the returned map
     * @param <V> the type of mapped values
     * @param expectedSize the number of entries you expect to add to the returned map
     * @return a new, empty {@code LinkedHashMap} with enough capacity to hold {@code expectedSize}
     *         entries without resizing
     * @throws IllegalArgumentException if {@code expectedSize} is negative
     */
    public static <K, V> LinkedHashMap<K, V> newLinkedHashMapWithExpectedSize(int expectedSize) {
        return new LinkedHashMap<>(capacity(expectedSize));
    }

    /**
     * Returns a capacity sufficient to keep the map from being resized as long as it grows no
     * larger than {@code expectedSize} and the load factor remains at its default (0.75).
     *
     * @param expectedSize the expected number of entries the map will hold
     * @return the initial capacity to use when constructing the map
     */
    static int capacity(int expectedSize) {
        if (expectedSize < 3) {
            return expectedSize + 1;
        }
        if (expectedSize < IntUtils.MAX_POWER_OF_TWO) {
            // This is the calculation used in JDK8 to resize when a putAll
            // happens; it seems to be the most conservative calculation we
            // can make.  0.75 is the default load factor.
            return (int) ((float) expectedSize / 0.75F + 1.0F);
        }
        return Integer.MAX_VALUE;
    }
}
