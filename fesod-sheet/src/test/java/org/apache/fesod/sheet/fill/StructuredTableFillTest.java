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
import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.fesod.sheet.ExcelWriter;
import org.apache.fesod.sheet.FesodSheet;
import org.apache.fesod.sheet.enums.WriteDirectionEnum;
import org.apache.fesod.sheet.exception.ExcelGenerateException;
import org.apache.fesod.sheet.testkit.Tags;
import org.apache.fesod.sheet.testkit.base.AbstractExcelTest;
import org.apache.fesod.sheet.testkit.enums.ExcelFormat;
import org.apache.fesod.sheet.write.handler.WorkbookWriteHandler;
import org.apache.fesod.sheet.write.handler.impl.StructuredTableWorkbookWriteHandler;
import org.apache.fesod.sheet.write.metadata.WriteSheet;
import org.apache.fesod.sheet.write.metadata.fill.FillConfig;
import org.apache.fesod.sheet.write.metadata.holder.WriteWorkbookHolder;
import org.apache.poi.ss.SpreadsheetVersion;
import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFTable;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTTableColumn;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STTotalsRowFunction;

@Tag(Tags.ROUND_TRIP)
class StructuredTableFillTest extends AbstractExcelTest {

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void expandsTableAtLastPhysicalRowAcrossRepeatedFills(boolean inMemory) throws Exception {
        for (boolean forceNewRow : new boolean[] {false, true}) {
            File source = template(false, false);
            File output = createTempFile("table-fill", ExcelFormat.XLSX);
            fill(source, output, inMemory, forceNewRow);
            try (XSSFWorkbook workbook = new XSSFWorkbook(output)) {
                XSSFSheet sheet = workbook.getSheet("Data");
                XSSFTable table = sheet.getTables().get(0);
                assertTable(table, "A1:C4", "A1:C4");
                Assertions.assertEquals("third", sheet.getRow(3).getCell(0).getStringCellValue());
                Assertions.assertEquals(
                        "TableStyleMedium2",
                        table.getCTTable().getTableStyleInfo().getName());
                Assertions.assertTrue(table.getCTTable().getTableStyleInfo().getShowRowStripes());
                Assertions.assertEquals("Orders", table.getName());
                Assertions.assertEquals("Orders", table.getDisplayName());
                Assertions.assertEquals("Amount", table.getColumns().get(2).getName());
                Assertions.assertEquals(
                        "Orders[[#This Row],[Quantity]]*2",
                        table.getCTTable()
                                .getTableColumns()
                                .getTableColumnArray(2)
                                .getCalculatedColumnFormula()
                                .getStringValue());
                Assertions.assertEquals(
                        "SUM(Orders[Quantity])",
                        workbook.getSheet("Summary").getRow(0).getCell(0).getCellFormula());
            }
        }
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void expandsBeforeTotalsAndShiftsTablesBelowWithoutChangingOtherSheets(boolean inMemory) throws Exception {
        File source = template(true, true);
        File output = createTempFile("table-totals", ExcelFormat.XLSX);
        fill(source, output, inMemory, true);
        try (XSSFWorkbook workbook = new XSSFWorkbook(output)) {
            XSSFSheet sheet = workbook.getSheet("Data");
            XSSFTable table = sheet.getTables().get(0);
            assertTable(table, "A1:C5", "A1:C4");
            Assertions.assertEquals(1, table.getTotalsRowCount());
            Assertions.assertEquals("Totals", sheet.getRow(4).getCell(0).getStringCellValue());
            CTTableColumn amount = table.getCTTable().getTableColumns().getTableColumnArray(2);
            Assertions.assertEquals(STTotalsRowFunction.SUM, amount.getTotalsRowFunction());
            Assertions.assertEquals(
                    "SUBTOTAL(109,Orders[Amount])", sheet.getRow(4).getCell(2).getCellFormula());
            assertTable(sheet.getTables().get(1), "A10:C11", "A10:C11");
            Assertions.assertEquals("unchanged", sheet.getRow(10).getCell(0).getStringCellValue());
            assertTable(workbook.getSheet("Other").getTables().get(0), "A1:C2", "A1:C2");
            Assertions.assertEquals(
                    "SUM(Orders[Quantity])",
                    workbook.getSheet("Summary").getRow(0).getCell(0).getCellFormula());
        }
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void keepsTablesUnchangedForAnEmptyCollection(boolean inMemory) throws Exception {
        File source = template(true, true);
        File output = createTempFile("table-empty", ExcelFormat.XLSX);
        FesodSheet.write(output)
                .withTemplate(source)
                .inMemory(inMemory)
                .sheet("Data")
                .doFill(
                        Collections.emptyList(),
                        FillConfig.builder().forceNewRow(true).build());
        try (XSSFWorkbook workbook = new XSSFWorkbook(output)) {
            assertTable(workbook.getSheet("Data").getTables().get(0), "A1:C3", "A1:C2");
            assertTable(workbook.getSheet("Data").getTables().get(1), "A8:C9", "A8:C9");
        }
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void shiftsA1ReferencesWithoutReplacingStructuredReferenceSyntax(boolean inMemory) throws Exception {
        File source = template(false, true);
        File modified = createTempFile("table-mixed-formulas", ExcelFormat.XLSX);
        try (XSSFWorkbook workbook = new XSSFWorkbook(source);
                FileOutputStream output = new FileOutputStream(modified)) {
            XSSFSheet summary = workbook.getSheet("Summary");
            summary.getRow(0).getCell(0).setCellFormula("SUM(Orders[Quantity])+Data!B9");
            summary.createRow(1).createCell(0).setCellFormula("SUM(Below[Quantity])");
            summary.createRow(2).createCell(0).setCellFormula("SUM(Orders[[#All],[Quantity]])+Data!B9");
            summary.createRow(3)
                    .createCell(0)
                    .setCellFormula("IF(\"Orders[Quantity]\"=\"Orders[Quantity]\",SUM(Orders[Quantity]),Data!B9)");
            summary.createRow(4).createCell(0).setCellFormula("SUM(orders[Quantity])+Data!B9");
            summary.createRow(5)
                    .createCell(0)
                    .setCellFormula("SUM(Orders[Quantity])+SUM(OtherOrders[Quantity])+Data!B9");
            workbook.setCellFormulaValidation(false);
            workbook.getSheet("Data").getRow(8).getCell(2).setCellFormula("[@Quantity]*2");
            workbook.write(output);
        }
        File output = createTempFile("table-mixed-formulas-filled", ExcelFormat.XLSX);
        fill(modified, output, inMemory, true);
        try (XSSFWorkbook workbook = new XSSFWorkbook(output)) {
            XSSFSheet summary = workbook.getSheet("Summary");
            Assertions.assertEquals(
                    "SUM(Orders[Quantity])+Data!B11",
                    summary.getRow(0).getCell(0).getCellFormula());
            Assertions.assertEquals(
                    "SUM(Below[Quantity])", summary.getRow(1).getCell(0).getCellFormula());
            Assertions.assertEquals(
                    "SUM(Orders[[#All],[Quantity]])+Data!B11",
                    summary.getRow(2).getCell(0).getCellFormula());
            Assertions.assertEquals(
                    "IF(\"Orders[Quantity]\"=\"Orders[Quantity]\",SUM(Orders[Quantity]),Data!B11)",
                    summary.getRow(3).getCell(0).getCellFormula());
            Assertions.assertEquals(
                    "SUM(orders[Quantity])+Data!B11",
                    summary.getRow(4).getCell(0).getCellFormula());
            Assertions.assertEquals(
                    "SUM(Orders[Quantity])+SUM(OtherOrders[Quantity])+Data!B11",
                    summary.getRow(5).getCell(0).getCellFormula());
            Assertions.assertEquals(
                    "[@Quantity]*2",
                    workbook.getSheet("Data").getRow(10).getCell(2).getCellFormula());
        }
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void preservesEscapedCharactersInStructuredColumnReferences(boolean inMemory) throws Exception {
        for (String[] header :
                new String[][] {{"O'Brien", "O''Brien"}, {"Q]ty", "Q']ty"}, {"Q[ty", "Q'[ty"}, {"Price#", "Price'#"}}) {
            File source = template(false, true);
            File modified = createTempFile("table-escaped-header", ExcelFormat.XLSX);
            String reference = "Orders[" + header[1] + "]";
            try (XSSFWorkbook workbook = new XSSFWorkbook(source);
                    FileOutputStream output = new FileOutputStream(modified)) {
                workbook.setCellFormulaValidation(false);
                XSSFSheet sheet = workbook.getSheet("Data");
                sheet.getRow(0).getCell(1).setCellValue(header[0]);
                sheet.getTables()
                        .get(0)
                        .getCTTable()
                        .getTableColumns()
                        .getTableColumnArray(1)
                        .setName(header[0]);
                sheet.getTables()
                        .get(0)
                        .getCTTable()
                        .getTableColumns()
                        .getTableColumnArray(2)
                        .getCalculatedColumnFormula()
                        .setStringValue(reference + "*2");
                sheet.getRow(1).getCell(2).setCellFormula(reference + "*2");
                workbook.getSheet("Summary").getRow(0).getCell(0).setCellFormula("SUM(" + reference + ")+Data!B9");
                workbook.write(output);
            }
            File output = createTempFile("escaped-header-filled", ExcelFormat.XLSX);
            fill(modified, output, inMemory, true);
            try (XSSFWorkbook workbook = new XSSFWorkbook(output)) {
                Assertions.assertEquals(
                        "SUM(" + reference + ")+Data!B11",
                        workbook.getSheet("Summary").getRow(0).getCell(0).getCellFormula());
                Assertions.assertEquals(
                        header[0],
                        workbook.getSheet("Data")
                                .getTables()
                                .get(0)
                                .getColumns()
                                .get(1)
                                .getName());
            }
        }
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void growsOnlyTheTargetTableWhenAnotherTableIsBesideIt(boolean inMemory) throws Exception {
        File source = template(false, false);
        File modified = createTempFile("side-by-side-tables", ExcelFormat.XLSX);
        try (XSSFWorkbook workbook = new XSSFWorkbook(source);
                FileOutputStream output = new FileOutputStream(modified)) {
            addTable(workbook.getSheet("Data"), "Beside", 0, false, 4);
            workbook.write(output);
        }
        File output = createTempFile("side-by-side-filled", ExcelFormat.XLSX);
        fill(modified, output, inMemory, false);
        try (XSSFWorkbook workbook = new XSSFWorkbook(output)) {
            assertTable(workbook.getSheet("Data").getTables().get(0), "A1:C4", "A1:C4");
            assertTable(workbook.getSheet("Data").getTables().get(1), "E1:G2", "E1:G2");
            Assertions.assertEquals(
                    "unchanged", workbook.getSheet("Data").getRow(1).getCell(4).getStringCellValue());
        }
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void expandsATableThatAlreadyHasMoreThanOneDataRow(boolean inMemory) throws Exception {
        File source = template(false, false);
        File modified = createTempFile("table-existing-data", ExcelFormat.XLSX);
        try (XSSFWorkbook workbook = new XSSFWorkbook(source);
                FileOutputStream output = new FileOutputStream(modified)) {
            XSSFSheet sheet = workbook.getSheet("Data");
            sheet.createRow(2).createCell(0).setCellValue("retained");
            sheet.getRow(2).createCell(1).setCellValue(10);
            sheet.getRow(2).createCell(2).setCellFormula("Orders[[#This Row],[Quantity]]*2");
            sheet.getTables().get(0).setCellReferences(new AreaReference("A1:C3", SpreadsheetVersion.EXCEL2007));
            workbook.write(output);
        }
        File output = createTempFile("table-existing-data-filled", ExcelFormat.XLSX);
        fill(modified, output, inMemory, true);
        try (XSSFWorkbook workbook = new XSSFWorkbook(output)) {
            assertTable(workbook.getSheet("Data").getTables().get(0), "A1:C5", "A1:C5");
            Assertions.assertEquals(
                    "retained", workbook.getSheet("Data").getRow(4).getCell(0).getStringCellValue());
            Assertions.assertEquals(
                    "SUM(Orders[Quantity])",
                    workbook.getSheet("Summary").getRow(0).getCell(0).getCellFormula());
        }
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void preservesCustomTotalsAndLabelOnlyTotals(boolean inMemory) throws Exception {
        for (boolean customFormula : new boolean[] {false, true}) {
            File source = template(true, false);
            File modified = createTempFile("table-custom-totals", ExcelFormat.XLSX);
            try (XSSFWorkbook workbook = new XSSFWorkbook(source);
                    FileOutputStream output = new FileOutputStream(modified)) {
                XSSFSheet sheet = workbook.getSheet("Data");
                CTTableColumn column =
                        sheet.getTables().get(0).getCTTable().getTableColumns().getTableColumnArray(2);
                if (customFormula) {
                    column.setTotalsRowFunction(STTotalsRowFunction.CUSTOM);
                    column.addNewTotalsRowFormula().setStringValue("SUM(Orders[Quantity])*2");
                    sheet.getRow(2).getCell(2).setCellFormula("SUM(Orders[Quantity])*2");
                } else {
                    column.unsetTotalsRowFunction();
                    sheet.getRow(2).getCell(2).setBlank();
                }
                workbook.write(output);
            }
            File output = createTempFile("custom-totals-filled", ExcelFormat.XLSX);
            fill(modified, output, inMemory, true);
            try (XSSFWorkbook workbook = new XSSFWorkbook(output)) {
                XSSFSheet sheet = workbook.getSheet("Data");
                XSSFTable table = sheet.getTables().get(0);
                assertTable(table, "A1:C5", "A1:C4");
                Assertions.assertEquals("Totals", sheet.getRow(4).getCell(0).getStringCellValue());
                Assertions.assertEquals(
                        "Totals",
                        table.getCTTable()
                                .getTableColumns()
                                .getTableColumnArray(0)
                                .getTotalsRowLabel());
                if (customFormula) {
                    Assertions.assertEquals(
                            "SUM(Orders[Quantity])*2",
                            sheet.getRow(4).getCell(2).getCellFormula());
                    Assertions.assertEquals(
                            "SUM(Orders[Quantity])*2",
                            table.getCTTable()
                                    .getTableColumns()
                                    .getTableColumnArray(2)
                                    .getTotalsRowFormula()
                                    .getStringValue());
                }
            }
        }
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void rejectsOverwritingTotalsAndUnsupportedColumnExpansion(boolean inMemory) throws Exception {
        File source = template(true, false);
        File output = createTempFile("table-rejected", ExcelFormat.XLSX);
        try (ExcelWriter writer =
                FesodSheet.write(output).withTemplate(source).inMemory(inMemory).build()) {
            ExcelGenerateException error = Assertions.assertThrows(
                    ExcelGenerateException.class,
                    () -> writer.fill(
                            Arrays.asList(item("first", 1), item("second", 2)),
                            FesodSheet.writerSheet("Data").build()));
            Assertions.assertTrue(error.getMessage().contains("forceNewRow(true)"));
        }
        source = template(false, false);
        try (ExcelWriter writer =
                FesodSheet.write(output).withTemplate(source).inMemory(inMemory).build()) {
            ExcelGenerateException error = Assertions.assertThrows(
                    ExcelGenerateException.class,
                    () -> writer.fill(
                            Arrays.asList(item("first", 1), item("second", 2), item("third", 3)),
                            FillConfig.builder()
                                    .direction(WriteDirectionEnum.HORIZONTAL)
                                    .build(),
                            FesodSheet.writerSheet("Data").build()));
            Assertions.assertTrue(error.getMessage().contains("Horizontal expansion"));
        }
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void rejectsExpansionIntoAnotherTable(boolean inMemory) throws Exception {
        File source = template(false, true);
        File output = createTempFile("table-overlap", ExcelFormat.XLSX);
        try (ExcelWriter writer =
                FesodSheet.write(output).withTemplate(source).inMemory(inMemory).build()) {
            Assertions.assertThrows(
                    ExcelGenerateException.class,
                    () -> writer.fill(
                            Collections.nCopies(9, item("record", 1)),
                            FesodSheet.writerSheet("Data").build()));
        }
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void rejectsARepeatedHorizontalFillPastTheTableBoundary(boolean inMemory) throws Exception {
        File source = template(false, false);
        File modified = createTempFile("horizontal-table-template", ExcelFormat.XLSX);
        try (XSSFWorkbook workbook = new XSSFWorkbook(source);
                FileOutputStream output = new FileOutputStream(modified)) {
            workbook.getSheet("Data").getRow(1).getCell(1).setCellValue("fixed");
            workbook.write(output);
        }
        File output = createTempFile("horizontal-table-filled", ExcelFormat.XLSX);
        try (ExcelWriter writer = FesodSheet.write(output)
                .withTemplate(modified)
                .inMemory(inMemory)
                .build()) {
            FillConfig config = FillConfig.builder()
                    .direction(WriteDirectionEnum.HORIZONTAL)
                    .build();
            WriteSheet sheet = FesodSheet.writerSheet("Data").build();
            writer.fill(Arrays.asList(item("first", 1), item("second", 2), item("third", 3)), config, sheet);
            ExcelGenerateException error = Assertions.assertThrows(
                    ExcelGenerateException.class,
                    () -> writer.fill(Collections.singletonList(item("fourth", 4)), config, sheet));
            Assertions.assertTrue(error.getMessage().contains("Horizontal expansion"));
        }
    }

    @Test
    void rejectsDuplicateTableNamesBeforeFilling() throws Exception {
        File source = template(false, true);
        File output = createTempFile("duplicate-table-name", ExcelFormat.XLSX);
        try (ExcelWriter writer = FesodSheet.write(output)
                .withTemplate(source)
                .registerWriteHandler(new WorkbookWriteHandler() {
                    @Override
                    public void afterWorkbookCreate(WriteWorkbookHolder holder) {
                        XSSFWorkbook workbook = (XSSFWorkbook) holder.getCachedWorkbook();
                        workbook.getSheet("Other")
                                .getTables()
                                .get(0)
                                .getCTTable()
                                .setName("Orders");
                    }
                })
                .build()) {
            ExcelGenerateException error = Assertions.assertThrows(
                    ExcelGenerateException.class,
                    () -> writer.fill(
                            Collections.singletonList(item("record", 1)),
                            FesodSheet.writerSheet("Data").build()));
            Assertions.assertTrue(error.getMessage().contains("unique"));
        }
    }

    @Test
    void rejectsShiftingATablePastTheWorksheetLimit() throws Exception {
        File source = template(false, false);
        File modified = createTempFile("table-at-sheet-limit", ExcelFormat.XLSX);
        try (XSSFWorkbook workbook = new XSSFWorkbook(source);
                FileOutputStream output = new FileOutputStream(modified)) {
            addTable(workbook.getSheet("Data"), "AtLimit", SpreadsheetVersion.EXCEL2007.getLastRowIndex() - 1, false);
            workbook.write(output);
        }
        File output = createTempFile("table-beyond-sheet-limit", ExcelFormat.XLSX);
        try (ExcelWriter writer =
                FesodSheet.write(output).withTemplate(modified).build()) {
            ExcelGenerateException error = Assertions.assertThrows(
                    ExcelGenerateException.class,
                    () -> writer.fill(
                            Arrays.asList(item("first", 1), item("second", 2)),
                            FillConfig.builder().forceNewRow(true).build(),
                            FesodSheet.writerSheet("Data").build()));
            Assertions.assertTrue(error.getMessage().contains("last worksheet row"));
        }
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void finalizesTablesAfterFillingMoreThanOneSheet(boolean inMemory) throws Exception {
        File source = template(false, true);
        File modified = createTempFile("multiple-table-sheets", ExcelFormat.XLSX);
        try (XSSFWorkbook workbook = new XSSFWorkbook(source);
                FileOutputStream output = new FileOutputStream(modified)) {
            workbook.getSheet("Other").getRow(1).getCell(0).setCellValue("{.name}");
            workbook.getSheet("Other").getRow(1).getCell(1).setCellValue("{.quantity}");
            workbook.write(output);
        }
        File output = createTempFile("multiple-table-sheets-filled", ExcelFormat.XLSX);
        try (ExcelWriter writer = FesodSheet.write(output)
                .withTemplate(modified)
                .inMemory(inMemory)
                .build()) {
            FillConfig config = FillConfig.builder().forceNewRow(true).build();
            writer.fill(
                    Arrays.asList(item("first", 1), item("second", 2)),
                    config,
                    FesodSheet.writerSheet("Data").build());
            writer.fill(
                    Arrays.asList(item("third", 3), item("fourth", 4), item("fifth", 5)),
                    config,
                    FesodSheet.writerSheet("Other").build());
        }
        try (XSSFWorkbook workbook = new XSSFWorkbook(output)) {
            assertTable(workbook.getSheet("Data").getTables().get(0), "A1:C3", "A1:C3");
            assertTable(workbook.getSheet("Other").getTables().get(0), "A1:C4", "A1:C4");
            Assertions.assertEquals(
                    "fifth", workbook.getSheet("Other").getRow(3).getCell(0).getStringCellValue());
        }
    }

    @Test
    void rejectsInconsistentTableColumnMetadata() throws Exception {
        File source = template(false, false);
        File output = createTempFile("inconsistent-table-columns", ExcelFormat.XLSX);
        try (ExcelWriter writer = FesodSheet.write(output)
                .withTemplate(source)
                .registerWriteHandler(new WorkbookWriteHandler() {
                    @Override
                    public void afterWorkbookCreate(WriteWorkbookHolder holder) {
                        ((XSSFWorkbook) holder.getCachedWorkbook())
                                .getSheet("Data")
                                .getTables()
                                .get(0)
                                .getCTTable()
                                .getTableColumns()
                                .setCount(2);
                    }
                })
                .build()) {
            ExcelGenerateException error = Assertions.assertThrows(
                    ExcelGenerateException.class,
                    () -> writer.fill(
                            Collections.singletonList(item("record", 1)),
                            FesodSheet.writerSheet("Data").build()));
            Assertions.assertTrue(error.getMessage().contains("different widths"));
        }
    }

    @Test
    void restoresTableFormulasWhenPoiRejectsTheRowShift() throws Exception {
        File source = template(false, false);
        try (XSSFWorkbook workbook = new XSSFWorkbook(source)) {
            XSSFSheet sheet = Mockito.spy(workbook.getSheet("Data"));
            IllegalStateException failure = new IllegalStateException("row shift failed");
            Mockito.doThrow(failure).when(sheet).shiftRows(2, 2, 1, true, false);
            StructuredTableWorkbookWriteHandler handler = new StructuredTableWorkbookWriteHandler();
            Assertions.assertSame(
                    failure,
                    Assertions.assertThrows(IllegalStateException.class, () -> handler.shiftRows(sheet, 1, 2, 1)));
            Assertions.assertEquals(
                    "SUM(Orders[Quantity])",
                    workbook.getSheet("Summary").getRow(0).getCell(0).getCellFormula());
        }
    }

    @Test
    void fillsAPristineClonedSheetWithIndependentTablePartsAndNames() throws Exception {
        File source = template(false, false);
        File output = createTempFile("cloned-table", ExcelFormat.XLSX);
        try (ExcelWriter writer = FesodSheet.write(output)
                .withTemplate(source)
                .inMemory(true)
                .registerWriteHandler(new WorkbookWriteHandler() {
                    @Override
                    public void afterWorkbookCreate(WriteWorkbookHolder holder) {
                        XSSFWorkbook workbook = (XSSFWorkbook) holder.getCachedWorkbook();
                        workbook.cloneSheet(0, "Copy");
                    }
                })
                .build()) {
            writer.fill(
                    Arrays.asList(item("first", 1), item("second", 2)),
                    FillConfig.builder().forceNewRow(true).build(),
                    FesodSheet.writerSheet("Copy").build());
        }
        try (XSSFWorkbook workbook = new XSSFWorkbook(output)) {
            XSSFTable original = workbook.getSheet("Data").getTables().get(0);
            XSSFTable copy = workbook.getSheet("Copy").getTables().get(0);
            assertTable(original, "A1:C2", "A1:C2");
            assertTable(copy, "A1:C3", "A1:C3");
            Assertions.assertNotEquals(original.getName(), copy.getName());
            Assertions.assertNotEquals(original.getDisplayName(), copy.getDisplayName());
            Assertions.assertNotEquals(
                    original.getPackagePart().getPartName(),
                    copy.getPackagePart().getPartName());
            Assertions.assertEquals(
                    copy.getName() + "[[#This Row],[Quantity]]*2",
                    copy.getCTTable()
                            .getTableColumns()
                            .getTableColumnArray(2)
                            .getCalculatedColumnFormula()
                            .getStringValue());
        }
    }

    private void fill(File template, File output, boolean inMemory, boolean forceNewRow) {
        try (ExcelWriter writer = FesodSheet.write(output)
                .withTemplate(template)
                .inMemory(inMemory)
                .build()) {
            WriteSheet sheet = FesodSheet.writerSheet("Data").build();
            FillConfig config = FillConfig.builder().forceNewRow(forceNewRow).build();
            writer.fill(Arrays.asList(item("first", 1), item("second", 2)), config, sheet);
            writer.fill(Collections.singletonList(item("third", 3)), config, sheet);
        }
    }

    private File template(boolean totals, boolean moreTables) throws Exception {
        File file = createTempFile("structured-table-template", ExcelFormat.XLSX);
        try (XSSFWorkbook workbook = new XSSFWorkbook();
                FileOutputStream output = new FileOutputStream(file)) {
            XSSFSheet sheet = workbook.createSheet("Data");
            XSSFTable table = addTable(sheet, "Orders", 0, totals);
            sheet.getRow(1).getCell(0).setCellValue("{.name}");
            sheet.getRow(1).getCell(1).setCellValue("{.quantity}");
            table.getCTTable()
                    .getTableColumns()
                    .getTableColumnArray(2)
                    .addNewCalculatedColumnFormula()
                    .setStringValue("Orders[[#This Row],[Quantity]]*2");
            sheet.getRow(1).getCell(2).setCellFormula("Orders[[#This Row],[Quantity]]*2");
            workbook.createSheet("Summary").createRow(0).createCell(0).setCellFormula("SUM(Orders[Quantity])");
            if (moreTables) {
                addTable(sheet, "Below", 7, false);
                addTable(workbook.createSheet("Other"), "OtherOrders", 0, false);
            }
            workbook.write(output);
        }
        return file;
    }

    private static XSSFTable addTable(XSSFSheet sheet, String name, int firstRow, boolean totals) {
        return addTable(sheet, name, firstRow, totals, 0);
    }

    private static XSSFTable addTable(XSSFSheet sheet, String name, int firstRow, boolean totals, int firstColumn) {
        List<String> headers = Arrays.asList("Name", "Quantity", "Amount");
        for (int row = firstRow; row <= firstRow + (totals ? 2 : 1); row++) {
            if (sheet.getRow(row) == null) {
                sheet.createRow(row);
            }
            for (int column = 0; column < headers.size(); column++) {
                sheet.getRow(row).createCell(firstColumn + column);
            }
        }
        for (int column = 0; column < headers.size(); column++) {
            sheet.getRow(firstRow).getCell(firstColumn + column).setCellValue(headers.get(column));
        }
        sheet.getRow(firstRow + 1).getCell(firstColumn).setCellValue("unchanged");
        XSSFTable table = sheet.createTable(new AreaReference(
                new CellReference(firstRow, firstColumn),
                new CellReference(firstRow + (totals ? 2 : 1), firstColumn + 2),
                SpreadsheetVersion.EXCEL2007));
        table.setName(name);
        table.setDisplayName(name);
        table.getCTTable().addNewTableStyleInfo().setName("TableStyleMedium2");
        table.getCTTable().getTableStyleInfo().setShowRowStripes(true);
        if (!table.getCTTable().isSetAutoFilter()) {
            table.getCTTable().addNewAutoFilter();
        }
        table.getCTTable()
                .getAutoFilter()
                .setRef(new AreaReference(
                                new CellReference(firstRow, firstColumn),
                                new CellReference(firstRow + 1, firstColumn + 2),
                                SpreadsheetVersion.EXCEL2007)
                        .formatAsString());
        if (totals) {
            table.getCTTable().setTotalsRowCount(1);
            table.getCTTable().getTableColumns().getTableColumnArray(0).setTotalsRowLabel("Totals");
            table.getCTTable().getTableColumns().getTableColumnArray(2).setTotalsRowFunction(STTotalsRowFunction.SUM);
            sheet.getRow(firstRow + 2).getCell(0).setCellValue("Totals");
            sheet.getRow(firstRow + 2).getCell(2).setCellFormula("SUBTOTAL(109," + name + "[Amount])");
        }
        return table;
    }

    private static Map<String, Object> item(String name, int quantity) {
        Map<String, Object> item = new HashMap<>();
        item.put("name", name);
        item.put("quantity", quantity);
        return item;
    }

    private static void assertTable(XSSFTable table, String area, String filter) {
        Assertions.assertEquals(area, table.getCTTable().getRef());
        Assertions.assertEquals(filter, table.getCTTable().getAutoFilter().getRef());
    }
}
