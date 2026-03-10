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
import org.apache.fesod.sheet.examples.read.data.IndexOrNameData;
import org.apache.fesod.sheet.examples.util.ExampleFileUtil;
import org.apache.fesod.sheet.read.listener.PageReadListener;

/**
 * Demonstrates reading Excel columns by positional index or header name.
 *
 * <h2>Scenario</h2>
 * <p>Your Excel file may have columns in an unpredictable order, or you only need a subset
 * of columns. Instead of relying on field declaration order, you can explicitly map fields
 * to columns by index (0-based position) or by header name.</p>
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>{@code @ExcelProperty(index = 2)} — Forces the field to read from the 3rd column (0-based),
 *       regardless of header name or field order.</li>
 *   <li>{@code @ExcelProperty("String")} — Matches the column whose header text is "String".
 *       This is resilient to column reordering.</li>
 *   <li><b>Priority:</b> {@code index} &gt; {@code order} &gt; default field declaration order.
 *       If both {@code index} and name are specified, {@code index} takes priority.</li>
 * </ul>
 *
 * <h2>Mapping Example ({@link IndexOrNameData})</h2>
 * <pre>
 * Excel Layout:  | String | Date       | Number |
 * Column Index:  |   0    |     1      |    2   |
 *
 * IndexOrNameData.doubleData  ← Column index 2 ("Number")
 * IndexOrNameData.string      ← Header name "String" (Column 0)
 * IndexOrNameData.date        ← Header name "Date" (Column 1)
 * </pre>
 *
 * <h2>When to Use</h2>
 * <ul>
 *   <li>Excel columns may be shuffled by users (use name matching).</li>
 *   <li>You need only specific columns from a wide spreadsheet (use index).</li>
 *   <li>Different Excel versions have different column orders (use name matching).</li>
 * </ul>
 *
 * <h2>Related Examples</h2>
 * <ul>
 *   <li>{@link BasicReadExample} — Default column matching by field order.</li>
 *   <li>{@link NoModelReadExample} — Read without any model (raw map data).</li>
 * </ul>
 *
 * @see IndexOrNameData
 * @see org.apache.fesod.sheet.annotation.ExcelProperty
 */
@Slf4j
public class IndexOrNameReadExample {

    public static void main(String[] args) {
        indexOrNameRead();
    }

    /**
     * Reads using {@link IndexOrNameData} which combines index-based and name-based column matching.
     *
     * <p>The {@code doubleData} field reads from column index 2, while {@code string} and
     * {@code date} fields match by header name. This flexible approach handles varying
     * column layouts gracefully.</p>
     */
    public static void indexOrNameRead() {
        String fileName = ExampleFileUtil.getExamplePath("demo.xlsx");
        log.info("Reading file with index/name mapping: {}", fileName);

        FesodSheet.read(fileName, IndexOrNameData.class, new PageReadListener<IndexOrNameData>(dataList -> {
                    for (IndexOrNameData data : dataList) {
                        log.info("Read a row of data with index or name: {}", JSON.toJSONString(data));
                    }
                }))
                .sheet()
                .doRead();

        log.info("Successfully read file: {}", fileName);
    }
}
