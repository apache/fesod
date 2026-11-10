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

package org.apache.fesod.sheet.converters.monthday;

import java.time.MonthDay;
import java.time.format.DateTimeParseException;
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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Tests {@link MonthDayStringConverter}.
 */
@Tag(Tags.UNIT)
class MonthDayStringConverterTest {

    private static final GlobalConfiguration GLOBAL_CONFIGURATION = new GlobalConfiguration();
    private final MonthDayStringConverter converter = new MonthDayStringConverter();

    @AfterEach
    void tearDown() {
        DateUtils.removeThreadLocalCache();
    }

    @ParameterizedTest
    @ValueSource(strings = {"--10-31", "10-31"})
    void convertToJavaDataAcceptsValidValues(String value) {
        MonthDay parsed = converter.convertToJavaData(new ReadCellData<>(value), null, GLOBAL_CONFIGURATION);

        Assertions.assertEquals(MonthDay.of(10, 31), parsed);
    }

    @ParameterizedTest
    @ValueSource(strings = {"2024-10-31", "13-01", "--02-30", "abc"})
    void convertToJavaDataRejectsInvalidValues(String value) {
        Assertions.assertThrows(
                DateTimeParseException.class,
                () -> converter.convertToJavaData(new ReadCellData<>(value), null, GLOBAL_CONFIGURATION));
    }

    @Test
    void convertToJavaDataSupportsLeapDay() {
        MonthDay parsed = converter.convertToJavaData(new ReadCellData<>("--02-29"), null, GLOBAL_CONFIGURATION);

        Assertions.assertEquals(MonthDay.of(2, 29), parsed);
    }

    @Test
    void convertToExcelDataWritesIsoMonthDay() {
        WriteCellData<?> cellData = converter.convertToExcelData(MonthDay.of(10, 31), null, GLOBAL_CONFIGURATION);

        Assertions.assertEquals("--10-31", cellData.getStringValue());
    }

    @Test
    void convertToExcelDataUsesConfiguredPattern() {
        WriteCellData<?> cellData =
                converter.convertToExcelData(MonthDay.of(10, 31), propertyWithFormat("MM/dd"), GLOBAL_CONFIGURATION);

        Assertions.assertEquals("10/31", cellData.getStringValue());
    }

    @Test
    void convertToJavaDataReadsConfiguredPattern() {
        MonthDay parsed = converter.convertToJavaData(
                new ReadCellData<>("10/31"), propertyWithFormat("MM/dd"), GLOBAL_CONFIGURATION);

        Assertions.assertEquals(MonthDay.of(10, 31), parsed);
    }

    @Test
    void convertToJavaDataRejectsTextNotMatchingConfiguredPattern() {
        ExcelContentProperty property = propertyWithFormat("MM/dd");

        Assertions.assertThrows(
                DateTimeParseException.class,
                () -> converter.convertToJavaData(new ReadCellData<>("--10-31"), property, GLOBAL_CONFIGURATION));
    }

    @Test
    void blankOrNullPatternFallsBackToIso() {
        for (String blankFormat : new String[] {"", null}) {
            ExcelContentProperty property = propertyWithFormat(blankFormat);

            Assertions.assertEquals(
                    "--10-31",
                    converter
                            .convertToExcelData(MonthDay.of(10, 31), property, GLOBAL_CONFIGURATION)
                            .getStringValue());
            Assertions.assertEquals(
                    MonthDay.of(10, 31),
                    converter.convertToJavaData(new ReadCellData<>("--10-31"), property, GLOBAL_CONFIGURATION));
        }
    }

    @Test
    void configuredPatternHonoursGlobalLocale() {
        ExcelContentProperty property = propertyWithFormat("MMMM d");
        GlobalConfiguration usConfiguration = new GlobalConfiguration();
        usConfiguration.setLocale(Locale.US);
        GlobalConfiguration frenchConfiguration = new GlobalConfiguration();
        frenchConfiguration.setLocale(Locale.FRENCH);

        Assertions.assertEquals(
                "March 15",
                converter
                        .convertToExcelData(MonthDay.of(3, 15), property, usConfiguration)
                        .getStringValue());
        Assertions.assertEquals(
                "mars 15",
                converter
                        .convertToExcelData(MonthDay.of(3, 15), property, frenchConfiguration)
                        .getStringValue());
        Assertions.assertEquals(
                MonthDay.of(3, 15),
                converter.convertToJavaData(new ReadCellData<>("March 15"), property, usConfiguration));
        Assertions.assertEquals(
                MonthDay.of(3, 15),
                converter.convertToJavaData(new ReadCellData<>("mars 15"), property, frenchConfiguration));
    }

    @ParameterizedTest
    @ValueSource(strings = {"--01-01", "--02-29", "--12-31"})
    void writeAndReadRoundTripsBoundaryValues(String isoText) {
        WriteCellData<?> written = converter.convertToExcelData(MonthDay.parse(isoText), null, GLOBAL_CONFIGURATION);

        Assertions.assertEquals(
                MonthDay.parse(isoText),
                converter.convertToJavaData(new ReadCellData<>(written.getStringValue()), null, GLOBAL_CONFIGURATION));
    }

    @Test
    void converterIsRegisteredForBothReadAndWrite() {
        Assertions.assertEquals(
                MonthDayStringConverter.class,
                DefaultConverterLoader.loadAllConverter()
                        .get(ConverterKeyBuild.buildKey(MonthDay.class, CellDataTypeEnum.STRING))
                        .getClass());
        Assertions.assertEquals(
                MonthDayStringConverter.class,
                DefaultConverterLoader.loadDefaultWriteConverter()
                        .get(ConverterKeyBuild.buildKey(MonthDay.class))
                        .getClass());
        Assertions.assertEquals(
                MonthDayStringConverter.class,
                DefaultConverterLoader.loadDefaultWriteConverter()
                        .get(ConverterKeyBuild.buildKey(MonthDay.class, CellDataTypeEnum.STRING))
                        .getClass());
        Assertions.assertEquals(
                1,
                DefaultConverterLoader.loadAllConverter().entrySet().stream()
                        .filter(entry -> entry.getKey().getClazz() == MonthDay.class)
                        .count());
    }

    private static ExcelContentProperty propertyWithFormat(String format) {
        ExcelContentProperty property = new ExcelContentProperty();
        property.setDateTimeFormatProperty(new DateTimeFormatProperty(format, false));
        return property;
    }
}
