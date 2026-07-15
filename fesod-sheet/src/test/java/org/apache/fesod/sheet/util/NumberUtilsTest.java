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

package org.apache.fesod.sheet.util;

import java.io.File;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import org.apache.fesod.sheet.FesodSheet;
import org.apache.fesod.sheet.metadata.data.WriteCellData;
import org.apache.fesod.sheet.metadata.property.ExcelContentProperty;
import org.apache.fesod.sheet.metadata.property.NumberFormatProperty;
import org.apache.fesod.sheet.testkit.Tags;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.api.parallel.ResourceLock;
import org.junit.jupiter.api.parallel.Resources;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Tests {@link NumberUtils}
 */
@Tag(Tags.UNIT)
@ExtendWith(MockitoExtension.class)
class NumberUtilsTest {

    @Mock
    private ExcelContentProperty contentProperty;

    @Mock
    private NumberFormatProperty numberFormatProperty;

    @AfterEach
    void tearDown() {
        NumberUtils.removeThreadLocalCache();
    }

    @Test
    void test_format_noFormat_BigDecimal() {
        BigDecimal bigDecimal = new BigDecimal("0.0000001");

        String result = NumberUtils.format(bigDecimal, null);

        Assertions.assertEquals("0.0000001", result);
    }

    @Test
    void test_format_noFormat_Integer() {
        Integer num = 123;
        String result = NumberUtils.format(num, null);

        Assertions.assertEquals("123", result);
    }

    @Test
    void test_format_withFormat() {
        // Setup
        Mockito.when(contentProperty.getNumberFormatProperty()).thenReturn(numberFormatProperty);
        Mockito.when(numberFormatProperty.getFormat()).thenReturn("#.00");
        Mockito.when(numberFormatProperty.getRoundingMode()).thenReturn(RoundingMode.UP);

        // 123.451 -> UP -> 123.46
        String result = NumberUtils.format(123.451, contentProperty);

        Assertions.assertEquals("123.46", result);
    }

    @Test
    @ResourceLock(Resources.LOCALE)
    void test_formatCache_tracksDefaultFormatLocaleChanges() {
        Locale originalLocale = Locale.getDefault(Locale.Category.FORMAT);
        try {
            String format = "#,##0.00";
            ExcelContentProperty property = new ExcelContentProperty();
            property.setNumberFormatProperty(new NumberFormatProperty(format, RoundingMode.HALF_UP));

            Locale.setDefault(Locale.Category.FORMAT, Locale.FRANCE);
            String franceResult = NumberUtils.format(1234.5, property);
            Locale.setDefault(Locale.Category.FORMAT, Locale.US);
            String usResult = NumberUtils.format(1234.5, property);

            Assertions.assertEquals(
                    new DecimalFormat(format, DecimalFormatSymbols.getInstance(Locale.FRANCE)).format(1234.5),
                    franceResult);
            Assertions.assertEquals(
                    new DecimalFormat(format, DecimalFormatSymbols.getInstance(Locale.US)).format(1234.5), usResult);
            Assertions.assertNotEquals(franceResult, usResult);
        } finally {
            Locale.setDefault(Locale.Category.FORMAT, originalLocale);
        }
    }

    @Test
    @ResourceLock(Resources.LOCALE)
    @SuppressWarnings("unchecked")
    void test_formatCache_isBounded() throws NoSuchFieldException, IllegalAccessException {
        int localeCap = readIntConstant("MAX_LOCALE_CACHE_SIZE");
        int formatCap = readIntConstant("MAX_FORMAT_CACHE_SIZE");
        Locale originalLocale = Locale.getDefault(Locale.Category.FORMAT);
        try {
            Locale.setDefault(Locale.Category.FORMAT, Locale.US);
            ExcelContentProperty property = new ExcelContentProperty();
            StringBuilder format = new StringBuilder("0.");
            for (int i = 0; i <= formatCap; i++) {
                format.append('0');
                property.setNumberFormatProperty(new NumberFormatProperty(format.toString(), RoundingMode.HALF_UP));
                NumberUtils.format(1234.5, property);
            }

            Field field = NumberUtils.class.getDeclaredField("DECIMAL_FORMAT_THREAD_LOCAL");
            field.setAccessible(true);
            ThreadLocal<Map<Locale, Map<String, DecimalFormat>>> threadLocal =
                    (ThreadLocal<Map<Locale, Map<String, DecimalFormat>>>) field.get(null);
            Map<Locale, Map<String, DecimalFormat>> localeCache = threadLocal.get();
            Assertions.assertEquals(formatCap, localeCache.get(Locale.US).size());

            for (int i = 0; i < localeCap + 2; i++) {
                Locale.setDefault(Locale.Category.FORMAT, new Locale("en", "X" + i));
                NumberUtils.format(1234.5, property);
            }
            Assertions.assertEquals(localeCap, localeCache.size());
        } finally {
            Locale.setDefault(Locale.Category.FORMAT, originalLocale);
        }
    }

