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
 * Strategy for choosing the {@link ReadCache} implementation used to hold the shared strings
 * of a spreadsheet.
 *
 * <p>Implementations may pick a cache based on the size or nature of the package part, or always
 * return a single predefined cache instance. The default choice is
 * {@link SimpleReadCacheSelector}.
 */
public interface ReadCacheSelector {

    /**
     * Select the cache to use for the given shared strings part.
     *
     * @param sharedStringsTablePackagePart
     *            the shared strings part of the workbook, never {@code null}
     * @return the cache used to read the shared strings; must not be {@code null}
     */
    ReadCache readCache(PackagePart sharedStringsTablePackagePart);
}
