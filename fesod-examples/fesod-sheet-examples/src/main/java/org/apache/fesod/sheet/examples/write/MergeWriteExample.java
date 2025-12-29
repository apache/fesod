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
 * Example demonstrating how to merge cells when writing an Excel file.
 */
@Slf4j
public class MergeWriteExample {

    public static void main(String[] args) {
        mergeWrite();
    }

    /**
     * Write with merged cells.
     */
    public static void mergeWrite() {
        String fileName = ExampleFileUtil.getTempPath("mergeWrite" + System.currentTimeMillis() + ".xlsx");

        // Method 1: Use annotations (see DemoMergeData)
        FesodSheet.write(fileName, DemoMergeData.class)
                .sheet("Annotation Merge")
                .doWrite(data());
        log.info("Successfully wrote file: {}", fileName);

        // Method 2: Use a merge strategy
        fileName = ExampleFileUtil.getPath() + "mergeWriteStrategy" + System.currentTimeMillis() + ".xlsx";
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
