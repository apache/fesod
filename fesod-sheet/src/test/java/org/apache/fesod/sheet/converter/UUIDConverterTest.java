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

import java.util.Locale;
import java.util.UUID;
import org.apache.fesod.sheet.converters.uuid.UUIDStringConverter;
import org.apache.fesod.sheet.enums.CellDataTypeEnum;
import org.apache.fesod.sheet.metadata.GlobalConfiguration;
import org.apache.fesod.sheet.metadata.data.ReadCellData;
import org.apache.fesod.sheet.metadata.data.WriteCellData;
import org.apache.fesod.sheet.testkit.Tags;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

@Tag(Tags.UNIT)
class UUIDConverterTest {

    private static final String TEXT = "123e4567-e89b-12d3-a456-426614174000";
    private final UUIDStringConverter converter = new UUIDStringConverter();
    private final GlobalConfiguration configuration = new GlobalConfiguration();

    @Test
    void supportsUuidStrings() {
        Assertions.assertEquals(UUID.class, converter.supportJavaTypeKey());
        Assertions.assertEquals(CellDataTypeEnum.STRING, converter.supportExcelTypeKey());
    }

    @ParameterizedTest
    @ValueSource(
            strings = {
                TEXT,
                "123E4567-E89B-12D3-A456-426614174000",
                "00000000-0000-0000-0000-000000000000",
                "ffffffff-ffff-ffff-ffff-ffffffffffff"
            })
    void readsAndWritesCanonicalStrings(String input) {
        UUID value = converter.convertToJavaData(new ReadCellData<>(input), null, configuration);
        WriteCellData<?> cell = converter.convertToExcelData(value, null, configuration);
        Assertions.assertEquals(CellDataTypeEnum.STRING, cell.getType());
        Assertions.assertEquals(input.toLowerCase(Locale.ROOT), cell.getStringValue());
    }

    @ParameterizedTest
    @ValueSource(
            strings = {
                "not-a-uuid",
                "123e4567-e89b-12d3-a456-42661417400g",
                "123e4567e89b12d3a456426614174000",
                "  not-a-uuid\t",
                "123e4567-e89b-12d3-a456-42661417 4000"
            })
    void rejectsInvalidInput(String input) {
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> converter.convertToJavaData(new ReadCellData<>(input), null, configuration));
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", " ", "\t\r\n", "\u2003"})
    void readsBlankStringAsMissingValue(String input) {
        ReadCellData<String> cell = new ReadCellData<>();
        cell.setStringValue(input);
        Assertions.assertNull(converter.convertToJavaData(cell, null, configuration));
    }

    @ParameterizedTest
    @ValueSource(strings = {" " + TEXT, TEXT + " ", "\t" + TEXT + "\r\n"})
    void trimsWhitespaceBeforeParsing(String input) {
        Assertions.assertEquals(
                UUID.fromString(TEXT), converter.convertToJavaData(new ReadCellData<>(input), null, configuration));
    }
}
