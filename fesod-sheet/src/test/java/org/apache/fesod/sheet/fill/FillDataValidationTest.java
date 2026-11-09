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
import java.util.List;
import org.apache.fesod.sheet.ExcelWriter;
import org.apache.fesod.sheet.FesodSheet;
import org.apache.fesod.sheet.testkit.Tags;
import org.apache.fesod.sheet.testkit.base.AbstractExcelTest;
import org.apache.fesod.sheet.testkit.enums.ExcelFormat;
import org.apache.fesod.sheet.testkit.params.ExcelFormatSource;
import org.apache.fesod.sheet.testkit.params.FormatScope;
import org.apache.fesod.sheet.write.metadata.WriteSheet;
import org.apache.fesod.sheet.write.metadata.fill.FillConfig;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.DataValidation;
import org.apache.poi.ss.usermodel.DataValidationConstraint;
import org.apache.poi.ss.usermodel.DataValidationHelper;
import org.apache.poi.ss.usermodel.Name;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@Tag(Tags.ROUND_TRIP)
class FillDataValidationTest extends AbstractExcelTest {

    @ParameterizedTest
    @ExcelFormatSource(FormatScope.BINARY)
    void shiftsDropdownRangesWithInsertedRowsAndPreservesRules(ExcelFormat format) throws Exception {
        File template = template(format);
        File output = createTempFile("validation-fill", format);
        FillConfig config = FillConfig.builder().forceNewRow(true).build();
        try (ExcelWriter writer =
                FesodSheet.write(output).withTemplate(template).build()) {
            WriteSheet sheet = FesodSheet.writerSheet().build();
            writer.fill(
                    Arrays.asList(
                            Collections.singletonMap("name", "first"), Collections.singletonMap("name", "second")),
                    config,
                    sheet);
            writer.fill(Collections.singletonList(Collections.singletonMap("name", "third")), config, sheet);
        }

        try (Workbook workbook = WorkbookFactory.create(output)) {
            Sheet sheet = workbook.getSheetAt(0);
            Assertions.assertEquals("footer", sheet.getRow(6).getCell(0).getStringCellValue());
            List<? extends DataValidation> validations = sheet.getDataValidations();
            Assertions.assertEquals(4, validations.size());
            assertRange(validations.get(0), 0, 0);
            assertRange(validations.get(1), 6, 7);
            assertRange(validations.get(2), 0, 7);
            assertRange(validations.get(3), 0, workbook.getSpreadsheetVersion().getLastRowIndex());
            for (DataValidation validation : validations) {
                Assertions.assertArrayEquals(
                        new String[] {"yes", "no"},
                        validation.getValidationConstraint().getExplicitListValues());
                Assertions.assertEquals("Choose", validation.getPromptBoxTitle());
                Assertions.assertEquals("Pick a value", validation.getPromptBoxText());
                Assertions.assertTrue(validation.getShowPromptBox());
                Assertions.assertTrue(validation.getShowErrorBox());
                Assertions.assertFalse(validation.getEmptyCellAllowed());
            }
        }
    }

    @ParameterizedTest
    @ExcelFormatSource(FormatScope.BINARY)
    void shiftsDropdownsBelowTheLastPhysicalRow(ExcelFormat format) throws Exception {
        File source = template(format);
        File template = createTempFile("validation-without-footer", format);
        try (Workbook workbook = WorkbookFactory.create(source)) {
            Sheet sheet = workbook.getSheetAt(0);
            sheet.removeRow(sheet.getRow(4));
            try (FileOutputStream output = new FileOutputStream(template)) {
                workbook.write(output);
            }
        }
        File output = createTempFile("validation-below-last-row", format);
        FesodSheet.write(output)
                .withTemplate(template)
                .sheet()
                .doFill(
                        Arrays.asList(
                                Collections.singletonMap("name", "first"), Collections.singletonMap("name", "second")),
                        FillConfig.builder().forceNewRow(true).build());
        try (Workbook workbook = WorkbookFactory.create(output)) {
            assertRange(workbook.getSheetAt(0).getDataValidations().get(1), 5, 6);
        }
    }

