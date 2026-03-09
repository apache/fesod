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

package org.apache.fesod.sheet.examples.quickstart;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.fesod.sheet.FesodSheet;
import org.apache.fesod.sheet.examples.quickstart.data.DemoData;
import org.apache.fesod.sheet.examples.util.ExampleFileUtil;

/**
 * The simplest way to write an Excel file with Apache Fesod.
 *
 * <h2>Scenario</h2>
 * <p>You have a list of Java objects and want to export them to an Excel file ({@code .xlsx}).
 * This is the recommended starting point for new users who need to generate Excel reports.</p>
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>{@link FesodSheet#write(String, Class)} — Creates a write builder bound to a file path and data class.</li>
 *   <li>{@link DemoData} — A POJO annotated with {@link org.apache.fesod.sheet.annotation.ExcelProperty}
 *       to define column headers. Fields annotated with {@link org.apache.fesod.sheet.annotation.ExcelIgnore}
 *       are excluded from the output.</li>
 *   <li>{@code .sheet("Template")} — Names the worksheet tab in the output file.</li>
 *   <li>{@code .doWrite(data)} — Terminal operation that writes all data and closes the file.</li>
 * </ul>
 *
 * <h2>Expected Output</h2>
 * <p>Generates an Excel file with headers derived from {@code @ExcelProperty} annotations:</p>
 * <pre>
 * | String Title | Date Title          | Number Title |
 * |------------- |---------------------|--------------|
 * | String0      | 2025-01-01 00:00:00 | 0.56         |
 * | String1      | 2025-01-01 00:00:00 | 0.56         |
 * | ...          | ...                 | ...          |
 * </pre>
 *
 * <h2>Related Examples</h2>
 * <ul>
 *   <li>{@link org.apache.fesod.sheet.examples.write.StyleWriteExample} — Customize header and content styles.</li>
 *   <li>{@link org.apache.fesod.sheet.examples.write.MergeWriteExample} — Merge cells during write.</li>
 *   <li>{@link org.apache.fesod.sheet.examples.quickstart.SimpleReadExample} — The read counterpart.</li>
 * </ul>
 *
 * @see FesodSheet#write(String, Class)
 * @see DemoData
 */
@Slf4j
public class SimpleWriteExample {

    public static void main(String[] args) {
        simpleWrite();
    }

    /**
     * Writes a list of {@link DemoData} objects to an Excel file in two simple steps.
     *
     * <ol>
     *   <li><b>Define a data model</b> — Create a POJO with {@code @ExcelProperty} annotations
     *       (see {@link DemoData}). The annotation value becomes the column header.</li>
     *   <li><b>Call the builder</b> — {@code FesodSheet.write(fileName, DemoData.class).sheet("name").doWrite(list)}
     *       generates the file. The builder handles file creation, header writing, and resource cleanup.</li>
     * </ol>
     *
     * <p><b>Output location:</b> The file is written to the system temp directory.
     * Check the log output for the exact path.</p>
     */
    public static void simpleWrite() {
        // Write to system temp directory for output files
        String fileName = ExampleFileUtil.getTempPath("demo" + System.currentTimeMillis() + ".xlsx");

        // Specify the class to write, then write to the first sheet named "Template"
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
