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

import org.apache.fesod.sheet.context.AnalysisContext;

/**
 * Shared-string (and similar) read cache.
 *
 * <p>{@link #get(Integer)} is the only required method. Lifecycle hooks and {@link #put(String)}
 * default to no-ops so implementations that wrap an existing store (for example {@link XlsCache})
 * do not need empty method bodies.
 */
public interface ReadCache {

    /**
     * Initialize cache
     *
     * @param analysisContext
     *            A context is the main anchorage point of a excel reader.
     */
    default void init(AnalysisContext analysisContext) {}

    /**
     * Automatically generate the key and put it in the cache.Key start from 0
     *
     * @param value
     *            Cache value
     */
    default void put(String value) {}

    /**
     * Get value
     *
     * @param key
     *            Index
     * @return Value
     */
    String get(Integer key);

    /**
     * It's called when all the values are put in
     */
    default void putFinished() {}

    /**
     * Called when the Excel read is complete
     */
    default void destroy() {}
}
