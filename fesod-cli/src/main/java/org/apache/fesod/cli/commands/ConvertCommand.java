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

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

/**
 * Convert command implementation
 */
@Command(
        name = "convert",
        description = "Convert spreadsheet between different formats",
        mixinStandardHelpOptions = true)
public class ConvertCommand extends BaseCommand {

    @Parameters(index = "0", description = "Input file path", paramLabel = "<input>")
    private String inputFile;

    @Parameters(index = "1", description = "Output file path", paramLabel = "<output>")
    private String outputFile;

    @Option(
            names = {"-s", "--sheet"},
            description = "Sheet index (0-based) to convert. If not specified, all sheets will be converted.")
    private Integer sheetIndex;

    @Option(
            names = {"-n", "--sheet-name"},
            description = "Sheet name to convert")
    private String sheetName;

    @Option(
            names = {"-a", "--all"},
            description = "Convert all sheets (default if no sheet is specified)")
    private Boolean convertAll;

    @Override
    protected void execute() {
        Path input = Paths.get(inputFile);
        Path output = Paths.get(outputFile);

        Map<String, Object> options = new HashMap<String, Object>();
        options.put("sheetIndex", sheetIndex);
        options.put("sheetName", sheetName);
        options.put("convertAll", convertAll);

        processor.convert(input, output, options);

        String sheetInfo = "";
        if (sheetIndex != null) {
            sheetInfo = " (sheet " + sheetIndex + ")";
        } else if (sheetName != null) {
            sheetInfo = " (sheet '" + sheetName + "')";
        } else {
            sheetInfo = " (all sheets)";
        }

        getOut().println("✓ Conversion completed" + sheetInfo + ":  " + inputFile + " → " + outputFile);
    }
}
