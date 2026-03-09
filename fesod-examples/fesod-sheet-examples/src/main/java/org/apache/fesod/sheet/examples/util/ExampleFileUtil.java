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

package org.apache.fesod.sheet.examples.util;

import java.io.File;
import java.nio.file.Files;
import lombok.SneakyThrows;

/**
 * Utility class for locating example input files and generating output file paths.
 *
 * <h2>File Locations</h2>
 * <ul>
 *   <li><b>Input files:</b> Located in the classpath under {@code example/} directory
 *       (e.g., {@code src/main/resources/example/demo.xlsx}).
 *       Access via {@link #getExamplePath(String)}.</li>
 *   <li><b>Output files:</b> Written to the system temp directory to avoid polluting
 *       the project workspace. Access via {@link #getTempPath(String)}.</li>
 * </ul>
 *
 * <h2>Usage</h2>
 * <pre>{@code
 * // Get path to an input example file
 * String input = ExampleFileUtil.getExamplePath("demo.xlsx");
 * String template = ExampleFileUtil.getExamplePath("templates/simple.xlsx");
 *
 * // Get path for an output file (in temp directory)
 * String output = ExampleFileUtil.getTempPath("result.xlsx");
 * }</pre>
 */
public class ExampleFileUtil {

    public static final String EXAMPLE = "example";

    public static String getPath() {
        java.net.URL resource = ExampleFileUtil.class.getClassLoader().getResource("");
        if (resource == null) {
            throw new IllegalStateException("Cannot find classpath root resource");
        }
        return resource.getPath();
    }

    /**
     * Get the path to a file in the example resource directory.
     *
     * @param fileName the file name relative to the example directory (e.g., "demo.xlsx" or "templates/simple.xlsx")
     * @return the full path to the file
     */
    public static String getExamplePath(String fileName) {
        java.net.URL resource = ExampleFileUtil.class.getClassLoader().getResource(EXAMPLE + "/" + fileName);
        if (resource != null) {
            return resource.getPath();
        }
        // Fallback to classpath root + example path
        return getPath() + EXAMPLE + File.separator + fileName;
    }

    /**
     * Get the path to write output files in the system temp directory.
     *
     * @param fileName the output file name
     * @return the full path to the output file in temp directory
     */
    @SneakyThrows
    public static String getTempPath(String fileName) {
        return Files.createTempDirectory("fesod-sheet-examples").toAbsolutePath() + File.separator + fileName;
    }
}
