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

package org.apache.fesod.sheet.converters.localtime;

import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.Locale;
import java.util.stream.Stream;
import org.apache.fesod.sheet.enums.CellDataTypeEnum;
import org.apache.fesod.sheet.metadata.GlobalConfiguration;
import org.apache.fesod.sheet.metadata.data.ReadCellData;
import org.apache.fesod.sheet.metadata.data.WriteCellData;
import org.apache.fesod.sheet.metadata.property.DateTimeFormatProperty;
import org.apache.fesod.sheet.metadata.property.ExcelContentProperty;
import org.apache.fesod.sheet.testkit.Tags;
import org.apache.fesod.sheet.util.DateUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Tests {@link LocalTimeStringConverter}.
 */
@Tag(Tags.UNIT)
class LocalTimeStringConverterTest {

    private static final GlobalConfiguration GLOBAL_CONFIGURATION = new GlobalConfiguration();
    private final LocalTimeStringConverter converter = new LocalTimeStringConverter();

    @AfterEach
    void tearDown() {
        DateUtils.removeThreadLocalCache();
    }

    @Test
    void supportKeys() {
        Assertions.assertEquals(LocalTime.class, converter.supportJavaTypeKey());
        Assertions.assertEquals(CellDataTypeEnum.STRING, converter.supportExcelTypeKey());
    }

    @ParameterizedTest
    @CsvSource({"00:00:00", "01:01:01", "12:00:00", "23:59:59"})
    void convertRoundTripWithDefaultFormat(String value) {
        LocalTime time = LocalTime.parse(value);

        LocalTime actual = converter.convertToJavaData(new ReadCellData<>(value), null, GLOBAL_CONFIGURATION);
        WriteCellData<?> written = converter.convertToExcelData(time, null, GLOBAL_CONFIGURATION);

        Assertions.assertEquals(time, actual);
        Assertions.assertEquals(CellDataTypeEnum.STRING, written.getType());
        Assertions.assertEquals(value, written.getStringValue());
    }

    @Test
    void convertToJavaDataAutoDetectsHourMinute() {
        LocalTime actual = converter.convertToJavaData(new ReadCellData<>("12:30"), null, GLOBAL_CONFIGURATION);

        Assertions.assertEquals(LocalTime.of(12, 30), actual);
    }

    @Test
    void convertUsesCustomFormatFromContentProperty() {
        ExcelContentProperty contentProperty = contentProperty(DateUtils.TIME_FORMAT_5, Boolean.FALSE);

        LocalTime actual =
                converter.convertToJavaData(new ReadCellData<>("12:30"), contentProperty, GLOBAL_CONFIGURATION);
        WriteCellData<?> written = converter.convertToExcelData(LocalTime.NOON, contentProperty, GLOBAL_CONFIGURATION);

        Assertions.assertEquals(LocalTime.of(12, 30), actual);
        Assertions.assertEquals("12:00", written.getStringValue());
    }

    @Test
    void convertFallsBackToDefaultWhenContentPropertyHasNoDateTimeFormat() {
        ExcelContentProperty contentProperty = new ExcelContentProperty();

        LocalTime actual =
                converter.convertToJavaData(new ReadCellData<>("01:01:01"), contentProperty, GLOBAL_CONFIGURATION);
        WriteCellData<?> written =
                converter.convertToExcelData(LocalTime.of(1, 1, 1), contentProperty, GLOBAL_CONFIGURATION);

        Assertions.assertEquals(LocalTime.of(1, 1, 1), actual);
        Assertions.assertEquals("01:01:01", written.getStringValue());
    }

    @Test
    void convertToJavaDataUsesSwitchTimeFormatWhenConfiguredFormatIsEmpty() {
        ExcelContentProperty contentProperty = contentProperty("", Boolean.FALSE);

        LocalTime withSeconds =
                converter.convertToJavaData(new ReadCellData<>("12:30:45"), contentProperty, GLOBAL_CONFIGURATION);
        LocalTime withoutSeconds =
                converter.convertToJavaData(new ReadCellData<>("12:30"), contentProperty, GLOBAL_CONFIGURATION);

        Assertions.assertEquals(LocalTime.of(12, 30, 45), withSeconds);
        Assertions.assertEquals(LocalTime.of(12, 30), withoutSeconds);
    }

    @Test
    void convertToExcelDataUsesDefaultFormatWhenConfiguredFormatIsEmpty() {
        ExcelContentProperty contentProperty = contentProperty("", Boolean.FALSE);

        WriteCellData<?> written =
                converter.convertToExcelData(LocalTime.of(12, 30, 45), contentProperty, GLOBAL_CONFIGURATION);

        Assertions.assertEquals("12:30:45", written.getStringValue());
    }

    @ParameterizedTest
    @MethodSource("locales")
    void convertToExcelDataKeepsNumericTimeAcrossLocales(Locale locale) {
        GlobalConfiguration configuration = new GlobalConfiguration();
        configuration.setLocale(locale);

        WriteCellData<?> written = converter.convertToExcelData(LocalTime.of(12, 30, 45), null, configuration);

        Assertions.assertEquals("12:30:45", written.getStringValue());
    }

    @Test
    void convertToExcelDataUsesLocaleSensitiveAmPmMarker() {
        ExcelContentProperty contentProperty = contentProperty("hh:mm:ss a", Boolean.FALSE);
        LocalTime time = LocalTime.of(12, 30, 45);

        GlobalConfiguration us = new GlobalConfiguration();
        us.setLocale(Locale.US);
        GlobalConfiguration china = new GlobalConfiguration();
        china.setLocale(Locale.CHINA);

        WriteCellData<?> usWritten = converter.convertToExcelData(time, contentProperty, us);
        WriteCellData<?> chinaWritten = converter.convertToExcelData(time, contentProperty, china);

        Assertions.assertEquals("12:30:45 PM", usWritten.getStringValue());
        Assertions.assertEquals("12:30:45 下午", chinaWritten.getStringValue());
    }

    @Test
    void convertToJavaDataRejectsUnknownPattern() {
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> converter.convertToJavaData(new ReadCellData<>("not-a-time"), null, GLOBAL_CONFIGURATION));
    }

    @Test
    void convertToJavaDataRejectsInvalidClockTime() {
        Assertions.assertThrows(
                DateTimeParseException.class,
                () -> converter.convertToJavaData(new ReadCellData<>("99:99:99"), null, GLOBAL_CONFIGURATION));
    }

    @Test
    void convertToExcelDataRejectsNullValue() {
        Assertions.assertThrows(
                IllegalArgumentException.class, () -> converter.convertToExcelData(null, null, GLOBAL_CONFIGURATION));
    }

    static Stream<Locale> locales() {
        return Stream.of(Locale.US, Locale.FRANCE, Locale.SIMPLIFIED_CHINESE);
    }

    private static ExcelContentProperty contentProperty(String format, Boolean use1904windowing) {
        ExcelContentProperty contentProperty = new ExcelContentProperty();
        contentProperty.setDateTimeFormatProperty(new DateTimeFormatProperty(format, use1904windowing));
        return contentProperty;
    }
}
