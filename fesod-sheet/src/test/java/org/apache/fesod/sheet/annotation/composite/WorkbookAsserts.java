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

package org.apache.fesod.sheet.annotation.composite;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;
import java.io.File;
import java.util.List;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.CellRangeAddress;

/**
 * Compares two Excel workbooks cell-by-cell for integration testing of composable annotations.
 * <p>
 * Verifies: cell values, cell styles (alignment, fill, border, data format),
 * fonts (name, height, bold, italic, color, etc.), column widths, row heights,
 * and merged regions.
 */
class WorkbookAsserts {

    static void assertWorkbooksMatch(File expected, File actual) {
        try (Workbook wb1 = WorkbookFactory.create(expected);
                Workbook wb2 = WorkbookFactory.create(actual)) {
            assertWorkbooksMatch(wb1, wb2);
        } catch (AssertionError e) {
            throw e;
        } catch (Exception e) {
            fail("Failed to read workbooks: " + e.getMessage(), e);
        }
    }

    static void assertWorkbooksMatch(Workbook expected, Workbook actual) {
        assertEquals(expected.getNumberOfSheets(), actual.getNumberOfSheets(), "Sheet count mismatch");
        for (int i = 0; i < expected.getNumberOfSheets(); i++) {
            assertSheetMatches(expected.getSheetAt(i), actual.getSheetAt(i), i);
        }
    }

    // ---- Sheet ----

    private static void assertSheetMatches(Sheet expected, Sheet actual, int sheetIdx) {
        String ctx = "Sheet[" + sheetIdx + "]";

        // Merged regions
        List<CellRangeAddress> expMerged = expected.getMergedRegions();
        List<CellRangeAddress> actMerged = actual.getMergedRegions();
        assertEquals(expMerged.size(), actMerged.size(), ctx + " merged region count");
        for (int i = 0; i < expMerged.size(); i++) {
            assertEquals(
                    expMerged.get(i).formatAsString(),
                    actMerged.get(i).formatAsString(),
                    ctx + " merged region[" + i + "]");
        }

        // Rows
        int lastRow = Math.max(expected.getLastRowNum(), actual.getLastRowNum());
        for (int r = 0; r <= lastRow; r++) {
            assertRowMatches(expected.getRow(r), actual.getRow(r), sheetIdx, r);
        }

        // Column widths
        int maxCol = Math.max(maxColumnIndex(expected), maxColumnIndex(actual));
        for (int c = 0; c <= maxCol; c++) {
            assertEquals(expected.getColumnWidth(c), actual.getColumnWidth(c), ctx + " column[" + c + "] width");
        }
    }

    // ---- Row ----

    private static void assertRowMatches(Row expected, Row actual, int sheetIdx, int rowIdx) {
        String ctx = "Sheet[" + sheetIdx + "].Row[" + rowIdx + "]";
        if (expected == null && actual == null) {
            return;
        }
        assertNotNull(expected, ctx + " missing in expected");
        assertNotNull(actual, ctx + " missing in actual");

        assertEquals(expected.getHeightInPoints(), actual.getHeightInPoints(), ctx + " heightInPoints");

        short maxCell = (short) Math.max(
                expected.getLastCellNum() < 0 ? 0 : expected.getLastCellNum(),
                actual.getLastCellNum() < 0 ? 0 : actual.getLastCellNum());
        for (int c = 0; c < maxCell; c++) {
            assertCellMatches(
                    expected.getCell(c),
                    actual.getCell(c),
                    sheetIdx,
                    rowIdx,
                    c,
                    expected.getSheet().getWorkbook(),
                    actual.getSheet().getWorkbook());
        }
    }

    // ---- Cell ----

    private static void assertCellMatches(
            Cell expected, Cell actual, int sheetIdx, int rowIdx, int colIdx, Workbook expWb, Workbook actWb) {
        String ctx = "Cell[" + rowIdx + "," + colIdx + "]";
        if (expected == null && actual == null) {
            return;
        }
        assertNotNull(expected, ctx + " missing in expected");
        assertNotNull(actual, ctx + " missing in actual");

        // Type
        assertEquals(expected.getCellType(), actual.getCellType(), ctx + " type");

        // Value
        switch (expected.getCellType()) {
            case STRING:
                assertEquals(expected.getStringCellValue(), actual.getStringCellValue(), ctx + " value");
                break;
            case NUMERIC:
                assertEquals(expected.getNumericCellValue(), actual.getNumericCellValue(), ctx + " value");
                break;
            case BOOLEAN:
                assertEquals(expected.getBooleanCellValue(), actual.getBooleanCellValue(), ctx + " value");
                break;
            case FORMULA:
                assertEquals(expected.getCellFormula(), actual.getCellFormula(), ctx + " formula");
                break;
            default:
                break;
        }

        // Style
        assertStyleMatches(expected.getCellStyle(), actual.getCellStyle(), expWb, actWb, ctx);
    }

