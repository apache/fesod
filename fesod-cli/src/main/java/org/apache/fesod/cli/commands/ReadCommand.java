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
package org.apache.fesod.cli.commands;

import org.apache.fesod.cli.formatters.FormatterFactory;
import org.apache.fesod.cli.formatters.OutputFormatter;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * Read command implementation
 */
@Command(
    name = "read",
    description = "Read spreadsheet data and output in specified format",
    mixinStandardHelpOptions = true
)
public class ReadCommand extends BaseCommand {
    
    @Parameters(
        index = "0",
        description = "Input file path",
        paramLabel = "<file>"
    )
    private String inputFile;
    
    @Option(
        names = {"--format", "-f"},
        description = "Output format: json, csv (default: json)",
        defaultValue = "json"
    )
    private String format;
    
    @Option(
        names = {"--sheet", "-s"},
        description = "Sheet name or index (default: 0)",
        paramLabel = "<name|index>"
    )
    private String sheet;
    
    @Option(
        names = {"--output", "-o"},
        description = "Output file path (default: stdout)",
        paramLabel = "<file>"
    )
    private String outputFile;
    
    @Option(
        names = {"--all"},
        description = "Read all sheets"
    )
    private boolean readAll;
    
    @Override
    protected void execute() {
        Path input = Paths.get(inputFile);
        
        Map<String, Object> options = new HashMap<String, Object>();
        
        if (sheet != null) {
            try {
                int sheetIndex = Integer.parseInt(sheet);
                options.put("sheetIndex", sheetIndex);
            } catch (NumberFormatException e) {
                options.put("sheetName", sheet);
            }
        }
        
        options.put("readAll", readAll);
        
        Map<String, Object> data = processor.read(input, options);
        
        OutputFormatter formatter = FormatterFactory.getFormatter(format);
        String output = formatter.format(data);
        
        if (outputFile != null) {
            formatter.writeToFile(output, Paths.get(outputFile));
            System.out.println("✓ Output written to: " + outputFile);
        } else {
            System.out.println(output);
        }
    }
}

