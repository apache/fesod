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
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.util.Locale;
import java.util.stream.Stream;
import org.apache.fesod.sheet.testkit.Tags;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Tests {@link DataFormatter}.
 */
@Tag(Tags.UNIT)
class DataFormatterTest {

    /** Display name for the {@code (data, pattern, expected)} tables. */
    private static final String PATTERN_AND_RESULT = "[{index}] {1} -> {2}";

    /** Display name for the {@code (data, dataFormat, pattern, expected)} tables. */
    private static final String DATE_PATTERN_AND_RESULT = "[{index}] {2} -> {3}";

    private static DataFormatter formatter() {
        return new DataFormatter(false, Locale.US, false);
    }

    private static String format(String data, String dataFormatString) {
        return format(data, null, dataFormatString);
    }

    private static String format(String data, Short dataFormat, String dataFormatString) {
        return formatter().format(new BigDecimal(data), dataFormat, dataFormatString);
    }

    @ParameterizedTest(name = PATTERN_AND_RESULT)
    @CsvSource(
            delimiter = '|',
            value = {
                "   1234.5678 | General | 1234.5678",
                // 1E11 is where General switches to scientific notation once that is turned on
                "100000000000 | General | 100000000000",
            })
    void formatsGeneralPattern(String data, String pattern, String expected) {
        Assertions.assertEquals(expected, format(data, pattern));
    }

    @Test
    void formatsGeneralWithScientificNotation() {
        DataFormatter formatter = new DataFormatter(false, Locale.US, true);
        Assertions.assertEquals("1E+11", formatter.format(new BigDecimal("100000000000"), null, "General"));
    }

    @ParameterizedTest(name = "[{index}] {0} -> {1}")
    @CsvSource(
            delimiter = '|',
            value = {
                "en-US | 1234.57",
                "de-DE | 1234,57",
            })
    void formatsDecimalPatternWithLocaleSymbols(String languageTag, String expected) {
        DataFormatter formatter = new DataFormatter(false, Locale.forLanguageTag(languageTag), false);
        Assertions.assertEquals(expected, formatter.format(new BigDecimal("1234.5678"), null, "0.00"));
    }

    @ParameterizedTest(name = PATTERN_AND_RESULT)
    @CsvSource(
            delimiter = '|',
            value = {
                " 2.345 | 0.00 |  2.35",
                "-2.345 | 0.00 | -2.35",
            })
    void roundsHalfUpLikeExcel(String data, String pattern, String expected) {
        Assertions.assertEquals(expected, format(data, pattern));
    }

    @ParameterizedTest(name = PATTERN_AND_RESULT)
    @CsvSource(
            delimiter = '|',
            value = {
                "1234567.891 | #,##0.00 | 1,234,567.89",
                "      12345 | #'##0    | 12'345",
            })
    void usesAlternateGroupingSeparator(String data, String pattern, String expected) {
        Assertions.assertEquals(expected, format(data, pattern));
    }

    @ParameterizedTest(name = PATTERN_AND_RESULT)
    @CsvSource(
            delimiter = '|',
            value = {
                "1234.5 | [Red]0.00         | 1234.50",
                "1234.5 | [$$-1009]#,##0.00 | $1,234.50",
                "1234.5 | [$-1009]#,##0.00  | 1,234.50",
            })
    void stripsColourAndLocalePrefixes(String data, String pattern, String expected) {
        Assertions.assertEquals(expected, format(data, pattern));
    }

    @ParameterizedTest(name = PATTERN_AND_RESULT)
    @CsvSource(
            delimiter = '|',
            value = {
                "1234.5 | _(#,##0.00_)       | 1,234.50",
                "1234.5 | 0.00\" kg\"        | 1234.50 kg",
                "1234.5 | #,##0.00\\ \"USD\" | 1,234.50 USD",
            })
    void stripsPaddingAndKeepsQuotedLiterals(String data, String pattern, String expected) {
        Assertions.assertEquals(expected, format(data, pattern));
    }

