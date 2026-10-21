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

package org.apache.fesod.sheet.converters;

import java.io.File;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.Month;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.apache.fesod.sheet.annotation.ExcelProperty;
import org.apache.fesod.sheet.testkit.Tags;
import org.apache.fesod.sheet.testkit.base.AbstractExcelTest;
import org.apache.fesod.sheet.testkit.enums.ExcelFormat;
import org.apache.fesod.sheet.testkit.helpers.RoundTripHelper;
import org.apache.fesod.sheet.testkit.params.ExcelFormatSource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;

/**
 * Field-level round trips for the Month, DayOfWeek, Period and Duration converters across every
 * supported format.
 */
@Tag(Tags.ROUND_TRIP)
public class MonthDayOfWeekPeriodDurationRoundTripTest extends AbstractExcelTest {

    @ParameterizedTest
    @ExcelFormatSource
    void monthFieldRoundTrip(ExcelFormat format) throws Exception {
        File file = createTempFile(format);
        List<MonthFieldData> data = new ArrayList<>();
        MonthFieldData row = new MonthFieldData();
        row.setFlag(Month.MARCH);
        data.add(row);

        List<MonthFieldData> result = RoundTripHelper.writeAndRead(file, MonthFieldData.class, data);

        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(Month.MARCH, result.get(0).getFlag());
    }

    @ParameterizedTest
    @ExcelFormatSource
    void dayOfWeekFieldRoundTrip(ExcelFormat format) throws Exception {
        File file = createTempFile(format);
        List<DayOfWeekFieldData> data = new ArrayList<>();
        DayOfWeekFieldData row = new DayOfWeekFieldData();
        row.setFlag(DayOfWeek.FRIDAY);
        data.add(row);

        List<DayOfWeekFieldData> result = RoundTripHelper.writeAndRead(file, DayOfWeekFieldData.class, data);

        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(DayOfWeek.FRIDAY, result.get(0).getFlag());
    }

    @ParameterizedTest
    @ExcelFormatSource
    void periodFieldRoundTrip(ExcelFormat format) throws Exception {
        File file = createTempFile(format);
        List<PeriodFieldData> data = new ArrayList<>();
        PeriodFieldData row = new PeriodFieldData();
        row.setFlag(Period.of(1, 2, 3));
        data.add(row);

        List<PeriodFieldData> result = RoundTripHelper.writeAndRead(file, PeriodFieldData.class, data);

        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(Period.of(1, 2, 3), result.get(0).getFlag());
    }

    @ParameterizedTest
    @ExcelFormatSource
    void durationFieldRoundTrip(ExcelFormat format) throws Exception {
        File file = createTempFile(format);
        List<DurationFieldData> data = new ArrayList<>();
        DurationFieldData row = new DurationFieldData();
        row.setFlag(Duration.ofHours(5).plusMinutes(30));
        data.add(row);

        List<DurationFieldData> result = RoundTripHelper.writeAndRead(file, DurationFieldData.class, data);

        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(
                Duration.ofHours(5).plusMinutes(30), result.get(0).getFlag());
    }

    @Getter
    @Setter
    public static class MonthFieldData {

        @ExcelProperty("flag")
        private Month flag;
    }

    @Getter
    @Setter
    public static class DayOfWeekFieldData {

        @ExcelProperty("flag")
        private DayOfWeek flag;
    }

    @Getter
    @Setter
    public static class PeriodFieldData {

        @ExcelProperty("flag")
        private Period flag;
    }

    @Getter
    @Setter
    public static class DurationFieldData {

        @ExcelProperty("flag")
        private Duration flag;
    }
}
