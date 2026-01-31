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

package org.apache.fesod.sheet.fill;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.apache.fesod.sheet.ExcelWriter;
import org.apache.fesod.sheet.FesodSheet;
import org.apache.fesod.sheet.enums.FillMergeStrategy;
import org.apache.fesod.sheet.enums.WriteDirectionEnum;
import org.apache.fesod.sheet.write.executor.ExcelWriteFillExecutor;
import org.apache.fesod.sheet.write.metadata.WriteSheet;
import org.apache.fesod.sheet.write.metadata.fill.FillConfig;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.CellRangeAddress;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Tests for multi-rows loop filling, cells merge handing and copy merged cell-styles.
 *
 * @see ExcelWriteFillExecutor
 */
class LoopRowFillingMergeTest {

    // Case 1: base filling
    private File caseA07;
    private File caseATemplate07;
    private File caseA03;
    private File caseATemplate03;

    // Case 2: filling with auto merge
    private File caseB07;
    private File caseBTemplate07;
    private File caseB03;
    private File caseBTemplate03;

    // Case 3: filling with auto merge and copy merged cell-styles
    private File caseC07;
    private File caseCTemplate07;
    private File caseC03;
    private File caseCTemplate03;

    // Case 4: combined base filling of collection-based data (list rows) and common data
    private File caseD07;
    private File caseDTemplate07;
    private File caseD03;
    private File caseDTemplate03;

    // Case 5: combined filling of collection-based data (list rows) with auto merge and common data
    private File caseE07;
    private File caseETemplate07;
    private File caseE03;
    private File caseETemplate03;

    // Case 6: combined filling of collection-based data (list rows)  with auto merge, copy merged cell-styles and
    // common data
    private File caseF07;
    private File caseFTemplate07;
    private File caseF03;
    private File caseFTemplate03;

    private static final int MOCK_DATA_SIZE = 1000;
    private List<LoopRowFillingMergeModel> mockDatas;
    private Map<String, String> mockCommonData;

    @BeforeEach
    void setup(@TempDir Path dir) throws Exception {
        this.caseA07 = createTmpFile(dir, "case_a_07.xlsx");
        this.caseATemplate07 = loadTemplate("case_a_07.xlsx");
        this.caseA03 = createTmpFile(dir, "case_a_03.xls");
        this.caseATemplate03 = loadTemplate("case_a_03.xls");

        this.caseB07 = createTmpFile(dir, "case_b_07.xlsx");
        this.caseBTemplate07 = loadTemplate("case_b_07.xlsx");
        this.caseB03 = createTmpFile(dir, "case_b_03.xls");
        this.caseBTemplate03 = loadTemplate("case_b_03.xls");

        this.caseC07 = createTmpFile(dir, "case_c_07.xlsx");
        this.caseCTemplate07 = loadTemplate("case_c_07.xlsx");
        this.caseC03 = createTmpFile(dir, "case_c_03.xls");
        this.caseCTemplate03 = loadTemplate("case_c_03.xls");

        this.caseD07 = createTmpFile(dir, "case_d_07.xlsx");
        this.caseDTemplate07 = loadTemplate("case_d_07.xlsx");
        this.caseD03 = createTmpFile(dir, "case_d_03.xls");
        this.caseDTemplate03 = loadTemplate("case_d_03.xls");

        this.caseE07 = createTmpFile(dir, "case_e_07.xlsx");
        this.caseETemplate07 = loadTemplate("case_e_07.xlsx");
        this.caseE03 = createTmpFile(dir, "case_e_03.xls");
        this.caseETemplate03 = loadTemplate("case_e_03.xls");

        this.caseF07 = createTmpFile(dir, "case_f_07.xlsx");
        this.caseFTemplate07 = loadTemplate("case_f_07.xlsx");
        this.caseF03 = createTmpFile(dir, "case_f_03.xls");
        this.caseFTemplate03 = loadTemplate("case_f_03.xls");

        this.mockDatas = datas();

        Map<String, String> tmp = new HashMap<>();
        tmp.put("string4", "String4");
        tmp.put("string5", "String5");
        this.mockCommonData = tmp;
    }

