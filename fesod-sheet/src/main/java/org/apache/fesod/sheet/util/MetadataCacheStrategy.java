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
import org.apache.fesod.common.util.MapUtils;
import org.apache.fesod.sheet.enums.CacheLocationEnum;

/**
 * Internal helper used by {@link SheetHeadFieldResolver} and {@link SheetContentPropertyResolver} for
 * caching resolved metadata, one implementation per {@link CacheLocationEnum} constant.
 * <p>
 * Not intended for direct use; use {@link ClassUtils} as the primary entry point instead.
 * </p>
 *
 * @param <K> the cache key
 * @param <V> the cached metadata
 */
interface MetadataCacheStrategy<K, V> {

    V get(K key, Function<K, V> mappingFunction);

    /**
     * The strategy for every {@link CacheLocationEnum} constant, over the two caches that back them.
     */
    static <K, V> Map<CacheLocationEnum, MetadataCacheStrategy<K, V>> byLocation(
            Map<K, V> memoryCache, ThreadLocal<Map<K, V>> threadLocalCache) {
        Map<CacheLocationEnum, MetadataCacheStrategy<K, V>> strategies = new EnumMap<>(CacheLocationEnum.class);
        strategies.put(CacheLocationEnum.MEMORY, new MemoryCache<>(memoryCache));
        strategies.put(CacheLocationEnum.THREAD_LOCAL, new ThreadLocalCache<>(threadLocalCache));
        strategies.put(CacheLocationEnum.NONE, new NoOpCache<>());
        return Collections.unmodifiableMap(strategies);
    }

    /**
     * Looks up the strategy configured for {@code cacheLocation}, failing loudly when a
     * {@link CacheLocationEnum} constant has no strategy registered for it.
     */
    static <K, V> MetadataCacheStrategy<K, V> select(
            Map<CacheLocationEnum, MetadataCacheStrategy<K, V>> strategies, CacheLocationEnum cacheLocation) {
        MetadataCacheStrategy<K, V> strategy = strategies.get(cacheLocation);
        if (strategy == null) {
            throw new UnsupportedOperationException("unsupported enum");
        }
        return strategy;
    }

    /**
     * The cache will not be cleared unless the app is stopped.
     */
    class MemoryCache<K, V> implements MetadataCacheStrategy<K, V> {

        private final Map<K, V> cache;

        MemoryCache(Map<K, V> cache) {
            this.cache = cache;
        }

        @Override
        public V get(K key, Function<K, V> mappingFunction) {
            return cache.computeIfAbsent(key, mappingFunction);
        }
    }

    /**
     * The cache will be stored in {@code ThreadLocal}, and will be cleared when the excel read and
     * write is completed.
     */
    class ThreadLocalCache<K, V> implements MetadataCacheStrategy<K, V> {

        private final ThreadLocal<Map<K, V>> cache;

        ThreadLocalCache(ThreadLocal<Map<K, V>> cache) {
            this.cache = cache;
        }

        @Override
        public V get(K key, Function<K, V> mappingFunction) {
            Map<K, V> cacheMap = cache.get();
            if (cacheMap == null) {
                cacheMap = MapUtils.newHashMap();
                cache.set(cacheMap);
            }
            return cacheMap.computeIfAbsent(key, mappingFunction);
        }
    }

    /**
     * No caching.It may lose some of performance.
     */
    class NoOpCache<K, V> implements MetadataCacheStrategy<K, V> {

        @Override
        public V get(K key, Function<K, V> mappingFunction) {
            return mappingFunction.apply(key);
        }
    }
}
