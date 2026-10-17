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

import org.apache.fesod.sheet.metadata.Head;
import org.apache.fesod.sheet.metadata.data.WriteCellData;
import org.apache.fesod.sheet.write.handler.context.CellWriteHandlerContext;
import org.apache.fesod.sheet.write.metadata.style.WriteCellStyle;

/**
 * A {@link AbstractCellStyleStrategy} that resolves styles vertically, i.e. one style per logical
 * column shared by every cell in that column.
 *
 * <p>Subclasses typically override {@link #headCellStyle(Head)} and/or {@link #contentCellStyle(Head)}
 * to return a {@link WriteCellStyle} for the given column. The framework then merges that style
 * into each cell's {@link WriteCellData} during the header and content phases respectively. The
 * {@link CellWriteHandlerContext} variants are provided for subclasses that need access to the
 * full context (sheet holder, current cell, converted data, …) instead of just the column head.</p>
 */
public abstract class AbstractVerticalCellStyleStrategy extends AbstractCellStyleStrategy {

    /**
     * {@inheritDoc}
     *
     * <p>This implementation skips the cell when {@link #stopProcessing(CellWriteHandlerContext)}
     * returns {@code true}, and otherwise merges the style returned by
     * {@link #headCellStyle(CellWriteHandlerContext)} into the first {@link WriteCellData} of the
     * current cell.</p>
     */
    @Override
    protected void setHeadCellStyle(CellWriteHandlerContext context) {
        if (stopProcessing(context)) {
            return;
        }
        WriteCellData<?> cellData = context.getFirstCellData();
        WriteCellStyle.merge(headCellStyle(context), cellData.getOrCreateStyle());
    }

    /**
     * {@inheritDoc}
     *
     * <p>This implementation is a no-op when the cell has no {@link WriteCellData} attached, and
     * otherwise merges the style returned by {@link #contentCellStyle(CellWriteHandlerContext)}
     * into the first {@link WriteCellData} of the current cell.</p>
     */
    @Override
    protected void setContentCellStyle(CellWriteHandlerContext context) {
        if (context.getFirstCellData() == null) {
            return;
        }
        WriteCellData<?> cellData = context.getFirstCellData();
        WriteCellStyle.merge(contentCellStyle(context), cellData.getOrCreateStyle());
    }

    /**
     * Resolves the header {@link WriteCellStyle} for the column represented by {@code context}.
     *
     * <p>The default implementation delegates to {@link #headCellStyle(Head)} using
     * {@code context.getHeadData()}. Override this variant only when the extra
     * information available in the context is required; otherwise override
     * {@link #headCellStyle(Head)}.</p>
     *
     * @param context the context of the header cell currently being styled; never {@code null}
     * @return the style to apply to the header cell, or {@code null} to leave the cell's existing
     *         style untouched
     */
    protected WriteCellStyle headCellStyle(CellWriteHandlerContext context) {
        return headCellStyle(context.getHeadData());
    }

    /**
     * Resolves the header {@link WriteCellStyle} for a given column head. This is the primary
     * extension point for subclasses that want a per-column header style.
     *
     * @param head the head metadata describing the column; may be {@code null} when writing
     *             without a header
     * @return the style to apply to every header cell in this column, or {@code null} for no
     *         change; the default implementation returns {@code null}
     */
    protected WriteCellStyle headCellStyle(Head head) {
        return null;
    }

    /**
     * Resolves the content {@link WriteCellStyle} for the column represented by {@code context}.
     *
     * <p>The default implementation delegates to {@link #contentCellStyle(Head)} using
     * {@code context.getHeadData()}. Override this variant only when the extra
     * information available in the context is required; otherwise override
     * {@link #contentCellStyle(Head)}.</p>
     *
     * @param context the context of the content cell currently being styled; never {@code null}
     * @return the style to apply to the content cell, or {@code null} to leave the cell's existing
     *         style untouched
     */
    protected WriteCellStyle contentCellStyle(CellWriteHandlerContext context) {
        return contentCellStyle(context.getHeadData());
    }

    /**
     * Resolves the content {@link WriteCellStyle} for a given column head. This is the primary
     * extension point for subclasses that want a per-column content style.
     *
     * @param head the head metadata describing the column; may be {@code null} when writing
     *             without a header
     * @return the style to apply to every content cell in this column, or {@code null} for no
     *         change; the default implementation returns {@code null}
     */
    protected WriteCellStyle contentCellStyle(Head head) {
        return null;
    }

    /**
     * Decides whether style processing should be skipped for the current cell.
     *
     * <p>The default implementation returns {@code true} when the cell has no
     * {@link WriteCellData} attached (nothing to style into) or when there is no head metadata
     * (so the vertical column style cannot be resolved). Subclasses may override to skip
     * additional cases.</p>
     *
     * @param context the context of the cell currently being styled; never {@code null}
     * @return {@code true} to skip style processing for this cell, {@code false} to continue
     */
    protected boolean stopProcessing(CellWriteHandlerContext context) {
        if (context.getFirstCellData() == null) {
            return true;
        }
        return context.getHeadData() == null;
    }
}
