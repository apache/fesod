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

package org.apache.fesod.sheet.benchmark.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.fesod.sheet.benchmark.core.BenchmarkConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class for managing benchmark test files
 */
public class BenchmarkFileUtil {

    private static final Logger logger = LoggerFactory.getLogger(BenchmarkFileUtil.class);

    private static final String TEST_DATA_DIR = "target/benchmark-testdata";

    /**
     * Create test data directory if it doesn't exist
     */
    public static void createTestDataDirectory() {
        try {
            Path testDataPath = Paths.get(TEST_DATA_DIR);
            if (!Files.exists(testDataPath)) {
                Files.createDirectories(testDataPath);
                logger.debug("Created test data directory: {}", testDataPath);
            }
        } catch (IOException e) {
            logger.error("Failed to create test data directory", e);
            throw new RuntimeException("Failed to create test data directory", e);
        }
    }

    /**
     * Generate a temporary file path for benchmarks
     */
    public static String getTempFilePath(
            BenchmarkConfiguration.FileFormat format, BenchmarkConfiguration.DatasetSize size, String benchmarkName) {
        createTestDataDirectory();

        String fileName = String.format(
                "temp_%s_%s_%s_%d.%s",
                benchmarkName,
                size.getLabel(),
                format.name().toLowerCase(),
                System.currentTimeMillis(),
                format.getExtension());
        return TEST_DATA_DIR + File.separator + fileName;
    }

    /**
     * Clean up temporary files created during benchmarks
     */
    public static void cleanupTempFiles() {
        try {
            Path testDataPath = Paths.get(TEST_DATA_DIR);
            if (Files.exists(testDataPath)) {
                Files.walk(testDataPath)
                        .filter(path -> path.getFileName().toString().startsWith("temp_"))
                        .forEach(path -> {
                            try {
                                Files.deleteIfExists(path);
                                logger.debug("Deleted temp file: {}", path);
                            } catch (IOException e) {
                                logger.warn("Failed to delete temp file: {}", path, e);
                            }
                        });
            }
        } catch (IOException e) {
            logger.warn("Failed to cleanup temp files", e);
        }
    }

    /**
     * Get file size in bytes
     */
    public static long getFileSize(String filePath) {
        try {
            return Files.size(Paths.get(filePath));
        } catch (IOException e) {
            logger.warn("Failed to get file size for: {}", filePath, e);
            return 0;
        }
    }

    /**
     * Get file size in human readable format
     */
    public static String getFileSizeFormatted(String filePath) {
        long bytes = getFileSize(filePath);
        return formatBytes(bytes);
    }

    /**
     * Format bytes into human readable format
     */
    public static String formatBytes(long bytes) {
        if (bytes < 1024) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(1024));
        String pre = "KMGTPE".charAt(exp - 1) + "";
        return String.format("%.1f %sB", bytes / Math.pow(1024, exp), pre);
    }

    /**
     * Create a test file with the specified name
     */
    public static File createTestFile(String fileName) {
        createTestDataDirectory();
        return new File(TEST_DATA_DIR, fileName);
    }

    /**
     * Read a string from a file
     * @param path the path to the file
     * @return the content of the file as a string
     * @throws IOException if an I/O error occurs
     */
    public static String readString(Path path) throws IOException {
        return new String(Files.readAllBytes(path));
    }

    /**
     * Create a list with the specified elements
     * @param elements the elements to include in the list
     * @return a list containing the specified elements
     */
    @SafeVarargs
    public static <T> List<T> listOf(T... elements) {
        List<T> list = new ArrayList<>(elements.length);
        list.addAll(Arrays.asList(elements));
        return list;
    }
}
