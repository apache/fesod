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

import org.apache.fesod.sheet.write.handler.context.RowWriteHandlerContext;
import org.apache.fesod.sheet.write.metadata.holder.WriteSheetHolder;
import org.apache.fesod.sheet.write.metadata.holder.WriteTableHolder;
import org.apache.poi.ss.usermodel.Row;

/**
 * intercepts handle row creation
 *
 * <p>Implementations may override either the {@code *Context} variants or the fine-grained
 * parameter variants of each callback. The default {@code *Context} methods delegate to the
 * fine-grained ones, so overriding the fine-grained method is enough for most cases.</p>
 */
public interface RowWriteHandler extends WriteHandler {

    /**
     * Called before create the row
     *
     * @param context the context that carries the sheet holder, table holder, row index,
     *                relative row index and the {@code isHead} flag for the row about to be cre
     */
    default void beforeRowCreate(RowWriteHandlerContext context) {
        beforeRowCreate(
                context.getWriteSheetHolder(),
                context.getWriteTableHolder(),
                context.getRowIndex(),
                context.getRelativeRowIndex(),
                context.getHead());
    }

    /**
     * Called before create the row
     *
     * @param writeSheetHolder the holder of the sheet that the row belongs to; never {@code null}
     * @param writeTableHolder the holder of the table that the row belongs to; {@code null} when
     *                         writing without using tables
     * @param rowIndex         the absolute row index within the sheet, starting from {@code 0}
     * @param relativeRowIndex the row index relative to the current write batch; {@code null} when
     *                         filling data
     * @param isHead           whether the row being created is a header row; {@code null} when
     *                         filling data
     */
    default void beforeRowCreate(
            WriteSheetHolder writeSheetHolder,
            WriteTableHolder writeTableHolder,
            Integer rowIndex,
            Integer relativeRowIndex,
            Boolean isHead) {}

    /**
     * Called after the row is created
     *
     * @param context the context that carries the newly created row together with the sheet holder,
     *                table holder, relative row index and the {@code isHead} flag
     */
    default void afterRowCreate(RowWriteHandlerContext context) {
        afterRowCreate(
                context.getWriteSheetHolder(),
                context.getWriteTableHolder(),
                context.getRow(),
                context.getRelativeRowIndex(),
                context.getHead());
    }

    /**
     * Called after the row is created
     *
     * @param writeSheetHolder the holder of the sheet that the row belongs to; never {@code null}
     * @param writeTableHolder the holder of the table that the row belongs to; {@code null} when
     *                         writing without using tables
     * @param row              the row that has just been created
     * @param relativeRowIndex the row index relative to the current write batch; {@code null} when
     *                         filling data
     * @param isHead           whether the row is a header row; {@code null} when filling data
     */
    default void afterRowCreate(
            WriteSheetHolder writeSheetHolder,
            WriteTableHolder writeTableHolder,
            Row row,
            Integer relativeRowIndex,
            Boolean isHead) {}

    /**
     * Called after all operations on the row have been completed.
     * In the case of the fill , may be called many times.
     *
     * @param context the context that carries the row being disposed together with the sheet
     *                holder, table holder, relative row index and the {@code isHead} flag
     */
    default void afterRowDispose(RowWriteHandlerContext context) {
        afterRowDispose(
                context.getWriteSheetHolder(),
                context.getWriteTableHolder(),
                context.getRow(),
                context.getRelativeRowIndex(),
                context.getHead());
    }

    /**
     * Called after all operations on the row have been completed.
     * In the case of the fill , may be called many times.
     *
     * @param writeSheetHolder the holder of the sheet that the row belongs to; never {@code null}
     * @param writeTableHolder the holder of the table that the row belongs to; {@code null} when
     *                         writing without using tables
     * @param row              the row whose write operations have been completed
     * @param relativeRowIndex the row index relative to the current write batch; {@code null} when
     *                         filling data
     * @param isHead           whether the row is a header row; {@code null} when filling data
     */
    default void afterRowDispose(
            WriteSheetHolder writeSheetHolder,
            WriteTableHolder writeTableHolder,
            Row row,
            Integer relativeRowIndex,
            Boolean isHead) {}
}
