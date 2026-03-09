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

package org.apache.fesod.sheet.examples;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

/**
 * Base class for Fesod example integration tests.
 *
 * <p>Provides common utilities for verifying Excel file output, following the patterns established
 * by Apache Flink's {@code ExampleOutputTestBase} and {@code AbstractTestBase}.
 *
 * <p>Key utilities:
 * <ul>
 *   <li>{@link #assertValidExcelFile(File)} — verifies a file is a readable Excel workbook</li>
 *   <li>{@link #assertValidExcelFile(File, int)} — additionally verifies minimum row count</li>
 *   <li>{@link #getTempOutputPath(Path, String)} — generates a temp file path within a directory</li>
 * </ul>
 */
public abstract class ExampleTestBase {

    /**
     * Assert that the given file exists, is non-empty, and is a valid Excel workbook that
     * Apache POI can open.
     *
     * @param file the Excel file to validate
     */
    protected static void assertValidExcelFile(File file) {
        assertNotNull(file, "File reference must not be null");
        assertTrue(file.exists(), "File should exist: " + file.getAbsolutePath());
        assertTrue(file.length() > 0, "File should not be empty: " + file.getAbsolutePath());

        try (FileInputStream fis = new FileInputStream(file);
                Workbook workbook = WorkbookFactory.create(fis)) {
            assertNotNull(workbook, "Workbook should be readable");
            assertTrue(workbook.getNumberOfSheets() > 0, "Workbook should have at least one sheet");
        } catch (IOException e) {
            fail("File should be a valid Excel workbook: " + file.getAbsolutePath() + ", error: " + e.getMessage());
        }
    }

    /**
     * Assert that the given file is a valid Excel workbook with at least the specified number of
     * data rows (excluding header).
     *
     * @param file the Excel file to validate
     * @param minDataRows the minimum number of data rows expected (excluding header row)
     */
    protected static void assertValidExcelFile(File file, int minDataRows) {
        assertValidExcelFile(file);

        try (FileInputStream fis = new FileInputStream(file);
                Workbook workbook = WorkbookFactory.create(fis)) {
            int totalRows = workbook.getSheetAt(0).getPhysicalNumberOfRows();
            // totalRows includes header row, so data rows = totalRows - 1
            assertTrue(
                    totalRows > minDataRows,
                    "Expected at least " + minDataRows + " data rows (plus header), but found " + totalRows
                            + " total rows in: " + file.getAbsolutePath());
        } catch (IOException e) {
            fail("Failed to read workbook for row count verification: " + e.getMessage());
        }
    }

    /**
     * Generate a temp output file path within the given directory.
     *
     * @param tempDir the temporary directory (typically from {@code @TempDir})
     * @param fileName the desired filename
     * @return the absolute path as a String
     */
    protected static String getTempOutputPath(Path tempDir, String fileName) {
        return tempDir.resolve(fileName).toAbsolutePath().toString();
    }
}
