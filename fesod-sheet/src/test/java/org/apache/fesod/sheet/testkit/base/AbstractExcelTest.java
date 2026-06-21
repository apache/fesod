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
import java.io.IOException;
import java.util.List;
import org.apache.fesod.sheet.testkit.enums.ExcelFormat;
import org.apache.fesod.sheet.testkit.helpers.RoundTripHelper;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.io.TempDir;

/**
 * Base class for modernized test classes. Provides:
 * <ul>
 *   <li>A JUnit 5 {@code @TempDir} for isolated temp files</li>
 *   <li>Convenience helpers delegating to {@link RoundTripHelper}</li>
 * </ul>
 *
 * <h2>Parameterized Test Sources</h2>
 * <p>This class previously defined static {@code @MethodSource} providers ({@code allFormats()},
 * {@code binaryFormats()}, {@code allFormatsWithApiMode()}). These have been replaced by the
 * composed annotation {@link org.apache.fesod.sheet.testkit.params.ExcelFormatSource @ExcelFormatSource},
 * which is backed by a custom {@link org.junit.jupiter.params.provider.ArgumentsProvider}.
 *
 * <h3>Migration Guide</h3>
 * <pre>{@code
 * // Before:
 * @ParameterizedTest
 * @MethodSource("allFormats")
 * void readAndWrite(ExcelFormat format) { ... }
 *
 * // After:
 * @ParameterizedTest
 * @ExcelFormatSource
 * void readAndWrite(ExcelFormat format) { ... }
 *
 * // Before (binary only):
 * @MethodSource("binaryFormats")
 *
 * // After:
 * @ExcelFormatSource(BINARY)
 *
 * // Before (with API mode):
 * @MethodSource("allFormatsWithApiMode")
 *
 * // After:
 * @ExcelFormatSource(withApiMode = true)
 *
 * // Before (capability gating):
 * @MethodSource("allFormats")
 * void test(ExcelFormat format) {
 *     Assumptions.assumeTrue(format.supportsTemplates());
 * }
 *
 * // After:
 * @ExcelFormatSource(requires = TEMPLATES)
 * }</pre>
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class AbstractExcelTest {

    @TempDir
    protected File tempDir;

    // --- Temp File Management ---

    protected File createTempFile(ExcelFormat format) throws IOException {
        return format.createTempFile("test", tempDir);
    }

    protected File createTempFile(String prefix, ExcelFormat format) throws IOException {
        return format.createTempFile(prefix, tempDir);
    }

    // --- Convenience Helpers (delegate to RoundTripHelper) ---

    protected <T> void writeData(File file, Class<T> clazz, List<? extends T> data) {
        RoundTripHelper.write(file, clazz, data);
    }

    protected <T> List<T> readData(File file, Class<T> clazz) {
        return RoundTripHelper.read(file, clazz);
    }

    protected <T> List<T> writeAndRead(ExcelFormat format, Class<T> clazz, List<? extends T> data) throws IOException {
        File file = createTempFile(format);
        return RoundTripHelper.writeAndRead(file, clazz, data);
    }

    protected <W, R> List<R> writeAndRead(ExcelFormat format, Class<W> writeClazz, List<W> data, Class<R> readClazz)
            throws IOException {
        File file = createTempFile(format);
        return RoundTripHelper.writeAndRead(file, writeClazz, data, readClazz);
    }
}
