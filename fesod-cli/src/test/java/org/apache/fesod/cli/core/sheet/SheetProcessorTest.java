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

package org.apache.fesod.cli.core.sheet;

import static org.junit.jupiter.api.Assertions.*;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import org.apache.fesod.cli.exception.FileProcessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Test for SheetProcessor
 */
class SheetProcessorTest {

    @TempDir
    Path tempDir;

    private SheetProcessor processor;

    @BeforeEach
    void setUp() {
        processor = new SheetProcessor();
    }

    @Test
    void testGetModuleName() {
        assertEquals("sheet", processor.getModuleName());
    }

    @Test
    void testReadWithNonExistentFile() {
        Path nonExistentFile = tempDir.resolve("nonexistent.xlsx");
        Map<String, Object> options = new HashMap<>();

        assertThrows(FileProcessException.class, () -> {
            processor.read(nonExistentFile, options);
        });
    }

    @Test
    void testWriteWithNullData() {
        Path outputFile = tempDir.resolve("output.xlsx");
        Map<String, Object> options = new HashMap<>();
        options.put("sheetName", "TestSheet");

        assertThrows(FileProcessException.class, () -> {
            processor.write(null, outputFile, options);
        });
    }

    @Test
    void testConvertWithNonExistentFile() {
        Path inputFile = tempDir.resolve("nonexistent.xlsx");
        Path outputFile = tempDir.resolve("output.xlsx");
        Map<String, Object> options = new HashMap<>();

        assertThrows(FileProcessException.class, () -> {
            processor.convert(inputFile, outputFile, options);
        });
    }

    @Test
    void testGetInfoWithNonExistentFile() {
        Path nonExistentFile = tempDir.resolve("nonexistent.xlsx");

        assertThrows(FileProcessException.class, () -> {
            processor.getInfo(nonExistentFile);
        });
    }

    @Test
    void testReadWithSheetIndexOption() {
        Path nonExistentFile = tempDir.resolve("test.xlsx");
        Map<String, Object> options = new HashMap<>();
        options.put("sheetIndex", 0);

        assertThrows(FileProcessException.class, () -> {
            processor.read(nonExistentFile, options);
        });
    }

    @Test
    void testReadWithSheetNameOption() {
        Path nonExistentFile = tempDir.resolve("test.xlsx");
        Map<String, Object> options = new HashMap<>();
        options.put("sheetName", "Sheet1");

        assertThrows(FileProcessException.class, () -> {
            processor.read(nonExistentFile, options);
        });
    }

    @Test
    void testReadAllSheetsOption() {
        Path nonExistentFile = tempDir.resolve("test.xlsx");
        Map<String, Object> options = new HashMap<>();
        options.put("readAll", true);

        assertThrows(FileProcessException.class, () -> {
            processor.read(nonExistentFile, options);
        });
    }
}
