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

package org.apache.fesod.sheet.write.style.column;

import java.io.File;
import java.nio.file.Path;
import java.util.Collections;
import org.apache.fesod.sheet.FesodSheet;
import org.apache.fesod.sheet.testkit.Tags;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Regression tests for the per-instance width cache of {@link LongestMatchColumnWidthStyleStrategy}.
 */
@Tag(Tags.WRITE)
class LongestMatchColumnWidthStyleStrategyTest {

    private static final String LONG_VALUE = "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";

    /**
     * Control: when every writer gets its own strategy instance, both files get their own width.
     */
    @Test
    void separateStrategyInstancesComputeEachWorkbookWidth(@TempDir Path dir) throws Exception {
        File first = dir.resolve("separate-first.xlsx").toFile();
        File second = dir.resolve("separate-second.xlsx").toFile();

        write(first, LONG_VALUE, new LongestMatchColumnWidthStyleStrategy());
        write(second, "A", new LongestMatchColumnWidthStyleStrategy());

        Assertions.assertEquals(LONG_VALUE.length() * 256, columnWidth(first, 0));
        Assertions.assertEquals(1 * 256, columnWidth(second, 0));
    }

    /**
     * The width cache is keyed only by sheet number and is never cleared when a workbook finishes, so a strategy
     * instance reused for a second writer keeps the first workbook's maximum. As a result the second workbook's
     * column width is never set and stays at POI's default instead of the computed 1 * 256.
     */
    @Test
    void sharedStrategyInstanceDoesNotLeakWidthAcrossWorkbooks(@TempDir Path dir) throws Exception {
        File first = dir.resolve("shared-first.xlsx").toFile();
        File second = dir.resolve("shared-second.xlsx").toFile();

        LongestMatchColumnWidthStyleStrategy shared = new LongestMatchColumnWidthStyleStrategy();
        write(first, LONG_VALUE, shared);
        write(second, "A", shared);

        Assertions.assertEquals(LONG_VALUE.length() * 256, columnWidth(first, 0));
        Assertions.assertEquals(
                1 * 256,
                columnWidth(second, 0),
                "column width leaked from the previous workbook when the strategy instance was reused");
    }

    private static void write(File file, String value, LongestMatchColumnWidthStyleStrategy strategy) {
        FesodSheet.write(file)
                .registerWriteHandler(strategy)
                .sheet()
                .doWrite(Collections.singletonList(Collections.singletonList(value)));
    }

    private static int columnWidth(File file, int column) throws Exception {
        try (Workbook workbook = WorkbookFactory.create(file)) {
            Sheet sheet = workbook.getSheetAt(0);
            return sheet.getColumnWidth(column);
        }
    }
}
