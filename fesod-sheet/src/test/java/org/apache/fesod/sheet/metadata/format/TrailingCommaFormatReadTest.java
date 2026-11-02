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

import java.util.List;
import java.util.Map;
import org.apache.fesod.sheet.FesodSheet;
import org.apache.fesod.sheet.testkit.Tags;
import org.apache.fesod.sheet.testkit.base.AbstractExcelTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/**
 * Reads cells with the custom format {@code 0\.0,} (issue #2319): the escaped dot is literal text and the
 * trailing comma divides by 1000, so Excel shows 9234 as {@code 0.9} and 123456 as {@code 12.3}.
 */
@Tag(Tags.READ)
class TrailingCommaFormatReadTest extends AbstractExcelTest {

    @ParameterizedTest
    @CsvSource({"test1.xlsx, 9234, 0.9", "test2.xlsx, 123456, 12.3"})
    void readsFormattedValue(String fileName, String rawValue, String formattedValue) {
        List<Map<Integer, String>> rows =
                FesodSheet.read(readFile("temp/issue2319/" + fileName)).sheet().doReadSync();
        Assertions.assertEquals(1, rows.size());
        Assertions.assertEquals(rawValue, rows.get(0).get(0));
        Assertions.assertEquals(formattedValue, rows.get(0).get(1));
    }
}
