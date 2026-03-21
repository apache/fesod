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

package org.apache.fesod.sheet.examples.read;

import lombok.extern.slf4j.Slf4j;
import org.apache.fesod.sheet.FesodSheet;
import org.apache.fesod.sheet.examples.read.data.ExceptionDemoData;
import org.apache.fesod.sheet.examples.read.listeners.ExceptionListener;
import org.apache.fesod.sheet.examples.util.ExampleFileUtil;

/**
 * Demonstrates graceful error handling when reading Excel files with data conversion issues.
 *
 * <h2>Scenario</h2>
 * <p>Your Excel file contains messy or inconsistent data — for example, a column expected to be
 * a date contains plain text like "N/A". Instead of failing the entire read, you want to:
 * <ul>
 *   <li>Log the problematic row and column</li>
 *   <li>Skip the bad row</li>
 *   <li>Continue processing the remaining rows</li>
 * </ul>
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>{@link org.apache.fesod.sheet.read.listener.ReadListener#onException(Exception, org.apache.fesod.sheet.context.AnalysisContext)}
 *       — Override this method to intercept conversion errors. If you don't rethrow,
 *       parsing continues to the next row.</li>
 *   <li>{@link org.apache.fesod.sheet.exception.ExcelDataConvertException} — Thrown when a cell value
 *       cannot be converted to the target Java type. Provides {@code getRowIndex()},
 *       {@code getColumnIndex()}, and {@code getCellData()} for precise error reporting.</li>
 *   <li>{@link ExceptionDemoData} — Intentionally uses {@code Date} type for a string column
 *       to trigger conversion errors.</li>
 * </ul>
 *
 * <h2>Error Handling Flow</h2>
 * <pre>
 * Row parsed
 *     │
 *     ├─ Conversion succeeds → invoke(data, context)
 *     │
 *     └─ Conversion fails → onException(ex, context)
 *         ├─ Log error, DON'T rethrow → skip row, continue parsing
 *         └─ Rethrow exception → stop parsing immediately
 * </pre>
 *
 * <h2>Expected Behavior</h2>
 * <p>Rows with valid dates are processed normally. Rows with incompatible data are logged
 * with their exact row/column position and skipped.</p>
 *
 * <h2>Related Examples</h2>
 * <ul>
 *   <li>{@link BasicReadExample} — Read without error handling (default: exceptions propagate).</li>
 * </ul>
 *
 * @see org.apache.fesod.sheet.exception.ExcelDataConvertException
 * @see ExceptionListener
 */
@Slf4j
public class ExceptionHandlingExample {

    public static void main(String[] args) {
        exceptionRead();
    }

    /**
     * Reads an Excel file with an {@link ExceptionListener} that catches and logs conversion errors.
     *
     * <p>The {@link ExceptionDemoData} model intentionally maps a string column to {@code Date},
     * causing {@link org.apache.fesod.sheet.exception.ExcelDataConvertException} to be thrown.
     * The listener catches these errors, logs them, and lets parsing continue.</p>
     */
    public static void exceptionRead() {
        String fileName = ExampleFileUtil.getExamplePath("demo.xlsx");
        log.info("Reading file with exception handling: {}", fileName);

        FesodSheet.read(fileName, ExceptionDemoData.class, new ExceptionListener())
                .sheet()
                .doRead();

        log.info("Successfully read file: {}", fileName);
    }
}
