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
import org.apache.fesod.sheet.ExcelReader;
import org.apache.fesod.sheet.FesodSheet;
import org.apache.fesod.sheet.examples.read.data.DemoData;
import org.apache.fesod.sheet.examples.read.listeners.DemoDataListener;
import org.apache.fesod.sheet.examples.util.ExampleFileUtil;
import org.apache.fesod.sheet.read.metadata.ReadSheet;

/**
 * Example demonstrating how to read multiple sheets from an Excel file.
 */
@Slf4j
public class MultiSheetReadExample {

    public static void main(String[] args) {
        repeatedRead();
    }

    /**
     * Read multiple sheets.
     */
    public static void repeatedRead() {
        String fileName = ExampleFileUtil.getExamplePath("demo.xlsx");
        log.info("Reading multiple sheets from file: {}", fileName);

        // 1. Read all sheets
        FesodSheet.read(fileName, DemoData.class, new DemoDataListener()).doReadAll();
        log.info("Read all sheets completed");

        // 2. Read specific sheets
        try (ExcelReader excelReader = FesodSheet.read(fileName).build()) {
            // Create ReadSheet objects for each sheet you want to read.
            ReadSheet readSheet1 = FesodSheet.readSheet(0)
                    .head(DemoData.class)
                    .registerReadListener(new DemoDataListener())
                    .build();
            ReadSheet readSheet2 = FesodSheet.readSheet(1)
                    .head(DemoData.class)
                    .registerReadListener(new DemoDataListener())
                    .build();

            // Read multiple sheets at once.
            excelReader.read(readSheet1, readSheet2);
        }
        log.info("Successfully read file: {}", fileName);
    }
}
