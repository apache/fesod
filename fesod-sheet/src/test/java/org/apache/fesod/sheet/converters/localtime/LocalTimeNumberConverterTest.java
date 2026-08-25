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

package org.apache.fesod.sheet.converters.localtime;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.stream.Stream;
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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Tests {@link LocalTimeNumberConverter}.
 */
@Tag(Tags.UNIT)
class LocalTimeNumberConverterTest {

    private static final GlobalConfiguration GLOBAL_CONFIGURATION = new GlobalConfiguration();
    private final LocalTimeNumberConverter converter = new LocalTimeNumberConverter();

    @AfterEach
    void tearDown() {
        DateUtils.removeThreadLocalCache();
    }

    @Test
    void supportKeys() {
        Assertions.assertEquals(LocalTime.class, converter.supportJavaTypeKey());
        Assertions.assertEquals(CellDataTypeEnum.NUMBER, converter.supportExcelTypeKey());
    }

    @ParameterizedTest
    @CsvSource({"0.5, 12:00:00", "1.0, 00:00:00", "43831.5, 12:00:00"})
    void convertToJavaDataReadsSerialAndDropsDate(double serial, String expected) {
        LocalTime actual =
                converter.convertToJavaData(new ReadCellData<>(BigDecimal.valueOf(serial)), null, GLOBAL_CONFIGURATION);

        Assertions.assertEquals(LocalTime.parse(expected), actual);
    }

    @Test
    void convertToJavaDataDropsDateFromFullDatetimeSerial() {
        double serial = DateUtil.getExcelDate(LocalTime.of(1, 1, 1).atDate(LocalDate.of(2020, 1, 1)), false);

        LocalTime actual =
                converter.convertToJavaData(new ReadCellData<>(BigDecimal.valueOf(serial)), null, GLOBAL_CONFIGURATION);

        Assertions.assertEquals(LocalTime.of(1, 1, 1), actual);
    }

    @ParameterizedTest
    @MethodSource("sampleTimes")
    void convertToExcelDataRoundTrip(LocalTime time) {
        WriteCellData<?> written = converter.convertToExcelData(time, null, GLOBAL_CONFIGURATION);
        LocalTime actual =
                converter.convertToJavaData(new ReadCellData<>(written.getNumberValue()), null, GLOBAL_CONFIGURATION);

        Assertions.assertEquals(CellDataTypeEnum.NUMBER, written.getType());
        Assertions.assertEquals(time, actual);
        Assertions.assertEquals(
                DateUtil.getExcelDate(time.atDate(DateUtils.EPOCH), false),
                written.getNumberValue().doubleValue(),
                1e-8);
    }

    @Test
    void convertUsesGlobal1904WindowingWhenContentPropertyHasNoDateTimeFormat() {
        GlobalConfiguration configuration = new GlobalConfiguration();
        configuration.setUse1904windowing(Boolean.TRUE);

        WriteCellData<?> written =
                converter.convertToExcelData(LocalTime.NOON, new ExcelContentProperty(), configuration);
        LocalTime actual =
                converter.convertToJavaData(new ReadCellData<>(written.getNumberValue()), null, configuration);

        Assertions.assertEquals(LocalTime.NOON, actual);
        Assertions.assertEquals(
                DateUtil.getExcelDate(LocalTime.NOON.atDate(DateUtils.EPOCH), true),
                written.getNumberValue().doubleValue(),
                1e-8);
    }

    @Test
    void convertPrefersContentPropertyWindowingOverGlobal() {
        ExcelContentProperty contentProperty = contentProperty(null, Boolean.TRUE);

        WriteCellData<?> written = converter.convertToExcelData(LocalTime.NOON, contentProperty, GLOBAL_CONFIGURATION);
        LocalTime actual = converter.convertToJavaData(
                new ReadCellData<>(written.getNumberValue()), contentProperty, GLOBAL_CONFIGURATION);

        Assertions.assertEquals(LocalTime.NOON, actual);
        Assertions.assertEquals(
                DateUtil.getExcelDate(LocalTime.NOON.atDate(DateUtils.EPOCH), true),
                written.getNumberValue().doubleValue(),
                1e-8);
    }

    static Stream<LocalTime> sampleTimes() {
        return Stream.of(LocalTime.MIDNIGHT, LocalTime.NOON, LocalTime.of(1, 1, 1), LocalTime.of(23, 59, 59));
    }

    private static ExcelContentProperty contentProperty(String format, Boolean use1904windowing) {
        ExcelContentProperty contentProperty = new ExcelContentProperty();
        contentProperty.setDateTimeFormatProperty(new DateTimeFormatProperty(format, use1904windowing));
        return contentProperty;
    }
}
