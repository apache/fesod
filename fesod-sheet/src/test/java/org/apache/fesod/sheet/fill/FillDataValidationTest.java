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
import org.apache.poi.ss.usermodel.DataValidationHelper;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;

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
