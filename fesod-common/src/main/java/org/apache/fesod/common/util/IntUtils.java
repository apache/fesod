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
 * Portions of this file are derived from Guava's Ints.
 *
 * Copyright (C) 2008 The Guava Authors
 */

package org.apache.fesod.common.util;

/**
 * Utilities for working with {@code int} values.
 *
 * <p>All methods in this class are static and the class cannot be instantiated.
 */
public class IntUtils {

    /**
     * Utility classes must never be instantiated.
     */
    private IntUtils() {}

    /**
     * The largest power of two that can be represented as an {@code int}, namely
     * {@code 2^30 = 1073741824}.
     */
    public static final int MAX_POWER_OF_TWO = 1 << (Integer.SIZE - 2);

    /**
     * <p>Returns the {@code int} value nearest in value to {@code value}, clamping the result
     * to the range of the {@code int} type ("saturating" the conversion) instead of silently
     * truncating high-order bits.</p>
     *
     * <pre>
     *   IntUtils.saturatedCast(0L)                     = 0
     *   IntUtils.saturatedCast(Integer.MAX_VALUE + 1L) = Integer.MAX_VALUE
     *   IntUtils.saturatedCast(Integer.MIN_VALUE - 1L) = Integer.MIN_VALUE
     * </pre>
     *
     * @param value any {@code long} value to convert
     * @return the same value cast to {@code int} if it is in the range of the {@code int} type,
     * {@link Integer#MAX_VALUE} if it is too large, or {@link Integer#MIN_VALUE} if it is too
     * small
     * @see Math#toIntExact(long)
     */
    public static int saturatedCast(long value) {
        if (value > Integer.MAX_VALUE) {
            return Integer.MAX_VALUE;
        }
        if (value < Integer.MIN_VALUE) {
            return Integer.MIN_VALUE;
        }
        return (int) value;
    }
}
