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

package org.apache.fesod.sheet.integration.read;

import static org.assertj.core.api.Assertions.assertThat;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.fesod.sheet.FesodSheet;
import org.apache.fesod.sheet.read.listener.PageReadListener;
import org.apache.fesod.sheet.simple.SimpleData;
import org.apache.fesod.sheet.testkit.base.AbstractExcelTest;
import org.apache.fesod.sheet.testkit.enums.ExcelFormat;
import org.apache.fesod.sheet.testkit.listeners.CollectingReadListener;
import org.apache.fesod.sheet.util.TestFileUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Tests for simple read/write operations across all Excel formats.
 *
 * <p>This test class validates basic Excel read/write functionality including:</p>
 * <ul>
 *   <li>File-based read/write operations</li>
 *   <li>Stream-based read/write operations</li>
 *   <li>Synchronous reading</li>
 *   <li>Sheet name/number navigation</li>
 *   <li>Paginated reading</li>
 * </ul>
 *
 * <p>Refactored from legacy {@code SimpleDataTest} to use parameterized tests
 * with format providers from {@link AbstractExcelTest}.</p>
 *
 * @see SimpleData
 * @see AbstractExcelTest
 */
@Tag("unit")
@DisplayName("Simple Read/Write Tests")
class SimpleReadWriteTest extends AbstractExcelTest {

    private static final int TEST_DATA_SIZE = 10;

    // ==================== Basic Read/Write Tests ====================

    @Nested
    @DisplayName("File-based Read/Write")
    class FileBasedReadWrite {

        @ParameterizedTest(name = "Format: {0}")
        @MethodSource("org.apache.fesod.sheet.testkit.base.AbstractExcelTest#allFormats")
        @DisplayName("should write and read data correctly using File")
        void shouldWriteAndReadUsingFile(ExcelFormat format) {
            // Given
            File file = createTempFile("simple", format);
            List<SimpleData> expected = createTestData();

            // When
            writeData(file, SimpleData.class, expected);
            List<SimpleData> actual = readData(file, SimpleData.class);

            // Then
            assertThat(actual)
                    .hasSize(TEST_DATA_SIZE)
                    .first()
                    .extracting(SimpleData::getName)
                    .isEqualTo("姓名0");
        }

        @ParameterizedTest(name = "Format: {0}")
        @MethodSource("org.apache.fesod.sheet.testkit.base.AbstractExcelTest#allFormats")
        @DisplayName("should write and read data correctly using InputStream/OutputStream")
        void shouldWriteAndReadUsingStreams(ExcelFormat format) throws Exception {
            // Given
            File file = createTempFile("simple-stream", format);
            List<SimpleData> expected = createTestData();

            // When - write using OutputStream
            FesodSheet.write(new FileOutputStream(file), SimpleData.class)
                    .excelType(format.getExcelType())
                    .sheet()
                    .doWrite(expected);

            // Read using InputStream
            CollectingReadListener<SimpleData> listener = new CollectingReadListener<>();
            FesodSheet.read(new FileInputStream(file), SimpleData.class, listener)
                    .sheet()
                    .doRead();

            // Then
            assertThat(listener.getData())
                    .hasSize(TEST_DATA_SIZE)
                    .first()
                    .extracting(SimpleData::getName)
                    .isEqualTo("姓名0");
        }
    }

    // ==================== Synchronous Read Tests ====================

    @Nested
    @DisplayName("Synchronous Reading")
    class SynchronousReading {

        @ParameterizedTest(name = "Format: {0}")
        @MethodSource("org.apache.fesod.sheet.testkit.base.AbstractExcelTest#allFormats")
        @DisplayName("should read all data synchronously")
        void shouldReadSynchronously(ExcelFormat format) {
            // Given
            File file = createTempFile("sync-read", format);
            writeData(file, SimpleData.class, createTestData());

            // When
            List<Object> result =
                    FesodSheet.read(file).head(SimpleData.class).sheet().doReadSync();

            // Then
            assertThat(result)
                    .hasSize(TEST_DATA_SIZE)
                    .first()
                    .isInstanceOf(SimpleData.class)
                    .extracting(obj -> ((SimpleData) obj).getName())
                    .isEqualTo("姓名0");
        }
    }

