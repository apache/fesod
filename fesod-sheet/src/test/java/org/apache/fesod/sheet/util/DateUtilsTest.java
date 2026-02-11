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

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Tests {@link DateUtils}
 */
class DateUtilsTest {

    @AfterEach
    void tearDown() {
        DateUtils.removeThreadLocalCache();
    }

    @Test
    void test_switchDateFormat() {
        Assertions.assertEquals(DateUtils.DATE_FORMAT_19, DateUtils.switchDateFormat("2026-01-01 12:00:00"));
        Assertions.assertEquals(
                DateUtils.DATE_FORMAT_19_FORWARD_SLASH, DateUtils.switchDateFormat("2026/01/01 12:00:00"));

        Assertions.assertEquals(DateUtils.DATE_FORMAT_16, DateUtils.switchDateFormat("2026-01-01 12:00"));
        Assertions.assertEquals(DateUtils.DATE_FORMAT_16_FORWARD_SLASH, DateUtils.switchDateFormat("2026/01/01 12:00"));

        Assertions.assertEquals(DateUtils.DATE_FORMAT_17, DateUtils.switchDateFormat("20260101 12:00:00"));
        Assertions.assertEquals(DateUtils.DATE_FORMAT_14, DateUtils.switchDateFormat("20260101120000"));
        Assertions.assertEquals(DateUtils.DATE_FORMAT_10, DateUtils.switchDateFormat("2026-01-01"));

        Assertions.assertThrows(
                IllegalArgumentException.class, () -> DateUtils.switchDateFormat("invalid_datestring_length"));
    }

    @Test
    void test_parseDate() throws ParseException {
        String dateStr = "2026-10-01 12:30:45";
        Date date1 = DateUtils.parseDate(dateStr);

        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);
        Assertions.assertEquals(2026, cal1.get(Calendar.YEAR));
        Assertions.assertEquals(Calendar.OCTOBER, cal1.get(Calendar.MONTH));
        Assertions.assertEquals(30, cal1.get(Calendar.MINUTE));

