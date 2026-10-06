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

package org.apache.fesod.sheet.benchmark.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Central place for the scratch directory benchmark files are written to
 * ({@code target/benchmark-testdata}), so all suites share one location that
 * Maven cleans up on {@code mvn clean}.
 */
public class BenchmarkFileUtil {

    private static final Logger logger = LoggerFactory.getLogger(BenchmarkFileUtil.class);

    private static final String TEST_DATA_DIR = "target/benchmark-testdata";

    /**
     * Create test data directory if it doesn't exist.
     */
    public static void createTestDataDirectory() {
        Path testDataPath = Paths.get(TEST_DATA_DIR);
        if (!Files.exists(testDataPath)) {
            try {
                Files.createDirectories(testDataPath);
            } catch (IOException e) {
                throw new IllegalStateException("Failed to create test data directory: " + testDataPath, e);
            }
        }
    }

    /**
     * Create a test file handle with the specified name inside the test data directory.
     */
    public static File createTestFile(String fileName) {
        createTestDataDirectory();
        return new File(TEST_DATA_DIR, fileName);
    }

    /**
     * Delete a benchmark scratch file, logging the cause when deletion fails
     * instead of failing the run.
     */
    public static void delete(File file) {
        if (file == null) {
            return;
        }
        try {
            Files.deleteIfExists(file.toPath());
        } catch (IOException e) {
            logger.warn("Failed to delete benchmark file {}: {}", file, e.getMessage());
        }
    }
}
