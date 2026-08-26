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

package org.apache.fesod.sheet.converter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import org.apache.fesod.sheet.converters.ConverterKeyBuild;
import org.apache.fesod.sheet.converters.DefaultConverterLoader;
import org.apache.fesod.sheet.converters.zoneddatetime.ZonedDateTimeDateConverter;
import org.apache.fesod.sheet.converters.zoneddatetime.ZonedDateTimeNumberConverter;
import org.apache.fesod.sheet.converters.zoneddatetime.ZonedDateTimeStringConverter;
import org.apache.fesod.sheet.enums.CellDataTypeEnum;
import org.apache.fesod.sheet.metadata.GlobalConfiguration;
import org.apache.fesod.sheet.metadata.data.ReadCellData;
import org.apache.fesod.sheet.metadata.data.WriteCellData;
import org.apache.fesod.sheet.metadata.property.DateTimeFormatProperty;
import org.apache.fesod.sheet.metadata.property.ExcelContentProperty;
import org.apache.fesod.sheet.testkit.Tags;
import org.apache.fesod.sheet.util.DateUtils;
import org.apache.poi.ss.usermodel.DateUtil;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag(Tags.UNIT)
class ZonedDateTimeConverterTest {
    private static final ZonedDateTime VALUE = ZonedDateTime.of(2020, 1, 2, 3, 4, 5, 0, ZoneId.of("UTC"));

    @Test
    void dateConverterDropsZoneWhilePreservingLocalDateTime() throws Exception {
        WriteCellData<?> result =
                new ZonedDateTimeDateConverter().convertToExcelData(VALUE, null, new GlobalConfiguration());
        assertEquals(CellDataTypeEnum.DATE, result.getType());
        assertEquals(VALUE.toLocalDateTime(), result.getDateValue());
    }

    @Test
    void dateConverterUsesDefaultFormatForEmptyPattern() throws Exception {
        ZonedDateTimeDateConverter converter = new ZonedDateTimeDateConverter();
        ExcelContentProperty property = new ExcelContentProperty();
        property.setDateTimeFormatProperty(new DateTimeFormatProperty("", false));

        WriteCellData<?> result = converter.convertToExcelData(VALUE, property, new GlobalConfiguration());

        assertEquals(
                DateUtils.defaultDateFormat,
                result.getWriteCellStyle().getDataFormatData().getFormat());
    }

    @Test
    void numberConverterDropsZoneWhilePreservingLocalDateTime() {
        ZonedDateTimeNumberConverter converter = new ZonedDateTimeNumberConverter();
        GlobalConfiguration globalConfiguration = new GlobalConfiguration();
        WriteCellData<?> written = converter.convertToExcelData(VALUE, null, globalConfiguration);
        ZonedDateTime read =
                converter.convertToJavaData(new ReadCellData<>(written.getNumberValue()), null, globalConfiguration);
        assertEquals(VALUE.toLocalDateTime(), read.toLocalDateTime());
        assertEquals(ZoneId.systemDefault(), read.getZone());
    }

