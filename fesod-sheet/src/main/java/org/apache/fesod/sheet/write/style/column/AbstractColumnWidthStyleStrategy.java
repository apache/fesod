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

package org.apache.fesod.sheet.write.style.column;

import java.util.List;
import org.apache.fesod.sheet.metadata.Head;
import org.apache.fesod.sheet.metadata.data.WriteCellData;
import org.apache.fesod.sheet.write.handler.CellWriteHandler;
import org.apache.fesod.sheet.write.handler.context.CellWriteHandlerContext;
import org.apache.fesod.sheet.write.metadata.holder.WriteSheetHolder;
import org.apache.poi.ss.usermodel.Cell;

/**
 * Base class for strategies that set the column width in response to cells being written.
 *
 * <p>The framework invokes {@link #afterCellDispose(CellWriteHandlerContext)} once per cell. This
 * implementation forwards to {@link #setColumnWidth(CellWriteHandlerContext)}, whose default
 * implementation unpacks the context and calls the fine-grained
 * {@link #setColumnWidth(WriteSheetHolder, List, Cell, Head, Integer, Boolean)} overload.</p>
 *
 * <p>Subclasses typically override the fine-grained overload and adjust the width using the POI
 * {@link org.apache.poi.ss.usermodel.Sheet#setColumnWidth(int, int)} API on
 * {@code writeSheetHolder.getSheet()}. Implementations are responsible for their own caching /
 * idempotency, because the callback fires for every cell in the column.</p>
 */
public abstract class AbstractColumnWidthStyleStrategy implements CellWriteHandler {

    /**
     * {@inheritDoc}
     *
     * <p>This implementation delegates to {@link #setColumnWidth(CellWriteHandlerContext)} for
     * every cell that has just been disposed.</p>
     */
    @Override
    public void afterCellDispose(CellWriteHandlerContext context) {
        setColumnWidth(context);
    }

    /**
     * Entry point that resolves the column width from the current write context.
     *
     * <p>The default implementation unpacks the context and delegates to the fine-grained
     * {@link #setColumnWidth(WriteSheetHolder, List, Cell, Head, Integer, Boolean)} overload.
     * Override this variant when access to the whole {@link CellWriteHandlerContext} is required;
     * otherwise override the fine-grained variant.</p>
     *
     * @param context the context of the cell that has just been disposed; never {@code null}
     */
    protected void setColumnWidth(CellWriteHandlerContext context) {
        setColumnWidth(
                context.getWriteSheetHolder(),
                context.getCellDataList(),
                context.getCell(),
                context.getHeadData(),
                context.getRelativeRowIndex(),
                context.getHead());
    }

    /**
     * Extension point that actually applies the column width for the given cell. The default
     * implementation throws {@link UnsupportedOperationException}, so subclasses that want
     * automatic column sizing must override either this method or
     * {@link #setColumnWidth(CellWriteHandlerContext)}.
     *
     * @param writeSheetHolder the holder of the sheet being written; never {@code null}, use
     *                         {@code writeSheetHolder.getSheet()} to reach the POI sheet
     * @param cellDataList     the {@link WriteCellData} list produced for this cell; may be
     *                         {@code null} when writing a header and may contain several entries
     *                         when filling data
     * @param cell             the cell that has just been disposed; never {@code null}
     * @param head             the head metadata for the cell's column; may be {@code null} when
     *                         filling data or writing without a header
     * @param relativeRowIndex the row index relative to the current write batch; {@code null} when
     *                         filling data
     * @param isHead           whether the cell belongs to a header row; may be {@code null} when
     *                         filling data
     * @throws UnsupportedOperationException if the subclass has not overridden this method
     */
    protected void setColumnWidth(
            WriteSheetHolder writeSheetHolder,
            List<WriteCellData<?>> cellDataList,
            Cell cell,
            Head head,
            Integer relativeRowIndex,
            Boolean isHead) {
        throw new UnsupportedOperationException("Custom styles must override the setColumnWidth method.");
    }
}
