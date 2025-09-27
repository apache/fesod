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

package org.apache.fesod.excel.demo.read;

import com.alibaba.fastjson2.JSON;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.fesod.excel.context.AnalysisContext;
import org.apache.fesod.excel.read.listener.ReadListener;
import org.apache.fesod.excel.util.ListUtils;

/**
 * Template data reading class
 *
 *
 */
@Slf4j
public class ConverterDataListener implements ReadListener<ConverterData> {

    /**
     * Save to the database every 5 records. In actual use, you might use 100 records,
     * then clear the list to facilitate memory recycling.
     */
    private static final int BATCH_COUNT = 5;

    private List<ConverterData> cachedDataList = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);

    @Override
    public void invoke(ConverterData data, AnalysisContext context) {
        log.info("Parsed a piece of data: {}", JSON.toJSONString(data));
        cachedDataList.add(data);
        if (cachedDataList.size() >= BATCH_COUNT) {
            saveData();
            cachedDataList = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        saveData();
        log.info("All data has been parsed and processed!");
    }

    /**
     * Simulate saving data to the database
     */
    private void saveData() {
        log.info("Saving {} records to the database!", cachedDataList.size());
        log.info("Data saved to the database successfully!");
    }
}
