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

package org.apache.fesod.excel.write.merge;

import org.apache.fesod.excel.metadata.property.OnceAbsoluteMergeProperty;
import org.apache.fesod.excel.write.handler.SheetWriteHandler;
import org.apache.fesod.excel.write.metadata.holder.WriteSheetHolder;
import org.apache.fesod.excel.write.metadata.holder.WriteWorkbookHolder;
import org.apache.poi.ss.util.CellRangeAddress;

/**
 * It only merges once when create cell(firstRowIndex,lastRowIndex)
 *
 *
 */
public class OnceAbsoluteMergeStrategy implements SheetWriteHandler {
    /**
     * First row
     */
    private final int firstRowIndex;
    /**
     * Last row
     */
    private final int lastRowIndex;
    /**
     * First column
     */
    private final int firstColumnIndex;
    /**
     * Last row
     */
    private final int lastColumnIndex;

    public OnceAbsoluteMergeStrategy(int firstRowIndex, int lastRowIndex, int firstColumnIndex, int lastColumnIndex) {
        if (firstRowIndex < 0 || lastRowIndex < 0 || firstColumnIndex < 0 || lastColumnIndex < 0) {
            throw new IllegalArgumentException("All parameters must be greater than 0");
        }
        this.firstRowIndex = firstRowIndex;
        this.lastRowIndex = lastRowIndex;
        this.firstColumnIndex = firstColumnIndex;
        this.lastColumnIndex = lastColumnIndex;
    }

    public OnceAbsoluteMergeStrategy(OnceAbsoluteMergeProperty onceAbsoluteMergeProperty) {
        this(
                onceAbsoluteMergeProperty.getFirstRowIndex(),
                onceAbsoluteMergeProperty.getLastRowIndex(),
                onceAbsoluteMergeProperty.getFirstColumnIndex(),
                onceAbsoluteMergeProperty.getLastColumnIndex());
    }

    @Override
    public void afterSheetCreate(WriteWorkbookHolder writeWorkbookHolder, WriteSheetHolder writeSheetHolder) {
        CellRangeAddress cellRangeAddress =
                new CellRangeAddress(firstRowIndex, lastRowIndex, firstColumnIndex, lastColumnIndex);
        writeSheetHolder.getSheet().addMergedRegionUnsafe(cellRangeAddress);
    }
}
