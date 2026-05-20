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

package org.apache.fesod.sheet.annotation.composite;

import java.io.File;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;
import org.apache.fesod.sheet.FesodSheet;
import org.apache.fesod.sheet.annotation.ExcelProperty;
import org.apache.fesod.sheet.annotation.format.DateTimeFormat;
import org.apache.fesod.sheet.annotation.format.NumberFormat;
import org.apache.fesod.sheet.annotation.write.style.ColumnWidth;
import org.apache.fesod.sheet.annotation.write.style.ContentFontStyle;
import org.apache.fesod.sheet.annotation.write.style.ContentLoopMerge;
import org.apache.fesod.sheet.annotation.write.style.ContentRowHeight;
import org.apache.fesod.sheet.annotation.write.style.ContentStyle;
import org.apache.fesod.sheet.annotation.write.style.HeadFontStyle;
import org.apache.fesod.sheet.annotation.write.style.HeadRowHeight;
import org.apache.fesod.sheet.annotation.write.style.HeadStyle;
import org.apache.fesod.sheet.annotation.write.style.OnceAbsoluteMerge;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Integration tests: composable annotations produce
 * identical output to their equivalent direct annotations.
 * <p />
 * Covered inner annotations:
 * <ul>
 *   <li>{@link ExcelProperty}</li>
 *   <li>{@link DateTimeFormat}</li>
 *   <li>{@link NumberFormat}</li>
 *   <li>{@link ColumnWidth}</li>
 *   <li>{@link ContentFontStyle}</li>
 *   <li>{@link ContentLoopMerge}</li>
 *   <li>{@link ContentRowHeight}</li>
 *   <li>{@link ContentStyle}</li>
 *   <li>{@link HeadFontStyle}</li>
 *   <li>{@link HeadRowHeight}</li>
 *   <li>{@link HeadStyle}</li>
 *   <li>{@link OnceAbsoluteMerge}</li>
 * </ul>
 * Covered test scenarios:
 * <ul>
 *   <li>Field-level composable/direct</li>
 *   <li>Class-level composable/direct</li>
 *   <li>Mixed-level composable/direct</li>
 * </ul>
 */
class IntegrationCompositeAnnotationTest {

    private static final String FILE_GROUPS_METHOD =
            "org.apache.fesod.sheet.annotation.composite.IntegrationCompositeAnnotationTest#fileGroups";

    @TempDir
    static Path dir;

    private static File composite07;
    private static File direct07;
    private static File composite03;
    private static File direct03;

    @BeforeAll
    static void setup() {
        composite07 = createTmpFile("composite07.xlsx");
        direct07 = createTmpFile("direct07.xlsx");
        composite03 = createTmpFile("composite03.xls");
        direct03 = createTmpFile("direct03.xls");
    }

    private static File createTmpFile(String filename) {
        return new File(dir.resolve(filename).toString());
    }

    static Stream<Arguments> fileGroups() {
        return Stream.of(Arguments.of(composite07, direct07), Arguments.of(composite03, direct03));
    }

    // ====================================================================
    //  Helper
    // ====================================================================

    private <C, D> void doWrite(
            File compositeFile,
            File directFile,
            Class<C> compositeClass,
            Class<D> directClass,
            List<C> compositeData,
            List<D> directData)
            throws Exception {

        try {
            FesodSheet.write(compositeFile, compositeClass)
                    .enableMetaMarked(true)
                    .sheet(0)
                    .doWrite(compositeData);

            FesodSheet.write(directFile, directClass)
                    .enableMetaMarked(false)
                    .sheet(0)
                    .doWrite(directData);
        } catch (Exception ex) {
            Assertions.fail("Data write failed.", ex);
        }
    }

    private void assertHeadNames(Row head, List<String> headNames, String label) {
        Assertions.assertNotNull(headNames);
        for (int i = 0; i < headNames.size(); i++) {
            String actual = head.getCell(i).getStringCellValue();
            String expected = headNames.get(i);
            Assertions.assertEquals(
                    expected, actual, "[" + label + "] The header of column [" + i + "] is unexpected.");
        }
    }

    private Date dateOf(int year, int month, int day) {
        return Date.from(LocalDate.of(year, month, day)
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant());
    }

    // ====================================================================
    //  Field-Level Tests
    // ====================================================================

    @Nested
    @DisplayName("Field-level composable annotations")
    class FieldLevelTests {

        @ParameterizedTest
        @MethodSource(FILE_GROUPS_METHOD)
        void shouldProduceIdenticalOutput_whenUsingExcelProperty(File composite, File direct) throws Exception {
            // Given: composable @ExcelProperty("Name") vs direct @ExcelProperty("Name") with identical data
            List<IntegrationExcelDatas.FieldExcelProperty.Composite> compositeData =
                    IntegrationExcelDatas.FieldExcelProperty.compositeData();
            List<IntegrationExcelDatas.FieldExcelProperty.Direct> directData =
                    IntegrationExcelDatas.FieldExcelProperty.directData();

            // When
            doWrite(
                    composite,
                    direct,
                    IntegrationExcelDatas.FieldExcelProperty.Composite.class,
                    IntegrationExcelDatas.FieldExcelProperty.Direct.class,
                    compositeData,
                    directData);

            // Then
            WorkbookAsserts.build(composite, "Composite", direct, "Direct").assertMulti((label, workbook) -> {
                Assertions.assertEquals(1, workbook.getNumberOfSheets());

                Sheet sheet = workbook.getSheetAt(0);
                Assertions.assertEquals(6, sheet.getPhysicalNumberOfRows());

                // head row
                Row headRow = sheet.getRow(0);
                Assertions.assertEquals(1, headRow.getPhysicalNumberOfCells());
                assertHeadNames(headRow, Arrays.asList("Name"), label);

                // data rows
                for (int i = 0; i < 5; i++) {
                    Row data = sheet.getRow(i + 1);

                    Assertions.assertEquals(1, data.getPhysicalNumberOfCells());
                    Assertions.assertEquals(CellType.STRING, data.getCell(0).getCellType());
                    Assertions.assertEquals(
                            "Name" + i,
                            data.getCell(0).getStringCellValue(),
                            "[" + label + "] The data of row[" + i + "] column[0] is unexpected.");
                }
            });
        }

        @ParameterizedTest
        @MethodSource(FILE_GROUPS_METHOD)
        void shouldProduceIdenticalOutput_whenUsingDateTimeFormat(File composite, File direct) throws Exception {
            // Given: composable @DateTimeFormat("yyyy-MM-dd") vs direct equivalent
            List<IntegrationExcelDatas.FieldDateTimeFormat.Composite> compositeData =
                    IntegrationExcelDatas.FieldDateTimeFormat.compositeData();
            List<IntegrationExcelDatas.FieldDateTimeFormat.Direct> directData =
                    IntegrationExcelDatas.FieldDateTimeFormat.directData();

            // When
            doWrite(
                    composite,
                    direct,
                    IntegrationExcelDatas.FieldDateTimeFormat.Composite.class,
                    IntegrationExcelDatas.FieldDateTimeFormat.Direct.class,
                    compositeData,
                    directData);

            // Then
            WorkbookAsserts.build(composite, "Composite", direct, "Direct").assertMulti((label, workbook) -> {
                Assertions.assertEquals(1, workbook.getNumberOfSheets());

                Sheet sheet = workbook.getSheetAt(0);
                Assertions.assertEquals(6, sheet.getPhysicalNumberOfRows());

                // head row
                Row headRow = sheet.getRow(0);
                Assertions.assertEquals(1, headRow.getPhysicalNumberOfCells());
                assertHeadNames(headRow, Arrays.asList("Name"), label);

                // data rows
                for (int i = 0; i < 5; i++) {
                    Row data = sheet.getRow(i + 1);

                    Assertions.assertEquals(1, data.getPhysicalNumberOfCells());
                    Assertions.assertEquals(CellType.NUMERIC, data.getCell(0).getCellType());
                    Assertions.assertEquals(
                            dateOf(2026, 1, i + 1),
                            data.getCell(0).getDateCellValue(),
                            "[" + label + "] The data of row[" + i + "] column[0] is unexpected.");
                    Assertions.assertEquals(
                            "yyyy-MM-dd",
                            data.getCell(0).getCellStyle().getDataFormatString(),
                            "[" + label + "] The data format of row[" + i + "] column[0] is unexpected.");
                }
            });
        }

        @ParameterizedTest
        @MethodSource(FILE_GROUPS_METHOD)
        void shouldProduceIdenticalOutput_whenUsingNumberFormat(File composite, File direct) throws Exception {
            // Given: composable @NumberFormat("#,##0.00") vs direct equivalent
            List<IntegrationExcelDatas.FieldNumberFormat.Composite> compositeData =
                    IntegrationExcelDatas.FieldNumberFormat.compositeData();
            List<IntegrationExcelDatas.FieldNumberFormat.Direct> directData =
                    IntegrationExcelDatas.FieldNumberFormat.directData();

            // When
            doWrite(
                    composite,
                    direct,
                    IntegrationExcelDatas.FieldNumberFormat.Composite.class,
                    IntegrationExcelDatas.FieldNumberFormat.Direct.class,
                    compositeData,
                    directData);

            // Then
            WorkbookAsserts.build(composite, "Composite", direct, "Direct").assertMulti((label, workbook) -> {
                Assertions.assertEquals(1, workbook.getNumberOfSheets());

                Sheet sheet = workbook.getSheetAt(0);
                Assertions.assertEquals(6, sheet.getPhysicalNumberOfRows());

                // head row
                Row headRow = sheet.getRow(0);
                Assertions.assertEquals(1, headRow.getPhysicalNumberOfCells());
                assertHeadNames(headRow, Arrays.asList("Name"), label);

                // data rows
                for (int i = 0; i < 5; i++) {
                    Row data = sheet.getRow(i + 1);

                    Assertions.assertEquals(1, data.getPhysicalNumberOfCells());
                    Assertions.assertEquals(CellType.NUMERIC, data.getCell(0).getCellType());
                    Assertions.assertEquals(
                            100.0 + i * 10.5,
                            data.getCell(0).getNumericCellValue(),
                            "[" + label + "] The data of row[" + i + "] column[0] is unexpected.");
                    Assertions.assertEquals(
                            "#,##0.00",
                            data.getCell(0).getCellStyle().getDataFormatString(),
                            "[" + label + "] The data format of row[" + i + "] column[0] is unexpected.");
                }
            });
        }