    private static int readIntConstant(String name) throws NoSuchFieldException, IllegalAccessException {
        Field field = NumberUtils.class.getDeclaredField(name);
        field.setAccessible(true);
        return field.getInt(null);
    }

    @Test
    void test_formatToCellDataString() {
        Integer num = 100;
        WriteCellData<?> data = NumberUtils.formatToCellDataString(num, null);

        Assertions.assertEquals("100", data.getStringValue());
    }

    @Test
    void test_formatToCellData_withFormat() {
        // Setup
        Mockito.when(contentProperty.getNumberFormatProperty()).thenReturn(numberFormatProperty);
        Mockito.when(numberFormatProperty.getFormat()).thenReturn("#,##0.00");

        // Execute
        WriteCellData<?> data = NumberUtils.formatToCellData(1234.56, contentProperty);

        // Verify Value
        Assertions.assertEquals(0, new BigDecimal("1234.56").compareTo(data.getNumberValue()));
        Assertions.assertNotNull(data.getWriteCellStyle());
        Assertions.assertNotNull(data.getWriteCellStyle().getDataFormatData());
        Assertions.assertEquals(
                "#,##0.00", data.getWriteCellStyle().getDataFormatData().getFormat());
    }

    @Test
    void test_formatToCellData_noFormat() {
        WriteCellData<?> data = NumberUtils.formatToCellData(99, null);

        Assertions.assertEquals(0, new BigDecimal("99").compareTo(data.getNumberValue()));
        Assertions.assertNull(data.getWriteCellStyle());
    }

    @Test
    void test_parseInteger_noFormat() throws ParseException {
        Integer result = NumberUtils.parseInteger("100", null);
        Assertions.assertEquals(100, result);
    }

    @Test
    void test_parseInteger_withFormat() throws ParseException {
        // Setup
        Mockito.when(contentProperty.getNumberFormatProperty()).thenReturn(numberFormatProperty);
        Mockito.when(numberFormatProperty.getFormat()).thenReturn("#");
        Mockito.when(numberFormatProperty.getRoundingMode()).thenReturn(RoundingMode.HALF_UP);

        // Execute
        Integer result = NumberUtils.parseInteger("99", contentProperty);

        // Verify
        Assertions.assertEquals(99, result);
    }

    @Test
    void test_parseBigDecimal_noFormat() throws ParseException {
        BigDecimal result = NumberUtils.parseBigDecimal("123.456789", null);
        Assertions.assertEquals(new BigDecimal("123.456789"), result);
    }

    @Test
    void test_parseBigDecimal_withFormat() throws ParseException {
        // Setup
        Mockito.when(contentProperty.getNumberFormatProperty()).thenReturn(numberFormatProperty);
        Mockito.when(numberFormatProperty.getFormat()).thenReturn("#,##0.00");
        Mockito.when(numberFormatProperty.getRoundingMode()).thenReturn(RoundingMode.HALF_UP);

        // Execute: "1,234.56" -> 1234.56
        BigDecimal result = NumberUtils.parseBigDecimal("1,234.56", contentProperty);

        // Verify
        Assertions.assertEquals(0, new BigDecimal("1234.56").compareTo(result));
    }

    @Test
    void test_parseFloat_noFormat() throws ParseException {
        Float result = NumberUtils.parseFloat("12.34", null);
        Assertions.assertEquals(12.34f, result, 0.0001f);
    }

    @Test
    void test_parseFloat_withFormat() throws ParseException {
        // Setup
        Mockito.when(contentProperty.getNumberFormatProperty()).thenReturn(numberFormatProperty);
        Mockito.when(numberFormatProperty.getFormat()).thenReturn("#.00");
        Mockito.when(numberFormatProperty.getRoundingMode()).thenReturn(RoundingMode.HALF_UP);

        // Execute
        Float result = NumberUtils.parseFloat("12.34", contentProperty);

        // Verify
        Assertions.assertEquals(12.34f, result, 0.0001f);
    }

    @Test
    void test_parseDouble_noFormat() throws ParseException {
        Double result = NumberUtils.parseDouble("123.456", null);
        Assertions.assertEquals(123.456, result, 0.000001);
    }

    @Test
    void test_parseDouble_withFormat() throws ParseException {
        // Setup
        Mockito.when(contentProperty.getNumberFormatProperty()).thenReturn(numberFormatProperty);
        // 12.34% -> 0.1234
        Mockito.when(numberFormatProperty.getFormat()).thenReturn("0.00%");
        Mockito.when(numberFormatProperty.getRoundingMode()).thenReturn(RoundingMode.HALF_UP);

        // Execute
        Double result = NumberUtils.parseDouble("12.34%", contentProperty);

        // Verify
        Assertions.assertEquals(0.1234, result, 0.000001);
    }

    @Test
    void test_parseLong_noFormat() throws ParseException {
        Long result = NumberUtils.parseLong("123456789", null);

        Assertions.assertEquals(123456789L, result);
    }

