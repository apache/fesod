/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.apache.fesod.sheet.converters.sqltime;

import java.math.BigDecimal;
import java.sql.Time;
import java.time.LocalDate;
import org.apache.fesod.sheet.converters.ConverterKeyBuild;
import org.apache.fesod.sheet.converters.DefaultConverterLoader;
import org.apache.fesod.sheet.enums.CellDataTypeEnum;
import org.apache.fesod.sheet.metadata.GlobalConfiguration;
import org.apache.fesod.sheet.metadata.data.ReadCellData;
import org.apache.fesod.sheet.metadata.data.WriteCellData;
import org.apache.fesod.sheet.metadata.property.DateTimeFormatProperty;
import org.apache.fesod.sheet.metadata.property.ExcelContentProperty;
import org.apache.poi.ss.usermodel.DateUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class SqlTimeConverterTest {
    private final GlobalConfiguration config = new GlobalConfiguration();

    @Test
    void registryContainsConverters() {
        Assertions.assertTrue(DefaultConverterLoader.loadDefaultReadConverter()
                .containsKey(ConverterKeyBuild.buildKey(Time.class, CellDataTypeEnum.NUMBER)));
        Assertions.assertTrue(DefaultConverterLoader.loadDefaultReadConverter()
                .containsKey(ConverterKeyBuild.buildKey(Time.class, CellDataTypeEnum.STRING)));
        Assertions.assertTrue(
                DefaultConverterLoader.loadDefaultWriteConverter().containsKey(ConverterKeyBuild.buildKey(Time.class)));
    }

    @Test
    void dateNumberAndStringConversions() throws Exception {
        Time time = Time.valueOf("12:34:56");
        WriteCellData<?> date = new SqlTimeDateConverter().convertToExcelData(time, null, config);
        Assertions.assertEquals(
                time.toLocalTime().atDate(org.apache.fesod.sheet.util.DateUtils.EPOCH), date.getDateValue());
        SqlTimeNumberConverter number = new SqlTimeNumberConverter();
        WriteCellData<?> numeric = number.convertToExcelData(time, null, config);
        Assertions.assertEquals(
                time, number.convertToJavaData(new ReadCellData<>(numeric.getNumberValue()), null, config));
        SqlTimeStringConverter string = new SqlTimeStringConverter();
        WriteCellData<?> text = string.convertToExcelData(time, null, config);
        Assertions.assertEquals(
                time, string.convertToJavaData(new ReadCellData<>(text.getStringValue()), null, config));
        Assertions.assertEquals(
                time,
                number.convertToJavaData(
                        new ReadCellData<>(BigDecimal.valueOf(
                                DateUtil.getExcelDate(LocalDate.of(2020, 1, 1).atTime(time.toLocalTime()), false))),
                        null,
                        config));
    }

    @Test
    void supportsCustomFormatAnd1904Windowing() {
        ExcelContentProperty property = new ExcelContentProperty();
        property.setDateTimeFormatProperty(new DateTimeFormatProperty("HH:mm", Boolean.TRUE));
        Time time = Time.valueOf("12:30:00");
        WriteCellData<?> text = new SqlTimeStringConverter().convertToExcelData(time, property, config);
        Assertions.assertEquals("12:30", text.getStringValue());
        WriteCellData<?> numeric = new SqlTimeNumberConverter().convertToExcelData(time, property, config);
        Assertions.assertEquals(
                DateUtil.getExcelDate(time.toLocalTime().atDate(org.apache.fesod.sheet.util.DateUtils.EPOCH), true),
                numeric.getNumberValue().doubleValue(),
                1e-8);
    }
}
