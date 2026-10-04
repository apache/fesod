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
import org.apache.fesod.sheet.metadata.data.ReadCellData;
import org.apache.fesod.sheet.testkit.Tags;
import org.apache.fesod.sheet.util.DateUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * Tests {@link DateStringConverter}.
 */
@Tag(Tags.UNIT)
class DateStringConverterTest {

    private final DateStringConverter converter = new DateStringConverter();

    @AfterEach
    void tearDown() {
        DateUtils.removeThreadLocalCache();
    }

    @Test
    void convertToJavaDataRejectsInvalidMonthEndInsteadOfNormalizingIt() {
        Assertions.assertThrows(
                ParseException.class, () -> converter.convertToJavaData(new ReadCellData<>("2024-02-31"), null, null));
    }

    @Test
    void convertToJavaDataRejectsInvalidLeapDayInsteadOfNormalizingIt() {
        Assertions.assertThrows(
                ParseException.class, () -> converter.convertToJavaData(new ReadCellData<>("2023-02-29"), null, null));
    }

    @Test
    void convertToJavaDataRejectsInvalidDayInDateTimeInsteadOfNormalizingIt() {
        Assertions.assertThrows(
                ParseException.class,
                () -> converter.convertToJavaData(new ReadCellData<>("2024-02-31 10:20:30"), null, null));
    }

    @Test
    void convertToJavaDataAcceptsValidLeapDay() throws ParseException {
        String formatted = DateUtils.format(
                converter.convertToJavaData(new ReadCellData<>("2024-02-29"), null, null), "yyyy-MM-dd");
        Assertions.assertEquals("2024-02-29", formatted);
    }
}
