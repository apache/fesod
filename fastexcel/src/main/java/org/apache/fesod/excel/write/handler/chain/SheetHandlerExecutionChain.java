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
import org.apache.fesod.excel.write.handler.SheetWriteHandler;
import org.apache.fesod.excel.write.handler.context.SheetWriteHandlerContext;

/**
 * Execute the sheet handler chain
 *
 *
 */
@Getter
@Setter
@EqualsAndHashCode
public class SheetHandlerExecutionChain {
    /**
     * next chain
     */
    private SheetHandlerExecutionChain next;
    /**
     * handler
     */
    private SheetWriteHandler handler;

    public SheetHandlerExecutionChain(SheetWriteHandler handler) {
        this.handler = handler;
    }

    public void beforeSheetCreate(SheetWriteHandlerContext context) {
        this.handler.beforeSheetCreate(context);
        if (this.next != null) {
            this.next.beforeSheetCreate(context);
        }
    }

    public void afterSheetCreate(SheetWriteHandlerContext context) {
        this.handler.afterSheetCreate(context);
        if (this.next != null) {
            this.next.afterSheetCreate(context);
        }
    }

    public void addLast(SheetWriteHandler handler) {
        SheetHandlerExecutionChain context = this;
        while (context.next != null) {
            context = context.next;
        }
        context.next = new SheetHandlerExecutionChain(handler);
    }

    public void afterSheetDispose(SheetWriteHandlerContext context) {
        this.handler.afterSheetDispose(context);
        if (this.next != null) {
            this.next.afterSheetDispose(context);
        }
    }
}
