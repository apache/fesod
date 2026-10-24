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

package org.apache.fesod.sheet.write.handler.impl;

import org.apache.fesod.sheet.enums.CellDataTypeEnum;
import org.apache.fesod.sheet.event.NotRepeatExecutor;
import org.apache.fesod.sheet.metadata.Head;
import org.apache.fesod.sheet.metadata.data.WriteCellData;
import org.apache.fesod.sheet.util.XlsxEscapeUtils;
import org.apache.fesod.sheet.write.handler.CellWriteHandler;
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
 * <p>
 * The read half of the same convention lives in
 * {@link org.apache.fesod.sheet.util.XlsxEscapeUtils#utfDecode(String) XlsxEscapeUtils.utfDecode}, which undoes what
 * this handler writes. Both sides read {@code _xHHHH_} the same way, so a change to what counts as an escape belongs
 * in both.
 * <p>
 * This handler implements {@link NotRepeatExecutor} so that it is only executed once. {@link #uniqueValue()} returns
 * the fully qualified class name, which never collides with handlers from other classes even across different class
 * loaders.
 *
 * @see org.apache.fesod.sheet.util.XlsxEscapeUtils#utfDecode(String)
 */
public class EscapeHexCellWriteHandler implements CellWriteHandler, NotRepeatExecutor {

    /**
     * Returns the fully qualified class name as the unique identity of this handler.
     * <p>
     * This handler only needs to execute once. The fully qualified class name serves as the unique identity,
     * which never collides with handlers from other classes even across different class loaders.
     */
    @Override
    public String uniqueValue() {
        return this.getClass().getName();
    }

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
                String escapedString = XlsxEscapeUtils.escape(originalString);
                cellData.setStringValue(escapedString);
            }
        }
    }
}
