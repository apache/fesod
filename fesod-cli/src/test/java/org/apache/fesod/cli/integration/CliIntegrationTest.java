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

package org.apache.fesod.cli.integration;

import static org.junit.jupiter.api.Assertions.*;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Path;
import org.apache.fesod.cli.FesodCli;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import picocli.CommandLine;

/**
 * Integration tests for CLI commands
 */
class CliIntegrationTest {

    @TempDir
    Path tempDir;

    @Test
    void testCliHelp() {
        FesodCli cli = new FesodCli();
        CommandLine cmd = new CommandLine(cli);

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        cmd.setOut(pw);

        int exitCode = cmd.execute("--help");
        assertEquals(0, exitCode);

        String output = sw.toString();
        assertTrue(output.contains("Apache Fesod CLI"));
        assertTrue(output.contains("read"));
        assertTrue(output.contains("write"));
        assertTrue(output.contains("convert"));
        assertTrue(output.contains("info"));
        assertTrue(output.contains("version"));
    }

    @Test
    void testCliVersion() {
        FesodCli cli = new FesodCli();
        CommandLine cmd = new CommandLine(cli);

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        cmd.setOut(pw);

        int exitCode = cmd.execute("version");
        assertEquals(0, exitCode);

        String output = sw.toString();
        assertTrue(output.contains("Apache Fesod CLI"));
        assertTrue(output.contains("Version: 2.0.0"));
        assertTrue(output.contains("Java Version"));
        assertTrue(output.contains("OS:"));
    }

    @Test
    void testCliWithNoArgs() {
        FesodCli cli = new FesodCli();
        CommandLine cmd = new CommandLine(cli);

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        cmd.setOut(pw);

        int exitCode = cmd.execute();
        assertEquals(0, exitCode);

        String output = sw.toString();
        assertTrue(output.contains("Apache Fesod CLI"));
        assertTrue(output.contains("read"));
        assertTrue(output.contains("write"));
        assertTrue(output.contains("convert"));
    }

    @Test
    void testCliWithVerboseFlag() {
        FesodCli cli = new FesodCli();
        CommandLine cmd = new CommandLine(cli);

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        cmd.setOut(pw);

        // Test with verbose flag (should still show help)
        int exitCode = cmd.execute("--verbose");
        assertEquals(0, exitCode);

        String output = sw.toString();
        assertTrue(output.contains("Apache Fesod CLI"));
    }

    @Test
    void testReadCommandHelp() {
        FesodCli cli = new FesodCli();
        CommandLine cmd = new CommandLine(cli);

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        cmd.setOut(pw);

        int exitCode = cmd.execute("read", "--help");
        assertEquals(0, exitCode);

        String output = sw.toString();
        assertTrue(output.contains("Read spreadsheet data"));
        assertTrue(output.contains("--format"));
        assertTrue(output.contains("--sheet"));
        assertTrue(output.contains("--output"));
        assertTrue(output.contains("--all"));
    }

    @Test
    void testWriteCommandHelp() {
        FesodCli cli = new FesodCli();
        CommandLine cmd = new CommandLine(cli);

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        cmd.setOut(pw);

        int exitCode = cmd.execute("write", "--help");
        assertEquals(0, exitCode);

        String output = sw.toString();
        assertTrue(output.contains("Write data from JSON/CSV to spreadsheet"));
        assertTrue(output.contains("--input-format"));
        assertTrue(output.contains("--sheet-name"));
    }

    @Test
    void testConvertCommandHelp() {
        FesodCli cli = new FesodCli();
        CommandLine cmd = new CommandLine(cli);

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        cmd.setOut(pw);

        int exitCode = cmd.execute("convert", "--help");
        assertEquals(0, exitCode);

        String output = sw.toString();
        assertTrue(output.contains("Convert spreadsheet between different formats"));
    }

    @Test
    void testInfoCommandHelp() {
        FesodCli cli = new FesodCli();
        CommandLine cmd = new CommandLine(cli);

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        cmd.setOut(pw);

        int exitCode = cmd.execute("info", "--help");
        assertEquals(0, exitCode);

        String output = sw.toString();
        assertTrue(output.contains("Display spreadsheet file information"));
    }

    @Test
    void testInvalidCommand() {
        FesodCli cli = new FesodCli();
        CommandLine cmd = new CommandLine(cli);

        int exitCode = cmd.execute("invalid-command");
        assertEquals(2, exitCode); // Invalid command should return exit code 2
    }

    @Test
    void testCliErrorHandling() {
        FesodCli cli = new FesodCli();
        CommandLine cmd = new CommandLine(cli);

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        cmd.setErr(pw);

        // Test with invalid arguments
        int exitCode = cmd.execute("read", "nonexistent.xlsx");
        assertEquals(1, exitCode);

        String errorOutput = sw.toString();
        assertTrue(errorOutput.contains("Error:")); // Should contain error message
    }
}
