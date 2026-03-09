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

import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.fesod.sheet.FesodSheet;
import org.apache.fesod.sheet.examples.quickstart.data.DemoData;
import org.apache.fesod.sheet.examples.util.ExampleFileUtil;
import org.apache.fesod.sheet.read.listener.PageReadListener;

/**
 * The simplest way to read an Excel file with Apache Fesod.
 *
 * <h2>Scenario</h2>
 * <p>You have an Excel file ({@code .xlsx} or {@code .xls}) and want to parse it into Java objects
 * with minimal boilerplate. This is the recommended starting point for new users.</p>
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>{@link PageReadListener} — A built-in listener that collects rows into pages (default 100 rows per page)
 *       and delivers them via a lambda callback. Ideal for simple use cases.</li>
 *   <li>{@link DemoData} — A POJO annotated with {@link org.apache.fesod.sheet.annotation.ExcelProperty}
 *       to map Excel columns to Java fields by header name.</li>
 *   <li>Fesod reads row-by-row in a streaming fashion, so memory usage stays low even for large files.</li>
 * </ul>
 *
 * <h2>Expected Behavior</h2>
 * <p>When run against the bundled {@code demo.xlsx}, each row is logged as a JSON string:</p>
 * <pre>{@code
 * Read a row of data: {"string":"String0","date":"2025-01-01","doubleData":0.56}
 * Read a row of data: {"string":"String1","date":"2025-01-02","doubleData":0.56}
 * ...
 * }</pre>
 *
 * <h2>Related Examples</h2>
 * <ul>
 *   <li>{@link org.apache.fesod.sheet.examples.read.BasicReadExample} — Uses a custom {@code ReadListener}
 *       with batch-save logic for production scenarios.</li>
 *   <li>{@link org.apache.fesod.sheet.examples.quickstart.SimpleWriteExample} — The write counterpart.</li>
 * </ul>
 *
 * @see FesodSheet#read(String, Class, org.apache.fesod.sheet.read.listener.ReadListener)
 * @see PageReadListener
 * @see DemoData
 */
@Slf4j
public class SimpleReadExample {

    public static void main(String[] args) {
        simpleRead();
    }

    /**
     * Reads an Excel file in three simple steps.
     *
     * <ol>
     *   <li><b>Define a data model</b> — Create a POJO with {@code @ExcelProperty} annotations
     *       (see {@link DemoData}).</li>
     *   <li><b>Provide a listener</b> — {@link PageReadListener} buffers rows and delivers them
     *       in batches via a lambda. You can also implement {@link org.apache.fesod.sheet.read.listener.ReadListener}
     *       directly for full control.</li>
     *   <li><b>Call the builder</b> — {@code FesodSheet.read(...).sheet().doRead()} starts
     *       the streaming parse, invoking your listener for each page of data.</li>
     * </ol>
     *
     * <p><b>Note:</b> The file is read in a streaming fashion. Once {@code doRead()} returns,
     * all rows have been processed and resources are automatically released.</p>
     */
    public static void simpleRead() {
        String fileName = ExampleFileUtil.getExamplePath("demo.xlsx");
        log.info("Reading file: {}", fileName);

        // Specify the class to read the data, then read the first sheet.
        FesodSheet.read(fileName, DemoData.class, new PageReadListener<DemoData>(dataList -> {
                    for (DemoData demoData : dataList) {
                        log.info("Read a row of data: {}", JSON.toJSONString(demoData));
                    }
                }))
                .sheet()
                .doRead();

        log.info("Successfully read file: {}", fileName);
    }
}
