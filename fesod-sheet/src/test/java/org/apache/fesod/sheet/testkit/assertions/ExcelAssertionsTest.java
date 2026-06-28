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

package org.apache.fesod.sheet.testkit.assertions;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.apache.fesod.sheet.FesodSheet;
import org.apache.fesod.sheet.testkit.Tags;
import org.apache.fesod.sheet.testkit.models.SimpleData;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

@Tag(Tags.UNIT)
class ExcelAssertionsTest {

    @TempDir
    File tempDir;

    private File writeSimpleFile(String name) {
        File file = new File(tempDir, name);
        List<SimpleData> data = new ArrayList<SimpleData>();
        for (int i = 0; i < 3; i++) {
            SimpleData d = new SimpleData();
            d.setName("Name" + i);
            data.add(d);
        }
        FesodSheet.write(file, SimpleData.class).sheet().doWrite(data);
        return file;
    }

    @Test
    void assertThatOpensFile() {
        File file = writeSimpleFile("test.xlsx");
        ExcelAssertions ea = ExcelAssertions.assertThat(file);
        ea.close();
    }

    @Test
    void assertThatThrowsForMissingFile() {
        File missing = new File(tempDir, "nonexistent.xlsx");
        assertThrows(AssertionError.class, () -> ExcelAssertions.assertThat(missing));
    }

    @Test
    void sheetCountAssertion() {
        File file = writeSimpleFile("test.xlsx");
        try (ExcelAssertions ea = ExcelAssertions.assertThat(file)) {
            ea.workbook().hasSheetCount(1);
        }
    }

    @Test
    void sheetCountMismatchThrows() {
        File file = writeSimpleFile("test.xlsx");
        try (ExcelAssertions ea = ExcelAssertions.assertThat(file)) {
            assertThrows(AssertionError.class, () -> ea.workbook().hasSheetCount(5));
        }
    }

    @Test
    void sheetIndexOutOfBoundsThrows() {
        File file = writeSimpleFile("test.xlsx");
        try (ExcelAssertions ea = ExcelAssertions.assertThat(file)) {
            assertThrows(AssertionError.class, () -> ea.sheet(99));
        }
    }

    @Test
    void rowCountAssertion() {
        File file = writeSimpleFile("test.xlsx");
        try (ExcelAssertions ea = ExcelAssertions.assertThat(file)) {
            // 1 header row + 3 data rows = 4
            ea.sheet(0).hasRowCount(4);
        }
    }

    @Test
    void nullRowThrows() {
        File file = writeSimpleFile("test.xlsx");
        try (ExcelAssertions ea = ExcelAssertions.assertThat(file)) {
            assertThrows(AssertionError.class, () -> ea.sheet(0).row(999));
        }
    }

    @Test
    void nullCellThrows() {
        File file = writeSimpleFile("test.xlsx");
        try (ExcelAssertions ea = ExcelAssertions.assertThat(file)) {
            assertThrows(AssertionError.class, () -> ea.sheet(0).row(0).cell(999));
        }
    }

    @Test
    void stringValueCorrectPasses() {
        File file = writeSimpleFile("test.xlsx");
        try (ExcelAssertions ea = ExcelAssertions.assertThat(file)) {
            // Row 0 is header "Name", row 1 is "Name0"
            ea.sheet(0).row(0).cell(0).hasStringValue("Name");
            ea.sheet(0).row(1).cell(0).hasStringValue("Name0");
            ea.sheet(0).row(2).cell(0).hasStringValue("Name1");
            ea.sheet(0).row(3).cell(0).hasStringValue("Name2");
        }
    }

    @Test
    void stringValueIncorrectThrows() {
        File file = writeSimpleFile("test.xlsx");
        try (ExcelAssertions ea = ExcelAssertions.assertThat(file)) {
            assertThrows(AssertionError.class, () -> ea.sheet(0).row(1).cell(0).hasStringValue("wrong"));
        }
    }

    @Test
    void autoCloseableWorks() {
        File file = writeSimpleFile("test.xlsx");
        try (ExcelAssertions ea = ExcelAssertions.assertThat(file)) {
            ea.sheet(0).row(0).cell(0).hasStringValue("Name");
        }
        // Should not throw — workbook is closed
    }

    @Test
    void satisfiesEscapeHatch() {
        File file = writeSimpleFile("test.xlsx");
        try (ExcelAssertions ea = ExcelAssertions.assertThat(file)) {
            ea.sheet(0)
                    .row(1)
                    .cell(0)
                    .satisfies(cell -> assertTrue(cell.getStringCellValue().startsWith("Name")));
        }
    }

    @Test
    void chainedNavigationAndBack() {
        File file = writeSimpleFile("test.xlsx");
        try (ExcelAssertions ea = ExcelAssertions.assertThat(file)) {
            ea.sheet(0)
                    .row(1)
                    .cell(0)
                    .hasStringValue("Name0")
                    .and() // back to RowAssert
                    .and() // back to SheetAssert
                    .row(2)
                    .cell(0)
                    .hasStringValue("Name1");
        }
    }
}
