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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.fesod.sheet.FesodSheet;
import org.apache.fesod.sheet.enums.CellDataTypeEnum;
import org.apache.fesod.sheet.metadata.data.WriteCellData;
import org.apache.fesod.sheet.testkit.Tags;
import org.apache.fesod.sheet.testkit.enums.ExcelFormat;
import org.apache.fesod.sheet.testkit.params.ExcelFormatSource;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

@Tag(Tags.UNIT)
class EscapeHexCellWriteHandlerTest {

    @TempDir
    File tempDir;

    private final EscapeHexCellWriteHandler handler = new EscapeHexCellWriteHandler();

    /**
     * Runs the handler over a string cell and returns the value it left behind.
     */
    private String escape(String input) throws IOException {
        try (SXSSFWorkbook workbook = new SXSSFWorkbook()) {
            SXSSFCell cell = workbook.createSheet().createRow(0).createCell(0);
            WriteCellData<?> cellData = new WriteCellData<>(input);
            handler.afterCellDataConverted(null, null, cellData, cell, null, 0, Boolean.FALSE);
            return cellData.getStringValue();
        }
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
    void afterCellDataConverted_escapesEveryValidHexPattern(String input, String expected) throws IOException {
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
    void afterCellDataConverted_leavesInvalidPatternsUntouched(String input) throws IOException {
        Assertions.assertEquals(input, escape(input));
    }

    /**
     * Escaping is not idempotent: an already-escaped literal is escaped again
     */
    @Test
    void afterCellDataConverted_escapesAnAlreadyEscapedSequenceAgain() throws IOException {
        Assertions.assertEquals("_x005F_x005F_x0041_", escape("_x005F_x0041_"));
    }

    @Test
    void afterCellDataConverted_ignoresNonStringCellData() throws IOException {
        try (SXSSFWorkbook workbook = new SXSSFWorkbook()) {
            SXSSFCell cell = workbook.createSheet().createRow(0).createCell(0);
            WriteCellData<?> cellData = new WriteCellData<>(CellDataTypeEnum.ERROR, "_x0041_");

            handler.afterCellDataConverted(null, null, cellData, cell, null, 0, Boolean.FALSE);

            Assertions.assertEquals("_x0041_", cellData.getStringValue());
        }
    }

    @Test
    void afterCellDataConverted_toleratesNullCellDataAndNullStringValue() throws IOException {
        try (SXSSFWorkbook workbook = new SXSSFWorkbook()) {
            SXSSFCell cell = workbook.createSheet().createRow(0).createCell(0);
            WriteCellData<?> emptyStringData = new WriteCellData<>(CellDataTypeEnum.STRING);

            Assertions.assertDoesNotThrow(
                    () -> handler.afterCellDataConverted(null, null, null, cell, null, 0, Boolean.FALSE));
            Assertions.assertDoesNotThrow(
                    () -> handler.afterCellDataConverted(null, null, emptyStringData, cell, null, 0, Boolean.FALSE));
            Assertions.assertNull(emptyStringData.getStringValue());
        }
    }

    private File writeEscapedWorkbook(ExcelFormat format) throws IOException {
        File file = format.createTempFile("escape-hex", tempDir);
        List<List<String>> rows = new ArrayList<>();
        rows.add(Collections.singletonList("_xB9f0_ and _x1234_"));

        FesodSheet.write(file)
                .excelType(format.toExcelTypeEnum())
                .head(Collections.singletonList(Collections.singletonList("value")))
                .registerWriteHandler(new EscapeHexCellWriteHandler())
                .sheet("escape")
                .doWrite(rows);
        return file;
    }

    private String readBackFirstDataValue(File file, ExcelFormat format) throws IOException {
        if (format == ExcelFormat.CSV) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                reader.readLine(); // header
                return reader.readLine();
            }
        }
        try (Workbook workbook = WorkbookFactory.create(file)) {
            return workbook.getSheetAt(0).getRow(1).getCell(0).getStringCellValue();
        }
    }

    /**
     * Writes a file with the handler registered and reads it back: the caller must see the literal they typed.
     *
     * <p>All three formats expect the same value, for different reasons. On XLSX the handler escapes the sequence
     * and POI's reader decodes that escape away again. On XLS and CSV the handler never fires, since it only
     * touches {@link SXSSFCell}, so there was nothing to undo.
     */
    @ParameterizedTest(name = "[{index}] {0} round-trips the literal hex sequence")
    @ExcelFormatSource
    void registeredOnAWrite_keepsLiteralHexSequencesIntactAcrossFormats(ExcelFormat format) throws IOException {
        File file = writeEscapedWorkbook(format);
        Assertions.assertEquals("_xB9f0_ and _x1234_", readBackFirstDataValue(file, format));
    }
}