    private File loadTemplate(String filename) throws URISyntaxException {
        URL resource = getClass().getClassLoader().getResource("fill" + File.separator + filename);
        Assertions.assertNotNull(resource);
        return new File(resource.toURI());
    }

    private File createTmpFile(Path dir, String filename) {
        return new File(dir.resolve(filename).toString());
    }

    private static List<LoopRowFillingMergeModel> datas() {
        return IntStream.rangeClosed(1, MOCK_DATA_SIZE)
                .mapToObj(no -> {
                    LoopRowFillingMergeModel result = new LoopRowFillingMergeModel();
                    result.setNo(no);
                    result.setString1("string1");
                    result.setString2("string2");
                    result.setString3("string3");
                    result.setLocalDate1(LocalDate.now());
                    result.setLocalDate2(LocalDate.now());
                    result.setLong1((no * 100L));
                    result.setLong2((no * 200L));
                    return result;
                })
                .collect(Collectors.toList());
    }

    @Test
    void test_base_fill() throws IOException {
        doTestBaseFill(caseATemplate03, caseA03);
        doTestBaseFill(caseATemplate07, caseA07);
    }

    @Test
    void test_fill_with_autoMerge() throws IOException {
        doTestFillWithAutoMerge(caseBTemplate03, caseB03);
        doTestFillWithAutoMerge(caseBTemplate07, caseB07);
    }

    @Test
    void test_fill_with_autoMerge_copyMergedCellStyles() throws IOException {
        doTestFillWithAutoMergeAndCopyMergedCellStyles(caseCTemplate03, caseC03);
        doTestFillWithAutoMergeAndCopyMergedCellStyles(caseCTemplate07, caseC07);
    }

    @Test
    void test_combine_base_fill() throws IOException {
        doTestCombineBaseFill(caseDTemplate03, caseD03);
        doTestCombineBaseFill(caseDTemplate07, caseD07);
    }

    @Test
    void test_combine_fill_with_autoMerge() throws IOException {
        doTestCombineFillWithAutoMerge(caseETemplate03, caseE03);
        doTestCombineFillWithAutoMerge(caseETemplate07, caseE07);
    }

    @Test
    void test_combine_fill_with_autoMerge_copyMergedCellStyles() throws IOException {
        doTestCombineFillWithAutoMergeAndCopyMergedCellStyles(caseFTemplate03, caseF03);
        doTestCombineFillWithAutoMergeAndCopyMergedCellStyles(caseFTemplate07, caseF07);
    }

    @Test
    void test_fill_horizontal_throws_exception() {
        FillConfig fillConfig1 = FillConfig.builder()
                .direction(WriteDirectionEnum.HORIZONTAL)
                .mergeStrategy(FillMergeStrategy.AUTO)
                .build();
        Assertions.assertThrowsExactly(IllegalArgumentException.class, () -> {
            FesodSheet.write(caseB03).withTemplate(caseBTemplate03).sheet().doFill(mockDatas, fillConfig1);
        });
        Assertions.assertThrowsExactly(IllegalArgumentException.class, () -> {
            FesodSheet.write(caseB07).withTemplate(caseBTemplate07).sheet().doFill(mockDatas, fillConfig1);
        });

        FillConfig fillConfig2 = FillConfig.builder()
                .direction(WriteDirectionEnum.HORIZONTAL)
                .mergeStrategy(FillMergeStrategy.MERGE_CELL_STYLE)
                .build();
        Assertions.assertThrowsExactly(IllegalArgumentException.class, () -> {
            FesodSheet.write(caseC03).withTemplate(caseCTemplate03).sheet().doFill(mockDatas, fillConfig2);
        });
        Assertions.assertThrowsExactly(IllegalArgumentException.class, () -> {
            FesodSheet.write(caseC07).withTemplate(caseCTemplate07).sheet().doFill(mockDatas, fillConfig2);
        });
    }

