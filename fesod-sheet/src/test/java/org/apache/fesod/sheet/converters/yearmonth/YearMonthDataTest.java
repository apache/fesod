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

/*
 * This file is part of the Apache Fesod (Incubating) project, which was derived from Alibaba EasyExcel.
 *
 * Copyright (C) 2018-2024 Alibaba Group Holding Ltd.
 */

package org.apache.fesod.sheet.converters.yearmonth;

import java.io.File;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.apache.fesod.sheet.FesodSheet;
import org.apache.fesod.sheet.annotation.ExcelProperty;
import org.apache.fesod.sheet.annotation.format.DateTimeFormat;
import org.apache.fesod.sheet.support.ExcelTypeEnum;
import org.apache.fesod.sheet.testkit.Tags;
import org.apache.fesod.sheet.testkit.base.AbstractExcelTest;
import org.apache.fesod.sheet.testkit.enums.ExcelFormat;
import org.apache.fesod.sheet.testkit.listeners.CollectingReadListener;
import org.apache.fesod.sheet.testkit.params.ExcelFormatSource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;

/**
 * Round trip tests for the java.time.YearMonth converters.
 */
@Tag(Tags.ROUND_TRIP)
public class YearMonthDataTest extends AbstractExcelTest {

    /**
     * A month name is written and parsed with the locale of the read or write, so the same French pattern is used on
     * both sides.
     */
    private static final DateTimeFormatter FRENCH_MONTH = DateTimeFormatter.ofPattern("MMM yyyy", Locale.FRENCH);

    @ParameterizedTest
    @ExcelFormatSource
    void readAndWrite(ExcelFormat format) throws Exception {
        File file = createTempFile(format);
        FesodSheet.write(file, YearMonthData.class).sheet().doWrite(data());

        CollectingReadListener<YearMonthData> listener = new CollectingReadListener<>();
        FesodSheet.read(file, YearMonthData.class, listener).sheet().doRead();

        Assertions.assertEquals(1, listener.getRowCount());
        YearMonthData row = listener.getFirstRow();
        Assertions.assertEquals(YearMonth.of(2020, 1), row.getMonth());
        Assertions.assertEquals(YearMonth.of(2020, 1), row.getMonthString());
        Assertions.assertEquals(YearMonth.of(2021, 12), row.getMonthFormattedString());
    }

    /**
     * {@code @DateTimeFormat} defaults to an empty value and an empty pattern is not a usable one, so an empty format
     * has to behave like a missing one.
     */
    @ParameterizedTest
    @ExcelFormatSource
    void emptyDateTimeFormat(ExcelFormat format) throws Exception {
        File file = createTempFile(format);
        FesodSheet.write(file, YearMonthData.class).sheet().doWrite(data());

        CollectingReadListener<YearMonthData> listener = new CollectingReadListener<>();
        FesodSheet.read(file, YearMonthData.class, listener).sheet().doRead();

        Assertions.assertEquals(1, listener.getRowCount());
        Assertions.assertEquals(YearMonth.of(2020, 1), listener.getFirstRow().getMonthEmptyFormat());
    }

    /**
     * The locale of the read or write decides how a month name is written and parsed. The cells of a csv file are
     * always strings, so the written text can be checked there as well.
     */
    @ParameterizedTest
    @ExcelFormatSource
    void localeOfTheConfigurationIsUsed(ExcelFormat format) throws Exception {
        File file = createTempFile(format);
        FesodSheet.write(file, YearMonthData.class)
                .locale(Locale.FRENCH)
                .sheet()
                .doWrite(data());

        if (format.toExcelTypeEnum() == ExcelTypeEnum.CSV) {
            List<Map<Integer, String>> raw =
                    FesodSheet.read(file).headRowNumber(0).sheet().doReadSync();
            // The column order follows the field order of the model, monthLocale is the fifth one
            Assertions.assertEquals(
                    FRENCH_MONTH.format(YearMonth.of(2020, 1)), raw.get(1).get(4));
        }

        CollectingReadListener<YearMonthData> listener = new CollectingReadListener<>();
        FesodSheet.read(file, YearMonthData.class, listener)
                .locale(Locale.FRENCH)
                .sheet()
                .doRead();

        Assertions.assertEquals(1, listener.getRowCount());
        Assertions.assertEquals(YearMonth.of(2020, 1), listener.getFirstRow().getMonthLocale());
    }

    private List<YearMonthData> data() {
        List<YearMonthData> list = new ArrayList<YearMonthData>();
        YearMonthData data = new YearMonthData();
        data.setMonth(YearMonth.of(2020, 1));
        data.setMonthString(YearMonth.of(2020, 1));
        data.setMonthFormattedString(YearMonth.of(2021, 12));
        data.setMonthEmptyFormat(YearMonth.of(2020, 1));
        data.setMonthLocale(YearMonth.of(2020, 1));
        list.add(data);
        return list;
    }

    @Getter
    @Setter
    @EqualsAndHashCode
    public static class YearMonthData {
        @ExcelProperty("month")
        private YearMonth month;

        @ExcelProperty("monthString")
        private YearMonth monthString;

        @ExcelProperty("monthFormattedString")
        @DateTimeFormat("yyyy/MM")
        private YearMonth monthFormattedString;

        @ExcelProperty("monthEmptyFormat")
        @DateTimeFormat("")
        private YearMonth monthEmptyFormat;

        @ExcelProperty("monthLocale")
        @DateTimeFormat("MMM yyyy")
        private YearMonth monthLocale;
    }
}
