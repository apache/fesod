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

import java.util.Arrays;
import java.util.function.Consumer;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Color;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFColor;

/**
 * Fluent assertions for a {@link Cell}, including style assertions that absorb
 * the logic from {@code StyleTestUtils}.
 */
public class CellAssert {

    private final Cell cell;
    private final Workbook workbook;
    private final RowAssert parent;

    CellAssert(Cell cell, Workbook workbook, RowAssert parent) {
        this.cell = cell;
        this.workbook = workbook;
        this.parent = parent;
    }

    /**
     * Asserts the cell has the expected string value.
     */
    public CellAssert hasStringValue(String expected) {
        String actual = cell.getStringCellValue();
        if (!expected.equals(actual)) {
            throw new AssertionError("Expected string value \"" + expected + "\" but was \"" + actual + "\"");
        }
        return this;
    }

    /**
     * Asserts the cell has the expected numeric value.
     */
    public CellAssert hasNumericValue(double expected) {
        double actual = cell.getNumericCellValue();
        if (Double.compare(expected, actual) != 0) {
            throw new AssertionError("Expected numeric value " + expected + " but was " + actual);
        }
        return this;
    }

    /**
     * Asserts the cell has the expected data format index.
     */
    public CellAssert hasDataFormat(int expected) {
        int actual = cell.getCellStyle().getDataFormat();
        if (actual != expected) {
            throw new AssertionError("Expected data format " + expected + " but was " + actual);
        }
        return this;
    }

    /**
     * Asserts the cell has the expected data format string.
     */
    public CellAssert hasDataFormatString(String expected) {
        String actual = cell.getCellStyle().getDataFormatString();
        if (!expected.equals(actual)) {
            throw new AssertionError("Expected data format string \"" + expected + "\" but was \"" + actual + "\"");
        }
        return this;
    }

    /**
     * Asserts the cell has the expected boolean value.
     */
    public CellAssert hasBooleanValue(boolean expected) {
        boolean actual = cell.getBooleanCellValue();
        if (expected != actual) {
            throw new AssertionError("Expected boolean value " + expected + " but was " + actual);
        }
        return this;
    }

    /**
     * Asserts the cell's fill foreground color matches the expected RGB bytes.
     * Dispatches on XSSF vs HSSF cell types.
     */
    public CellAssert hasFillColor(byte[] expectedRgb) {
        byte[] actual;
        if (cell instanceof XSSFCell) {
            Color color = ((XSSFCell) cell).getCellStyle().getFillForegroundColorColor();
            if (color == null) {
                throw new AssertionError(
                        "Expected fill color " + Arrays.toString(expectedRgb) + " but no fill color was set");
            }
            actual = ((XSSFColor) color).getRGB();
        } else {
            HSSFColor color = ((HSSFCell) cell).getCellStyle().getFillForegroundColorColor();
            if (color == null) {
                throw new AssertionError(
                        "Expected fill color " + Arrays.toString(expectedRgb) + " but no fill color was set");
            }
            actual = short2byte(color.getTriplet());
        }
        if (!Arrays.equals(expectedRgb, actual)) {
            throw new AssertionError(
                    "Expected fill color " + Arrays.toString(expectedRgb) + " but was " + Arrays.toString(actual));
        }
        return this;
    }

    /**
     * Asserts the cell's font color matches the expected RGB bytes.
     * Dispatches on XSSF vs HSSF cell types.
     */
    public CellAssert hasFontColor(byte[] expectedRgb) {
        byte[] actual;
        if (cell instanceof XSSFCell) {
            XSSFColor color = ((XSSFCell) cell).getCellStyle().getFont().getXSSFColor();
            if (color == null) {
                throw new AssertionError(
                        "Expected font color " + Arrays.toString(expectedRgb) + " but no font color was set");
            }
            actual = color.getRGB();
        } else {
            HSSFColor color = ((HSSFCell) cell).getCellStyle().getFont(workbook).getHSSFColor((HSSFWorkbook) workbook);
            if (color == null) {
                throw new AssertionError(
                        "Expected font color " + Arrays.toString(expectedRgb) + " but no font color was set");
            }
            actual = short2byte(color.getTriplet());
        }
        if (!Arrays.equals(expectedRgb, actual)) {
            throw new AssertionError(
                    "Expected font color " + Arrays.toString(expectedRgb) + " but was " + Arrays.toString(actual));
        }
        return this;
    }

    /**
     * Asserts the cell's font size matches the expected points.
     */
    public CellAssert hasFontSize(short expectedPoints) {
        short actual;
        if (cell instanceof XSSFCell) {
            actual = ((XSSFCell) cell).getCellStyle().getFont().getFontHeightInPoints();
        } else {
            actual = ((HSSFCell) cell).getCellStyle().getFont(workbook).getFontHeightInPoints();
        }
        if (actual != expectedPoints) {
            throw new AssertionError("Expected font size " + expectedPoints + " but was " + actual);
        }
        return this;
    }

    /**
     * Asserts whether the cell font is bold.
     */
    public CellAssert hasBoldFont(boolean expected) {
        boolean actual;
        if (cell instanceof XSSFCell) {
            actual = ((XSSFCell) cell).getCellStyle().getFont().getBold();
        } else {
            actual = ((HSSFCell) cell).getCellStyle().getFont(workbook).getBold();
        }
        if (actual != expected) {
            throw new AssertionError("Expected bold font " + expected + " but was " + actual);
        }
        return this;
    }

    /**
     * Escape hatch for custom POI assertions on the underlying cell.
     */
    public CellAssert satisfies(Consumer<Cell> assertion) {
        assertion.accept(cell);
        return this;
    }

    /**
     * Returns to the parent assertion chain.
     */
    public RowAssert and() {
        return parent;
    }

    private static byte[] short2byte(short[] shorts) {
        byte[] bytes = new byte[shorts.length];
        for (int i = 0; i < shorts.length; i++) {
            bytes[i] = (byte) shorts[i];
        }
        return bytes;
    }
}
