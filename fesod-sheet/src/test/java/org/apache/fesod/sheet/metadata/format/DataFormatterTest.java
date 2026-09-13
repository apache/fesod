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

package org.apache.fesod.sheet.metadata.format;

import java.math.BigDecimal;
import java.util.Locale;
import org.apache.fesod.sheet.testkit.Tags;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

@Tag(Tags.UNIT)
class DataFormatterTest {

    private static final BigDecimal LARGE_NUMBER = new BigDecimal("100000000000");

    @ParameterizedTest(name = "windowing={0}, scientific={1} -> {2}")
    @CsvSource(
            nullValues = "null",
            value = {"false, null, 100000000000", "null, true, 1E+11", "null, false, 100000000000"})
    void test_format_honorsScientificFormatWithNullableOptions(
            Boolean use1904windowing, Boolean useScientificFormat, String expected) {
        DataFormatter formatter = new DataFormatter(use1904windowing, Locale.US, useScientificFormat);

        String result = formatter.format(LARGE_NUMBER, null, "General");

        Assertions.assertEquals(expected, result);
    }
}
