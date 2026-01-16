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

package org.apache.fesod.sheet.testkit.base;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;
import org.apache.fesod.sheet.FesodSheet;
import org.apache.fesod.sheet.testkit.enums.ExcelFormat;
import org.apache.fesod.sheet.testkit.listeners.CollectingReadListener;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.provider.Arguments;

/**
 * Abstract base class for Excel tests providing common infrastructure.
 *
 * <p>Features:</p>
 * <ul>
 *   <li>Automatic temp directory management via JUnit 5 @TempDir</li>
 *   <li>Format provider methods for parameterized tests</li>
 *   <li>Common read/write operations</li>
 *   <li>Format-aware file creation</li>
 * </ul>
 *
 * <p>Usage:</p>
 * <pre>{@code
 * class MyTest extends AbstractExcelTest {
 *     @ParameterizedTest
 *     @MethodSource("allFormats")
 *     void shouldReadAndWrite(ExcelFormat format) {
 *         File file = createTempFile("test", format);
 *         // test logic...
 *     }
 * }
 * }</pre>
 */
public abstract class AbstractExcelTest {

    @TempDir
    protected Path tempDir;

    // ==================== Format Providers ====================

    /**
     * Provides all supported formats: XLSX, XLS, CSV
     */
    protected static Stream<Arguments> allFormats() {
        return Stream.of(Arguments.of(ExcelFormat.XLSX), Arguments.of(ExcelFormat.XLS), Arguments.of(ExcelFormat.CSV));
    }

    /**
     * Provides Excel-only formats: XLSX, XLS (excludes CSV)
     */
    protected static Stream<Arguments> excelFormats() {
        return Stream.of(Arguments.of(ExcelFormat.XLSX), Arguments.of(ExcelFormat.XLS));
    }

    /**
     * Provides formats that support styling
     */
    protected static Stream<Arguments> styledFormats() {
        return Stream.of(ExcelFormat.values())
                .filter(ExcelFormat::supportsStyle)
                .map(Arguments::of);
    }

    /**
     * Provides only XLSX format (for streaming tests)
     */
    protected static Stream<Arguments> xlsxOnly() {
        return Stream.of(Arguments.of(ExcelFormat.XLSX));
    }

    // ==================== File Operations ====================

    /**
     * Creates a temp file with the given name and format extension.
     * @param baseName base file name (without extension)
     * @param format the Excel format
     * @return the created file
     */
    protected File createTempFile(String baseName, ExcelFormat format) {
        return tempDir.resolve(baseName + format.getExtension()).toFile();
    }

    /**
     * Creates a temp file with unique name based on test method.
     */
    protected File createTempFile(ExcelFormat format, org.junit.jupiter.api.TestInfo testInfo) {
        String methodName = testInfo.getTestMethod().map(m -> m.getName()).orElse("test");
        return createTempFile(methodName, format);
    }

    // ==================== Common Operations ====================

    /**
     * Writes data to file using FesodSheet.
     */
    protected <T> void writeData(File file, Class<T> clazz, List<T> data) {
        FesodSheet.write(file, clazz).sheet().doWrite(data);
    }

    /**
     * Writes data to a named sheet.
     */
    protected <T> void writeData(File file, Class<T> clazz, List<T> data, String sheetName) {
        FesodSheet.write(file, clazz).sheet(sheetName).doWrite(data);
    }

    /**
     * Reads data from file synchronously.
     */
    protected <T> List<T> readData(File file, Class<T> clazz) {
        CollectingReadListener<T> listener = new CollectingReadListener<>();
        FesodSheet.read(file, clazz, listener).sheet().doRead();
        return listener.getData();
    }

    /**
     * Reads data from a named sheet.
     */
    protected <T> List<T> readData(File file, Class<T> clazz, String sheetName) {
        CollectingReadListener<T> listener = new CollectingReadListener<>();
        FesodSheet.read(file, clazz, listener).sheet(sheetName).doRead();
        return listener.getData();
    }

    /**
     * Performs a write-read roundtrip and returns the read data.
     */
    protected <T> List<T> roundTrip(File file, Class<T> clazz, List<T> data) {
        writeData(file, clazz, data);
        return readData(file, clazz);
    }
}
