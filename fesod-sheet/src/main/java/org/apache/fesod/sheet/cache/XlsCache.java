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

import org.apache.poi.hssf.record.SSTRecord;

/**
 * Read cache for XLS (BIFF8) files, backed by POI's {@link SSTRecord}.
 *
 * <p>XLS files store shared strings in a single Shared String Table record, so this cache
 * is read-only: lookups delegate directly to the {@link SSTRecord} and {@code put} is
 * inherited as a no-op from {@link ReadCache}.
 */
public class XlsCache implements ReadCache {
    private final SSTRecord sstRecord;

    /**
     * Construct a cache backed by the given Shared String Table record.
     *
     * @param sstRecord
     *            The Shared String Table record parsed from the XLS file; must not be null.
     */
    public XlsCache(SSTRecord sstRecord) {
        this.sstRecord = sstRecord;
    }

    /**
     * Look up a shared string by its index in the Shared String Table.
     *
     * @param key
     *            The zero-based index into the shared string table.
     * @return The string value at the given index, or null if the entry is empty.
     */
    @Override
    public String get(Integer key) {
        return sstRecord.getString(key).toString();
    }
}
