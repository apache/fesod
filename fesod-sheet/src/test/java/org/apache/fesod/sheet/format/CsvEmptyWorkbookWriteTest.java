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

package org.apache.fesod.sheet.format;

import java.io.ByteArrayOutputStream;
import org.apache.fesod.sheet.ExcelWriter;
import org.apache.fesod.sheet.FesodSheet;
import org.apache.fesod.sheet.support.ExcelTypeEnum;
import org.apache.fesod.sheet.testkit.Tags;
import org.apache.fesod.sheet.testkit.base.AbstractExcelTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * A writer that is finished without ever writing a sheet must succeed for CSV just like it does for XLSX - a
 * conditional export that ends up with nothing to write.
 */
@Tag(Tags.WRITE)
public class CsvEmptyWorkbookWriteTest extends AbstractExcelTest {

    @Test
    public void finishWithoutAnySheetWritesEmptyOutputForCsv() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        ExcelWriter writer = FesodSheet.write(out).excelType(ExcelTypeEnum.CSV).build();
        writer.finish();

        Assertions.assertEquals(0, out.size(), "an empty export produces no content");
    }

    @Test
    public void finishWithoutAnySheetWritesEmptyOutputForXlsx() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        ExcelWriter writer = FesodSheet.write(out).excelType(ExcelTypeEnum.XLSX).build();
        writer.finish();

        Assertions.assertTrue(out.size() > 0, "xlsx produces an empty workbook file");
    }
}
