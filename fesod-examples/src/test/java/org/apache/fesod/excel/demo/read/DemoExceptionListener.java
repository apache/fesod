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
 * Read and convert exceptions.
 *
 *
 */
@Slf4j
public class DemoExceptionListener implements ReadListener<ExceptionDemoData> {

    /**
     * Save to the database every 5 records. In actual use, it can be set to 100 records, and then clear the list to facilitate memory recycling.
     */
    private static final int BATCH_COUNT = 5;

    private List<ExceptionDemoData> cachedDataList = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);

    /**
     * This interface will be called in case of conversion exceptions or other exceptions. If an exception is thrown here, reading will be stopped. If no exception is thrown, the next line will continue to be read.
     *
     * @param exception The exception that occurred.
     * @param context The analysis context.
     * @throws Exception If an exception needs to be propagated.
     */
    @Override
    public void onException(Exception exception, AnalysisContext context) {
        log.error("Parsing failed, but continue parsing the next line: {}", exception.getMessage());
        // If it is a cell conversion exception, the specific row number can be obtained.
        // If the header information is needed, use it in conjunction with invokeHeadMap.
        if (exception instanceof ExcelDataConvertException) {
            ExcelDataConvertException excelDataConvertException = (ExcelDataConvertException) exception;
            log.error(
                    "Parsing exception in row {}, column {}, data: {}",
                    excelDataConvertException.getRowIndex(),
                    excelDataConvertException.getColumnIndex(),
                    excelDataConvertException.getCellData());
        }
    }

    /**
     * This method will return the header row line by line.
     *
     * @param headMap The header map containing column index and cell data.
     * @param context The analysis context.
     */
    @Override
    public void invokeHead(Map<Integer, ReadCellData<?>> headMap, AnalysisContext context) {
        log.info("Parsed a header row: {}", JSON.toJSONString(headMap));
    }

    @Override
    public void invoke(ExceptionDemoData data, AnalysisContext context) {
        log.info("Parsed a data row: {}", JSON.toJSONString(data));
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
