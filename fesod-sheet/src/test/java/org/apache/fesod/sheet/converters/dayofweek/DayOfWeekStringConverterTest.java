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

package org.apache.fesod.sheet.converters.dayofweek;

import java.time.DayOfWeek;
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
 * Tests {@link DayOfWeekStringConverter}.
 */
@Tag(Tags.UNIT)
class DayOfWeekStringConverterTest {

    private final DayOfWeekStringConverter converter = new DayOfWeekStringConverter();

    @Test
    void convertToJavaDataReadsFullNameCaseInsensitively() {
        Assertions.assertEquals(
                DayOfWeek.MONDAY,
                converter.convertToJavaData(new ReadCellData<>("Monday"), null, configuration(Locale.US)));
        Assertions.assertEquals(
                DayOfWeek.MONDAY,
                converter.convertToJavaData(new ReadCellData<>("monday"), null, configuration(Locale.US)));
    }

    @Test
    void convertToJavaDataReadsIsoNumericDay() {
        Assertions.assertEquals(
                DayOfWeek.MONDAY, converter.convertToJavaData(new ReadCellData<>("1"), null, configuration(Locale.US)));
        Assertions.assertEquals(
                DayOfWeek.SUNDAY, converter.convertToJavaData(new ReadCellData<>("7"), null, configuration(Locale.US)));
    }

    @Test
    void convertToJavaDataRejectsInvalidDayOfWeek() {
        Assertions.assertThrows(
                Exception.class,
                () -> converter.convertToJavaData(new ReadCellData<>("8"), null, configuration(Locale.US)));
        Assertions.assertThrows(
                Exception.class,
                () -> converter.convertToJavaData(new ReadCellData<>("nope"), null, configuration(Locale.US)));
    }

    @Test
    void convertToExcelDataWritesLocaleIndependentIsoValue() {
        WriteCellData<?> cellData = converter.convertToExcelData(DayOfWeek.FRIDAY, null, configuration(Locale.US));
        Assertions.assertEquals("5", cellData.getStringValue());
    }

    @Test
    void customPatternIsHonoredOnBothDirections() {
        ExcelContentProperty contentProperty = contentProperty("EEE");

        Assertions.assertEquals(
                DayOfWeek.MONDAY,
                converter.convertToJavaData(new ReadCellData<>("Mon"), contentProperty, configuration(Locale.US)));
        WriteCellData<?> cellData =
                converter.convertToExcelData(DayOfWeek.MONDAY, contentProperty, configuration(Locale.US));
        Assertions.assertEquals("Mon", cellData.getStringValue());
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
