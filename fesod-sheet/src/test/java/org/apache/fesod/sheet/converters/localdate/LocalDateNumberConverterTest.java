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

package org.apache.fesod.sheet.converters.localdate;

import java.math.BigDecimal;
import java.time.LocalDate;
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
 * Tests {@link LocalDateNumberConverter}.
 */
@Tag(Tags.UNIT)
class LocalDateNumberConverterTest {

    private final LocalDateNumberConverter converter = new LocalDateNumberConverter();

    @AfterEach
    void tearDown() {
        DateUtils.removeThreadLocalCache();
    }

    @Test
    void convertToJavaDataFallsBackToGlobal1904WindowingWhenAnnotationWindowingIsDefault() {
        GlobalConfiguration configuration = new GlobalConfiguration();
        configuration.setUse1904windowing(Boolean.TRUE);
        ExcelContentProperty contentProperty = contentProperty("yyyy-MM-dd", null);

        LocalDate actual =
                converter.convertToJavaData(new ReadCellData<>(BigDecimal.ONE), contentProperty, configuration);

        Assertions.assertEquals(DateUtils.getLocalDate(1, true), actual);
    }

    @Test
    void convertToExcelDataFallsBackToGlobal1904WindowingWhenAnnotationWindowingIsDefault() {
        GlobalConfiguration configuration = new GlobalConfiguration();
        configuration.setUse1904windowing(Boolean.TRUE);
        ExcelContentProperty contentProperty = contentProperty("yyyy-MM-dd", null);
        LocalDate value = DateUtils.getLocalDate(1, true);

        WriteCellData<?> written = converter.convertToExcelData(value, contentProperty, configuration);

        Assertions.assertEquals(
                DateUtil.getExcelDate(value, true), written.getNumberValue().doubleValue(), 1e-8);
    }

    @Test
    void convertPrefersExplicitAnnotationWindowingOverGlobal() {
        GlobalConfiguration configuration = new GlobalConfiguration();
        configuration.setUse1904windowing(Boolean.TRUE);
        ExcelContentProperty contentProperty = contentProperty("yyyy-MM-dd", Boolean.FALSE);

        LocalDate actual =
                converter.convertToJavaData(new ReadCellData<>(BigDecimal.ONE), contentProperty, configuration);

        Assertions.assertEquals(DateUtils.getLocalDate(1, false), actual);
    }

    private static ExcelContentProperty contentProperty(String format, Boolean use1904windowing) {
        ExcelContentProperty contentProperty = new ExcelContentProperty();
        contentProperty.setDateTimeFormatProperty(new DateTimeFormatProperty(format, use1904windowing));
        return contentProperty;
    }
}
