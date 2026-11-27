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

package org.apache.fesod.sheet.style;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.apache.fesod.sheet.ExcelWriter;
import org.apache.fesod.sheet.FesodSheet;
import org.apache.fesod.sheet.enums.WriteDirectionEnum;
import org.apache.fesod.sheet.testkit.Tags;
import org.apache.fesod.sheet.testkit.base.AbstractExcelTest;
import org.apache.fesod.sheet.testkit.enums.ExcelFormat;
import org.apache.fesod.sheet.write.metadata.WriteSheet;
import org.apache.fesod.sheet.write.metadata.fill.FillConfig;
import org.apache.fesod.sheet.write.metadata.fill.FillWrapper;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@Tag(Tags.ROUND_TRIP)
class InterleavedFillTest extends AbstractExcelTest {

    private static final String[] GROUPS = {"upper", "middle", "lower"};
    private static final short[] COLORS = {
        IndexedColors.YELLOW.getIndex(), IndexedColors.LIGHT_GREEN.getIndex(), IndexedColors.LIGHT_BLUE.getIndex()
    };

    private Stream<Arguments> workbooks() {
        return Stream.of(ExcelFormat.XLS, ExcelFormat.XLSX)
                .flatMap(format -> Stream.of(false, true).map(inMemory -> Arguments.of(format, inMemory)));
    }

    @ParameterizedTest
    @MethodSource("workbooks")
    void appendInterleavedPages(ExcelFormat format, boolean inMemory) throws Exception {
        assertPages(format, inMemory, Arrays.asList("upper", "middle", "lower", "upper", "middle", "lower"));
    }

    @ParameterizedTest
    @MethodSource("workbooks")
    void appendAfterFillingLowerGroupsFirst(ExcelFormat format, boolean inMemory) throws Exception {
        assertPages(format, inMemory, Arrays.asList("lower", "middle", "upper", "lower", "middle", "upper"));
    }

    @ParameterizedTest
    @MethodSource("workbooks")
    void appendOrderedPages(ExcelFormat format, boolean inMemory) throws Exception {
        assertPages(format, inMemory, Arrays.asList("upper", "upper", "middle", "middle", "lower", "lower"));
    }

    private void assertPages(ExcelFormat format, boolean inMemory, List<String> order) throws Exception {
        File result = createTempFile("interleaved", format);
        FillConfig config = FillConfig.builder().forceNewRow(true).build();
        WriteSheet writeSheet = FesodSheet.writerSheet("Report").build();
        Map<String, Integer> nextRecord = new HashMap<>();
        try (ExcelWriter writer = FesodSheet.write(result)
                .inMemory(inMemory)
                .withTemplate(new ByteArrayInputStream(template(format, false)))
                .build()) {
            for (String group : order) {
                int firstRecord = nextRecord.getOrDefault(group, 1);
                writer.fill(new FillWrapper(group, records(group, firstRecord, 2)), config, writeSheet);
                nextRecord.put(group, firstRecord + 2);
            }
            writer.fill(Collections.singletonMap("total", "12"), writeSheet);
        }
        try (Workbook workbook = WorkbookFactory.create(result)) {
            Sheet sheet = workbook.getSheet("Report");
            for (int group = 0; group < GROUPS.length; group++) {
                for (int record = 0; record < 4; record++) {
                    Row row = sheet.getRow(group * 6 + record);
                    String expected = GROUPS[group] + "-" + (record + 1);
                    assertEquals(expected, row.getCell(0).getStringCellValue());
                    assertEquals("Value: " + expected, row.getCell(1).getStringCellValue());
                    assertEquals((short) (400 + group * 100), row.getHeight());
                    assertStyle(row.getCell(0), COLORS[group]);
                    assertStyle(row.getCell(1), COLORS[group]);
                }
            }
            assertEquals("Footer: 12", sheet.getRow(18).getCell(0).getStringCellValue());
            assertEquals(18, sheet.getLastRowNum());
        }
    }

