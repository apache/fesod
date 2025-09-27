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
import org.apache.fesod.excel.write.handler.RowWriteHandler;
import org.apache.fesod.excel.write.handler.context.RowWriteHandlerContext;

/**
 * Execute the row handler chain
 *
 *
 */
@Getter
@Setter
@EqualsAndHashCode
public class RowHandlerExecutionChain {
    /**
     * next chain
     */
    private RowHandlerExecutionChain next;
    /**
     * handler
     */
    private RowWriteHandler handler;

    public RowHandlerExecutionChain(RowWriteHandler handler) {
        this.handler = handler;
    }

    public void beforeRowCreate(RowWriteHandlerContext context) {
        this.handler.beforeRowCreate(context);
        if (this.next != null) {
            this.next.beforeRowCreate(context);
        }
    }

    public void afterRowCreate(RowWriteHandlerContext context) {
        this.handler.afterRowCreate(context);
        if (this.next != null) {
            this.next.afterRowCreate(context);
        }
    }

    public void afterRowDispose(RowWriteHandlerContext context) {
        this.handler.afterRowDispose(context);
        if (this.next != null) {
            this.next.afterRowDispose(context);
        }
    }

    public void addLast(RowWriteHandler handler) {
        RowHandlerExecutionChain context = this;
        while (context.next != null) {
            context = context.next;
        }
        context.next = new RowHandlerExecutionChain(handler);
    }
}
