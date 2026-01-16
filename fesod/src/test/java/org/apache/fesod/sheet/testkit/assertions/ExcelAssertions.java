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

package org.apache.fesod.sheet.testkit.assertions;

import java.io.File;
import java.io.IOException;
import org.apache.poi.ss.usermodel.*;
import org.junit.jupiter.api.Assertions;

/**
 * Main entry point for Excel-specific assertions.
 * Provides a fluent API for testing Excel file contents.
 *
 * <p>Usage:</p>
 * <pre>{@code
 * ExcelAssertions.assertThat(file)
 *     .sheet(0)
 *     .hasRowCount(10)
 *     .cell(0, 0)
 *     .hasStringValue("Header");
 * }</pre>
 */
public class ExcelAssertions {

    /**
     * Entry point for workbook assertions from a file.
     */
    public static WorkbookAssert assertThat(File file) {
        try {
            Workbook workbook = WorkbookFactory.create(file);
            return new WorkbookAssert(workbook, true);
        } catch (IOException e) {
            throw new AssertionError("Failed to open workbook: " + file, e);
        }
    }

    /**
     * Entry point for workbook assertions from a Workbook object.
     */
    public static WorkbookAssert assertThat(Workbook workbook) {
        return new WorkbookAssert(workbook, false);
    }

    /**
     * Workbook-level assertions.
     */
    public static class WorkbookAssert implements AutoCloseable {
        protected final Workbook workbook;
        private final boolean shouldClose;

        public WorkbookAssert(Workbook workbook, boolean shouldClose) {
            this.workbook = workbook;
            this.shouldClose = shouldClose;
        }

        public WorkbookAssert hasSheetCount(int expected) {
            Assertions.assertEquals(
                    expected,
                    workbook.getNumberOfSheets(),
                    "Expected sheet count " + expected + " but was " + workbook.getNumberOfSheets());
            return this;
        }

        public WorkbookAssert hasSheetNamed(String name) {
            Sheet sheet = workbook.getSheet(name);
            Assertions.assertNotNull(sheet, "Sheet named '" + name + "' not found");
            return this;
        }

        public SheetAssert sheet(int index) {
            Sheet sheet = workbook.getSheetAt(index);
            Assertions.assertNotNull(sheet, "Sheet at index " + index + " is null");
            return new SheetAssert(sheet, workbook);
        }

        public SheetAssert sheet(String name) {
            Sheet sheet = workbook.getSheet(name);
            Assertions.assertNotNull(sheet, "Sheet named '" + name + "' not found");
            return new SheetAssert(sheet, workbook);
        }

        @Override
        public void close() throws Exception {
            if (shouldClose && workbook != null) {
                workbook.close();
            }
        }
    }

    /**
     * Sheet-level assertions.
     */
    public static class SheetAssert {
        protected final Sheet sheet;
        protected final Workbook workbook;

        public SheetAssert(Sheet sheet, Workbook workbook) {
            this.sheet = sheet;
            this.workbook = workbook;
        }

        public SheetAssert hasRowCount(int expected) {
            int actual = sheet.getPhysicalNumberOfRows();
            Assertions.assertEquals(expected, actual, "Expected row count " + expected + " but was " + actual);
            return this;
        }

        public SheetAssert hasColumnCount(int expected, int rowIndex) {
            Row row = sheet.getRow(rowIndex);
            if (row == null) {
                Assertions.fail("Row at index " + rowIndex + " does not exist");
            }
            int actual = row.getPhysicalNumberOfCells();
            Assertions.assertEquals(
                    expected,
                    actual,
                    "Expected column count " + expected + " at row " + rowIndex + " but was " + actual);
            return this;
        }

        public SheetAssert hasColumnWidth(int col, int expectedWidth) {
            int actualWidth = sheet.getColumnWidth(col);
            Assertions.assertEquals(
                    expectedWidth,
                    actualWidth,
                    "Expected column width " + expectedWidth + " for column " + col + " but was " + actualWidth);
            return this;
        }

