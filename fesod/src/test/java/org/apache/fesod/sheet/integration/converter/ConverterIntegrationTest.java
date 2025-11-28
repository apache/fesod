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
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import org.apache.fesod.sheet.FesodSheet;
import org.apache.fesod.sheet.converter.ReadAllConverterData;
import org.apache.fesod.sheet.metadata.data.WriteCellData;
import org.apache.fesod.sheet.model.ConverterData;
import org.apache.fesod.sheet.model.ImageData;
import org.apache.fesod.sheet.testkit.base.AbstractExcelTest;
import org.apache.fesod.sheet.testkit.enums.ExcelFormat;
import org.apache.fesod.sheet.testkit.listeners.CollectingReadListener;
import org.apache.fesod.sheet.util.DateUtils;
import org.apache.fesod.sheet.util.FileUtils;
import org.apache.fesod.sheet.util.TestFileUtil;
import org.apache.fesod.sheet.util.TestUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Integration tests for type converter functionality across all Excel formats.
 *
 * <p>This test class validates that FesodSheet correctly converts between:</p>
 * <ul>
 *   <li>Java primitive types and their wrappers</li>
 *   <li>Date, LocalDate, LocalDateTime</li>
 *   <li>BigDecimal, BigInteger</li>
 *   <li>Custom cell data types</li>
 *   <li>Image data (Excel formats only)</li>
 * </ul>
 *
 * <p>Refactored from legacy {@code ConverterDataTest} to use:</p>
 * <ul>
 *   <li>Parameterized tests with format providers</li>
 *   <li>Unified model classes from model/ package</li>
 *   <li>CollectingReadListener instead of custom listeners</li>
 * </ul>
 *
 * @see ConverterData
 * @see AbstractExcelTest
 */
@Tag("integration")
@DisplayName("Converter Integration Tests")
class ConverterIntegrationTest extends AbstractExcelTest {

    // ==================== Basic Type Conversion Tests ====================

    @Nested
    @DisplayName("Basic Type Conversions")
    class BasicTypeConversions {

        @ParameterizedTest(name = "Format: {0}")
        @MethodSource("org.apache.fesod.sheet.testkit.base.AbstractExcelTest#allFormats")
        @DisplayName("should convert all basic types correctly through write-read cycle")
        void shouldConvertBasicTypesCorrectly(ExcelFormat format) {
            // Given
            File file = createTempFile("converter", format);
            List<ConverterData> writeData = createConverterWriteData();

            // When - write with write-specific data class
            FesodSheet.write(file, ConverterData.class).sheet().doWrite(writeData);

            // Read with read-specific data class
            CollectingReadListener<ConverterData> listener = new CollectingReadListener<>();
            FesodSheet.read(file, ConverterData.class, listener).sheet().doRead();

            // Then
            List<ConverterData> result = listener.getData();
            assertThat(result).hasSize(1);

            ConverterData data = result.get(0);
            assertBasicTypeConversions(data);
        }

        @ParameterizedTest(name = "Format: {0}")
        @MethodSource("org.apache.fesod.sheet.testkit.base.AbstractExcelTest#allFormats")
        @DisplayName("should work with unified ConverterData model")
        void shouldWorkWithUnifiedModel(ExcelFormat format) {
            // Given
            File file = createTempFile("converter-unified", format);
            List<ConverterData> writeData = new ArrayList<>();
            ConverterData testData = ConverterData.createTestData();
            // Note: CellData field is excluded due to hashCode issues in FesodSheet internals
            testData.setCellData(null);
            writeData.add(testData);

            // When - write and read with unified model
            FesodSheet.write(file, ConverterData.class).sheet().doWrite(writeData);

            CollectingReadListener<ConverterData> listener = new CollectingReadListener<>();
            FesodSheet.read(file, ConverterData.class, listener).sheet().doRead();

            // Then
            List<ConverterData> result = listener.getData();
            assertThat(result).hasSize(1);

            ConverterData data = result.get(0);
            assertThat(data.getDate()).isEqualTo(TestUtil.TEST_DATE);
            assertThat(data.getBooleanData()).isTrue();
            assertThat(data.getString()).isEqualTo("测试");
        }

