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

package org.apache.fesod.excel.write.handler.chain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.apache.fesod.excel.write.handler.WorkbookWriteHandler;
import org.apache.fesod.excel.write.handler.context.WorkbookWriteHandlerContext;

/**
 * Execute the workbook handler chain
 *
 *
 */
@Getter
@Setter
@EqualsAndHashCode
public class WorkbookHandlerExecutionChain {
    /**
     * next chain
     */
    private WorkbookHandlerExecutionChain next;

    /**
     * handler
     */
    private WorkbookWriteHandler handler;

    public WorkbookHandlerExecutionChain(WorkbookWriteHandler handler) {
        this.handler = handler;
    }

    public void beforeWorkbookCreate(WorkbookWriteHandlerContext context) {
        this.handler.beforeWorkbookCreate(context);
        if (this.next != null) {
            this.next.beforeWorkbookCreate(context);
        }
    }

    public void afterWorkbookCreate(WorkbookWriteHandlerContext context) {
        this.handler.afterWorkbookCreate(context);
        if (this.next != null) {
            this.next.afterWorkbookCreate(context);
        }
    }

    public void afterWorkbookDispose(WorkbookWriteHandlerContext context) {
        this.handler.afterWorkbookDispose(context);
        if (this.next != null) {
            this.next.afterWorkbookDispose(context);
        }
    }

    public void addLast(WorkbookWriteHandler handler) {
        WorkbookHandlerExecutionChain context = this;
        while (context.next != null) {
            context = context.next;
        }
        context.next = new WorkbookHandlerExecutionChain(handler);
    }
}
