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

package org.apache.fesod.sheet.converter;

import java.io.File;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import org.apache.fesod.sheet.FesodSheet;
import org.apache.fesod.sheet.annotation.format.DateTimeFormat;
import org.apache.fesod.sheet.testkit.Tags;
import org.apache.fesod.sheet.testkit.base.AbstractExcelTest;
import org.apache.fesod.sheet.testkit.enums.ExcelFormat;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * An empty {@code @DateTimeFormat} value means "no explicit pattern": date fields must fall back to the default date
 * format instead of writing an empty custom format that renders the raw serial number.
 */
@Tag(Tags.WRITE)
public class EmptyDateTimeFormatWriteTest extends AbstractExcelTest {

    private static final String DEFAULT_DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public static class LocalDateTimeModel {
        @DateTimeFormat("")
        private LocalDateTime when;

        public LocalDateTime getWhen() {
            return when;
        }

        public void setWhen(LocalDateTime when) {
            this.when = when;
        }
    }

    public static class OffsetDateTimeModel {
        @DateTimeFormat("")
        private OffsetDateTime when;

        public OffsetDateTime getWhen() {
            return when;
        }

        public void setWhen(OffsetDateTime when) {
            this.when = when;
        }
    }

    @Test
    public void localDateTimeWithEmptyFormatFallsBackToDefaultDateFormat() throws Exception {
        File file = createTempFile("emptyFmtLocalDateTime", ExcelFormat.XLSX);
        LocalDateTimeModel model = new LocalDateTimeModel();
        model.setWhen(LocalDateTime.of(2020, 1, 2, 3, 4, 5));

        FesodSheet.write(file, LocalDateTimeModel.class).sheet().doWrite(Collections.singletonList(model));

        Assertions.assertEquals(
                DEFAULT_DATE_TIME_FORMAT,
                readCellDataFormat(file),
                "an empty pattern must fall back to the default date format, not an empty one");
    }

    @Test
    public void offsetDateTimeWithEmptyFormatFallsBackToDefaultDateFormat() throws Exception {
        File file = createTempFile("emptyFmtOffsetDateTime", ExcelFormat.XLSX);
        OffsetDateTimeModel model = new OffsetDateTimeModel();
        model.setWhen(OffsetDateTime.of(2020, 1, 2, 3, 4, 5, 0, ZoneOffset.UTC));

        FesodSheet.write(file, OffsetDateTimeModel.class).sheet().doWrite(Collections.singletonList(model));

        Assertions.assertEquals(
                DEFAULT_DATE_TIME_FORMAT,
                readCellDataFormat(file),
                "the offset converter already normalizes empty patterns");
    }

    @Test
    public void explicitFormatIsStillApplied() throws Exception {
        File file = createTempFile("explicitFmt", ExcelFormat.XLSX);
        ExplicitFormatModel model = new ExplicitFormatModel();
        model.setWhen(LocalDateTime.of(2020, 1, 2, 3, 4, 5));

        FesodSheet.write(file, ExplicitFormatModel.class).sheet().doWrite(Collections.singletonList(model));

        Assertions.assertEquals("yyyy/MM/dd", readCellDataFormat(file), "an explicit pattern is applied as-is");
    }

    public static class ExplicitFormatModel {
        @DateTimeFormat("yyyy/MM/dd")
        private LocalDateTime when;

        public LocalDateTime getWhen() {
            return when;
        }

        public void setWhen(LocalDateTime when) {
            this.when = when;
        }
    }

    private String readCellDataFormat(File file) throws Exception {
        try (Workbook workbook = WorkbookFactory.create(file)) {
            Cell cell = workbook.getSheetAt(0).getRow(1).getCell(0);
            return cell.getCellStyle().getDataFormatString();
        }
    }
}