    @ParameterizedTest
    @ExcelFormatSource(FormatScope.BINARY)
    void keepsDropdownRangesWhenFillDoesNotInsertRows(ExcelFormat format) throws Exception {
        File template = template(format);
        File output = createTempFile("validation-no-shift", format);
        FesodSheet.write(output)
                .withTemplate(template)
                .sheet()
                .doFill(Collections.singletonList(Collections.singletonMap("name", "first")));
        try (Workbook workbook = WorkbookFactory.create(output)) {
            List<? extends DataValidation> validations = workbook.getSheetAt(0).getDataValidations();
            assertRange(validations.get(0), 0, 0);
            assertRange(validations.get(1), 4, 5);
            assertRange(validations.get(2), 0, 5);
        }
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void shiftsXlsxValidationFormulasWithRepeatedRowInsertion(boolean inMemory) throws Exception {
        File template = formulaTemplate("$E$5:$E$6");
        File output = createTempFile("validation-formulas", ExcelFormat.XLSX);
        FillConfig config = FillConfig.builder().forceNewRow(true).build();
        try (ExcelWriter writer = FesodSheet.write(output)
                .withTemplate(template)
                .inMemory(inMemory)
                .build()) {
            WriteSheet sheet = FesodSheet.writerSheet().build();
            writer.fill(
                    Arrays.asList(
                            Collections.singletonMap("name", "first"), Collections.singletonMap("name", "second")),
                    config,
                    sheet);
            writer.fill(Collections.singletonList(Collections.singletonMap("name", "third")), config, sheet);
        }
        try (Workbook workbook = WorkbookFactory.create(output)) {
            Sheet sheet = workbook.getSheetAt(0);
            Assertions.assertEquals("yes", sheet.getRow(6).getCell(4).getStringCellValue());
            Assertions.assertEquals("no", sheet.getRow(7).getCell(4).getStringCellValue());
            List<? extends DataValidation> validations = sheet.getDataValidations();
            assertRange(validations.get(0), 6, 6);
            assertFormula(validations.get(0), "$E$7:$E$8", null);
            assertFormula(validations.get(1), "$F$7", "$F$8");
            assertRange(validations.get(2), 0, 0);
            assertFormula(validations.get(2), "F7<F8", null);
            Assertions.assertArrayEquals(
                    new String[] {"E5", "F6"},
                    validations.get(3).getValidationConstraint().getExplicitListValues());
            assertFormula(validations.get(4), "1", "9");
            assertFormula(validations.get(5), "Choices", null);
            Assertions.assertEquals(
                    "Data!$E$7:$E$8", workbook.getName("Choices").getRefersToFormula());
            assertFormula(validations.get(6), "Other!$E$5:$E$6", null);
            DataValidation otherSheetValidation =
                    workbook.getSheetAt(1).getDataValidations().get(0);
            assertRange(otherSheetValidation, 4, 4);
            assertFormula(otherSheetValidation, "Data!$E$7:$E$8", null);
        }
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void keepsXlsxValidationFormulasWhenFillDoesNotInsertRows(boolean inMemory) throws Exception {
        File template = formulaTemplate("$E$5:$E$6");
        File output = createTempFile("validation-formulas-no-shift", ExcelFormat.XLSX);
        FesodSheet.write(output)
                .withTemplate(template)
                .inMemory(inMemory)
                .sheet()
                .doFill(
                        Collections.singletonList(Collections.singletonMap("name", "first")),
                        FillConfig.builder().forceNewRow(true).build());
        try (Workbook workbook = WorkbookFactory.create(output)) {
            List<? extends DataValidation> validations = workbook.getSheetAt(0).getDataValidations();
            assertRange(validations.get(0), 4, 4);
            assertFormula(validations.get(0), "$E$5:$E$6", null);
            assertFormula(validations.get(1), "$F$5", "$F$6");
            assertFormula(validations.get(2), "F5<F6", null);
            Assertions.assertEquals(
                    "Data!$E$5:$E$6", workbook.getName("Choices").getRefersToFormula());
            assertFormula(workbook.getSheetAt(1).getDataValidations().get(0), "Data!$E$5:$E$6", null);
        }
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void preservesXlsxValidationFormulasUsingUnsupportedExcelSyntax(boolean inMemory) throws Exception {
        File template = formulaTemplate("$E$5#");
        File output = createTempFile("validation-unsupported-formula", ExcelFormat.XLSX);
        FesodSheet.write(output)
                .withTemplate(template)
                .inMemory(inMemory)
                .sheet()
                .doFill(
                        Arrays.asList(
                                Collections.singletonMap("name", "first"), Collections.singletonMap("name", "second")),
                        FillConfig.builder().forceNewRow(true).build());
        try (Workbook workbook = WorkbookFactory.create(output)) {
            DataValidation validation =
                    workbook.getSheetAt(0).getDataValidations().get(0);
            assertRange(validation, 5, 5);
            assertFormula(validation, "$E$5#", null);
        }
    }

    private File formulaTemplate(String listFormula) throws Exception {
        File file = createTempFile("validation-formula-template", ExcelFormat.XLSX);
        try (Workbook workbook = new XSSFWorkbook();
                FileOutputStream output = new FileOutputStream(file)) {
            Sheet sheet = workbook.createSheet("Data");
            sheet.createRow(1).createCell(0).setCellValue("{.name}");
            sheet.createRow(4).createCell(4).setCellValue("yes");
            sheet.createRow(5).createCell(4).setCellValue("no");
            sheet.getRow(4).createCell(5).setCellValue(1);
            sheet.getRow(5).createCell(5).setCellValue(9);
            Sheet other = workbook.createSheet("Other");
            other.createRow(4).createCell(4).setCellValue("other yes");
            other.createRow(5).createCell(4).setCellValue("other no");
            Name choices = workbook.createName();
            choices.setNameName("Choices");
            choices.setRefersToFormula("Data!$E$5:$E$6");
            DataValidationHelper helper = sheet.getDataValidationHelper();
            addValidation(sheet, 4, 1, helper.createFormulaListConstraint(listFormula));
            addValidation(
                    sheet,
                    4,
                    2,
                    helper.createDecimalConstraint(DataValidationConstraint.OperatorType.BETWEEN, "$F$5", "$F$6"));
            addValidation(sheet, 0, 3, helper.createCustomConstraint("F5<F6"));
            addValidation(sheet, 4, 3, helper.createExplicitListConstraint(new String[] {"E5", "F6"}));
            addValidation(
                    sheet,
                    4,
                    6,
                    helper.createDecimalConstraint(DataValidationConstraint.OperatorType.BETWEEN, "1", "9"));
            addValidation(sheet, 4, 7, helper.createFormulaListConstraint("Choices"));
            addValidation(sheet, 4, 8, helper.createFormulaListConstraint("Other!$E$5:$E$6"));
            addValidation(other, 4, 1, other.getDataValidationHelper().createFormulaListConstraint("Data!$E$5:$E$6"));
            workbook.write(output);
        }
        return file;
    }

    private static void addValidation(Sheet sheet, int row, int column, DataValidationConstraint constraint) {
        sheet.addValidationData(sheet.getDataValidationHelper()
                .createValidation(constraint, new CellRangeAddressList(row, row, column, column)));
    }

    private static void assertFormula(DataValidation validation, String formula1, String formula2) {
        Assertions.assertEquals(formula1, validation.getValidationConstraint().getFormula1());
        Assertions.assertEquals(formula2, validation.getValidationConstraint().getFormula2());
    }

    private File template(ExcelFormat format) throws Exception {
        File file = createTempFile("validation-template", format);
        try (Workbook workbook = format == ExcelFormat.XLS ? new HSSFWorkbook() : new XSSFWorkbook();
                FileOutputStream output = new FileOutputStream(file)) {
            Sheet sheet = workbook.createSheet();
            sheet.createRow(1).createCell(0).setCellValue("{.name}");
            sheet.createRow(4).createCell(0).setCellValue("footer");
            addDropdown(sheet, 0, 0, 1);
            addDropdown(sheet, 4, 5, 2);
            addDropdown(sheet, 0, 5, 3);
            addDropdown(sheet, 0, workbook.getSpreadsheetVersion().getLastRowIndex(), 4);
            workbook.write(output);
        }
        return file;
    }

    private static void addDropdown(Sheet sheet, int firstRow, int lastRow, int column) {
        DataValidationHelper helper = sheet.getDataValidationHelper();
        DataValidation validation = helper.createValidation(
                helper.createExplicitListConstraint(new String[] {"yes", "no"}),
                new CellRangeAddressList(firstRow, lastRow, column, column));
        validation.createPromptBox("Choose", "Pick a value");
        validation.createErrorBox("Invalid", "Use yes or no");
        validation.setShowPromptBox(true);
        validation.setShowErrorBox(true);
        validation.setEmptyCellAllowed(false);
        sheet.addValidationData(validation);
    }

    private static void assertRange(DataValidation validation, int firstRow, int lastRow) {
        CellRangeAddress range = validation.getRegions().getCellRangeAddress(0);
        Assertions.assertEquals(firstRow, range.getFirstRow());
        Assertions.assertEquals(lastRow, range.getLastRow());
    }
}
