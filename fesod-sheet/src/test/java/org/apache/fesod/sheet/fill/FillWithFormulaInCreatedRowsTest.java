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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.fesod.sheet.ExcelWriter;
import org.apache.fesod.sheet.FesodSheet;
import org.apache.fesod.sheet.testkit.Tags;
import org.apache.fesod.sheet.testkit.base.AbstractExcelTest;
import org.apache.fesod.sheet.testkit.enums.ExcelFormat;
import org.apache.fesod.sheet.write.metadata.WriteSheet;
import org.apache.fesod.sheet.write.metadata.fill.FillConfig;
import org.apache.fesod.sheet.write.metadata.fill.FillWrapper;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag(Tags.ROUND_TRIP)
public class FillWithFormulaInCreatedRowsTest extends AbstractExcelTest {

    private File createTemplate() throws Exception {
        File template = createTempFile(ExcelFormat.XLSX);
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Sheet0");
            Row headRow = sheet.createRow(0);
            headRow.createCell(0).setCellValue("item");
            headRow.createCell(1).setCellValue("double");
            headRow.createCell(2).setCellValue("abs");
            Row templateRow = sheet.createRow(1);
            templateRow.createCell(0).setCellValue("{data.name}");
            templateRow.createCell(1).setCellFormula("A2*2");
            templateRow.createCell(2).setCellFormula("$A$2*10");
            try (FileOutputStream out = new FileOutputStream(template)) {
                workbook.write(out);
            }
        }
        return template;
    }

    private List<Map<String, String>> buildData(int size) {
        List<Map<String, String>> list = new ArrayList<>();
        for (int i = 1; i <= size; i++) {
            Map<String, String> row = new HashMap<>();
            row.put("name", "n" + i);
            list.add(row);
        }
        return list;
    }

    private static String formulaOf(Sheet sheet, int rowIndex, int columnIndex) {
        Row row = sheet.getRow(rowIndex);
        if (row == null) {
            return null;
        }
        Cell cell = row.getCell(columnIndex);
        if (cell == null) {
            return null;
        }
        return cell.getCellType() == CellType.FORMULA ? cell.getCellFormula() : null;
    }

    @Test
    void fillListKeepsFormulasInCreatedRows() throws Exception {
        File template = createTemplate();
        File outFile = createTempFile(ExcelFormat.XLSX);

        FesodSheet.write(outFile)
                .withTemplate(template)
                .sheet()
                .doFill(
                        new FillWrapper("data", buildData(3)),
                        FillConfig.builder().forceNewRow(true).build());

        try (XSSFWorkbook workbook = new XSSFWorkbook(new FileInputStream(outFile))) {
            Sheet sheet = workbook.getSheetAt(0);
            // Values are filled for every created row.
            for (int i = 1; i <= 3; i++) {
                Assertions.assertEquals("n" + i, sheet.getRow(i).getCell(0).getStringCellValue());
            }
            // The original row keeps its template formulas; every created row carries a
            // translated copy: relative references follow the copied row, absolute ones stay.
            Assertions.assertEquals("A2*2", formulaOf(sheet, 1, 1));
            Assertions.assertEquals("A3*2", formulaOf(sheet, 2, 1));
            Assertions.assertEquals("A4*2", formulaOf(sheet, 3, 1));
            Assertions.assertEquals("$A$2*10", formulaOf(sheet, 1, 2));
            Assertions.assertEquals("$A$2*10", formulaOf(sheet, 2, 2));
            Assertions.assertEquals("$A$2*10", formulaOf(sheet, 3, 2));
        }
    }

    @Test
    void fillListWithoutForceNewRowKeepsFormulasInCreatedRows() throws Exception {
        File template = createTemplate();
        File outFile = createTempFile(ExcelFormat.XLSX);

        FesodSheet.write(outFile).withTemplate(template).sheet().doFill(new FillWrapper("data", buildData(3)));

        try (XSSFWorkbook workbook = new XSSFWorkbook(new FileInputStream(outFile))) {
            Sheet sheet = workbook.getSheetAt(0);
            Assertions.assertEquals("A2*2", formulaOf(sheet, 1, 1));
            Assertions.assertEquals("A3*2", formulaOf(sheet, 2, 1));
            Assertions.assertEquals("A4*2", formulaOf(sheet, 3, 1));
            Assertions.assertEquals("$A$2*10", formulaOf(sheet, 2, 2));
        }
    }

    /**
     * Two collection areas on one sheet: {@code other} sits above {@code data}, so {@code data}
     * is filled first (full template scan) and {@code other} is filled second (its analysis data
     * is already cached). Formulas next to both collections must survive.
     */
    private File createTwoCollectionTemplate() throws Exception {
        File template = createTempFile(ExcelFormat.XLSX);
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Sheet0");
            Row headRow = sheet.createRow(0);
            headRow.createCell(0).setCellValue("item");
            headRow.createCell(1).setCellValue("double");
            Row otherRow = sheet.createRow(1);
            otherRow.createCell(0).setCellValue("{other.name}");
            otherRow.createCell(1).setCellFormula("A2*2");
            Row separator = sheet.createRow(2);
            separator.createCell(0).setCellValue("sep");
            Row dataRow = sheet.createRow(3);
            dataRow.createCell(0).setCellValue("{data.name}");
            dataRow.createCell(1).setCellFormula("A4*2");
            try (FileOutputStream out = new FileOutputStream(template)) {
                workbook.write(out);
            }
        }
        return template;
    }

    @Test
    void fillKeepsFormulasForEveryCollectionArea() throws Exception {
        File template = createTwoCollectionTemplate();
        File outFile = createTempFile(ExcelFormat.XLSX);

        // One writer, two fills: builder.doFill() finishes the writer after every call, so two
        // collection areas need the lower-level fill API.
        ExcelWriter writer = FesodSheet.write(outFile).withTemplate(template).build();
        WriteSheet writeSheet = new WriteSheet();
        writer.fill(new FillWrapper("data", buildData(2)), writeSheet);
        writer.fill(new FillWrapper("other", buildData(2)), writeSheet);
        writer.finish();

        try (XSSFWorkbook workbook = new XSSFWorkbook(new FileInputStream(outFile))) {
            Sheet sheet = workbook.getSheetAt(0);
            // The collection filled first keeps its formulas (existing behaviour).
            Assertions.assertEquals("A4*2", formulaOf(sheet, 3, 1));
            Assertions.assertEquals("A5*2", formulaOf(sheet, 4, 1));
            // The collection filled second reaches the cached analysis data; its formulas must be
            // collected on that path too.
            Assertions.assertEquals("A2*2", formulaOf(sheet, 1, 1));
            Assertions.assertEquals("A3*2", formulaOf(sheet, 2, 1));
        }
    }
}