        private void assertBasicTypeConversions(ConverterData data) {
            // Date/Time types
            assertThat(data.getDate()).isEqualTo(TestUtil.TEST_DATE);
            assertThat(data.getLocalDate()).isEqualTo(TestUtil.TEST_LOCAL_DATE);
            assertThat(data.getLocalDateTime()).isEqualTo(TestUtil.TEST_LOCAL_DATE_TIME);

            // Boolean
            assertThat(data.getBooleanData()).isTrue();

            // Big number types
            assertThat(data.getBigDecimal().doubleValue()).isEqualTo(BigDecimal.ONE.doubleValue());
            assertThat(data.getBigInteger().intValue()).isEqualTo(BigInteger.ONE.intValue());

            // Integer types
            assertThat(data.getLongData()).isEqualTo(1L);
            assertThat(data.getIntegerData()).isEqualTo(1);
            assertThat(data.getShortData()).isEqualTo((short) 1);
            assertThat(data.getByteData()).isEqualTo((byte) 1);

            // Floating point types
            assertThat(data.getDoubleData()).isEqualTo(1.0);
            assertThat(data.getFloatData()).isEqualTo(1.0f);

            // String
            assertThat(data.getString()).isEqualTo("测试");

            // Custom cell data
            assertThat(data.getCellData().getStringValue()).isEqualTo("自定义");
        }
    }

    // ==================== All Converter Tests ====================

    @Nested
    @DisplayName("All Type Converters")
    class AllTypeConverters {

        @ParameterizedTest(name = "Format: {0}")
        @MethodSource("org.apache.fesod.sheet.testkit.base.AbstractExcelTest#allFormats")
        @DisplayName("should convert all cell types (boolean, number, string) to all Java types")
        void shouldConvertAllCellTypesToAllJavaTypes(ExcelFormat format) throws ParseException {
            // Given - use existing test resource files
            String fileName = getConverterResourcePath(format);
            File file = TestFileUtil.readFile(fileName);

            // When
            CollectingReadListener<ReadAllConverterData> listener = new CollectingReadListener<>();
            FesodSheet.read(file, ReadAllConverterData.class, listener).sheet().doRead();

            // Then
            List<ReadAllConverterData> result = listener.getData();
            assertThat(result).hasSize(1);

            ReadAllConverterData data = result.get(0);
            assertAllConverterTypes(data, format);
        }

        private String getConverterResourcePath(ExcelFormat format) {
            switch (format) {
                case XLSX:
                    return "converter" + File.separator + "converter07.xlsx";
                case XLS:
                    return "converter" + File.separator + "converter03.xls";
                case CSV:
                    return "converter" + File.separator + "converterCsv.csv";
                default:
                    throw new IllegalArgumentException("Unknown format: " + format);
            }
        }

        private void assertAllConverterTypes(ReadAllConverterData data, ExcelFormat format) throws ParseException {

            // BigDecimal from different sources
            assertThat(data.getBigDecimalBoolean().doubleValue()).isEqualTo(BigDecimal.ONE.doubleValue());
            assertThat(data.getBigDecimalNumber().doubleValue()).isEqualTo(BigDecimal.ONE.doubleValue());
            assertThat(data.getBigDecimalString().doubleValue()).isEqualTo(BigDecimal.ONE.doubleValue());

            // BigInteger from different sources
            assertThat(data.getBigIntegerBoolean().intValue()).isEqualTo(BigInteger.ONE.intValue());
            assertThat(data.getBigIntegerNumber().intValue()).isEqualTo(BigInteger.ONE.intValue());
            assertThat(data.getBigIntegerString().intValue()).isEqualTo(BigInteger.ONE.intValue());

            // Boolean from different sources
            assertThat(data.getBooleanBoolean()).isTrue();
            assertThat(data.getBooleanNumber()).isTrue();
            assertThat(data.getBooleanString()).isTrue();

            // Byte from different sources
            assertThat(data.getByteBoolean()).isEqualTo((byte) 1);
            assertThat(data.getByteNumber()).isEqualTo((byte) 1);
            assertThat(data.getByteString()).isEqualTo((byte) 1);

            // Date conversions
            assertThat(data.getDateNumber()).isEqualTo(DateUtils.parseDate("2020-01-01 01:01:01"));
            assertThat(data.getDateString()).isEqualTo(DateUtils.parseDate("2020-01-01 01:01:01"));

            // LocalDateTime conversions
            assertThat(data.getLocalDateTimeNumber())
                    .isEqualTo(DateUtils.parseLocalDateTime("2020-01-01 01:01:01", null, null));
            assertThat(data.getLocalDateTimeString())
                    .isEqualTo(DateUtils.parseLocalDateTime("2020-01-01 01:01:01", null, null));

            // Double from different sources
            assertThat(data.getDoubleBoolean()).isEqualTo(1.0);
            assertThat(data.getDoubleNumber()).isEqualTo(1.0);
            assertThat(data.getDoubleString()).isEqualTo(1.0);

            // Float from different sources
            assertThat(data.getFloatBoolean()).isEqualTo(1.0f);
            assertThat(data.getFloatNumber()).isEqualTo(1.0f);
            assertThat(data.getFloatString()).isEqualTo(1.0f);

            // Integer from different sources
            assertThat(data.getIntegerBoolean()).isEqualTo(1);
            assertThat(data.getIntegerNumber()).isEqualTo(1);
            assertThat(data.getIntegerString()).isEqualTo(1);

            // Long from different sources
            assertThat(data.getLongBoolean()).isEqualTo(1L);
            assertThat(data.getLongNumber()).isEqualTo(1L);
            assertThat(data.getLongString()).isEqualTo(1L);

            // Short from different sources
            assertThat(data.getShortBoolean()).isEqualTo((short) 1);
            assertThat(data.getShortNumber()).isEqualTo((short) 1);
            assertThat(data.getShortString()).isEqualTo((short) 1);

            // String conversions
            assertThat(data.getStringBoolean().toLowerCase()).isEqualTo("true");
            assertThat(data.getStringString()).isEqualTo("测试");
            assertThat(data.getStringError()).isEqualTo("#VALUE!");

            // Format-specific date string representation
            if (format != ExcelFormat.CSV) {
                assertThat(data.getStringNumberDate()).isEqualTo("2020-1-1 1:01");
            } else {
                assertThat(data.getStringNumberDate()).isEqualTo("2020-01-01 01:01:01");
            }

            // Formula results
            assertThat(new BigDecimal(data.getStringFormulaNumber()).doubleValue())
                    .isEqualTo(2.0);
            assertThat(data.getStringFormulaString()).isEqualTo("1测试");
        }
    }

