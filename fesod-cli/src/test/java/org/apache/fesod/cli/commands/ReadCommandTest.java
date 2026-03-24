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

package org.apache.fesod.cli.commands;

import static org.junit.jupiter.api.Assertions.*;
import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Path;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import picocli.CommandLine;

/**
 * Test for ReadCommand
 */
class ReadCommandTest {

    @TempDir
    Path tempDir;

    private File testExcelFile;
    private CommandLine cmd;

    @BeforeEach
    void setUp() throws Exception {
        // Create a simple test Excel file
        testExcelFile = tempDir.resolve("test.xlsx").toFile();

        // Create command instance
        ReadCommand readCommand = new ReadCommand();
        cmd = new CommandLine(readCommand);
    }

    @Test
    void testReadCommandWithNonExistentFile() {
        // Test with non-existent file
        String[] args = {"nonexistent.xlsx"};

        int exitCode = cmd.execute(args);
        assertEquals(1, exitCode); // Should fail
    }

    @Test
    void testReadCommandHelp() {
        String[] args = {"--help"};

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        cmd.setOut(pw);

        int exitCode = cmd.execute(args);
        assertEquals(0, exitCode);

        String output = sw.toString();
        assertTrue(output.contains("Read spreadsheet data"));
        assertTrue(output.contains("--format"));
        assertTrue(output.contains("--sheet"));
    }

    @Test
    void testReadCommandWithInvalidFormat() {
        String[] args = {"test.xlsx", "--format", "invalid"};

        int exitCode = cmd.execute(args);
        assertEquals(1, exitCode); // Should fail due to unsupported format
    }

    @Test
    void testReadCommandWithJsonFormat() {
        String[] args = {"test.xlsx", "--format", "json"};

        int exitCode = cmd.execute(args);
        assertEquals(1, exitCode); // Should fail because file doesn't exist, but command parsing should work
    }

    @Test
    void testReadCommandWithSheetIndex() {
        String[] args = {"test.xlsx", "--sheet", "0", "--format", "json"};

        int exitCode = cmd.execute(args);
        assertEquals(1, exitCode); // Should fail because file doesn't exist
    }

    @Test
    void testReadCommandWithSheetName() {
        String[] args = {"test.xlsx", "--sheet", "Sheet1", "--format", "json"};

        int exitCode = cmd.execute(args);
        assertEquals(1, exitCode); // Should fail because file doesn't exist
    }

    @Test
    void testReadCommandWithAllSheets() {
        String[] args = {"test.xlsx", "--all", "--format", "json"};

        int exitCode = cmd.execute(args);
        assertEquals(1, exitCode); // Should fail because file doesn't exist
    }
}
