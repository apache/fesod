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

package org.apache.fesod.sheet.converters.sqldate;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
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
 * Unit tests for java.sql.Date converter implementations.
 */
@Tag(Tags.UNIT)
class SqlDateConverterTest {

    private static final GlobalConfiguration GLOBAL_CONFIGURATION = new GlobalConfiguration();

    @AfterEach
    void tearDown() {
        DateUtils.removeThreadLocalCache();
    }

    @Test
    void supportKeys() {
        Assertions.assertEquals(Date.class, new SqlDateDateConverter().supportJavaTypeKey());

        SqlDateNumberConverter numberConverter = new SqlDateNumberConverter();
        Assertions.assertEquals(Date.class, numberConverter.supportJavaTypeKey());
        Assertions.assertEquals(CellDataTypeEnum.NUMBER, numberConverter.supportExcelTypeKey());

        SqlDateStringConverter stringConverter = new SqlDateStringConverter();
        Assertions.assertEquals(Date.class, stringConverter.supportJavaTypeKey());
        Assertions.assertEquals(CellDataTypeEnum.STRING, stringConverter.supportExcelTypeKey());
    }

    @Test
    void numberConverterReadAndWrite() {
        SqlDateNumberConverter converter = new SqlDateNumberConverter();
        LocalDate localDate = LocalDate.of(2020, 1, 1);
        double excelDate = DateUtil.getExcelDate(localDate);

        Date javaData = converter.convertToJavaData(
                new ReadCellData<>(BigDecimal.valueOf(excelDate)), null, GLOBAL_CONFIGURATION);
        Assertions.assertEquals(Date.valueOf(localDate), javaData);

        WriteCellData<?> writeCellData =
                converter.convertToExcelData(Date.valueOf(localDate), null, GLOBAL_CONFIGURATION);
        Assertions.assertEquals(0, writeCellData.getNumberValue().compareTo(BigDecimal.valueOf(excelDate)));
    }

    @Test
    void numberConverterRespectsUse1904Windowing() {
        SqlDateNumberConverter converter = new SqlDateNumberConverter();
        LocalDate localDate = LocalDate.of(2020, 1, 1);

        ExcelContentProperty contentProperty = new ExcelContentProperty();
        DateTimeFormatProperty dateTimeFormatProperty = new DateTimeFormatProperty(null, true);
        contentProperty.setDateTimeFormatProperty(dateTimeFormatProperty);

        double excelDate1904 = DateUtil.getExcelDate(localDate, true);
        Date javaData = converter.convertToJavaData(
                new ReadCellData<>(BigDecimal.valueOf(excelDate1904)), contentProperty, GLOBAL_CONFIGURATION);
        Assertions.assertEquals(Date.valueOf(localDate), javaData);

        WriteCellData<?> writeCellData =
                converter.convertToExcelData(Date.valueOf(localDate), contentProperty, GLOBAL_CONFIGURATION);
        Assertions.assertEquals(0, writeCellData.getNumberValue().compareTo(BigDecimal.valueOf(excelDate1904)));
    }

    @Test
    void numberConverterFallsBackToGlobalWindowingWhenFormatLeaves1904Unset() {
        SqlDateNumberConverter converter = new SqlDateNumberConverter();
        LocalDate localDate = LocalDate.of(2020, 1, 1);

        ExcelContentProperty contentProperty = new ExcelContentProperty();
        contentProperty.setDateTimeFormatProperty(new DateTimeFormatProperty("yyyy/MM/dd", null));

        double excelDate = DateUtil.getExcelDate(localDate);
        Date javaData = converter.convertToJavaData(
                new ReadCellData<>(BigDecimal.valueOf(excelDate)), contentProperty, GLOBAL_CONFIGURATION);
        Assertions.assertEquals(Date.valueOf(localDate), javaData);

        GlobalConfiguration globalConfiguration1904 = new GlobalConfiguration();
        globalConfiguration1904.setUse1904windowing(Boolean.TRUE);
        double excelDate1904 = DateUtil.getExcelDate(localDate, true);
        Date javaData1904 = converter.convertToJavaData(
                new ReadCellData<>(BigDecimal.valueOf(excelDate1904)), contentProperty, globalConfiguration1904);
        Assertions.assertEquals(Date.valueOf(localDate), javaData1904);

        WriteCellData<?> writeCellData1904 =
                converter.convertToExcelData(Date.valueOf(localDate), contentProperty, globalConfiguration1904);
        Assertions.assertEquals(0, writeCellData1904.getNumberValue().compareTo(BigDecimal.valueOf(excelDate1904)));
    }

