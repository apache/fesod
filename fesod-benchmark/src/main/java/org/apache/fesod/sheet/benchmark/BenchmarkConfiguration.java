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

package org.apache.fesod.sheet.benchmark;

/**
 * Shared constants for the benchmark suites.
 */
public final class BenchmarkConfiguration {

    private BenchmarkConfiguration() {}

    /**
     * Dataset sizes used by the benchmark suites. Rows have 20 columns each
     * (see {@link org.apache.fesod.sheet.benchmark.data.BenchmarkData}).
     */
    public enum DatasetSize {
        SMALL(1_000, "1K"),
        MEDIUM(10_000, "10K"),
        LARGE(100_000, "100K");

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
     * File formats covered by the benchmark suites. Note that XLS is limited
     * to 65,536 rows per sheet.
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
}
