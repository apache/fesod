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

package org.apache.fesod.sheet.head;

import java.io.File;
import java.util.List;
import org.apache.fesod.sheet.FesodSheet;
import org.apache.fesod.sheet.testkit.Tags;
import org.apache.fesod.sheet.testkit.base.AbstractExcelTest;
import org.apache.fesod.sheet.testkit.builders.TestDataBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * Supplying the head class on the sheet builder instead of the workbook builder must parse the file identically:
 * the sheet's own multi-row head defines the head row count, so head rows never leak into the data.
 */
@Tag(Tags.READ)
public class SheetLevelHeadClassTest extends AbstractExcelTest {

    @Test
    public void workbookLevelHeadClassParsesMultiRowHead() throws Exception {
        File file = writeComplexHeadFile();

        List<ComplexHeadData> rows =
                FesodSheet.read(file).head(ComplexHeadData.class).sheet().doReadSync();

        Assertions.assertEquals(2, rows.size());
        Assertions.assertEquals("String4", rows.get(1).getString4());
    }

    @Test
    public void sheetLevelHeadClassParsesMultiRowHead() throws Exception {
        File file = writeComplexHeadFile();

        List<ComplexHeadData> rows =
                FesodSheet.read(file).sheet().head(ComplexHeadData.class).doReadSync();

        Assertions.assertEquals(2, rows.size(), "the multi-row head must not leak into the data rows");
        Assertions.assertEquals("String4", rows.get(1).getString4());
    }

    private File writeComplexHeadFile() throws Exception {
        File file = createTempFile("sheetLevelHeadClass", org.apache.fesod.sheet.testkit.enums.ExcelFormat.XLSX);
        FesodSheet.write(file, ComplexHeadData.class).sheet().doWrite(TestDataBuilder.complexHeadData(2));
        return file;
    }
}
