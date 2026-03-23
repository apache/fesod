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
import org.apache.fesod.sheet.ExcelReader;
import org.apache.fesod.sheet.FesodSheet;
import org.apache.fesod.sheet.examples.read.data.DemoData;
import org.apache.fesod.sheet.examples.read.listeners.DemoDataListener;
import org.apache.fesod.sheet.examples.util.ExampleFileUtil;
import org.apache.fesod.sheet.read.metadata.ReadSheet;

/**
 * Demonstrates reading multiple sheets from a single Excel workbook.
 *
 * <h2>Scenario</h2>
 * <p>Your Excel workbook contains multiple sheets (tabs), each with its own data.
 * You need to read some or all of them, potentially with different data models
 * or listeners for each sheet.</p>
 *
 * <h2>Two Approaches</h2>
 *
 * <h3>Approach 1: Read All Sheets ({@code doReadAll()})</h3>
 * <p>Reads every sheet in the workbook using the same data model and listener.
 * Simplest approach when all sheets share the same structure.</p>
 * <pre>{@code
 * FesodSheet.read(fileName, DemoData.class, new DemoDataListener()).doReadAll();
 * }</pre>
 *
 * <h3>Approach 2: Read Selected Sheets ({@code ExcelReader})</h3>
 * <p>Creates an {@link ExcelReader} and configures individual {@link ReadSheet} objects.
 * Each sheet can have its own data model, listener, and configuration.
 * The {@code ExcelReader} must be closed after use (use try-with-resources).</p>
 * <pre>{@code
 * try (ExcelReader reader = FesodSheet.read(fileName).build()) {
 *     ReadSheet sheet1 = FesodSheet.readSheet(0).head(TypeA.class).registerReadListener(listenerA).build();
 *     ReadSheet sheet2 = FesodSheet.readSheet(1).head(TypeB.class).registerReadListener(listenerB).build();
 *     reader.read(sheet1, sheet2);
 * }
 * }</pre>
 *
 * <h2>Expected Behavior</h2>
 * <p>Each sheet's rows are delivered to its respective listener in order.
 * When using {@code doReadAll()}, all sheets share the same listener instance,
 * so the listener receives rows from all sheets sequentially.</p>
 *
 * <h2>Related Examples</h2>
 * <ul>
 *   <li>{@link BasicReadExample} — Single-sheet read.</li>
 *   <li>{@link NoModelReadExample} — Read without a data model.</li>
 * </ul>
 *
 * @see ExcelReader
 * @see ReadSheet
 * @see FesodSheet#readSheet(Integer)
 */
@Slf4j
public class MultiSheetReadExample {

    public static void main(String[] args) {
        repeatedRead();
    }

    /**
     * Demonstrates both approaches for reading multiple sheets.
     *
     * <p><b>Approach 1</b> uses {@code doReadAll()} for simplicity.<br/>
     * <b>Approach 2</b> uses {@code ExcelReader} with individual {@code ReadSheet} configurations
     * for full control over each sheet's data model and listener.</p>
     *
     * <p><b>Note:</b> In Approach 2, the {@link ExcelReader} is wrapped in try-with-resources
     * to ensure proper resource cleanup. Always close the reader after use.</p>
     */
    public static void repeatedRead() {
        String fileName = ExampleFileUtil.getExamplePath("demo.xlsx");
        log.info("Reading multiple sheets from file: {}", fileName);

        // 1. Read all sheets
        FesodSheet.read(fileName, DemoData.class, new DemoDataListener()).doReadAll();
        log.info("Read all sheets completed");

        // 2. Read specific sheets
        try (ExcelReader excelReader = FesodSheet.read(fileName).build()) {
            // Create ReadSheet objects for each sheet you want to read.
            ReadSheet readSheet1 = FesodSheet.readSheet(0)
                    .head(DemoData.class)
                    .registerReadListener(new DemoDataListener())
                    .build();
            ReadSheet readSheet2 = FesodSheet.readSheet(1)
                    .head(DemoData.class)
                    .registerReadListener(new DemoDataListener())
                    .build();

            // Read multiple sheets at once.
            excelReader.read(readSheet1, readSheet2);
        }
        log.info("Successfully read file: {}", fileName);
    }
}
