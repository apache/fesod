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

package org.apache.fesod.sheet.util;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.function.Function;
import org.apache.fesod.sheet.enums.CacheLocationEnum;
import org.apache.fesod.sheet.metadata.ConfigurationHolder;

/**
 * Internal helper used by {@link SheetHeadFieldResolver} and {@link SheetContentPropertyResolver} for
 * caching resolved metadata in the tier the current read or write is configured for.
 * <p>
 * Not intended for direct use; use {@link ClassUtils} as the primary entry point instead.
 * </p>
 *
 * @param <K> the cache key
 * @param <V> the cached metadata
 */
final class MetadataCaches<K, V> {

    private final Map<CacheLocationEnum, MetadataCacheStrategy<K, V>> byLocation;

    MetadataCaches(Map<K, V> memoryCache, ThreadLocal<Map<K, V>> threadLocalCache) {
        Map<CacheLocationEnum, MetadataCacheStrategy<K, V>> strategies = new EnumMap<>(CacheLocationEnum.class);
        strategies.put(CacheLocationEnum.MEMORY, new MetadataCacheStrategy.InMemoryCache<>(memoryCache));
        strategies.put(CacheLocationEnum.THREAD_LOCAL, new MetadataCacheStrategy.ThreadLocalCache<>(threadLocalCache));
        strategies.put(CacheLocationEnum.NONE, new MetadataCacheStrategy.NoOpCache<>());
        this.byLocation = Collections.unmodifiableMap(strategies);
    }

    /**
     * Caches in the tier {@code configurationHolder} is configured for, failing loudly when a
     * {@link CacheLocationEnum} constant has no strategy registered for it.
     */
    V get(ConfigurationHolder configurationHolder, K key, Function<K, V> mappingFunction) {
        CacheLocationEnum cacheLocation =
                configurationHolder.globalConfiguration().getFiledCacheLocation();
        MetadataCacheStrategy<K, V> strategy = byLocation.get(cacheLocation);
        if (strategy == null) {
            throw new UnsupportedOperationException("unsupported enum");
        }
        return strategy.get(key, mappingFunction);
    }
}
