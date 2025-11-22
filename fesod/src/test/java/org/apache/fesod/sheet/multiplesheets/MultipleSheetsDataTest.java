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

package org.apache.fesod.sheet.multiplesheets;

import java.io.File;
import java.util.List;
import org.apache.fesod.sheet.ExcelReader;
import org.apache.fesod.sheet.FesodSheet;
import org.apache.fesod.sheet.read.metadata.ReadSheet;
import org.apache.fesod.sheet.util.TestFileUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

/**
 *
 */
@TestMethodOrder(MethodOrderer.MethodName.class)
public class MultipleSheetsDataTest {

    private static File file07;
    private static File file03;

    @BeforeAll
    public static void init() {
        file07 = TestFileUtil.readFile("multiplesheets" + File.separator + "multiplesheets.xlsx");
        file03 = TestFileUtil.readFile("multiplesheets" + File.separator + "multiplesheets.xls");
    }

    @Test
    public void t01Read07() {
        read(file07);
    }

    @Test
    public void t02Read03() {
        read(file03);
    }

    @Test
    public void t03Read07All() {
        readAll(file07);
    }

    @Test
    public void t04Read03All() {
        readAll(file03);
    }

    private void read(File file) {
        MultipleSheetsListener multipleSheetsListener = new MultipleSheetsListener();
        try (ExcelReader excelReader = FesodSheet.read(file, MultipleSheetsData.class, multipleSheetsListener)
                .build()) {
            List<ReadSheet> sheets = excelReader.excelExecutor().sheetList();
            int count = 1;
            for (ReadSheet readSheet : sheets) {
                excelReader.read(readSheet);
                Assertions.assertEquals(multipleSheetsListener.getList().size(), count);
                count++;
            }
        }
    }

    private void readAll(File file) {
        FesodSheet.read(file, MultipleSheetsData.class, new MultipleSheetsListener())
                .doReadAll();
    }
}
