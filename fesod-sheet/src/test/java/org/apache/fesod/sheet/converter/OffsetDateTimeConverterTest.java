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
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import org.apache.fesod.sheet.converters.ConverterKeyBuild;
import org.apache.fesod.sheet.converters.DefaultConverterLoader;
import org.apache.fesod.sheet.converters.offsetdatetime.OffsetDateTimeDateConverter;
import org.apache.fesod.sheet.converters.offsetdatetime.OffsetDateTimeNumberConverter;
import org.apache.fesod.sheet.converters.offsetdatetime.OffsetDateTimeStringConverter;
import org.apache.fesod.sheet.enums.CellDataTypeEnum;
import org.apache.fesod.sheet.metadata.GlobalConfiguration;
import org.apache.fesod.sheet.metadata.data.ReadCellData;
import org.apache.fesod.sheet.metadata.data.WriteCellData;
import org.apache.fesod.sheet.metadata.property.DateTimeFormatProperty;
import org.apache.fesod.sheet.metadata.property.ExcelContentProperty;
import org.apache.fesod.sheet.testkit.Tags;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag(Tags.UNIT)
class OffsetDateTimeConverterTest {
    private static final OffsetDateTime VALUE = OffsetDateTime.of(2020, 1, 2, 3, 4, 5, 0, ZoneOffset.ofHours(8));

    @Test
    void dateConverterDropsOffsetWhilePreservingLocalDateTime() throws Exception {
        WriteCellData<?> result =
                new OffsetDateTimeDateConverter().convertToExcelData(VALUE, null, new GlobalConfiguration());
        assertEquals(CellDataTypeEnum.DATE, result.getType());
        assertEquals(VALUE.toLocalDateTime(), result.getDateValue());
    }

    @Test
    void numberConverterDropsOffsetWhilePreservingLocalDateTime() {
        OffsetDateTimeNumberConverter converter = new OffsetDateTimeNumberConverter();
        GlobalConfiguration globalConfiguration = new GlobalConfiguration();
        WriteCellData<?> written = converter.convertToExcelData(VALUE, null, globalConfiguration);
        OffsetDateTime read =
                converter.convertToJavaData(new ReadCellData<>(written.getNumberValue()), null, globalConfiguration);
        assertEquals(VALUE.toLocalDateTime().atZone(ZoneId.systemDefault()).toOffsetDateTime(), read);
    }

    @Test
    void stringConverterPreservesOffsetInIsoText() throws Exception {
        OffsetDateTimeStringConverter converter = new OffsetDateTimeStringConverter();
        GlobalConfiguration globalConfiguration = new GlobalConfiguration();
        WriteCellData<?> written = converter.convertToExcelData(VALUE, null, globalConfiguration);
        assertEquals(
                VALUE,
                converter.convertToJavaData(new ReadCellData<>(written.getStringValue()), null, globalConfiguration));
    }

    @Test
    void stringConverterUsesConfiguredPattern() throws Exception {
        OffsetDateTimeStringConverter converter = new OffsetDateTimeStringConverter();
        ExcelContentProperty property = new ExcelContentProperty();
        property.setDateTimeFormatProperty(new DateTimeFormatProperty("yyyy-MM-dd HH:mm:ss Z", false));
        GlobalConfiguration globalConfiguration = new GlobalConfiguration();
        assertEquals(
                "2020-01-02 03:04:05 +0800",
                converter
                        .convertToExcelData(VALUE, property, globalConfiguration)
                        .getStringValue());
    }

    @Test
    void stringConverterFallsBackToLocalDateTimeWhenOffsetIsMissing() throws Exception {
        OffsetDateTimeStringConverter converter = new OffsetDateTimeStringConverter();
        GlobalConfiguration globalConfiguration = new GlobalConfiguration();
        ReadCellData<String> cellData = new ReadCellData<>("2020-01-02T03:04:05");
        assertEquals(
                VALUE.toLocalDateTime().atZone(ZoneId.systemDefault()).toOffsetDateTime(),
                converter.convertToJavaData(cellData, null, globalConfiguration));
    }

    @Test
    void stringConverterWithEmptyOrNullPatternFallsBackToIso() throws Exception {
        OffsetDateTimeStringConverter converter = new OffsetDateTimeStringConverter();
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
                OffsetDateTimeDateConverter.class,
                DefaultConverterLoader.loadDefaultWriteConverter()
                        .get(ConverterKeyBuild.buildKey(OffsetDateTime.class))
                        .getClass());
        assertEquals(
                2,
                DefaultConverterLoader.loadAllConverter().entrySet().stream()
                        .filter(entry -> entry.getKey().getClazz() == OffsetDateTime.class)
                        .count());
    }
}
