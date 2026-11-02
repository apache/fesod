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

package org.apache.fesod.common.util;

public class BooleanUtils {

    /**
     * The text Excel writes into the value element of a boolean cell that holds {@code true}, which is
     * the only input treated as {@code true} by {@link #valueOf(String)}.
     */
    private static final String TRUE_NUMBER = "1";

    /**
     * Utility classes must never be instantiated.
     */
    private BooleanUtils() {}

    /**
     * <p>Checks whether the text of an Excel boolean cell carries the {@code true} marker, returning a
     * {@code Boolean} instead of a primitive.</p>
     *
     * <p>Only the exact string {@code "1"} is {@code true}; every other input — including {@code "0"},
     * {@code "true"} and {@code null} — is {@code false}. Unlike {@link Boolean#parseBoolean(String)}
     * the comparison is made against the constant, so a {@code null} argument is accepted silently and
     * no exception is ever thrown.</p>
     *
     * <pre>
     *   BooleanUtils.valueOf("1")    = Boolean.TRUE
     *   BooleanUtils.valueOf("0")    = Boolean.FALSE
     *   BooleanUtils.valueOf("true") = Boolean.FALSE
     *   BooleanUtils.valueOf("")     = Boolean.FALSE
     *   BooleanUtils.valueOf(null)   = Boolean.FALSE
     * </pre>
     *
     * @param str the cell text to inspect, may be {@code null}
     * @return {@link Boolean#TRUE} if {@code str} is the {@code true} marker {@code "1"},
     * {@link Boolean#FALSE} otherwise (never {@code null})
     * @deprecated this only targets Excel's {@code 0}/{@code 1} boolean cell markers and is not a
     *     general-purpose string-to-boolean conversion; it should not be used outside that scenario and
     *     will be removed in a future release. Use
     *     {@code org.apache.fesod.sheet.metadata.data.ReadCellData#setBooleanValueFromString(String)}
     *     instead.
     * @see Boolean#parseBoolean(String)
     */
    @Deprecated
    public static Boolean valueOf(String str) {
        if (TRUE_NUMBER.equals(str)) {
            return Boolean.TRUE;
        } else {
            return Boolean.FALSE;
        }
    }

    // boolean Boolean methods
    // -----------------------------------------------------------------------

    /**
     * <p>Checks if a {@code Boolean} value is {@code true},
     * handling {@code null} by returning {@code false}.</p>
     *
     * <pre>
     *   BooleanUtils.isTrue(Boolean.TRUE)  = true
     *   BooleanUtils.isTrue(Boolean.FALSE) = false
     *   BooleanUtils.isTrue(null)          = false
     * </pre>
     *
     * @param bool the boolean to check, null returns {@code false}
     * @return {@code true} only if the input is non-null and true
     * @see #isNotTrue(Boolean)
     * @see #isFalse(Boolean)
     */
    public static boolean isTrue(final Boolean bool) {
        return Boolean.TRUE.equals(bool);
    }

    /**
     * <p>Checks if a {@code Boolean} value is <i>not</i> {@code true},
     * handling {@code null} by returning {@code true}.</p>
     *
     * <pre>
     *   BooleanUtils.isNotTrue(Boolean.TRUE)  = false
     *   BooleanUtils.isNotTrue(Boolean.FALSE) = true
     *   BooleanUtils.isNotTrue(null)          = true
     * </pre>
     *
     * @param bool the boolean to check, null returns {@code true}
     * @return {@code true} if the input is null or false
     * @see #isTrue(Boolean)
     */
    public static boolean isNotTrue(final Boolean bool) {
        return !isTrue(bool);
    }

    /**
     * <p>Checks if a {@code Boolean} value is {@code false},
     * handling {@code null} by returning {@code false}.</p>
     *
     * <pre>
     *   BooleanUtils.isFalse(Boolean.TRUE)  = false
     *   BooleanUtils.isFalse(Boolean.FALSE) = true
     *   BooleanUtils.isFalse(null)          = false
     * </pre>
     *
     * @param bool the boolean to check, null returns {@code false}
     * @return {@code true} only if the input is non-null and false
     * @see #isNotFalse(Boolean)
     * @see #isTrue(Boolean)
     */
    public static boolean isFalse(final Boolean bool) {
        return Boolean.FALSE.equals(bool);
    }

    /**
     * <p>Checks if a {@code Boolean} value is <i>not</i> {@code false},
     * handling {@code null} by returning {@code true}.</p>
     *
     * <pre>
     *   BooleanUtils.isNotFalse(Boolean.TRUE)  = true
     *   BooleanUtils.isNotFalse(Boolean.FALSE) = false
     *   BooleanUtils.isNotFalse(null)          = true
     * </pre>
     *
     * @param bool the boolean to check, null returns {@code true}
     * @return {@code true} if the input is null or true
     * @see #isFalse(Boolean)
     */
    public static boolean isNotFalse(final Boolean bool) {
        return !isFalse(bool);
    }
}
