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
import org.apache.fesod.sheet.testkit.Tags;
import org.apache.fesod.sheet.testkit.base.AbstractExcelTest;
import org.apache.fesod.sheet.testkit.enums.ExcelFormat;
import org.apache.fesod.sheet.testkit.params.ExcelFormatSource;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;

@Tag(Tags.ROUND_TRIP)
class EscapeHexCellWriteHandlerRoundTripTest extends AbstractExcelTest {

    private File writeEscapedWorkbook(ExcelFormat format) throws IOException {
        File file = createTempFile("escape-hex", format);
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
