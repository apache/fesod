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

package org.apache.fesod.sheet.write.handler;

import org.apache.fesod.sheet.enums.CellDataTypeEnum;
import org.apache.fesod.sheet.metadata.data.WriteCellData;
import org.apache.fesod.sheet.testkit.Tags;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;

@Tag(Tags.UNIT)
class EscapeHexCellWriteHandlerTest {

    private final EscapeHexCellWriteHandler handler = new EscapeHexCellWriteHandler();

    /**
     * The handler only checks that the cell is an {@link SXSSFCell} and never reads from it, so a mock is all it
     * needs.
     */
    private final SXSSFCell cell = Mockito.mock(SXSSFCell.class);

    /**
     * Runs the handler over a string cell and returns the value it left behind.
     */
    private String escape(String input) {
        WriteCellData<?> cellData = new WriteCellData<>(input);
        handler.afterCellDataConverted(null, null, cellData, cell, null, 0, Boolean.FALSE);
        return cellData.getStringValue();
    }

    @ParameterizedTest(name = "[{index}] {0} -> {1}")
    @CsvSource(
            delimiter = '|',
            value = {
                "_xB9f0_|_x005F_xB9f0_",
                "abc_x0041_|abc_x005F_x0041_",
                "_x0041__x0042_|_x005F_x0041__x005F_x0042_",
                "_xB9f0_ and _x1234_ and _xABCD_|_x005F_xB9f0_ and _x005F_x1234_ and _x005F_xABCD_",
                // 3 below check for partially valid cases - 1st format is valid, 2nd is invalid.
                "_x1234_ _xGHIJ_|_x005F_x1234_ _xGHIJ_",
                "_x0041__x12|_x005F_x0041__x12",
                "_x0041__x12345|_x005F_x0041__x12345",
            })
    void afterCellDataConverted_escapesEveryValidHexPattern(String input, String expected) {
        Assertions.assertEquals(expected, escape(input));
    }

    @ParameterizedTest(name = "[{index}] {0} is left alone")
    @ValueSource(
            strings = {
                "normalString",
                "_x12345_", // seventh character is not underscore
                "_x0041", // one character short of a complete pattern
                "_x00G1_", // a non-hex character
                "_x_x0041", // an unterminated pattern
                "", // empty input must not trip the scan
                "_x00é1_", // a non-ASCII character
                "_X1234_", // uppercase X
            })
    void afterCellDataConverted_leavesInvalidPatternsUntouched(String input) {
        Assertions.assertEquals(input, escape(input));
    }

    /**
     * Escaping is not idempotent: an already-escaped literal is escaped again
     */
    @Test
    void afterCellDataConverted_escapesAnAlreadyEscapedSequenceAgain() {
        Assertions.assertEquals("_x005F_x005F_x0041_", escape("_x005F_x0041_"));
    }

    @Test
    void afterCellDataConverted_ignoresNonStringCellData() {
        WriteCellData<?> cellData = new WriteCellData<>(CellDataTypeEnum.ERROR, "_x0041_");

        handler.afterCellDataConverted(null, null, cellData, cell, null, 0, Boolean.FALSE);

        Assertions.assertEquals("_x0041_", cellData.getStringValue());
    }

    @Test
    void afterCellDataConverted_ignoresNonSxssfCells() {
        WriteCellData<?> cellData = new WriteCellData<>("_x0041_");

        handler.afterCellDataConverted(null, null, cellData, Mockito.mock(HSSFCell.class), null, 0, Boolean.FALSE);

        Assertions.assertEquals("_x0041_", cellData.getStringValue());
    }

    @Test
    void afterCellDataConverted_toleratesNullCellDataAndNullStringValue() {
        WriteCellData<?> emptyStringData = new WriteCellData<>(CellDataTypeEnum.STRING);

        Assertions.assertDoesNotThrow(
                () -> handler.afterCellDataConverted(null, null, null, cell, null, 0, Boolean.FALSE));
        Assertions.assertDoesNotThrow(
                () -> handler.afterCellDataConverted(null, null, emptyStringData, cell, null, 0, Boolean.FALSE));
        Assertions.assertNull(emptyStringData.getStringValue());
    }
}
