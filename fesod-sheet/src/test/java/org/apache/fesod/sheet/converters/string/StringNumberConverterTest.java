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

package org.apache.fesod.sheet.converters.string;

import java.math.BigDecimal;
import org.apache.fesod.sheet.metadata.GlobalConfiguration;
import org.apache.fesod.sheet.metadata.data.ReadCellData;
import org.apache.fesod.sheet.metadata.property.DateTimeFormatProperty;
import org.apache.fesod.sheet.metadata.property.ExcelContentProperty;
import org.apache.fesod.sheet.testkit.Tags;
import org.apache.fesod.sheet.util.DateUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * Tests {@link StringNumberConverter}.
 */
@Tag(Tags.UNIT)
class StringNumberConverterTest {

    private final StringNumberConverter converter = new StringNumberConverter();

    @AfterEach
    void tearDown() {
        DateUtils.removeThreadLocalCache();
    }

    @Test
    void convertToJavaDataFallsBackToGlobal1904WindowingWhenAnnotationWindowingIsDefault() {
        GlobalConfiguration configuration = new GlobalConfiguration();
        configuration.setUse1904windowing(Boolean.TRUE);
        ExcelContentProperty contentProperty = contentProperty("yyyy-MM-dd", null);

        String actual = converter.convertToJavaData(new ReadCellData<>(BigDecimal.ONE), contentProperty, configuration);

        Assertions.assertEquals("1904-01-02", actual);
    }

    @Test
    void convertToJavaDataPrefersExplicitAnnotationWindowingOverGlobal() {
        GlobalConfiguration configuration = new GlobalConfiguration();
        configuration.setUse1904windowing(Boolean.TRUE);
        ExcelContentProperty contentProperty = contentProperty("yyyy-MM-dd", Boolean.FALSE);

        String actual = converter.convertToJavaData(new ReadCellData<>(BigDecimal.ONE), contentProperty, configuration);

        Assertions.assertEquals("1900-01-01", actual);
    }

    private static ExcelContentProperty contentProperty(String format, Boolean use1904windowing) {
        ExcelContentProperty contentProperty = new ExcelContentProperty();
        contentProperty.setDateTimeFormatProperty(new DateTimeFormatProperty(format, use1904windowing));
        return contentProperty;
    }
}
