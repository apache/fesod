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

    // Static hex lookup table for O(1) character validation
    private static final boolean[] HEX_TABLE = new boolean[128];

    static {
        for (char c = '0'; c <= '9'; c++) HEX_TABLE[c] = true;
        for (char c = 'A'; c <= 'F'; c++) HEX_TABLE[c] = true;
        for (char c = 'a'; c <= 'f'; c++) HEX_TABLE[c] = true;
    }

    private static final String PREFIX = "_x";
    private static final int PREFIX_LENGTH = PREFIX.length();
    private static final int HEX_DIGIT_COUNT = 4;
    // "_x" + 4 hex digits + closing "_"
    private static final int PATTERN_LENGTH = PREFIX_LENGTH + HEX_DIGIT_COUNT + 1;

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
     * Escapes hexadecimal-encoded strings with optimized performance Replaces _xHHHH_ with _x005F_xHHHH_ to prevent POI
     * from decoding them
     */
    private String escapeHex(String originalString) {
        int length = originalString.length();

        // Fast path: if string is too short to contain pattern, return original
        if (length < PATTERN_LENGTH) {
            return originalString;
        }

        // Lazily allocated: stays null (no allocation) when no valid pattern is found
        StringBuilder result = null;
        int lastEnd = 0;
        int searchStart = 0;
        int patternIndex;
        while ((patternIndex = originalString.indexOf(PREFIX, searchStart)) != -1) {
            // Check if we have enough characters for full pattern
            if (patternIndex + PATTERN_LENGTH - 1 >= length) {
                break;
            }

            // Quick validation: check if it ends with '_' and has valid hex
            if (originalString.charAt(patternIndex + PATTERN_LENGTH - 1) == '_'
                    && isValidHexFast(originalString, patternIndex + PREFIX_LENGTH)) {
                if (result == null) {
                    result = new StringBuilder(length + 64); // More generous pre-allocation
                }
                // Append content since the previous match, then the escaped pattern
                result.append(originalString, lastEnd, patternIndex);
                result.append("_x005F_x");
                result.append(originalString, patternIndex + PREFIX_LENGTH, patternIndex + PATTERN_LENGTH - 1);
                result.append('_');
                lastEnd = patternIndex + PATTERN_LENGTH;
                searchStart = patternIndex + PATTERN_LENGTH;
            } else {
                searchStart = patternIndex + PREFIX_LENGTH;
            }
        }

        // No valid patterns found
        if (result == null) {
            return originalString;
        }

        // Append remaining content
        if (lastEnd < length) {
            result.append(originalString, lastEnd, length);
        }

        return result.toString();
    }

    /**
     * Fast hex validation using lookup table - O(1) per character
     */
    private static boolean isValidHexFast(String str, int startIndex) {
        for (int i = 0; i < HEX_DIGIT_COUNT; i++) {
            char c = str.charAt(startIndex + i);
            if (c >= 128 || !HEX_TABLE[c]) {
                return false;
            }
        }
        return true;
    }
}
