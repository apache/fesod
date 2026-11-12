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

package org.apache.fesod.sheet.cache;

import java.util.ArrayList;
import java.util.List;

/**
 * A {@link ReadCache} implementation that stores all values in memory.
 * <p>
 * Lookup is performed directly by index, which is slightly faster than a map-based cache, but it
 * keeps every value resident and is therefore memory intensive.
 */
public class MapCache implements ReadCache {
    private final List<String> cache = new ArrayList<>();

    /**
     * Append a value to the cache. The key is implicitly the position of the value in the internal
     * list, starting from 0.
     *
     * @param value
     *            The value to cache.
     */
    @Override
    public void put(String value) {
        cache.add(value);
    }

    /**
     * Return the value stored at the given index.
     *
     * @param key
     *            The zero-based index of the cached value.
     * @return The cached value, or {@code null} if {@code key} is {@code null} or negative.
     */
    @Override
    public String get(Integer key) {
        if (key == null || key < 0) {
            return null;
        }
        return cache.get(key);
    }
}