    // ==================== Sheet Navigation Tests ====================

    @Nested
    @DisplayName("Sheet Navigation")
    class SheetNavigation {

        @Test
        @DisplayName("should read sheet by name (XLSX only)")
        void shouldReadSheetByName() {
            // Given - use existing test resource file
            File file = TestFileUtil.readFile("simple" + File.separator + "simple07.xlsx");

            // When
            List<Map<Integer, Object>> result =
                    FesodSheet.read(file).sheet("simple").doReadSync();

            // Then
            assertThat(result).hasSize(1);
        }

        @Test
        @DisplayName("should read sheet by index (XLSX only)")
        void shouldReadSheetByIndex() {
            // Given
            File file = TestFileUtil.readFile("simple" + File.separator + "simple07.xlsx");

            // When - sheetNo is 0-based
            List<Map<Integer, Object>> result = FesodSheet.read(file).sheet(1).doReadSync();

            // Then
            assertThat(result).hasSize(1);
        }
    }

    // ==================== Paginated Read Tests ====================

    @Nested
    @DisplayName("Paginated Reading")
    class PaginatedReading {

        @ParameterizedTest(name = "Format: {0}")
        @MethodSource("org.apache.fesod.sheet.testkit.base.AbstractExcelTest#allFormats")
        @DisplayName("should read data in pages using PageReadListener")
        void shouldReadInPages(ExcelFormat format) {
            // Given
            File file = createTempFile("page-read", format);
            int pageSize = 5;
            List<List<SimpleData>> pages = new ArrayList<>();

            writeData(file, SimpleData.class, createTestData());

            // When
            FesodSheet.read(
                            file,
                            SimpleData.class,
                            new PageReadListener<SimpleData>(
                                    pageData -> pages.add(new ArrayList<>(pageData)), pageSize))
                    .sheet()
                    .doRead();

            // Then
            assertThat(pages)
                    .hasSize(2) // 10 items / 5 per page = 2 pages
                    .allSatisfy(page -> assertThat(page).hasSize(pageSize));
        }
    }

    // ==================== Head Map Tests ====================

    @Nested
    @DisplayName("Header Mapping")
    class HeaderMapping {

        @ParameterizedTest(name = "Format: {0}")
        @MethodSource("org.apache.fesod.sheet.testkit.base.AbstractExcelTest#allFormats")
        @DisplayName("should correctly parse header row")
        void shouldParseHeaderRow(ExcelFormat format) {
            // Given
            File file = createTempFile("header-test", format);
            writeData(file, SimpleData.class, createTestData());

            // When
            CollectingReadListener<SimpleData> listener = new CollectingReadListener<>();
            FesodSheet.read(file, SimpleData.class, listener).sheet().doRead();

            // Then
            assertThat(listener.getHeadMap()).isNotNull().containsEntry(0, "姓名");
        }
    }

    // ==================== Roundtrip Tests ====================

    @Nested
    @DisplayName("Roundtrip Validation")
    class RoundtripValidation {

        @ParameterizedTest(name = "Format: {0}")
        @MethodSource("org.apache.fesod.sheet.testkit.base.AbstractExcelTest#allFormats")
        @DisplayName("should preserve data through write-read cycle")
        void shouldPreserveDataThroughRoundtrip(ExcelFormat format) {
            // Given
            File file = createTempFile("roundtrip", format);
            List<SimpleData> expected = createTestData();

            // When
            List<SimpleData> actual = roundTrip(file, SimpleData.class, expected);

            // Then
            assertThat(actual)
                    .hasSize(expected.size())
                    .usingRecursiveFieldByFieldElementComparator()
                    .containsExactlyElementsOf(expected);
        }
    }

    // ==================== Helper Methods ====================

    /**
     * Creates test data with the specified number of rows.
     *
     * @return list of SimpleData objects
     */
    private List<SimpleData> createTestData() {
        List<SimpleData> list = new ArrayList<>();
        for (int i = 0; i < TEST_DATA_SIZE; i++) {
            SimpleData simpleData = new SimpleData();
            simpleData.setName("姓名" + i);
            list.add(simpleData);
        }
        return list;
    }
}
