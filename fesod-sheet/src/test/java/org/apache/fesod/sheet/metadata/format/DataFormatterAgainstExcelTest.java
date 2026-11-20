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

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;
import org.apache.fesod.sheet.testkit.Tags;
import org.apache.fesod.sheet.util.DateUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.TestFactory;

/**
 * Checks {@link DataFormatter} against text produced by Excel itself: every {@code TEXT(value, format)}
 * formula in these sample workbooks has its result cached as Excel calculated it, so the cached text
 * is what Excel displays for that value and format.
 * <p>
 * The workbooks come from Apache POI's test data, where the same check is run against POI's
 * {@code org.apache.poi.ss.usermodel.DataFormatter} (see {@code TestDataFormatterAgainstExcel}).
 * All of them were last saved by Microsoft Excel.
 * <p>
 * Cases where Fesod does not match Excel are listed in {@link #UNSUPPORTED_FORMATS} and
 * {@link #KNOWN_FAILURES}. Each listed case is asserted to <em>still differ</em> from Excel, so a
 * change that makes one match fails the test until the entry is removed. This gives regression
 * coverage when changing the formatter, e.g. when switching to POI's {@code DataFormatter}.
 */
@Tag(Tags.UNIT)
class DataFormatterAgainstExcelTest {

    private static final String RESOURCE_DIR = "dataformat/excel/";

    private static final String[] WORKBOOKS = {
        "NumberFormatTests.xlsx",
        "GeneralFormatTests.xlsx",
        "FormatConditionTests.xlsx",
        "FormatChoiceTests.xlsx",
        "ElapsedFormatTests.xlsx",
        "DateFormatTests.xlsx",
        "DateFormatNumberTests.xlsx",
        "DateTimeToNumberTestCases.xls",
        "59106.xlsx"
    };

    /**
     * Formats where DataFormatter does not match Excel for any of the sample values.
     */
    private static final Set<String> UNSUPPORTED_FORMATS = new HashSet<>(Arrays.asList(
            // a comma between digit placeholders is taken as a custom grouping size,
            // instead of grouping by thousands; a leading comma is literal text
            "#,##",
            "#,#",
            ",#",
            "0,00",
            "0,0",
            ",0",
            "0,00.0",
            "0,0000",
            "0,00000",
            "0,000000",
            "0,0000000",
            "0,00000000",
            "0,000000000",
            "0,0.00%",
            // a decimal point with no digits after it is still shown, and the grouping is wrong
            "#,##.#",
            // '?' digit placeholders outside fractions
            "?,???",
            "?,????",
            "?,?????",
            "?,??????",
            "?,???????",
            "?,????????",
            "?,?????????",
            "|?.?|",
            "|??.?|",
            "|?.??|",
            "|??.??|",
            // a scaling comma before the decimal point is ignored
            "#,.#,",
            // two percent signs multiply by 100 twice
            "0,0.00%%",
            // fractions with a '_' space, or with text within the whole number or numerator digits
            "|#\\:#/#|",
            "|#-#-#\\:#/#|",
            "|#\\:? ?#/#|",
            "|#\\:? ?0#/000",
            "|#_#/#|",
            "|#_#=/=#|",
            "|#_#=/=?|",
            "|#_#=/=0|",
            "|#_?=/=#|",
            "|#_?=/=?|",
            // the text is dropped when the condition of a single-section format applies
            "[>=10]#\" Big\"",
            "[>-10]#\" Big\"",
            "[>=-10]#\" Big\"",
            "[<>-10]#\" Wow\"",
            // fractions of a second after an elapsed seconds unit
            "[s].000",
            "[ss].000",
            // elapsed time units with text around them, or not at the start
            "s:m\" @ hour \"[hh]",
            "s\" secs and \"[m]\" mins\"",
            "\"It was \"[h]\" [yes, \"h\"] hours and \"mm:ss",
            "[s]\" [yes, \"ss\"] seconds\"",
            // a single 'y' is a 2-digit year
            "d-m-y",
            "D-M-Y",
            "d-m-y h:m:s",
            // 'A/P' shows a single letter, in the case written
            "hh:mm:ss a/p",
            "h:m:s.000 A/P",
            "HH:MM:SS A/P",
            "H:M:S.00 a/p",
            "h:m:s.00 A/P",
            // escaped letters and quoted text in date formats
            "d \\d\\a\\y\\s h",
            "d \"days\" h",
            "d \\d\\a\\y\\s h a/p",
            "d \"days\" h am/pm"));

