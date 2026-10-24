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

package org.apache.fesod.sheet.analysis.v07.handlers;

import java.math.BigDecimal;

/**
 * Parses the current cell text without materializing an intermediate {@code String} per cell. The text
 * arrives in the sheet holder's {@link StringBuilder}; the digits are copied into a reusable buffer
 * (grown on demand) and parsed in place.
 *
 * <p>Both parse methods are exactly equivalent to parsing {@code text.toString()} with the
 * corresponding JDK API: values, accepted syntax and thrown exceptions (including
 * {@code NumberFormatException} on malformed content) are identical. The plain-digit fast path simply
 * skips the String allocation; anything unusual falls back to the JDK parser.</p>
 *
 * <p>An analysis run drives its handlers from a single thread, so one instance per thread (shared by
 * every sheet parsed on that thread, sequentially) covers all uses without synchronization.</p>
 */
final class CellTextParser {

    private static final ThreadLocal<CellTextParser> CURRENT = ThreadLocal.withInitial(CellTextParser::new);

    /**
     * Returns the parser instance of the current analysis thread.
     */
    static CellTextParser current() {
        return CURRENT.get();
    }

    private char[] buffer = new char[64];

    /**
     * Equivalent to {@code Integer.parseInt(text.toString())}.
     */
    int parseInt(StringBuilder text) {
        int length = text.length();
        if (length == 0 || length > 10) {
            return Integer.parseInt(text.toString());
        }
        copy(text, length);
        int value = 0;
        for (int i = 0; i < length; i++) {
            char c = buffer[i];
            if (c < '0' || c > '9' || value > (Integer.MAX_VALUE - (c - '0')) / 10) {
                return Integer.parseInt(text.toString());
            }
            value = value * 10 + (c - '0');
        }
        return value;
    }

    /**
     * Equivalent to {@code new BigDecimal(text.toString())}.
     */
    BigDecimal parseBigDecimal(StringBuilder text) {
        int length = text.length();
        copy(text, length);
        return new BigDecimal(buffer, 0, length);
    }

    private void copy(StringBuilder text, int length) {
        if (buffer.length < length) {
            buffer = new char[Math.max(length, buffer.length * 2)];
        }
        text.getChars(0, length, buffer, 0);
    }
}