    private void doTestBaseFill(File template, File output) throws IOException {
        Assertions.assertDoesNotThrow(() -> {
            FesodSheet.write(output).withTemplate(template).sheet().doFill(mockDatas);
        });

        try (Workbook workbook = WorkbookFactory.create(output)) {
            Sheet sheet = workbook.getSheetAt(0);

            validateHeadRow(sheet);
            validateLoopFillingRowData(sheet);
        }
    }

    private void doTestCombineBaseFill(File template, File output) throws IOException {
        Assertions.assertDoesNotThrow(() -> {
            FillConfig fillConfig = FillConfig.builder().forceNewRow(true).build();

            try (ExcelWriter writer =
                    FesodSheet.write(output).withTemplate(template).build()) {
                WriteSheet sheet = FesodSheet.writerSheet(0).build();
                writer.fill(mockDatas, fillConfig, sheet);
                writer.fill(mockCommonData, sheet);

                writer.finish();
            }
        });

        try (Workbook workbook = WorkbookFactory.create(output)) {
            Sheet sheet = workbook.getSheetAt(0);

            validateHeadRow(sheet);
            validateLoopFillingRowData(sheet);
            validateCommonData(sheet);
        }
    }

    private void doTestFillWithAutoMerge(File template, File output) throws IOException {
        Assertions.assertDoesNotThrow(() -> {
            FesodSheet.write(output)
                    .withTemplate(template)
                    .sheet()
                    .doFill(
                            mockDatas,
                            FillConfig.builder()
                                    .mergeStrategy(FillMergeStrategy.AUTO)
                                    .build());
        });

        try (Workbook workbook = WorkbookFactory.create(output)) {
            Sheet sheet = workbook.getSheetAt(0);

            validateHeadRow(sheet);
            validateLoopFillingRowData(sheet);
            validateLoopFillingCellMergedStrategies(sheet);
        }
    }

    private void doTestCombineFillWithAutoMerge(File template, File output) throws IOException {
        Assertions.assertDoesNotThrow(() -> {
            FillConfig fillConfig = FillConfig.builder()
                    .forceNewRow(true)
                    .mergeStrategy(FillMergeStrategy.AUTO)
                    .build();

            try (ExcelWriter writer =
                    FesodSheet.write(output).withTemplate(template).build()) {
                WriteSheet sheet = FesodSheet.writerSheet(0).build();
                writer.fill(mockDatas, fillConfig, sheet);
                writer.fill(mockCommonData, sheet);

                writer.finish();
            }
        });

        try (Workbook workbook = WorkbookFactory.create(output)) {
            Sheet sheet = workbook.getSheetAt(0);

            validateHeadRow(sheet);
            validateLoopFillingRowData(sheet);
            validateLoopFillingCellMergedStrategies(sheet);
            validateCommonData(sheet);
        }
    }

    private void doTestFillWithAutoMergeAndCopyMergedCellStyles(File template, File output) throws IOException {
        Assertions.assertDoesNotThrow(() -> {
            FesodSheet.write(output)
                    .withTemplate(template)
                    .sheet()
                    .doFill(
                            mockDatas,
                            FillConfig.builder()
                                    .forceNewRow(true)
                                    .mergeStrategy(FillMergeStrategy.MERGE_CELL_STYLE)
                                    .build());
        });

        try (Workbook workbook = WorkbookFactory.create(output)) {
            Sheet sheet = workbook.getSheetAt(0);

            validateHeadRow(sheet);
            validateLoopFillingRowData(sheet);
            validateLoopFillingCellMergedStrategies(sheet);
            validateMergedCellStyles(sheet);
        }
    }

