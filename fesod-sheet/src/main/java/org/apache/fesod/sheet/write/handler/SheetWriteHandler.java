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
import org.apache.fesod.sheet.write.handler.context.SheetWriteHandlerContext;
import org.apache.fesod.sheet.write.metadata.holder.WriteSheetHolder;
import org.apache.fesod.sheet.write.metadata.holder.WriteWorkbookHolder;

/**
 * Intercept the sheet lifecycle during writing.
 *
 * <p>Three callback points are exposed: before a sheet is created, immediately after a sheet has
 * been created, and after all operations on the sheet have been completed.</p>
 *
 * <p>For {@link #beforeSheetCreate} and {@link #afterSheetCreate}, implementations may override
 * either the {@link SheetWriteHandlerContext} variant or the fine-grained parameter variant. The
 * default {@code *Context} methods delegate to the fine-grained ones, so overriding the
 * fine-grained method is enough for most cases.</p>
 */
public interface SheetWriteHandler extends WriteHandler {

    /**
     * Called before create the sheet
     *
     * @param context the context that carries the workbook holder and the sheet holder for the
     *                sheet about to be created
     */
    default void beforeSheetCreate(SheetWriteHandlerContext context) {
        beforeSheetCreate(context.getWriteWorkbookHolder(), context.getWriteSheetHolder());
    }

    /**
     * Called before create the sheet
     *
     * @param writeWorkbookHolder the holder of the workbook being written; never {@code null}
     * @param writeSheetHolder    the holder of the sheet about to be created; never {@code null}
     */
    default void beforeSheetCreate(WriteWorkbookHolder writeWorkbookHolder, WriteSheetHolder writeSheetHolder) {}

    /**
     * Called after the sheet is created
     *
     * @param context the context that carries the workbook holder and the newly created sheet
     *                holder
     */
    default void afterSheetCreate(SheetWriteHandlerContext context) {
        afterSheetCreate(context.getWriteWorkbookHolder(), context.getWriteSheetHolder());
    }

    /**
     * Called after the sheet is created
     *
     * @param writeWorkbookHolder the holder of the workbook being written; never {@code null}
     * @param writeSheetHolder    the holder of the sheet that has just been created; never
     *                            {@code null}
     */
    default void afterSheetCreate(WriteWorkbookHolder writeWorkbookHolder, WriteSheetHolder writeSheetHolder) {}

    /**
     * Called after all operations on a sheet have been completed.
     * This is the sheet-level counterpart of
     * {@link RowWriteHandler#afterRowDispose(RowWriteHandlerContext)} and is typically used to
     * finalize sheet-wide formatting or resources.
     *
     * @param context the context that carries the workbook holder and the sheet being disposed
     */
    default void afterSheetDispose(SheetWriteHandlerContext context) {}
}