        @ParameterizedTest
        @MethodSource(FILE_GROUPS_METHOD)
        void shouldProduceIdenticalOutput_whenUsingColumnWidth(File composite, File direct) throws Exception {
            // Given: composable @ColumnWidth(25) on field vs direct equivalent
            List<IntegrationExcelDatas.FieldColumnWidth.Composite> compositeData =
                    IntegrationExcelDatas.FieldColumnWidth.compositeData();
            List<IntegrationExcelDatas.FieldColumnWidth.Direct> directData =
                    IntegrationExcelDatas.FieldColumnWidth.directData();

            // When
            doWrite(
                    composite,
                    direct,
                    IntegrationExcelDatas.FieldColumnWidth.Composite.class,
                    IntegrationExcelDatas.FieldColumnWidth.Direct.class,
                    compositeData,
                    directData);

            // Then
            WorkbookAsserts.build(composite, "Composite", direct, "Direct").assertMulti((label, workbook) -> {
                Assertions.assertEquals(1, workbook.getNumberOfSheets());

                Sheet sheet = workbook.getSheetAt(0);
                Assertions.assertEquals(6, sheet.getPhysicalNumberOfRows());

                // head row
                Row headRow = sheet.getRow(0);
                Assertions.assertEquals(1, headRow.getPhysicalNumberOfCells());
                assertHeadNames(headRow, Arrays.asList("Name"), label);

                // data rows
                for (int i = 0; i < 5; i++) {
                    Row data = sheet.getRow(i + 1);

                    Assertions.assertEquals(1, data.getPhysicalNumberOfCells());
                    Assertions.assertEquals(CellType.STRING, data.getCell(0).getCellType());
                    Assertions.assertEquals(
                            "Name" + i,
                            data.getCell(0).getStringCellValue(),
                            "[" + label + "] The data of row[" + i + "] column[0] is unexpected.");
                    Assertions.assertEquals(
                            25 * 256,
                            sheet.getColumnWidth(0),
                            "[" + label + "] The column width of column[0] is unexpected.");
                }
            });
        }

        @ParameterizedTest
        @MethodSource(FILE_GROUPS_METHOD)
        void shouldProduceIdenticalOutput_whenUsingHeadStyle(File composite, File direct) throws Exception {
            // Given: composable @HeadStyle(...) on field vs direct equivalent
            List<IntegrationExcelDatas.FieldHeadStyle.Composite> compositeData =
                    IntegrationExcelDatas.FieldHeadStyle.compositeData();
            List<IntegrationExcelDatas.FieldHeadStyle.Direct> directData =
                    IntegrationExcelDatas.FieldHeadStyle.directData();

            // When
            doWrite(
                    composite,
                    direct,
                    IntegrationExcelDatas.FieldHeadStyle.Composite.class,
                    IntegrationExcelDatas.FieldHeadStyle.Direct.class,
                    compositeData,
                    directData);

            // Then
            WorkbookAsserts.build(composite, "Composite", direct, "Direct").assertMulti((label, workbook) -> {
                Assertions.assertEquals(1, workbook.getNumberOfSheets());

                Sheet sheet = workbook.getSheetAt(0);
                Assertions.assertEquals(6, sheet.getPhysicalNumberOfRows());

                // head row
                Row headRow = sheet.getRow(0);
                Assertions.assertEquals(1, headRow.getPhysicalNumberOfCells());
                assertHeadNames(headRow, Arrays.asList("Name"), label);

                CellStyle headStyle = headRow.getCell(0).getCellStyle();
                Assertions.assertEquals(
                        HorizontalAlignment.CENTER,
                        headStyle.getAlignment(),
                        "[" + label + "] The horizontal alignment of head row is unexpected.");
                Assertions.assertEquals(
                        42,
                        headStyle.getFillForegroundColor(),
                        "[" + label + "] The fill foreground color of head row is unexpected.");
                Assertions.assertEquals(
                        FillPatternType.SOLID_FOREGROUND,
                        headStyle.getFillPattern(),
                        "[" + label + "] The fill pattern of head row is unexpected.");

                // data rows
                for (int i = 0; i < 5; i++) {
                    Row data = sheet.getRow(i + 1);

                    Assertions.assertEquals(1, data.getPhysicalNumberOfCells());
                    Assertions.assertEquals(CellType.STRING, data.getCell(0).getCellType());
                    Assertions.assertEquals(
                            "Name" + i,
                            data.getCell(0).getStringCellValue(),
                            "[" + label + "] The data of row[" + i + "] column[0] is unexpected.");
                }
            });
        }

        @ParameterizedTest
        @MethodSource(FILE_GROUPS_METHOD)
        void shouldProduceIdenticalOutput_whenUsingHeadFontStyle(File composite, File direct) throws Exception {
            // Given: composable @HeadFontStyle(bold=true, fontHeight=14) on field vs direct equivalent
            List<IntegrationExcelDatas.FieldHeadFontStyle.Composite> compositeData =
                    IntegrationExcelDatas.FieldHeadFontStyle.compositeData();
            List<IntegrationExcelDatas.FieldHeadFontStyle.Direct> directData =
                    IntegrationExcelDatas.FieldHeadFontStyle.directData();

            // When
            doWrite(
                    composite,
                    direct,
                    IntegrationExcelDatas.FieldHeadFontStyle.Composite.class,
                    IntegrationExcelDatas.FieldHeadFontStyle.Direct.class,
                    compositeData,
                    directData);

            // Then
            WorkbookAsserts.build(composite, "Composite", direct, "Direct").assertMulti((label, workbook) -> {
                Assertions.assertEquals(1, workbook.getNumberOfSheets());

                Sheet sheet = workbook.getSheetAt(0);
                Assertions.assertEquals(6, sheet.getPhysicalNumberOfRows());

                // head row
                Row headRow = sheet.getRow(0);
                Assertions.assertEquals(1, headRow.getPhysicalNumberOfCells());
                assertHeadNames(headRow, Arrays.asList("Name"), label);

                Font headFont =
                        workbook.getFontAt(headRow.getCell(0).getCellStyle().getFontIndex());
                Assertions.assertTrue(headFont.getBold(), "[" + label + "] The bold of head row font is unexpected.");
                Assertions.assertEquals(
                        14,
                        headFont.getFontHeightInPoints(),
                        "[" + label + "] The font height of head row is unexpected.");

                // data rows
                for (int i = 0; i < 5; i++) {
                    Row data = sheet.getRow(i + 1);

                    Assertions.assertEquals(1, data.getPhysicalNumberOfCells());
                    Assertions.assertEquals(CellType.STRING, data.getCell(0).getCellType());
                    Assertions.assertEquals(
                            "Name" + i,
                            data.getCell(0).getStringCellValue(),
                            "[" + label + "] The data of row[" + i + "] column[0] is unexpected.");
                }
            });
        }

        @ParameterizedTest
        @MethodSource(FILE_GROUPS_METHOD)
        void shouldProduceIdenticalOutput_whenUsingContentStyle(File composite, File direct) throws Exception {
            // Given: composable @ContentStyle(wrapped=true, verticalAlignment=CENTER) on field vs direct equivalent
            List<IntegrationExcelDatas.FieldContentStyle.Composite> compositeData =
                    IntegrationExcelDatas.FieldContentStyle.compositeData();
            List<IntegrationExcelDatas.FieldContentStyle.Direct> directData =
                    IntegrationExcelDatas.FieldContentStyle.directData();

            // When
            doWrite(
                    composite,
                    direct,
                    IntegrationExcelDatas.FieldContentStyle.Composite.class,
                    IntegrationExcelDatas.FieldContentStyle.Direct.class,
                    compositeData,
                    directData);

            // Then
            WorkbookAsserts.build(composite, "Composite", direct, "Direct").assertMulti((label, workbook) -> {
                Assertions.assertEquals(1, workbook.getNumberOfSheets());

                Sheet sheet = workbook.getSheetAt(0);
                Assertions.assertEquals(6, sheet.getPhysicalNumberOfRows());

                // head row
                Row headRow = sheet.getRow(0);
                Assertions.assertEquals(1, headRow.getPhysicalNumberOfCells());
                assertHeadNames(headRow, Arrays.asList("Name"), label);

                // data rows
                for (int i = 0; i < 5; i++) {
                    Row data = sheet.getRow(i + 1);

                    Assertions.assertEquals(1, data.getPhysicalNumberOfCells());
                    Assertions.assertEquals(CellType.STRING, data.getCell(0).getCellType());
                    Assertions.assertEquals(
                            "Name" + i,
                            data.getCell(0).getStringCellValue(),
                            "[" + label + "] The data of row[" + i + "] column[0] is unexpected.");

                    CellStyle contentStyle = data.getCell(0).getCellStyle();
                    Assertions.assertTrue(
                            contentStyle.getWrapText(),
                            "[" + label + "] The wrap text of row[" + i + "] is unexpected.");
                    Assertions.assertEquals(
                            VerticalAlignment.CENTER,
                            contentStyle.getVerticalAlignment(),
                            "[" + label + "] The vertical alignment of row[" + i + "] is unexpected.");
                }
            });
        }

