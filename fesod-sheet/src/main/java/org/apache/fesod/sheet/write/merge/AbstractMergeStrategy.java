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

package org.apache.fesod.sheet.write.merge;

import org.apache.fesod.sheet.metadata.Head;
import org.apache.fesod.sheet.write.handler.CellWriteHandler;
import org.apache.fesod.sheet.write.handler.context.CellWriteHandlerContext;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;

/**
 * Base class for cell-merge strategies driven by {@link CellWriteHandler}.
 *
 * <p>Subclasses decide, for each freshly written cell, which surrounding cells should be merged
 * into it. The framework invokes {@link #merge(Sheet, Cell, Head, Integer)} after every non-header
 * cell has been fully disposed, so subclasses only need to implement the merge logic itself.</p>
 */
public abstract class AbstractMergeStrategy implements CellWriteHandler {

    /**
     * {@inheritDoc}
     *
     * <p>This implementation skips header cells and delegates the actual merge to
     * {@link #merge(Sheet, Cell, Head, Integer)} for the current data cell.</p>
     */
    @Override
    public void afterCellDispose(CellWriteHandlerContext context) {
        if (context.getHead()) {
            return;
        }
        merge(
                context.getWriteSheetHolder().getSheet(),
                context.getCell(),
                context.getHeadData(),
                context.getRelativeRowIndex());
    }

    /**
     * Performs the merge for a single data cell that has just been written.
     * Called by {@link #afterCellDispose(CellWriteHandlerContext)} once for every non-header cell.
     *
     * @param sheet            the sheet that owns the cell; never {@code null}
     * @param cell             the cell that has just been fully written and may act as the anchor
     *                         of a merged region; never {@code null}
     * @param head             the head metadata associated with the cell's column; may be
     *                         {@code null} when writing without a header or when filling data
     * @param relativeRowIndex the row index relative to the current write batch; {@code null} when
     *                         filling data
     */
    protected abstract void merge(Sheet sheet, Cell cell, Head head, Integer relativeRowIndex);
}
