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
import java.util.stream.Stream;
import org.apache.fesod.sheet.testkit.enums.ApiMode;
import org.apache.fesod.sheet.testkit.enums.ExcelFormat;
import org.apache.fesod.sheet.testkit.helpers.RoundTripHelper;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.provider.Arguments;

/**
 * Base class for modernized test classes. Provides:
 * <ul>
 *   <li>A JUnit 5 {@code @TempDir} for isolated temp files</li>
 *   <li>Static method sources for parameterized tests</li>
 *   <li>Convenience helpers delegating to {@link RoundTripHelper}</li>
 * </ul>
 */
public abstract class AbstractExcelTest {

    @TempDir
    protected File tempDir;

    // --- Format Providers (static, for @MethodSource) ---

    protected static Stream<ExcelFormat> allFormats() {
        return Stream.of(ExcelFormat.values());
    }

    protected static Stream<ExcelFormat> binaryFormats() {
        return Stream.of(ExcelFormat.XLSX, ExcelFormat.XLS);
    }

    protected static Stream<Arguments> allFormatsWithApiMode() {
        Stream.Builder<Arguments> builder = Stream.builder();
        for (ExcelFormat format : ExcelFormat.values()) {
            for (ApiMode mode : ApiMode.values()) {
                builder.add(Arguments.of(format, mode));
            }
        }
        return builder.build();
    }

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
