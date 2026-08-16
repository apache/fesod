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

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

/**
 * Fluent assertions for a {@link Workbook}.
 */
public class WorkbookAssert {

    private final Workbook workbook;
    private final ExcelAssertions parent;

    WorkbookAssert(Workbook workbook, ExcelAssertions parent) {
        this.workbook = workbook;
        this.parent = parent;
    }

    /**
     * Asserts the workbook has the expected number of sheets.
     */
    public WorkbookAssert hasSheetCount(int expected) {
        int actual = workbook.getNumberOfSheets();
        if (actual != expected) {
            throw new AssertionError("Expected " + expected + " sheets but was " + actual);
        }
        return this;
    }

    /**
     * Navigates to a sheet by index for further assertions.
     *
     * @throws AssertionError if the index is out of bounds
     */
    public SheetAssert sheet(int index) {
        int count = workbook.getNumberOfSheets();
        if (index < 0 || index >= count) {
            throw new AssertionError("Sheet index " + index + " out of bounds, workbook has " + count + " sheet(s)");
        }
        Sheet sheet = workbook.getSheetAt(index);
        return new SheetAssert(sheet, workbook, this);
    }

    /**
     * Returns to the parent assertion chain.
     */
    public ExcelAssertions and() {
        return parent;
    }
}
