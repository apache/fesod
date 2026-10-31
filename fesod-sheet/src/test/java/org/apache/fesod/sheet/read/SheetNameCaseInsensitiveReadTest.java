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

package org.apache.fesod.sheet.read;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Map;
import org.apache.fesod.sheet.FesodSheet;
import org.apache.fesod.sheet.testkit.Tags;
import org.apache.fesod.sheet.testkit.base.AbstractExcelTest;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * Sheet-name matching on read follows the workbook's own name semantics (POI resolves sheet names
 * case-insensitively, and the write/fill path uses that), so a name differing only in case must match instead of
 * silently reading nothing.
 */
@Tag(Tags.READ)
public class SheetNameCaseInsensitiveReadTest extends AbstractExcelTest {

    @Test
    public void readBySheetNameMatchesCaseInsensitively() throws Exception {
        File file = fileWithTab("Data");

        Assertions.assertEquals(1, readRows(file, "Data"), "the exact name matches");
        Assertions.assertEquals(1, readRows(file, "data"), "a lowercase variant matches like POI's getSheet");
        Assertions.assertEquals(1, readRows(file, "DATA"), "an uppercase variant matches like POI's getSheet");
        Assertions.assertEquals(0, readRows(file, "NotTheSheet"), "an unrelated name still reads nothing");
    }

    private File fileWithTab(String tabName) throws Exception {
        File file = createTempFile("sheetNameCase", org.apache.fesod.sheet.testkit.enums.ExcelFormat.XLSX);
        try (Workbook workbook = new XSSFWorkbook()) {
            workbook.createSheet(tabName).createRow(0).createCell(0).setCellValue("v");
            try (FileOutputStream out = new FileOutputStream(file)) {
                workbook.write(out);
            }
        }
        return file;
    }

    private int readRows(File file, String sheetName) {
        List<Map<Integer, String>> rows =
                FesodSheet.read(file).sheet(sheetName).headRowNumber(0).doReadSync();
        return rows.size();
    }
}
