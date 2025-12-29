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

package org.apache.fesod.cli.config;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.yaml.snakeyaml.Yaml;

/**
 * Configuration loader
 */
public class ConfigLoader {

    private static final String DEFAULT_CONFIG_PATH = System.getProperty("user.home") + "/.fesod/config.yaml";

    public CliConfig loadDefault() {
        Path defaultPath = Paths.get(DEFAULT_CONFIG_PATH);

        if (Files.exists(defaultPath)) {
            return loadFromFile(defaultPath);
        }

        return createDefaultConfig();
    }

    public CliConfig loadFromFile(Path configPath) {
        try {
            InputStream is = Files.newInputStream(configPath);
            Yaml yaml = new Yaml();
            CliConfig config = yaml.loadAs(is, CliConfig.class);
            is.close();
            return config != null ? config : createDefaultConfig();
        } catch (Exception e) {
            System.err.println("Warning: Failed to load config from " + configPath + ", using defaults");
            return createDefaultConfig();
        }
    }

    private CliConfig createDefaultConfig() {
        return new CliConfig();
    }
}
