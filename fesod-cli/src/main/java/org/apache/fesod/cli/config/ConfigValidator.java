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

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import org.apache.fesod.cli.exception.ConfigurationException;

/**
 * Configuration validator
 */
public class ConfigValidator {

    private static final List<String> SUPPORTED_OUTPUT_FORMATS = Arrays.asList("json", "csv", "xml");
    private static final List<String> SUPPORTED_ENCODINGS =
            Arrays.asList("UTF-8", "UTF-16", "ISO-8859-1", "GBK", "GB2312");

    public static void validate(CliConfig config) {
        if (config == null) {
            throw new ConfigurationException("Configuration cannot be null");
        }

        validateDefaults(config.getDefaults());
        validateRead(config.getRead());
        validateWrite(config.getWrite());
    }

    private static void validateDefaults(CliConfig.DefaultsConfig defaults) {
        if (defaults == null) {
            return; // defaults are optional
        }

        if (defaults.getOutputFormat() != null) {
            if (!SUPPORTED_OUTPUT_FORMATS.contains(defaults.getOutputFormat().toLowerCase())) {
                throw new ConfigurationException("Unsupported output format: " + defaults.getOutputFormat()
                        + ". Supported formats: " + String.join(", ", SUPPORTED_OUTPUT_FORMATS));
            }
        }

        if (defaults.getEncoding() != null) {
            try {
                Charset.forName(defaults.getEncoding());
            } catch (Exception e) {
                throw new ConfigurationException("Unsupported encoding: " + defaults.getEncoding()
                        + ". Supported encodings: " + String.join(", ", SUPPORTED_ENCODINGS));
            }
        }
    }

    private static void validateRead(CliConfig.ReadConfig read) {
        if (read == null) {
            return; // read config is optional
        }

        // No specific validation needed for read config currently
        // Can add validation for boolean fields if needed
    }

    private static void validateWrite(CliConfig.WriteConfig write) {
        if (write == null) {
            return; // write config is optional
        }

        // No specific validation needed for write config currently
        // Can add validation for boolean fields if needed
    }

    /**
     * Validate configuration file path
     */
    public static void validateConfigFile(String configFile) {
        if (configFile == null || configFile.trim().isEmpty()) {
            return; // null or empty is acceptable (will use defaults)
        }

        if (!configFile.endsWith(".yaml") && !configFile.endsWith(".yml")) {
            throw new ConfigurationException("Configuration file must be a YAML file (.yaml or .yml)");
        }
    }
}
