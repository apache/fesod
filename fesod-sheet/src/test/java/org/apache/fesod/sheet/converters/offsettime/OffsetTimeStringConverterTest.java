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

package org.apache.fesod.sheet.converters.offsettime;

import java.time.OffsetTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeParseException;
import java.util.Locale;
import java.util.stream.Stream;
import org.apache.fesod.sheet.converters.ConverterKeyBuild;
import org.apache.fesod.sheet.converters.DefaultConverterLoader;
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
 * Tests {@link OffsetTimeStringConverter}.
 */
@Tag(Tags.UNIT)
class OffsetTimeStringConverterTest {

    private static final GlobalConfiguration GLOBAL_CONFIGURATION = new GlobalConfiguration();
    private static final OffsetTime VALUE = OffsetTime.of(9, 30, 45, 0, ZoneOffset.ofHours(8));
    private final OffsetTimeStringConverter converter = new OffsetTimeStringConverter();

    @AfterEach
    void tearDown() {
        DateUtils.removeThreadLocalCache();
    }

    @Test
    void supportKeys() {
        Assertions.assertEquals(OffsetTime.class, converter.supportJavaTypeKey());
        Assertions.assertEquals(CellDataTypeEnum.STRING, converter.supportExcelTypeKey());
    }

    @ParameterizedTest
    @CsvSource({"09:30:45+08:00", "23:59:59-05:00", "00:00:00Z", "12:34:56.789+05:30"})
    void convertRoundTripWithDefaultFormat(String value) {
        OffsetTime time = OffsetTime.parse(value);

        OffsetTime actual = converter.convertToJavaData(new ReadCellData<>(value), null, GLOBAL_CONFIGURATION);
        WriteCellData<?> written = converter.convertToExcelData(time, null, GLOBAL_CONFIGURATION);

        Assertions.assertEquals(time, actual);
        Assertions.assertEquals(CellDataTypeEnum.STRING, written.getType());
        Assertions.assertEquals(value, written.getStringValue());
    }

    @Test
    void convertUsesCustomFormatFromContentProperty() {
        ExcelContentProperty contentProperty = contentProperty("HH:mm:ss Z", Boolean.FALSE);

        OffsetTime actual = converter.convertToJavaData(
                new ReadCellData<>("09:30:45 +0800"), contentProperty, GLOBAL_CONFIGURATION);
        WriteCellData<?> written = converter.convertToExcelData(VALUE, contentProperty, GLOBAL_CONFIGURATION);

        Assertions.assertEquals(VALUE, actual);
        Assertions.assertEquals("09:30:45 +0800", written.getStringValue());
    }

    @Test
    void convertFallsBackToDefaultWhenContentPropertyHasNoDateTimeFormat() {
        ExcelContentProperty contentProperty = new ExcelContentProperty();

        OffsetTime actual = converter.convertToJavaData(
                new ReadCellData<>("09:30:45+08:00"), contentProperty, GLOBAL_CONFIGURATION);
        WriteCellData<?> written = converter.convertToExcelData(VALUE, contentProperty, GLOBAL_CONFIGURATION);

        Assertions.assertEquals(VALUE, actual);
        Assertions.assertEquals("09:30:45+08:00", written.getStringValue());
    }

    @Test
    void convertWithEmptyOrNullPatternFallsBackToIso() {
        ExcelContentProperty emptyProperty = contentProperty("", Boolean.FALSE);
        ExcelContentProperty nullProperty = contentProperty(null, Boolean.FALSE);

        Assertions.assertEquals(
                VALUE,
                converter.convertToJavaData(new ReadCellData<>("09:30:45+08:00"), emptyProperty, GLOBAL_CONFIGURATION));
        Assertions.assertEquals(
                VALUE,
                converter.convertToJavaData(new ReadCellData<>("09:30:45+08:00"), nullProperty, GLOBAL_CONFIGURATION));
        Assertions.assertEquals(
                "09:30:45+08:00",
                converter
                        .convertToExcelData(VALUE, emptyProperty, GLOBAL_CONFIGURATION)
                        .getStringValue());
        Assertions.assertEquals(
                "09:30:45+08:00",
                converter
                        .convertToExcelData(VALUE, nullProperty, GLOBAL_CONFIGURATION)
                        .getStringValue());
    }

    @ParameterizedTest
    @MethodSource("locales")
    void convertToExcelDataKeepsOffsetAcrossLocales(Locale locale) {
        GlobalConfiguration configuration = new GlobalConfiguration();
        configuration.setLocale(locale);

        WriteCellData<?> written = converter.convertToExcelData(VALUE, null, configuration);

        Assertions.assertEquals("09:30:45+08:00", written.getStringValue());
    }

    @Test
    void convertToExcelDataUsesLocaleSensitiveAmPmMarker() {
        ExcelContentProperty contentProperty = contentProperty("hh:mm:ss a", Boolean.FALSE);
        OffsetTime time = OffsetTime.of(12, 30, 45, 0, ZoneOffset.ofHours(8));

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
    void convertToJavaDataRejectsTextWithoutOffset() {
        Assertions.assertThrows(
                DateTimeParseException.class,
                () -> converter.convertToJavaData(new ReadCellData<>("09:30:45"), null, GLOBAL_CONFIGURATION));
    }

    @Test
    void convertToJavaDataRejectsTextWithUnparsedOffsetWhenPatternHasNone() {
        ExcelContentProperty contentProperty = contentProperty("HH:mm:ss", Boolean.FALSE);

        Assertions.assertThrows(
                DateTimeParseException.class,
                () -> converter.convertToJavaData(
                        new ReadCellData<>("09:30:45+08:00"), contentProperty, GLOBAL_CONFIGURATION));
    }

    @Test
    void convertToJavaDataRejectsInvalidClockTime() {
        Assertions.assertThrows(
                DateTimeParseException.class,
                () -> converter.convertToJavaData(new ReadCellData<>("99:99:99+08:00"), null, GLOBAL_CONFIGURATION));
    }

    @Test
    void convertToExcelDataRejectsNullValue() {
        Assertions.assertThrows(
                IllegalArgumentException.class, () -> converter.convertToExcelData(null, null, GLOBAL_CONFIGURATION));
    }

    @Test
    void converterIsRegisteredForSupportedDirections() {
        Assertions.assertEquals(
                OffsetTimeStringConverter.class,
                DefaultConverterLoader.loadAllConverter()
                        .get(ConverterKeyBuild.buildKey(OffsetTime.class, CellDataTypeEnum.STRING))
                        .getClass());
        Assertions.assertEquals(
                OffsetTimeStringConverter.class,
                DefaultConverterLoader.loadDefaultWriteConverter()
                        .get(ConverterKeyBuild.buildKey(OffsetTime.class))
                        .getClass());
        Assertions.assertEquals(
                OffsetTimeStringConverter.class,
                DefaultConverterLoader.loadDefaultWriteConverter()
                        .get(ConverterKeyBuild.buildKey(OffsetTime.class, CellDataTypeEnum.STRING))
                        .getClass());
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
