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

package org.apache.fesod.sheet.examples.fill;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.fesod.sheet.ExcelWriter;
import org.apache.fesod.sheet.FesodSheet;
import org.apache.fesod.sheet.examples.fill.data.FillData;
import org.apache.fesod.sheet.examples.util.ExampleFileUtil;
import org.apache.fesod.sheet.write.metadata.WriteSheet;

/**
 * Demonstrates filling list data into a template with repeating rows.
 *
 * <h2>Scenario</h2>
 * <p>Your template has a list area that should expand with multiple rows of data —
 * for example, an invoice with line items, or a report with multiple data rows.
 * The template uses {@code {.fieldName}} (dot prefix) placeholders to mark the
 * repeating area.</p>
 *
 * <h2>Template Format</h2>
 * <p>The template file ({@code templates/list.xlsx}) contains a single row with
 * list placeholders:</p>
 * <pre>
 * Template:        | {.name}       | {.number} | {.date}     |
 *                       ↓               ↓           ↓
 * Filled result:   | Zhang San0    | 5.2       | 2025-01-01  |
 *                  | Zhang San1    | 5.2       | 2025-01-02  |
 *                  | ...           | ...       | ...         |
 * </pre>
 *
 * <h2>Two Fill Strategies</h2>
 *
 * <h3>Strategy 1: Single-Pass Fill</h3>
 * <p>Load all data into memory and fill at once with {@code doFill(list)}.
 * Simple but requires all data in memory.</p>
 *
 * <h3>Strategy 2: Multi-Pass Fill (Memory-Efficient)</h3>
 * <p>Use {@link ExcelWriter} to fill in batches. Fesod uses file-based caching
 * between passes, keeping memory usage low for large datasets.</p>
 * <pre>{@code
 * try (ExcelWriter writer = FesodSheet.write(fileName).withTemplate(template).build()) {
 *     WriteSheet sheet = FesodSheet.writerSheet().build();
 *     writer.fill(batch1, sheet);  // First batch
 *     writer.fill(batch2, sheet);  // Second batch
 * }
 * }</pre>
 *
 * <h2>Related Examples</h2>
 * <ul>
 *   <li>{@link FillBasicExample} — Simple single-value template fills.</li>
 * </ul>
 *
 * @see ExcelWriter#fill(Object, WriteSheet)
 * @see FillData
 */
@Slf4j
public class FillComplexExample {

    public static void main(String[] args) {
        listFill();
    }

    /**
     * Fills a list template using both single-pass and multi-pass strategies.
     *
     * <p><b>Single-pass:</b> All 10 rows loaded into memory, filled in one call.<br/>
     * <b>Multi-pass:</b> Two batches of 10 rows each, filled via {@link ExcelWriter}
     * with file-backed caching for lower memory usage.</p>
     */
    public static void listFill() {
        String templateFileName = ExampleFileUtil.getExamplePath("templates" + File.separator + "list.xlsx");

        // Option 1: Load all data into memory at once and fill
        String fileName = ExampleFileUtil.getTempPath("listFill" + System.currentTimeMillis() + ".xlsx");
        FesodSheet.write(fileName).withTemplate(templateFileName).sheet().doFill(data());
        log.info("Successfully wrote file: {}", fileName);

        // Option 2: Fill in multiple passes, using file caching (saves memory)
        fileName = ExampleFileUtil.getTempPath("listFillMultiple" + System.currentTimeMillis() + ".xlsx");
        try (ExcelWriter excelWriter =
                FesodSheet.write(fileName).withTemplate(templateFileName).build()) {
            WriteSheet writeSheet = FesodSheet.writerSheet().build();
            excelWriter.fill(data(), writeSheet);
            excelWriter.fill(data(), writeSheet);
        }
        log.info("Successfully wrote file: {}", fileName);
    }

    private static List<FillData> data() {
        List<FillData> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            FillData fillData = new FillData();
            fillData.setName("Zhang San" + i);
            fillData.setNumber(5.2);
            fillData.setDate(new Date());
            list.add(fillData);
        }
        return list;
    }
}
