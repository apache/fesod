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

package org.apache.fesod.sheet.readwrite;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;
import org.apache.fesod.sheet.FesodSheet;
import org.apache.fesod.sheet.testkit.Tags;
import org.apache.fesod.sheet.testkit.base.AbstractExcelTest;
import org.apache.fesod.sheet.testkit.enums.ExcelFormat;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * Regression test for issue #1042: a field carrying {@code @DateTimeFormat} for the pattern only (leaving
 * {@code use1904windowing} at {@code DEFAULT}) must follow the global 1904 windowing setting instead of
 * collapsing to the 1900 windowing system.
 */
@Tag(Tags.ROUND_TRIP)
public class DateTimeFormatDefaultWindowingTest extends AbstractExcelTest {

    private static final LocalDateTime VALUE = LocalDateTime.of(2026, 8, 28, 10, 30, 0);

    @Test
    void reads1904FileWithDefaultAnnotationWindowingFromDetectedFlag() throws Exception {
        File file1904 = createTempFile("dtf1904detected", ExcelFormat.XLSX);
        write1904File(file1904);

        List<DateTimeFormatData> read =
                FesodSheet.read(file1904).head(DateTimeFormatData.class).sheet().doReadSync();

        Assertions.assertEquals(1, read.size());
        Assertions.assertEquals(VALUE, read.get(0).getTime());
    }

    @Test
    void reads1904FileWithDefaultAnnotationWindowingFromExplicitGlobalSetting() throws Exception {
        File file1904 = createTempFile("dtf1904explicit", ExcelFormat.XLSX);
        write1904File(file1904);

        List<DateTimeFormatData> read = FesodSheet.read(file1904)
                .head(DateTimeFormatData.class)
                .use1904windowing(true)
                .sheet()
                .doReadSync();

        Assertions.assertEquals(1, read.size());
        Assertions.assertEquals(VALUE, read.get(0).getTime());
    }

    @Test
    void writes1904SerialWithDefaultAnnotationWindowing() throws Exception {
        File file1904 = createTempFile("dtf1904write", ExcelFormat.XLSX);

        FesodSheet.write(file1904, DateTimeFormatData.class)
                .use1904windowing(true)
                .sheet()
                .doWrite(java.util.Collections.singletonList(new DateTimeFormatData(VALUE)));

        try (Workbook workbook = WorkbookFactory.create(file1904)) {
            double serial = workbook.getSheetAt(0).getRow(1).getCell(0).getNumericCellValue();
            Assertions.assertEquals(DateUtil.getExcelDate(VALUE, true), serial, 1e-6);
        }
    }

    private void write1904File(File file) {
        FesodSheet.write(file, DateTimeFormatData.class)
                .use1904windowing(true)
                .sheet()
                .doWrite(java.util.Collections.singletonList(new DateTimeFormatData(VALUE)));
    }
}