    /**
     * Format and value combinations where DataFormatter does not match Excel,
     * for formats that match Excel for other values.
     */
    private static final Set<String> KNOWN_FAILURES = new HashSet<>(Arrays.asList(
            // '?' digit placeholders outside fractions
            failure("?,?", 1234567.0),
            failure("?,?", -1234567.0),
            // General shows large and very small numbers in scientific notation,
            // unless useScientificFormat is set
            failure("General", 1.0E11),
            failure("General", 1.1E11),
            failure("General", 1.12E11),
            failure("General", 1.123E11),
            failure("General", 1.12345E11),
            failure("General", 1.123451E11),
            failure("General", 1.123459E11),
            failure("General", 1.0E-10),
            failure("General", 1.1E-10),
            failure("General", 1.12E-10),
            failure("General", 1.123E-10),
            failure("General", 1.12345E-10),
            failure("General", 1.123451E-10),
            failure("General", 1.123459E-10),
            failure("General", -1.0E11),
            failure("General", -1.1E11),
            failure("General", -1.12E11),
            failure("General", -1.123E11),
            failure("General", -1.12345E11),
            failure("General", -1.123451E11),
            failure("General", -1.123459E11),
            failure("General", -1.0E-10),
            failure("General", -1.1E-10),
            failure("General", -1.12E-10),
            failure("General", -1.123E-10),
            failure("General", -1.12345E-10),
            failure("General", -1.123451E-10),
            failure("General", -1.123459E-10),
            // the text is dropped when the condition of a single-section format applies
            failure("[<10]#\" Wow\"", 1.0),
            failure("[<10]#\" Wow\"", 1.5),
            failure("[>10]#\" Big\"", 11.0),
            failure("[>10]#\" Big\"", 12.0),
            failure("[<=10]#\" Wow\"", 1.0),
            failure("[<=10]#\" Wow\"", 10.0),
            failure("[=10]#\" Wow\"", 10.0),
            failure("[<>10]#\" Wow\"", 1.0),
            failure("[<>10]#\" Wow\"", 11.0),
            failure("[< 10]#\" Wow\"", 1.0),
            failure("[<      10]#\" Wow\"", 1.0),
            // fractions of a second are dropped, or the seconds are rounded up while the
            // fraction is still shown
            failure("[h]:m:s.000", 3.14159),
            failure("[m]:s.000", 3.14159),
            failure("hh:mm:ss.000 am/pm", 36191.1702084375),
            failure("HH:MM:SS.000 AM/PM", 36191.1702084375),
            failure("yyyy-mm-dd hh:mm:ss.000", 1.0032134017094),
            failure("yyyy-mm-dd hh:mm:ss.000", 1.00000478703703),
            failure("yyyy-mm-dd hh:mm:ss.00", 1.0032134017094),
            failure("yyyy-mm-dd hh:mm:ss.0", 1.0032134017094),
            // negative elapsed times are not shown as times
            failure("[h]:m:s.000", -0.08546296296296296),
            failure("[h]:m:s.000", -3.14159),
            failure("[m]:s.000", -0.08546296296296296),
            failure("[m]:s.000", -3.14159),
            failure("[hh]:mm:ss.0", -0.08546296296296296),
            failure("[hh]:mm:ss.0", -3.14159),
            failure("[mm]:ss.0", -0.08546296296296296),
            failure("[mm]:ss.0", -3.14159)));

    /**
     * Formats used in Chinese workbooks, 45000.6 is 2023-03-15 14:24. These expectations are the
     * ones in POI's {@code TestDataFormatter} (apache/poi#1400). For {@code [DBNum1]}, Excel shows
     * Chinese numerals; POI and these expectations accept Arabic digits instead.
     */
    private static final String[][] CHINESE_CASES = {
        // format, value, expected
        {"yyyy\"年\"m\"月\"d\"日\"", "45000.6", "2023年3月15日"},
        {"m\"月\"d\"日\"", "45000.6", "3月15日"},
        {"h\"时\"mm\"分\"", "45000.6", "14时24分"},
        {"h\"时\"mm\"分\"ss\"秒\"", "45000.6", "14时24分00秒"},
        {"h时mm分ss秒", "45000.6", "14时24分00秒"},
        {"yyyy\"年\"m\"月\"d\"日\" h\"时\"mm\"分\"", "45000.6", "2023年3月15日 14时24分"},
        {"h\"時\"mm\"分\"", "45000.6", "14時24分"},
        {"上午/下午h\"时\"mm\"分\"", "45000.6", "下午2时24分"},
        {"上午/下午h\"时\"mm\"分\"ss\"秒\"", "45000.6", "下午2时24分00秒"},
        {"上午/下午h:mm", "45000.37847222222", "上午9:05"},
        {"yyyy/m/d 上午/下午h:mm", "45000.6", "2023/3/15 下午2:24"},
        {"[DBNum1]yyyy\"年\"m\"月\"d\"日\"", "45000.6", "2023年3月15日"},
        {"[DBNum1][$-804]yyyy\"年\"m\"月\"d\"日\"", "45000.6", "2023年3月15日"},
        {"[$-804][DBNum1]yyyy\"年\"m\"月\"d\"日\"", "45000.6", "2023年3月15日"},
        {"[DBNum2]0.00", "45000.6", "45000.60"},
        {"[DBNum3]0.00", "45000.6", "45000.60"},
        {"0.00\"m/秒\"", "1.5", "1.50m/秒"}
    };