    @Test
    void numberConverterSupports1904Windowing() {
        ZonedDateTimeNumberConverter converter = new ZonedDateTimeNumberConverter();

        // 1. Configured via GlobalConfiguration
        GlobalConfiguration global1900 = new GlobalConfiguration();
        GlobalConfiguration global1904 = new GlobalConfiguration();
        global1904.setUse1904windowing(Boolean.TRUE);

        WriteCellData<?> written1900 = converter.convertToExcelData(VALUE, null, global1900);
        WriteCellData<?> written1904 = converter.convertToExcelData(VALUE, null, global1904);

        assertEquals(
                BigDecimal.valueOf(DateUtil.getExcelDate(VALUE.toLocalDateTime(), false)),
                written1900.getNumberValue());
        assertEquals(
                BigDecimal.valueOf(DateUtil.getExcelDate(VALUE.toLocalDateTime(), true)), written1904.getNumberValue());
        assertNotEquals(written1900.getNumberValue(), written1904.getNumberValue());

        ZonedDateTime read1904 =
                converter.convertToJavaData(new ReadCellData<>(written1904.getNumberValue()), null, global1904);
        assertEquals(VALUE.toLocalDateTime(), read1904.toLocalDateTime());
        assertEquals(ZoneId.systemDefault(), read1904.getZone());

        ZonedDateTime read1904With1900 =
                converter.convertToJavaData(new ReadCellData<>(written1904.getNumberValue()), null, global1900);
        assertNotEquals(VALUE.toLocalDateTime(), read1904With1900.toLocalDateTime());

        // 2. Configured via ExcelContentProperty
        ExcelContentProperty property1904 = new ExcelContentProperty();
        property1904.setDateTimeFormatProperty(new DateTimeFormatProperty("yyyy-MM-dd HH:mm:ss", true));

        WriteCellData<?> writtenProperty1904 = converter.convertToExcelData(VALUE, property1904, global1900);
        assertEquals(
                BigDecimal.valueOf(DateUtil.getExcelDate(VALUE.toLocalDateTime(), true)),
                writtenProperty1904.getNumberValue());

        ZonedDateTime readProperty1904 = converter.convertToJavaData(
                new ReadCellData<>(writtenProperty1904.getNumberValue()), property1904, global1900);
        assertEquals(VALUE.toLocalDateTime(), readProperty1904.toLocalDateTime());
        assertEquals(ZoneId.systemDefault(), readProperty1904.getZone());
    }

    @Test
    void stringConverterPreservesZoneInIsoText() throws Exception {
        ZonedDateTimeStringConverter converter = new ZonedDateTimeStringConverter();
        GlobalConfiguration globalConfiguration = new GlobalConfiguration();
        WriteCellData<?> written = converter.convertToExcelData(VALUE, null, globalConfiguration);
        assertEquals(
                VALUE,
                converter.convertToJavaData(new ReadCellData<>(written.getStringValue()), null, globalConfiguration));
    }

    @Test
    void stringConverterUsesConfiguredPattern() throws Exception {
        ZonedDateTimeStringConverter converter = new ZonedDateTimeStringConverter();
        ExcelContentProperty property = new ExcelContentProperty();
        property.setDateTimeFormatProperty(new DateTimeFormatProperty("yyyy-MM-dd HH:mm:ss Z", false));
        GlobalConfiguration globalConfiguration = new GlobalConfiguration();
        assertEquals(
                "2020-01-02 03:04:05 +0000",
                converter
                        .convertToExcelData(VALUE, property, globalConfiguration)
                        .getStringValue());
    }

    @Test
    void stringConverterWithEmptyOrNullPatternFallsBackToIso() throws Exception {
        ZonedDateTimeStringConverter converter = new ZonedDateTimeStringConverter();
        GlobalConfiguration globalConfiguration = new GlobalConfiguration();

        ExcelContentProperty emptyProperty = new ExcelContentProperty();
        emptyProperty.setDateTimeFormatProperty(new DateTimeFormatProperty("", false));
        WriteCellData<?> writtenEmpty = converter.convertToExcelData(VALUE, emptyProperty, globalConfiguration);
        assertEquals(
                VALUE,
                converter.convertToJavaData(
                        new ReadCellData<>(writtenEmpty.getStringValue()), emptyProperty, globalConfiguration));

        ExcelContentProperty nullFormatProperty = new ExcelContentProperty();
        nullFormatProperty.setDateTimeFormatProperty(new DateTimeFormatProperty(null, false));
        WriteCellData<?> writtenNullFormat =
                converter.convertToExcelData(VALUE, nullFormatProperty, globalConfiguration);
        assertEquals(
                VALUE,
                converter.convertToJavaData(
                        new ReadCellData<>(writtenNullFormat.getStringValue()),
                        nullFormatProperty,
                        globalConfiguration));
    }

    @Test
    void convertersAreRegisteredForSupportedDirections() {
        assertEquals(
                ZonedDateTimeDateConverter.class,
                DefaultConverterLoader.loadDefaultWriteConverter()
                        .get(ConverterKeyBuild.buildKey(ZonedDateTime.class))
                        .getClass());
        assertEquals(
                2,
                DefaultConverterLoader.loadAllConverter().entrySet().stream()
                        .filter(entry -> entry.getKey().getClazz() == ZonedDateTime.class)
                        .count());
    }
}
