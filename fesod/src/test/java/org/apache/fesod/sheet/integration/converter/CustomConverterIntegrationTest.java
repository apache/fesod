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

package org.apache.fesod.sheet.integration.converter;

import static org.assertj.core.api.Assertions.assertThat;
import java.io.File;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.fesod.sheet.ExcelWriter;
import org.apache.fesod.sheet.FesodSheet;
import org.apache.fesod.sheet.converter.CustomConverterWriteData;
import org.apache.fesod.sheet.converter.TimestampNumberConverter;
import org.apache.fesod.sheet.converter.TimestampStringConverter;
import org.apache.fesod.sheet.converters.Converter;
import org.apache.fesod.sheet.converters.ConverterKeyBuild;
import org.apache.fesod.sheet.testkit.base.AbstractExcelTest;
import org.apache.fesod.sheet.testkit.enums.ExcelFormat;
import org.apache.fesod.sheet.write.builder.ExcelWriterSheetBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Integration tests for custom converter registration and usage.
 *
 * <p>Tests the ability to register and use custom converters like
 * {@link TimestampStringConverter} and {@link TimestampNumberConverter}.</p>
 *
 * <p>Refactored from legacy {@code CustomConverterTest} to use parameterized tests.</p>
 *
 * @see TimestampStringConverter
 * @see TimestampNumberConverter
 */
@Tag("integration")
@DisplayName("Custom Converter Integration Tests")
class CustomConverterIntegrationTest extends AbstractExcelTest {

    // ==================== Converter Registration Tests ====================

    @Nested
    @DisplayName("Converter Registration")
    class ConverterRegistration {

        @Test
        @DisplayName("should register custom converters in converter map")
        void shouldRegisterCustomConvertersInConverterMap() throws Exception {
            // Given
            File file = createTempFile("converter-map-test", ExcelFormat.CSV);
            TimestampStringConverter timestampStringConverter = new TimestampStringConverter();
            TimestampNumberConverter timestampNumberConverter = new TimestampNumberConverter();

            // When
            ExcelWriter excelWriter = FesodSheet.write(file)
                    .registerConverter(timestampStringConverter)
                    .registerConverter(timestampNumberConverter)
                    .build();

            Map<ConverterKeyBuild.ConverterKey, Converter<?>> converterMap =
                    excelWriter.writeContext().currentWriteHolder().converterMap();

            excelWriter.write(
                    createTimestampData(),
                    new ExcelWriterSheetBuilder().sheetNo(0).build());
            excelWriter.finish();

            // Then
            assertThat(converterMap)
                    .containsKey(ConverterKeyBuild.buildKey(
                            timestampStringConverter.supportJavaTypeKey(),
                            timestampStringConverter.supportExcelTypeKey()));
            assertThat(converterMap)
                    .containsKey(ConverterKeyBuild.buildKey(
                            timestampNumberConverter.supportJavaTypeKey(),
                            timestampNumberConverter.supportExcelTypeKey()));
        }
    }

    // ==================== Custom Converter Write Tests ====================

    @Nested
    @DisplayName("Custom Converter Write Operations")
    class CustomConverterWriteOperations {

        @ParameterizedTest(name = "Format: {0}")
        @MethodSource("org.apache.fesod.sheet.testkit.base.AbstractExcelTest#allFormats")
        @DisplayName("should write data with custom converters to all formats")
        void shouldWriteDataWithCustomConverters(ExcelFormat format) throws Exception {
            // Given
            File file = createTempFile("custom-converter-write", format);
            List<CustomConverterWriteData> data = createTimestampData();

            // When
            FesodSheet.write(file)
                    .registerConverter(new TimestampNumberConverter())
                    .registerConverter(new TimestampStringConverter())
                    .sheet()
                    .doWrite(data);

            // Then
            assertThat(file).exists();
            assertThat(file.length()).isGreaterThan(0);
        }
    }

    // ==================== Helper Methods ====================

    /**
     * Creates test data with timestamp values for custom converter testing.
     *
     * @return list of CustomConverterWriteData objects
     */
    private List<CustomConverterWriteData> createTimestampData() {
        List<CustomConverterWriteData> list = new ArrayList<>();
        CustomConverterWriteData writeData = new CustomConverterWriteData();
        writeData.setTimestampStringData(Timestamp.valueOf("2020-01-01 01:00:00"));
        writeData.setTimestampNumberData(Timestamp.valueOf("2020-12-01 12:12:12"));
        list.add(writeData);
        return list;
    }
}