    /**
     * Quoted text in date formats, checked with {@link Locale#US} only; also from POI's {@code TestDataFormatter}.
     */
    private static final String[][] QUOTED_LITERAL_CASES = {
        // format, value, expected
        {"d\"-\"mmm", "45000.6", "15-Mar"},
        {"hh\"h\"mm", "45000.6", "14h24"}
    };

    /**
     * Cases from {@link #CHINESE_CASES} and {@link #QUOTED_LITERAL_CASES}, as format and value,
     * where DataFormatter does not give the expected text.
     */
    private static final Set<String> POI_KNOWN_FAILURES = new HashSet<>(Arrays.asList(
            // an 'mm' after quoted or Chinese text is taken as the month, not the minutes
            failure("h\"时\"mm\"分\"", 45000.6),
            failure("h\"时\"mm\"分\"ss\"秒\"", 45000.6),
            failure("h时mm分ss秒", 45000.6),
            failure("yyyy\"年\"m\"月\"d\"日\" h\"时\"mm\"分\"", 45000.6),
            failure("h\"時\"mm\"分\"", 45000.6),
            // as above, and the Chinese AM/PM marker follows the locale (PM for en_US)
            failure("上午/下午h\"时\"mm\"分\"", 45000.6),
            failure("上午/下午h\"时\"mm\"分\"ss\"秒\"", 45000.6),
            // a format with the Chinese AM/PM marker before the time is not taken as a date format
            failure("上午/下午h:mm", 45000.37847222222),
            failure("yyyy/m/d 上午/下午h:mm", 45000.6),
            // a number format with 秒 in quoted text is taken as a date format
            failure("0.00\"m/秒\"", 1.5),
            // letters in quoted text are taken as pattern letters
            failure("hh\"h\"mm", 45000.6)));

    private static String failure(String format, double value) {
        return format + "|" + value;
    }

    @AfterEach
    void tearDown() {
        DateUtils.removeThreadLocalCache();
    }

    @TestFactory
    Stream<DynamicTest> formatsMatchExcel() throws Exception {
        List<ExcelText> cases = new ArrayList<>();
        for (String name : WORKBOOKS) {
            readTextFormulas(name, cases);
        }
        Assertions.assertFalse(cases.isEmpty());
        FormatIndexes indexes = new FormatIndexes();
        return cases.stream()
                .map(c -> DynamicTest.dynamicTest(c.source + " " + c.format + " " + c.value, () -> {
                    String actual = format(c.value, indexes.get(c.format), c.format, c.date1904, Locale.US);
                    if (UNSUPPORTED_FORMATS.contains(c.format) || KNOWN_FAILURES.contains(failure(c.format, c.value))) {
                        Assertions.assertNotEquals(
                                c.expected, actual, "now matches Excel, remove it from the known failures");
                    } else {
                        Assertions.assertEquals(c.expected, actual);
                    }
                }));
    }

    @TestFactory
    Stream<DynamicTest> chineseFormats() {
        FormatIndexes indexes = new FormatIndexes();
        List<DynamicTest> tests = new ArrayList<>();
        for (Locale locale : new Locale[] {Locale.CHINA, Locale.US}) {
            for (String[] c : CHINESE_CASES) {
                tests.add(poiCase(c, locale, indexes));
            }
        }
        for (String[] c : QUOTED_LITERAL_CASES) {
            tests.add(poiCase(c, Locale.US, indexes));
        }
        return tests.stream();
    }

