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

    private static final String USER_CONFIG_PATH = System.getProperty("user.home") + "/.fesod/config.yaml";

    public CliConfig loadDefault() {
        // 1. Try user home config (~/.fesod/config.yaml)
        Path userConfig = Paths.get(USER_CONFIG_PATH);
        if (Files.exists(userConfig)) {
            System.out.println("Loading config from: " + userConfig);
            return loadFromFile(userConfig);
        }

        // 2. Try FESOD_HOME/conf/default-config.yaml
        String fesodHome = System.getenv("FESOD_HOME");
        if (fesodHome != null && !fesodHome.isEmpty()) {
            Path installConfig = Paths.get(fesodHome, "conf", "default-config.yaml");
            if (Files.exists(installConfig)) {
                System.out.println("Loading config from: " + installConfig);
                return loadFromFile(installConfig);
            }
        }

        // 3. Try relative path: conf/default-config.yaml
        Path relativeConfig = Paths.get("conf", "default-config.yaml");
        if (Files.exists(relativeConfig)) {
            System.out.println("Loading config from: " + relativeConfig.toAbsolutePath());
            return loadFromFile(relativeConfig);
        }

        // 4. Fallback to embedded config in JAR
        System.out.println("Loading default config from JAR");
        return createDefaultConfig();
    }

    public CliConfig loadFromFile(Path configPath) {
        try {
            try (InputStream is = Files.newInputStream(configPath)) {
                Yaml yaml = new Yaml();
                CliConfig config = yaml.loadAs(is, CliConfig.class);
                ConfigValidator.validate(config);
                return config;
            }

        } catch (Exception e) {
            System.err.println("Warning: Failed to load config from " + configPath + ", using defaults");
            return createDefaultConfig();
        }
    }

    private CliConfig createDefaultConfig() {

        try (InputStream is = getClass().getResourceAsStream("/default-config.yaml")) {
            if (is != null) {
                Yaml yaml = new Yaml();
                return yaml.loadAs(is, CliConfig.class);
            }
        } catch (Exception e) {
            // fallback
        }
        return new CliConfig();
    }
}
