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

package org.apache.fesod.sheet.write.handler;

import org.apache.fesod.sheet.enums.CellDataTypeEnum;
import org.apache.fesod.sheet.metadata.Head;
import org.apache.fesod.sheet.metadata.data.WriteCellData;
import org.apache.fesod.sheet.write.metadata.holder.WriteSheetHolder;
import org.apache.fesod.sheet.write.metadata.holder.WriteTableHolder;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.streaming.SXSSFCell;

/**
 * A cell write handler that escapes _x[0-9A-Fa-f]{4}_ format strings to prevent POI from automatically decoding them.
 * <p>
 * In Office Open XML, _xHHHH_ format is used to encode special characters. For example, _x000D_ represents the Unicode
 * character 0D (carriage return).
 * <p>
 * To store the literal _xHHHH_ sequence without it being decoded by POI, we need to escape the initial underscore by
 * replacing _x with _x005F_x.
 */
public class EscapeHexCellWriteHandler implements CellWriteHandler {

    // ASCII hex digits only. Not Character.digit(c, 16), which also accepts non-ASCII
    // digits such as U+0663 that OOXML never uses.
    private static final boolean[] HEX_TABLE = new boolean[128];

    static {
        for (char c = '0'; c <= '9'; c++) HEX_TABLE[c] = true;
        for (char c = 'A'; c <= 'F'; c++) HEX_TABLE[c] = true;
        for (char c = 'a'; c <= 'f'; c++) HEX_TABLE[c] = true;
    }

    private static final String PREFIX = "_x";
    private static final int PREFIX_LENGTH = PREFIX.length();
    private static final int HEX_DIGIT_COUNT = 4;

    @Override
    public void afterCellDataConverted(
            WriteSheetHolder writeSheetHolder,
            WriteTableHolder writeTableHolder,
            WriteCellData<?> cellData,
            Cell cell,
            Head head,
            Integer relativeRowIndex,
            Boolean isHead) {
        // Only process SXSSFCell (cell in xlsx) and cell data of string type
        if (cellData != null && cell instanceof SXSSFCell && CellDataTypeEnum.STRING.equals(cellData.getType())) {
            String originalString = cellData.getStringValue();
            if (originalString != null) {
                String escapedString = escapeHex(originalString);
                cellData.setStringValue(escapedString);
            }
        }
    }

    /**
     * Replaces every _xHHHH_ sequence with _x005F_xHHHH_ to prevent POI from decoding them.
     */
    private String escapeHex(String originalString) {
        int length = originalString.length();

        // Lazily allocated: stays null (no allocation) when no valid pattern is found
        StringBuilder result = null;
        int lastEnd = 0;
        int searchStart = 0;
        int patternIndex;
        while ((patternIndex = originalString.indexOf(PREFIX, searchStart)) != -1) {
            int hexStart = patternIndex + PREFIX_LENGTH;
            int suffixIndex = hexStart + HEX_DIGIT_COUNT;
            int patternEnd = suffixIndex + 1;
            // Too few characters left for a full pattern, and any later match has even fewer
            if (patternEnd > length) {
                break;
            }

            if (originalString.charAt(suffixIndex) == '_' && isHexDigits(originalString, hexStart)) {
                if (result == null) {
                    result = new StringBuilder(length + 64);
                }
                // Append content since the previous match, then the escaped pattern
                result.append(originalString, lastEnd, patternIndex);
                result.append("_x005F_x");
                result.append(originalString, hexStart, suffixIndex);
                result.append('_');
                lastEnd = patternEnd;
                searchStart = patternEnd;
            } else {
                searchStart = hexStart;
            }
        }

        // No valid patterns found
        if (result == null) {
            return originalString;
        }

        result.append(originalString, lastEnd, length);
        return result.toString();
    }

    /**
     * Checks whether the four characters starting at {@code startIndex} are all ASCII hex digits.
     */
    private static boolean isHexDigits(String str, int startIndex) {
        for (int i = 0; i < HEX_DIGIT_COUNT; i++) {
            char c = str.charAt(startIndex + i);
            if (c >= 128 || !HEX_TABLE[c]) {
                return false;
            }
        }
        return true;
    }
}
