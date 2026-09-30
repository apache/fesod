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

/**
 * Tests SQL date converters.
 */
@Tag(Tags.UNIT)
class SqlDateConverterTest {

    @AfterEach
    void tearDown() {
        DateUtils.removeThreadLocalCache();
    }

    @Test
    void dateConverterSupportsSqlDate() {
        SqlDateDateConverter converter = new SqlDateDateConverter();

        Assertions.assertEquals(Date.class, converter.supportJavaTypeKey());
    }

    @Test
    void dateConverterConvertsToExcelData() throws Exception {
        SqlDateDateConverter converter = new SqlDateDateConverter();
        ExcelContentProperty contentProperty = contentProperty("yyyy-MM-dd");
        Date value = Date.valueOf("2026-09-06");

        WriteCellData<?> actual = converter.convertToExcelData(value, contentProperty, new GlobalConfiguration());

        Assertions.assertEquals(value.toLocalDate().atStartOfDay(), actual.getDateValue());
    }

    @Test
    void numberConverterConvertsToJavaData() {
        SqlDateNumberConverter converter = new SqlDateNumberConverter();
        ReadCellData<?> cellData = new ReadCellData<>(BigDecimal.ONE);

        Date actual = converter.convertToJavaData(cellData, null, new GlobalConfiguration());

        Assertions.assertNotNull(actual);
        Assertions.assertEquals(Date.class, actual.getClass());
        Assertions.assertEquals(CellDataTypeEnum.NUMBER, converter.supportExcelTypeKey());
    }

    @Test
    void numberConverterUses1904Windowing() {
        SqlDateNumberConverter converter = new SqlDateNumberConverter();
        GlobalConfiguration configuration = new GlobalConfiguration();
        configuration.setUse1904windowing(Boolean.TRUE);
        ReadCellData<?> cellData = new ReadCellData<>(BigDecimal.ONE);

        Date actual = converter.convertToJavaData(cellData, null, configuration);

        Assertions.assertEquals(new Date(DateUtils.getJavaDate(1, true).getTime()), actual);
    }

    @Test
    void numberConverterPrefersExplicitWindowingOverGlobal() {
        SqlDateNumberConverter converter = new SqlDateNumberConverter();
        GlobalConfiguration configuration = new GlobalConfiguration();
        configuration.setUse1904windowing(Boolean.TRUE);
        ExcelContentProperty contentProperty = contentProperty("yyyy-MM-dd", Boolean.FALSE);

        Date actual = converter.convertToJavaData(new ReadCellData<>(BigDecimal.ONE), contentProperty, configuration);

        Assertions.assertEquals(new Date(DateUtils.getJavaDate(1, false).getTime()), actual);
    }

    @Test
    void stringConverterConvertsToJavaData() throws Exception {
        SqlDateStringConverter converter = new SqlDateStringConverter();
        ExcelContentProperty contentProperty = contentProperty("yyyy-MM-dd");
        ReadCellData<?> cellData = new ReadCellData<>("2026-09-06");

        Date actual = converter.convertToJavaData(cellData, contentProperty, new GlobalConfiguration());

        Assertions.assertEquals(Date.valueOf("2026-09-06"), actual);
        Assertions.assertEquals(CellDataTypeEnum.STRING, converter.supportExcelTypeKey());
    }

    @Test
    void stringConverterConvertsToExcelData() {
        SqlDateStringConverter converter = new SqlDateStringConverter();
        ExcelContentProperty contentProperty = contentProperty("yyyy-MM-dd");

        WriteCellData<?> actual =
                converter.convertToExcelData(Date.valueOf("2026-09-06"), contentProperty, new GlobalConfiguration());

        Assertions.assertEquals("2026-09-06", actual.getStringValue());
    }

    private static ExcelContentProperty contentProperty(String format) {
        return contentProperty(format, null);
    }

    private static ExcelContentProperty contentProperty(String format, Boolean use1904windowing) {
        ExcelContentProperty contentProperty = new ExcelContentProperty();
        contentProperty.setDateTimeFormatProperty(new DateTimeFormatProperty(format, use1904windowing));
        return contentProperty;
    }
}
