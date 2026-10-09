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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Tests {@link LocalTimeDateConverter}.
 */
@Tag(Tags.UNIT)
class LocalTimeDateConverterTest {

    private static final GlobalConfiguration GLOBAL_CONFIGURATION = new GlobalConfiguration();
    private final LocalTimeDateConverter converter = new LocalTimeDateConverter();

    @AfterEach
    void tearDown() {
        DateUtils.removeThreadLocalCache();
    }

    @Test
    void supportJavaTypeKey() {
        Assertions.assertEquals(LocalTime.class, converter.supportJavaTypeKey());
    }

    @Test
    void convertToJavaDataIsUnsupported() {
        Assertions.assertThrows(
                UnsupportedOperationException.class,
                () -> converter.convertToJavaData(new ReadCellData<>("01:01:01"), null, GLOBAL_CONFIGURATION));
    }

    @ParameterizedTest
    @MethodSource("sampleTimes")
    void convertToExcelDataUsesEpochDateAndDefaultFormat(LocalTime time) throws Exception {
        WriteCellData<?> cellData = converter.convertToExcelData(time, null, GLOBAL_CONFIGURATION);

        Assertions.assertEquals(CellDataTypeEnum.DATE, cellData.getType());
        Assertions.assertEquals(time.atDate(DateUtils.EPOCH), cellData.getDateValue());
        Assertions.assertEquals(
                DateUtils.DEFAULT_LOCAL_TIME_FORMAT,
                cellData.getWriteCellStyle().getDataFormatData().getFormat());
    }

    @Test
    void convertToExcelDataFallsBackToDefaultFormatWhenContentPropertyHasNoDateTimeFormat() throws Exception {
        WriteCellData<?> cellData =
                converter.convertToExcelData(LocalTime.NOON, new ExcelContentProperty(), GLOBAL_CONFIGURATION);

        Assertions.assertEquals(
                DateUtils.DEFAULT_LOCAL_TIME_FORMAT,
                cellData.getWriteCellStyle().getDataFormatData().getFormat());
    }

    @Test
    void convertToExcelDataUsesCustomFormat() throws Exception {
        ExcelContentProperty contentProperty = contentProperty(DateUtils.TIME_FORMAT_5, Boolean.FALSE);

        WriteCellData<?> cellData =
                converter.convertToExcelData(LocalTime.of(1, 1, 1), contentProperty, GLOBAL_CONFIGURATION);

        Assertions.assertEquals(LocalTime.of(1, 1, 1).atDate(DateUtils.EPOCH), cellData.getDateValue());
        Assertions.assertEquals(
                DateUtils.TIME_FORMAT_5,
                cellData.getWriteCellStyle().getDataFormatData().getFormat());
    }

    @Test
    void convertToExcelDataRejectsNullValue() {
        Assertions.assertThrows(
                IllegalArgumentException.class, () -> converter.convertToExcelData(null, null, GLOBAL_CONFIGURATION));
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
