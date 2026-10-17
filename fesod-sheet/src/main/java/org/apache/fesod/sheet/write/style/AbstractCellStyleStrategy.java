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

package org.apache.fesod.sheet.write.style;

import org.apache.fesod.sheet.constant.OrderConstant;
import org.apache.fesod.sheet.metadata.Head;
import org.apache.fesod.sheet.write.handler.CellWriteHandler;
import org.apache.fesod.sheet.write.handler.context.CellWriteHandlerContext;
import org.apache.poi.ss.usermodel.Cell;

/**
 * Base class for cell-style strategies that decide which style a header or content cell should be
 * written with.
 *
 * <p>The framework invokes {@link #afterCellDispose(CellWriteHandlerContext)} once per cell. This
 * implementation dispatches to {@link #setHeadCellStyle(CellWriteHandlerContext)} for header cells
 * and to {@link #setContentCellStyle(CellWriteHandlerContext)} for content cells; subclasses
 * override whichever side they care about, typically the fine-grained
 * {@link #setHeadCellStyle(Cell, Head, Integer)} / {@link #setContentCellStyle(Cell, Head, Integer)}
 * variants.</p>
 *
 * <p>This handler runs at {@link OrderConstant#DEFINE_STYLE} so that the cell value is already
 * written by the time the style is attached.</p>
 */
public abstract class AbstractCellStyleStrategy implements CellWriteHandler {

    /**
     * {@inheritDoc}
     *
     * <p>Returns {@link OrderConstant#DEFINE_STYLE} so that style strategies run in the
     * "define style" phase of the write pipeline.</p>
     */
    @Override
    public int order() {
        return OrderConstant.DEFINE_STYLE;
    }

    /**
     * {@inheritDoc}
     *
     * <p>This implementation skips cells whose {@code isHead} flag is {@code null} (for example
     * when filling data) and otherwise dispatches to
     * {@link #setHeadCellStyle(CellWriteHandlerContext)} or
     * {@link #setContentCellStyle(CellWriteHandlerContext)}.</p>
     */
    @Override
    public void afterCellDispose(CellWriteHandlerContext context) {
        if (context.getHead() == null) {
            return;
        }
        if (context.getHead()) {
            setHeadCellStyle(context);
        } else {
            setContentCellStyle(context);
        }
    }

    /**
     * Applies the header style to the current cell by delegating to
     * {@link #setHeadCellStyle(Cell, Head, Integer)}.
     *
     * <p>Override this variant when extra information from the {@link CellWriteHandlerContext}
     * (sheet holder, workbook, converted cell data, …) is needed; otherwise override the
     * fine-grained variant.</p>
     *
     * @param context the context that carries the header cell being styled together with its head
     *                metadata and relative row index; never {@code null}
     */
    protected void setHeadCellStyle(CellWriteHandlerContext context) {
        setHeadCellStyle(context.getCell(), context.getHeadData(), context.getRelativeRowIndex());
    }

    /**
     * Extension point that applies the header style to a single cell. The default implementation
     * throws {@link UnsupportedOperationException}, so any subclass that wants header styling must
     * override it (or override the {@link CellWriteHandlerContext} variant instead).
     *
     * @param cell             the header cell to style; never {@code null}
     * @param head             the head metadata for the cell's column; may be {@code null} when
     *                         writing without a header
     * @param relativeRowIndex the row index relative to the current write batch; {@code null} when
     *                         filling data
     * @throws UnsupportedOperationException if the subclass has not overridden this method
     */
    protected void setHeadCellStyle(Cell cell, Head head, Integer relativeRowIndex) {
        throw new UnsupportedOperationException("Custom styles must override the setHeadCellStyle method.");
    }

    /**
     * Applies the content style to the current cell by delegating to
     * {@link #setContentCellStyle(Cell, Head, Integer)}.
     *
     * <p>Override this variant when extra information from the {@link CellWriteHandlerContext} is
     * needed; otherwise override the fine-grained variant.</p>
     *
     * @param context the context that carries the content cell being styled together with its head
     *                metadata and relative row index; never {@code null}
     */
    protected void setContentCellStyle(CellWriteHandlerContext context) {
        setContentCellStyle(context.getCell(), context.getHeadData(), context.getRelativeRowIndex());
    }

    /**
     * Extension point that applies the content style to a single cell. The default implementation
     * throws {@link UnsupportedOperationException}, so any subclass that wants content styling must
     * override it (or override the {@link CellWriteHandlerContext} variant instead).
     *
     * @param cell             the content cell to style; never {@code null}
     * @param head             the head metadata for the cell's column; may be {@code null} when
     *                         writing without a header
     * @param relativeRowIndex the row index relative to the current write batch; {@code null} when
     *                         filling data
     * @throws UnsupportedOperationException if the subclass has not overridden this method
     */
    protected void setContentCellStyle(Cell cell, Head head, Integer relativeRowIndex) {
        throw new UnsupportedOperationException("Custom styles must override the setContentCellStyle method.");
    }
}
