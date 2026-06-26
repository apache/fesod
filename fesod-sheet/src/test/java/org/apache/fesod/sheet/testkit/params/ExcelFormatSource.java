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

package org.apache.fesod.sheet.testkit.params;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.apache.fesod.sheet.testkit.enums.ApiMode;
import org.apache.fesod.sheet.testkit.enums.ExcelFormat;
import org.junit.jupiter.params.provider.ArgumentsSource;

/**
 * Supplies {@link ExcelFormat} arguments (and optionally an {@link ApiMode}) to a
 * {@code @ParameterizedTest}, replacing the inherited static {@code @MethodSource}
 * providers previously defined on {@code AbstractExcelTest}.
 *
 * <h2>Usage</h2>
 * <pre>{@code
 * @ParameterizedTest
 * @ExcelFormatSource                       // all formats, single ExcelFormat param
 * void readAndWrite(ExcelFormat format) { ... }
 *
 * @ParameterizedTest
 * @ExcelFormatSource(BINARY)               // XLSX + XLS only
 * void writeImage(ExcelFormat format) { ... }
 *
 * @ParameterizedTest
 * @ExcelFormatSource(withApiMode = true)   // cross product: ExcelFormat x ApiMode
 * void readAndWriteWithApiMode(ExcelFormat format, ApiMode mode) { ... }
 *
 * @ParameterizedTest
 * @ExcelFormatSource(requires = TEMPLATES) // skip formats without template support
 * void writeTemplate(ExcelFormat format) { ... }
 * }</pre>
 *
 * <p>The {@link #requires()} attribute filters out formats that cannot satisfy one or more
 * capabilities, replacing inline {@code Assumptions.assumeTrue(format.supportsXxx())} guards.
 */
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ArgumentsSource(ExcelFormatArgumentsProvider.class)
public @interface ExcelFormatSource {

    /**
     * Which format values to supply.
     *
     * @return the scope; defaults to {@link FormatScope#ALL}
     */
    FormatScope value() default FormatScope.ALL;

    /**
     * Whether to additionally cross-product every format with each {@link ApiMode}.
     *
     * <p>When {@code true} the provider yields {@code Arguments.of(format, mode)} pairs
     * (a test method signed {@code (ExcelFormat, ApiMode)}). When {@code false} (default)
     * it yields single {@code ExcelFormat} arguments.
     *
     * @return {@code true} to include the {@link ApiMode} dimension
     */
    boolean withApiMode() default false;

    /**
     * Capabilities that every supplied {@link ExcelFormat} must support; formats lacking
     * any of them are filtered out (skipped) rather than passed to the test. This replaces
     * inline {@code Assumptions.assumeTrue(format.supportsXxx())} guards.
     *
     * @return the required capabilities; defaults to an empty array (no filtering)
     */
    FormatCapability[] requires() default {};
}
