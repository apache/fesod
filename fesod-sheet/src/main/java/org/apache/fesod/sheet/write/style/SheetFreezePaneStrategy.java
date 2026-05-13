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

package org.apache.fesod.sheet.write.style;

import org.apache.fesod.common.util.ValidateUtils;
import org.apache.fesod.sheet.metadata.property.SheetFreezePaneProperty;
import org.apache.fesod.sheet.write.handler.SheetWriteHandler;
import org.apache.fesod.sheet.write.metadata.holder.WriteSheetHolder;
import org.apache.fesod.sheet.write.metadata.holder.WriteWorkbookHolder;

/**
 * A strategy for creating a freeze pane. Any existing freeze pane or split pane is overwritten.
 */
public class SheetFreezePaneStrategy implements SheetWriteHandler {

    /**
     * Horizontal position of split.
     */
    private final int colSplit;

    /**
     * Vertical position of split.
     */
    private final int rowSplit;

    /**
     * Left column visible in right pane.
     */
    private final int leftmostColumn;

    /**
     * Top row visible in bottom pane
     */
    private final int topRow;

    public SheetFreezePaneStrategy(SheetFreezePaneProperty property) {
        this(property.getColSplit(), property.getRowSplit(), property.getLeftmostColumn(), property.getTopRow());
    }

    public SheetFreezePaneStrategy(int colSplit, int rowSplit) {
        this(colSplit, rowSplit, colSplit, rowSplit);
    }

    public SheetFreezePaneStrategy(int colSplit, int rowSplit, int leftmostColumn, int topRow) {
        ValidateUtils.isTrue(colSplit >= 0, "colSplit must be >= 0");
        ValidateUtils.isTrue(rowSplit >= 0, "rowSplit must be >= 0");
        ValidateUtils.isTrue(leftmostColumn >= 0, "leftmostColumn must be >= 0");
        ValidateUtils.isTrue(topRow >= 0, "topRow must be >= 0");

        this.colSplit = colSplit;
        this.rowSplit = rowSplit;
        this.leftmostColumn = leftmostColumn;
        this.topRow = topRow;
    }

    @Override
    public void afterSheetCreate(WriteWorkbookHolder writeWorkbookHolder, WriteSheetHolder writeSheetHolder) {
        writeSheetHolder.getSheet().createFreezePane(colSplit, rowSplit, leftmostColumn, topRow);
    }
}
