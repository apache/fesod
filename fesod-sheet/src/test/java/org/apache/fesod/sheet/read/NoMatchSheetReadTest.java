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
import java.util.List;
import java.util.Map;
import org.apache.fesod.sheet.FesodSheet;
import org.apache.fesod.sheet.support.ExcelTypeEnum;
import org.apache.fesod.sheet.testkit.Tags;
import org.apache.fesod.sheet.testkit.base.AbstractExcelTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * Requesting a sheet that the file does not contain must behave the same for every reader type: an empty read without
 * an exception.
 */
@Tag(Tags.READ)
public class NoMatchSheetReadTest extends AbstractExcelTest {

    @Test
    public void readXlsWithNonExistingSheetNumberReturnsEmpty() {
        File file = readFile("converter" + File.separator + "converter03.xls");

        List<Map<Integer, String>> rows =
                FesodSheet.read(file).excelType(ExcelTypeEnum.XLS).sheet(99).doReadSync();

        Assertions.assertTrue(rows.isEmpty(), "an unmatched sheet must read as empty, not throw");
    }

    @Test
    public void readXlsWithNonExistingSheetNameReturnsEmpty() {
        File file = readFile("converter" + File.separator + "converter03.xls");

        List<Map<Integer, String>> rows = FesodSheet.read(file)
                .excelType(ExcelTypeEnum.XLS)
                .sheet("no such sheet")
                .doReadSync();

        Assertions.assertTrue(rows.isEmpty(), "an unmatched sheet must read as empty, not throw");
    }

    @Test
    public void readXlsxWithNonExistingSheetNumberReturnsEmpty() {
        File file = readFile("converter" + File.separator + "converter07.xlsx");

        List<Map<Integer, String>> rows =
                FesodSheet.read(file).excelType(ExcelTypeEnum.XLSX).sheet(99).doReadSync();

        Assertions.assertTrue(rows.isEmpty(), "the xlsx reader already behaves this way");
    }
}
