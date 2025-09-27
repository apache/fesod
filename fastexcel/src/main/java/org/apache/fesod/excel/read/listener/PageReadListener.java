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

package org.apache.fesod.excel.read.listener;

import java.util.List;
import java.util.function.Consumer;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.fesod.excel.context.AnalysisContext;
import org.apache.fesod.excel.util.ListUtils;

/**
 * page read listener
 *
 *
 */
public class PageReadListener<T> implements ReadListener<T> {
    /**
     * Default single handle the amount of data
     */
    public static int BATCH_COUNT = 1;
    /**
     * Temporary storage of data
     */
    private List<T> cachedDataList = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);
    /**
     * consumer
     */
    private final Consumer<List<T>> consumer;

    /**
     * Single handle the amount of data
     */
    private final int batchCount;

    public PageReadListener(Consumer<List<T>> consumer) {
        this(consumer, BATCH_COUNT);
    }

    public PageReadListener(Consumer<List<T>> consumer, int batchCount) {
        this.consumer = consumer;
        this.batchCount = batchCount;
    }

    @Override
    public void invoke(T data, AnalysisContext context) {
        cachedDataList.add(data);
        if (cachedDataList.size() >= batchCount) {
            consumer.accept(cachedDataList);
            cachedDataList = ListUtils.newArrayListWithExpectedSize(batchCount);
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        if (CollectionUtils.isNotEmpty(cachedDataList)) {
            consumer.accept(cachedDataList);
        }
    }
}