        @ParameterizedTest
        @MethodSource(FILE_GROUPS_METHOD)
        void shouldProduceIdenticalOutput_whenUsingContentFontStyle(File composite, File direct) throws Exception {
            // Given: composable @ContentFontStyle(italic=true, fontName="Arial") on field vs direct equivalent
            List<IntegrationExcelDatas.FieldContentFontStyle.Composite> compositeData =
                    IntegrationExcelDatas.FieldContentFontStyle.compositeData();
            List<IntegrationExcelDatas.FieldContentFontStyle.Direct> directData =
                    IntegrationExcelDatas.FieldContentFontStyle.directData();

            // When
            doWrite(
                    composite,
                    direct,
                    IntegrationExcelDatas.FieldContentFontStyle.Composite.class,
                    IntegrationExcelDatas.FieldContentFontStyle.Direct.class,
                    compositeData,
                    directData);

            // Then
            WorkbookAsserts.build(composite, "Composite", direct, "Direct").assertMulti((label, workbook) -> {
                Assertions.assertEquals(1, workbook.getNumberOfSheets());

                Sheet sheet = workbook.getSheetAt(0);
                Assertions.assertEquals(6, sheet.getPhysicalNumberOfRows());

                // head row
                Row headRow = sheet.getRow(0);
                Assertions.assertEquals(1, headRow.getPhysicalNumberOfCells());
                assertHeadNames(headRow, Arrays.asList("Name"), label);

                // data rows
                for (int i = 0; i < 5; i++) {
                    Row data = sheet.getRow(i + 1);

                    Assertions.assertEquals(1, data.getPhysicalNumberOfCells());
                    Assertions.assertEquals(CellType.STRING, data.getCell(0).getCellType());
                    Assertions.assertEquals(
                            "Name" + i,
                            data.getCell(0).getStringCellValue(),
                            "[" + label + "] The data of row[" + i + "] column[0] is unexpected.");

                    Font contentFont =
                            workbook.getFontAt(data.getCell(0).getCellStyle().getFontIndex());
                    Assertions.assertTrue(
                            contentFont.getItalic(),
                            "[" + label + "] The italic of row[" + i + "] font is unexpected.");
                    Assertions.assertEquals(
                            "Arial",
                            contentFont.getFontName(),
                            "[" + label + "] The font name of row[" + i + "] is unexpected.");
                }
            });
        }

        @ParameterizedTest
        @MethodSource(FILE_GROUPS_METHOD)
        void shouldProduceIdenticalOutput_whenUsingContentLoopMerge(File composite, File direct) throws Exception {
            // Given: composable @ContentLoopMerge(eachRow=2, columnExtend=1) on field vs direct equivalent
            List<IntegrationExcelDatas.FieldContentLoopMerge.Composite> compositeData =
                    IntegrationExcelDatas.FieldContentLoopMerge.compositeData();
            List<IntegrationExcelDatas.FieldContentLoopMerge.Direct> directData =
                    IntegrationExcelDatas.FieldContentLoopMerge.directData();

            // When
            doWrite(
                    composite,
                    direct,
                    IntegrationExcelDatas.FieldContentLoopMerge.Composite.class,
                    IntegrationExcelDatas.FieldContentLoopMerge.Direct.class,
                    compositeData,
                    directData);

            // Then
            WorkbookAsserts.build(composite, "Composite", direct, "Direct").assertMulti((label, workbook) -> {
                Assertions.assertEquals(1, workbook.getNumberOfSheets());

                Sheet sheet = workbook.getSheetAt(0);
                Assertions.assertEquals(6, sheet.getPhysicalNumberOfRows());

                // head row
                Row headRow = sheet.getRow(0);
                Assertions.assertEquals(1, headRow.getPhysicalNumberOfCells());
                assertHeadNames(headRow, Arrays.asList("Name"), label);

                // data rows
                for (int i = 0; i < 5; i++) {
                    Row data = sheet.getRow(i + 1);

                    Assertions.assertEquals(1, data.getPhysicalNumberOfCells());
                    Assertions.assertEquals(CellType.STRING, data.getCell(0).getCellType());
                    Assertions.assertEquals(
                            "Name" + i,
                            data.getCell(0).getStringCellValue(),
                            "[" + label + "] The data of row[" + i + "] column[0] is unexpected.");
                }

                // merged regions: every 2 rows merged
                List<CellRangeAddress> merges = sheet.getMergedRegions();
                Assertions.assertEquals(
                        3, merges.size(), "[" + label + "] The number of merged regions is unexpected.");
                Assertions.assertEquals(
                        "A2:A3",
                        merges.get(0).formatAsString(),
                        "[" + label + "] The first merged region is unexpected.");
                Assertions.assertEquals(
                        "A4:A5",
                        merges.get(1).formatAsString(),
                        "[" + label + "] The second merged region is unexpected.");
                Assertions.assertEquals(
                        "A6:A7",
                        merges.get(2).formatAsString(),
                        "[" + label + "] The last merged region is unexpected.");
            });
        }

        @ParameterizedTest
        @MethodSource(FILE_GROUPS_METHOD)
        void shouldProduceIdenticalOutput_whenUsingExcelPropertyAliasFor(File composite, File direct) throws Exception {
            // Given: @AliasFor(value) → @ExcelProperty("Custom Name") — same attribute name
            List<IntegrationExcelDatas.AliasForExcelProperty.Composite> compositeData =
                    IntegrationExcelDatas.AliasForExcelProperty.compositeData();
            List<IntegrationExcelDatas.AliasForExcelProperty.Direct> directData =
                    IntegrationExcelDatas.AliasForExcelProperty.directData();

            // When
            doWrite(
                    composite,
                    direct,
                    IntegrationExcelDatas.AliasForExcelProperty.Composite.class,
                    IntegrationExcelDatas.AliasForExcelProperty.Direct.class,
                    compositeData,
                    directData);

            // Then
            WorkbookAsserts.build(composite, "Composite", direct, "Direct").assertMulti((label, workbook) -> {
                Assertions.assertEquals(1, workbook.getNumberOfSheets());

                Sheet sheet = workbook.getSheetAt(0);
                Assertions.assertEquals(6, sheet.getPhysicalNumberOfRows());

                // head row
                Row headRow = sheet.getRow(0);
                Assertions.assertEquals(1, headRow.getPhysicalNumberOfCells());
                assertHeadNames(headRow, Arrays.asList("Custom Name"), label);

                // data rows
                for (int i = 0; i < 5; i++) {
                    Row data = sheet.getRow(i + 1);

                    Assertions.assertEquals(1, data.getPhysicalNumberOfCells());
                    Assertions.assertEquals(CellType.STRING, data.getCell(0).getCellType());
                    Assertions.assertEquals(
                            "Name" + i,
                            data.getCell(0).getStringCellValue(),
                            "[" + label + "] The data of row[" + i + "] column[0] is unexpected.");
                }
            });
        }

        @ParameterizedTest
        @MethodSource(FILE_GROUPS_METHOD)
        void shouldProduceIdenticalOutput_whenUsingColumnWidthAliasFor(File composite, File direct) throws Exception {
            // Given: @AliasFor(width=20) → @ColumnWidth(20) — renamed attribute
            List<IntegrationExcelDatas.AliasForColumnWidth.Composite> compositeData =
                    IntegrationExcelDatas.AliasForColumnWidth.compositeData();
            List<IntegrationExcelDatas.AliasForColumnWidth.Direct> directData =
                    IntegrationExcelDatas.AliasForColumnWidth.directData();

            // When
            doWrite(
                    composite,
                    direct,
                    IntegrationExcelDatas.AliasForColumnWidth.Composite.class,
                    IntegrationExcelDatas.AliasForColumnWidth.Direct.class,
                    compositeData,
                    directData);

            // Then
            WorkbookAsserts.build(composite, "Composite", direct, "Direct").assertMulti((label, workbook) -> {
                Assertions.assertEquals(1, workbook.getNumberOfSheets());

                Sheet sheet = workbook.getSheetAt(0);
                Assertions.assertEquals(6, sheet.getPhysicalNumberOfRows());

                // head row
                Row headRow = sheet.getRow(0);
                Assertions.assertEquals(1, headRow.getPhysicalNumberOfCells());
                assertHeadNames(headRow, Arrays.asList("Name"), label);

                // data rows
                for (int i = 0; i < 5; i++) {
                    Row data = sheet.getRow(i + 1);

                    Assertions.assertEquals(1, data.getPhysicalNumberOfCells());
                    Assertions.assertEquals(CellType.STRING, data.getCell(0).getCellType());
                    Assertions.assertEquals(
                            "Name" + i,
                            data.getCell(0).getStringCellValue(),
                            "[" + label + "] The data of row[" + i + "] column[0] is unexpected.");
                    Assertions.assertEquals(
                            20 * 256,
                            sheet.getColumnWidth(0),
                            "[" + label + "] The column width of column[0] is unexpected.");
                }
            });
        }

