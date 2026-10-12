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
import org.apache.fesod.sheet.FesodSheet;
import org.apache.fesod.sheet.testkit.Tags;
import org.apache.fesod.sheet.testkit.base.AbstractExcelTest;
import org.apache.fesod.sheet.testkit.enums.ExcelFormat;
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
}