    private void doTestCombineFillWithAutoMergeAndCopyMergedCellStyles(File template, File output) throws IOException {
        Assertions.assertDoesNotThrow(() -> {
            FillConfig fillConfig = FillConfig.builder()
                    .forceNewRow(true)
                    .mergeStrategy(FillMergeStrategy.MERGE_CELL_STYLE)
                    .build();

            try (ExcelWriter writer =
                    FesodSheet.write(output).withTemplate(template).build()) {
                WriteSheet sheet = FesodSheet.writerSheet(0).build();
                writer.fill(mockDatas, fillConfig, sheet);
                writer.fill(mockCommonData, sheet);

                writer.finish();
            }
        });

        try (Workbook workbook = WorkbookFactory.create(output)) {
            Sheet sheet = workbook.getSheetAt(0);

            validateHeadRow(sheet);
            validateLoopFillingRowData(sheet);
            validateLoopFillingCellMergedStrategies(sheet);
            validateMergedCellStyles(sheet);
            validateCommonData(sheet);
        }
    }

    private void validateHeadRow(Sheet sheet) {
        Row headRow = sheet.getRow(0);

        Assertions.assertEquals("No (Merge Across Rows)", headRow.getCell(0).getStringCellValue());
        Assertions.assertEquals("String1-2 (Normal)", headRow.getCell(1).getStringCellValue());
        Assertions.assertEquals(
                "String3 (Merge Across Rows)", headRow.getCell(2).getStringCellValue());
        Assertions.assertEquals(
                "LocalDate1-2 (Merge Across Columns)", headRow.getCell(3).getStringCellValue());
        Assertions.assertFalse(hasCellValue(headRow.getCell(4)));
        Assertions.assertEquals(
                "Long1 (Merge Across Rows And Columns)", headRow.getCell(5).getStringCellValue());
        Assertions.assertFalse(hasCellValue(headRow.getCell(6)));
        Assertions.assertEquals("Long2 (Normal)", headRow.getCell(7).getStringCellValue());
    }

    private void validateLoopFillingRowData(Sheet sheet) {
        int rowSpan = 2;
        int currentStartRow = 1;
        for (LoopRowFillingMergeModel data : mockDatas) {
            // Model first row
            Row firstRow = sheet.getRow(currentStartRow);
            Assertions.assertEquals(
                    data.getNo().doubleValue(), firstRow.getCell(0).getNumericCellValue());
            Assertions.assertEquals(data.getString1(), firstRow.getCell(1).getStringCellValue());
            Assertions.assertEquals(data.getString3(), firstRow.getCell(2).getStringCellValue());
            Assertions.assertEquals(
                    data.getLocalDate1(),
                    firstRow.getCell(3).getLocalDateTimeCellValue().toLocalDate());
            Assertions.assertFalse(hasCellValue(firstRow.getCell(4)));
            Assertions.assertEquals(
                    data.getLong1().doubleValue(), firstRow.getCell(5).getNumericCellValue());
            Assertions.assertFalse(hasCellValue(firstRow.getCell(6)));
            Assertions.assertEquals(
                    data.getLong2().doubleValue(), firstRow.getCell(7).getNumericCellValue());

            // Model second row
            Row secondRow = sheet.getRow(currentStartRow + 1);
            Assertions.assertFalse(hasCellValue(secondRow.getCell(0)));
            Assertions.assertEquals(data.getString2(), secondRow.getCell(1).getStringCellValue());
            Assertions.assertFalse(hasCellValue(secondRow.getCell(2)));
            Assertions.assertEquals(
                    data.getLocalDate2(),
                    secondRow.getCell(3).getLocalDateTimeCellValue().toLocalDate());
            Assertions.assertFalse(hasCellValue(secondRow.getCell(4)));
            Assertions.assertFalse(hasCellValue(secondRow.getCell(5)));
            Assertions.assertFalse(hasCellValue(secondRow.getCell(6)));
            Assertions.assertFalse(hasCellValue(secondRow.getCell(7)));

            currentStartRow += rowSpan;
        }
    }