        @ParameterizedTest
        @MethodSource(FILE_GROUPS_METHOD)
        void shouldProduceIdenticalOutput_whenUsingDateTimeFormatAliasFor(File composite, File direct)
                throws Exception {
            // Given: @AliasFor(pattern="yyyy/MM/dd") → @DateTimeFormat("yyyy/MM/dd") — renamed attribute
            List<IntegrationExcelDatas.AliasForDateTimeFormat.Composite> compositeData =
                    IntegrationExcelDatas.AliasForDateTimeFormat.compositeData();
            List<IntegrationExcelDatas.AliasForDateTimeFormat.Direct> directData =
                    IntegrationExcelDatas.AliasForDateTimeFormat.directData();

            // When
            doWrite(
                    composite,
                    direct,
                    IntegrationExcelDatas.AliasForDateTimeFormat.Composite.class,
                    IntegrationExcelDatas.AliasForDateTimeFormat.Direct.class,
                    compositeData,
                    directData);

            // Then
            WorkbookAsserts.build(composite, "Composite", direct, "Direct").assertMulti((label, workbook) -> {
                Assertions.assertEquals(1, workbook.getNumberOfSheets());

                Sheet sheet = workbook.getSheetAt(0);
                Assertions.assertEquals(6, sheet.getPhysicalNumberOfRows());

                // head row
                Row headRow = sheet.getRow(0);
                Assertions.assertEquals(1, headRow.getPhysicalNumberOfCells());
                assertHeadNames(headRow, Arrays.asList("Name"), label);

                // data rows
                for (int i = 0; i < 5; i++) {
                    Row data = sheet.getRow(i + 1);

                    Assertions.assertEquals(1, data.getPhysicalNumberOfCells());
                    Assertions.assertEquals(CellType.NUMERIC, data.getCell(0).getCellType());
                    Assertions.assertEquals(
                            dateOf(2026, 1, i + 1),
                            data.getCell(0).getDateCellValue(),
                            "[" + label + "] The data of row[" + i + "] column[0] is unexpected.");
                    Assertions.assertEquals(
                            "yyyy/MM/dd",
                            data.getCell(0).getCellStyle().getDataFormatString(),
                            "[" + label + "] The data format of row[" + i + "] column[0] is unexpected.");
                }
            });
        }

        @ParameterizedTest
        @MethodSource(FILE_GROUPS_METHOD)
        void shouldProduceIdenticalOutput_whenUsingNumberFormatAliasFor(File composite, File direct) throws Exception {
            // Given: @AliasFor(pattern="0.00%") → @NumberFormat("0.00%") — renamed attribute
            List<IntegrationExcelDatas.AliasForNumberFormat.Composite> compositeData =
                    IntegrationExcelDatas.AliasForNumberFormat.compositeData();
            List<IntegrationExcelDatas.AliasForNumberFormat.Direct> directData =
                    IntegrationExcelDatas.AliasForNumberFormat.directData();

            // When
            doWrite(
                    composite,
                    direct,
                    IntegrationExcelDatas.AliasForNumberFormat.Composite.class,
                    IntegrationExcelDatas.AliasForNumberFormat.Direct.class,
                    compositeData,
                    directData);

            // Then
            WorkbookAsserts.build(composite, "Composite", direct, "Direct").assertMulti((label, workbook) -> {
                Assertions.assertEquals(1, workbook.getNumberOfSheets());

                Sheet sheet = workbook.getSheetAt(0);
                Assertions.assertEquals(6, sheet.getPhysicalNumberOfRows());

                // head row
                Row headRow = sheet.getRow(0);
                Assertions.assertEquals(1, headRow.getPhysicalNumberOfCells());
                assertHeadNames(headRow, Arrays.asList("Name"), label);

                // data rows
                for (int i = 0; i < 5; i++) {
                    Row data = sheet.getRow(i + 1);

                    Assertions.assertEquals(1, data.getPhysicalNumberOfCells());
                    Assertions.assertEquals(CellType.NUMERIC, data.getCell(0).getCellType());
                    Assertions.assertEquals(
                            0.5 + i * 0.1,
                            data.getCell(0).getNumericCellValue(),
                            "[" + label + "] The data of row[" + i + "] column[0] is unexpected.");
                    Assertions.assertEquals(
                            "0.00%",
                            data.getCell(0).getCellStyle().getDataFormatString(),
                            "[" + label + "] The data format of row[" + i + "] column[0] is unexpected.");
                }
            });
        }

        @ParameterizedTest
        @MethodSource(FILE_GROUPS_METHOD)
        void shouldProduceIdenticalOutput_whenUsingHeadStyleAliasFor(File composite, File direct) throws Exception {
            // Given: multiple @AliasFor: alignment=RIGHT → horizontalAlignment, bgColor=13 → fillForegroundColor
            List<IntegrationExcelDatas.AliasForHeadStyle.Composite> compositeData =
                    IntegrationExcelDatas.AliasForHeadStyle.compositeData();
            List<IntegrationExcelDatas.AliasForHeadStyle.Direct> directData =
                    IntegrationExcelDatas.AliasForHeadStyle.directData();

            // When
            doWrite(
                    composite,
                    direct,
                    IntegrationExcelDatas.AliasForHeadStyle.Composite.class,
                    IntegrationExcelDatas.AliasForHeadStyle.Direct.class,
                    compositeData,
                    directData);

            // Then
            WorkbookAsserts.build(composite, "Composite", direct, "Direct").assertMulti((label, workbook) -> {
                Assertions.assertEquals(1, workbook.getNumberOfSheets());

                Sheet sheet = workbook.getSheetAt(0);
                Assertions.assertEquals(6, sheet.getPhysicalNumberOfRows());

                // head row
                Row headRow = sheet.getRow(0);
                Assertions.assertEquals(1, headRow.getPhysicalNumberOfCells());
                assertHeadNames(headRow, Arrays.asList("Name"), label);

                CellStyle headStyle = headRow.getCell(0).getCellStyle();
                Assertions.assertEquals(
                        HorizontalAlignment.RIGHT,
                        headStyle.getAlignment(),
                        "[" + label + "] The horizontal alignment of head row is unexpected.");
                Assertions.assertEquals(
                        13,
                        headStyle.getFillForegroundColor(),
                        "[" + label + "] The fill foreground color of head row is unexpected.");

                // data rows
                for (int i = 0; i < 5; i++) {
                    Row data = sheet.getRow(i + 1);

                    Assertions.assertEquals(1, data.getPhysicalNumberOfCells());
                    Assertions.assertEquals(CellType.STRING, data.getCell(0).getCellType());
                    Assertions.assertEquals(
                            "Name" + i,
                            data.getCell(0).getStringCellValue(),
                            "[" + label + "] The data of row[" + i + "] column[0] is unexpected.");
                }
            });
        }

        @ParameterizedTest
        @MethodSource(FILE_GROUPS_METHOD)
        void shouldProduceIdenticalOutput_whenUsingHeadFontStyleAliasFor(File composite, File direct) throws Exception {
            // Given: multiple @AliasFor: fontSize=16 → fontHeightInPoints, fontColor=10 → color
            List<IntegrationExcelDatas.AliasForHeadFontStyle.Composite> compositeData =
                    IntegrationExcelDatas.AliasForHeadFontStyle.compositeData();
            List<IntegrationExcelDatas.AliasForHeadFontStyle.Direct> directData =
                    IntegrationExcelDatas.AliasForHeadFontStyle.directData();

            // When
            doWrite(
                    composite,
                    direct,
                    IntegrationExcelDatas.AliasForHeadFontStyle.Composite.class,
                    IntegrationExcelDatas.AliasForHeadFontStyle.Direct.class,
                    compositeData,
                    directData);

            // Then
            WorkbookAsserts.build(composite, "Composite", direct, "Direct").assertMulti((label, workbook) -> {
                Assertions.assertEquals(1, workbook.getNumberOfSheets());

                Sheet sheet = workbook.getSheetAt(0);
                Assertions.assertEquals(6, sheet.getPhysicalNumberOfRows());

                // head row
                Row headRow = sheet.getRow(0);
                Assertions.assertEquals(1, headRow.getPhysicalNumberOfCells());
                assertHeadNames(headRow, Arrays.asList("Name"), label);

                Font headFont =
                        workbook.getFontAt(headRow.getCell(0).getCellStyle().getFontIndex());
                Assertions.assertEquals(
                        16,
                        headFont.getFontHeightInPoints(),
                        "[" + label + "] The font height of head row is unexpected.");
                Assertions.assertEquals(
                        10, headFont.getColor(), "[" + label + "] The color of head row font is unexpected.");

                // data rows
                for (int i = 0; i < 5; i++) {
                    Row data = sheet.getRow(i + 1);

                    Assertions.assertEquals(1, data.getPhysicalNumberOfCells());
                    Assertions.assertEquals(CellType.STRING, data.getCell(0).getCellType());
                    Assertions.assertEquals(
                            "Name" + i,
                            data.getCell(0).getStringCellValue(),
                            "[" + label + "] The data of row[" + i + "] column[0] is unexpected.");
                }
            });
        }

