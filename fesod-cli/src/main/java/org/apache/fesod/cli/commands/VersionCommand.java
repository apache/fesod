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

import picocli.CommandLine.Command;

/**
 * Version command implementation
 */
@Command(
    name = "version",
    description = "Display version information",
    mixinStandardHelpOptions = true
)
public class VersionCommand implements Runnable {
    
    @Override
    public void run() {
        System.out.println("Apache Fesod CLI");
        System.out.println("Version: 2.0.0");
        System.out.println("Java Version: " + System.getProperty("java.version"));
        System.out.println("OS:  " + System.getProperty("os.name") + " " + System.getProperty("os.arch"));
        System.out.println();
        System.out.println("Copyright © 2025 The Apache Software Foundation");
        System.out.println("Licensed under the Apache License 2.0");
    }
}

