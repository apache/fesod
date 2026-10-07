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

package org.apache.fesod.sheet.converters.date;

import java.text.ParseException;
import java.util.Date;
import org.apache.fesod.sheet.metadata.GlobalConfiguration;
import org.apache.fesod.sheet.metadata.data.ReadCellData;
import org.apache.fesod.sheet.testkit.Tags;
import org.apache.fesod.sheet.util.DateUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Tests {@link DateStringConverter}.
 */
@Tag(Tags.UNIT)
class DateStringConverterTest {

    private static final GlobalConfiguration GLOBAL_CONFIGURATION = new GlobalConfiguration();
    private final DateStringConverter converter = new DateStringConverter();

    @ParameterizedTest
    @ValueSource(strings = {"2024-02-31", "2023-02-29", "2024-13-01"})
    void convertToJavaDataRejectsInvalidCalendarDates(String value) {
        Assertions.assertThrows(
                ParseException.class,
                () -> converter.convertToJavaData(new ReadCellData<>(value), null, GLOBAL_CONFIGURATION));
    }

    @ParameterizedTest
    @ValueSource(strings = {"2024-02-29", "2023-02-28"})
    void convertToJavaDataAcceptsValidCalendarDates(String value) throws ParseException {
        Date parsed = converter.convertToJavaData(new ReadCellData<>(value), null, GLOBAL_CONFIGURATION);

        Assertions.assertEquals(value, DateUtils.format(parsed, DateUtils.DATE_FORMAT_10));
    }
}
