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

package org.apache.fesod.sheet.write.style.column;

import org.apache.fesod.sheet.metadata.Head;

/**
 * A ready-made {@link AbstractHeadColumnWidthStyleStrategy} that applies the same fixed width to
 * every column, ignoring the head metadata and the column index.
 *
 * <p>Use this strategy when all columns should look uniform. For per-column sizing, extend
 * {@link AbstractHeadColumnWidthStyleStrategy} directly and implement
 * {@link AbstractHeadColumnWidthStyleStrategy#columnWidth(Head, Integer)}.</p>
 */
public class SimpleColumnWidthStyleStrategy extends AbstractHeadColumnWidthStyleStrategy {

    /**
     * The width, in characters, that will be returned for every column regardless of head or index.
     */
    private final Integer columnWidth;

    /**
     * Creates a strategy that applies the same width to every column.
     *
     * @param columnWidth the fixed column width measured in characters; the value is passed
     *                    through to {@link AbstractHeadColumnWidthStyleStrategy} which scales it
     *                    to POI's unit internally. A {@code null} value leaves every column at
     *                    its existing width
     */
    public SimpleColumnWidthStyleStrategy(Integer columnWidth) {
        this.columnWidth = columnWidth;
    }

    /**
     * {@inheritDoc}
     *
     * <p>This implementation always returns the width supplied to the constructor, ignoring both
     * {@code head} and {@code columnIndex}.</p>
     */
    @Override
    protected Integer columnWidth(Head head, Integer columnIndex) {
        return columnWidth;
    }
}
