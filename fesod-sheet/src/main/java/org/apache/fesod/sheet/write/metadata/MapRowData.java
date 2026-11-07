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

package org.apache.fesod.sheet.write.metadata;

import java.util.Map;

/**
 * A row whose integer keys identify zero-based data positions.
 * Missing positions before the last key are included in the row size.
 *
 *
 */
public class MapRowData implements RowData {

    private final Map<Integer, ?> map;

    // Determine the row extent once before cell handlers run, like CollectionRowData.
    private final int size;

    public MapRowData(Map<Integer, ?> map) {
        this.map = map;
        int size = map.size();
        for (Object key : map.keySet()) {
            if (key instanceof Integer) {
                int index = (Integer) key;
                if (index >= size) {
                    if (index == Integer.MAX_VALUE) {
                        throw new IllegalArgumentException("Row data index is too large: " + index);
                    }
                    size = index + 1;
                }
            }
        }
        this.size = size;
    }

    @Override
    public Object get(int index) {
        return map.get(index);
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }
}
