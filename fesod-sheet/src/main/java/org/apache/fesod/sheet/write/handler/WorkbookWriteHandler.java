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

import org.apache.fesod.sheet.write.handler.context.WorkbookWriteHandlerContext;
import org.apache.fesod.sheet.write.metadata.holder.WriteWorkbookHolder;

/**
 * Intercept handler for the workbook creation and disposal lifecycle.
 *
 * <p>Implementations can override any of the callback methods to inject custom logic at the
 * corresponding stage of the writing process. All methods provide default no-op implementations,
 * so a handler only needs to implement the stages it cares about.</p>
 *
 * @see WriteHandler
 */
public interface WorkbookWriteHandler extends WriteHandler {

    /**
     * Called before create the workbook
     *
     * @param context the workbook write handler context, which contains the write context and the
     *                {@link WriteWorkbookHolder} being written
     */
    default void beforeWorkbookCreate(WorkbookWriteHandlerContext context) {
        beforeWorkbookCreate();
    }

    /**
     * Called before create the workbook
     *
     * <p>This is the deprecated no-context variant kept for backward compatibility; prefer
     * {@link #beforeWorkbookCreate(WorkbookWriteHandlerContext)}.</p>
     */
    default void beforeWorkbookCreate() {}

    /**
     * Called after the workbook is created
     *
     * @param context the workbook write handler context, which contains the write context and the
     *                {@link WriteWorkbookHolder} of the newly created workbook
     */
    default void afterWorkbookCreate(WorkbookWriteHandlerContext context) {
        afterWorkbookCreate(context.getWriteWorkbookHolder());
    }

    /**
     * Called after the workbook is created
     *
     * <p>This is the deprecated no-context variant kept for backward compatibility; prefer
     * {@link #afterWorkbookCreate(WorkbookWriteHandlerContext)}.</p>
     *
     * @param writeWorkbookHolder the holder that wraps the workbook being written and its related context
     */
    default void afterWorkbookCreate(WriteWorkbookHolder writeWorkbookHolder) {}

    /**
     * Called after all operations on the workbook have been completed
     *
     * @param context the workbook write handler context, which contains the write context and the
     *                {@link WriteWorkbookHolder} being disposed
     */
    default void afterWorkbookDispose(WorkbookWriteHandlerContext context) {
        afterWorkbookDispose(context.getWriteWorkbookHolder());
    }

    /**
     * Called after all operations on the workbook have been completed
     *
     * <p>This is the deprecated no-context variant kept for backward compatibility; prefer
     * {@link #afterWorkbookDispose(WorkbookWriteHandlerContext)}.</p>
     *
     * @param writeWorkbookHolder the holder that wraps the workbook being written and its related context
     */
    default void afterWorkbookDispose(WriteWorkbookHolder writeWorkbookHolder) {}
}