    @ParameterizedTest(name = PATTERN_AND_RESULT)
    @CsvSource(
            delimiter = '|',
            value = {
                "   5 | 0%              | 500%",
                "   5 | 0\\%            | 5%",
                "0.05 | 0\\%            | 0%",
                "  -5 | 0\\%            | -5%",
                "   5 | 0.00\\%         | 5.00%",
                "   5 | 0\\%;[Red]0\\%  | 5%",
                "   5 | 0\\%;-0\\%;0\\% | 5%",
            })
    void doesNotScaleEscapedPercent(String data, String pattern, String expected) {
        Assertions.assertEquals(expected, format(data, pattern));
    }

    @ParameterizedTest(name = PATTERN_AND_RESULT)
    @CsvSource(
            delimiter = '|',
            value = {
                "12345.678 | 0.00E00  | 1.23E+04",
                " 0.000123 | 0.00E+00 | 1.23E-04",
            })
    void normalisesScientificExponentSign(String data, String pattern, String expected) {
        Assertions.assertEquals(expected, format(data, pattern));
    }

    @ParameterizedTest(name = PATTERN_AND_RESULT)
    @CsvSource(
            delimiter = '|',
            value = {
                " 1.5 | # #/#   | 1 1/2",
                "0.25 | # ??/?? | 1/4",
            })
    void formatsFractions(String data, String pattern, String expected) {
        Assertions.assertEquals(expected, format(data, pattern));
    }

    @ParameterizedTest(name = PATTERN_AND_RESULT)
    @CsvSource(
            delimiter = '|',
            value = {
                // SSN, plain and as Excel escapes it
                " 123456789 | 000-00-0000                       | 123-45-6789",
                " 123456789 | 000\\-00\\-0000                   | 123-45-6789",
                // zip + 4
                " 123456789 | 00000-0000                        | 12345-6789",
                " 123456789 | 00000\\-0000                      | 12345-6789",
                // phone, 10 / 7 / <= 4 digits
                "8005551234 | ###-####;(###) ###-####           | (800) 555-1234",
                "8005551234 | ###\\-####;\\(###\\)\\ ###\\-#### | (800) 555-1234",
                "   5551234 | ###-####;(###) ###-####           | 555-1234",
                "       123 | ###-####;(###) ###-####           | 123",
            })
    void formatsBuiltInSpecialPatterns(String data, String pattern, String expected) {
        Assertions.assertEquals(expected, format(data, pattern));
    }

    static Stream<Arguments> formatsDatesWithSelectedWindowing() {
        return Stream.of(
                Arguments.of(false, "1900-01-01"), Arguments.of(true, "1904-01-02"), Arguments.of(null, "1900-01-01"));
    }

    @ParameterizedTest(name = "[{index}] use1904windowing={0} -> {1}")
    @MethodSource
    void formatsDatesWithSelectedWindowing(Boolean use1904windowing, String expected) {
        DataFormatter formatter = new DataFormatter(use1904windowing, Locale.US, false);

        Assertions.assertEquals(expected, formatter.format(BigDecimal.ONE, (short) 14, "yyyy-mm-dd"));
    }

    @ParameterizedTest(name = DATE_PATTERN_AND_RESULT)
    @CsvSource(
            delimiter = '|',
            value = {
                "  42000 | 14 | dddd, mmmm dd, yyyy | Saturday, December 27, 2014",
                "  42000 | 14 | dd-mmm              | 27-Dec",
                "42000.5 | 22 | m\\/d\\/yyyy\\ h:mm | 12/27/2014 12:00",
            })
    void formatsDayAndMonthNames(String data, short dataFormat, String pattern, String expected) {
        Assertions.assertEquals(expected, format(data, dataFormat, pattern));
    }

    @ParameterizedTest(name = DATE_PATTERN_AND_RESULT)
    @CsvSource(
            delimiter = '|',
            value = {
                "42000.5 | 22 | yyyy-mm-dd\\Thh:mm:ss | 2014-12-27T12:00:00",
                "  42000 | 14 | mm\"-\"dd\"-\"yyyy    | 12-27-2014",
            })
    void unescapesQuotedLiteralsInDatePatterns(String data, short dataFormat, String pattern, String expected) {
        Assertions.assertEquals(expected, format(data, dataFormat, pattern));
    }

