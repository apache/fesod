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

package org.apache.fesod.excel.util;

import java.math.BigDecimal;
import java.util.Locale;
import org.apache.fesod.excel.metadata.GlobalConfiguration;
import org.apache.fesod.excel.metadata.format.DataFormatter;

/**
 * Convert number data, including date.
 *
 *
 **/
public class NumberDataFormatterUtils {

    /**
     * Cache DataFormatter.
     */
    private static final ThreadLocal<DataFormatter> DATA_FORMATTER_THREAD_LOCAL = new ThreadLocal<DataFormatter>();

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
        DataFormatter dataFormatter = DATA_FORMATTER_THREAD_LOCAL.get();
        if (dataFormatter == null) {
            dataFormatter = new DataFormatter(use1904windowing, locale, useScientificFormat);
            DATA_FORMATTER_THREAD_LOCAL.set(dataFormatter);
        }
        return dataFormatter.format(data, dataFormat, dataFormatString);
    }

    public static void removeThreadLocalCache() {
        DATA_FORMATTER_THREAD_LOCAL.remove();
    }
}
