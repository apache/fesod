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

package org.apache.fesod.sheet.annotation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import lombok.Data;
import org.apache.fesod.common.util.StringUtils;
import org.apache.fesod.sheet.FesodSheet;
import org.apache.fesod.sheet.annotation.format.DateTimeFormat;
import org.apache.fesod.sheet.context.AnalysisContext;
import org.apache.fesod.sheet.read.listener.ReadListener;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class ExcelPropertyFormatTest {

    @Data
    static class FormatSample {
        @ExcelProperty()
        @DateTimeFormat("yyyy-MMM-dd")
        private Date date2;

        @ExcelProperty()
        @DateTimeFormat("yyyy-MMM-dd")
        private LocalDate dateLocalDate2;
    }

    @Test
    public void testSingleFormatSampleWriteAndRead(@TempDir Path tempDir) throws IOException {
        List<FormatSample> singleElementList = new LinkedList<>();
        FormatSample sample = new FormatSample();
        sample.setDate2(new Date());
        sample.setDateLocalDate2(LocalDate.of(2025, 2, 1));
        singleElementList.add(sample);
        String fileName =
                tempDir.resolve(System.currentTimeMillis() + "_single.xlsx").toString();
        FesodSheet.write(fileName, FormatSample.class).sheet("UnitTest").doWrite(singleElementList);
        try (FileInputStream fis = new FileInputStream(fileName);
                XSSFWorkbook workbook = new XSSFWorkbook(fis)) {
            Row dataRow = workbook.getSheetAt(0).getRow(1);
            assertEquals("yyyy-MMM-dd", dataRow.getCell(0).getCellStyle().getDataFormatString());
            assertEquals("yyyy-MMM-dd", dataRow.getCell(1).getCellStyle().getDataFormatString());
        }
    }

    /**
     * Test cell format string after writing date format pattern with Chinese characters.
     */
    @Data
    static class ChinesePatternSample {
        @ExcelProperty
        @DateTimeFormat("yyyy年MM月dd日HH时mm分ss秒")
        private Date fullDate;

        @ExcelProperty
        @DateTimeFormat("yyyy年MM月dd日")
        private LocalDate localDate;
    }

    @Test
    public void testChinesePatternFormat(@TempDir Path tempDir) throws IOException {
        ChinesePatternSample sample = new ChinesePatternSample();
        sample.setFullDate(new Date());
        sample.setLocalDate(LocalDate.of(2025, 1, 2));
        String fileName = tempDir.resolve("chinese_pattern.xlsx").toString();
        FesodSheet.write(fileName, ChinesePatternSample.class)
                .sheet("ChineseFormat")
                .doWrite(Collections.singletonList(sample));
        try (FileInputStream fis = new FileInputStream(fileName);
                XSSFWorkbook workbook = new XSSFWorkbook(fis)) {
            Row dataRow = workbook.getSheetAt(0).getRow(1);
            assertEquals(
                    "yyyy年MM月dd日HH时mm分ss秒", dataRow.getCell(0).getCellStyle().getDataFormatString());
            assertEquals("yyyy年MM月dd日", dataRow.getCell(1).getCellStyle().getDataFormatString());
        }
    }

    /**
     * Test different fields with different formats.
     */
    @Data
    static class MultiPatternSample {
        @ExcelProperty
        @DateTimeFormat("yyyy/MM/dd")
        private Date dateSlash;

        @ExcelProperty
        @DateTimeFormat("dd-MM-yyyy")
        private LocalDate dateDash;

        @ExcelProperty
        @DateTimeFormat("yyyy-MM-dd HH:mm")
        private LocalDateTime dateTimeMinute;
    }

    @Test
    public void testMultiplePatternsPerRow(@TempDir Path tempDir) throws IOException {
        MultiPatternSample s = new MultiPatternSample();
        s.setDateSlash(new Date());
        s.setDateDash(LocalDate.of(2024, 12, 31));
        s.setDateTimeMinute(LocalDateTime.of(2025, 3, 4, 15, 20));
        String file = tempDir.resolve("multi_pattern.xlsx").toString();
        FesodSheet.write(file, MultiPatternSample.class).sheet("MultiFormat").doWrite(Collections.singletonList(s));
        try (FileInputStream fis = new FileInputStream(file);
                XSSFWorkbook workbook = new XSSFWorkbook(fis)) {
            Row row = workbook.getSheetAt(0).getRow(1);
            assertEquals("yyyy/MM/dd", row.getCell(0).getCellStyle().getDataFormatString());
            assertEquals("dd-MM-yyyy", row.getCell(1).getCellStyle().getDataFormatString());
            assertEquals("yyyy-MM-dd HH:mm", row.getCell(2).getCellStyle().getDataFormatString());
        }
    }

    /**
     * Write Date with pattern, read to String field with annotation pattern, verify the read string matches the formatted value.
     */
    @Data
    static class WriteDateModel {
        @ExcelProperty
        @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
        private Date eventTime;
    }

    @Data
    static class ReadStringModel {
        @ExcelProperty
        @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
        private String eventTime; // Should be formatted as string when reading
    }

    static class CapturingListener implements ReadListener<ReadStringModel> {
        private final List<ReadStringModel> list = new ArrayList<>();

        @Override
        public void invoke(ReadStringModel data, AnalysisContext context) {
            list.add(data);
        }

        @Override
        public void doAfterAllAnalysed(AnalysisContext context) {}

        List<ReadStringModel> getList() {
            return list;
        }
    }

    /**
     * Test null date field: should write blank cell and not throw exception if value is not set.
     */
    @Data
    static class NullDateSample {
        @ExcelProperty
        @DateTimeFormat("yyyy-MM-dd")
        private Date mayBeNull;
    }

    @Test
    public void testNullDateFieldWritesBlank(@TempDir Path tempDir) throws IOException {
        NullDateSample sample = new NullDateSample();
        String file = tempDir.resolve("null_date.xlsx").toString();
        FesodSheet.write(file, NullDateSample.class).sheet("Null").doWrite(Collections.singletonList(sample));
        try (FileInputStream fis = new FileInputStream(file);
                XSSFWorkbook workbook = new XSSFWorkbook(fis)) {
            Row row = workbook.getSheetAt(0).getRow(1);
            assertNotNull(row, "expect row existing");
            assertTrue(
                    StringUtils.isBlank(row.getCell(0).getStringCellValue()),
                    "Empty date field should write as blank cell");
        }
    }
}
