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

package org.apache.fesod.sheet.converters.year;

import java.time.DateTimeException;
import java.time.Year;
import java.util.Locale;
import org.apache.fesod.sheet.enums.CellDataTypeEnum;
import org.apache.fesod.sheet.metadata.GlobalConfiguration;
import org.apache.fesod.sheet.metadata.data.ReadCellData;
import org.apache.fesod.sheet.metadata.data.WriteCellData;
import org.apache.fesod.sheet.metadata.property.DateTimeFormatProperty;
import org.apache.fesod.sheet.metadata.property.ExcelContentProperty;
import org.apache.fesod.sheet.testkit.Tags;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag(Tags.UNIT)
class YearStringConverterTest {

    private static final GlobalConfiguration GLOBAL_CONFIGURATION = new GlobalConfiguration();
    private final YearStringConverter converter = new YearStringConverter();

    @Test
    void supportJavaTypeKey() {
        Assertions.assertEquals(Year.class, converter.supportJavaTypeKey());
    }

    @Test
    void supportExcelTypeKey() {
        Assertions.assertEquals(CellDataTypeEnum.STRING, converter.supportExcelTypeKey());
    }

    @Test
    void convertToJavaDataUsesDefaultFormat() {
        Assertions.assertEquals(
                Year.of(2026), converter.convertToJavaData(new ReadCellData<>("2026"), null, GLOBAL_CONFIGURATION));
    }

    @Test
    void convertToJavaDataThrowsOnInvalidValue() {
        Assertions.assertThrows(
                DateTimeException.class,
                () -> converter.convertToJavaData(new ReadCellData<>("not-a-year"), null, GLOBAL_CONFIGURATION));
    }

    @Test
    void convertToExcelDataUsesDefaultFormat() {
        WriteCellData<?> cellData = converter.convertToExcelData(Year.of(2026), null, GLOBAL_CONFIGURATION);
        Assertions.assertEquals(CellDataTypeEnum.STRING, cellData.getType());
        Assertions.assertEquals("2026", cellData.getStringValue());
    }

    @Test
    void convertToExcelDataUsesCustomFormat() {
        ExcelContentProperty contentProperty = new ExcelContentProperty();
        contentProperty.setDateTimeFormatProperty(new DateTimeFormatProperty("'Y'yyyy", null));
        WriteCellData<?> cellData = converter.convertToExcelData(Year.of(2026), contentProperty, GLOBAL_CONFIGURATION);
        Assertions.assertEquals("Y2026", cellData.getStringValue());
    }

    @Test
    void convertToExcelDataRespectsLocaleForEraPatterns() {
        GlobalConfiguration chinaConfiguration = new GlobalConfiguration();
        chinaConfiguration.setLocale(Locale.CHINA);
        GlobalConfiguration usConfiguration = new GlobalConfiguration();
        usConfiguration.setLocale(Locale.US);
        ExcelContentProperty contentProperty = new ExcelContentProperty();
        contentProperty.setDateTimeFormatProperty(new DateTimeFormatProperty("G y", null));

        WriteCellData<?> chinaCellData =
                converter.convertToExcelData(Year.of(2026), contentProperty, chinaConfiguration);
        Assertions.assertEquals("公元 2026", chinaCellData.getStringValue());

        WriteCellData<?> usCellData = converter.convertToExcelData(Year.of(2026), contentProperty, usConfiguration);
        Assertions.assertEquals("AD 2026", usCellData.getStringValue());
    }
}
