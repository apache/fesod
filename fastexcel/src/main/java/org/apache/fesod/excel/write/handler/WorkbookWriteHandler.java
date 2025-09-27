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

import org.apache.fesod.excel.write.handler.context.WorkbookWriteHandlerContext;
import org.apache.fesod.excel.write.metadata.holder.WriteWorkbookHolder;

/**
 * intercepts handle Workbook creation
 *
 *
 */
public interface WorkbookWriteHandler extends WriteHandler {

    /**
     * Called before create the workbook
     *
     * @param context
     */
    default void beforeWorkbookCreate(WorkbookWriteHandlerContext context) {
        beforeWorkbookCreate();
    }

    /**
     * Called before create the workbook
     */
    default void beforeWorkbookCreate() {}

    /**
     * Called after the workbook is created
     *
     * @param context
     */
    default void afterWorkbookCreate(WorkbookWriteHandlerContext context) {
        afterWorkbookCreate(context.getWriteWorkbookHolder());
    }

    /**
     * Called after the workbook is created
     *
     * @param writeWorkbookHolder
     */
    default void afterWorkbookCreate(WriteWorkbookHolder writeWorkbookHolder) {}

    /**
     * Called after all operations on the workbook have been completed
     *
     * @param context
     */
    default void afterWorkbookDispose(WorkbookWriteHandlerContext context) {
        afterWorkbookDispose(context.getWriteWorkbookHolder());
    }

    /**
     * Called after all operations on the workbook have been completed
     *
     * @param writeWorkbookHolder
     */
    default void afterWorkbookDispose(WriteWorkbookHolder writeWorkbookHolder) {}
}
