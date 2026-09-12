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

package org.apache.fesod.sheet.util;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import org.apache.fesod.common.util.BooleanUtils;
import org.apache.fesod.common.util.MapUtils;
import org.apache.fesod.common.util.StringUtils;
import org.apache.fesod.sheet.metadata.GlobalConfiguration;
import org.apache.fesod.sheet.metadata.property.ExcelContentProperty;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.util.LocaleUtil;

/**
 * Date utils
 * <p>
 * Excel format-string date/time detection lives in {@link ExcelDateFormatDetector}
 */
public class DateUtils {

    private static final int MAX_LOCALE_CACHE_SIZE = 8;

    private static final int MAX_FORMAT_CACHE_SIZE = 64;

    /**
     * Is a cache of dates
     */
    private static final ThreadLocal<Map<String, SimpleDateFormat>> DATE_FORMAT_THREAD_LOCAL = new ThreadLocal<>();

    /**
     * Bounded cache of {@link DateTimeFormatter}, nested by resolved locale then pattern. Both levels evict FIFO, and
     * the whole cache is cleared by {@link #removeThreadLocalCache()}.
     */
    private static final ThreadLocal<Map<Locale, Map<String, DateTimeFormatter>>> DATE_TIME_FORMATTER_THREAD_LOCAL =
            new ThreadLocal<>();

    /**
     * The epoch date (1970-01-01) used as the date component when converting
     * {@code java.sql.Time} to {@code LocalDateTime}.
     */
    public static final LocalDate EPOCH = LocalDate.of(1970, 1, 1);

    public static final String DATE_FORMAT_10 = "yyyy-MM-dd";
    public static final String DATE_FORMAT_14 = "yyyyMMddHHmmss";
    public static final String DATE_FORMAT_16 = "yyyy-MM-dd HH:mm";
    public static final String DATE_FORMAT_16_FORWARD_SLASH = "yyyy/MM/dd HH:mm";
    public static final String DATE_FORMAT_17 = "yyyyMMdd HH:mm:ss";
    public static final String DATE_FORMAT_19 = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_FORMAT_19_FORWARD_SLASH = "yyyy/MM/dd HH:mm:ss";
    public static final String TIME_FORMAT_5 = "HH:mm";
    public static final String TIME_FORMAT_8 = "HH:mm:ss";
    private static final String MINUS = "-";

    public static String defaultDateFormat = DATE_FORMAT_19;

    public static String defaultLocalDateFormat = DATE_FORMAT_10;

    public static final String DEFAULT_LOCAL_TIME_FORMAT = TIME_FORMAT_8;

    public static final int SECONDS_PER_MINUTE = 60;
    public static final int MINUTES_PER_HOUR = 60;
    public static final int HOURS_PER_DAY = 24;
    public static final int SECONDS_PER_DAY = (HOURS_PER_DAY * MINUTES_PER_HOUR * SECONDS_PER_MINUTE);

    // used to specify that date is invalid
    private static final int BAD_DATE = -1;
    public static final long DAY_MILLISECONDS = SECONDS_PER_DAY * 1000L;

    private DateUtils() {}

    /**
     * convert string to date
     *
     * @param dateString
     * @param dateFormat
     * @return
     * @throws ParseException
     */
    public static Date parseDate(String dateString, String dateFormat) throws ParseException {
        if (StringUtils.isEmpty(dateFormat)) {
            dateFormat = switchDateFormat(dateString);
        }
        return getCacheDateFormat(dateFormat).parse(dateString);
    }

    /**
     * convert string to date
     *
     * @param dateString
     * @param dateFormat
     * @param local
     * @return
     */
    public static LocalDateTime parseLocalDateTime(String dateString, String dateFormat, Locale local) {
        if (StringUtils.isEmpty(dateFormat)) {
            dateFormat = switchDateFormat(dateString);
        }
        return LocalDateTime.parse(dateString, getCacheDateTimeFormat(dateFormat, local));
    }