    @ParameterizedTest(name = "[{index}] {0} -> {1}")
    @CsvSource(
            delimiter = '|',
            value = {
                "en-US | 6:00 PM",
                "zh-CN | 6:00 下午",
            })
    void formatsAmPmMarkerPerLocale(String languageTag, String expected) {
        DataFormatter formatter = new DataFormatter(false, Locale.forLanguageTag(languageTag), false);

        Assertions.assertEquals(expected, formatter.format(new BigDecimal("0.75"), (short) 18, "h:mm AM/PM"));
    }

    @Test
    void formatsElapsedTime() {
        Assertions.assertEquals("36:00:00", format("1.5", (short) 46, "[h]:mm:ss"));
    }

    @ParameterizedTest(name = PATTERN_AND_RESULT)
    @CsvSource(
            delimiter = '|',
            value = {
                "  5 | 0.00;[Red]-0.00;\"zero\" | 5.00",
                " -5 | 0.00;[Red]-0.00;\"zero\" | -5.00",
                "  0 | 0.00;[Red]-0.00;\"zero\" | zero",
                "150 | [>=100]\"big\";0.00      | big",
            })
    void delegatesMultiPartPatternsToCellFormat(String data, String pattern, String expected) {
        Assertions.assertEquals(expected, format(data, pattern));
    }

    @Test
    void delegatesMultiPartDatePatternsToCellFormat() {
        Assertions.assertEquals("2014-12-27", format("42000", (short) 14, "[>=1]yyyy-mm-dd;\"negative\""));
    }

    @Test
    void fallsBackToGeneralWhenPatternIsUnparseable() {
        Assertions.assertEquals("1.5", format("1.5", "0.0.0#00#"));
    }

    @Test
    void addFormatOverridesPatternHandling() {
        DataFormatter formatter = formatter();
        formatter.addFormat("0.00", new PrefixFormat("custom:"));

        Assertions.assertEquals("custom:1.5", formatter.format(new BigDecimal("1.5"), null, "0.00"));
    }

    @Test
    void setDefaultNumberFormatReplacesCachedAndFutureDefaults() {
        DataFormatter formatter = formatter();
        Assertions.assertEquals("1.5", formatter.format(new BigDecimal("1.5"), null, "General"));

        formatter.setDefaultNumberFormat(new PrefixFormat("default:"));

        Assertions.assertEquals("default:1.5", formatter.format(new BigDecimal("1.5"), null, "General"));
        Assertions.assertEquals("default:1.5", formatter.format(new BigDecimal("1.5"), null, "@"));
    }

    @Test
    void setExcelStyleRoundingModeOverridesDecimalFormatDefault() {
        DecimalFormat decimalFormat = new DecimalFormat("0");
        Assertions.assertEquals(RoundingMode.HALF_EVEN, decimalFormat.getRoundingMode());

        DataFormatter.setExcelStyleRoundingMode(decimalFormat);
        Assertions.assertEquals(RoundingMode.HALF_UP, decimalFormat.getRoundingMode());
        Assertions.assertEquals("1", decimalFormat.format(0.5));

        DataFormatter.setExcelStyleRoundingMode(decimalFormat, RoundingMode.DOWN);
        Assertions.assertEquals(RoundingMode.DOWN, decimalFormat.getRoundingMode());
        Assertions.assertEquals("1", decimalFormat.format(1.9));
    }

    /**
     * A {@link Format} that marks its output, so replacing the default format is observable.
     */
    private static final class PrefixFormat extends Format {

        private final String prefix;

        private PrefixFormat(String prefix) {
            this.prefix = prefix;
        }

        @Override
        public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
            return toAppendTo.append(prefix).append(obj);
        }

        @Override
        public Object parseObject(String source, ParsePosition pos) {
            throw new UnsupportedOperationException();
        }
    }
}