        @ParameterizedTest
        @MethodSource(FILE_GROUPS_METHOD)
        void shouldProduceIdenticalOutput_whenUsingContentStyleAliasFor(File composite, File direct) throws Exception {
            // Given: multiple @AliasFor: wrap=TRUE → wrapped, vAlign=CENTER → verticalAlignment
            List<IntegrationExcelDatas.AliasForContentStyle.Composite> compositeData =
                    IntegrationExcelDatas.AliasForContentStyle.compositeData();
            List<IntegrationExcelDatas.AliasForContentStyle.Direct> directData =
                    IntegrationExcelDatas.AliasForContentStyle.directData();

            // When
            doWrite(
                    composite,
                    direct,
                    IntegrationExcelDatas.AliasForContentStyle.Composite.class,
                    IntegrationExcelDatas.AliasForContentStyle.Direct.class,
                    compositeData,
                    directData);

            // Then
            WorkbookAsserts.build(composite, "Composite", direct, "Direct").assertMulti((label, workbook) -> {
                Assertions.assertEquals(1, workbook.getNumberOfSheets());

                Sheet sheet = workbook.getSheetAt(0);
                Assertions.assertEquals(6, sheet.getPhysicalNumberOfRows());

                // head row
                Row headRow = sheet.getRow(0);
                Assertions.assertEquals(1, headRow.getPhysicalNumberOfCells());
                assertHeadNames(headRow, Arrays.asList("Name"), label);

                // data rows
                for (int i = 0; i < 5; i++) {
                    Row data = sheet.getRow(i + 1);

                    Assertions.assertEquals(1, data.getPhysicalNumberOfCells());
                    Assertions.assertEquals(CellType.STRING, data.getCell(0).getCellType());
                    Assertions.assertEquals(
                            "Name" + i,
                            data.getCell(0).getStringCellValue(),
                            "[" + label + "] The data of row[" + i + "] column[0] is unexpected.");

                    CellStyle contentStyle = data.getCell(0).getCellStyle();
                    Assertions.assertTrue(
                            contentStyle.getWrapText(),
                            "[" + label + "] The wrap text of row[" + i + "] is unexpected.");
                    Assertions.assertEquals(
                            VerticalAlignment.CENTER,
                            contentStyle.getVerticalAlignment(),
                            "[" + label + "] The vertical alignment of row[" + i + "] is unexpected.");
                }
            });
        }

        @ParameterizedTest
        @MethodSource(FILE_GROUPS_METHOD)
        void shouldProduceIdenticalOutput_whenUsingContentFontStyleAliasFor(File composite, File direct)
                throws Exception {
            // Given: multiple @AliasFor: font="Courier New" → fontName, size=18 → fontHeightInPoints
            List<IntegrationExcelDatas.AliasForContentFontStyle.Composite> compositeData =
                    IntegrationExcelDatas.AliasForContentFontStyle.compositeData();
            List<IntegrationExcelDatas.AliasForContentFontStyle.Direct> directData =
                    IntegrationExcelDatas.AliasForContentFontStyle.directData();

            // When
            doWrite(
                    composite,
                    direct,
                    IntegrationExcelDatas.AliasForContentFontStyle.Composite.class,
                    IntegrationExcelDatas.AliasForContentFontStyle.Direct.class,
                    compositeData,
                    directData);

            // Then
            WorkbookAsserts.build(composite, "Composite", direct, "Direct").assertMulti((label, workbook) -> {
                Assertions.assertEquals(1, workbook.getNumberOfSheets());

                Sheet sheet = workbook.getSheetAt(0);
                Assertions.assertEquals(6, sheet.getPhysicalNumberOfRows());

                // head row
                Row headRow = sheet.getRow(0);
                Assertions.assertEquals(1, headRow.getPhysicalNumberOfCells());
                assertHeadNames(headRow, Arrays.asList("Name"), label);

                // data rows
                for (int i = 0; i < 5; i++) {
                    Row data = sheet.getRow(i + 1);

                    Assertions.assertEquals(1, data.getPhysicalNumberOfCells());
                    Assertions.assertEquals(CellType.STRING, data.getCell(0).getCellType());
                    Assertions.assertEquals(
                            "Name" + i,
                            data.getCell(0).getStringCellValue(),
                            "[" + label + "] The data of row[" + i + "] column[0] is unexpected.");

                    Font contentFont =
                            workbook.getFontAt(data.getCell(0).getCellStyle().getFontIndex());
                    Assertions.assertEquals(
                            "Courier New",
                            contentFont.getFontName(),
                            "[" + label + "] The font name of row[" + i + "] is unexpected.");
                    Assertions.assertEquals(
                            18,
                            contentFont.getFontHeightInPoints(),
                            "[" + label + "] The font height of row[" + i + "] is unexpected.");
                }
            });
        }

        @ParameterizedTest
        @MethodSource(FILE_GROUPS_METHOD)
        void shouldProduceIdenticalOutput_whenUsingContentLoopMergeAliasFor(File composite, File direct)
                throws Exception {
            // Given: multiple @AliasFor: rows=3 → eachRow, cols=1 → columnExtend
            List<IntegrationExcelDatas.AliasForContentLoopMerge.Composite> compositeData =
                    IntegrationExcelDatas.AliasForContentLoopMerge.compositeData();
            List<IntegrationExcelDatas.AliasForContentLoopMerge.Direct> directData =
                    IntegrationExcelDatas.AliasForContentLoopMerge.directData();

            // When
            doWrite(
                    composite,
                    direct,
                    IntegrationExcelDatas.AliasForContentLoopMerge.Composite.class,
                    IntegrationExcelDatas.AliasForContentLoopMerge.Direct.class,
                    compositeData,
                    directData);

            // Then
            WorkbookAsserts.build(composite, "Composite", direct, "Direct").assertMulti((label, workbook) -> {
                Assertions.assertEquals(1, workbook.getNumberOfSheets());

                Sheet sheet = workbook.getSheetAt(0);
                Assertions.assertEquals(6, sheet.getPhysicalNumberOfRows());

                // head row
                Row headRow = sheet.getRow(0);
                Assertions.assertEquals(1, headRow.getPhysicalNumberOfCells());
                assertHeadNames(headRow, Arrays.asList("Name"), label);

                // data rows
                for (int i = 0; i < 5; i++) {
                    Row data = sheet.getRow(i + 1);

                    Assertions.assertEquals(1, data.getPhysicalNumberOfCells());
                    Assertions.assertEquals(CellType.STRING, data.getCell(0).getCellType());
                    Assertions.assertEquals(
                            "Name" + i,
                            data.getCell(0).getStringCellValue(),
                            "[" + label + "] The data of row[" + i + "] column[0] is unexpected.");
                }

                // merged regions: every 3 rows merged
                List<CellRangeAddress> merges = sheet.getMergedRegions();
                Assertions.assertEquals(
                        2, merges.size(), "[" + label + "] The number of merged regions is unexpected.");
                Assertions.assertEquals(
                        "A2:A4",
                        merges.get(0).formatAsString(),
                        "[" + label + "] The first merged region is unexpected.");
                Assertions.assertEquals(
                        "A5:A7",
                        merges.get(1).formatAsString(),
                        "[" + label + "] The last merged region is unexpected.");
            });
        }
    }

    // ====================================================================
    //  Class-Level Tests
    // ====================================================================

    @Nested
    @DisplayName("Class-level composable annotations")
    class ClassLevelTests {

        @ParameterizedTest
        @MethodSource(FILE_GROUPS_METHOD)
        void shouldProduceIdenticalOutput_whenUsingColumnWidth(File composite, File direct) throws Exception {
            // Given: composable @ColumnWidth(25) on class vs direct equivalent
            List<IntegrationExcelDatas.ClassColumnWidth.Composite> compositeData =
                    IntegrationExcelDatas.ClassColumnWidth.compositeData();
            List<IntegrationExcelDatas.ClassColumnWidth.Direct> directData =
                    IntegrationExcelDatas.ClassColumnWidth.directData();

            // When
            doWrite(
                    composite,
                    direct,
                    IntegrationExcelDatas.ClassColumnWidth.Composite.class,
                    IntegrationExcelDatas.ClassColumnWidth.Direct.class,
                    compositeData,
                    directData);

            // Then
            WorkbookAsserts.build(composite, "Composite", direct, "Direct").assertMulti((label, workbook) -> {
                Assertions.assertEquals(1, workbook.getNumberOfSheets());

                Sheet sheet = workbook.getSheetAt(0);
                Assertions.assertEquals(6, sheet.getPhysicalNumberOfRows());

                // head row
                Row headRow = sheet.getRow(0);
                Assertions.assertEquals(1, headRow.getPhysicalNumberOfCells());
                assertHeadNames(headRow, Arrays.asList("Name"), label);

                // data rows
                for (int i = 0; i < 5; i++) {
                    Row data = sheet.getRow(i + 1);

                    Assertions.assertEquals(1, data.getPhysicalNumberOfCells());
                    Assertions.assertEquals(CellType.STRING, data.getCell(0).getCellType());
                    Assertions.assertEquals(
                            "Name" + i,
                            data.getCell(0).getStringCellValue(),
                            "[" + label + "] The data of row[" + i + "] column[0] is unexpected.");
                }

                Assertions.assertEquals(
                        25 * 256,
                        sheet.getColumnWidth(0),
                        "[" + label + "] The column width of column[0] is unexpected.");
            });
        }

