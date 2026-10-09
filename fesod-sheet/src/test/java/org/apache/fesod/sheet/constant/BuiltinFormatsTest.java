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

package org.apache.fesod.sheet.constant;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
import org.apache.poi.hssf.record.FormatRecord;
import org.apache.poi.hssf.record.Record;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.api.parallel.ResourceLock;
import org.junit.jupiter.api.parallel.Resources;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Tests for {@link BuiltinFormats#getBuiltinFormat(Short, String, Locale)}.
 */
@Tag(Tags.FORMAT)
class BuiltinFormatsTest {

    private static final String US_CURRENCY_5 = "\"$\"#,##0_);(\"$\"#,##0)";
    private static final String US_CURRENCY_44 = "_(\"$\"* #,##0.00_);_(\"$\"* (#,##0.00);_(\"$\"* \"-\"??_);_(@_)";
    private static final String CN_CURRENCY_5 = "\"￥\"#,##0_);(\"￥\"#,##0)";

    @TempDir
    File tempDir;

    @ParameterizedTest
    @MethodSource("builtinFormatProvider")
    void getBuiltinFormat(int index, String defaultFormat, Locale locale, String expected) {
        assertEquals(expected, BuiltinFormats.getBuiltinFormat((short) index, defaultFormat, locale));
    }

    static Stream<Arguments> builtinFormatProvider() {
        return Stream.of(
                // US currency entries come from the US table, or from the file when it defines the format
                Arguments.of(5, null, Locale.US, US_CURRENCY_5),
                Arguments.of(5, "reserved-5", Locale.US, US_CURRENCY_5),
                Arguments.of(44, null, Locale.US, US_CURRENCY_44),
                Arguments.of(44, "\"€\"#,##0.00", Locale.US, "\"€\"#,##0.00"),
                // CN and other non-US locales keep the all-language entries; a null locale falls back to the CN table
                Arguments.of(5, null, Locale.CHINA, CN_CURRENCY_5),
                Arguments.of(5, US_CURRENCY_5, Locale.CHINA, CN_CURRENCY_5),
                Arguments.of(5, US_CURRENCY_5, Locale.GERMANY, CN_CURRENCY_5),
                Arguments.of(5, null, null, CN_CURRENCY_5),
                // non-currency entries are the same for every locale and still win over the POI defaults
                Arguments.of(14, "m/d/yy", Locale.US, "yyyy/m/d"),
                Arguments.of(22, "m/d/yy h:mm", Locale.US, "yyyy-m-d h:mm"),
                Arguments.of(14, "m/d/yy", Locale.CHINA, "yyyy/m/d"));
    }

    @Test
    @ResourceLock(Resources.GLOBAL)
    void getBuiltinFormatUsesEditedAllLanguageEntries() {
        String[] all = BuiltinFormats.BUILTIN_FORMATS_ALL_LANGUAGES;
        String original7 = all[7];
        String original14 = all[14];
        String euro7 = "\"€\"#,##0.00_);(\"€\"#,##0.00)";
        String usCurrency7 = "\"$\"#,##0.00_);(\"$\"#,##0.00)";
        try {
            // the class javadoc suggests editing the tables for languages other than Chinese
            all[7] = euro7;
            all[14] = "dd.mm.yyyy";
            assertEquals(euro7, BuiltinFormats.getBuiltinFormat((short) 7, usCurrency7, Locale.GERMANY));
            assertEquals(euro7, BuiltinFormats.getBuiltinFormat((short) 7, usCurrency7, null));
            assertEquals("dd.mm.yyyy", BuiltinFormats.getBuiltinFormat((short) 14, "m/d/yy", Locale.US));
            // the US locale still skips the all-language table for its currency entries
            assertEquals(usCurrency7, BuiltinFormats.getBuiltinFormat((short) 7, usCurrency7, Locale.US));
        } finally {
            all[7] = original7;
            all[14] = original14;
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"xlsx", "xls"})
    void readBuiltinCurrencyFollowsLocale(String extension) throws IOException {
        File file = writeCurrencyCell(extension, null);
        assertEquals("$1,234.50", readFirstCell(file, Locale.US));
        assertEquals("￥1,234.50", readFirstCell(file, Locale.CHINA));
    }

    @ParameterizedTest
    @ValueSource(strings = {"xlsx", "xls"})
    void readCurrencyFormatDefinedInFileWithUsLocale(String extension) throws IOException {
        File euro = writeCurrencyCell(extension, "\"€\"#,##0.00");
        assertEquals("€1,234.50", readFirstCell(euro, Locale.US));

        // a file saved by a Chinese Excel keeps its yuan formats when read with the US locale
        File yuan = writeCurrencyCell(extension, "\"￥\"#,##0.00");
        assertEquals("￥1,234.50", readFirstCell(yuan, Locale.US));
    }

    private static String readFirstCell(File file, Locale locale) {
        List<Map<Integer, String>> rows =
                FesodSheet.read(file).locale(locale).headRowNumber(0).doReadAllSync();
        return rows.get(0).get(0);
    }

    /**
     * Writes {@code 1234.5} with the built-in currency format 7, or with format 44 redefined as
     * {@code numFmt44} in the file when it is not null.
     */
    private File writeCurrencyCell(String extension, String numFmt44) throws IOException {
        File file = File.createTempFile("currency", "." + extension, tempDir);
        try (Workbook workbook = "xls".equals(extension) ? new HSSFWorkbook() : new XSSFWorkbook();
                OutputStream out = new FileOutputStream(file)) {
            CellStyle style = workbook.createCellStyle();
            if (numFmt44 == null) {
                style.setDataFormat((short) 7);
            } else {
                redefineFormat44(workbook, numFmt44);
                style.setDataFormat((short) 44);
            }
            Cell cell = workbook.createSheet("Sheet1").createRow(0).createCell(0);
            cell.setCellValue(1234.5);
            cell.setCellStyle(style);
            workbook.write(out);
        }
        return file;
    }

    private static void redefineFormat44(Workbook workbook, String format) {
        if (workbook instanceof XSSFWorkbook) {
            ((XSSFWorkbook) workbook).getStylesSource().putNumberFormat((short) 44, format);
            return;
        }
        // a new HSSF workbook already carries a FORMAT record for 44, replace it
        List<Record> records = ((HSSFWorkbook) workbook).getInternalWorkbook().getRecords();
        for (int i = 0; i < records.size(); i++) {
            Record record = records.get(i);
            if (record instanceof FormatRecord && ((FormatRecord) record).getIndexCode() == 44) {
                records.set(i, new FormatRecord(44, format));
                return;
            }
        }
        throw new IllegalStateException("no FORMAT record for 44");
    }
}
