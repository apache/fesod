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

import java.util.regex.Pattern;

/**
 * Utility class for parsing row and column positions from spreadsheet cell references.
 *
 * <p>Cell references (e.g. {@code "A1"}, {@code "$B$2"}) are converted into zero-based
 * row/column indexes used by the streaming read pipeline.
 *
 * @see <a href="https://learn.microsoft.com/en-us/office/troubleshoot/excel/identifier-and-reference">Excel identifiers and cell references</a>
 */
public class PositionUtils {

    private static final Pattern CELL_REF_PATTERN =
            Pattern.compile("(\\$?[A-Z]+)?" + "(\\$?[0-9]+)?", Pattern.CASE_INSENSITIVE);
    private static final char SHEET_NAME_DELIMITER = '!';
    private static final char REDUNDANT_CHARACTERS = '$';

    /**
     * Utility classes must never be instantiated.
     */
    private PositionUtils() {}

    /**
     * Resolves the zero-based row index from a row tag (the {@code r} attribute of an XLSX {@code <row>} element).
     *
     * <p>When {@code rowTag} is absent, the row is treated as following the previously read row, falling back
     * to the first row ({@code 0}) when there is no previous row.
     *
     * @param rowTag the 1-based row number declared in the file, may be {@code null}
     * @param before the zero-based index of the previously read row, may be {@code null}
     * @return the zero-based row index
     * @throws NumberFormatException if {@code rowTag} is not a valid integer
     */
    public static int getRowByRowTag(String rowTag, Integer before) {
        int row;
        if (rowTag != null) {
            row = Integer.parseInt(rowTag) - 1;
            return row;
        } else {
            if (before == null) {
                before = -1;
            }
            return before + 1;
        }
    }

    /**
     * Extracts the zero-based row index from a cell reference by parsing its trailing digits.
     *
     * <p>Leading column letters and absolute-reference markers are ignored, e.g. {@code "B10"} and
     * {@code "C$20"} both resolve to row {@code 9}.
     *
     * @param currentCellIndex the cell reference to parse, may be {@code null}
     * @return the zero-based row index, or {@code -1} if {@code currentCellIndex} is {@code null}
     * @throws NumberFormatException if the reference contains no row number (e.g. {@code "ABC"})
     */
    public static int getRow(String currentCellIndex) {
        if (currentCellIndex == null) {
            return -1;
        }
        int firstNumber = currentCellIndex.length() - 1;
        for (; firstNumber >= 0; firstNumber--) {
            char c = currentCellIndex.charAt(firstNumber);
            if (c < '0' || c > '9') {
                break;
            }
        }
        return Integer.parseUnsignedInt(currentCellIndex.substring(firstNumber + 1)) - 1;
    }

    /**
     * Extracts the zero-based column index from a cell reference by converting its leading letters
     * from base-26 notation (e.g. {@code "A"} to {@code 0}, {@code "Z"} to {@code 25}, {@code "AA"} to {@code 26}).
     *
     * <p>Lowercase letters are supported and an absolute-reference marker ({@code '$'}) is skipped. When
     * {@code currentCellIndex} is absent, the column is treated as following the previously read column,
     * falling back to the first column ({@code 0}) when there is no previous column.
     *
     * @param currentCellIndex the cell reference to parse, may be {@code null}
     * @param before the zero-based index of the previously read column, used only when
     *               {@code currentCellIndex} is {@code null}, may be {@code null}
     * @return the zero-based column index
     */
    public static int getCol(String currentCellIndex, Integer before) {
        if (currentCellIndex == null) {
            if (before == null) {
                before = -1;
            }
            return before + 1;
        }
        int firstNumber = currentCellIndex.charAt(0) == REDUNDANT_CHARACTERS ? 1 : 0;
        int col = 0;
        for (; firstNumber < currentCellIndex.length(); firstNumber++) {
            char c = currentCellIndex.charAt(firstNumber);
            boolean isNotLetter = c == REDUNDANT_CHARACTERS || (c >= '0' && c <= '9');
            if (isNotLetter) {
                break;
            }
            col = col * 26 + Character.toUpperCase(c) - 'A' + 1;
        }
        return col - 1;
    }
}
