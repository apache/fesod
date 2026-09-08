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

import java.util.Map;
import java.util.regex.Pattern;
import org.apache.fesod.common.util.MapUtils;
import org.apache.fesod.common.util.StringUtils;

/**
 * Internal helper used by {@link DateUtils} for detecting date-related spreadsheet format strings.
 * <p>
 * Not intended for direct use; use {@link DateUtils} as the primary entry point instead.
 * </p>
 */
class ExcelDateFormatDetector {

    /**
     * Is a cache of dates
     */
    private static final ThreadLocal<Map<Short, Boolean>> DATE_THREAD_LOCAL = new ThreadLocal<>();

    /**
     * The following patterns are used in {@link #isADateFormat(Short, String)}
     */
    private static final Pattern date_ptrn1 = Pattern.compile("^\\[\\$\\-.*?\\]");

    private static final Pattern date_ptrn2 = Pattern.compile("^\\[[a-zA-Z]+\\]");
    private static final Pattern date_ptrn3a = Pattern.compile("[yYmMdDhHsS]");
    // add "\u5e74 \u6708 \u65e5" for Chinese/Japanese date format:2017 \u5e74 2 \u6708 7 \u65e5
    private static final Pattern date_ptrn3b =
            Pattern.compile("^[\\[\\]yYmMdDhHsS\\-T/\u5e74\u6708\u65e5,. :\"\\\\]+0*[ampAMP/]*$");
    // elapsed time patterns: [h],[m] and [s]
    private static final Pattern date_ptrn4 = Pattern.compile("^\\[([hH]+|[mM]+|[sS]+)\\]");
    // for format which start with "[DBNum1]" or "[DBNum2]" or "[DBNum3]" could be a Chinese date
    private static final Pattern date_ptrn5 = Pattern.compile("^\\[DBNum(1|2|3)\\]");
    // for format which start with "年" or "月" or "日" or "时" or "分" or "秒" could be a Chinese date
    private static final Pattern date_ptrn6 = Pattern.compile("(年|月|日|时|分|秒)+");

    private ExcelDateFormatDetector() {}

    /**
     * Determine if it is a date format.
     *
     * @param formatIndex
     * @param formatString
     * @return
     */
    static boolean isADateFormat(Short formatIndex, String formatString) {
        if (formatIndex == null) {
            return false;
        }
        Map<Short, Boolean> isDateCache = DATE_THREAD_LOCAL.get();
        if (isDateCache == null) {
            isDateCache = MapUtils.newHashMap();
            DATE_THREAD_LOCAL.set(isDateCache);
        } else {
            Boolean isDatecachedDataList = isDateCache.get(formatIndex);
            if (isDatecachedDataList != null) {
                return isDatecachedDataList;
            }
        }
        boolean isDate = isADateFormatUncached(formatIndex, formatString);
        isDateCache.put(formatIndex, isDate);
        return isDate;
    }

    /**
     * Determine if it is a date format.
     *
     * @param formatIndex
     * @param formatString
     * @return
     */
    static boolean isADateFormatUncached(Short formatIndex, String formatString) {
        // First up, is this an internal date format?
        if (isInternalDateFormat(formatIndex)) {
            return true;
        }
        if (StringUtils.isEmpty(formatString)) {
            return false;
        }

        String fs = sanitizeString(formatString);

        // short-circuit if it indicates elapsed time: [h], [m] or [s]
        if (date_ptrn4.matcher(fs).matches()) {
            return true;
        }
        // If it starts with [DBNum1] or [DBNum2] or [DBNum3]
        // then it could be a Chinese date
        fs = date_ptrn5.matcher(fs).replaceAll("");
        // If it starts with [$-...], then could be a date, but
        // who knows what that starting bit is all about
        fs = date_ptrn1.matcher(fs).replaceAll("");
        // If it starts with something like [Black] or [Yellow],
        // then it could be a date
        fs = date_ptrn2.matcher(fs).replaceAll("");
        // You're allowed something like dd/mm/yy;[red]dd/mm/yy
        // which would place dates before 1900/1904 in red
        // For now, only consider the first one
        final int separatorIndex = fs.indexOf(';');
        if (0 < separatorIndex && separatorIndex < fs.length() - 1) {
            fs = fs.substring(0, separatorIndex);
        }

        // Ensure it has some date letters in it
        // (Avoids false positives on the rest of pattern 3)
        if (!date_ptrn3a.matcher(fs).find()) {
            return false;
        }

        // If we get here, check it's only made up, in any case, of:
        // y m d h s - \ / , . : [ ] T
        // optionally followed by AM/PM
        boolean result = date_ptrn3b.matcher(fs).matches();
        if (result) {
            return true;
        }
        result = date_ptrn6.matcher(fs).find();
        return result;
    }

    private static String sanitizeString(String fs) {
        final int length = fs.length();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            char c = fs.charAt(i);
            if (i < length - 1) {
                char nc = fs.charAt(i + 1);
                if (c == '\\' && isEscapableChar(nc)) {
                    continue;
                } else if (c == ';' && nc == '@') {
                    i++;
                    // skip ";@" duplets
                    continue;
                }
            }
            sb.append(c);
        }
        return sb.toString();
    }

    private static boolean isEscapableChar(char c) {
        return c == '-' || c == ',' || c == '.' || c == ' ' || c == '\\';
    }

    /**
     * Given a format ID this will check whether the format represents an internal excel date format or not.
     *
     * @see #isADateFormat(Short, String)
     */
    static boolean isInternalDateFormat(short format) {
        switch (format) {
            // Internal Date Formats as described on page 427 in
            // Microsoft Excel Dev's Kit...
            // 14-22
            case 0x0e:
            case 0x0f:
            case 0x10:
            case 0x11:
            case 0x12:
            case 0x13:
            case 0x14:
            case 0x15:
            case 0x16:
            // 45-47
            case 0x2d:
            case 0x2e:
            case 0x2f:
                return true;
        }
        return false;
    }

    static void removeThreadLocalCache() {
        DATE_THREAD_LOCAL.remove();
    }
}
