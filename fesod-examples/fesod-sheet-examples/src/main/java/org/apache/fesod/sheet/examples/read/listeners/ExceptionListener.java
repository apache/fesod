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
import org.apache.fesod.sheet.examples.read.data.ExceptionDemoData;
import org.apache.fesod.sheet.exception.ExcelDataConvertException;
import org.apache.fesod.sheet.metadata.data.ReadCellData;
import org.apache.fesod.sheet.read.listener.ReadListener;

/**
 * Listener that demonstrates graceful exception handling during Excel reading.
 *
 * <h2>Scenario</h2>
 * <p>When reading an Excel file with inconsistent data (e.g., text in a date column),
 * Fesod throws an {@link ExcelDataConvertException}. This listener catches those errors,
 * logs diagnostic information, and allows parsing to continue for the remaining rows.</p>
 *
 * <h2>Key Methods</h2>
 * <ul>
 *   <li>{@link #onException(Exception, AnalysisContext)} — Intercepts any exception during parsing.
 *       If the exception is an {@code ExcelDataConvertException}, logs the exact row, column,
 *       and cell value that caused the error. <em>Not rethrowing</em> the exception tells Fesod
 *       to skip this row and continue.</li>
 *   <li>{@link #invokeHead(Map, AnalysisContext)} — Called when the header row is parsed.
 *       Receives a map of column index → {@link ReadCellData} containing header cell information.</li>
 *   <li>{@link #invoke(ExceptionDemoData, AnalysisContext)} — Called for each successfully
 *       converted data row.</li>
 *   <li>{@link #doAfterAllAnalysed(AnalysisContext)} — Called after the last row.
 *       Persists any remaining cached data.</li>
 * </ul>
 *
 * <h2>Error Handling Strategy</h2>
 * <pre>
 * onException() is called:
 *     ├─ ExcelDataConvertException → log row/column/value, continue
 *     └─ Other exception → log message, continue (or rethrow to stop)
 * </pre>
 *
 * @see ExcelDataConvertException
 * @see org.apache.fesod.sheet.examples.read.ExceptionHandlingExample
 */
@Slf4j
public class ExceptionListener implements ReadListener<ExceptionDemoData> {

    private static final int BATCH_COUNT = 100;

    private List<ExceptionDemoData> cachedDataList = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);

    @Override
    public void onException(Exception exception, AnalysisContext context) {
        log.error("Parsing failed, but continue parsing the next line: {}", exception.getMessage());
        if (exception instanceof ExcelDataConvertException) {
            ExcelDataConvertException excelDataConvertException = (ExcelDataConvertException) exception;
            log.error(
                    "Parsing exception in row {}, column {}, data: {}",
                    excelDataConvertException.getRowIndex(),
                    excelDataConvertException.getColumnIndex(),
                    excelDataConvertException.getCellData());
        }
    }

    @Override
    public void invokeHead(Map<Integer, ReadCellData<?>> headMap, AnalysisContext context) {
        log.info("Parsed a header row: {}", JSON.toJSONString(headMap));
    }

    @Override
    public void invoke(ExceptionDemoData data, AnalysisContext context) {
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

    private void saveData() {
        log.info("{} records saved to database!", cachedDataList.size());
    }
}
