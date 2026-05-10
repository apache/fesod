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

package org.apache.fesod.sheet.examples.write;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.fesod.sheet.FesodSheet;
import org.apache.fesod.sheet.examples.util.ExampleFileUtil;
import org.apache.fesod.sheet.examples.write.data.DemoData;

/**
 * Demonstrates the standard pattern for writing data to an Excel file.
 *
 * <h2>Scenario</h2>
 * <p>You have a list of Java objects (e.g., query results from a database) and want to
 * export them to an Excel file with proper column headers. This is the most common
 * write use case.</p>
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>{@link FesodSheet#write(String, Class)} — Creates a write builder. The class parameter
 *       defines the column headers (from {@code @ExcelProperty} annotations) and data types.</li>
 *   <li>{@code .sheet("Template")} — Creates a worksheet with the given tab name.</li>
 *   <li>{@code .doWrite(data)} — Terminal operation: writes all rows and closes the file.
 *       Handles header generation, type conversion, and resource cleanup automatically.</li>
 * </ul>
 *
 * <h2>Expected Output</h2>
 * <p>An Excel file with one sheet named "Template" containing:</p>
 * <pre>
 * | String Title | Date Title          | Number Title |
 * |--------------|---------------------|--------------|
 * | String0      | 2025-01-01 00:00:00 | 0.56         |
 * | String1      | 2025-01-01 00:00:00 | 0.56         |
 * | ...          | ...                 | ...          |
 * | String9      | 2025-01-01 00:00:00 | 0.56         |
 * </pre>
 *
 * <h2>Related Examples</h2>
 * <ul>
 *   <li>{@link StyleWriteExample} — Add custom header/content styles (colors, fonts).</li>
 *   <li>{@link MergeWriteExample} — Merge cells during write.</li>
 *   <li>{@link ImageWriteExample} — Export images to Excel cells.</li>
 *   <li>{@link org.apache.fesod.sheet.examples.advanced.LargeFileWriteExample}
 *       — Write 100K+ rows with memory optimization.</li>
 * </ul>
 *
 * @see FesodSheet#write(String, Class)
 * @see DemoData
 */
@Slf4j
public class BasicWriteExample {

    public static void main(String[] args) {
        basicWrite();
    }

    /**
     * Writes 10 rows of demo data to an Excel file.
     *
     * <p>The output file is created in the system temp directory.
     * Check the log output for the exact file path.</p>
     */
    public static void basicWrite() {
        String fileName = ExampleFileUtil.getTempPath("basicWrite" + System.currentTimeMillis() + ".xlsx");

        // Specify the class to write, then write to the first sheet.
        FesodSheet.write(fileName, DemoData.class).sheet("Template").doWrite(data());
        log.info("Successfully wrote file: {}", fileName);
    }

    private static List<DemoData> data() {
        List<DemoData> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            DemoData data = new DemoData();
            data.setString("String" + i);
            data.setDate(new Date());
            data.setDoubleData(0.56);
            list.add(data);
        }
        return list;
    }
}