    // ---- CellStyle ----

    private static void assertStyleMatches(
            CellStyle expected, CellStyle actual, Workbook expWb, Workbook actWb, String ctx) {
        // Alignment
        assertEquals(expected.getAlignment(), actual.getAlignment(), ctx + " alignment");
        assertEquals(expected.getVerticalAlignment(), actual.getVerticalAlignment(), ctx + " verticalAlignment");
        assertEquals(expected.getWrapText(), actual.getWrapText(), ctx + " wrapText");
        assertEquals(expected.getRotation(), actual.getRotation(), ctx + " rotation");
        assertEquals(expected.getIndention(), actual.getIndention(), ctx + " indent");

        // Visibility / protection
        assertEquals(expected.getHidden(), actual.getHidden(), ctx + " hidden");
        assertEquals(expected.getLocked(), actual.getLocked(), ctx + " locked");
        assertEquals(expected.getShrinkToFit(), actual.getShrinkToFit(), ctx + " shrinkToFit");

        // Borders
        assertEquals(expected.getBorderLeft(), actual.getBorderLeft(), ctx + " borderLeft");
        assertEquals(expected.getBorderRight(), actual.getBorderRight(), ctx + " borderRight");
        assertEquals(expected.getBorderTop(), actual.getBorderTop(), ctx + " borderTop");
        assertEquals(expected.getBorderBottom(), actual.getBorderBottom(), ctx + " borderBottom");
        assertEquals(expected.getLeftBorderColor(), actual.getLeftBorderColor(), ctx + " leftBorderColor");
        assertEquals(expected.getRightBorderColor(), actual.getRightBorderColor(), ctx + " rightBorderColor");
        assertEquals(expected.getTopBorderColor(), actual.getTopBorderColor(), ctx + " topBorderColor");
        assertEquals(expected.getBottomBorderColor(), actual.getBottomBorderColor(), ctx + " bottomBorderColor");

        // Fill
        assertEquals(expected.getFillPattern(), actual.getFillPattern(), ctx + " fillPattern");
        assertEquals(expected.getFillForegroundColor(), actual.getFillForegroundColor(), ctx + " fillForegroundColor");
        assertEquals(expected.getFillBackgroundColor(), actual.getFillBackgroundColor(), ctx + " fillBackgroundColor");

        // Data format
        assertEquals(expected.getDataFormat(), actual.getDataFormat(), ctx + " dataFormat");

        // Font
        assertFontMatches(expWb.getFontAt(expected.getFontIndex()), actWb.getFontAt(actual.getFontIndex()), ctx);
    }

    // ---- Font ----

    private static void assertFontMatches(Font expected, Font actual, String ctx) {
        assertEquals(expected.getFontName(), actual.getFontName(), ctx + " fontName");
        assertEquals(expected.getFontHeightInPoints(), actual.getFontHeightInPoints(), ctx + " fontHeightInPoints");
        assertEquals(expected.getBold(), actual.getBold(), ctx + " bold");
        assertEquals(expected.getItalic(), actual.getItalic(), ctx + " italic");
        assertEquals(expected.getColor(), actual.getColor(), ctx + " color");
        assertEquals(expected.getUnderline(), actual.getUnderline(), ctx + " underline");
        assertEquals(expected.getStrikeout(), actual.getStrikeout(), ctx + " strikeout");
        assertEquals(expected.getTypeOffset(), actual.getTypeOffset(), ctx + " typeOffset");
    }

    // ---- Helpers ----

    private static int maxColumnIndex(Sheet sheet) {
        int max = 0;
        for (Row row : sheet) {
            if (row.getLastCellNum() > max) {
                max = row.getLastCellNum();
            }
        }
        return max;
    }
}
