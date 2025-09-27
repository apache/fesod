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

package org.apache.fesod.excel.write.handler;

import java.util.List;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.fesod.excel.metadata.Head;
import org.apache.fesod.excel.metadata.data.WriteCellData;
import org.apache.fesod.excel.write.handler.context.CellWriteHandlerContext;
import org.apache.fesod.excel.write.metadata.holder.WriteSheetHolder;
import org.apache.fesod.excel.write.metadata.holder.WriteTableHolder;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

/**
 * intercepts handle cell creation
 *
 *
 */
public interface CellWriteHandler extends WriteHandler {

    /**
     * Called before create the cell
     *
     * @param context
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
     * @param writeSheetHolder
     * @param writeTableHolder Nullable.It is null without using table writes.
     * @param row
     * @param head             Nullable.It is null in the case of fill data and without head.
     * @param columnIndex
     * @param relativeRowIndex Nullable.It is null in the case of fill data.
     * @param isHead           It will always be false when fill data.
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
     * @param context
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
     * @param writeSheetHolder
     * @param writeTableHolder Nullable.It is null without using table writes.
     * @param cell
     * @param head             Nullable.It is null in the case of fill data and without head.
     * @param relativeRowIndex Nullable.It is null in the case of fill data.
     * @param isHead           It will always be false when fill data.
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
     * @param context
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
     * @param writeSheetHolder
     * @param writeTableHolder Nullable.It is null without using table writes.
     * @param cell
     * @param head             Nullable.It is null in the case of fill data and without head.
     * @param cellData         Nullable.It is null in the case of add header.
     * @param relativeRowIndex Nullable.It is null in the case of fill data.
     * @param isHead           It will always be false when fill data.
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
     * @param context
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
     * @param writeSheetHolder
     * @param writeTableHolder Nullable.It is null without using table writes.
     * @param cell
     * @param head             Nullable.It is null in the case of fill data and without head.
     * @param cellDataList     Nullable.It is null in the case of add header.There may be several when fill the data.
     * @param relativeRowIndex Nullable.It is null in the case of fill data.
     * @param isHead           It will always be false when fill data.
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
