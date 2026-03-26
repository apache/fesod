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
import org.apache.fesod.sheet.examples.read.listeners.NoModelDataListener;
import org.apache.fesod.sheet.examples.util.ExampleFileUtil;

/**
 * Demonstrates reading an Excel file without defining a Java data model.
 *
 * <h2>Scenario</h2>
 * <p>You don't know the Excel file's structure at compile time, or you want a quick way
 * to inspect any Excel file without creating a POJO. Each row is returned as a
 * {@code Map<Integer, String>} where the key is the column index (0-based)
 * and the value is the cell content as a string.</p>
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Omit the data class parameter from {@code FesodSheet.read()} — Fesod will use
 *       {@code Map<Integer, String>} as the default row type.</li>
 *   <li>{@link NoModelDataListener} extends
 *       {@link org.apache.fesod.sheet.event.AnalysisEventListener}{@code <Map<Integer, String>>}
 *       to receive raw map data.</li>
 *   <li>All cell values are converted to strings, regardless of the original Excel cell type.</li>
 * </ul>
 *
 * <h2>Row Data Format</h2>
 * <pre>
 * Excel Row:  | Hello | 2025-01-01 | 0.56 |
 *                 ↓           ↓         ↓
 * Map:  {0: "Hello", 1: "2025-01-01", 2: "0.56"}
 * </pre>
 *
 * <h2>When to Use</h2>
 * <ul>
 *   <li>Building generic Excel import tools that handle arbitrary file structures.</li>
 *   <li>Quick prototyping or debugging — inspect what's in a file.</li>
 *   <li>Schema-free processing where columns are dynamic.</li>
 * </ul>
 *
 * <h2>Related Examples</h2>
 * <ul>
 *   <li>{@link BasicReadExample} — Type-safe read with a data model (recommended for production).</li>
 *   <li>{@link IndexOrNameReadExample} — Flexible column matching with index or name.</li>
 * </ul>
 *
 * @see NoModelDataListener
 * @see org.apache.fesod.sheet.event.AnalysisEventListener
 */
@Slf4j
public class NoModelReadExample {

    public static void main(String[] args) {
        noModelRead();
    }

    /**
     * Reads an Excel file without specifying a data class.
     *
     * <p>When no class is provided to {@code FesodSheet.read()}, each row arrives
     * as a {@code Map<Integer, String>}. The {@link NoModelDataListener} processes
     * these maps with the same batch-save pattern used in typed listeners.</p>
     */
    public static void noModelRead() {
        String fileName = ExampleFileUtil.getExamplePath("demo.xlsx");
        log.info("Reading file (no model): {}", fileName);

        // No need to specify a class, just use NoModelDataListener.
        FesodSheet.read(fileName, new NoModelDataListener()).sheet().doRead();

        log.info("Successfully read file: {}", fileName);
    }
}
