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

package org.apache.fesod.sheet.benchmark.core;

/**
 * Configuration class for benchmark parameters
 */
public class BenchmarkConfiguration {

    /**
     * Dataset sizes for benchmarks
     */
    public enum DatasetSize {
        SMALL(1000, "1K"),
        MEDIUM(10000, "10K"),
        LARGE(100000, "100K"),
        EXTRA_LARGE(1000000, "1M");

        private final int rowCount;
        private final String label;

        DatasetSize(int rowCount, String label) {
            this.rowCount = rowCount;
            this.label = label;
        }

        public int getRowCount() {
            return rowCount;
        }

        public String getLabel() {
            return label;
        }
    }

    /**
     * File formats supported for benchmarking
     */
    public enum FileFormat {
        XLSX("xlsx"),
        XLS("xls"),
        CSV("csv");

        private final String extension;

        FileFormat(String extension) {
            this.extension = extension;
        }

        public String getExtension() {
            return extension;
        }
    }

    /**
     * Benchmark operation types
     */
    public enum OperationType {
        READ,
        WRITE,
        FILL,
        CONVERT
    }

    // Default benchmark configuration
    public static final int DEFAULT_WARMUP_ITERATIONS = 3;
    public static final int DEFAULT_MEASUREMENT_ITERATIONS = 5;
    public static final int DEFAULT_FORK_COUNT = 1;
    public static final String DEFAULT_OUTPUT_DIR = "target/benchmark-results";
    public static final String DEFAULT_BASELINE_DIR = "src/test/resources/baselines";

    // Memory monitoring configuration
    public static final boolean ENABLE_MEMORY_PROFILING = true;
    public static final long MEMORY_SAMPLING_INTERVAL_MS = 100;
}
