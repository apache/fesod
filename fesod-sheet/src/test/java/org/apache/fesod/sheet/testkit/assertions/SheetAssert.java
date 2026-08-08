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

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

/**
 * Fluent assertions for a {@link Sheet}.
 */
public class SheetAssert {

    private final Sheet sheet;
    private final Workbook workbook;
    private final WorkbookAssert parent;

    SheetAssert(Sheet sheet, Workbook workbook, WorkbookAssert parent) {
        this.sheet = sheet;
        this.workbook = workbook;
        this.parent = parent;
    }

    /**
     * Asserts the sheet has the expected number of physical rows.
     */
    public SheetAssert hasRowCount(int expected) {
        int actual = sheet.getPhysicalNumberOfRows();
        if (actual != expected) {
            throw new AssertionError(
                    "Expected " + expected + " rows but sheet '" + sheet.getSheetName() + "' has " + actual);
        }
        return this;
    }

    /**
     * Asserts a column has the expected width (in 1/256th of a character width units).
     */
    public SheetAssert hasColumnWidth(int columnIndex, int expectedWidth) {
        int actual = sheet.getColumnWidth(columnIndex);
        if (actual != expectedWidth) {
            throw new AssertionError(
                    "Expected column " + columnIndex + " width " + expectedWidth + " but was " + actual);
        }
        return this;
    }

    /**
     * Navigates to a row by index for further assertions.
     *
     * @throws AssertionError if the row is null
     */
    public RowAssert row(int index) {
        Row row = sheet.getRow(index);
        if (row == null) {
            throw new AssertionError("Row " + index + " is null in sheet '" + sheet.getSheetName() + "'");
        }
        return new RowAssert(row, workbook, this);
    }

    /**
     * Returns to the parent assertion chain.
     */
    public WorkbookAssert and() {
        return parent;
    }
}
