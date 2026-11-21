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

import java.time.LocalDate;
import java.time.MonthDay;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.fesod.sheet.testkit.Tags;
import org.apache.fesod.sheet.testkit.base.AbstractExcelTest;
import org.apache.fesod.sheet.testkit.enums.ExcelFormat;
import org.apache.fesod.sheet.testkit.helpers.RoundTripHelper;
import org.apache.fesod.sheet.testkit.params.ExcelFormatSource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;

/**
 * Round-trip tests for {@link org.apache.fesod.sheet.converters.monthday.MonthDayStringConverter} over all Excel
 * formats, covering the default ISO format, a user-configured {@code DateTimeFormat}, null values, boundary values and
 * coexistence with other date converters.
 */
@Tag(Tags.ROUND_TRIP)
public class MonthDayConverterDataTest extends AbstractExcelTest {

    @ParameterizedTest
    @ExcelFormatSource
    void readAndWriteMonthDay(ExcelFormat format) throws Exception {
        MonthDayWriteData data = new MonthDayWriteData();
        data.setBirthday(MonthDay.of(10, 31));

        List<MonthDayWriteData> result = RoundTripHelper.writeAndRead(
                createTempFile(format), MonthDayWriteData.class, Collections.singletonList(data));

        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(MonthDay.of(10, 31), result.get(0).getBirthday());
    }

    @ParameterizedTest
    @ExcelFormatSource
    void readAndWriteMonthDayWithDateTimeFormat(ExcelFormat format) throws Exception {
        MonthDayWithFormatData data = new MonthDayWithFormatData();
        data.setBirthday(MonthDay.of(2, 29));

        List<MonthDayWithFormatData> result = RoundTripHelper.writeAndRead(
                createTempFile(format), MonthDayWithFormatData.class, Collections.singletonList(data));

        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(MonthDay.of(2, 29), result.get(0).getBirthday());
    }

    @ParameterizedTest
    @ExcelFormatSource
    void readAndWriteNullMonthDay(ExcelFormat format) throws Exception {
        MonthDayMixedData data = new MonthDayMixedData();
        data.setDate(LocalDate.of(2024, 3, 15));

        List<MonthDayMixedData> result = RoundTripHelper.writeAndRead(
                createTempFile(format), MonthDayMixedData.class, Collections.singletonList(data));

        Assertions.assertEquals(1, result.size());
        Assertions.assertNull(result.get(0).getBirthday());
        Assertions.assertEquals(LocalDate.of(2024, 3, 15), result.get(0).getDate());
    }

    @ParameterizedTest
    @ExcelFormatSource
    void readAndWriteBoundaryMonthDays(ExcelFormat format) throws Exception {
        List<MonthDayWriteData> data = new ArrayList<>();
        for (String value : new String[] {"--01-01", "--02-29", "--12-31"}) {
            MonthDayWriteData row = new MonthDayWriteData();
            row.setBirthday(MonthDay.parse(value));
            data.add(row);
        }

        List<MonthDayWriteData> result =
                RoundTripHelper.writeAndRead(createTempFile(format), MonthDayWriteData.class, data);

        Assertions.assertEquals(3, result.size());
        Assertions.assertEquals(MonthDay.of(1, 1), result.get(0).getBirthday());
        Assertions.assertEquals(MonthDay.of(2, 29), result.get(1).getBirthday());
        Assertions.assertEquals(MonthDay.of(12, 31), result.get(2).getBirthday());
    }

    @ParameterizedTest
    @ExcelFormatSource
    void readAndWriteMonthDayAlongsideLocalDate(ExcelFormat format) throws Exception {
        MonthDayMixedData data = new MonthDayMixedData();
        data.setBirthday(MonthDay.of(3, 15));
        data.setDate(LocalDate.of(2024, 3, 15));

        List<MonthDayMixedData> result = RoundTripHelper.writeAndRead(
                createTempFile(format), MonthDayMixedData.class, Collections.singletonList(data));

        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(MonthDay.of(3, 15), result.get(0).getBirthday());
        Assertions.assertEquals(LocalDate.of(2024, 3, 15), result.get(0).getDate());
    }
}
