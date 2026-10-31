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

package org.apache.fesod.sheet.write.handler.context;

import org.apache.fesod.sheet.context.WriteContext;
import org.apache.fesod.sheet.metadata.Head;
import org.apache.fesod.sheet.metadata.property.ExcelContentProperty;
import org.apache.fesod.sheet.util.WriteHandlerUtils;
import org.apache.poi.ss.usermodel.Row;

/**
 * Recycles the {@link CellWriteHandlerContext} of one write session (Tomcat-style
 * request recycling): the session owns a single instance that is reset at every cell
 * boundary instead of allocating a fresh context per cell.
 *
 * <p>The context is handed to {@code CellWriteHandler} callbacks, so recycling relies on
 * the documented contract that a context is only valid for the duration of one cell's
 * handler chain and must not be retained afterwards.
 *
 * <p>A recycler belongs to one write session and is used from the single thread that
 * drives the writer; no synchronization. While the recycled instance is still in flight
 * (a nested write on the same writer inside a cell callback), {@link #renew} temporarily
 * falls back to allocating fresh contexts rather than resetting the live one.
 */
public final class CellWriteHandlerContextRecycler {

    /**
     * The one recycled instance of this write session; {@code null} after {@link #release()}.
     */
    private CellWriteHandlerContext cached;

    /**
     * The instance currently handed out for a cell whose handler chain has not completed.
     */
    private CellWriteHandlerContext inFlight;

    /**
     * Returns the context for the next cell: the recycled instance reset to the given
     * per-cell arguments, or a fresh one while the recycled instance is in flight.
     *
     * @param writeContext         write context
     * @param row                  row
     * @param rowIndex             row index
     * @param head                 head data of the cell, nullable
     * @param columnIndex          column index
     * @param relativeRowIndex     relative row index, nullable
     * @param isHead               whether the cell is a header cell
     * @param excelContentProperty field annotation configuration, nullable
     */
    public CellWriteHandlerContext renew(
            WriteContext writeContext,
            Row row,
            Integer rowIndex,
            Head head,
            Integer columnIndex,
            Integer relativeRowIndex,
            Boolean isHead,
            ExcelContentProperty excelContentProperty) {
        if (cached == null) {
            cached = WriteHandlerUtils.createCellWriteHandlerContext(
                    writeContext, row, rowIndex, head, columnIndex, relativeRowIndex, isHead, excelContentProperty);
        } else if (inFlight == cached) {
            // A nested write is running on the same writer while a cell is still in
            // flight: never reset the live outer context, fall back to allocation.
            return WriteHandlerUtils.createCellWriteHandlerContext(
                    writeContext, row, rowIndex, head, columnIndex, relativeRowIndex, isHead, excelContentProperty);
        } else {
            cached.reset(
                    writeContext, row, rowIndex, head, columnIndex, relativeRowIndex, isHead, excelContentProperty);
        }
        inFlight = cached;
        return cached;
    }

    /**
     * Marks the handler chain of the cell that used {@code context} complete. A context
     * handed to a nested write is ignored — only the outer cell's completion frees the
     * recycled instance for the next cell.
     */
    public void cellCompleted(CellWriteHandlerContext context) {
        if (context == inFlight) {
            inFlight = null;
        }
    }

    /**
     * Drops the recycled instance so no {@code Row}/{@code Cell} references survive a
     * finished write session, and detaches it from a session that ended with an exception
     * (an {@code ExcelWriteDataConvertException} retains the context it was built with).
     */
    public void release() {
        cached = null;
        inFlight = null;
    }
}