    private static DynamicTest poiCase(String[] c, Locale locale, FormatIndexes indexes) {
        String format = c[0];
        double value = Double.parseDouble(c[1]);
        String expected = c[2];
        return DynamicTest.dynamicTest(locale + " " + format + " " + value, () -> {
            String actual = format(value, indexes.get(format), format, false, locale);
            if (POI_KNOWN_FAILURES.contains(failure(format, value))) {
                Assertions.assertNotEquals(
                        expected, actual, "now gives the expected text, remove it from the known failures");
            } else {
                Assertions.assertEquals(expected, actual);
            }
        });
    }

    private static String format(double value, short formatIndex, String format, boolean date1904, Locale locale) {
        try {
            return new DataFormatter(date1904, locale, false).format(BigDecimal.valueOf(value), formatIndex, format);
        } catch (RuntimeException e) {
            return "threw " + e;
        }
    }

    /**
     * Gives each format its own custom format index: {@link DateUtils#isADateFormat(Short, String)}
     * caches its result by format index.
     */
    private static final class FormatIndexes {
        private final Map<String, Short> indexes = new HashMap<>();

        synchronized short get(String format) {
            return indexes.computeIfAbsent(format, f -> (short) (1000 + indexes.size()));
        }
    }

    private static final class ExcelText {
        private final String source;
        private final String format;
        private final double value;
        private final String expected;
        private final boolean date1904;

        private ExcelText(String source, String format, double value, String expected, boolean date1904) {
            this.source = source;
            this.format = format;
            this.value = value;
            this.expected = expected;
            this.date1904 = date1904;
        }
    }

    private static void readTextFormulas(String name, List<ExcelText> cases) throws Exception {
        try (InputStream in =
                        DataFormatterAgainstExcelTest.class.getClassLoader().getResourceAsStream(RESOURCE_DIR + name);
                Workbook wb = WorkbookFactory.create(in)) {
            boolean date1904 = wb instanceof XSSFWorkbook
                    ? ((XSSFWorkbook) wb).isDate1904()
                    : ((HSSFWorkbook) wb).getInternalWorkbook().isUsing1904DateWindowing();
            for (Sheet sheet : wb) {
                for (Row row : sheet) {
                    for (Cell cell : row) {
                        ExcelText excelText = toExcelText(name, sheet, cell, date1904);
                        if (excelText != null) {
                            cases.add(excelText);
                        }
                    }
                }
            }
        }
    }

    private static ExcelText toExcelText(String name, Sheet sheet, Cell cell, boolean date1904) {
        if (cell.getCellType() != CellType.FORMULA || cell.getCachedFormulaResultType() != CellType.STRING) {
            return null;
        }
        String formula = cell.getCellFormula();
        if (!formula.regionMatches(true, 0, "TEXT(", 0, 5) || !formula.endsWith(")")) {
            return null;
        }
        String args = formula.substring(5, formula.length() - 1);
        int comma = topLevelComma(args);
        if (comma < 0) {
            return null;
        }
        Cell valueCell = referencedCell(sheet, args.substring(0, comma).trim());
        String format = formatArgument(sheet, args.substring(comma + 1).trim());
        if (valueCell == null || format == null || resultType(valueCell) != CellType.NUMERIC) {
            return null;
        }
        String source = name + "!" + sheet.getSheetName() + "!" + cell.getAddress();
        return new ExcelText(source, format, valueCell.getNumericCellValue(), cell.getStringCellValue(), date1904);
    }

    private static int topLevelComma(String args) {
        boolean quoted = false;
        for (int i = 0; i < args.length(); i++) {
            char c = args.charAt(i);
            if (c == '"') {
                quoted = !quoted;
            } else if (c == ',' && !quoted) {
                return i;
            }
        }
        return -1;
    }

    private static String formatArgument(Sheet sheet, String arg) {
        if (arg.length() >= 2 && arg.startsWith("\"") && arg.endsWith("\"")) {
            return arg.substring(1, arg.length() - 1).replace("\"\"", "\"");
        }
        Cell cell = referencedCell(sheet, arg);
        return cell != null && resultType(cell) == CellType.STRING ? cell.getStringCellValue() : null;
    }

    private static Cell referencedCell(Sheet sheet, String ref) {
        if (!ref.matches("\\$?[A-Z]+\\$?[0-9]+")) {
            return null;
        }
        CellReference cellReference = new CellReference(ref);
        Row row = sheet.getRow(cellReference.getRow());
        return row == null ? null : row.getCell(cellReference.getCol());
    }

    private static CellType resultType(Cell cell) {
        return cell.getCellType() == CellType.FORMULA ? cell.getCachedFormulaResultType() : cell.getCellType();
    }
}
