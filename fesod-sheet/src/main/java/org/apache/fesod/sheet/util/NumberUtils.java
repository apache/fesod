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
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.util.Locale;
import java.util.Map;
import org.apache.fesod.common.util.MapUtils;
import org.apache.fesod.common.util.StringUtils;
import org.apache.fesod.sheet.metadata.data.WriteCellData;
import org.apache.fesod.sheet.metadata.property.ExcelContentProperty;

/**
 * Number utils
 *
 *
 */
public class NumberUtils {

    private static final int MAX_LOCALE_CACHE_SIZE = 4;

    private static final int MAX_FORMAT_CACHE_SIZE = 64;

    /**
     * Bounded cache of {@link DecimalFormat}, nested by default FORMAT locale then pattern. {@link DecimalFormat} is
     * not thread-safe, so it is thread-local; both levels evict FIFO to keep long-lived threads bounded.
     */
    private static final ThreadLocal<Map<Locale, Map<String, DecimalFormat>>> DECIMAL_FORMAT_THREAD_LOCAL =
            new ThreadLocal<>();

    private NumberUtils() {}

    /**
     * format
     *
     * @param num
     * @param contentProperty
     * @return
     */
    public static String format(Number num, ExcelContentProperty contentProperty) {
        if (contentProperty == null
                || contentProperty.getNumberFormatProperty() == null
                || StringUtils.isEmpty(contentProperty.getNumberFormatProperty().getFormat())) {
            if (num instanceof BigDecimal) {
                return ((BigDecimal) num).toPlainString();
            } else {
                return num.toString();
            }
        }
        String format = contentProperty.getNumberFormatProperty().getFormat();
        RoundingMode roundingMode = contentProperty.getNumberFormatProperty().getRoundingMode();
        return getCacheDecimalFormat(format, roundingMode).format(num);
    }

    /**
     * format
     *
     * @param num
     * @param contentProperty
     * @return
     */
    public static WriteCellData<?> formatToCellDataString(Number num, ExcelContentProperty contentProperty) {
        return new WriteCellData<>(format(num, contentProperty));
    }

    /**
     * format
     *
     * @param num
     * @param contentProperty
     * @return
     */
    public static WriteCellData<?> formatToCellData(Number num, ExcelContentProperty contentProperty) {
        WriteCellData<?> cellData = new WriteCellData<>(new BigDecimal(num.toString()));
        if (contentProperty != null
                && contentProperty.getNumberFormatProperty() != null
                && StringUtils.isNotBlank(
                        contentProperty.getNumberFormatProperty().getFormat())) {
            WorkBookUtil.fillDataFormat(
                    cellData, contentProperty.getNumberFormatProperty().getFormat(), null);
        }
        return cellData;
    }

    /**
     * parse
     *
     * @param string
     * @param contentProperty
     * @return
     */
    public static Short parseShort(String string, ExcelContentProperty contentProperty) throws ParseException {
        if (!hasFormat(contentProperty)) {
            return new BigDecimal(string).shortValue();
        }
        return parse(string, contentProperty).shortValue();
    }

    /**
     * parse
     *
     * @param string
     * @param contentProperty
     * @return
     */
    public static Long parseLong(String string, ExcelContentProperty contentProperty) throws ParseException {
        if (!hasFormat(contentProperty)) {
            return new BigDecimal(string).longValue();
        }
        return parse(string, contentProperty).longValue();
    }

    /**
     * parse Integer from string
     *
     * @param string          An integer read in string format
     * @param contentProperty Properties of the content read in
     * @return An integer converted from a string
     */
    public static Integer parseInteger(String string, ExcelContentProperty contentProperty) throws ParseException {
        if (!hasFormat(contentProperty)) {
            return new BigDecimal(string).intValue();
        }
        return parse(string, contentProperty).intValue();
    }

    /**
     * parse
     *
     * @param string
     * @param contentProperty
     * @return
     */
    public static Float parseFloat(String string, ExcelContentProperty contentProperty) throws ParseException {
        if (!hasFormat(contentProperty)) {
            return new BigDecimal(string).floatValue();
        }
        return parse(string, contentProperty).floatValue();
    }

    /**
     * parse
     *
     * @param string
     * @param contentProperty
     * @return
     */
    public static BigDecimal parseBigDecimal(String string, ExcelContentProperty contentProperty)
            throws ParseException {
        if (!hasFormat(contentProperty)) {
            return new BigDecimal(string);
        }
        return new BigDecimal(parse(string, contentProperty).toString());
    }

    /**
     * parse
     *
     * @param string
     * @param contentProperty
     * @return
     */
    public static Byte parseByte(String string, ExcelContentProperty contentProperty) throws ParseException {
        if (!hasFormat(contentProperty)) {
            return new BigDecimal(string).byteValue();
        }
        return parse(string, contentProperty).byteValue();
    }

    /**
     * parse
     *
     * @param string
     * @param contentProperty
     * @return
     */
    public static Double parseDouble(String string, ExcelContentProperty contentProperty) throws ParseException {
        if (!hasFormat(contentProperty)) {
            return new BigDecimal(string).doubleValue();
        }
        return parse(string, contentProperty).doubleValue();
    }

    private static boolean hasFormat(ExcelContentProperty contentProperty) {
        return contentProperty != null
                && contentProperty.getNumberFormatProperty() != null
                && !StringUtils.isEmpty(
                        contentProperty.getNumberFormatProperty().getFormat());
    }

    /**
     * parse
     *
     * @param string
     * @param contentProperty
     * @return
     * @throws ParseException
     */
    private static Number parse(String string, ExcelContentProperty contentProperty) throws ParseException {
        String format = contentProperty.getNumberFormatProperty().getFormat();
        RoundingMode roundingMode = contentProperty.getNumberFormatProperty().getRoundingMode();
        DecimalFormat decimalFormat = getCacheDecimalFormat(format, roundingMode);
        decimalFormat.setParseBigDecimal(true);
        return decimalFormat.parse(string);
    }

    private static DecimalFormat getCacheDecimalFormat(String format, RoundingMode roundingMode) {
        Locale locale = Locale.getDefault(Locale.Category.FORMAT);
        Map<Locale, Map<String, DecimalFormat>> localeCache = DECIMAL_FORMAT_THREAD_LOCAL.get();
        if (localeCache == null) {
            localeCache = MapUtils.newBoundedMap(MAX_LOCALE_CACHE_SIZE);
            DECIMAL_FORMAT_THREAD_LOCAL.set(localeCache);
        }
        Map<String, DecimalFormat> formatCache = localeCache.get(locale);
        if (formatCache == null) {
            formatCache = MapUtils.newBoundedMap(MAX_FORMAT_CACHE_SIZE);
            localeCache.put(locale, formatCache);
        }
        DecimalFormat decimalFormat = formatCache.get(format);
        if (decimalFormat == null) {
            decimalFormat = new DecimalFormat(format, DecimalFormatSymbols.getInstance(locale));
            formatCache.put(format, decimalFormat);
        }
        if (decimalFormat.getRoundingMode() != roundingMode) {
            decimalFormat.setRoundingMode(roundingMode);
        }
        return decimalFormat;
    }

    public static void removeThreadLocalCache() {
        DECIMAL_FORMAT_THREAD_LOCAL.remove();
    }
}
