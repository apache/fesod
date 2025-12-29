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
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Path;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import picocli.CommandLine;

/**
 * Test for ConvertCommand
 */
class ConvertCommandTest {

    @TempDir
    Path tempDir;

    private CommandLine cmd;

    @BeforeEach
    void setUp() {
        // Create command instance
        ConvertCommand convertCommand = new ConvertCommand();
        cmd = new CommandLine(convertCommand);
    }

    @Test
    void testConvertCommandHelp() {
        String[] args = {"--help"};

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        cmd.setOut(pw);

        int exitCode = cmd.execute(args);
        assertEquals(0, exitCode);

        String output = sw.toString();
        assertTrue(output.contains("Convert spreadsheet between different formats"));
    }

    @Test
    void testConvertCommandWithMissingArguments() {
        String[] args = {"input.xlsx"};

        int exitCode = cmd.execute(args);
        assertEquals(2, exitCode); // Missing required argument
    }

    @Test
    void testConvertCommandWithNonExistentInput() {
        String[] args = {"nonexistent.xlsx", "output.xlsx"};

        int exitCode = cmd.execute(args);
        assertEquals(1, exitCode); // Should fail due to missing input file
    }

    @Test
    void testConvertCommandWithSameInputOutput() {
        String[] args = {"same.xlsx", "same.xlsx"};

        int exitCode = cmd.execute(args);
        assertEquals(1, exitCode); // Should fail
    }
}
