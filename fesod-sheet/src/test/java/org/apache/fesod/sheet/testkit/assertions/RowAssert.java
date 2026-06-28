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

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;

/**
 * Fluent assertions for a {@link Row}.
 */
public class RowAssert {

    private final Row row;
    private final Workbook workbook;
    private final SheetAssert parent;

    RowAssert(Row row, Workbook workbook, SheetAssert parent) {
        this.row = row;
        this.workbook = workbook;
        this.parent = parent;
    }

    /**
     * Asserts the row has the expected height in twips.
     */
    public RowAssert hasHeight(short expectedHeight) {
        short actual = row.getHeight();
        if (actual != expectedHeight) {
            throw new AssertionError("Expected row height " + expectedHeight + " but was " + actual);
        }
        return this;
    }

    /**
     * Navigates to a cell by index for further assertions.
     *
     * @throws AssertionError if the cell is null
     */
    public CellAssert cell(int index) {
        Cell cell = row.getCell(index);
        if (cell == null) {
            throw new AssertionError("Cell " + index + " is null in row " + row.getRowNum());
        }
        return new CellAssert(cell, workbook, this);
    }

    /**
     * Returns to the parent assertion chain.
     */
    public SheetAssert and() {
        return parent;
    }
}
