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

package org.apache.fesod.sheet.examples.read.listeners;

import com.alibaba.fastjson2.JSON;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.fesod.common.util.ListUtils;
import org.apache.fesod.sheet.context.AnalysisContext;
import org.apache.fesod.sheet.event.AnalysisEventListener;

/**
 * Listener for schema-free Excel reading without a data model.
 *
 * <h2>Scenario</h2>
 * <p>When you don't have (or don't want) a POJO mapped to the Excel structure,
 * this listener receives each row as a {@code Map<Integer, String>} where:
 * <ul>
 *   <li><b>Key</b> = column index (0-based)</li>
 *   <li><b>Value</b> = cell content as a string</li>
 * </ul>
 *
 * <h2>Key Differences from Typed Listeners</h2>
 * <ul>
 *   <li>Extends {@link AnalysisEventListener}{@code <Map<Integer, String>>} instead of
 *       implementing {@code ReadListener<SomePojo>}.</li>
 *   <li>No {@code @ExcelProperty} annotations needed — all columns are automatically included.</li>
 *   <li>All values arrive as strings, even if the Excel cell is numeric or date type.</li>
 * </ul>
 *
 * <h2>When to Use</h2>
 * <ul>
 *   <li>Generic Excel import tools that accept any file layout.</li>
 *   <li>Quick data inspection / debugging.</li>
 *   <li>Dynamic column handling where structure is unknown at compile time.</li>
 * </ul>
 *
 * @see org.apache.fesod.sheet.examples.read.NoModelReadExample
 * @see AnalysisEventListener
 */
@Slf4j
public class NoModelDataListener extends AnalysisEventListener<Map<Integer, String>> {

    /**
     * Save to the database every 100 records.
     */
    private static final int BATCH_COUNT = 100;

    private List<Map<Integer, String>> cachedDataList = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);

    @Override
    public void invoke(Map<Integer, String> data, AnalysisContext context) {
        log.info("Parsed a data row: {}", JSON.toJSONString(data));
        cachedDataList.add(data);
        if (cachedDataList.size() >= BATCH_COUNT) {
            saveData();
            cachedDataList = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        saveData();
        log.info("All data parsing completed!");
    }

    /**
     * Save data to the database.
     */
    private void saveData() {
        log.info("{} records, starting to save to the database!", cachedDataList.size());
        log.info("Data saved to the database successfully!");
    }
}
