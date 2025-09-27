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

package org.apache.fesod.excel.write.style;

import org.apache.fesod.excel.constant.OrderConstant;
import org.apache.fesod.excel.metadata.Head;
import org.apache.fesod.excel.write.handler.CellWriteHandler;
import org.apache.fesod.excel.write.handler.context.CellWriteHandlerContext;
import org.apache.poi.ss.usermodel.Cell;

/**
 * Cell style strategy
 *
 *
 */
public abstract class AbstractCellStyleStrategy implements CellWriteHandler {

    @Override
    public int order() {
        return OrderConstant.DEFINE_STYLE;
    }

    @Override
    public void afterCellDispose(CellWriteHandlerContext context) {
        if (context.getHead() == null) {
            return;
        }
        if (context.getHead()) {
            setHeadCellStyle(context);
        } else {
            setContentCellStyle(context);
        }
    }

    /**
     * Sets the cell style of header
     *
     * @param context
     */
    protected void setHeadCellStyle(CellWriteHandlerContext context) {
        setHeadCellStyle(context.getCell(), context.getHeadData(), context.getRelativeRowIndex());
    }

    /**
     * Sets the cell style of header
     *
     * @param cell
     * @param head
     * @param relativeRowIndex
     */
    protected void setHeadCellStyle(Cell cell, Head head, Integer relativeRowIndex) {
        throw new UnsupportedOperationException("Custom styles must override the setHeadCellStyle method.");
    }

    /**
     * Sets the cell style of content
     *
     * @param context
     */
    protected void setContentCellStyle(CellWriteHandlerContext context) {
        setContentCellStyle(context.getCell(), context.getHeadData(), context.getRelativeRowIndex());
    }

    /**
     * Sets the cell style of content
     *
     * @param cell
     * @param head
     * @param relativeRowIndex
     */
    protected void setContentCellStyle(Cell cell, Head head, Integer relativeRowIndex) {
        throw new UnsupportedOperationException("Custom styles must override the setContentCellStyle method.");
    }
}
