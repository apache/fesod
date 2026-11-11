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
import org.apache.fesod.sheet.converters.ConverterKeyBuild;
import org.apache.fesod.sheet.converters.DefaultConverterLoader;
import org.apache.fesod.sheet.enums.CellDataTypeEnum;
import org.apache.fesod.sheet.metadata.GlobalConfiguration;
import org.apache.fesod.sheet.metadata.data.ReadCellData;
import org.apache.fesod.sheet.metadata.data.WriteCellData;
import org.apache.fesod.sheet.metadata.property.DateTimeFormatProperty;
import org.apache.fesod.sheet.metadata.property.ExcelContentProperty;
import org.apache.fesod.sheet.testkit.Tags;
import org.apache.poi.ss.usermodel.DateUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag(Tags.UNIT)
class SqlDateConverterTest {
    private final GlobalConfiguration config = new GlobalConfiguration();

    @Test
    void registryContainsConverters() {
        Assertions.assertTrue(DefaultConverterLoader.loadDefaultReadConverter()
                .containsKey(ConverterKeyBuild.buildKey(Date.class, CellDataTypeEnum.NUMBER)));
        Assertions.assertTrue(DefaultConverterLoader.loadDefaultReadConverter()
                .containsKey(ConverterKeyBuild.buildKey(Date.class, CellDataTypeEnum.STRING)));
        Assertions.assertTrue(
                DefaultConverterLoader.loadDefaultWriteConverter().containsKey(ConverterKeyBuild.buildKey(Date.class)));
    }

    @Test
    void dateNumberAndStringConversions() throws Exception {
        Date date = Date.valueOf("2020-06-15");

        WriteCellData<?> dateCell = new SqlDateDateConverter().convertToExcelData(date, null, config);
        Assertions.assertEquals(date.toLocalDate().atTime(0, 0), dateCell.getDateValue());

        SqlDateNumberConverter number = new SqlDateNumberConverter();
        WriteCellData<?> numeric = number.convertToExcelData(date, null, config);
        Assertions.assertEquals(
                date, number.convertToJavaData(new ReadCellData<>(numeric.getNumberValue()), null, config));

        SqlDateStringConverter string = new SqlDateStringConverter();
        WriteCellData<?> text = string.convertToExcelData(date, null, config);
        Assertions.assertEquals(
                date, string.convertToJavaData(new ReadCellData<>(text.getStringValue()), null, config));

        Assertions.assertEquals(
                date,
                number.convertToJavaData(
                        new ReadCellData<>(BigDecimal.valueOf(DateUtil.getExcelDate(LocalDate.of(2020, 6, 15), false))),
                        null,
                        config));
    }

    @Test
    void invalidNumberReturnsNull() {
        Assertions.assertNull(new SqlDateNumberConverter()
                .convertToJavaData(new ReadCellData<>(BigDecimal.valueOf(-1)), null, config));
    }

    @Test
    void supportsCustomFormatAnd1904Windowing() {
        ExcelContentProperty property = new ExcelContentProperty();
        property.setDateTimeFormatProperty(new DateTimeFormatProperty("yyyy/MM/dd", Boolean.TRUE));
        Date date = Date.valueOf("2020-06-15");

        WriteCellData<?> text = new SqlDateStringConverter().convertToExcelData(date, property, config);
        Assertions.assertEquals("2020/06/15", text.getStringValue());

        WriteCellData<?> numeric = new SqlDateNumberConverter().convertToExcelData(date, property, config);
        Assertions.assertEquals(
                DateUtil.getExcelDate(date.toLocalDate(), true),
                numeric.getNumberValue().doubleValue(),
                1e-8);
    }
}