    @Test
    void numberConverterReturnsNullForInvalidExcelDate() {
        SqlDateNumberConverter converter = new SqlDateNumberConverter();
        Assertions.assertNull(
                converter.convertToJavaData(new ReadCellData<>(BigDecimal.valueOf(-1)), null, GLOBAL_CONFIGURATION));
    }

    @Test
    void stringConverterReadAndWrite() {
        SqlDateStringConverter converter = new SqlDateStringConverter();
        Date date = Date.valueOf("2020-01-01");

        Date javaData = converter.convertToJavaData(new ReadCellData<>("2020-01-01"), null, GLOBAL_CONFIGURATION);
        Assertions.assertEquals(date, javaData);

        WriteCellData<?> writeCellData = converter.convertToExcelData(date, null, GLOBAL_CONFIGURATION);
        Assertions.assertEquals("2020-01-01", writeCellData.getStringValue());
    }

    @Test
    void stringConverterRespectsDateTimeFormatAndLocale() {
        SqlDateStringConverter converter = new SqlDateStringConverter();
        Date date = Date.valueOf("2020-01-01");

        ExcelContentProperty contentProperty = new ExcelContentProperty();
        contentProperty.setDateTimeFormatProperty(new DateTimeFormatProperty("dd MMMM yyyy", null));

        GlobalConfiguration globalConfiguration = new GlobalConfiguration();
        globalConfiguration.setLocale(Locale.US);

        Date javaData = converter.convertToJavaData(
                new ReadCellData<>("01 January 2020"), contentProperty, globalConfiguration);
        Assertions.assertEquals(date, javaData);

        WriteCellData<?> writeCellData = converter.convertToExcelData(date, contentProperty, globalConfiguration);
        Assertions.assertEquals("01 January 2020", writeCellData.getStringValue());
    }

    @Test
    void dateConverterWrite() {
        SqlDateDateConverter converter = new SqlDateDateConverter();
        Date date = Date.valueOf("2020-01-01");

        WriteCellData<?> writeCellData = converter.convertToExcelData(date, null, GLOBAL_CONFIGURATION);
        Assertions.assertEquals(CellDataTypeEnum.DATE, writeCellData.getType());
        Assertions.assertEquals(date.toLocalDate().atStartOfDay(), writeCellData.getDateValue());
        Assertions.assertEquals(
                DateUtils.defaultLocalDateFormat,
                writeCellData.getWriteCellStyle().getDataFormatData().getFormat());
    }

    @Test
    void dateConverterFallsBackToDefaultFormatForEmptyDateTimeFormat() {
        SqlDateDateConverter converter = new SqlDateDateConverter();
        Date date = Date.valueOf("2020-01-01");

        ExcelContentProperty contentProperty = new ExcelContentProperty();
        contentProperty.setDateTimeFormatProperty(new DateTimeFormatProperty("", false));

        WriteCellData<?> writeCellData = converter.convertToExcelData(date, contentProperty, GLOBAL_CONFIGURATION);
        Assertions.assertEquals(
                DateUtils.defaultLocalDateFormat,
                writeCellData.getWriteCellStyle().getDataFormatData().getFormat());
    }

    @Test
    void defaultConverterLoaderRegistersSqlDateConverters() {
        Assertions.assertTrue(DefaultConverterLoader.loadAllConverter()
                .containsKey(ConverterKeyBuild.buildKey(Date.class, CellDataTypeEnum.NUMBER)));
        Assertions.assertTrue(DefaultConverterLoader.loadAllConverter()
                .containsKey(ConverterKeyBuild.buildKey(Date.class, CellDataTypeEnum.STRING)));

        Assertions.assertTrue(
                DefaultConverterLoader.loadDefaultWriteConverter().containsKey(ConverterKeyBuild.buildKey(Date.class)));
        Assertions.assertTrue(DefaultConverterLoader.loadDefaultWriteConverter()
                .containsKey(ConverterKeyBuild.buildKey(Date.class, CellDataTypeEnum.STRING)));
    }
}