    @Test
    void test_parseLong_withFormat() throws ParseException {
        // Setup
        Mockito.when(contentProperty.getNumberFormatProperty()).thenReturn(numberFormatProperty);
        Mockito.when(numberFormatProperty.getFormat()).thenReturn("#,###");
        Mockito.when(numberFormatProperty.getRoundingMode()).thenReturn(RoundingMode.HALF_UP);

        // Execute: "1,234" -> 1234L
        Long result = NumberUtils.parseLong("1,234", contentProperty);

        // Verify
        Assertions.assertEquals(1234L, result);
    }

    @Test
    void test_parseByte_noFormat() throws ParseException {
        Byte result = NumberUtils.parseByte("127", null);
        Assertions.assertEquals((byte) 127, result);
    }

    @Test
    void test_parseByte_withFormat() throws ParseException {
        // Setup
        Mockito.when(contentProperty.getNumberFormatProperty()).thenReturn(numberFormatProperty);
        Mockito.when(numberFormatProperty.getFormat()).thenReturn("#");
        Mockito.when(numberFormatProperty.getRoundingMode()).thenReturn(RoundingMode.HALF_UP);

        // Execute
        Byte result = NumberUtils.parseByte("100", contentProperty);

        // Verify
        Assertions.assertEquals((byte) 100, result);
    }

    @Test
    void test_parseShort_withFormat() throws ParseException {
        Mockito.when(contentProperty.getNumberFormatProperty()).thenReturn(numberFormatProperty);
        Mockito.when(numberFormatProperty.getFormat()).thenReturn("#");
        Mockito.when(numberFormatProperty.getRoundingMode()).thenReturn(RoundingMode.UP);

        Short resultSimple = NumberUtils.parseShort("123", contentProperty);
        Assertions.assertEquals((short) 123, resultSimple);
    }

    @Test
    void test_parseShort_noFormat() throws ParseException {
        Short resultNullProp = NumberUtils.parseShort("123", null);
        Assertions.assertEquals((short) 123, resultNullProp);

        Mockito.reset(contentProperty);
        Mockito.when(contentProperty.getNumberFormatProperty()).thenReturn(null);

        Short resultNullFormat = NumberUtils.parseShort("456", contentProperty);
        Assertions.assertEquals((short) 456, resultNullFormat);

        Mockito.when(contentProperty.getNumberFormatProperty()).thenReturn(numberFormatProperty);
        Mockito.when(numberFormatProperty.getFormat()).thenReturn(null);

        Short resultEmptyFormat = NumberUtils.parseShort("789", contentProperty);
        Assertions.assertEquals((short) 789, resultEmptyFormat);
    }

    @Test
    void test_parse_Error() {
        Mockito.when(contentProperty.getNumberFormatProperty()).thenReturn(numberFormatProperty);
        Mockito.when(numberFormatProperty.getFormat()).thenReturn("#");
        Mockito.when(numberFormatProperty.getRoundingMode()).thenReturn(RoundingMode.UP);

        Assertions.assertThrows(ParseException.class, () -> {
            NumberUtils.parseInteger("not_a_number", contentProperty);
        });
    }

    @Test
    void test_removeThreadLocalCache() throws NoSuchFieldException, IllegalAccessException {
        ExcelContentProperty property = new ExcelContentProperty();
        property.setNumberFormatProperty(new NumberFormatProperty("#,##0.00", RoundingMode.HALF_UP));
        NumberUtils.format(1234.5, property);

        Field field = NumberUtils.class.getDeclaredField("DECIMAL_FORMAT_THREAD_LOCAL");
        field.setAccessible(true);
        ThreadLocal<?> threadLocal = (ThreadLocal<?>) field.get(null);
        Assertions.assertNotNull(threadLocal.get());

        NumberUtils.removeThreadLocalCache();

        Assertions.assertNull(threadLocal.get());
    }

    @Test
    void test_readAndWriteFinishRemoveThreadLocalCache(@TempDir Path tempDir)
            throws NoSuchFieldException, IllegalAccessException {
        Field field = NumberUtils.class.getDeclaredField("DECIMAL_FORMAT_THREAD_LOCAL");
        field.setAccessible(true);
        ThreadLocal<?> threadLocal = (ThreadLocal<?>) field.get(null);
        ExcelContentProperty property = new ExcelContentProperty();
        property.setNumberFormatProperty(new NumberFormatProperty("#,##0.00", RoundingMode.HALF_UP));
        File file = tempDir.resolve("thread-local-cache.xlsx").toFile();

        NumberUtils.format(1234.5, property);
        Assertions.assertNotNull(threadLocal.get());
        FesodSheet.write(file).sheet().doWrite(Collections.singletonList(Collections.singletonList("value")));
        Assertions.assertNull(threadLocal.get());

        NumberUtils.format(1234.5, property);
        Assertions.assertNotNull(threadLocal.get());
        FesodSheet.read(file).sheet().doReadSync();
        Assertions.assertNull(threadLocal.get());
    }
}
