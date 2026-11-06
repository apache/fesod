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

package org.apache.fesod.sheet.write.handler;

import java.util.List;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.fesod.sheet.metadata.Head;
import org.apache.fesod.sheet.metadata.data.WriteCellData;
import org.apache.fesod.sheet.write.handler.context.CellWriteHandlerContext;
import org.apache.fesod.sheet.write.metadata.holder.WriteSheetHolder;
import org.apache.fesod.sheet.write.metadata.holder.WriteTableHolder;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

/**
 * intercepts handle cell creation
 *
 * <p>Four callback points are exposed: before a cell is created, right after a cell is created,
 * after the cell data has been converted, and after all operations on the cell have been
 * completed.</p>
 *
 * <p>Implementations may override either the {@code *Context} variants or the fine-grained
 * parameter variants of each callback. The default {@code *Context} methods delegate to the
 * fine-grained ones, so overriding the fine-grained method is enough for most cases.</p>
 */
public interface CellWriteHandler extends WriteHandler {

    /**
     * Called before create the cell
     *
     * @param context the context that carries the sheet holder, table holder, row, head data,
     *                column index, relative row index and the {@code isHead} flag for the cell
     *                about to be created
     */
    default void beforeCellCreate(CellWriteHandlerContext context) {
        beforeCellCreate(
                context.getWriteSheetHolder(),
                context.getWriteTableHolder(),
                context.getRow(),
                context.getHeadData(),
                context.getColumnIndex(),
                context.getRelativeRowIndex(),
                context.getHead());
    }

    /**
     * Called before create the cell
     *
     * @param writeSheetHolder the holder of the sheet that the cell belongs to; never {@code null}
     * @param writeTableHolder the holder of the table that the cell belongs to; {@code null} when
     *                         writing without using tables
     * @param row              the row that the cell will belong to; may be {@code null} when the
     *                         row has not been created yet
     * @param head             the head metadata of the cell; {@code null} when filling data or
     *                         writing without a header
     * @param columnIndex      the absolute column index within the row, starting from {@code 0}
     * @param relativeRowIndex the row index relative to the current write batch; {@code null} when
     *                         filling data
     * @param isHead           whether the cell belongs to a header row; always {@code false} when
     *                         filling data
     */
    default void beforeCellCreate(
            WriteSheetHolder writeSheetHolder,
            WriteTableHolder writeTableHolder,
            Row row,
            Head head,
            Integer columnIndex,
            Integer relativeRowIndex,
            Boolean isHead) {}

    /**
     * Called after the cell is created
     *
     * @param context the context that carries the newly created cell together with the sheet
     *                holder, table holder, head data, relative row index and the {@code isHead}
     *                flag
     */
    default void afterCellCreate(CellWriteHandlerContext context) {
        afterCellCreate(
                context.getWriteSheetHolder(),
                context.getWriteTableHolder(),
                context.getCell(),
                context.getHeadData(),
                context.getRelativeRowIndex(),
                context.getHead());
    }

    /**
     * Called after the cell is created
     *
     * @param writeSheetHolder the holder of the sheet that the cell belongs to; never {@code null}
     * @param writeTableHolder the holder of the table that the cell belongs to; {@code null} when
     *                         writing without using tables
     * @param cell             the cell that has just been created
     * @param head             the head metadata of the cell; {@code null} when filling data or
     *                         writing without a header
     * @param relativeRowIndex the row index relative to the current write batch; {@code null} when
     *                         filling data
     * @param isHead           whether the cell belongs to a header row; always {@code false} when
     *                         filling data
     */
    default void afterCellCreate(
            WriteSheetHolder writeSheetHolder,
            WriteTableHolder writeTableHolder,
            Cell cell,
            Head head,
            Integer relativeRowIndex,
            Boolean isHead) {}

    /**
     * Called after the cell data is converted
     *
     * @param context the context that carries the converted {@link WriteCellData} together with
     *                the cell, head data, relative row index and the {@code isHead} flag
     */
    default void afterCellDataConverted(CellWriteHandlerContext context) {
        WriteCellData<?> writeCellData = CollectionUtils.isNotEmpty(context.getCellDataList())
                ? context.getCellDataList().get(0)
                : null;
        afterCellDataConverted(
                context.getWriteSheetHolder(),
                context.getWriteTableHolder(),
                writeCellData,
                context.getCell(),
                context.getHeadData(),
                context.getRelativeRowIndex(),
                context.getHead());
    }

    /**
     * Called after the cell data is converted
     *
     * @param writeSheetHolder the holder of the sheet that the cell belongs to; never {@code null}
     * @param writeTableHolder the holder of the table that the cell belongs to; {@code null} when
     *                         writing without using tables
     * @param cellData         the converted cell data; {@code null} when writing a header. When
     *                         filling data a cell may produce multiple {@link WriteCellData}
     *                         entries, in which case only the first one is passed here — override
     *                         the {@link CellWriteHandlerContext} variant to see all of them
     * @param cell             the cell whose data has just been converted
     * @param head             the head metadata of the cell; {@code null} when filling data or
     *                         writing without a header
     * @param relativeRowIndex the row index relative to the current write batch; {@code null} when
     *                         filling data
     * @param isHead           whether the cell belongs to a header row; always {@code false} when
     *                         filling data
     */
    default void afterCellDataConverted(
            WriteSheetHolder writeSheetHolder,
            WriteTableHolder writeTableHolder,
            WriteCellData<?> cellData,
            Cell cell,
            Head head,
            Integer relativeRowIndex,
            Boolean isHead) {}

    /**
     * Called after all operations on the cell have been completed
     *
     * @param context the context that carries the cell being disposed together with the resulting
     *                cell data list, head data, relative row index and the {@code isHead} flag
     */
    default void afterCellDispose(CellWriteHandlerContext context) {
        afterCellDispose(
                context.getWriteSheetHolder(),
                context.getWriteTableHolder(),
                context.getCellDataList(),
                context.getCell(),
                context.getHeadData(),
                context.getRelativeRowIndex(),
                context.getHead());
    }

    /**
     * Called after all operations on the cell have been completed
     *
     * @param writeSheetHolder the holder of the sheet that the cell belongs to; never {@code null}
     * @param writeTableHolder the holder of the table that the cell belongs to; {@code null} when
     *                         writing without using tables
     * @param cellDataList     the list of {@link WriteCellData} produced for this cell;
     *                         {@code null} when writing a header, and may contain several entries
     *                         when filling data
     * @param cell             the cell whose write operations have been completed
     * @param head             the head metadata of the cell; {@code null} when filling data or
     *                         writing without a header
     * @param relativeRowIndex the row index relative to the current write batch; {@code null} when
     *                         filling data
     * @param isHead           whether the cell belongs to a header row; always {@code false} when
     *                         filling data
     */
    default void afterCellDispose(
            WriteSheetHolder writeSheetHolder,
            WriteTableHolder writeTableHolder,
            List<WriteCellData<?>> cellDataList,
            Cell cell,
            Head head,
            Integer relativeRowIndex,
            Boolean isHead) {}
}
