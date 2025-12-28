/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the License.   You may obtain a copy of the License at
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
package org.apache.fesod.cli.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Logging utility class
 */
public class LogUtils {

    private static final Logger logger = LoggerFactory.getLogger(LogUtils.class);

    /**
     * Log command execution start
     */
    public static void logCommandStart(String commandName, String... args) {
        if (logger.isInfoEnabled()) {
            StringBuilder sb = new StringBuilder();
            sb.append("Executing command: ").append(commandName);
            if (args.length > 0) {
                sb.append(" with args: ");
                for (int i = 0; i < args.length; i++) {
                    if (i > 0) sb.append(", ");
                    sb.append("'").append(args[i]).append("'");
                }
            }
            logger.info(sb.toString());
        }
    }

    /**
     * Log command execution end
     */
    public static void logCommandEnd(String commandName, long startTime) {
        long duration = System.currentTimeMillis() - startTime;
        logger.info("Command '{}' completed in {} ms", commandName, duration);
    }

    /**
     * Log command execution error
     */
    public static void logCommandError(String commandName, Exception e) {
        logger.error("Command '{}' failed: {}", commandName, e.getMessage(), e);
    }

    /**
     * Log file operation
     */
    public static void logFileOperation(String operation, String filePath) {
        logger.debug("{} file: {}", operation, filePath);
    }

    /**
     * Log file operation with size
     */
    public static void logFileOperation(String operation, String filePath, long fileSize) {
        logger.debug("{} file: {} (size: {})", operation, filePath, FileUtils.getHumanReadableFileSize(fileSize));
    }

    /**
     * Log configuration loading
     */
    public static void logConfigLoading(String configPath) {
        if (configPath != null) {
            logger.debug("Loading configuration from: {}", configPath);
        } else {
            logger.debug("Using default configuration");
        }
    }

    /**
     * Log module registration
     */
    public static void logModuleRegistration(String moduleName, String className) {
        logger.debug("Registered module '{}' with class '{}'", moduleName, className);
    }

    /**
     * Log processor initialization
     */
    public static void logProcessorInit(String processorType, String moduleName) {
        logger.debug("Initialized {} processor for module '{}'", processorType, moduleName);
    }

    /**
     * Get a logger for a specific class
     */
    public static Logger getLogger(Class<?> clazz) {
        return LoggerFactory.getLogger(clazz);
    }

    /**
     * Get a logger for a specific name
     */
    public static Logger getLogger(String name) {
        return LoggerFactory.getLogger(name);
    }
}

