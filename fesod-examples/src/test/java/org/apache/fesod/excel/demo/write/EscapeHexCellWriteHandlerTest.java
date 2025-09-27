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

package org.apache.fesod.excel.demo.write;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.fesod.excel.FastExcel;
import org.apache.fesod.excel.support.ExcelTypeEnum;
import org.apache.fesod.excel.write.handler.EscapeHexCellWriteHandler;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class EscapeHexCellWriteHandlerTest {

    @TempDir
    Path tempDir;

    private DemoData createDemoData(String str) {
        DemoData data = new DemoData();
        data.setString(str);
        data.setDate(new Date());
        data.setDoubleData(123.45);
        data.setIgnore("ignoreMe");
        return data;
    }

    @Test
    public void testEscapeHex_xlsx_singleHex() throws Exception {
        List<DemoData> list = new ArrayList<>();
        list.add(createDemoData("_xB9f0_"));

        File file = tempDir.resolve("testEscapeHex.xlsx").toFile();
        FastExcel.write(file, DemoData.class)
                .registerWriteHandler(new EscapeHexCellWriteHandler())
                .sheet("TestSheet")
                .doWrite(list);

        // Verify the result
        try (Workbook workbook = org.apache.poi.ss.usermodel.WorkbookFactory.create(file)) {
            Sheet sheet = workbook.getSheetAt(0);
            Row row = sheet.getRow(1); // Data row (header is row 0)
            Cell cell = row.getCell(0); // String column
            String actualValue = cell.getStringCellValue();
            System.out.println("XLSX result: " + actualValue);
            Assertions.assertNotEquals("_x005F_xB9f0_", actualValue, "xlsx should not escape _xB9f0_ to _x005F_xB9f0_");
        }
    }

    @Test
    public void testEscapeHex_xls_singleHex() throws Exception {
        List<DemoData> list = new ArrayList<>();
        list.add(createDemoData("_xB9f0_"));

        File file = tempDir.resolve("testEscapeHex.xls").toFile();
        FastExcel.write(file, DemoData.class)
                .excelType(ExcelTypeEnum.XLS)
                .registerWriteHandler(new EscapeHexCellWriteHandler())
                .sheet("TestSheet")
                .doWrite(list);

        // Verify the result
        try (Workbook workbook = org.apache.poi.ss.usermodel.WorkbookFactory.create(file)) {
            Sheet sheet = workbook.getSheetAt(0);
            Row row = sheet.getRow(1);
            Cell cell = row.getCell(0);
            Assertions.assertNotEquals(
                    "_x005F_xB9f0_", cell.getStringCellValue(), "xls should not escape _xB9f0_ to _x005F_xB9f0_");
        }
    }

    @Test
    public void testEscapeHex_csv_singleHex() throws Exception {
        List<DemoData> list = new ArrayList<>();
        list.add(createDemoData("_xB9f0_"));

        File file = tempDir.resolve("testEscapeHex.csv").toFile();
        FastExcel.write(file, DemoData.class)
                .excelType(ExcelTypeEnum.CSV)
                .registerWriteHandler(new EscapeHexCellWriteHandler())
                .sheet("TestSheet")
                .doWrite(list);

        // Verify the result
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            reader.readLine(); // Skip header
            String dataLine = reader.readLine();
            Assertions.assertNotNull(dataLine);
            Assertions.assertFalse(
                    dataLine.contains("_x005F_xB9f0_"),
                    "csv should not contain escaped _x005F_xB9f0_, but was: " + dataLine);
        }
    }

    @Test
    public void testEscapeHex_multipleHexInOneString() throws Exception {
        List<DemoData> list = new ArrayList<>();
        list.add(createDemoData("_xB9f0_ and _x1234_ and _xABCD_"));

        File file = tempDir.resolve("testMultipleHex.xlsx").toFile();
        FastExcel.write(file, DemoData.class)
                .registerWriteHandler(new EscapeHexCellWriteHandler())
                .sheet("TestSheet")
                .doWrite(list);

        try (Workbook workbook = org.apache.poi.ss.usermodel.WorkbookFactory.create(file)) {
            Sheet sheet = workbook.getSheetAt(0);
            Row row = sheet.getRow(1);
            Cell cell = row.getCell(0);
            String expected = "_xB9f0_ and _x1234_ and _xABCD_";
            Assertions.assertEquals(expected, cell.getStringCellValue(), "Multiple hex patterns should all be escaped");
        }
    }

    @Test
    public void testEscapeHex_noHexPattern() throws Exception {
        List<DemoData> list = new ArrayList<>();
        list.add(createDemoData("normalString"));

        File file = tempDir.resolve("testNoHex.xlsx").toFile();
        FastExcel.write(file, DemoData.class)
                .registerWriteHandler(new EscapeHexCellWriteHandler())
                .sheet("TestSheet")
                .doWrite(list);

        try (Workbook workbook = org.apache.poi.ss.usermodel.WorkbookFactory.create(file)) {
            Sheet sheet = workbook.getSheetAt(0);
            Row row = sheet.getRow(1);
            Cell cell = row.getCell(0);
            Assertions.assertEquals("normalString", cell.getStringCellValue(), "Normal strings should not be modified");
        }
    }

    @Test
    public void testEscapeHex_partialHexPattern() throws Exception {
        List<DemoData> list = new ArrayList<>();
        list.add(createDemoData("_x123_ _xABC_ _x12345_")); // Invalid patterns

        File file = tempDir.resolve("testPartialHex.xlsx").toFile();
        FastExcel.write(file, DemoData.class)
                .registerWriteHandler(new EscapeHexCellWriteHandler())
                .sheet("TestSheet")
                .doWrite(list);

        try (Workbook workbook = org.apache.poi.ss.usermodel.WorkbookFactory.create(file)) {
            Sheet sheet = workbook.getSheetAt(0);
            Row row = sheet.getRow(1);
            Cell cell = row.getCell(0);
            Assertions.assertEquals(
                    "_x123_ _xABC_ _x12345_", cell.getStringCellValue(), "Invalid hex patterns should not be modified");
        }
    }

    @Test
    public void testEscapeHex_mixedValidAndInvalidPatterns() throws Exception {
        List<DemoData> list = new ArrayList<>();
        list.add(createDemoData("_x1234_ _x123_ _xABCD_ _xGHIJ_"));

        File file = tempDir.resolve("testMixedHex.xlsx").toFile();
        FastExcel.write(file, DemoData.class)
                .registerWriteHandler(new EscapeHexCellWriteHandler())
                .sheet("TestSheet")
                .doWrite(list);

        try (Workbook workbook = org.apache.poi.ss.usermodel.WorkbookFactory.create(file)) {
            Sheet sheet = workbook.getSheetAt(0);
            Row row = sheet.getRow(1);
            Cell cell = row.getCell(0);
            String expected = "_x1234_ _x123_ _xABCD_ _xGHIJ_";
            Assertions.assertEquals(expected, cell.getStringCellValue(), "Only valid hex patterns should be escaped");
        }
    }

    @Test
    public void testEscapeHex_emptyAndNullStrings() throws Exception {
        List<DemoData> list = new ArrayList<>();
        DemoData data1 = createDemoData("");
        DemoData data2 = createDemoData(null);
        list.add(data1);
        list.add(data2);

        File file = tempDir.resolve("testEmptyNull.xlsx").toFile();
        FastExcel.write(file, DemoData.class)
                .registerWriteHandler(new EscapeHexCellWriteHandler())
                .sheet("TestSheet")
                .doWrite(list);

        try (Workbook workbook = org.apache.poi.ss.usermodel.WorkbookFactory.create(file)) {
            Sheet sheet = workbook.getSheetAt(0);

            // Check empty string
            Row row1 = sheet.getRow(1);
            Cell cell1 = row1.getCell(0);
            Assertions.assertEquals("", cell1.getStringCellValue(), "Empty string should remain empty");

            // Check null string
            Row row2 = sheet.getRow(2);
            Cell cell2 = row2.getCell(0);
            if (cell2 != null) {
                Assertions.assertEquals("", cell2.getStringCellValue(), "Null string should be handled gracefully");
            }
        }
    }

    @Test
    public void testEscapeHex_caseInsensitiveHex() throws Exception {
        List<DemoData> list = new ArrayList<>();
        list.add(createDemoData("_x1a2B_ _XC3d4_ _x9F8e_"));

        File file = tempDir.resolve("testCaseInsensitive.xlsx").toFile();
        FastExcel.write(file, DemoData.class)
                .registerWriteHandler(new EscapeHexCellWriteHandler())
                .sheet("TestSheet")
                .doWrite(list);

        try (Workbook workbook = org.apache.poi.ss.usermodel.WorkbookFactory.create(file)) {
            Sheet sheet = workbook.getSheetAt(0);
            Row row = sheet.getRow(1);
            Cell cell = row.getCell(0);
            String expected = "_x1a2B_ _XC3d4_ _x9F8e_";
            Assertions.assertEquals(
                    expected, cell.getStringCellValue(), "Case-sensitive hex patterns should be handled correctly");
        }
    }

    @Test
    public void testEscapeHex_multipleDifferentFormats() throws Exception {
        List<DemoData> list = new ArrayList<>();
        list.add(createDemoData("_xB9f0_"));

        // Test xlsx
        File xlsxFile = tempDir.resolve("testFormats.xlsx").toFile();
        FastExcel.write(xlsxFile, DemoData.class)
                .registerWriteHandler(new EscapeHexCellWriteHandler())
                .sheet("TestSheet")
                .doWrite(list);

        // Test xls
        File xlsFile = tempDir.resolve("testFormats.xls").toFile();
        FastExcel.write(xlsFile, DemoData.class)
                .excelType(ExcelTypeEnum.XLS)
                .registerWriteHandler(new EscapeHexCellWriteHandler())
                .sheet("TestSheet")
                .doWrite(list);

        // Test csv
        File csvFile = tempDir.resolve("testFormats.csv").toFile();
        FastExcel.write(csvFile, DemoData.class)
                .excelType(ExcelTypeEnum.CSV)
                .registerWriteHandler(new EscapeHexCellWriteHandler())
                .sheet("TestSheet")
                .doWrite(list);

        // Verify all formats produce the same escaped result
        try (Workbook xlsxWorkbook = org.apache.poi.ss.usermodel.WorkbookFactory.create(xlsxFile);
                Workbook xlsWorkbook = org.apache.poi.ss.usermodel.WorkbookFactory.create(xlsFile);
                BufferedReader csvReader = new BufferedReader(new FileReader(csvFile))) {

            // Check xlsx
            String xlsxValue = xlsxWorkbook.getSheetAt(0).getRow(1).getCell(0).getStringCellValue();
            Assertions.assertEquals("_xB9f0_", xlsxValue);

            // Check xls
            String xlsValue = xlsWorkbook.getSheetAt(0).getRow(1).getCell(0).getStringCellValue();
            Assertions.assertEquals("_xB9f0_", xlsValue);

            // Check csv
            csvReader.readLine(); // Skip header
            String csvLine = csvReader.readLine();
            Assertions.assertTrue(csvLine.contains("_xB9f0_"));
            Assertions.assertFalse(csvLine.contains("_x005F_xB9f0_"));

            // All formats should produce the same result
            Assertions.assertEquals(xlsxValue, xlsValue, "xlsx, csv and xls should produce the same escaped result");
        }
    }
}
