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

package org.apache.fesod.sheet.testkit;

/**
 * Standard JUnit 5 {@code @Tag} constants for grouping and filtering the test suite.
 *
 * <h2>Usage with Maven Surefire</h2>
 * <pre>
 * mvn test -Dtest.groups=unit              # fast feedback: util tests only
 * mvn test -Dtest.excludedGroups=fuzz      # exclude slow fuzz tests
 * mvn test                                 # run all (default)
 * </pre>
 *
 * <h2>Tag Definitions</h2>
 * <ul>
 *   <li>{@link #UNIT} — pure logic tests with no Excel I/O ({@code util/*} tests)</li>
 *   <li>{@link #ROUND_TRIP} — write-then-read matrix tests extending {@code AbstractExcelTest}</li>
 *   <li>{@link #READ} — read-only tests (analysers, SAX parsers, head detection)</li>
 *   <li>{@link #WRITE} — write-only tests (sheet creation, handlers, freeze panes)</li>
 *   <li>{@link #FORMAT} — format-specific tests (CSV, BOM, charset, date formats)</li>
 *   <li>{@link #FUZZ} — fuzzing tests (slow, property-based random input)</li>
 * </ul>
 */
public final class Tags {

    /** Pure logic tests with no Excel I/O ({@code util/*} tests). */
    public static final String UNIT = "unit";

    /** Write-then-read matrix tests extending {@code AbstractExcelTest}. */
    public static final String ROUND_TRIP = "round-trip";

    /** Read-only tests (analysers, SAX parsers, head detection). */
    public static final String READ = "read";

    /** Write-only tests (sheet creation, handlers, freeze panes). */
    public static final String WRITE = "write";

    /** Format-specific tests (CSV, BOM, charset, date formats). */
    public static final String FORMAT = "format";

    /** Fuzzing tests (slow, property-based random input). */
    public static final String FUZZ = "fuzz";

    private Tags() {
        // utility class
    }
}
