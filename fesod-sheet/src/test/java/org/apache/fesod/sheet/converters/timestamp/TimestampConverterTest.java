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

package org.apache.fesod.sheet.converters.timestamp;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Locale;
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
import org.apache.poi.ss.usermodel.DateUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for java.sql.Timestamp converter implementations.
 */
@Tag(Tags.UNIT)
class TimestampConverterTest {

    private static final GlobalConfiguration GLOBAL_CONFIGURATION = new GlobalConfiguration();

    @AfterEach
    void tearDown() {
        DateUtils.removeThreadLocalCache();
    }

    @Test
    void supportKeys() {
        Assertions.assertEquals(Timestamp.class, new TimestampDateConverter().supportJavaTypeKey());

        TimestampNumberConverter numberConverter = new TimestampNumberConverter();
        Assertions.assertEquals(Timestamp.class, numberConverter.supportJavaTypeKey());
        Assertions.assertEquals(CellDataTypeEnum.NUMBER, numberConverter.supportExcelTypeKey());

        TimestampStringConverter stringConverter = new TimestampStringConverter();
        Assertions.assertEquals(Timestamp.class, stringConverter.supportJavaTypeKey());
        Assertions.assertEquals(CellDataTypeEnum.STRING, stringConverter.supportExcelTypeKey());
    }

    @Test
    void numberConverterReadAndWrite() throws Exception {
        TimestampNumberConverter converter = new TimestampNumberConverter();
        LocalDateTime localDateTime = LocalDateTime.of(2020, 1, 1, 1, 1, 1);
        double excelDate = DateUtil.getExcelDate(localDateTime);

        Timestamp javaData = converter.convertToJavaData(
                new ReadCellData<>(BigDecimal.valueOf(excelDate)), null, GLOBAL_CONFIGURATION);
        Assertions.assertEquals(Timestamp.valueOf(localDateTime), javaData);

        WriteCellData<?> writeCellData =
                converter.convertToExcelData(Timestamp.valueOf(localDateTime), null, GLOBAL_CONFIGURATION);
        Assertions.assertEquals(0, writeCellData.getNumberValue().compareTo(BigDecimal.valueOf(excelDate)));
    }

    @Test
    void numberConverterRespectsUse1904Windowing() throws Exception {
        TimestampNumberConverter converter = new TimestampNumberConverter();
        LocalDateTime localDateTime = LocalDateTime.of(2020, 1, 1, 1, 1, 1);

        ExcelContentProperty contentProperty = new ExcelContentProperty();
        DateTimeFormatProperty dateTimeFormatProperty = new DateTimeFormatProperty(null, true);
        contentProperty.setDateTimeFormatProperty(dateTimeFormatProperty);

        double excelDate1904 = DateUtil.getExcelDate(localDateTime, true);
        Timestamp javaData = converter.convertToJavaData(
                new ReadCellData<>(BigDecimal.valueOf(excelDate1904)), contentProperty, GLOBAL_CONFIGURATION);
        Assertions.assertEquals(Timestamp.valueOf(localDateTime), javaData);

        WriteCellData<?> writeCellData =
                converter.convertToExcelData(Timestamp.valueOf(localDateTime), contentProperty, GLOBAL_CONFIGURATION);
        Assertions.assertEquals(0, writeCellData.getNumberValue().compareTo(BigDecimal.valueOf(excelDate1904)));
    }

    @Test
    void numberConverterFallsBackToGlobalWindowingWhenFormatLeaves1904Unset() throws Exception {
        TimestampNumberConverter converter = new TimestampNumberConverter();
        LocalDateTime localDateTime = LocalDateTime.of(2020, 1, 1, 1, 1, 1);

        ExcelContentProperty contentProperty = new ExcelContentProperty();
        contentProperty.setDateTimeFormatProperty(new DateTimeFormatProperty("yyyy/MM/dd HH:mm:ss", null));

        double excelDate = DateUtil.getExcelDate(localDateTime);
        Timestamp javaData = converter.convertToJavaData(
                new ReadCellData<>(BigDecimal.valueOf(excelDate)), contentProperty, GLOBAL_CONFIGURATION);
        Assertions.assertEquals(Timestamp.valueOf(localDateTime), javaData);

        WriteCellData<?> writeCellData =
                converter.convertToExcelData(Timestamp.valueOf(localDateTime), contentProperty, GLOBAL_CONFIGURATION);
        Assertions.assertEquals(0, writeCellData.getNumberValue().compareTo(BigDecimal.valueOf(excelDate)));

        GlobalConfiguration globalConfiguration1904 = new GlobalConfiguration();
        globalConfiguration1904.setUse1904windowing(Boolean.TRUE);
        double excelDate1904 = DateUtil.getExcelDate(localDateTime, true);
        Timestamp javaData1904 = converter.convertToJavaData(
                new ReadCellData<>(BigDecimal.valueOf(excelDate1904)), contentProperty, globalConfiguration1904);
        Assertions.assertEquals(Timestamp.valueOf(localDateTime), javaData1904);

        WriteCellData<?> writeCellData1904 = converter.convertToExcelData(
                Timestamp.valueOf(localDateTime), contentProperty, globalConfiguration1904);
        Assertions.assertEquals(0, writeCellData1904.getNumberValue().compareTo(BigDecimal.valueOf(excelDate1904)));
    }

