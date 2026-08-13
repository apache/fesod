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

package org.apache.fesod.sheet.analysis.v03.handlers;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.fesod.sheet.FesodSheet;
import org.apache.fesod.sheet.enums.ReadDefaultReturnEnum;
import org.apache.fesod.sheet.testkit.Tags;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.FormulaError;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * A cell holding a literal error value (an error stored as the cell value rather than as a formula
 * result, e.g. after "Paste Special &rarr; Values") is written to a BIFF {@code BOOLERR} record, which
 * stores either a boolean or an error code. Reading such a cell must yield the error text, exactly as
 * the xlsx path already does, instead of a boolean derived from the error code.
 */
@Tag(Tags.READ)
class BoolErrRecordHandlerTest {

    private static final String MARKER = "marker";

    @Test
    void read_literalErrorCells_returnErrorText_inStringMode(@TempDir Path dir) throws IOException {
        Map<Integer, Object> xls = readFirstRow(writeXls(dir), ReadDefaultReturnEnum.STRING);
        Map<Integer, Object> xlsx = readFirstRow(writeXlsx(dir), ReadDefaultReturnEnum.STRING);

        Map<Integer, Object> expected = new LinkedHashMap<>();
        expected.put(0, "true");
        expected.put(1, "#DIV/0!");
        expected.put(2, "#N/A");
        expected.put(3, "#NULL!");
        expected.put(4, MARKER);

        // Before the fix the .xls row read back as {0=true, 1=true, 2=true, 3=false, 4=marker}: the
        // error code was taken as `code != 0`, so #NULL! (code 0) even came back as false.
        Assertions.assertEquals(expected, xls);
        Assertions.assertEquals(xlsx, xls, "the same content must read the same from .xls and .xlsx");
    }

    @Test
    void read_literalErrorCells_returnErrorText_inActualDataMode(@TempDir Path dir) throws IOException {
        Map<Integer, Object> xls = readFirstRow(writeXls(dir), ReadDefaultReturnEnum.ACTUAL_DATA);
        Map<Integer, Object> xlsx = readFirstRow(writeXlsx(dir), ReadDefaultReturnEnum.ACTUAL_DATA);

        // The boolean cell keeps its Boolean type; only the error cells change.
        Assertions.assertEquals(Boolean.TRUE, xls.get(0));
        Assertions.assertEquals("#DIV/0!", xls.get(1));
        Assertions.assertEquals("#N/A", xls.get(2));
        Assertions.assertEquals("#NULL!", xls.get(3));
        Assertions.assertEquals(MARKER, xls.get(4));
        Assertions.assertEquals(xlsx, xls, "the same content must read the same from .xls and .xlsx");
    }

    /** One row: a literal boolean, three literal error values, and a plain string. */
    private static void fillRow(Workbook workbook) {
        Row row = workbook.createSheet("sheet").createRow(0);
        row.createCell(0).setCellValue(true);
        row.createCell(1).setCellErrorValue(FormulaError.DIV0.getCode());
        row.createCell(2).setCellErrorValue(FormulaError.NA.getCode());
        row.createCell(3).setCellErrorValue(FormulaError.NULL.getCode());
        row.createCell(4).setCellValue(MARKER);
    }

    private static File writeXls(Path dir) throws IOException {
        return write(dir.resolve("literal-error.xls"), new HSSFWorkbook());
    }

    private static File writeXlsx(Path dir) throws IOException {
        return write(dir.resolve("literal-error.xlsx"), new XSSFWorkbook());
    }

    private static File write(Path path, Workbook workbook) throws IOException {
        try (Workbook closeable = workbook;
                OutputStream out = Files.newOutputStream(path)) {
            fillRow(closeable);
            closeable.write(out);
        }
        return path.toFile();
    }

    private static Map<Integer, Object> readFirstRow(File file, ReadDefaultReturnEnum readDefaultReturn) {
        List<Map<Integer, Object>> rows = FesodSheet.read(file)
                .readDefaultReturn(readDefaultReturn)
                .headRowNumber(0)
                .sheet(0)
                .doReadSync();
        return rows.get(0);
    }
}
