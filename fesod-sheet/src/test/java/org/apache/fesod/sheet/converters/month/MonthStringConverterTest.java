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

package org.apache.fesod.sheet.converters.month;

import java.time.Month;
import java.util.Locale;
import org.apache.fesod.sheet.metadata.GlobalConfiguration;
import org.apache.fesod.sheet.metadata.data.ReadCellData;
import org.apache.fesod.sheet.metadata.data.WriteCellData;
import org.apache.fesod.sheet.metadata.property.DateTimeFormatProperty;
import org.apache.fesod.sheet.metadata.property.ExcelContentProperty;
import org.apache.fesod.sheet.testkit.Tags;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * Tests {@link MonthStringConverter}.
 */
@Tag(Tags.UNIT)
class MonthStringConverterTest {

    private final MonthStringConverter converter = new MonthStringConverter();

    @Test
    void convertToJavaDataReadsFullNameCaseInsensitively() {
        Assertions.assertEquals(
                Month.MARCH, converter.convertToJavaData(new ReadCellData<>("March"), null, configuration(Locale.US)));
        Assertions.assertEquals(
                Month.MARCH, converter.convertToJavaData(new ReadCellData<>("march"), null, configuration(Locale.US)));
    }

    @Test
    void convertToJavaDataReadsNumericMonth() {
        Assertions.assertEquals(
                Month.MARCH, converter.convertToJavaData(new ReadCellData<>("3"), null, configuration(Locale.US)));
    }

    @Test
    void convertToJavaDataRejectsInvalidMonth() {
        Assertions.assertThrows(
                Exception.class,
                () -> converter.convertToJavaData(new ReadCellData<>("13"), null, configuration(Locale.US)));
        Assertions.assertThrows(
                Exception.class,
                () -> converter.convertToJavaData(new ReadCellData<>("nope"), null, configuration(Locale.US)));
    }

    @Test
    void convertToExcelDataWritesLocaleIndependentNumericValue() {
        WriteCellData<?> cellData = converter.convertToExcelData(Month.JANUARY, null, configuration(Locale.US));
        Assertions.assertEquals("1", cellData.getStringValue());
    }

    @Test
    void customPatternIsHonoredOnBothDirections() {
        ExcelContentProperty contentProperty = contentProperty("MMM");

        Assertions.assertEquals(
                Month.MARCH,
                converter.convertToJavaData(new ReadCellData<>("Mar"), contentProperty, configuration(Locale.US)));
        WriteCellData<?> cellData =
                converter.convertToExcelData(Month.MARCH, contentProperty, configuration(Locale.US));
        Assertions.assertEquals("Mar", cellData.getStringValue());
    }

    private GlobalConfiguration configuration(Locale locale) {
        GlobalConfiguration globalConfiguration = new GlobalConfiguration();
        globalConfiguration.setLocale(locale);
        return globalConfiguration;
    }

    private ExcelContentProperty contentProperty(String format) {
        ExcelContentProperty contentProperty = new ExcelContentProperty();
        contentProperty.setDateTimeFormatProperty(new DateTimeFormatProperty(format, null));
        return contentProperty;
    }
}
