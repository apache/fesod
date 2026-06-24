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

package org.apache.fesod.sheet.sheet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.io.File;
import java.util.List;
import org.apache.fesod.sheet.ExcelReader;
import org.apache.fesod.sheet.FesodSheet;
import org.apache.fesod.sheet.read.metadata.ReadSheet;
import org.apache.fesod.sheet.testkit.Tags;
import org.apache.fesod.sheet.testkit.base.AbstractExcelTest;
import org.apache.fesod.sheet.testkit.enums.ExcelFormat;
import org.apache.fesod.sheet.testkit.listeners.CollectingReadListener;
import org.apache.fesod.sheet.testkit.models.TitleData;
import org.apache.fesod.sheet.testkit.params.ExcelFormatSource;
import org.apache.fesod.sheet.testkit.params.FormatScope;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;

@Tag(Tags.ROUND_TRIP)
public class HiddenSheetsTest extends AbstractExcelTest {

    @ParameterizedTest
    @ExcelFormatSource(FormatScope.BINARY)
    void read(ExcelFormat format) {
        File file = readFile("hiddensheets" + File.separator + "hiddensheets" + format.getExtension());
        read(file, null);
        read(file, Boolean.FALSE);
        read(file, Boolean.TRUE);
    }

    @ParameterizedTest
    @ExcelFormatSource(FormatScope.BINARY)
    void readAll(ExcelFormat format) {
        File file = readFile("hiddensheets" + File.separator + "hiddensheets" + format.getExtension());
        readAll(file, null);
        readAll(file, Boolean.FALSE);
        readAll(file, Boolean.TRUE);
    }

    @ParameterizedTest
    @ExcelFormatSource(FormatScope.BINARY)
    void readHiddenList(ExcelFormat format) {
        File file = readFile("hiddensheets" + File.separator + "hiddensheets" + format.getExtension());
        try (ExcelReader excelReader = FesodSheet.read(file, TitleData.class, new CollectingReadListener<TitleData>())
                .build()) {
            List<ReadSheet> allSheetList = excelReader.excelExecutor().sheetList();
            assertEquals(2, allSheetList.stream().filter(ReadSheet::isHidden).count());
            assertEquals(
                    1, allSheetList.stream().filter(ReadSheet::isVeryHidden).count());
            assertEquals(
                    "Sheet5",
                    allSheetList.stream()
                            .filter(ReadSheet::isVeryHidden)
                            .findFirst()
                            .get()
                            .getSheetName());
        }
    }

    private void read(File file, Boolean ignoreHidden) {
        try (ExcelReader excelReader = FesodSheet.read(file, TitleData.class, new CollectingReadListener<TitleData>())
                .ignoreHiddenSheet(ignoreHidden)
                .build()) {
            List<ReadSheet> sheets = excelReader.excelExecutor().sheetList();
            if (Boolean.TRUE.equals(ignoreHidden)) {
                assertEquals(3, sheets.size());
            } else {
                assertEquals(6, sheets.size());
            }
        }
    }

    private void readAll(File file, Boolean ignoreHidden) {
        List<TitleData> dataList = FesodSheet.read(file, TitleData.class, new CollectingReadListener<TitleData>())
                .ignoreHiddenSheet(ignoreHidden)
                .doReadAllSync();
        if (Boolean.TRUE.equals(ignoreHidden)) {
            assertEquals(3, dataList.size());
        } else {
            assertEquals(6, dataList.size());
        }
    }
}
