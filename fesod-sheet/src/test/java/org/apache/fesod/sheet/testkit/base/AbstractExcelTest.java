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
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
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

    // --- Resource File Access ---

    /**
     * Materializes a classpath resource into a real {@link File} under {@link #tempDir} and returns it.
     *
     * <p>This replaces the legacy {@code TestFileUtil.readFile()}, which derived a path from
     * {@code getResource("/").getPath()}. That approach is unreliable: the returned string is
     * {@linkplain java.net.URLDecode URL-encoded}, so any project path containing a space (common on
     * macOS, e.g. {@code /Users/John Doe/...}) yields {@code /Users/John%20Doe/...}, which does not
     * resolve to an existing OS file and causes {@link java.io.FileNotFoundException}. It also breaks
     * entirely when the resources live inside a packaged JAR.
     *
     * <p>Reading the resource as a stream and copying it to the per-test temp directory is robust in
     * all of those cases. The {@code resourcePath} may use either {@code /} or
     * {@link File#separator File.separator}; segments are normalized to {@code /} for the classpath
     * lookup.
     *
     * @param resourcePath classpath-relative resource location (e.g. {@code "template/template07.xlsx"})
     * @return a real {@link File} backed by a copy of the resource
     * @throws UncheckedIOException if the resource is missing or cannot be copied
     */
    protected File readFile(String resourcePath) {
        String normalized = resourcePath.replace(File.separatorChar, '/');
        Path target = tempDir.toPath().resolve(normalized.replace('/', File.separatorChar));
        try {
            Files.createDirectories(target.getParent());
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to create directories for " + target, e);
        }
        try (InputStream in = getClass().getClassLoader().getResourceAsStream(normalized)) {
            if (in == null) {
                throw new UncheckedIOException(
                        new java.io.FileNotFoundException("classpath resource not found: " + normalized));
            }
            Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to copy resource " + normalized + " to " + target, e);
        }
        return target.toFile();
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
