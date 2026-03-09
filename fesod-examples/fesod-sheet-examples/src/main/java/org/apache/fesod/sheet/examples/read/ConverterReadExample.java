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

import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.fesod.sheet.FesodSheet;
import org.apache.fesod.sheet.examples.read.data.ConverterData;
import org.apache.fesod.sheet.examples.util.ExampleFileUtil;
import org.apache.fesod.sheet.read.listener.PageReadListener;

/**
 * Demonstrates reading Excel files with data format converters.
 *
 * <h2>Scenario</h2>
 * <p>Your Excel file contains dates and numbers in specific formats (e.g., "2025-01-01 12:30:00"
 * or "56.00%"), and you want them read as formatted {@code String} values rather than raw types.
 * Or you need a completely custom transformation for a column.</p>
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>{@link org.apache.fesod.sheet.annotation.format.DateTimeFormat} — Converts date cells
 *       to formatted strings using the specified pattern (e.g., {@code "yyyy-MM-dd HH:mm:ss"}).</li>
 *   <li>{@link org.apache.fesod.sheet.annotation.format.NumberFormat} — Converts number cells
 *       to formatted strings using {@link java.text.DecimalFormat} patterns (e.g., {@code "#.##%"}).</li>
 *   <li>{@link org.apache.fesod.sheet.examples.read.converters.CustomStringStringConverter} —
 *       A custom converter that prepends "Custom：" to string values, demonstrating
 *       the {@link org.apache.fesod.sheet.converters.Converter} interface.</li>
 * </ul>
 *
 * <h2>Data Class Mapping ({@link ConverterData})</h2>
 * <pre>
 * Excel Cell           → Java Field (String)
 * ─────────────────────────────────────────
 * "Hello"              → "Custom：Hello"       (via CustomStringStringConverter)
 * 2025-01-01 12:30:00  → "2025-01-01 12:30:00" (via @DateTimeFormat)
 * 0.56                 → "56%"                (via @NumberFormat("#.##%"))
 * </pre>
 *
 * <h2>Expected Behavior</h2>
 * <p>All fields in {@link ConverterData} are {@code String} type. Fesod applies the configured
 * converter/format to transform the raw Excel cell value before setting the field.</p>
 *
 * <h2>Related Examples</h2>
 * <ul>
 *   <li>{@link org.apache.fesod.sheet.examples.advanced.CustomConverterExample} —
 *       Register a custom converter at the builder level (applies to all matching fields).</li>
 *   <li>{@link BasicReadExample} — Read without converters (automatic type mapping).</li>
 * </ul>
 *
 * @see ConverterData
 * @see org.apache.fesod.sheet.converters.Converter
 * @see org.apache.fesod.sheet.annotation.format.DateTimeFormat
 * @see org.apache.fesod.sheet.annotation.format.NumberFormat
 */
@Slf4j
public class ConverterReadExample {

    public static void main(String[] args) {
        converterRead();
    }

    /**
     * Reads an Excel file with converters and format annotations applied.
     *
     * <p>Uses {@link PageReadListener} for simplicity. The actual conversion happens
     * transparently during parsing — by the time your listener receives the data,
     * all fields are already converted according to their annotations.</p>
     */
    public static void converterRead() {
        String fileName = ExampleFileUtil.getExamplePath("demo.xlsx");
        log.info("Reading file with converters: {}", fileName);

        FesodSheet.read(fileName, ConverterData.class, new PageReadListener<ConverterData>(dataList -> {
                    for (ConverterData data : dataList) {
                        log.info("Read a row of data with converter: {}", JSON.toJSONString(data));
                    }
                }))
                .sheet()
                .doRead();

        log.info("Successfully read file: {}", fileName);
    }
}
