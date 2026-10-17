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

import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
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
 * Tests {@link LocalDateStringConverter}.
 */
@Tag(Tags.UNIT)
class LocalDateStringConverterTest {

    private final LocalDateStringConverter converter = new LocalDateStringConverter();

    @AfterEach
    void tearDown() {
        DateUtils.removeThreadLocalCache();
    }

    @Test
    void convertToJavaDataRejectsInvalidMonthEndInsteadOfNormalizingIt() {
        Assertions.assertThrows(
                DateTimeParseException.class,
                () -> converter.convertToJavaData(new ReadCellData<>("2024-02-31"), null, new GlobalConfiguration()));
    }

    @Test
    void convertToJavaDataRejectsInvalidLeapDayInsteadOfNormalizingIt() {
        Assertions.assertThrows(
                DateTimeParseException.class,
                () -> converter.convertToJavaData(new ReadCellData<>("2023-02-29"), null, new GlobalConfiguration()));
    }

    @Test
    void convertToJavaDataAcceptsValidLeapDay() throws ParseException {
        LocalDate actual =
                converter.convertToJavaData(new ReadCellData<>("2024-02-29"), null, new GlobalConfiguration());
        Assertions.assertEquals(LocalDate.of(2024, 2, 29), actual);
    }

    @Test
    void convertToJavaDataRejectsInvalidDayWithCustomPattern() {
        ExcelContentProperty contentProperty = new ExcelContentProperty();
        contentProperty.setDateTimeFormatProperty(new DateTimeFormatProperty("yyyy/MM/dd", null));

        Assertions.assertThrows(
                DateTimeParseException.class, () -> convert(new ReadCellData<>("2024/02/31"), contentProperty));
    }

    @Test
    void convertToJavaDataAcceptsValidDayWithCustomPattern() throws ParseException {
        ExcelContentProperty contentProperty = new ExcelContentProperty();
        contentProperty.setDateTimeFormatProperty(new DateTimeFormatProperty("yyyy/MM/dd", null));

        LocalDate actual = convert(new ReadCellData<>("2024/02/29"), contentProperty);
        Assertions.assertEquals(LocalDate.of(2024, 2, 29), actual);
    }

    private LocalDate convert(ReadCellData<?> cellData, ExcelContentProperty contentProperty) throws ParseException {
        return converter.convertToJavaData(cellData, contentProperty, new GlobalConfiguration());
    }
}