        public CellAssert cell(int row, int col) {
            Row sheetRow = sheet.getRow(row);
            if (sheetRow == null) {
                Assertions.fail("Row at index " + row + " does not exist");
            }
            Cell cell = sheetRow.getCell(col);
            if (cell == null) {
                Assertions.fail("Cell at row " + row + ", column " + col + " does not exist");
            }
            return new CellAssert(cell, workbook);
        }

        public SheetAssert hasCellAt(int row, int col) {
            Row sheetRow = sheet.getRow(row);
            Assertions.assertNotNull(sheetRow, "Row at index " + row + " does not exist");
            Cell cell = sheetRow.getCell(col);
            Assertions.assertNotNull(cell, "Cell at row " + row + ", column " + col + " does not exist");
            return this;
        }
    }

    /**
     * Cell-level assertions.
     */
    public static class CellAssert {
        private final Cell cell;
        private final Workbook workbook;

        public CellAssert(Cell cell, Workbook workbook) {
            this.cell = cell;
            this.workbook = workbook;
        }

        public CellAssert hasStringValue(String expected) {
            String actual = getCellValueAsString();
            Assertions.assertEquals(
                    expected, actual, "Expected cell value '" + expected + "' but was '" + actual + "'");
            return this;
        }

        public CellAssert hasNumericValue(double expected) {
            Assertions.assertEquals(
                    CellType.NUMERIC, cell.getCellType(), "Cell is not numeric, got: " + cell.getCellType());
            double actual = cell.getNumericCellValue();
            Assertions.assertEquals(
                    expected, actual, 0.001, "Expected numeric value " + expected + " but was " + actual);
            return this;
        }

        public CellAssert hasBooleanValue(boolean expected) {
            Assertions.assertEquals(
                    CellType.BOOLEAN, cell.getCellType(), "Cell is not boolean, got: " + cell.getCellType());
            boolean actual = cell.getBooleanCellValue();
            Assertions.assertEquals(expected, actual, "Expected boolean value " + expected + " but was " + actual);
            return this;
        }

        public CellAssert hasFormulaValue() {
            Assertions.assertEquals(
                    CellType.FORMULA, cell.getCellType(), "Cell is not a formula, got: " + cell.getCellType());
            return this;
        }

        public CellAssert isEmpty() {
            CellType type = cell.getCellType();
            Assertions.assertTrue(
                    type == CellType.BLANK
                            || (type == CellType.STRING
                                    && cell.getStringCellValue().trim().isEmpty()),
                    "Expected cell to be empty, but was: " + getCellValueAsString());
            return this;
        }

        public CellAssert hasType(CellType expected) {
            CellType actual = cell.getCellType();
            Assertions.assertEquals(expected, actual, "Expected cell type " + expected + " but was " + actual);
            return this;
        }

        public CellAssert hasFontColor(short expectedColorIndex) {
            Font font = workbook.getFontAt(cell.getCellStyle().getFontIndex());
            Assertions.assertEquals(
                    expectedColorIndex,
                    font.getColor(),
                    "Expected font color index " + expectedColorIndex + " but was " + font.getColor());
            return this;
        }

        public CellAssert hasFillColor(short expectedColorIndex) {
            Assertions.assertEquals(
                    expectedColorIndex,
                    cell.getCellStyle().getFillForegroundColor(),
                    "Expected fill color index " + expectedColorIndex + " but was "
                            + cell.getCellStyle().getFillForegroundColor());
            return this;
        }

        private String getCellValueAsString() {
            switch (cell.getCellType()) {
                case STRING:
                    return cell.getStringCellValue();
                case NUMERIC:
                    if (DateUtil.isCellDateFormatted(cell)) {
                        return cell.getDateCellValue().toString();
                    } else {
                        double value = cell.getNumericCellValue();
                        // Convert to integer if it's a whole number
                        if (value == Math.floor(value)) {
                            return String.valueOf((long) value);
                        } else {
                            return String.valueOf(value);
                        }
                    }
                case BOOLEAN:
                    return String.valueOf(cell.getBooleanCellValue());
                case FORMULA:
                    return cell.getCellFormula();
                case BLANK:
                    return "";
                default:
                    return cell.getStringCellValue();
            }
        }
    }
}
