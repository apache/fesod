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

package org.apache.fesod.sheet.view;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.io.input.BOMInputStream;
import org.apache.fesod.sheet.FesodSheet;
import org.apache.fesod.sheet.support.ExcelTypeEnum;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Tests for the view-based export grouping feature using {@code @ExcelView}.
 */
class WriteSheetViewTests {

    private File write03;
    private File write07;
    private File writeCsv;

    @BeforeEach
    void setUp(@TempDir Path tempDir) {
        write03 = createTmpFile(tempDir, "write03.xls");
        write07 = createTmpFile(tempDir, "write07.xls");
        writeCsv = createTmpFile(tempDir, "writeCsv.csv");
    }

    private File createTmpFile(Path dir, String filename) {
        return new File(dir.resolve(filename).toString());
    }

    @FunctionalInterface
    interface WriteExecutor {
        void execute(File file, ExcelTypeEnum type, String sheetName) throws Exception;
    }

    private void doTestAllFormatsAndVerify(List<String> expectedHeads, WriteExecutor action) throws Exception {
        ExcelTypeEnum[] types = {ExcelTypeEnum.XLS, ExcelTypeEnum.XLSX, ExcelTypeEnum.CSV};
        File[] files = {write03, write07, writeCsv};
        String sheetName = "TestSheet";

        for (int i = 0; i < types.length; i++) {
            File currentFile = files[i];
            ExcelTypeEnum currentType = types[i];

            // Write
            action.execute(currentFile, currentType, sheetName);

            // Verify
            verifyHeaders(currentFile, currentType, sheetName, expectedHeads);
        }
    }

    private void verifyHeaders(File file, ExcelTypeEnum excelType, String sheetName, List<String> expectedHeads)
            throws Exception {
        if (excelType == ExcelTypeEnum.CSV) {
            try (InputStream is = BOMInputStream.builder()
                            .setInputStream(Files.newInputStream(file.toPath()))
                            .get();
                    Reader reader = new InputStreamReader(is, StandardCharsets.UTF_8)) {
                CSVParser parser = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(reader);

                Map<String, Integer> headerMap = parser.getHeaderMap();

                Assertions.assertNotNull(headerMap, "CSV file is empty");

                String[] headers = headerMap.keySet().toArray(new String[0]);
                Assertions.assertEquals(expectedHeads.size(), headers.length, "CSV Header count mismatch");
                for (int i = 0; i < expectedHeads.size(); i++) {
                    Assertions.assertEquals(expectedHeads.get(i), headers[i], "CSV Header text mismatch");
                }
            }
        } else {
            try (Workbook workbook = WorkbookFactory.create(file)) {
                Sheet sheet = workbook.getSheet(sheetName);

                Row headRow = sheet.getRow(0);
                Assertions.assertNotNull(headRow, "Excel header row is null");
                Assertions.assertEquals(
                        expectedHeads.size(),
                        headRow.getPhysicalNumberOfCells(),
                        "Excel Header count mismatch for " + excelType);

                for (int i = 0; i < expectedHeads.size(); i++) {
                    Assertions.assertEquals(
                            expectedHeads.get(i),
                            headRow.getCell(i).getStringCellValue(),
                            "Excel Header text mismatch for " + excelType);
                }
            }
        }
    }

    // =========================================================================
    // Test by Class Type
    // =========================================================================

    @Nested
    class ClassBasedViewTests {

        @Test
        void testWriteWithBaseAndSubTypes() throws Exception {
            List<String> expectedHeads = Arrays.asList("string1", "string2", "string5");

            doTestAllFormatsAndVerify(expectedHeads, (file, type, sheetName) -> FesodSheet.write(file)
                    .head(WriteTypedViewsData.class)
                    .excelType(type)
                    .groups(WriteViewStrategy.BaseView.class)
                    .sheet(sheetName)
                    .doWrite(Collections.emptyList()));
        }

        @Test
        void testWriteWithExactViewMatch() throws Exception {
            List<String> expectedHeads = Arrays.asList("string2", "string3");
            doTestAllFormatsAndVerify(expectedHeads, (file, type, sheetName) -> FesodSheet.write(file)
                    .head(WriteTypedViewsData.class)
                    .excelType(type)
                    .groups(WriteViewStrategy.GroupA.class)
                    .sheet(sheetName)
                    .doWrite(Collections.emptyList()));
        }

        @Test
        void testWriteWithMultipleGroups() throws Exception {
            List<String> expectedHeads = Arrays.asList("string2", "string3", "string4");
            doTestAllFormatsAndVerify(expectedHeads, (file, type, sheetName) -> FesodSheet.write(file)
                    .head(WriteTypedViewsData.class)
                    .excelType(type)
                    .groups(WriteViewStrategy.GroupA.class, WriteViewStrategy.GroupB.class)
                    .sheet(sheetName)
                    .doWrite(Collections.emptyList()));
        }
    }

    // =========================================================================
    // Test by String Label Type
    // =========================================================================

    @Nested
    class StringBasedViewTests {

        @Test
        void testWriteWithSingleView() throws Exception {
            List<String> expectedHeads = Arrays.asList("string1", "string2");
            doTestAllFormatsAndVerify(expectedHeads, (file, type, sheetName) -> FesodSheet.write(file)
                    .head(WriteNamedViewsData.class)
                    .excelType(type)
                    .groups("base")
                    .sheet(sheetName)
                    .doWrite(Collections.emptyList()));
        }

        @Test
        void testWriteWithMultipleViews() throws Exception {
            List<String> expectedHeads = Arrays.asList("string1", "string2", "string3");
            doTestAllFormatsAndVerify(expectedHeads, (file, type, sheetName) -> FesodSheet.write(file)
                    .head(WriteNamedViewsData.class)
                    .excelType(type)
                    .groups("base", "detail")
                    .sheet(sheetName)
                    .doWrite(Collections.emptyList()));
        }
    }

    // =========================================================================
    // Test for Conflict and Override Strategies, and Default Behavior
    // =========================================================================

    @Nested
    class ConflictAndEdgeCaseTests {

        @Test
        void testTagOverridesViewWhenCalledLast() throws Exception {
            // The tags called later should override the previous groups.
            List<String> expectedHeads = Arrays.asList("string2", "string3");

            doTestAllFormatsAndVerify(expectedHeads, (file, type, sheetName) -> FesodSheet.write(file)
                    .head(WriteMixedViewData.class)
                    .excelType(type)
                    .groups(WriteViewStrategy.BaseView.class)
                    // Take effect
                    .groups("detail")
                    .sheet(sheetName)
                    .doWrite(Collections.emptyList()));
        }

        @Test
        void testWriteWithoutViewApi() throws Exception {
            // Export all fields marked and unmarked with @ExcelView
            List<String> expectedHeads = Arrays.asList("string1", "string2", "string3", "defaultString");

            doTestAllFormatsAndVerify(expectedHeads, (file, type, sheetName) -> FesodSheet.write(file)
                    .head(WriteMixedViewData.class)
                    .excelType(type)
                    .sheet(sheetName)
                    .doWrite(Collections.emptyList()));
        }
    }
}
