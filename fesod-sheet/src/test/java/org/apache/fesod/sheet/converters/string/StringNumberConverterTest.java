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

package org.apache.fesod.sheet.converters.string;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import org.apache.fesod.sheet.metadata.GlobalConfiguration;
import org.apache.fesod.sheet.metadata.data.ReadCellData;
import org.apache.fesod.sheet.metadata.property.DateTimeFormatProperty;
import org.apache.fesod.sheet.metadata.property.ExcelContentProperty;
import org.apache.fesod.sheet.testkit.Tags;
import org.apache.fesod.sheet.util.DateUtils;
import org.apache.poi.ss.usermodel.DateUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.ResourceLock;
import org.junit.jupiter.api.parallel.Resources;

/**
 * Tests {@link StringNumberConverter}.
 */
@Tag(Tags.UNIT)
class StringNumberConverterTest {

    private final StringNumberConverter converter = new StringNumberConverter();
    private Locale savedDefault;

    @BeforeEach
    void setUp() {
        savedDefault = Locale.getDefault();
        Locale.setDefault(Locale.US);
    }

    @AfterEach
    void tearDown() {
        Locale.setDefault(savedDefault);
        DateUtils.removeThreadLocalCache();
    }

    @Test
    void convertToJavaDataFallsBackToGlobal1904WindowingWhenAnnotationWindowingIsDefault() {
        GlobalConfiguration configuration = new GlobalConfiguration();
        configuration.setUse1904windowing(Boolean.TRUE);
        ExcelContentProperty contentProperty = contentProperty("yyyy-MM-dd", null);

        String actual = converter.convertToJavaData(new ReadCellData<>(BigDecimal.ONE), contentProperty, configuration);

        Assertions.assertEquals("1904-01-02", actual);
    }

    @Test
    void convertToJavaDataPrefersExplicitAnnotationWindowingOverGlobal() {
        GlobalConfiguration configuration = new GlobalConfiguration();
        configuration.setUse1904windowing(Boolean.TRUE);
        ExcelContentProperty contentProperty = contentProperty("yyyy-MM-dd", Boolean.FALSE);

        String actual = converter.convertToJavaData(new ReadCellData<>(BigDecimal.ONE), contentProperty, configuration);

        Assertions.assertEquals("1900-01-01", actual);
    }

    @Test
    @ResourceLock(Resources.LOCALE)
    void convertToJavaDataHonorsConfiguredLocaleForNumericDateSerial() {
        BigDecimal serial = new BigDecimal("45000");
        ExcelContentProperty contentProperty = contentProperty("dd MMMM yyyy", null);

        String chinese = converter.convertToJavaData(
                new ReadCellData<>(serial), contentProperty, globalConfiguration(Locale.CHINESE));
        String us = converter.convertToJavaData(
                new ReadCellData<>(serial), contentProperty, globalConfiguration(Locale.US));

        // The configured locale must change the produced text...
        Assertions.assertNotEquals(us, chinese);
        // ...and the localized output must use that locale rather than the JVM default.
        LocalDateTime localDateTime = DateUtil.getLocalDateTime(serial.doubleValue(), false, true);
        String expectedChinese =
                DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale.CHINESE).format(localDateTime);
        Assertions.assertEquals(expectedChinese, chinese);
    }

    private static GlobalConfiguration globalConfiguration(Locale locale) {
        GlobalConfiguration globalConfiguration = new GlobalConfiguration();
        globalConfiguration.setLocale(locale);
        return globalConfiguration;
    }

    private static ExcelContentProperty contentProperty(String format, Boolean use1904windowing) {
        ExcelContentProperty contentProperty = new ExcelContentProperty();
        contentProperty.setDateTimeFormatProperty(new DateTimeFormatProperty(format, use1904windowing));
        return contentProperty;
    }
}