    /**
     * convert string to date
     *
     * @param dateString
     * @param dateFormat
     * @param local
     * @return
     */
    public static LocalDate parseLocalDate(String dateString, String dateFormat, Locale local) {
        if (StringUtils.isEmpty(dateFormat)) {
            dateFormat = switchDateFormat(dateString);
        }
        return LocalDate.parse(dateString, getCacheDateTimeFormat(dateFormat, local));
    }

    /**
     * convert string to time
     *
     * @param timeString
     * @param timeFormat
     * @param local
     * @return
     */
    public static LocalTime parseLocalTime(String timeString, String timeFormat, Locale local) {
        if (StringUtils.isEmpty(timeFormat)) {
            timeFormat = switchTimeFormat(timeString);
        }
        return LocalTime.parse(timeString, getCacheDateTimeFormat(timeFormat, local));
    }

    /**
     * convert string to date, requiring the string to carry an explicit offset
     *
     * @param dateString date string, e.g. 2020-01-02T03:04:05+08:00
     * @param dateFormat date format, empty means ISO_OFFSET_DATE_TIME
     * @param local local
     * @return
     */
    public static OffsetDateTime parseOffsetDateTime(String dateString, String dateFormat, Locale local) {
        if (StringUtils.isEmpty(dateFormat)) {
            return OffsetDateTime.parse(dateString, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        }
        return OffsetDateTime.parse(dateString, getCacheDateTimeFormat(dateFormat, local));
    }

    /**
     * convert string to date
     *
     * @param dateString
     * @return
     * @throws ParseException
     */
    public static Date parseDate(String dateString) throws ParseException {
        return parseDate(dateString, switchDateFormat(dateString));
    }

    /**
     * switch date format
     *
     * @param dateString
     * @return
     */
    public static String switchDateFormat(String dateString) {
        int length = dateString.length();
        switch (length) {
            case 19:
                if (dateString.contains(MINUS)) {
                    return DATE_FORMAT_19;
                } else {
                    return DATE_FORMAT_19_FORWARD_SLASH;
                }
            case 16:
                if (dateString.contains(MINUS)) {
                    return DATE_FORMAT_16;
                } else {
                    return DATE_FORMAT_16_FORWARD_SLASH;
                }
            case 17:
                return DATE_FORMAT_17;
            case 14:
                return DATE_FORMAT_14;
            case 10:
                return DATE_FORMAT_10;
            default:
                throw new IllegalArgumentException("can not find date format for：" + dateString);
        }
    }

    /**
     * switch time-only format
     *
     * @param timeString
     * @return
     */
    public static String switchTimeFormat(String timeString) {
        int length = timeString.length();
        switch (length) {
            case 8:
                return TIME_FORMAT_8;
            case 5:
                return TIME_FORMAT_5;
            default:
                throw new IllegalArgumentException("can not find time format for：" + timeString);
        }
    }

    /**
     * Format date
     * <p>
     * yyyy-MM-dd HH:mm:ss
     *
     * @param date
     * @return
     */
    public static String format(Date date) {
        return format(date, null);
    }

    /**
     * Format date
     *
     * @param date
     * @param dateFormat
     * @return
     */
    public static String format(Date date, String dateFormat) {
        if (date == null) {
            return null;
        }
        if (StringUtils.isEmpty(dateFormat)) {
            dateFormat = defaultDateFormat;
        }
        return getCacheDateFormat(dateFormat).format(date);
    }

    /**
     * Format date
     *
     * @param date
     * @param dateFormat
     * @return
     */
    public static String format(LocalDateTime date, String dateFormat, Locale local) {
        if (date == null) {
            return null;
        }
        if (StringUtils.isEmpty(dateFormat)) {
            dateFormat = defaultDateFormat;
        }
        return date.format(getCacheDateTimeFormat(dateFormat, local));
    }

    /**
     * Format date
     *
     * @param date
     * @param dateFormat
     * @return
     */
    public static String format(LocalDate date, String dateFormat) {
        return format(date, dateFormat, null);
    }

    /**
     * Format date
     *
     * @param date
     * @param dateFormat
     * @return
     */
    public static String format(LocalDate date, String dateFormat, Locale local) {
        if (date == null) {
            return null;
        }
        if (StringUtils.isEmpty(dateFormat)) {
            dateFormat = defaultLocalDateFormat;
        }
        return date.format(getCacheDateTimeFormat(dateFormat, local));
    }

    /**
     * Format time
     *
     * @param time  LocalTime
     * @param timeFormat time format
     * @return format string
     */
    public static String format(LocalTime time, String timeFormat) {
        return format(time, timeFormat, null);
    }

    /**
     * Format time
     *
     * @param time LocalTime
     * @param timeFormat time format
     * @param local local
     * @return format string
     */
    public static String format(LocalTime time, String timeFormat, Locale local) {
        if (time == null) {
            return null;
        }
        if (StringUtils.isEmpty(timeFormat)) {
            timeFormat = DEFAULT_LOCAL_TIME_FORMAT;
        }
        return time.format(getCacheDateTimeFormat(timeFormat, local));
    }

    /**
     * Format date, preserving the offset in the output
     *
     * @param date date
     * @param dateFormat date format, empty means ISO_OFFSET_DATE_TIME
     * @param local local
     * @return format string
     */
    public static String format(OffsetDateTime date, String dateFormat, Locale local) {
        if (date == null) {
            return null;
        }
        if (StringUtils.isEmpty(dateFormat)) {
            return date.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        }
        return date.format(getCacheDateTimeFormat(dateFormat, local));
    }

    /**
     * Format date
     *
     * @param date
     * @param dateFormat
     * @return
     */
    public static String format(LocalDateTime date, String dateFormat) {
        return format(date, dateFormat, null);
    }

    /**
     * Format date
     *
     * @param date
     * @param dateFormat
     * @return
     */
    public static String format(BigDecimal date, Boolean use1904windowing, String dateFormat) {
        if (date == null) {
            return null;
        }
        LocalDateTime localDateTime =
                DateUtil.getLocalDateTime(date.doubleValue(), BooleanUtils.isTrue(use1904windowing), true);
        return format(localDateTime, dateFormat);
    }

    /**
     * Whether the 1904 windowing system applies to a field: the annotation value wins when explicitly set, otherwise
     * fall back to the global configuration (null-safe, defaults to false).
     *
     * @param contentProperty the field's content property, may be null
     * @param globalConfiguration the global configuration
     * @return true if dates use the 1904 windowing system
     */
    public static boolean isDate1904(ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) {
        if (contentProperty != null && contentProperty.getDateTimeFormatProperty() != null) {
            Boolean propertyUse1904windowing =
                    contentProperty.getDateTimeFormatProperty().getUse1904windowing();
            if (propertyUse1904windowing != null) {
                return propertyUse1904windowing;
            }
        }
        Boolean globalUse1904windowing = globalConfiguration.getUse1904windowing();
        return globalUse1904windowing != null && globalUse1904windowing;
    }

    private static DateTimeFormatter getCacheDateTimeFormat(String dateFormat, Locale locale) {
        Locale actualLocale = locale == null ? Locale.getDefault(Locale.Category.FORMAT) : locale;
        Map<Locale, Map<String, DateTimeFormatter>> localeCache = DATE_TIME_FORMATTER_THREAD_LOCAL.get();
        if (localeCache == null) {
            localeCache = MapUtils.newBoundedMap(MAX_LOCALE_CACHE_SIZE);
            DATE_TIME_FORMATTER_THREAD_LOCAL.set(localeCache);
        }
        Map<String, DateTimeFormatter> formatCache = localeCache.get(actualLocale);
        if (formatCache == null) {
            formatCache = MapUtils.newBoundedMap(MAX_FORMAT_CACHE_SIZE);
            localeCache.put(actualLocale, formatCache);
        }
        DateTimeFormatter formatter = formatCache.get(dateFormat);
        if (formatter == null) {
            formatter = DateTimeFormatter.ofPattern(dateFormat, actualLocale);
            formatCache.put(dateFormat, formatter);
        }
        return formatter;
    }

    private static DateFormat getCacheDateFormat(String dateFormat) {
        Map<String, SimpleDateFormat> dateFormatMap = DATE_FORMAT_THREAD_LOCAL.get();
        if (dateFormatMap == null) {
            dateFormatMap = new HashMap<String, SimpleDateFormat>();
            DATE_FORMAT_THREAD_LOCAL.set(dateFormatMap);
        } else {
            SimpleDateFormat dateFormatCached = dateFormatMap.get(dateFormat);
            if (dateFormatCached != null) {
                return dateFormatCached;
            }
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
        dateFormatMap.put(dateFormat, simpleDateFormat);
        return simpleDateFormat;
    }

    /**
     * Given an Excel date with either 1900 or 1904 date windowing,
     * converts it to a java.util.Date.
     *
     * Excel Dates and Times are stored without any timezone
     * information. If you know (through other means) that your file
     * uses a different TimeZone to the system default, you can use
     * this version of the getJavaDate() method to handle it.
     *
     * @param date             The Excel date.
     * @param use1904windowing true if date uses 1904 windowing,
     *                         or false if using 1900 date windowing.
     * @return Java representation of the date, or null if date is not a valid Excel date
     */
    public static Date getJavaDate(double date, boolean use1904windowing) {
        Calendar calendar = getJavaCalendar(date, use1904windowing, null, true);
        return calendar == null ? null : calendar.getTime();
    }

    /**
     * Get EXCEL date as Java Calendar with given time zone.
     * @param date  The Excel date.
     * @param use1904windowing  true if date uses 1904 windowing,
     *  or false if using 1900 date windowing.
     * @param timeZone The TimeZone to evaluate the date in
     * @param roundSeconds round to closest second
     * @return Java representation of the date, or null if date is not a valid Excel date
     */
    public static Calendar getJavaCalendar(
            double date, boolean use1904windowing, TimeZone timeZone, boolean roundSeconds) {
        if (!isValidExcelDate(date)) {
            return null;
        }
        int wholeDays = (int) Math.floor(date);
        int millisecondsInDay = (int) ((date - wholeDays) * DAY_MILLISECONDS + 0.5);
        Calendar calendar;
        if (timeZone != null) {
            calendar = LocaleUtil.getLocaleCalendar(timeZone);
        } else {
            calendar = LocaleUtil.getLocaleCalendar(); // using default time-zone
        }
        setCalendar(calendar, wholeDays, millisecondsInDay, use1904windowing, roundSeconds);
        return calendar;
    }

    public static void setCalendar(
            Calendar calendar, int wholeDays, int millisecondsInDay, boolean use1904windowing, boolean roundSeconds) {
        int startYear = 1900;
        int dayAdjust = -1; // Excel thinks 2/29/1900 is a valid date, which it isn't
        if (use1904windowing) {
            startYear = 1904;
            dayAdjust = 1; // 1904 date windowing uses 1/2/1904 as the first day
        } else if (wholeDays < 61) {
            // Date is prior to 3/1/1900, so adjust because Excel thinks 2/29/1900 exists
            // If Excel date == 2/29/1900, will become 3/1/1900 in Java representation
            dayAdjust = 0;
        }
        calendar.set(startYear, Calendar.JANUARY, wholeDays + dayAdjust, 0, 0, 0);
        calendar.set(Calendar.MILLISECOND, millisecondsInDay);
        if (calendar.get(Calendar.MILLISECOND) == 0) {
            calendar.clear(Calendar.MILLISECOND);
        }
        if (roundSeconds) {
            // This is different from poi where you need to change 500 to 499
            calendar.add(Calendar.MILLISECOND, 499);
            calendar.clear(Calendar.MILLISECOND);
        }
    }

    /**
     * Given a double, checks if it is a valid Excel date.
     *
     * @return true if valid
     * @param  value the double value
     */
    public static boolean isValidExcelDate(double value) {
        return (value > -Double.MIN_VALUE);
    }

    /**
     * Given an Excel date with either 1900 or 1904 date windowing,
     * converts it to a java.time.LocalDateTime.
     *
     * Excel Dates and Times are stored without any timezone
     * information. If you know (through other means) that your file
     * uses a different TimeZone to the system default, you can use
     * this version of the getJavaDate() method to handle it.
     *
     * @param date             The Excel date.
     * @param use1904windowing true if date uses 1904 windowing,
     *                         or false if using 1900 date windowing.
     * @return Java representation of the date, or null if date is not a valid Excel date
     */
    public static LocalDateTime getLocalDateTime(double date, boolean use1904windowing) {
        return DateUtil.getLocalDateTime(date, use1904windowing, true);
    }

    /**
     * Given an Excel date with either 1900 or 1904 date windowing,
     * converts it to a java.time.LocalDate.
     *
     * Excel Dates and Times are stored without any timezone
     * information. If you know (through other means) that your file
     * uses a different TimeZone to the system default, you can use
     * this version of the getJavaDate() method to handle it.
     *
     * @param date             The Excel date.
     * @param use1904windowing true if date uses 1904 windowing,
     *                         or false if using 1900 date windowing.
     * @return Java representation of the date, or null if date is not a valid Excel date
     */
    public static LocalDate getLocalDate(double date, boolean use1904windowing) {
        LocalDateTime localDateTime = getLocalDateTime(date, use1904windowing);
        return localDateTime == null ? null : localDateTime.toLocalDate();
    }

    /**
     * Given an Excel date with either 1900 or 1904 date windowing,
     * converts it to a java.time.LocalTime.
     *
     * Excel Dates and Times are stored without any timezone
     * information. The date component is discarded; only the
     * wall-clock time of day is returned.
     *
     * @param date             The Excel date.
     * @param use1904windowing true if date uses 1904 windowing,
     *                         or false if using 1900 date windowing.
     * @return Java representation of the time, or null if date is not a valid Excel date
     */
    public static LocalTime getLocalTime(double date, boolean use1904windowing) {
        LocalDateTime localDateTime = getLocalDateTime(date, use1904windowing);
        return localDateTime == null ? null : localDateTime.toLocalTime();
    }

    /**
     * Determine if it is a date format.
     *
     * @param formatIndex
     * @param formatString
     * @return
     */
    public static boolean isADateFormat(Short formatIndex, String formatString) {
        return ExcelDateFormatDetector.isADateFormat(formatIndex, formatString);
    }

    /**
     * Determine if it is a date format.
     *
     * @param formatIndex
     * @param formatString
     * @return
     */
    public static boolean isADateFormatUncached(Short formatIndex, String formatString) {
        return ExcelDateFormatDetector.isADateFormatUncached(formatIndex, formatString);
    }

    /**
     * Given a format ID this will check whether the format represents an internal excel date format or not.
     *
     * @see #isADateFormat(Short, String)
     */
    public static boolean isInternalDateFormat(short format) {
        return ExcelDateFormatDetector.isInternalDateFormat(format);
    }

    public static void removeThreadLocalCache() {
        DATE_FORMAT_THREAD_LOCAL.remove();
        DATE_TIME_FORMATTER_THREAD_LOCAL.remove();
        ExcelDateFormatDetector.removeThreadLocalCache();
    }
}
