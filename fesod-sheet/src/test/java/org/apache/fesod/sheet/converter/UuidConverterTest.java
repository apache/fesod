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
import org.apache.fesod.sheet.converters.ConverterKeyBuild;
import org.apache.fesod.sheet.converters.DefaultConverterLoader;
import org.apache.fesod.sheet.converters.uuid.UUIDStringConverter;
import org.apache.fesod.sheet.enums.CellDataTypeEnum;
import org.apache.fesod.sheet.exception.ExcelDataConvertException;
import org.apache.fesod.sheet.metadata.GlobalConfiguration;
import org.apache.fesod.sheet.metadata.data.ReadCellData;
import org.apache.fesod.sheet.metadata.data.WriteCellData;
import org.apache.fesod.sheet.testkit.Tags;
import org.apache.fesod.sheet.testkit.base.AbstractExcelTest;
import org.apache.fesod.sheet.testkit.enums.ExcelFormat;
import org.apache.fesod.sheet.testkit.helpers.RoundTripHelper;
import org.apache.fesod.sheet.testkit.params.ExcelFormatSource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

@Tag(Tags.ROUND_TRIP)
class UuidConverterTest extends AbstractExcelTest {

    private static final String TEXT = "123e4567-e89b-12d3-a456-426614174000";
    private final UUIDStringConverter converter = new UUIDStringConverter();
    private final GlobalConfiguration configuration = new GlobalConfiguration();

    @Test
    void supportsUuidStringsAndRegistersAllLookupPaths() {
        Assertions.assertEquals(UUID.class, converter.supportJavaTypeKey());
        Assertions.assertEquals(CellDataTypeEnum.STRING, converter.supportExcelTypeKey());
        Assertions.assertInstanceOf(
                UUIDStringConverter.class,
                DefaultConverterLoader.loadDefaultReadConverter()
                        .get(ConverterKeyBuild.buildKey(UUID.class, CellDataTypeEnum.STRING)));
        Assertions.assertInstanceOf(
                UUIDStringConverter.class,
                DefaultConverterLoader.loadDefaultWriteConverter().get(ConverterKeyBuild.buildKey(UUID.class)));
        Assertions.assertInstanceOf(
                UUIDStringConverter.class,
                DefaultConverterLoader.loadDefaultWriteConverter()
                        .get(ConverterKeyBuild.buildKey(UUID.class, CellDataTypeEnum.STRING)));
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
        StringData padded = new StringData();
        padded.setId(" \t" + TEXT + "\r\n");
        padded.setLabel("padded UUID");
        StringData whitespace = new StringData();
        whitespace.setId(" \t ");
        whitespace.setLabel("whitespace UUID");
        RoundTripHelper.write(file, StringData.class, Arrays.asList(first, second, padded, whitespace));
        List<UuidData> rows = RoundTripHelper.read(file, UuidData.class);
        Assertions.assertEquals(4, rows.size());
        Assertions.assertEquals(UUID.fromString(TEXT), rows.get(0).getId());
        Assertions.assertNull(rows.get(1).getId());
        Assertions.assertEquals(UUID.fromString(TEXT), rows.get(2).getId());
        Assertions.assertNull(rows.get(3).getId());
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
