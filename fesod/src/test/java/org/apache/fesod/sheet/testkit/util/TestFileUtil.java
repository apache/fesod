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

package org.apache.fesod.sheet.testkit.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.fesod.sheet.testkit.enums.ExcelFormat;

/**
 * Enhanced test file utilities with format-aware methods.
 * Extends the existing TestFileUtil functionality.
 */
public class TestFileUtil {

    /**
     * Creates a new temporary file with the given name and format extension.
     */
    public static File createTempFile(String name, ExcelFormat format) {
        String fileName = name + format.getExtension();
        File tempFile = new File(System.getProperty("java.io.tmpdir"), fileName);
        // Make sure file doesn't exist
        if (tempFile.exists()) {
            tempFile.delete();
        }
        return tempFile;
    }

    /**
     * Creates a new temporary file with unique name for the specified format.
     */
    public static File createUniqueTempFile(ExcelFormat format) {
        return createTempFile(
                "test_" + System.currentTimeMillis() + "_"
                        + Thread.currentThread().getId(),
                format);
    }

    /**
     * Creates a new temporary file with unique name and specified base name for the format.
     */
    public static File createUniqueTempFile(String baseName, ExcelFormat format) {
        return createTempFile(
                baseName + "_" + System.currentTimeMillis() + "_"
                        + Thread.currentThread().getId(),
                format);
    }

    /**
     * Checks if a file exists and is readable.
     */
    public static boolean isFileReadable(File file) {
        return file != null && file.exists() && file.canRead() && file.length() > 0;
    }

    /**
     * Gets the format of a file based on its extension.
     */
    public static ExcelFormat getFileFormat(File file) {
        String fileName = file.getName().toLowerCase();
        if (fileName.endsWith(".xlsx")) {
            return ExcelFormat.XLSX;
        } else if (fileName.endsWith(".xls")) {
            return ExcelFormat.XLS;
        } else if (fileName.endsWith(".csv")) {
            return ExcelFormat.CSV;
        } else {
            throw new IllegalArgumentException("Unknown file format: " + fileName);
        }
    }

    /**
     * Copies a file resource to a temporary file.
     */
    public static File copyResourceToFile(String resourceName, File destination) throws IOException {
        try (InputStream inputStream = TestFileUtil.class.getClassLoader().getResourceAsStream(resourceName)) {
            if (inputStream == null) {
                throw new IOException("Resource not found: " + resourceName);
            }

            java.nio.file.Files.copy(inputStream, destination.toPath());
            return destination;
        }
    }

    /**
     * Cleans up temporary files created during testing.
     */
    public static void cleanupTempFile(File file) {
        if (file != null && file.exists()) {
            file.delete();
        }
    }

    /**
     * Reads file contents as a byte array.
     */
    public static byte[] readFileAsBytes(File file) throws IOException {
        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] bytes = new byte[(int) file.length()];
            int read = fis.read(bytes);
            if (read != bytes.length) {
                throw new IOException("Could not read complete file");
            }
            return bytes;
        }
    }
}
