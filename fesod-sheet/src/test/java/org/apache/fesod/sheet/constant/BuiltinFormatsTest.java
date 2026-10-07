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

/*
 * This file is part of the Apache Fesod (Incubating) project, which was derived from Alibaba EasyExcel.
 *
 * Copyright (C) 2018-2024 Alibaba Group Holding Ltd.
 */

package org.apache.fesod.sheet.constant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Stream;
import org.apache.fesod.sheet.FesodSheet;
import org.apache.fesod.sheet.testkit.Tags;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Tests for {@link BuiltinFormats#getBuiltinFormat(Short, String, Locale)}.
 *
 * <p>
 * The all-language table carries "￥" currency entries at indices 5-8, 42 and 44, so it must not shadow the
 * US table (which uses "$" there) nor the numFmt override provided by the file's styles.xml when the locale
 * resolves to the US table.
 */
@Tag(Tags.UNIT)
@Tag(Tags.FORMAT)
class BuiltinFormatsTest {

    @TempDir
    File tempDir;

    @ParameterizedTest
    @MethodSource("builtinFormatProvider")
    void getBuiltinFormat(int index, String defaultFormat, Locale locale, String expected) {
        assertEquals(expected, BuiltinFormats.getBuiltinFormat((short) index, defaultFormat, locale));
    }

    static Stream<Arguments> builtinFormatProvider() {
        return Stream.of(
                // The US locale must resolve the "$" currency entries of the US table.
                Arguments.of(5, null, Locale.US, "\"$\"#,##0_);(\"$\"#,##0)"),
                Arguments.of(7, null, Locale.US, "\"$\"#,##0.00_);(\"$\"#,##0.00)"),
                Arguments.of(44, null, Locale.US, "_(\"$\"* #,##0.00_);_(\"$\"* (#,##0.00);_(\"$\"* \"-\"??_);_(@_)"),
                // CN and default (null) locales keep resolving the all-language "￥" entries.
                Arguments.of(5, null, Locale.CHINA, "\"￥\"#,##0_);(\"￥\"#,##0)"),
                Arguments.of(5, null, null, "\"￥\"#,##0_);(\"￥\"#,##0)"),
                // The externally provided format (the file's styles.xml numFmt override) wins for the US locale.
                Arguments.of(14, "mm-dd-yy", Locale.US, "mm-dd-yy"),
                // CN and default locales keep preferring the all-language entry over the external format.
                Arguments.of(14, "mm-dd-yy", Locale.CHINA, "yyyy/m/d"),
                Arguments.of(14, "mm-dd-yy", null, "yyyy/m/d"),
                // Reserved placeholders are never returned as the effective format.
                Arguments.of(5, "reserved-5", Locale.US, "\"$\"#,##0_);(\"$\"#,##0)"));
    }

    @Test
    void readBuiltinCurrencyWithUsLocaleUsesDollar() throws IOException {
        File file = createBuiltinCurrencyFile();
        List<Map<Integer, String>> dataMap =
                FesodSheet.read(file).locale(Locale.US).headRowNumber(0).doReadAllSync();
        String value = dataMap.get(0).get(0);
        assertTrue(value.contains("$"), "expected dollar currency but was: " + value);
        assertFalse(value.contains("￥"), "unexpected yuan currency but was: " + value);
    }

    @Test
    void readBuiltinCurrencyWithCnLocaleUsesYuan() throws IOException {
        File file = createBuiltinCurrencyFile();
        List<Map<Integer, String>> dataMap =
                FesodSheet.read(file).locale(Locale.CHINA).headRowNumber(0).doReadAllSync();
        String value = dataMap.get(0).get(0);
        assertTrue(value.contains("￥"), "expected yuan currency but was: " + value);
        assertFalse(value.contains("$"), "unexpected dollar currency but was: " + value);
    }

    /**
     * Creates an xlsx file with a single cell holding {@code 1234.5} formatted with built-in format 7
     * (currency, two decimal places) and no styles.xml numFmt override.
     */
    private File createBuiltinCurrencyFile() throws IOException {
        File file = File.createTempFile("builtinCurrency", ".xlsx", tempDir);
        try (XSSFWorkbook workbook = new XSSFWorkbook();
                OutputStream out = new FileOutputStream(file)) {
            Sheet sheet = workbook.createSheet("Sheet1");
            CellStyle style = workbook.createCellStyle();
            style.setDataFormat((short) 7);
            Cell cell = sheet.createRow(0).createCell(0);
            cell.setCellValue(1234.5);
            cell.setCellStyle(style);
            workbook.write(out);
        }
        return file;
    }
}