    @Test
    void numberConverterReturnsNullForInvalidExcelDate() throws Exception {
        TimestampNumberConverter converter = new TimestampNumberConverter();
        Assertions.assertNull(
                converter.convertToJavaData(new ReadCellData<>(BigDecimal.valueOf(-1)), null, GLOBAL_CONFIGURATION));
    }

    @Test
    void stringConverterReadAndWrite() throws Exception {
        TimestampStringConverter converter = new TimestampStringConverter();
        Timestamp timestamp = Timestamp.valueOf("2020-01-01 01:01:01");

        Timestamp javaData =
                converter.convertToJavaData(new ReadCellData<>("2020-01-01 01:01:01"), null, GLOBAL_CONFIGURATION);
        Assertions.assertEquals(timestamp, javaData);

        WriteCellData<?> writeCellData = converter.convertToExcelData(timestamp, null, GLOBAL_CONFIGURATION);
        Assertions.assertEquals("2020-01-01 01:01:01", writeCellData.getStringValue());
    }

    @Test
    void stringConverterRespectsDateTimeFormatAndLocale() throws Exception {
        TimestampStringConverter converter = new TimestampStringConverter();
        Timestamp timestamp = Timestamp.valueOf("2020-01-01 01:01:01");

        ExcelContentProperty contentProperty = new ExcelContentProperty();
        DateTimeFormatProperty dateTimeFormatProperty = new DateTimeFormatProperty("dd MMMM yyyy HH:mm:ss", null);
        contentProperty.setDateTimeFormatProperty(dateTimeFormatProperty);

        GlobalConfiguration globalConfiguration = new GlobalConfiguration();
        globalConfiguration.setLocale(Locale.US);

        Timestamp javaData = converter.convertToJavaData(
                new ReadCellData<>("01 January 2020 01:01:01"), contentProperty, globalConfiguration);
        Assertions.assertEquals(timestamp, javaData);

        WriteCellData<?> writeCellData = converter.convertToExcelData(timestamp, contentProperty, globalConfiguration);
        Assertions.assertEquals("01 January 2020 01:01:01", writeCellData.getStringValue());
    }

    @Test
    void dateConverterWrite() throws Exception {
        TimestampDateConverter converter = new TimestampDateConverter();
        Timestamp timestamp = Timestamp.valueOf("2020-01-01 01:01:01");

        WriteCellData<?> writeCellData = converter.convertToExcelData(timestamp, null, GLOBAL_CONFIGURATION);
        Assertions.assertEquals(CellDataTypeEnum.DATE, writeCellData.getType());
        Assertions.assertEquals(timestamp.toLocalDateTime(), writeCellData.getDateValue());
        Assertions.assertEquals(
                DateUtils.defaultDateFormat,
                writeCellData.getWriteCellStyle().getDataFormatData().getFormat());
    }

    @Test
    void dateConverterFallsBackToDefaultFormatForEmptyDateTimeFormat() throws Exception {
        TimestampDateConverter converter = new TimestampDateConverter();
        Timestamp timestamp = Timestamp.valueOf("2020-01-01 01:01:01");

        ExcelContentProperty contentProperty = new ExcelContentProperty();
        contentProperty.setDateTimeFormatProperty(new DateTimeFormatProperty("", false));

        WriteCellData<?> writeCellData = converter.convertToExcelData(timestamp, contentProperty, GLOBAL_CONFIGURATION);
        Assertions.assertEquals(
                DateUtils.defaultDateFormat,
                writeCellData.getWriteCellStyle().getDataFormatData().getFormat());
    }

    @Test
    void defaultConverterLoaderRegistersTimestampConverters() {
        Assertions.assertTrue(DefaultConverterLoader.loadAllConverter()
                .containsKey(ConverterKeyBuild.buildKey(Timestamp.class, CellDataTypeEnum.NUMBER)));
        Assertions.assertTrue(DefaultConverterLoader.loadAllConverter()
                .containsKey(ConverterKeyBuild.buildKey(Timestamp.class, CellDataTypeEnum.STRING)));

        Assertions.assertTrue(DefaultConverterLoader.loadDefaultWriteConverter()
                .containsKey(ConverterKeyBuild.buildKey(Timestamp.class)));
        Assertions.assertTrue(DefaultConverterLoader.loadDefaultWriteConverter()
                .containsKey(ConverterKeyBuild.buildKey(Timestamp.class, CellDataTypeEnum.STRING)));
    }
}
