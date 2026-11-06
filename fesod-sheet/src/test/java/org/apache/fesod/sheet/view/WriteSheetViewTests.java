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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.io.input.BOMInputStream;
import org.apache.fesod.sheet.FesodSheet;
import org.apache.fesod.sheet.support.ExcelTypeEnum;
import org.apache.fesod.sheet.testkit.Tags;
import org.apache.fesod.sheet.testkit.assertions.ExcelAssertions;
import org.apache.fesod.sheet.testkit.assertions.RowAssert;
import org.apache.fesod.sheet.testkit.base.AbstractExcelTest;
import org.apache.fesod.sheet.testkit.enums.ExcelFormat;
import org.apache.fesod.sheet.testkit.params.ExcelFormatSource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;

/**
 * Tests for the view-based export grouping feature using {@code @ExcelView}.
 */
@Tag(Tags.ROUND_TRIP)
@Tag(Tags.WRITE)
class WriteSheetViewTests extends AbstractExcelTest {

    static final String SHEET_NAME = "TestSheetView";

    private void verifyHeaders(File file, ExcelTypeEnum excelType, List<String> expectedHeads) throws Exception {
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
            try (ExcelAssertions ea = ExcelAssertions.assertThat(file)) {
                RowAssert ra = ea.workbook().hasSheetCount(1).sheet(SHEET_NAME).row(0);

                for (int i = 0; i < expectedHeads.size(); i++) {
                    ra.cell(i).hasStringValue(expectedHeads.get(i));
                }
            }
        }
    }

    // =========================================================================
    // Test by Class Type
    // =========================================================================

    @Nested
    class ClassBasedViewTests {

        @ParameterizedTest
        @ExcelFormatSource
        void testWriteWithBaseAndSubTypes(ExcelFormat format) throws Exception {
            File file = createTempFile("SheetView", format);
            List<String> expectedHeads = Arrays.asList("string1", "string2", "string5");

            // Write
            FesodSheet.write(file)
                    .head(WriteTypedViewsData.class)
                    .excelType(format.toExcelTypeEnum())
                    .groups(WriteViewStrategy.BaseView.class)
                    .sheet(SHEET_NAME)
                    .doWrite(Collections.emptyList());

            // Verify
            verifyHeaders(file, format.toExcelTypeEnum(), expectedHeads);
        }

        @ParameterizedTest
        @ExcelFormatSource
        void testWriteWithExactViewMatch(ExcelFormat format) throws Exception {
            File file = createTempFile("SheetView", format);
            List<String> expectedHeads = Arrays.asList("string2", "string3");

            // Write
            FesodSheet.write(file)
                    .head(WriteTypedViewsData.class)
                    .excelType(format.toExcelTypeEnum())
                    .groups(WriteViewStrategy.GroupA.class)
                    .sheet(SHEET_NAME)
                    .doWrite(Collections.emptyList());

            // Verify
            verifyHeaders(file, format.toExcelTypeEnum(), expectedHeads);
        }

        @ParameterizedTest
        @ExcelFormatSource
        void testWriteWithMultipleGroups(ExcelFormat format) throws Exception {
            File file = createTempFile("SheetView", format);
            List<String> expectedHeads = Arrays.asList("string2", "string3", "string4");

            // Write
            FesodSheet.write(file)
                    .head(WriteTypedViewsData.class)
                    .excelType(format.toExcelTypeEnum())
                    .groups(WriteViewStrategy.GroupA.class, WriteViewStrategy.GroupB.class)
                    .sheet(SHEET_NAME)
                    .doWrite(Collections.emptyList());

            // Verify
            verifyHeaders(file, format.toExcelTypeEnum(), expectedHeads);
        }
    }

    // =========================================================================
    // Test by String Label Type
    // =========================================================================

    @Nested
    class StringBasedViewTests {

        @ParameterizedTest
        @ExcelFormatSource
        void testWriteWithSingleView(ExcelFormat format) throws Exception {
            File file = createTempFile("SheetView", format);
            List<String> expectedHeads = Arrays.asList("string1", "string2");

            // Write
            FesodSheet.write(file)
                    .head(WriteNamedViewsData.class)
                    .excelType(format.toExcelTypeEnum())
                    .groups("base")
                    .sheet(SHEET_NAME)
                    .doWrite(Collections.emptyList());

            // Verify
            verifyHeaders(file, format.toExcelTypeEnum(), expectedHeads);
        }

        @ParameterizedTest
        @ExcelFormatSource
        void testWriteWithMultipleViews(ExcelFormat format) throws Exception {
            File file = createTempFile("SheetView", format);
            List<String> expectedHeads = Arrays.asList("string1", "string2", "string3");

            // Write
            FesodSheet.write(file)
                    .head(WriteNamedViewsData.class)
                    .excelType(format.toExcelTypeEnum())
                    .groups("base", "detail")
                    .sheet(SHEET_NAME)
                    .doWrite(Collections.emptyList());

            // Verify
            verifyHeaders(file, format.toExcelTypeEnum(), expectedHeads);
        }
    }

    // =========================================================================
    // Test for Conflict and Override Strategies, and Default Behavior
    // =========================================================================

    @Nested
    class ConflictAndEdgeCaseTests {

        @ParameterizedTest
        @ExcelFormatSource
        void testTagOverridesViewWhenCalledLast(ExcelFormat format) throws Exception {
            // The tags called later should override the previous groups.
            File file = createTempFile("SheetView", format);
            List<String> expectedHeads = Arrays.asList("string2", "string3");

            // Write
            FesodSheet.write(file)
                    .head(WriteMixedViewData.class)
                    .excelType(format.toExcelTypeEnum())
                    .groups(WriteViewStrategy.BaseView.class)
                    // Take effect
                    .groups("detail")
                    .sheet(SHEET_NAME)
                    .doWrite(Collections.emptyList());

            // Verify
            verifyHeaders(file, format.toExcelTypeEnum(), expectedHeads);
        }

        @ParameterizedTest
        @ExcelFormatSource
        void testWriteWithoutViewApi(ExcelFormat format) throws Exception {
            // Export all fields marked and unmarked with @ExcelView
            File file = createTempFile("SheetView", format);
            List<String> expectedHeads = Arrays.asList("string1", "string2", "string3", "defaultString");

            // Write
            FesodSheet.write(file)
                    .head(WriteMixedViewData.class)
                    .excelType(format.toExcelTypeEnum())
                    .sheet(SHEET_NAME)
                    .doWrite(Collections.emptyList());

            // Verify
            verifyHeaders(file, format.toExcelTypeEnum(), expectedHeads);
        }
    }
}
