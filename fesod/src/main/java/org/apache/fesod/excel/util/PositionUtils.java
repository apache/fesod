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

import java.util.regex.Pattern;

/**
 *
 */
public class PositionUtils {

    private static final Pattern CELL_REF_PATTERN =
            Pattern.compile("(\\$?[A-Z]+)?" + "(\\$?[0-9]+)?", Pattern.CASE_INSENSITIVE);
    private static final char SHEET_NAME_DELIMITER = '!';
    private static final char REDUNDANT_CHARACTERS = '$';

    private PositionUtils() {}

    public static int getRowByRowTagt(String rowTagt, Integer before) {
        int row;
        if (rowTagt != null) {
            row = Integer.parseInt(rowTagt) - 1;
            return row;
        } else {
            if (before == null) {
                before = -1;
            }
            return before + 1;
        }
    }

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
