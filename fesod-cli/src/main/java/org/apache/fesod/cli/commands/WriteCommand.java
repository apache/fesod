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

import com.alibaba.fastjson2.JSON;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

/**
 * Write command implementation
 */
@Command(name = "write", description = "Write data from JSON/CSV to spreadsheet", mixinStandardHelpOptions = true)
public class WriteCommand extends BaseCommand {

    @Parameters(index = "0", description = "Input data file (JSON/CSV)", paramLabel = "<input>")
    private String inputFile;

    @Parameters(index = "1", description = "Output spreadsheet file", paramLabel = "<output>")
    private String outputFile;

    @Option(
            names = {"--input-format"},
            description = "Input data format: json, csv (default:  json)",
            defaultValue = "json")
    private String inputFormat;

    @Option(
            names = {"--sheet-name"},
            description = "Sheet name (default: Sheet1)",
            defaultValue = "Sheet1")
    private String sheetName;

    @Override
    protected void execute() {
        try {
            Path input = Paths.get(inputFile);
            Path output = Paths.get(outputFile);

            String content = new String(Files.readAllBytes(input), "UTF-8");
            Map<String, Object> data = new HashMap<String, Object>();

            if ("json".equalsIgnoreCase(inputFormat)) {
                data.put("data", JSON.parse(content));
            } else {
                throw new UnsupportedOperationException("CSV input format not yet implemented");
            }

            Map<String, Object> options = new HashMap<String, Object>();
            options.put("sheetName", sheetName);

            processor.write(data, output, options);

            getOut().println("✓ Data written to: " + outputFile);

        } catch (Exception e) {
            throw new RuntimeException("Failed to write data:  " + e.getMessage(), e);
        }
    }
}
