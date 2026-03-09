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

package org.apache.fesod.sheet.examples.read;

import lombok.extern.slf4j.Slf4j;
import org.apache.fesod.sheet.FesodSheet;
import org.apache.fesod.sheet.examples.read.data.DemoData;
import org.apache.fesod.sheet.examples.read.listeners.DemoDataListener;
import org.apache.fesod.sheet.examples.util.ExampleFileUtil;

/**
 * Demonstrates the standard pattern for reading Excel files with a custom {@link DemoDataListener}.
 *
 * <h2>Scenario</h2>
 * <p>You need to read an Excel file and process rows in batches (e.g., inserting into a database
 * every 100 rows). This is the production-recommended pattern for reading Excel files with Fesod.</p>
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>{@link DemoDataListener} — A custom {@link org.apache.fesod.sheet.read.listener.ReadListener}
 *       that implements batch caching and database persistence. See the listener class for
 *       the full lifecycle details.</li>
 *   <li>{@link DemoData} — POJO model class mapping Excel columns via {@code @ExcelProperty}.</li>
 *   <li>Unlike {@link org.apache.fesod.sheet.examples.quickstart.SimpleReadExample} which uses
 *       the convenience {@code PageReadListener}, this approach gives full control over
 *       the row processing lifecycle.</li>
 * </ul>
 *
 * <h2>Listener Lifecycle</h2>
 * <pre>
 * File opened
 *     │
 *     ├─ invoke(data, context)       ← called for each data row
 *     │   └─ batch save every 100 rows
 *     │
 *     └─ doAfterAllAnalysed(context) ← called once after last row
 *         └─ final batch save
 * </pre>
 *
 * <h2>Expected Behavior</h2>
 * <p>Each row is logged as JSON. Every 100 rows (or at end of file), a batch save is triggered.</p>
 *
 * <h2>Related Examples</h2>
 * <ul>
 *   <li>{@link org.apache.fesod.sheet.examples.quickstart.SimpleReadExample} — Simpler lambda-based approach.</li>
 *   <li>{@link ConverterReadExample} — Read with data format conversion.</li>
 *   <li>{@link MultiSheetReadExample} — Read multiple sheets from one file.</li>
 * </ul>
 *
 * @see DemoDataListener
 * @see org.apache.fesod.sheet.read.listener.ReadListener
 */
@Slf4j
public class BasicReadExample {

    public static void main(String[] args) {
        basicRead();
    }

    /**
     * Reads an Excel file using a custom {@link DemoDataListener}.
     *
     * <p>The listener handles row-by-row processing with batch persistence.
     * A new listener instance is created per read operation to avoid shared state issues.</p>
     *
     * <p><b>Important:</b> Never reuse a listener instance across multiple read operations
     * or make it a Spring singleton — it holds mutable state (the cached data list).</p>
     */
    public static void basicRead() {
        String fileName = ExampleFileUtil.getExamplePath("demo.xlsx");
        log.info("Reading file: {}", fileName);

        // Specify the class to read the data, then read the first sheet.
        FesodSheet.read(fileName, DemoData.class, new DemoDataListener())
                .sheet()
                .doRead();

        log.info("Successfully read file: {}", fileName);
    }
}