        @ParameterizedTest
        @MethodSource(FILE_GROUPS_METHOD)
        void shouldProduceIdenticalOutput_whenUsingHeadStyle(File composite, File direct) throws Exception {
            // Given: composable @HeadStyle(...) on class vs direct equivalent
            List<IntegrationExcelDatas.ClassHeadStyle.Composite> compositeData =
                    IntegrationExcelDatas.ClassHeadStyle.compositeData();
            List<IntegrationExcelDatas.ClassHeadStyle.Direct> directData =
                    IntegrationExcelDatas.ClassHeadStyle.directData();

            // When
            doWrite(
                    composite,
                    direct,
                    IntegrationExcelDatas.ClassHeadStyle.Composite.class,
                    IntegrationExcelDatas.ClassHeadStyle.Direct.class,
                    compositeData,
                    directData);

            // Then
            WorkbookAsserts.build(composite, "Composite", direct, "Direct").assertMulti((label, workbook) -> {
                Assertions.assertEquals(1, workbook.getNumberOfSheets());

                Sheet sheet = workbook.getSheetAt(0);
                Assertions.assertEquals(6, sheet.getPhysicalNumberOfRows());

                // head row
                Row headRow = sheet.getRow(0);
                Assertions.assertEquals(1, headRow.getPhysicalNumberOfCells());
                assertHeadNames(headRow, Arrays.asList("Name"), label);

                CellStyle headStyle = headRow.getCell(0).getCellStyle();
                Assertions.assertEquals(
                        HorizontalAlignment.CENTER,
                        headStyle.getAlignment(),
                        "[" + label + "] The horizontal alignment of head row is unexpected.");
                Assertions.assertEquals(
                        42,
                        headStyle.getFillForegroundColor(),
                        "[" + label + "] The fill foreground color of head row is unexpected.");
                Assertions.assertEquals(
                        FillPatternType.SOLID_FOREGROUND,
                        headStyle.getFillPattern(),
                        "[" + label + "] The fill pattern of head row is unexpected.");

                // data rows
                for (int i = 0; i < 5; i++) {
                    Row data = sheet.getRow(i + 1);

                    Assertions.assertEquals(1, data.getPhysicalNumberOfCells());
                    Assertions.assertEquals(CellType.STRING, data.getCell(0).getCellType());
                    Assertions.assertEquals(
                            "Name" + i,
                            data.getCell(0).getStringCellValue(),
                            "[" + label + "] The data of row[" + i + "] column[0] is unexpected.");
                }
            });
        }

        @ParameterizedTest
        @MethodSource(FILE_GROUPS_METHOD)
        void shouldProduceIdenticalOutput_whenUsingHeadFontStyle(File composite, File direct) throws Exception {
            // Given: composable @HeadFontStyle(...) on class vs direct equivalent
            List<IntegrationExcelDatas.ClassHeadFontStyle.Composite> compositeData =
                    IntegrationExcelDatas.ClassHeadFontStyle.compositeData();
            List<IntegrationExcelDatas.ClassHeadFontStyle.Direct> directData =
                    IntegrationExcelDatas.ClassHeadFontStyle.directData();

            // When
            doWrite(
                    composite,
                    direct,
                    IntegrationExcelDatas.ClassHeadFontStyle.Composite.class,
                    IntegrationExcelDatas.ClassHeadFontStyle.Direct.class,
                    compositeData,
                    directData);

            // Then
            WorkbookAsserts.build(composite, "Composite", direct, "Direct").assertMulti((label, workbook) -> {
                Assertions.assertEquals(1, workbook.getNumberOfSheets());

                Sheet sheet = workbook.getSheetAt(0);
                Assertions.assertEquals(6, sheet.getPhysicalNumberOfRows());

                // head row
                Row headRow = sheet.getRow(0);
                Assertions.assertEquals(1, headRow.getPhysicalNumberOfCells());
                assertHeadNames(headRow, Arrays.asList("Name"), label);

                Font headFont =
                        workbook.getFontAt(headRow.getCell(0).getCellStyle().getFontIndex());
                Assertions.assertTrue(headFont.getBold(), "[" + label + "] The bold of head row font is unexpected.");
                Assertions.assertEquals(
                        14,
                        headFont.getFontHeightInPoints(),
                        "[" + label + "] The font height of head row is unexpected.");

                // data rows
                for (int i = 0; i < 5; i++) {
                    Row data = sheet.getRow(i + 1);

                    Assertions.assertEquals(1, data.getPhysicalNumberOfCells());
                    Assertions.assertEquals(CellType.STRING, data.getCell(0).getCellType());
                    Assertions.assertEquals(
                            "Name" + i,
                            data.getCell(0).getStringCellValue(),
                            "[" + label + "] The data of row[" + i + "] column[0] is unexpected.");
                }
            });
        }

        @ParameterizedTest
        @MethodSource(FILE_GROUPS_METHOD)
        void shouldProduceIdenticalOutput_whenUsingContentStyle(File composite, File direct) throws Exception {
            // Given: composable @ContentStyle(...) on class vs direct equivalent
            List<IntegrationExcelDatas.ClassContentStyle.Composite> compositeData =
                    IntegrationExcelDatas.ClassContentStyle.compositeData();
            List<IntegrationExcelDatas.ClassContentStyle.Direct> directData =
                    IntegrationExcelDatas.ClassContentStyle.directData();

            // When
            doWrite(
                    composite,
                    direct,
                    IntegrationExcelDatas.ClassContentStyle.Composite.class,
                    IntegrationExcelDatas.ClassContentStyle.Direct.class,
                    compositeData,
                    directData);

            // Then
            WorkbookAsserts.build(composite, "Composite", direct, "Direct").assertMulti((label, workbook) -> {
                Assertions.assertEquals(1, workbook.getNumberOfSheets());

                Sheet sheet = workbook.getSheetAt(0);
                Assertions.assertEquals(6, sheet.getPhysicalNumberOfRows());

                // head row
                Row headRow = sheet.getRow(0);
                Assertions.assertEquals(1, headRow.getPhysicalNumberOfCells());
                assertHeadNames(headRow, Arrays.asList("Name"), label);

                // data rows
                for (int i = 0; i < 5; i++) {
                    Row data = sheet.getRow(i + 1);

                    Assertions.assertEquals(1, data.getPhysicalNumberOfCells());
                    Assertions.assertEquals(CellType.STRING, data.getCell(0).getCellType());
                    Assertions.assertEquals(
                            "Name" + i,
                            data.getCell(0).getStringCellValue(),
                            "[" + label + "] The data of row[" + i + "] column[0] is unexpected.");

                    CellStyle contentStyle = data.getCell(0).getCellStyle();
                    Assertions.assertTrue(
                            contentStyle.getWrapText(),
                            "[" + label + "] The wrap text of row[" + i + "] is unexpected.");
                    Assertions.assertEquals(
                            VerticalAlignment.CENTER,
                            contentStyle.getVerticalAlignment(),
                            "[" + label + "] The vertical alignment of row[" + i + "] is unexpected.");
                }
            });
        }

        @ParameterizedTest
        @MethodSource(FILE_GROUPS_METHOD)
        void shouldProduceIdenticalOutput_whenUsingContentFontStyle(File composite, File direct) throws Exception {
            // Given: composable @ContentFontStyle(...) on class vs direct equivalent
            List<IntegrationExcelDatas.ClassContentFontStyle.Composite> compositeData =
                    IntegrationExcelDatas.ClassContentFontStyle.compositeData();
            List<IntegrationExcelDatas.ClassContentFontStyle.Direct> directData =
                    IntegrationExcelDatas.ClassContentFontStyle.directData();

            // When
            doWrite(
                    composite,
                    direct,
                    IntegrationExcelDatas.ClassContentFontStyle.Composite.class,
                    IntegrationExcelDatas.ClassContentFontStyle.Direct.class,
                    compositeData,
                    directData);

            // Then
            WorkbookAsserts.build(composite, "Composite", direct, "Direct").assertMulti((label, workbook) -> {
                Assertions.assertEquals(1, workbook.getNumberOfSheets());

                Sheet sheet = workbook.getSheetAt(0);
                Assertions.assertEquals(6, sheet.getPhysicalNumberOfRows());

                // head row
                Row headRow = sheet.getRow(0);
                Assertions.assertEquals(1, headRow.getPhysicalNumberOfCells());
                assertHeadNames(headRow, Arrays.asList("Name"), label);

                // data rows
                for (int i = 0; i < 5; i++) {
                    Row data = sheet.getRow(i + 1);

                    Assertions.assertEquals(1, data.getPhysicalNumberOfCells());
                    Assertions.assertEquals(CellType.STRING, data.getCell(0).getCellType());
                    Assertions.assertEquals(
                            "Name" + i,
                            data.getCell(0).getStringCellValue(),
                            "[" + label + "] The data of row[" + i + "] column[0] is unexpected.");

                    Font contentFont =
                            workbook.getFontAt(data.getCell(0).getCellStyle().getFontIndex());
                    Assertions.assertTrue(
                            contentFont.getItalic(),
                            "[" + label + "] The italic of row[" + i + "] font is unexpected.");
                    Assertions.assertEquals(
                            "Arial",
                            contentFont.getFontName(),
                            "[" + label + "] The font name of row[" + i + "] is unexpected.");
                }
            });
        }

