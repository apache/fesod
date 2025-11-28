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

package org.apache.fesod.sheet.unit.converter;

import static org.assertj.core.api.Assertions.assertThat;
import java.math.BigDecimal;
import java.sql.Timestamp;
import org.apache.fesod.sheet.converter.TimestampNumberConverter;
import org.apache.fesod.sheet.converter.TimestampStringConverter;
import org.apache.fesod.sheet.converters.WriteConverterContext;
import org.apache.fesod.sheet.converters.floatconverter.FloatNumberConverter;
import org.apache.fesod.sheet.enums.CellDataTypeEnum;
import org.apache.fesod.sheet.metadata.GlobalConfiguration;
import org.apache.fesod.sheet.metadata.data.WriteCellData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for individual converter implementations.
 * Tests converters in isolation without file I/O.
 *
 * <p>Consolidated from legacy {@code ConverterTest} and expanded with
 * additional converter tests.</p>
 */
@Tag("unit")
@DisplayName("Converter Unit Tests")
class ConverterUnitTest {

    // ==================== Float Converter Tests ====================

    @Nested
    @DisplayName("FloatNumberConverter")
    class FloatNumberConverterTests {

        private final FloatNumberConverter converter = new FloatNumberConverter();

        @Test
        @DisplayName("should convert float to BigDecimal with correct precision")
        void shouldConvertFloatToExactBigDecimal() {
            // Given
            WriteConverterContext<Float> context = new WriteConverterContext<>();
            context.setValue(95.62F);

            // When
            WriteCellData<?> result = converter.convertToExcelData(context);

            // Then
            assertThat(result.getNumberValue()).isNotNull().isEqualByComparingTo(new BigDecimal("95.62"));
        }

        @Test
        @DisplayName("should convert zero correctly")
        void shouldConvertZeroCorrectly() {
            // Given
            WriteConverterContext<Float> context = new WriteConverterContext<>();
            context.setValue(0.0F);

            // When
            WriteCellData<?> result = converter.convertToExcelData(context);

            // Then
            assertThat(result.getNumberValue()).isEqualByComparingTo(BigDecimal.ZERO);
        }

        @Test
        @DisplayName("should convert negative float correctly")
        void shouldConvertNegativeFloatCorrectly() {
            // Given
            WriteConverterContext<Float> context = new WriteConverterContext<>();
            context.setValue(-123.45F);

            // When
            WriteCellData<?> result = converter.convertToExcelData(context);

            // Then
            assertThat(result.getNumberValue()).isEqualByComparingTo(new BigDecimal("-123.45"));
        }

        @Test
        @DisplayName("should support Float java type")
        void shouldSupportFloatJavaType() {
            assertThat(converter.supportJavaTypeKey()).isEqualTo(Float.class);
        }
    }

    // ==================== Timestamp String Converter Tests ====================

    @Nested
    @DisplayName("TimestampStringConverter")
    class TimestampStringConverterTests {

        private final TimestampStringConverter converter = new TimestampStringConverter();

        @Test
        @DisplayName("should convert timestamp to string format")
        void shouldConvertTimestampToString() {
            // Given
            Timestamp timestamp = Timestamp.valueOf("2020-01-01 12:30:45");
            GlobalConfiguration config = new GlobalConfiguration();

            // When
            WriteCellData<?> result = converter.convertToExcelData(timestamp, null, config);

            // Then
            assertThat(result.getType()).isEqualTo(CellDataTypeEnum.STRING);
            assertThat(result.getStringValue()).isNotNull();
            // The exact format depends on locale, just verify it's a date string
            assertThat(result.getStringValue()).contains("2020");
        }

        @Test
        @DisplayName("should support Timestamp java type")
        void shouldSupportTimestampJavaType() {
            assertThat(converter.supportJavaTypeKey()).isEqualTo(Timestamp.class);
        }

        @Test
        @DisplayName("should support STRING excel type")
        void shouldSupportStringExcelType() {
            assertThat(converter.supportExcelTypeKey()).isEqualTo(CellDataTypeEnum.STRING);
        }
    }

    // ==================== Timestamp Number Converter Tests ====================

    @Nested
    @DisplayName("TimestampNumberConverter")
    class TimestampNumberConverterTests {

        private final TimestampNumberConverter converter = new TimestampNumberConverter();

        @Test
        @DisplayName("should convert timestamp to Excel date number")
        void shouldConvertTimestampToExcelNumber() {
            // Given
            Timestamp timestamp = Timestamp.valueOf("2020-01-01 00:00:00");
            GlobalConfiguration config = new GlobalConfiguration();

            // When
            WriteCellData<?> result = converter.convertToExcelData(timestamp, null, config);

            // Then
            assertThat(result.getNumberValue()).isNotNull().isGreaterThan(BigDecimal.ZERO);
            // Excel date for 2020-01-01 is approximately 43831
            assertThat(result.getNumberValue().intValue()).isGreaterThan(43000);
        }

        @Test
        @DisplayName("should support Timestamp java type")
        void shouldSupportTimestampJavaType() {
            assertThat(converter.supportJavaTypeKey()).isEqualTo(Timestamp.class);
        }

        @Test
        @DisplayName("should support NUMBER excel type")
        void shouldSupportNumberExcelType() {
            assertThat(converter.supportExcelTypeKey()).isEqualTo(CellDataTypeEnum.NUMBER);
        }
    }

    // ==================== Converter Type Support Tests ====================

    @Nested
    @DisplayName("Converter Type Support")
    class ConverterTypeSupportTests {

        @Test
        @DisplayName("timestamp converters should support different excel types")
        void timestampConvertersShouldSupportDifferentExcelTypes() {
            TimestampStringConverter stringConverter = new TimestampStringConverter();
            TimestampNumberConverter numberConverter = new TimestampNumberConverter();

            // Both support same Java type
            assertThat(stringConverter.supportJavaTypeKey())
                    .isEqualTo(numberConverter.supportJavaTypeKey())
                    .isEqualTo(Timestamp.class);

            // But different Excel types
            assertThat(stringConverter.supportExcelTypeKey()).isNotEqualTo(numberConverter.supportExcelTypeKey());
            assertThat(stringConverter.supportExcelTypeKey()).isEqualTo(CellDataTypeEnum.STRING);
            assertThat(numberConverter.supportExcelTypeKey()).isEqualTo(CellDataTypeEnum.NUMBER);
        }
    }
}
