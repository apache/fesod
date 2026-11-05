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

package org.apache.fesod.sheet.converters.date;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import org.apache.fesod.sheet.metadata.GlobalConfiguration;
import org.apache.fesod.sheet.metadata.data.ReadCellData;
import org.apache.fesod.sheet.metadata.property.DateTimeFormatProperty;
import org.apache.fesod.sheet.metadata.property.ExcelContentProperty;
import org.apache.fesod.sheet.testkit.Tags;
import org.apache.fesod.sheet.util.DateUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.ResourceLock;
import org.junit.jupiter.api.parallel.Resources;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Tests {@link DateStringConverter}.
 */
@Tag(Tags.UNIT)
@ResourceLock(Resources.LOCALE)
class DateStringConverterTest {

    private static final String LOCALIZED_PATTERN = "EEEE dd MMMM yyyy";
    private static final Locale LOCALIZED_LOCALE = Locale.CHINESE;
    private static final GlobalConfiguration GLOBAL_CONFIGURATION = new GlobalConfiguration();
    private final DateStringConverter converter = new DateStringConverter();
    private Locale savedDefault;

    @BeforeEach
    void setUp() {
        savedDefault = Locale.getDefault();
        Locale.setDefault(Locale.US);
        DateUtils.removeThreadLocalCache();
    }

    @AfterEach
    void tearDown() {
        Locale.setDefault(savedDefault);
        DateUtils.removeThreadLocalCache();
    }

    @ParameterizedTest
    @ValueSource(strings = {"2024-02-31", "2023-02-29", "2024-13-01"})
    void convertToJavaDataRejectsInvalidCalendarDates(String value) {
        Assertions.assertThrows(
                ParseException.class,
                () -> converter.convertToJavaData(new ReadCellData<>(value), null, GLOBAL_CONFIGURATION));
    }

    @ParameterizedTest
    @ValueSource(strings = {"2024-02-29", "2023-02-28"})
    void convertToJavaDataAcceptsValidCalendarDates(String value) throws ParseException {
        Date parsed = converter.convertToJavaData(new ReadCellData<>(value), null, GLOBAL_CONFIGURATION);

        Assertions.assertEquals(value, DateUtils.format(parsed, DateUtils.DATE_FORMAT_10));
    }

    @Test
    void convertToExcelDataHonorsConfiguredLocale() throws ParseException {
        Date date = date("2026-11-16");
        ExcelContentProperty contentProperty = contentProperty(LOCALIZED_PATTERN);

        // The same pattern is used twice: the cache must not reuse the formatter built for the first locale.
        String us = converter
                .convertToExcelData(date, contentProperty, globalConfiguration(Locale.US))
                .getStringValue();
        String chinese = converter
                .convertToExcelData(date, contentProperty, globalConfiguration(LOCALIZED_LOCALE))
                .getStringValue();

        Assertions.assertEquals(new SimpleDateFormat(LOCALIZED_PATTERN, Locale.US).format(date), us);
        Assertions.assertEquals(new SimpleDateFormat(LOCALIZED_PATTERN, LOCALIZED_LOCALE).format(date), chinese);
        Assertions.assertNotEquals(us, chinese);
    }

    @Test
    void convertToJavaDataHonorsConfiguredLocale() throws ParseException {
        Date expected = date("2026-11-16");
        String localized = new SimpleDateFormat(LOCALIZED_PATTERN, LOCALIZED_LOCALE).format(expected);
        Assertions.assertNotEquals(new SimpleDateFormat(LOCALIZED_PATTERN, Locale.US).format(expected), localized);

        Date parsed = converter.convertToJavaData(
                new ReadCellData<>(localized),
                contentProperty(LOCALIZED_PATTERN),
                globalConfiguration(LOCALIZED_LOCALE));

        Assertions.assertEquals(expected, parsed);
    }

    private static Date date(String value) throws ParseException {
        return new SimpleDateFormat(DateUtils.DATE_FORMAT_10).parse(value);
    }

    private static ExcelContentProperty contentProperty(String format) {
        ExcelContentProperty contentProperty = new ExcelContentProperty();
        contentProperty.setDateTimeFormatProperty(new DateTimeFormatProperty(format, null));
        return contentProperty;
    }

    private static GlobalConfiguration globalConfiguration(Locale locale) {
        GlobalConfiguration globalConfiguration = new GlobalConfiguration();
        globalConfiguration.setLocale(locale);
        return globalConfiguration;
    }
}