    @ParameterizedTest
    @MethodSource("workbooks")
    void horizontalCursorSurvivesRowsInsertedAbove(ExcelFormat format, boolean inMemory) throws Exception {
        File result = createTempFile("horizontal", format);
        WriteSheet writeSheet = FesodSheet.writerSheet("Report").build();
        FillConfig vertical = FillConfig.builder().forceNewRow(true).build();
        FillConfig horizontal =
                FillConfig.builder().direction(WriteDirectionEnum.HORIZONTAL).build();
        ByteArrayOutputStream template = new ByteArrayOutputStream();
        try (Workbook workbook = newWorkbook(format)) {
            Sheet sheet = workbook.createSheet("Report");
            sheet.createRow(0).createCell(0).setCellValue("{upper.name}");
            Cell cell = sheet.createRow(3).createCell(0);
            cell.setCellValue("{lower.name}");
            cell.setCellStyle(style(workbook, COLORS[2]));
            sheet.createRow(6).createCell(0).setCellValue("Footer");
            workbook.write(template);
        }
        try (ExcelWriter writer = FesodSheet.write(result)
                .inMemory(inMemory)
                .withTemplate(new ByteArrayInputStream(template.toByteArray()))
                .build()) {
            writer.fill(new FillWrapper("lower", records("lower", 1, 2)), horizontal, writeSheet);
            writer.fill(new FillWrapper("upper", records("upper", 1, 3)), vertical, writeSheet);
            writer.fill(new FillWrapper("lower", records("lower", 3, 2)), horizontal, writeSheet);
            writer.fill(new FillWrapper("upper", records("upper", 4, 2)), vertical, writeSheet);
            writer.fill(new FillWrapper("lower", records("lower", 5, 1)), horizontal, writeSheet);
        }
        try (Workbook workbook = WorkbookFactory.create(result)) {
            Sheet sheet = workbook.getSheet("Report");
            for (int record = 0; record < 5; record++) {
                assertEquals(
                        "upper-" + (record + 1), sheet.getRow(record).getCell(0).getStringCellValue());
                Cell cell = sheet.getRow(7).getCell(record);
                assertEquals("lower-" + (record + 1), cell.getStringCellValue());
                assertStyle(cell, COLORS[2]);
            }
            assertEquals("Footer", sheet.getRow(10).getCell(0).getStringCellValue());
        }
    }

    @ParameterizedTest
    @MethodSource("workbooks")
    void rowsInsertedOnAnotherSheetDoNotMoveCachedPositions(ExcelFormat format, boolean inMemory) throws Exception {
        File result = createTempFile("sheets", format);
        FillConfig config = FillConfig.builder().forceNewRow(true).build();
        WriteSheet first = FesodSheet.writerSheet("Report").build();
        WriteSheet second = FesodSheet.writerSheet("Other").build();
        try (ExcelWriter writer = FesodSheet.write(result)
                .inMemory(inMemory)
                .withTemplate(new ByteArrayInputStream(template(format, true)))
                .build()) {
            writer.fill(new FillWrapper("lower", records("other", 1, 1)), config, second);
            writer.fill(new FillWrapper("upper", records("upper", 1, 3)), config, first);
            writer.fill(new FillWrapper("lower", records("other", 2, 1)), config, second);
        }
        try (Workbook workbook = WorkbookFactory.create(result)) {
            Sheet other = workbook.getSheet("Other");
            assertEquals("other-1", other.getRow(6).getCell(0).getStringCellValue());
            assertEquals("other-2", other.getRow(7).getCell(0).getStringCellValue());
            assertEquals("Footer: ", other.getRow(10).getCell(0).getStringCellValue());
            assertNull(other.getRow(11));
        }
    }

    private byte[] template(ExcelFormat format, boolean secondSheet) throws Exception {
        ByteArrayOutputStream template = new ByteArrayOutputStream();
        try (Workbook workbook = newWorkbook(format)) {
            createTemplateSheet(workbook, "Report");
            if (secondSheet) {
                createTemplateSheet(workbook, "Other");
            }
            workbook.write(template);
        }
        return template.toByteArray();
    }

    private void createTemplateSheet(Workbook workbook, String name) {
        Sheet sheet = workbook.createSheet(name);
        for (int group = 0; group < GROUPS.length; group++) {
            Row row = sheet.createRow(group * 3);
            row.setHeight((short) (400 + group * 100));
            CellStyle style = style(workbook, COLORS[group]);
            row.createCell(0).setCellValue("{" + GROUPS[group] + ".name}");
            row.createCell(1).setCellValue("Value: {" + GROUPS[group] + ".name}");
            row.getCell(0).setCellStyle(style);
            row.getCell(1).setCellStyle(style);
        }
        sheet.createRow(9).createCell(0).setCellValue("Footer: {total}");
    }

    private Workbook newWorkbook(ExcelFormat format) {
        return format == ExcelFormat.XLS ? new HSSFWorkbook() : new XSSFWorkbook();
    }

    private CellStyle style(Workbook workbook, short color) {
        CellStyle style = workbook.createCellStyle();
        style.setFillForegroundColor(color);
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        return style;
    }

    private void assertStyle(Cell cell, short color) {
        assertEquals(color, cell.getCellStyle().getFillForegroundColor());
        assertEquals(FillPatternType.SOLID_FOREGROUND, cell.getCellStyle().getFillPattern());
        assertEquals(BorderStyle.THIN, cell.getCellStyle().getBorderBottom());
    }

    private List<Map<String, String>> records(String group, int start, int count) {
        List<Map<String, String>> records = new ArrayList<>();
        for (int record = start; record < start + count; record++) {
            records.add(Collections.singletonMap("name", group + "-" + record));
        }
        return records;
    }
}
