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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.fesod.sheet.FesodSheet;
import org.apache.fesod.sheet.support.ExcelTypeEnum;
import org.apache.fesod.sheet.testkit.Tags;
import org.apache.fesod.sheet.testkit.base.AbstractExcelTest;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * The default XLSX write handler set includes {@code EscapeHexCellWriteHandler}, so literal {@code _xHHHH_}
 * sequences survive a round trip for streaming and in-memory writes alike. POI's reader decodes {@code _xHHHH_}
 * to the character it names, so an unescaped literal comes back corrupted.
 */
@Tag(Tags.ROUND_TRIP)
class EscapeHexInMemoryWriteTest extends AbstractExcelTest {

    private static final String VALUE = "_xB9f0_ and _x1234_";

    @Test
    void streamingWriteRoundTripsLiteralEscapeSequences() throws Exception {
        File file = writeWorkbook(Boolean.FALSE);

        Assertions.assertEquals(VALUE, readBackFirstDataValue(file));
    }

    @Test
    void inMemoryWriteRoundTripsLiteralEscapeSequences() throws Exception {
        File file = writeWorkbook(Boolean.TRUE);

        Assertions.assertEquals(VALUE, readBackFirstDataValue(file));
    }

    private File writeWorkbook(Boolean inMemory) throws IOException {
        File file = createTempFile("escape-hex-in-memory", org.apache.fesod.sheet.testkit.enums.ExcelFormat.XLSX);
        List<List<String>> rows = new ArrayList<>();
        rows.add(Collections.singletonList(VALUE));

        FesodSheet.write(file)
                .excelType(ExcelTypeEnum.XLSX)
                .inMemory(inMemory)
                .head(Collections.singletonList(Collections.singletonList("value")))
                .sheet("escape")
                .doWrite(rows);
        return file;
    }

    private String readBackFirstDataValue(File file) throws Exception {
        try (Workbook workbook = WorkbookFactory.create(file)) {
            return workbook.getSheetAt(0).getRow(1).getCell(0).getStringCellValue();
        }
    }
}
