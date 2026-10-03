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

package org.apache.fesod.sheet.converter;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import lombok.Data;
import org.apache.fesod.sheet.exception.ExcelDataConvertException;
import org.apache.fesod.sheet.testkit.Tags;
import org.apache.fesod.sheet.testkit.base.AbstractExcelTest;
import org.apache.fesod.sheet.testkit.enums.ExcelFormat;
import org.apache.fesod.sheet.testkit.helpers.RoundTripHelper;
import org.apache.fesod.sheet.testkit.params.ExcelFormatSource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;

@Tag(Tags.ROUND_TRIP)
class UUIDConverterRoundTripTest extends AbstractExcelTest {

    private static final String TEXT = "123e4567-e89b-12d3-a456-426614174000";

    @ParameterizedTest
    @ExcelFormatSource
    void roundTripsUuidAndNullFieldsWithoutCustomRegistration(ExcelFormat format) throws Exception {
        File file = createTempFile(format);
        UuidData first = new UuidData();
        first.setId(UUID.fromString(TEXT));
        first.setLabel("populated");
        UuidData second = new UuidData();
        second.setLabel("null UUID");
        List<UuidData> expected = Arrays.asList(first, second);
        Assertions.assertEquals(expected, RoundTripHelper.writeAndRead(file, UuidData.class, expected));
        List<StringData> strings = RoundTripHelper.read(file, StringData.class);
        Assertions.assertEquals(TEXT, strings.get(0).getId());
    }

    @ParameterizedTest
    @ExcelFormatSource
    void readsUppercaseAndBlankCells(ExcelFormat format) throws Exception {
        File file = createTempFile(format);
        StringData first = new StringData();
        first.setId(TEXT.toUpperCase(Locale.ROOT));
        first.setLabel("uppercase");
        StringData second = new StringData();
        second.setId("");
        second.setLabel("blank UUID");
        RoundTripHelper.write(file, StringData.class, Arrays.asList(first, second));
        List<UuidData> rows = RoundTripHelper.read(file, UuidData.class);
        Assertions.assertEquals(2, rows.size());
        Assertions.assertEquals(UUID.fromString(TEXT), rows.get(0).getId());
        Assertions.assertNull(rows.get(1).getId());
    }

    @ParameterizedTest
    @ExcelFormatSource
    void reportsInvalidCellAsConversionFailure(ExcelFormat format) throws Exception {
        File file = createTempFile(format);
        StringData row = new StringData();
        row.setId("not-a-uuid");
        row.setLabel("invalid");
        RoundTripHelper.write(file, StringData.class, Collections.singletonList(row));
        ExcelDataConvertException error = Assertions.assertThrows(
                ExcelDataConvertException.class, () -> RoundTripHelper.read(file, UuidData.class));
        Assertions.assertInstanceOf(IllegalArgumentException.class, error.getCause());
    }

    @Data
    public static class UuidData {
        private UUID id;
        private String label;
    }

    @Data
    public static class StringData {
        private String id;
        private String label;
    }
}
