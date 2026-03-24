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

import java.io.PrintWriter;
import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Spec;

/**
 * Version command implementation
 */
@Command(name = "version", description = "Display version information", mixinStandardHelpOptions = true)
public class VersionCommand implements Runnable {

    @Spec
    CommandSpec spec;

    @Override
    public void run() {
        PrintWriter out = spec.commandLine().getOut();
        out.println("Apache Fesod CLI");
        out.println("Java Version: " + System.getProperty("java.version"));
        out.println("OS: " + System.getProperty("os.name") + " " + System.getProperty("os.arch"));
        out.println();
        out.println("Copyright © 2025 The Apache Software Foundation");
        out.println("Licensed under the Apache License 2.0");
    }
}
