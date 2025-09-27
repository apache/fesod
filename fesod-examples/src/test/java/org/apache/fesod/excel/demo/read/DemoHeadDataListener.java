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
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.fesod.excel.context.AnalysisContext;
import org.apache.fesod.excel.exception.ExcelDataConvertException;
import org.apache.fesod.excel.metadata.data.ReadCellData;
import org.apache.fesod.excel.read.listener.ReadListener;
import org.apache.fesod.excel.util.ListUtils;

/**
 * Reading headers
 *
 *
 */
@Slf4j
public class DemoHeadDataListener implements ReadListener<DemoData> {

    /**
     * Save to the database every 5 records. In actual use, you might use 100 records,
     * then clear the list to facilitate memory recycling.
     */
    private static final int BATCH_COUNT = 5;

    private List<ExceptionDemoData> cachedDataList = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);

    /**
     * This method is called when a conversion exception or other exceptions occur.
     * Throwing an exception will stop the reading process. If no exception is thrown here,
     * the reading will continue to the next row.
     *
     * @param exception The exception that occurred.
     * @param context   The analysis context.
     * @throws Exception If an exception is thrown to stop reading.
     */
    @Override
    public void onException(Exception exception, AnalysisContext context) {
        log.error("Parsing failed, but continue parsing the next row: {}", exception.getMessage());
        if (exception instanceof ExcelDataConvertException) {
            ExcelDataConvertException excelDataConvertException = (ExcelDataConvertException) exception;
            log.error(
                    "Row {}, Column {} parsing exception, data is: {}",
                    excelDataConvertException.getRowIndex(),
                    excelDataConvertException.getColumnIndex(),
                    excelDataConvertException.getCellData());
        }
    }

    /**
     * This method is called for each header row.
     *
     * @param headMap The header data as a map.
     * @param context The analysis context.
     */
    @Override
    public void invokeHead(Map<Integer, ReadCellData<?>> headMap, AnalysisContext context) {
        log.info("Parsed a header row: {}", JSON.toJSONString(headMap));
        // If you want to convert it to a Map<Integer, String>:
        // Solution 1: Do not implement ReadListener, but extend AnalysisEventListener.
        // Solution 2: Call ConverterUtils.convertToStringMap(headMap, context) to convert automatically.
    }

    @Override
    public void invoke(DemoData data, AnalysisContext context) {
        log.info("Parsed a piece of data: {}", JSON.toJSONString(data));
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
     * Simulate saving data to the database.
     */
    private void saveData() {
        log.info("Saving {} records to the database!", cachedDataList.size());
        log.info("Data saved to the database successfully!");
    }
}
