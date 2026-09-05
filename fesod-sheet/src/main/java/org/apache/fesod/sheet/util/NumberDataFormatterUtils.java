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
import java.util.Locale;
import org.apache.fesod.sheet.metadata.GlobalConfiguration;
import org.apache.fesod.sheet.metadata.format.DataFormatter;

/**
 * Convert number data, including date.
 *
 *
 **/
public class NumberDataFormatterUtils {

    /**
     * Cache DataFormatter.
     */
    private static final ThreadLocal<DataFormatterCache> DATA_FORMATTER_THREAD_LOCAL =
            new ThreadLocal<DataFormatterCache>();

    private static final class DataFormatterCache {
        private final boolean use1904windowing;
        private final Locale locale;
        private final boolean useScientificFormat;
        private final DataFormatter dataFormatter;

        private DataFormatterCache(boolean use1904windowing, Locale locale, boolean useScientificFormat) {
            this.use1904windowing = use1904windowing;
            this.locale = locale;
            this.useScientificFormat = useScientificFormat;
            this.dataFormatter = new DataFormatter(use1904windowing, locale, useScientificFormat);
        }

        private boolean matches(boolean use1904windowing, Locale locale, boolean useScientificFormat) {
            return this.use1904windowing == use1904windowing
                    && this.locale.equals(locale)
                    && this.useScientificFormat == useScientificFormat;
        }
    }

    /**
     * Format number data.
     *
     * @param data
     * @param dataFormat          Not null.
     * @param dataFormatString
     * @param globalConfiguration
     * @return
     */
    public static String format(
            BigDecimal data, Short dataFormat, String dataFormatString, GlobalConfiguration globalConfiguration) {
        if (globalConfiguration == null) {
            return format(data, dataFormat, dataFormatString, null, null, null);
        }
        return format(
                data,
                dataFormat,
                dataFormatString,
                globalConfiguration.getUse1904windowing(),
                globalConfiguration.getLocale(),
                globalConfiguration.getUseScientificFormat());
    }

    /**
     * Format number data.
     *
     * @param data
     * @param dataFormat          Not null.
     * @param dataFormatString
     * @param use1904windowing
     * @param locale
     * @param useScientificFormat
     * @return
     */
    public static String format(
            BigDecimal data,
            Short dataFormat,
            String dataFormatString,
            Boolean use1904windowing,
            Locale locale,
            Boolean useScientificFormat) {
        boolean resolvedUse1904windowing = Boolean.TRUE.equals(use1904windowing);
        Locale resolvedLocale = locale == null ? Locale.getDefault() : locale;
        boolean resolvedUseScientificFormat = Boolean.TRUE.equals(useScientificFormat);
        DataFormatterCache cache = DATA_FORMATTER_THREAD_LOCAL.get();
        if (cache == null
                || !cache.matches(resolvedUse1904windowing, resolvedLocale, resolvedUseScientificFormat)) {
            cache = new DataFormatterCache(
                    resolvedUse1904windowing, resolvedLocale, resolvedUseScientificFormat);
            DATA_FORMATTER_THREAD_LOCAL.set(cache);
        }
        return cache.dataFormatter.format(data, dataFormat, dataFormatString);
    }

    public static void removeThreadLocalCache() {
        DATA_FORMATTER_THREAD_LOCAL.remove();
    }
}
