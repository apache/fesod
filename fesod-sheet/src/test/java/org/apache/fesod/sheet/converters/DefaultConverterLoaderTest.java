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

import java.sql.Time;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalTime;
import java.time.Month;
import java.time.Period;
import java.util.Map;
import org.apache.fesod.sheet.converters.ConverterKeyBuild.ConverterKey;
import org.apache.fesod.sheet.converters.dayofweek.DayOfWeekStringConverter;
import org.apache.fesod.sheet.converters.duration.DurationStringConverter;
import org.apache.fesod.sheet.converters.localtime.LocalTimeDateConverter;
import org.apache.fesod.sheet.converters.localtime.LocalTimeNumberConverter;
import org.apache.fesod.sheet.converters.localtime.LocalTimeStringConverter;
import org.apache.fesod.sheet.converters.month.MonthStringConverter;
import org.apache.fesod.sheet.converters.period.PeriodStringConverter;
import org.apache.fesod.sheet.converters.sqltime.SqlTimeDateConverter;
import org.apache.fesod.sheet.converters.sqltime.SqlTimeNumberConverter;
import org.apache.fesod.sheet.converters.sqltime.SqlTimeStringConverter;
import org.apache.fesod.sheet.enums.CellDataTypeEnum;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class DefaultConverterLoaderTest {

    @Test
    void loadDefaultWriteConverterIsImmutableAndCopyIsMutable() {
        assertLoadIsImmutableAndCopyIsMutable(
                DefaultConverterLoader.loadDefaultWriteConverter(), DefaultConverterLoader.copyDefaultWriteConverter());
    }

    @Test
    void loadDefaultReadConverterIsImmutableAndCopyIsMutable() {
        assertLoadIsImmutableAndCopyIsMutable(
                DefaultConverterLoader.loadDefaultReadConverter(), DefaultConverterLoader.copyDefaultReadConverter());
    }

    @Test
    void loadAllConverterIsImmutableAndCopyIsMutable() {
        assertLoadIsImmutableAndCopyIsMutable(
                DefaultConverterLoader.loadAllConverter(), DefaultConverterLoader.copyAllConverter());
    }

    @Test
    void loadConvertersRegistersLocalTimeFamily() {
        Map<ConverterKey, Converter<?>> allConverter = DefaultConverterLoader.loadAllConverter();
        Assertions.assertInstanceOf(
                LocalTimeNumberConverter.class,
                allConverter.get(ConverterKeyBuild.buildKey(LocalTime.class, CellDataTypeEnum.NUMBER)));
        Assertions.assertInstanceOf(
                LocalTimeStringConverter.class,
                allConverter.get(ConverterKeyBuild.buildKey(LocalTime.class, CellDataTypeEnum.STRING)));

        Map<ConverterKey, Converter<?>> writeConverter = DefaultConverterLoader.loadDefaultWriteConverter();
        Assertions.assertInstanceOf(
                LocalTimeDateConverter.class, writeConverter.get(ConverterKeyBuild.buildKey(LocalTime.class)));
        Assertions.assertInstanceOf(
                LocalTimeStringConverter.class,
                writeConverter.get(ConverterKeyBuild.buildKey(LocalTime.class, CellDataTypeEnum.STRING)));
    }

    @Test
    void loadConvertersRegistersMonthDayOfWeekPeriodAndDurationFamilies() {
        Map<ConverterKey, Converter<?>> allConverter = DefaultConverterLoader.loadAllConverter();
        Assertions.assertInstanceOf(
                MonthStringConverter.class,
                allConverter.get(ConverterKeyBuild.buildKey(Month.class, CellDataTypeEnum.STRING)));
        Assertions.assertInstanceOf(
                DayOfWeekStringConverter.class,
                allConverter.get(ConverterKeyBuild.buildKey(DayOfWeek.class, CellDataTypeEnum.STRING)));
        Assertions.assertInstanceOf(
                PeriodStringConverter.class,
                allConverter.get(ConverterKeyBuild.buildKey(Period.class, CellDataTypeEnum.STRING)));
        Assertions.assertInstanceOf(
                DurationStringConverter.class,
                allConverter.get(ConverterKeyBuild.buildKey(Duration.class, CellDataTypeEnum.STRING)));

        Map<ConverterKey, Converter<?>> writeConverter = DefaultConverterLoader.loadDefaultWriteConverter();
        Assertions.assertInstanceOf(
                MonthStringConverter.class, writeConverter.get(ConverterKeyBuild.buildKey(Month.class)));
        Assertions.assertInstanceOf(
                DayOfWeekStringConverter.class, writeConverter.get(ConverterKeyBuild.buildKey(DayOfWeek.class)));
        Assertions.assertInstanceOf(
                PeriodStringConverter.class, writeConverter.get(ConverterKeyBuild.buildKey(Period.class)));
        Assertions.assertInstanceOf(
                DurationStringConverter.class, writeConverter.get(ConverterKeyBuild.buildKey(Duration.class)));
    }

    @Test
    void loadConvertersRegistersSqlTimeFamily() {
        Map<ConverterKey, Converter<?>> allConverter = DefaultConverterLoader.loadAllConverter();
        Assertions.assertInstanceOf(
                SqlTimeNumberConverter.class,
                allConverter.get(ConverterKeyBuild.buildKey(Time.class, CellDataTypeEnum.NUMBER)));
        Assertions.assertInstanceOf(
                SqlTimeStringConverter.class,
                allConverter.get(ConverterKeyBuild.buildKey(Time.class, CellDataTypeEnum.STRING)));
        Map<ConverterKey, Converter<?>> writeConverter = DefaultConverterLoader.loadDefaultWriteConverter();
        Assertions.assertInstanceOf(
                SqlTimeDateConverter.class, writeConverter.get(ConverterKeyBuild.buildKey(Time.class)));
        Assertions.assertInstanceOf(
                SqlTimeStringConverter.class,
                writeConverter.get(ConverterKeyBuild.buildKey(Time.class, CellDataTypeEnum.STRING)));
    }

    private static void assertLoadIsImmutableAndCopyIsMutable(
            Map<ConverterKey, Converter<?>> loaded, Map<ConverterKey, Converter<?>> copy) {
        Map.Entry<ConverterKey, Converter<?>> entry =
                loaded.entrySet().iterator().next();

        Assertions.assertThrows(
                UnsupportedOperationException.class, () -> loaded.put(entry.getKey(), entry.getValue()));

        copy.remove(entry.getKey());
        Assertions.assertFalse(copy.containsKey(entry.getKey()));
        Assertions.assertTrue(loaded.containsKey(entry.getKey()));
    }
}
