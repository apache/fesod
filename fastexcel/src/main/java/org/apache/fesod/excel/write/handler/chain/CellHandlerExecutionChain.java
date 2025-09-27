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
import org.apache.fesod.excel.write.handler.CellWriteHandler;
import org.apache.fesod.excel.write.handler.context.CellWriteHandlerContext;

/**
 * Execute the cell handler chain
 *
 *
 */
@Getter
@Setter
@EqualsAndHashCode
public class CellHandlerExecutionChain {
    /**
     * next chain
     */
    private CellHandlerExecutionChain next;
    /**
     * handler
     */
    private CellWriteHandler handler;

    public CellHandlerExecutionChain(CellWriteHandler handler) {
        this.handler = handler;
    }

    public void beforeCellCreate(CellWriteHandlerContext context) {
        this.handler.beforeCellCreate(context);
        if (this.next != null) {
            this.next.beforeCellCreate(context);
        }
    }

    public void afterCellCreate(CellWriteHandlerContext context) {
        this.handler.afterCellCreate(context);
        if (this.next != null) {
            this.next.afterCellCreate(context);
        }
    }

    public void afterCellDataConverted(CellWriteHandlerContext context) {
        this.handler.afterCellDataConverted(context);
        if (this.next != null) {
            this.next.afterCellDataConverted(context);
        }
    }

    public void afterCellDispose(CellWriteHandlerContext context) {
        this.handler.afterCellDispose(context);
        if (this.next != null) {
            this.next.afterCellDispose(context);
        }
    }

    public void addLast(CellWriteHandler handler) {
        CellHandlerExecutionChain context = this;
        while (context.next != null) {
            context = context.next;
        }
        context.next = new CellHandlerExecutionChain(handler);
    }
}
