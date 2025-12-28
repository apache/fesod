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
package org.apache.fesod.cli;

import org.apache.fesod.cli.commands.ConvertCommand;
import org.apache.fesod.cli.commands.InfoCommand;
import org.apache.fesod.cli.commands.ReadCommand;
import org.apache.fesod.cli.commands.VersionCommand;
import org.apache.fesod.cli.commands.WriteCommand;
import org.apache.fesod.cli.exception.CliException;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/**
 * Apache Fesod CLI - Main Entry Point
 * 
 * @author Apache Fesod Team
 * @since 2.0.0
 */
@Command(
    name = "fesod-cli",
    mixinStandardHelpOptions = true,
    version = {
        "Apache Fesod CLI 2.0.0",
        "Java Runtime:  ${java.version}",
        "OS: ${os.name} ${os.arch}"
    },
    description = "Fast and Easy spreadsheet processing from the command line",
    subcommands = {
        ReadCommand.class,
        WriteCommand.class,
        ConvertCommand.class,
        InfoCommand.class,
        VersionCommand.class,
        CommandLine.HelpCommand.class
    },
    usageHelpAutoWidth = true,
    footer = {
        "",
        "Examples:",
        "  fesod-cli read data.xlsx --format json",
        "  fesod-cli convert input.xls output.xlsx",
        "  fesod-cli info data.xlsx",
        "",
        "Documentation:  https://fesod.apache.org/docs/cli",
        "Report bugs:  https://github.com/apache/fesod/issues"
    }
)
public class FesodCli implements Runnable {

    @Option(
        names = {"--verbose", "-v"},
        description = "Enable verbose logging"
    )
    private boolean verbose;

    @Override
    public void run() {
        // Default:  show help when no command specified
        CommandLine.usage(this, System.out);
    }

    public static void main(String[] args) {
        int exitCode = new CommandLine(new FesodCli())
            .setExecutionExceptionHandler((ex, cmd, parseResult) -> {
                cmd.getErr().println(cmd.getColorScheme().errorText("Error: " + ex.getMessage()));
                
                if (ex instanceof CliException) {
                    CliException cliEx = (CliException) ex;
                    if (cliEx.getCause() != null && parseResult.hasMatchedOption("--verbose")) {
                        cliEx.printStackTrace(cmd.getErr());
                    }
                    return cliEx.getExitCode();
                } else {
                    if (parseResult.hasMatchedOption("--verbose")) {
                        ex.printStackTrace(cmd.getErr());
                    }
                    return 1;
                }
            })
            .execute(args);
        
        System.exit(exitCode);
    }
}

