/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.   You may obtain a copy of the License at
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
package org.apache.fesod.cli.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * File utility class
 */
public class FileUtils {

    private static final Logger log = LoggerFactory.getLogger(FileUtils.class);

    /**
     * Check if file exists and is readable
     */
    public static boolean isReadableFile(String filePath) {
        if (filePath == null || filePath.trim().isEmpty()) {
            return false;
        }

        Path path = Paths.get(filePath);
        return Files.exists(path) && Files.isReadable(path) && Files.isRegularFile(path);
    }

    /**
     * Check if directory exists and is writable
     */
    public static boolean isWritableDirectory(String dirPath) {
        if (dirPath == null || dirPath.trim().isEmpty()) {
            return false;
        }

        Path path = Paths.get(dirPath);
        return Files.exists(path) && Files.isDirectory(path) && Files.isWritable(path);
    }

    /**
     * Create parent directories if they don't exist
     */
    public static void createParentDirectories(Path filePath) throws IOException {
        if (filePath.getParent() != null) {
            Files.createDirectories(filePath.getParent());
            log.debug("Created parent directories for: {}", filePath);
        }
    }

    /**
     * Get file extension
     */
    public static String getFileExtension(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return "";
        }

        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex > 0 && lastDotIndex < fileName.length() - 1) {
            return fileName.substring(lastDotIndex + 1).toLowerCase();
        }

        return "";
    }

    /**
     * Check if file has Excel extension
     */
    public static boolean isExcelFile(String filePath) {
        String extension = getFileExtension(filePath);
        return "xlsx".equals(extension) || "xls".equals(extension) || "csv".equals(extension);
    }

    /**
     * Get file size in human readable format
     */
    public static String getHumanReadableFileSize(long bytes) {
        if (bytes < 1024) {
            return bytes + " B";
        }
        int exp = (int) (Math.log(bytes) / Math.log(1024));
        String pre = "KMGTPE".charAt(exp - 1) + "";
        return String.format("%.1f %sB", bytes / Math.pow(1024, exp), pre);
    }

    /**
     * Validate output file path
     */
    public static void validateOutputFile(String outputPath) {
        if (outputPath == null || outputPath.trim().isEmpty()) {
            return; // stdout is acceptable
        }

        Path path = Paths.get(outputPath);
        if (Files.exists(path) && !Files.isRegularFile(path)) {
            throw new IllegalArgumentException("Output path is not a regular file: " + outputPath);
        }

        // Check if parent directory is writable
        if (path.getParent() != null && !isWritableDirectory(path.getParent().toString())) {
            throw new IllegalArgumentException("Output directory is not writable: " + path.getParent());
        }
    }
}

