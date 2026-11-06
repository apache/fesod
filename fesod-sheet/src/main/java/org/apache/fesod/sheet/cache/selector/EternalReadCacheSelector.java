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

package org.apache.fesod.sheet.cache.selector;

import org.apache.fesod.sheet.cache.ReadCache;
import org.apache.poi.openxml4j.opc.PackagePart;

/**
 * A {@link ReadCacheSelector} that always reuses a single, externally supplied
 * {@link ReadCache} instance for every requested part.
 * <p>
 * Since the same cache is returned regardless of the package part, all shared
 * string data is kept in memory for the lifetime of the cache. This is suitable
 * when the cache is small or when the caller wants full control over its lifecycle.
 */
public class EternalReadCacheSelector implements ReadCacheSelector {

    /**
     * The cache instance returned for every request. Never {@code null} after construction.
     */
    private ReadCache readCache;

    /**
     * Create a selector that always returns the given cache.
     *
     * @param readCache the cache to be reused for all parts; must not be {@code null}
     */
    public EternalReadCacheSelector(ReadCache readCache) {
        this.readCache = readCache;
    }

    /**
     * Return the configured cache. The package part is ignored, so the same cache
     * instance is returned for every call.
     *
     * @param sharedStringsTablePackagePart the shared strings part; not used
     * @return the supplied {@link ReadCache} instance
     */
    @Override
    public ReadCache readCache(PackagePart sharedStringsTablePackagePart) {
        return readCache;
    }
}