        Date date2 = DateUtils.parseDate(dateStr, "");

        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        Assertions.assertEquals(2026, cal2.get(Calendar.YEAR));
        Assertions.assertEquals(Calendar.OCTOBER, cal2.get(Calendar.MONTH));
        Assertions.assertEquals(30, cal2.get(Calendar.MINUTE));
    }

    @Test
    void test_parseLocalDateTime() {
        String dateStr = "2026-10-01 12:30:45";
        String format = "yyyy-MM-dd HH:mm:ss";
        LocalDateTime usResult = DateUtils.parseLocalDateTime(dateStr, format, Locale.US);

        Assertions.assertEquals(2026, usResult.getYear());
        Assertions.assertEquals(10, usResult.getMonthValue());
        Assertions.assertEquals(1, usResult.getDayOfMonth());
        Assertions.assertEquals(12, usResult.getHour());
        Assertions.assertEquals(30, usResult.getMinute());
        Assertions.assertEquals(45, usResult.getSecond());

        LocalDateTime result = DateUtils.parseLocalDateTime(dateStr, format, null);

        Assertions.assertEquals(2026, result.getYear());
        Assertions.assertEquals(10, result.getMonthValue());
        Assertions.assertEquals(1, result.getDayOfMonth());
        Assertions.assertEquals(12, result.getHour());
        Assertions.assertEquals(30, result.getMinute());
        Assertions.assertEquals(45, result.getSecond());

        LocalDateTime autoDetectFormatResult = DateUtils.parseLocalDateTime(dateStr, "", null);

        Assertions.assertEquals(2026, autoDetectFormatResult.getYear());
        Assertions.assertEquals(10, autoDetectFormatResult.getMonthValue());
        Assertions.assertEquals(1, autoDetectFormatResult.getDayOfMonth());
        Assertions.assertEquals(12, autoDetectFormatResult.getHour());
        Assertions.assertEquals(30, autoDetectFormatResult.getMinute());
        Assertions.assertEquals(45, autoDetectFormatResult.getSecond());
    }

    @Test
    void test_parseLocalDate() {
        String dateStr = "2026-10-01";
        String format = "yyyy-MM-dd";
        LocalDate usResult = DateUtils.parseLocalDate(dateStr, format, Locale.US);

        Assertions.assertEquals(2026, usResult.getYear());
        Assertions.assertEquals(10, usResult.getMonthValue());
        Assertions.assertEquals(1, usResult.getDayOfMonth());

        LocalDate result = DateUtils.parseLocalDate(dateStr, format, null);

        Assertions.assertEquals(2026, result.getYear());
        Assertions.assertEquals(10, result.getMonthValue());
        Assertions.assertEquals(1, result.getDayOfMonth());

        LocalDate autoDetectFormatResult = DateUtils.parseLocalDate(dateStr, "", null);

        Assertions.assertEquals(2026, autoDetectFormatResult.getYear());
        Assertions.assertEquals(10, autoDetectFormatResult.getMonthValue());
        Assertions.assertEquals(1, autoDetectFormatResult.getDayOfMonth());
    }

    @Test
    void test_format_default() {
        Date now = new Date();
        String result = DateUtils.format(now);
        Assertions.assertNotNull(result);
        // yyyy-MM-dd HH:mm:ss
        Assertions.assertEquals(19, result.length());

        Assertions.assertNull(DateUtils.format(null));
    }

    @Test
    void test_format_LocalDateTime() {
        LocalDateTime ldt = LocalDateTime.of(2026, 10, 1, 12, 0, 0);
        String format = "dd-MMM-yyyy";

        String usResult = DateUtils.format(ldt, format, Locale.US);
        Assertions.assertEquals("01-Oct-2026", usResult);

        String cnResult = DateUtils.format(ldt, format, Locale.SIMPLIFIED_CHINESE);
        Assertions.assertNotNull(cnResult);

        Assertions.assertNull(DateUtils.format((LocalDateTime) null, format, Locale.US));

        String defaultUSResult = DateUtils.format(ldt, "", Locale.US);
        Assertions.assertEquals("2026-10-01 12:00:00", defaultUSResult);
    }

    @Test
    void test_format_LocalDate() {
        LocalDate ld = LocalDate.of(2026, 10, 1);
        String format = "dd-MMM-yyyy";

        String usResult = DateUtils.format(ld, format, Locale.US);
        Assertions.assertEquals("01-Oct-2026", usResult);

        String cnResult = DateUtils.format(ld, format, Locale.SIMPLIFIED_CHINESE);
        Assertions.assertNotNull(cnResult);

        Assertions.assertNull(DateUtils.format((LocalDate) null, format, Locale.US));

        String defaultUSResult = DateUtils.format(ld, "", Locale.US);
        Assertions.assertEquals("2026-10-01", defaultUSResult);

        String defaultFormatResult = DateUtils.format(ld, "", null);
        Assertions.assertEquals("2026-10-01", defaultFormatResult);

        String defaultFormatResult2 = DateUtils.format(ld, "");
        Assertions.assertEquals("2026-10-01", defaultFormatResult2);
    }

    @Test
    void test_format_BigDecimal() {
        // 43831 = 2020-01-01
        BigDecimal excelDate = new BigDecimal("43831.5");

        String result = DateUtils.format(excelDate, false, DateUtils.DATE_FORMAT_19);
        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.contains("2020-01-01"));

        Assertions.assertNull(DateUtils.format(null, false, DateUtils.DATE_FORMAT_19));
    }

    @ParameterizedTest
    @CsvSource({
        "1.0, 1900-01-01 00:00:00",
        "32.0, 1900-02-01 00:00:00",
        "61.0, 1900-03-01 00:00:00",
        "43831.5, 2020-01-01 12:00:00"
    })
    void test_getJavaDate_1900(double excelValue, String expectedStr) throws ParseException {
        Date date = DateUtils.getJavaDate(excelValue, false);

        SimpleDateFormat sdf = new SimpleDateFormat(DateUtils.DATE_FORMAT_19);
        sdf.setTimeZone(TimeZone.getDefault());

        Assertions.assertEquals(expectedStr, sdf.format(date));
    }

    @ParameterizedTest
    @CsvSource({"0.0, 1904-01-01 00:00:00", "1.0, 1904-01-02 00:00:00", "42369.5, 2020-01-01 12:00:00"})
    void test_getJavaDate_1904(double excelValue, String expectedStr) {
        Date date = DateUtils.getJavaDate(excelValue, true);

        SimpleDateFormat sdf = new SimpleDateFormat(DateUtils.DATE_FORMAT_19);
        sdf.setTimeZone(TimeZone.getDefault());

        Assertions.assertEquals(expectedStr, sdf.format(date));
    }

    @ParameterizedTest
    @CsvSource({
        "1.0, 1900-01-01 00:00:00",
        "32.0, 1900-02-01 00:00:00",
        "61.0, 1900-03-01 00:00:00",
        "43831.5, 2020-01-01 12:00:00"
    })
    void test_getLocalDateTime_1900(double excelValue, String expectedStr) {
        LocalDateTime date = DateUtils.getLocalDateTime(excelValue, false);
        String formatted = date.atZone(TimeZone.getDefault().toZoneId())
                .format(DateTimeFormatter.ofPattern(DateUtils.DATE_FORMAT_19));

        Assertions.assertEquals(expectedStr, formatted);
    }

    @ParameterizedTest
    @CsvSource({"0.0, 1904-01-01 00:00:00", "1.0, 1904-01-02 00:00:00", "42369.5, 2020-01-01 12:00:00"})
    void test_getLocalDateTime_1904(double excelValue, String expectedStr) {
        LocalDateTime date = DateUtils.getLocalDateTime(excelValue, true);

        String formatted = date.atZone(TimeZone.getDefault().toZoneId())
                .format(DateTimeFormatter.ofPattern(DateUtils.DATE_FORMAT_19));

        Assertions.assertEquals(expectedStr, formatted);
    }

    @ParameterizedTest
    @CsvSource({"1.0, 1900-01-01", "32.0, 1900-02-01", "61.0, 1900-03-01", "43831.5, 2020-01-01"})
    void test_getLocalDate_1900(double excelValue, String expectedStr) {
        LocalDate date = DateUtils.getLocalDate(excelValue, false);
        Assertions.assertNotNull(date);

        String formatted = date.format(DateTimeFormatter.ofPattern(DateUtils.DATE_FORMAT_10));
        Assertions.assertEquals(expectedStr, formatted);
    }

    @ParameterizedTest
    @CsvSource({"0.0, 1904-01-01", "1.0, 1904-01-02", "42369.5, 2020-01-01"})
    void test_getLocalDate_1904(double excelValue, String expectedStr) {
        LocalDate date = DateUtils.getLocalDate(excelValue, true);
        Assertions.assertNotNull(date);

        String formatted = date.format(DateTimeFormatter.ofPattern(DateUtils.DATE_FORMAT_10));
        Assertions.assertEquals(expectedStr, formatted);
    }

    @Test
    void test_isValidExcelDate() {
        Assertions.assertTrue(DateUtils.isValidExcelDate(0.0));
        Assertions.assertTrue(DateUtils.isValidExcelDate(100.0));
        Assertions.assertFalse(DateUtils.isValidExcelDate(-1.0));
    }

    @Test
    void test_getJavaCalendar_rounding() {
        double base = 44000.0;
        double halfDay = 0.5;

        Calendar cal = DateUtils.getJavaCalendar(base + halfDay, false, null, true);
        Assertions.assertNotNull(cal);
        Assertions.assertEquals(12, cal.get(Calendar.HOUR_OF_DAY));
        Assertions.assertEquals(0, cal.get(Calendar.SECOND));
        Assertions.assertEquals(0, cal.get(Calendar.MILLISECOND));
    }

    @ParameterizedTest
    @ValueSource(shorts = {0x0e, 0x0f, 0x16, 0x2d, 0x2f})
    void test_isADateFormat_internal(short formatId) {
        Assertions.assertTrue(DateUtils.isADateFormat(formatId, null));
        Assertions.assertTrue(DateUtils.isADateFormat(formatId, "General"));
    }

    @ParameterizedTest
    @ValueSource(
            strings = {
                "yyyy-mm-dd",
                "mm/dd/yy",
                "hh:mm:ss",
                "yyyy年mm月dd日",
                "[$-F800]dddd\\,\\ mmmm\\ dd\\,\\ yyyy",
                "[DBNum1]yyyy年mm月dd日",
                "yyyy/mm/dd;@",
                "[h]:mm:ss",
                "mm:ss.0",
                "yyyy-MM-dd HH:mm:ss"
            })
    void test_isADateFormat_true(String formatString) {
        Assertions.assertTrue(DateUtils.isADateFormat((short) 100, formatString));
    }

    @ParameterizedTest
    @ValueSource(strings = {"General", "0.00", "#", "#,##0", "0%", "@", "_-* #,##0_-", ""})
    void test_isADateFormat_false(String formatString) {
        Assertions.assertFalse(DateUtils.isADateFormat((short) 100, formatString));

        Assertions.assertFalse(DateUtils.isADateFormat(null, formatString));
    }

    @Test
    void test_isADateFormat_Cache() throws NoSuchFieldException, IllegalAccessException {
        short formatId = 200;
        String formatStr = "yyyy-MM-dd";

        boolean res1 = DateUtils.isADateFormat(formatId, formatStr);
        Assertions.assertTrue(res1);

        Field threadLocalField = DateUtils.class.getDeclaredField("DATE_THREAD_LOCAL");
        threadLocalField.setAccessible(true);
        ThreadLocal<Map<Short, Boolean>> tl = (ThreadLocal<Map<Short, Boolean>>) threadLocalField.get(null);

        Map<Short, Boolean> cache = tl.get();
        Assertions.assertNotNull(cache);
        Assertions.assertTrue(cache.containsKey(formatId));
        Assertions.assertTrue(cache.get(formatId));

        boolean res2 = DateUtils.isADateFormat(formatId, formatStr);
        Assertions.assertTrue(res2);
    }

    @Test
    void test_removeThreadLocalCache() throws NoSuchFieldException, IllegalAccessException {
        DateUtils.format(new Date(), "yyyy-MM-dd");
        DateUtils.isADateFormat((short) 100, "yyyy-MM-dd");

        Field f1 = DateUtils.class.getDeclaredField("DATE_THREAD_LOCAL");
        Field f2 = DateUtils.class.getDeclaredField("DATE_FORMAT_THREAD_LOCAL");
        f1.setAccessible(true);
        f2.setAccessible(true);

        Assertions.assertNotNull(((ThreadLocal<?>) f1.get(null)).get());
        Assertions.assertNotNull(((ThreadLocal<?>) f2.get(null)).get());

        DateUtils.removeThreadLocalCache();

        Assertions.assertNull(((ThreadLocal<?>) f1.get(null)).get());
        Assertions.assertNull(((ThreadLocal<?>) f2.get(null)).get());
    }
}