    private void validateLoopFillingCellMergedStrategies(Sheet sheet) {
        List<CellRangeAddress> dataMergedRegions = getMergedRegionsForDataRow(sheet);

        // ignored head row and template variable rows
        Assertions.assertEquals((MOCK_DATA_SIZE - 1) * 5, dataMergedRegions.size());

        Set<CellRangeAddress> removeFlag = new HashSet<>();
        int totalMergedRegions = 5;
        int rowSpan = 2;
        int maxDataSize = MOCK_DATA_SIZE * rowSpan;
        for (int currentStartRow = 3; currentStartRow <= maxDataSize; currentStartRow += rowSpan) {
            // Model first row
            List<CellRangeAddress> col0 = findMergedRegions(dataMergedRegions, currentStartRow, 0);
            Assertions.assertEquals(1, col0.size());
            removeFlag.add(col0.get(0));

            List<CellRangeAddress> col1 = findMergedRegions(dataMergedRegions, currentStartRow, 1);
            Assertions.assertEquals(0, col1.size());

            List<CellRangeAddress> col2 = findMergedRegions(dataMergedRegions, currentStartRow, 2);
            Assertions.assertEquals(1, col2.size());
            removeFlag.add(col2.get(0));

            List<CellRangeAddress> col3 = findMergedRegions(dataMergedRegions, currentStartRow, 3);
            Assertions.assertEquals(1, col3.size());
            removeFlag.add(col3.get(0));

            List<CellRangeAddress> col4 = findMergedRegions(dataMergedRegions, currentStartRow, 4);
            Assertions.assertEquals(1, col4.size());
            removeFlag.add(col4.get(0));

            List<CellRangeAddress> col5 = findMergedRegions(dataMergedRegions, currentStartRow, 5);
            Assertions.assertEquals(1, col5.size());
            removeFlag.add(col5.get(0));

            List<CellRangeAddress> col6 = findMergedRegions(dataMergedRegions, currentStartRow, 6);
            Assertions.assertEquals(1, col6.size());
            removeFlag.add(col6.get(0));

            List<CellRangeAddress> col7 = findMergedRegions(dataMergedRegions, currentStartRow, 7);
            Assertions.assertEquals(0, col7.size());

            // Model second row
            List<CellRangeAddress> secondCol0 = findMergedRegions(dataMergedRegions, currentStartRow + 1, 0);
            Assertions.assertEquals(1, secondCol0.size());
            removeFlag.add(secondCol0.get(0));

            List<CellRangeAddress> secondCol1 = findMergedRegions(dataMergedRegions, currentStartRow + 1, 1);
            Assertions.assertEquals(0, secondCol1.size());

            List<CellRangeAddress> secondCol2 = findMergedRegions(dataMergedRegions, currentStartRow + 1, 2);
            Assertions.assertEquals(1, secondCol2.size());
            removeFlag.add(secondCol2.get(0));

            List<CellRangeAddress> secondCol3 = findMergedRegions(dataMergedRegions, currentStartRow + 1, 3);
            Assertions.assertEquals(1, secondCol3.size());
            removeFlag.add(secondCol3.get(0));

            List<CellRangeAddress> secondCol4 = findMergedRegions(dataMergedRegions, currentStartRow + 1, 4);
            Assertions.assertEquals(1, secondCol4.size());
            removeFlag.add(secondCol4.get(0));

            List<CellRangeAddress> secondCol5 = findMergedRegions(dataMergedRegions, currentStartRow + 1, 5);
            Assertions.assertEquals(1, secondCol5.size());
            removeFlag.add(secondCol5.get(0));

            List<CellRangeAddress> secondCol6 = findMergedRegions(dataMergedRegions, currentStartRow + 1, 6);
            Assertions.assertEquals(1, secondCol6.size());
            removeFlag.add(secondCol6.get(0));

            List<CellRangeAddress> secondCol7 = findMergedRegions(dataMergedRegions, currentStartRow + 1, 7);
            Assertions.assertEquals(0, secondCol7.size());

            Assertions.assertEquals(totalMergedRegions, removeFlag.size());

            // reset removeFlag
            dataMergedRegions.removeAll(removeFlag);
            removeFlag = new HashSet<>();
        }

        Assertions.assertEquals(0, dataMergedRegions.size());
    }

