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

package org.apache.fesod.sheet.examples.write;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.fesod.sheet.FesodSheet;
import org.apache.fesod.sheet.examples.util.ExampleFileUtil;
import org.apache.fesod.sheet.examples.write.data.DemoMergeData;
import org.apache.fesod.sheet.write.merge.LoopMergeStrategy;

/**
 * Demonstrates cell merging strategies when writing Excel files.
 *
 * <h2>Scenario</h2>
 * <p>You need to create an Excel report where certain cells are merged — for example,
 * merging repeated category names in the first column, or creating summary rows.
 * Fesod supports two approaches: annotation-based and strategy-based merging.</p>
 *
 * <h2>Approach 1: Annotation-Based ({@code @ContentLoopMerge})</h2>
 * <p>Annotate a field in the data class with {@code @ContentLoopMerge(eachRow = N)}
 * to automatically merge every N rows in that column. See {@link DemoMergeData}.</p>
 * <pre>
 * Result (eachRow = 2):
 * | String Title | Date Title          | Number Title |
 * |--------------|---------------------|--------------|
 * | String0      | 2025-01-01 00:00:00 | 0.56         |
 * | (merged)     | 2025-01-01 00:00:00 | 0.56         |
 * | String1      | 2025-01-01 00:00:00 | 0.56         |
 * | (merged)     | 2025-01-01 00:00:00 | 0.56         |
 * </pre>
 *
 * <h2>Approach 2: Strategy-Based ({@link LoopMergeStrategy})</h2>
 * <p>Register a {@code LoopMergeStrategy(eachRow, columnIndex)} as a write handler.
 * More flexible — can be configured at runtime and applied to any column.</p>
 * <pre>{@code
 * // Merge every 2 rows in column 0
 * LoopMergeStrategy strategy = new LoopMergeStrategy(2, 0);
 * FesodSheet.write(fileName, DemoMergeData.class)
 *     .registerWriteHandler(strategy)
 *     .sheet().doWrite(data);
 * }</pre>
 *
 * <h2>When to Use Which</h2>
 * <ul>
 *   <li><b>Annotation</b> — Simple, fixed merge patterns baked into the data class.</li>
 *   <li><b>Strategy</b> — Dynamic merging, multiple merge rules, or merging based on data content.</li>
 * </ul>
 *
 * <h2>Related Examples</h2>
 * <ul>
 *   <li>{@link StyleWriteExample} — Style customization.</li>
 *   <li>{@link BasicWriteExample} — Simple write without merging.</li>
 * </ul>
 *
 * @see org.apache.fesod.sheet.annotation.write.style.ContentLoopMerge
 * @see LoopMergeStrategy
 * @see DemoMergeData
 */
@Slf4j
public class MergeWriteExample {

    public static void main(String[] args) {
        mergeWrite();
    }

    /**
     * Demonstrates both annotation-based and strategy-based cell merging.
     *
     * <p>Writes two separate files:
     * <ol>
     *   <li><b>Annotation merge</b> — Uses {@code @ContentLoopMerge(eachRow = 2)} on the
     *       {@code string} field of {@link DemoMergeData}.</li>
     *   <li><b>Strategy merge</b> — Registers a {@link LoopMergeStrategy} to merge every
     *       2 rows in column 0.</li>
     * </ol>
     * Both produce the same visual result but via different mechanisms.</p>
     */
    public static void mergeWrite() {
        String fileName = ExampleFileUtil.getTempPath("mergeWrite" + System.currentTimeMillis() + ".xlsx");

        // Method 1: Use annotations (see DemoMergeData)
        FesodSheet.write(fileName, DemoMergeData.class)
                .sheet("Annotation Merge")
                .doWrite(data());
        log.info("Successfully wrote file: {}", fileName);

        // Method 2: Use a merge strategy
        fileName = ExampleFileUtil.getTempPath("mergeWriteStrategy" + System.currentTimeMillis() + ".xlsx");
        // Merge every 2 rows in the 0th column.
        LoopMergeStrategy loopMergeStrategy = new LoopMergeStrategy(2, 0);
        FesodSheet.write(fileName, DemoMergeData.class)
                .registerWriteHandler(loopMergeStrategy)
                .sheet("Strategy Merge")
                .doWrite(data());
        log.info("Successfully wrote file: {}", fileName);
    }

    private static List<DemoMergeData> data() {
        List<DemoMergeData> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            DemoMergeData data = new DemoMergeData();
            data.setString("String" + (i / 2)); // Same string for merged rows
            data.setDate(new Date());
            data.setDoubleData(0.56);
            list.add(data);
        }
        return list;
    }
}