        @ParameterizedTest
        @MethodSource(FILE_GROUPS_METHOD)
        void shouldProduceIdenticalOutput_whenUsingHeadRowHeight(File composite, File direct) throws Exception {
            // Given: composable @HeadRowHeight(40) on class vs direct equivalent
            List<IntegrationExcelDatas.ClassHeadRowHeight.Composite> compositeData =
                    IntegrationExcelDatas.ClassHeadRowHeight.compositeData();
            List<IntegrationExcelDatas.ClassHeadRowHeight.Direct> directData =
                    IntegrationExcelDatas.ClassHeadRowHeight.directData();

            // When
            doWrite(
                    composite,
                    direct,
                    IntegrationExcelDatas.ClassHeadRowHeight.Composite.class,
                    IntegrationExcelDatas.ClassHeadRowHeight.Direct.class,
                    compositeData,
                    directData);

            // Then
            WorkbookAsserts.build(composite, "Composite", direct, "Direct").assertMulti((label, workbook) -> {
                Assertions.assertEquals(1, workbook.getNumberOfSheets());

                Sheet sheet = workbook.getSheetAt(0);
                Assertions.assertEquals(6, sheet.getPhysicalNumberOfRows());

                // head row
                Row headRow = sheet.getRow(0);
                Assertions.assertEquals(1, headRow.getPhysicalNumberOfCells());
                assertHeadNames(headRow, Arrays.asList("Name"), label);
                Assertions.assertEquals(
                        40.0f, headRow.getHeightInPoints(), "[" + label + "] The head row height is unexpected.");

                // data rows
                for (int i = 0; i < 5; i++) {
                    Row data = sheet.getRow(i + 1);

                    Assertions.assertEquals(1, data.getPhysicalNumberOfCells());
                    Assertions.assertEquals(CellType.STRING, data.getCell(0).getCellType());
                    Assertions.assertEquals(
                            "Name" + i,
                            data.getCell(0).getStringCellValue(),
                            "[" + label + "] The data of row[" + i + "] column[0] is unexpected.");
                }
            });
        }

        @ParameterizedTest
        @MethodSource(FILE_GROUPS_METHOD)
        void shouldProduceIdenticalOutput_whenUsingContentRowHeight(File composite, File direct) throws Exception {
            // Given: composable @ContentRowHeight(30) on class vs direct equivalent
            List<IntegrationExcelDatas.ClassContentRowHeight.Composite> compositeData =
                    IntegrationExcelDatas.ClassContentRowHeight.compositeData();
            List<IntegrationExcelDatas.ClassContentRowHeight.Direct> directData =
                    IntegrationExcelDatas.ClassContentRowHeight.directData();

            // When
            doWrite(
                    composite,
                    direct,
                    IntegrationExcelDatas.ClassContentRowHeight.Composite.class,
                    IntegrationExcelDatas.ClassContentRowHeight.Direct.class,
                    compositeData,
                    directData);

            // Then
            WorkbookAsserts.build(composite, "Composite", direct, "Direct").assertMulti((label, workbook) -> {
                Assertions.assertEquals(1, workbook.getNumberOfSheets());

                Sheet sheet = workbook.getSheetAt(0);
                Assertions.assertEquals(6, sheet.getPhysicalNumberOfRows());

                // head row
                Row headRow = sheet.getRow(0);
                Assertions.assertEquals(1, headRow.getPhysicalNumberOfCells());
                assertHeadNames(headRow, Arrays.asList("Name"), label);

                // data rows
                for (int i = 0; i < 5; i++) {
                    Row data = sheet.getRow(i + 1);

                    Assertions.assertEquals(1, data.getPhysicalNumberOfCells());
                    Assertions.assertEquals(CellType.STRING, data.getCell(0).getCellType());
                    Assertions.assertEquals(
                            "Name" + i,
                            data.getCell(0).getStringCellValue(),
                            "[" + label + "] The data of row[" + i + "] column[0] is unexpected.");
                    Assertions.assertEquals(
                            30.0f,
                            data.getHeightInPoints(),
                            "[" + label + "] The content row height of row[" + i + "] is unexpected.");
                }
            });
        }

        @ParameterizedTest
        @MethodSource(FILE_GROUPS_METHOD)
        void shouldProduceIdenticalOutput_whenUsingOnceAbsoluteMerge(File composite, File direct) throws Exception {
            // Given: composable @OnceAbsoluteMerge(...) on class vs direct equivalent
            List<IntegrationExcelDatas.ClassOnceAbsoluteMerge.Composite> compositeData =
                    IntegrationExcelDatas.ClassOnceAbsoluteMerge.compositeData();
            List<IntegrationExcelDatas.ClassOnceAbsoluteMerge.Direct> directData =
                    IntegrationExcelDatas.ClassOnceAbsoluteMerge.directData();

            // When
            doWrite(
                    composite,
                    direct,
                    IntegrationExcelDatas.ClassOnceAbsoluteMerge.Composite.class,
                    IntegrationExcelDatas.ClassOnceAbsoluteMerge.Direct.class,
                    compositeData,
                    directData);

            // Then
            WorkbookAsserts.build(composite, "Composite", direct, "Direct").assertMulti((label, workbook) -> {
                Assertions.assertEquals(1, workbook.getNumberOfSheets());

                Sheet sheet = workbook.getSheetAt(0);
                Assertions.assertEquals(6, sheet.getPhysicalNumberOfRows());

                // head row
                Row headRow = sheet.getRow(0);
                Assertions.assertEquals(2, headRow.getPhysicalNumberOfCells());
                assertHeadNames(headRow, Arrays.asList("Name", "Value"), label);

                // data rows
                for (int i = 0; i < 5; i++) {
                    Row data = sheet.getRow(i + 1);

                    Assertions.assertEquals(2, data.getPhysicalNumberOfCells());
                    Assertions.assertEquals(CellType.STRING, data.getCell(0).getCellType());
                    Assertions.assertEquals(
                            "Name" + i,
                            data.getCell(0).getStringCellValue(),
                            "[" + label + "] The data of row[" + i + "] column[0] is unexpected.");
                    Assertions.assertEquals(CellType.STRING, data.getCell(1).getCellType());
                    Assertions.assertEquals(
                            "Value" + i,
                            data.getCell(1).getStringCellValue(),
                            "[" + label + "] The data of row[" + i + "] column[1] is unexpected.");
                }

                // merged region
                List<CellRangeAddress> merges = sheet.getMergedRegions();
                Assertions.assertEquals(
                        1, merges.size(), "[" + label + "] The number of merged regions is unexpected.");
                Assertions.assertEquals(
                        "A1:B1", merges.get(0).formatAsString(), "[" + label + "] The merged region is unexpected.");
            });
        }

        @ParameterizedTest
        @MethodSource(FILE_GROUPS_METHOD)
        void shouldProduceIdenticalOutput_whenUsingHeadRowHeightAliasFor(File composite, File direct) throws Exception {
            // Given: @AliasFor(height=50) → @HeadRowHeight(50) — renamed attribute
            List<IntegrationExcelDatas.AliasForHeadRowHeight.Composite> compositeData =
                    IntegrationExcelDatas.AliasForHeadRowHeight.compositeData();
            List<IntegrationExcelDatas.AliasForHeadRowHeight.Direct> directData =
                    IntegrationExcelDatas.AliasForHeadRowHeight.directData();

            // When
            doWrite(
                    composite,
                    direct,
                    IntegrationExcelDatas.AliasForHeadRowHeight.Composite.class,
                    IntegrationExcelDatas.AliasForHeadRowHeight.Direct.class,
                    compositeData,
                    directData);

            // Then
            WorkbookAsserts.build(composite, "Composite", direct, "Direct").assertMulti((label, workbook) -> {
                Assertions.assertEquals(1, workbook.getNumberOfSheets());

                Sheet sheet = workbook.getSheetAt(0);
                Assertions.assertEquals(6, sheet.getPhysicalNumberOfRows());

                // head row
                Row headRow = sheet.getRow(0);
                Assertions.assertEquals(1, headRow.getPhysicalNumberOfCells());
                assertHeadNames(headRow, Arrays.asList("Name"), label);
                Assertions.assertEquals(
                        50.0f, headRow.getHeightInPoints(), "[" + label + "] The head row height is unexpected.");

                // data rows
                for (int i = 0; i < 5; i++) {
                    Row data = sheet.getRow(i + 1);

                    Assertions.assertEquals(1, data.getPhysicalNumberOfCells());
                    Assertions.assertEquals(CellType.STRING, data.getCell(0).getCellType());
                    Assertions.assertEquals(
                            "Name" + i,
                            data.getCell(0).getStringCellValue(),
                            "[" + label + "] The data of row[" + i + "] column[0] is unexpected.");
                }
            });
        }

        @ParameterizedTest
        @MethodSource(FILE_GROUPS_METHOD)
        void shouldProduceIdenticalOutput_whenUsingContentRowHeightAliasFor(File composite, File direct)
                throws Exception {
            // Given: @AliasFor(height=35) → @ContentRowHeight(35) — renamed attribute
            List<IntegrationExcelDatas.AliasForContentRowHeight.Composite> compositeData =
                    IntegrationExcelDatas.AliasForContentRowHeight.compositeData();
            List<IntegrationExcelDatas.AliasForContentRowHeight.Direct> directData =
                    IntegrationExcelDatas.AliasForContentRowHeight.directData();

            // When
            doWrite(
                    composite,
                    direct,
                    IntegrationExcelDatas.AliasForContentRowHeight.Composite.class,
                    IntegrationExcelDatas.AliasForContentRowHeight.Direct.class,
                    compositeData,
                    directData);

            // Then
            WorkbookAsserts.build(composite, "Composite", direct, "Direct").assertMulti((label, workbook) -> {
                Assertions.assertEquals(1, workbook.getNumberOfSheets());

                Sheet sheet = workbook.getSheetAt(0);
                Assertions.assertEquals(6, sheet.getPhysicalNumberOfRows());

                // head row
                Row headRow = sheet.getRow(0);
                Assertions.assertEquals(1, headRow.getPhysicalNumberOfCells());
                assertHeadNames(headRow, Arrays.asList("Name"), label);

                // data rows
                for (int i = 0; i < 5; i++) {
                    Row data = sheet.getRow(i + 1);

                    Assertions.assertEquals(1, data.getPhysicalNumberOfCells());
                    Assertions.assertEquals(CellType.STRING, data.getCell(0).getCellType());
                    Assertions.assertEquals(
                            "Name" + i,
                            data.getCell(0).getStringCellValue(),
                            "[" + label + "] The data of row[" + i + "] column[0] is unexpected.");
                    Assertions.assertEquals(
                            35.0f,
                            data.getHeightInPoints(),
                            "[" + label + "] The content row height of row[" + i + "] is unexpected.");
                }
            });
        }