    // ==================== Image Converter Tests ====================

    @Nested
    @DisplayName("Image Conversions")
    class ImageConversions {

        @ParameterizedTest(name = "Format: {0}")
        @MethodSource("org.apache.fesod.sheet.testkit.base.AbstractExcelTest#excelFormats")
        @DisplayName("should write image data to Excel files")
        void shouldWriteImageData(ExcelFormat format) throws Exception {
            // Given
            File file = createTempFile("image-converter", format);
            String imagePath = TestFileUtil.getPath() + "converter" + File.separator + "img.jpg";

            // When - write image data using model.ImageData
            InputStream inputStream = null;
            try {
                List<ImageData> list = new ArrayList<>();
                ImageData imageData = new ImageData();
                list.add(imageData);
                imageData.setByteArray(FileUtils.readFileToByteArray(new File(imagePath)));
                imageData.setFile(new File(imagePath));
                imageData.setString(imagePath);
                inputStream = FileUtils.openInputStream(new File(imagePath));
                imageData.setInputStream(inputStream);
                FesodSheet.write(file, ImageData.class).sheet().doWrite(list);
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }
            }

            // Then - file should be created successfully
            assertThat(file).exists();
            assertThat(file.length()).isGreaterThan(0);
        }
    }

    // ==================== Roundtrip Tests ====================

    @Nested
    @DisplayName("Converter Roundtrip Validation")
    class ConverterRoundtripValidation {

        @ParameterizedTest(name = "Format: {0}")
        @MethodSource("org.apache.fesod.sheet.testkit.base.AbstractExcelTest#allFormats")
        @DisplayName("should preserve all converted types through write-read cycle")
        void shouldPreserveConvertedTypesThroughRoundtrip(ExcelFormat format) {
            // Given
            File file = createTempFile("converter-roundtrip", format);
            List<ConverterData> writeData = createConverterWriteData();

            // When
            FesodSheet.write(file, ConverterData.class).sheet().doWrite(writeData);

            CollectingReadListener<ConverterData> listener = new CollectingReadListener<>();
            FesodSheet.read(file, ConverterData.class, listener).sheet().doRead();

            // Then
            assertThat(listener.getData()).hasSize(1).first().satisfies(data -> {
                assertThat(data.getDate()).isEqualTo(TestUtil.TEST_DATE);
                assertThat(data.getBooleanData()).isTrue();
                assertThat(data.getString()).isEqualTo("测试");
            });
        }
    }

    // ==================== Helper Methods ====================

    /**
     * Creates test data for converter write operations.
     *
     * @return list of ConverterData objects
     */
    private List<ConverterData> createConverterWriteData() {
        List<ConverterData> list = new ArrayList<>();
        ConverterData data = new ConverterData();

        // Date/Time types
        data.setDate(TestUtil.TEST_DATE);
        data.setLocalDate(TestUtil.TEST_LOCAL_DATE);
        data.setLocalDateTime(TestUtil.TEST_LOCAL_DATE_TIME);

        // Boolean
        data.setBooleanData(Boolean.TRUE);

        // Big number types
        data.setBigDecimal(BigDecimal.ONE);
        data.setBigInteger(BigInteger.ONE);

        // Integer types
        data.setLongData(1L);
        data.setIntegerData(1);
        data.setShortData((short) 1);
        data.setByteData((byte) 1);

        // Floating point types
        data.setDoubleData(1.0);
        data.setFloatData(1.0f);

        // String
        data.setString("测试");

        // Custom cell data
        data.setCellData(new WriteCellData<>("自定义"));

        list.add(data);
        return list;
    }
}
