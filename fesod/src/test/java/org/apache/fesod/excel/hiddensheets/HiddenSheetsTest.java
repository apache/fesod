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

package org.apache.fesod.excel.hiddensheets;

import java.io.File;
import java.util.List;
import org.apache.fesod.excel.ExcelReader;
import org.apache.fesod.excel.FastExcel;
import org.apache.fesod.excel.read.metadata.ReadSheet;
import org.apache.fesod.excel.util.TestFileUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.MethodName.class)
public class HiddenSheetsTest {

    private static File file07;
    private static File file03;

    @BeforeAll
    public static void init() {
        file07 = TestFileUtil.readFile("hiddensheets" + File.separator + "hiddensheets.xlsx");
        file03 = TestFileUtil.readFile("hiddensheets" + File.separator + "hiddensheets.xls");
    }

    @Test
    public void t01Read07() {
        read(file07, null);
        read(file07, Boolean.FALSE);
        read(file07, Boolean.TRUE);
    }

    @Test
    public void t02Read03() {
        read(file03, null);
        read(file03, Boolean.FALSE);
        read(file03, Boolean.TRUE);
    }

    @Test
    public void t03Read07All() {
        readAll(file07, null);
        readAll(file07, Boolean.FALSE);
        readAll(file07, Boolean.TRUE);
    }

    @Test
    public void t04Read03All() {
        readAll(file03, null);
        readAll(file03, Boolean.FALSE);
        readAll(file03, Boolean.TRUE);
    }

    @Test
    public void t05ReadHiddenList() {
        readHiddenList(file03);
        readHiddenList(file07);
    }

    private void readHiddenList(File file) {
        try (ExcelReader excelReader = FastExcel.read(file, HiddenSheetsData.class, new HiddenSheetsListener())
                .build()) {
            List<ReadSheet> allSheetList = excelReader.excelExecutor().sheetList();
            Assertions.assertEquals(
                    2, allSheetList.stream().filter(ReadSheet::isHidden).count());
            Assertions.assertEquals(
                    1, allSheetList.stream().filter(ReadSheet::isVeryHidden).count());
            Assertions.assertEquals(
                    "Sheet5",
                    allSheetList.stream()
                            .filter(ReadSheet::isVeryHidden)
                            .findFirst()
                            .get()
                            .getSheetName());
        }
    }

    private void read(File file, Boolean ignoreHidden) {
        try (ExcelReader excelReader = FastExcel.read(file, HiddenSheetsData.class, new HiddenSheetsListener())
                .ignoreHiddenSheet(ignoreHidden)
                .build()) {
            List<ReadSheet> sheets = excelReader.excelExecutor().sheetList();
            if (Boolean.TRUE.equals(ignoreHidden)) {
                Assertions.assertEquals(3, sheets.size());
            } else {
                Assertions.assertEquals(6, sheets.size());
            }
        }
    }

    private void readAll(File file, Boolean ignoreHidden) {
        List<HiddenSheetsData> dataList = FastExcel.read(file, HiddenSheetsData.class, new HiddenSheetsListener())
                .ignoreHiddenSheet(ignoreHidden)
                .doReadAllSync();
        if (Boolean.TRUE.equals(ignoreHidden)) {
            Assertions.assertEquals(3, dataList.size());
        } else {
            Assertions.assertEquals(6, dataList.size());
        }
    }
}