    private void validateMergedCellStyles(Sheet sheet) {
        List<CellRangeAddress> dataMergedRegions = getMergedRegionsForDataRow(sheet);

        for (CellRangeAddress range : dataMergedRegions) {
            int firstRow = range.getFirstRow();
            int firstCol = range.getFirstColumn();

            Cell anchor = sheet.getRow(firstRow).getCell(firstCol);
            Assertions.assertNotNull(anchor);

            CellStyle anchorStyle = anchor.getCellStyle();
            Assertions.assertNotNull(anchorStyle);

            for (int rownum = firstRow; rownum <= range.getLastRow(); rownum++) {
                for (int column = firstCol; column <= range.getLastColumn(); column++) {
                    if (rownum == firstRow && column == firstCol) {
                        continue;
                    }

                    Row row = sheet.getRow(rownum);
                    Assertions.assertNotNull(row);
                    Cell cell = row.getCell(column);
                    Assertions.assertNotNull(cell);

                    Assertions.assertEquals(
                            anchorStyle.getIndex(), cell.getCellStyle().getIndex());
                }
            }
        }
    }

    private void validateCommonData(Sheet sheet) {
        // validate common head row
        // first head row + data rows + space rows
        int commonHeadRow = 1 + (MOCK_DATA_SIZE * 2) + 2;
        Row headRow = sheet.getRow(commonHeadRow);
        Assertions.assertNotNull(headRow);
        Assertions.assertNotNull(headRow.getCell(0));
        Assertions.assertEquals("String4 (Normal Common)", headRow.getCell(0).getStringCellValue());
        Assertions.assertNotNull(headRow.getCell(2));
        Assertions.assertEquals("String5 (Normal Common)", headRow.getCell(2).getStringCellValue());

        // validate common data row
        Row dataRow = sheet.getRow(commonHeadRow + 1);
        Assertions.assertNotNull(dataRow);
        Assertions.assertEquals(
                mockCommonData.get("string4"), dataRow.getCell(0).getStringCellValue());
        Assertions.assertEquals(
                mockCommonData.get("string5"), dataRow.getCell(2).getStringCellValue());
    }

    private static List<CellRangeAddress> getMergedRegionsForDataRow(Sheet sheet) {
        List<CellRangeAddress> allMergedRegions = sheet.getMergedRegions();
        // Ignore merged regions in the head row and template variable rows
        List<CellRangeAddress> dataMergedRegions = new ArrayList<>();

        for (CellRangeAddress range : allMergedRegions) {
            boolean isInRange = IntStream.rangeClosed(0, 6).anyMatch(column -> {
                boolean isHeadCellInRange = range.isInRange(0, column);
                boolean isDataCellInRange = range.isInRange(1, column) || range.isInRange(2, column);
                return isHeadCellInRange || isDataCellInRange;
            });
            if (!isInRange) {
                dataMergedRegions.add(range);
            }
        }
        return dataMergedRegions;
    }

    private List<CellRangeAddress> findMergedRegions(List<CellRangeAddress> dataMergedRegions, int row, int col) {
        return dataMergedRegions.stream()
                .filter(range -> range.isInRange(row, col))
                .collect(Collectors.toList());
    }

    private boolean hasCellValue(Cell cell) {
        if (cell == null) {
            return false;
        }
        CellType type = cell.getCellType();
        if (type == CellType.BLANK) {
            return false;
        }
        if (type == CellType.STRING) {
            return !cell.getStringCellValue().trim().isEmpty();
        }
        if (type == CellType.FORMULA) {
            return cell.getCachedFormulaResultType() != CellType.BLANK;
        }
        return true;
    }
}