        @ParameterizedTest
        @MethodSource(FILE_GROUPS_METHOD)
        void shouldProduceIdenticalOutput_whenUsingOnceAbsoluteMergeAliasFor(File composite, File direct)
                throws Exception {
            // Given: 4 @AliasFor: startRow/endRow/startCol/endCol →
            // firstRowIndex/lastRowIndex/firstColumnIndex/lastColumnIndex
            List<IntegrationExcelDatas.AliasForOnceAbsoluteMerge.Composite> compositeData =
                    IntegrationExcelDatas.AliasForOnceAbsoluteMerge.compositeData();
            List<IntegrationExcelDatas.AliasForOnceAbsoluteMerge.Direct> directData =
                    IntegrationExcelDatas.AliasForOnceAbsoluteMerge.directData();

            // When
            doWrite(
                    composite,
                    direct,
                    IntegrationExcelDatas.AliasForOnceAbsoluteMerge.Composite.class,
                    IntegrationExcelDatas.AliasForOnceAbsoluteMerge.Direct.class,
                    compositeData,
                    directData);

            // Then
            WorkbookAsserts.build(composite, "Composite", direct, "Direct").assertMulti((label, workbook) -> {
                Assertions.assertEquals(1, workbook.getNumberOfSheets());

                Sheet sheet = workbook.getSheetAt(0);
                Assertions.assertEquals(6, sheet.getPhysicalNumberOfRows());

                // head row
                Row headRow = sheet.getRow(0);
                Assertions.assertEquals(2, headRow.getPhysicalNumberOfCells());
                assertHeadNames(headRow, Arrays.asList("Name", "Value"), label);

                // data rows
                for (int i = 0; i < 5; i++) {
                    Row data = sheet.getRow(i + 1);

                    Assertions.assertEquals(2, data.getPhysicalNumberOfCells());
                    Assertions.assertEquals(CellType.STRING, data.getCell(0).getCellType());
                    Assertions.assertEquals(
                            "Name" + i,
                            data.getCell(0).getStringCellValue(),
                            "[" + label + "] The data of row[" + i + "] column[0] is unexpected.");
                    Assertions.assertEquals(CellType.STRING, data.getCell(1).getCellType());
                    Assertions.assertEquals(
                            "Value" + i,
                            data.getCell(1).getStringCellValue(),
                            "[" + label + "] The data of row[" + i + "] column[1] is unexpected.");
                }

                // merged region
                List<CellRangeAddress> merges = sheet.getMergedRegions();
                Assertions.assertEquals(
                        1, merges.size(), "[" + label + "] The number of merged regions is unexpected.");
                Assertions.assertEquals(
                        "A1:B2", merges.get(0).formatAsString(), "[" + label + "] The merged region is unexpected.");
            });
        }
    }

    // ====================================================================
    //  Mixed-Level Tests
    // ====================================================================

    @Nested
    @DisplayName("Mixed-level composable annotations (class + field)")
    class MixedLevelTests {

        @ParameterizedTest
        @MethodSource(FILE_GROUPS_METHOD)
        void shouldProduceIdenticalOutput_whenUsingMixedAnnotations(File composite, File direct) throws Exception {
            // Given: composable @HeadRowHeight + @ContentRowHeight on class,
            //        @ExcelProperty + @ColumnWidth + @HeadStyle on field "name",
            //        @ExcelProperty + @ContentFontStyle on field "value"
            List<IntegrationExcelDatas.MixedAll.Composite> compositeData =
                    IntegrationExcelDatas.MixedAll.compositeData();
            List<IntegrationExcelDatas.MixedAll.Direct> directData = IntegrationExcelDatas.MixedAll.directData();

            // When
            doWrite(
                    composite,
                    direct,
                    IntegrationExcelDatas.MixedAll.Composite.class,
                    IntegrationExcelDatas.MixedAll.Direct.class,
                    compositeData,
                    directData);

            // Then
            WorkbookAsserts.build(composite, "Composite", direct, "Direct").assertMulti((label, workbook) -> {
                Assertions.assertEquals(1, workbook.getNumberOfSheets());

                Sheet sheet = workbook.getSheetAt(0);
                Assertions.assertEquals(6, sheet.getPhysicalNumberOfRows());

                // head row
                Row headRow = sheet.getRow(0);
                Assertions.assertEquals(2, headRow.getPhysicalNumberOfCells());
                assertHeadNames(headRow, Arrays.asList("Name", "Value"), label);
                Assertions.assertEquals(
                        40.0f, headRow.getHeightInPoints(), "[" + label + "] The head row height is unexpected.");

                // head style for column 0
                CellStyle headStyle0 = headRow.getCell(0).getCellStyle();
                Assertions.assertEquals(
                        HorizontalAlignment.CENTER,
                        headStyle0.getAlignment(),
                        "[" + label + "] The horizontal alignment of head row column[0] is unexpected.");
                Assertions.assertEquals(
                        42,
                        headStyle0.getFillForegroundColor(),
                        "[" + label + "] The fill foreground color of head row column[0] is unexpected.");
                Assertions.assertEquals(
                        FillPatternType.SOLID_FOREGROUND,
                        headStyle0.getFillPattern(),
                        "[" + label + "] The fill pattern of head row column[0] is unexpected.");

                // column width
                Assertions.assertEquals(
                        25 * 256,
                        sheet.getColumnWidth(0),
                        "[" + label + "] The column width of column[0] is unexpected.");

                // data rows
                for (int i = 0; i < 5; i++) {
                    Row data = sheet.getRow(i + 1);

                    Assertions.assertEquals(2, data.getPhysicalNumberOfCells());
                    Assertions.assertEquals(
                            30.0f,
                            data.getHeightInPoints(),
                            "[" + label + "] The content row height of row[" + i + "] is unexpected.");

                    Assertions.assertEquals(CellType.STRING, data.getCell(0).getCellType());
                    Assertions.assertEquals(
                            "Name" + i,
                            data.getCell(0).getStringCellValue(),
                            "[" + label + "] The data of row[" + i + "] column[0] is unexpected.");

                    Assertions.assertEquals(CellType.STRING, data.getCell(1).getCellType());
                    Assertions.assertEquals(
                            "Value" + i,
                            data.getCell(1).getStringCellValue(),
                            "[" + label + "] The data of row[" + i + "] column[1] is unexpected.");

                    Font contentFont1 =
                            workbook.getFontAt(data.getCell(1).getCellStyle().getFontIndex());
                    Assertions.assertTrue(
                            contentFont1.getItalic(),
                            "[" + label + "] The italic of row[" + i + "] column[1] font is unexpected.");
                    Assertions.assertEquals(
                            "Arial",
                            contentFont1.getFontName(),
                            "[" + label + "] The font name of row[" + i + "] column[1] is unexpected.");
                }
            });
        }

        @ParameterizedTest
        @MethodSource(FILE_GROUPS_METHOD)
        void shouldProduceIdenticalOutput_whenDirectAnnotationOverridesComposite(File composite, File direct)
                throws Exception {
            // Given: field has both @CompositeExcelPropertyAliasFor(value="Value") and @ExcelProperty("Final Value")
            //        Direct annotation has higher priority (smaller distance), so final header is "Final Value"
            List<IntegrationExcelDatas.PriorityDirectOverComposite.Composite> compositeData =
                    IntegrationExcelDatas.PriorityDirectOverComposite.compositeData();
            List<IntegrationExcelDatas.PriorityDirectOverComposite.Direct> directData =
                    IntegrationExcelDatas.PriorityDirectOverComposite.directData();

            // When
            doWrite(
                    composite,
                    direct,
                    IntegrationExcelDatas.PriorityDirectOverComposite.Composite.class,
                    IntegrationExcelDatas.PriorityDirectOverComposite.Direct.class,
                    compositeData,
                    directData);

            // Then
            WorkbookAsserts.build(composite, "Composite", direct, "Direct").assertMulti((label, workbook) -> {
                Assertions.assertEquals(1, workbook.getNumberOfSheets());

                Sheet sheet = workbook.getSheetAt(0);
                Assertions.assertEquals(6, sheet.getPhysicalNumberOfRows());

                // head row: direct annotation "Final Value" overrides composite alias "Value"
                Row headRow = sheet.getRow(0);
                Assertions.assertEquals(1, headRow.getPhysicalNumberOfCells());
                assertHeadNames(headRow, Arrays.asList("Final Value"), label);

                // data rows
                for (int i = 0; i < 5; i++) {
                    Row data = sheet.getRow(i + 1);

                    Assertions.assertEquals(1, data.getPhysicalNumberOfCells());
                    Assertions.assertEquals(CellType.STRING, data.getCell(0).getCellType());
                    Assertions.assertEquals(
                            "Name" + i,
                            data.getCell(0).getStringCellValue(),
                            "[" + label + "] The data of row[" + i + "] column[0] is unexpected.");
                }
            });
        }
    }
}
