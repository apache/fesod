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

package org.apache.fesod.sheet.format;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.apache.fesod.sheet.FesodSheet;
import org.apache.fesod.sheet.testkit.Tags;
import org.apache.fesod.sheet.testkit.base.AbstractExcelTest;
import org.apache.fesod.sheet.testkit.enums.ExcelFormat;
import org.apache.fesod.sheet.testkit.params.ExcelFormatSource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;

/**
 * Reproduction test for issue #1105:
 * Blank cells and all-blank rows read differently in XLS and CSV than in XLSX.
 */
@Tag(Tags.ROUND_TRIP)
@Tag(Tags.FORMAT)
public class BlankCellAndRowTest extends AbstractExcelTest {

    private List<List<Object>> testData() {
        return Arrays.asList(
                Arrays.asList("x", "y"),
                Arrays.asList(" ", "\t"), // whitespace only
                Arrays.asList("", ""), // empty strings
                Arrays.asList(" ", "z"), // one blank cell next to data
                Arrays.asList("p", "q"));
    }

    @ParameterizedTest(name = "{0}")
    @ExcelFormatSource
    void testBlankCellsAndRowsDefaultSettings(ExcelFormat format) throws IOException {
        File file = createTempFile("blank_default", format);
        FesodSheet.write(file).sheet().doWrite(testData());

        List<Map<Integer, String>> rows =
                FesodSheet.read(file).headRowNumber(0).sheet().doReadSync();

        Assertions.assertEquals(3, rows.size(), "Should skip all-blank rows by default");
        Assertions.assertEquals("x", rows.get(0).get(0));
        Assertions.assertEquals("y", rows.get(0).get(1));

        Assertions.assertNull(rows.get(1).get(0), "Empty cell should be null, not empty string");
        Assertions.assertEquals("z", rows.get(1).get(1));

        Assertions.assertEquals("p", rows.get(2).get(0));
        Assertions.assertEquals("q", rows.get(2).get(1));
    }

    @ParameterizedTest(name = "{0}")
    @ExcelFormatSource
    void testBlankCellsAndRowsIgnoreEmptyRowFalse(ExcelFormat format) throws IOException {
        File file = createTempFile("blank_no_ignore", format);
        FesodSheet.write(file).sheet().doWrite(testData());

        List<Map<Integer, String>> rows = FesodSheet.read(file)
                .ignoreEmptyRow(false)
                .headRowNumber(0)
                .sheet()
                .doReadSync();

        Assertions.assertEquals(5, rows.size(), "Should return all 5 rows when ignoreEmptyRow is false");
        Assertions.assertEquals("x", rows.get(0).get(0));
        Assertions.assertEquals("y", rows.get(0).get(1));

        Assertions.assertNull(rows.get(1).get(0), "Empty cell should be null in all-blank row");
        Assertions.assertNull(rows.get(1).get(1), "Empty cell should be null in all-blank row");

        Assertions.assertNull(rows.get(2).get(0), "Empty cell should be null in all-blank row");
        Assertions.assertNull(rows.get(2).get(1), "Empty cell should be null in all-blank row");

        Assertions.assertNull(rows.get(3).get(0), "Blank cell next to data should be null");
        Assertions.assertEquals("z", rows.get(3).get(1));

        Assertions.assertEquals("p", rows.get(4).get(0));
        Assertions.assertEquals("q", rows.get(4).get(1));
    }

    @ParameterizedTest(name = "{0}")
    @ExcelFormatSource
    void testBlankCellsAndRowsAutoTrimFalse(ExcelFormat format) throws IOException {
        File file = createTempFile("blank_no_autotrim", format);
        FesodSheet.write(file).sheet().doWrite(testData());

        List<Map<Integer, String>> rows =
                FesodSheet.read(file).autoTrim(false).headRowNumber(0).sheet().doReadSync();

        Assertions.assertEquals(4, rows.size(), "Row 2 (empty strings) should be skipped, but row 1 (whitespace) kept");
        Assertions.assertEquals("x", rows.get(0).get(0));
        Assertions.assertEquals("y", rows.get(0).get(1));

        Assertions.assertEquals(" ", rows.get(1).get(0), "Whitespace should be preserved when autoTrim is false");
        Assertions.assertEquals("\t", rows.get(1).get(1), "Tab whitespace should be preserved when autoTrim is false");

        Assertions.assertEquals(
                " ", rows.get(2).get(0), "Whitespace before data should be preserved when autoTrim is false");
        Assertions.assertEquals("z", rows.get(2).get(1));

        Assertions.assertEquals("p", rows.get(3).get(0));
        Assertions.assertEquals("q", rows.get(3).get(1));
    }

    @ParameterizedTest(name = "{0}")
    @ExcelFormatSource
    void testBlankCellsAndRowsIgnoreEmptyRowFalseAndAutoTrimFalse(ExcelFormat format) throws IOException {
        File file = createTempFile("blank_no_ignore_no_autotrim", format);
        FesodSheet.write(file).sheet().doWrite(testData());

        List<Map<Integer, String>> rows = FesodSheet.read(file)
                .ignoreEmptyRow(false)
                .autoTrim(false)
                .headRowNumber(0)
                .sheet()
                .doReadSync();

        Assertions.assertEquals(5, rows.size(), "Should return all 5 rows when ignoreEmptyRow is false");
        Assertions.assertEquals("x", rows.get(0).get(0));
        Assertions.assertEquals("y", rows.get(0).get(1));

        Assertions.assertEquals(" ", rows.get(1).get(0), "Whitespace should be preserved when autoTrim is false");
        Assertions.assertEquals("\t", rows.get(1).get(1), "Tab whitespace should be preserved when autoTrim is false");

        Assertions.assertNull(rows.get(2).get(0), "Empty string cell should still be null");
        Assertions.assertNull(rows.get(2).get(1), "Empty string cell should still be null");

        Assertions.assertEquals(" ", rows.get(3).get(0), "Whitespace before data should be preserved");
        Assertions.assertEquals("z", rows.get(3).get(1));

        Assertions.assertEquals("p", rows.get(4).get(0));
        Assertions.assertEquals("q", rows.get(4).get(1));
    }
}
