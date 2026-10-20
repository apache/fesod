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

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.apache.fesod.sheet.FesodSheet;
import org.apache.fesod.sheet.testkit.Tags;
import org.apache.fesod.sheet.testkit.assertions.ExcelAssertions;
import org.apache.fesod.sheet.testkit.base.AbstractExcelTest;
import org.apache.fesod.sheet.testkit.enums.ExcelFormat;
import org.apache.fesod.sheet.util.DateUtils;
import org.apache.fesod.sheet.write.handler.RowWriteHandler;
import org.apache.fesod.sheet.write.handler.context.RowWriteHandlerContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * End-to-end coverage of the default date patterns: per-call builder parameters, the process-wide setters, and the
 * workbook-level snapshot semantics (a mid-write global change must not affect the running workbook).
 */
@Tag(Tags.FORMAT)
public class DefaultDateFormatWriteTest extends AbstractExcelTest {

    private static final String PARAM_DATE_TIME_FORMAT = "yyyy/MM/dd HH:mm:ss";

    private static final String PARAM_LOCAL_DATE_FORMAT = "yyyy/MM/dd";

    private static final String PARAM_YEAR_FORMAT = "yy";

    @AfterEach
    void resetDefaults() {
        DateUtils.resetDefaultDateFormats();
    }

    @Test
    void perCallBuilderParametersApplyToXlsxWrite() throws IOException {
        File file = createTempFile(ExcelFormat.XLSX);

        FesodSheet.write(file)
                .defaultDateFormat(PARAM_DATE_TIME_FORMAT)
                .defaultLocalDateFormat(PARAM_LOCAL_DATE_FORMAT)
                .defaultYearFormat(PARAM_YEAR_FORMAT)
                .head(DateRow.class)
                .sheet()
                .doWrite(rows());

        ExcelAssertions.assertThat(file)
                .sheet(0)
                .row(1)
                .cell(0)
                .hasDataFormatString(PARAM_DATE_TIME_FORMAT)
                .and()
                .cell(1)
                .hasDataFormatString(PARAM_DATE_TIME_FORMAT)
                .and()
                .cell(2)
                .hasDataFormatString(PARAM_LOCAL_DATE_FORMAT)
                .and()
                .cell(3)
                .hasStringValue("26");
    }

    @Test
    void globalSettersApplyToXlsxWrite() throws IOException {
        DateUtils.setDefaultDateFormat(PARAM_DATE_TIME_FORMAT);
        DateUtils.setDefaultLocalDateFormat(PARAM_LOCAL_DATE_FORMAT);
        DateUtils.setDefaultYearFormat(PARAM_YEAR_FORMAT);
        File file = createTempFile(ExcelFormat.XLSX);

        FesodSheet.write(file).head(DateRow.class).sheet().doWrite(rows());

        ExcelAssertions.assertThat(file)
                .sheet(0)
                .row(1)
                .cell(0)
                .hasDataFormatString(PARAM_DATE_TIME_FORMAT)
                .and()
                .cell(1)
                .hasDataFormatString(PARAM_DATE_TIME_FORMAT)
                .and()
                .cell(2)
                .hasDataFormatString(PARAM_LOCAL_DATE_FORMAT)
                .and()
                .cell(3)
                .hasStringValue("26");
    }

    @Test
    void midWriteGlobalChangeDoesNotAffectRunningWorkbook() throws IOException {
        File file = createTempFile(ExcelFormat.XLSX);

        FesodSheet.write(file)
                .registerWriteHandler(new GlobalFlippingRowHandler())
                .head(DateRow.class)
                .sheet()
                .doWrite(rows());

        // Both rows must carry the snapshot taken at context creation, not the flipped global value.
        ExcelAssertions.assertThat(file)
                .sheet(0)
                .row(1)
                .cell(0)
                .hasDataFormatString(DateUtils.DATE_FORMAT_19)
                .and()
                .and()
                .row(2)
                .cell(0)
                .hasDataFormatString(DateUtils.DATE_FORMAT_19);
    }

    @Test
    void csvWriteUsesPerCallDefault() throws IOException {
        File file = createTempFile("defaults", ExcelFormat.CSV);

        FesodSheet.write(file)
                .defaultDateFormat(PARAM_DATE_TIME_FORMAT)
                .head(DateRow.class)
                .sheet()
                .doWrite(rows());

        String csv = new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
        Assertions.assertTrue(csv.contains("2026/01/15 10:30:00"));
        Assertions.assertTrue(csv.contains("2026/01/15"));
    }

    @Getter
    @Setter
    static class DateRow {
        private Date date;
        private LocalDateTime dateTime;
        private LocalDate localDate;
        private Year year;
    }

    /**
     * Flips the process-wide default after the first data row was written; the running workbook must keep using the
     * pattern it snapshotted at context creation.
     */
    static class GlobalFlippingRowHandler implements RowWriteHandler {
        @Override
        public void afterRowDispose(RowWriteHandlerContext context) {
            if (Integer.valueOf(1).equals(context.getRowIndex())) {
                DateUtils.setDefaultDateFormat("yyyyMMddHHmmss");
            }
        }
    }

    private static List<DateRow> rows() {
        return Arrays.asList(row(15), row(16));
    }

    private static DateRow row(int day) {
        DateRow row = new DateRow();
        row.setDate(new GregorianCalendar(2026, Calendar.JANUARY, day, 10, 30, 0).getTime());
        row.setDateTime(LocalDateTime.of(2026, 1, day, 10, 30, 0));
        row.setLocalDate(LocalDate.of(2026, 1, day));
        row.setYear(Year.of(2026));
        return row;
    }
}
