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

package org.apache.fesod.excel.write.style.row;

import org.apache.fesod.excel.write.handler.RowWriteHandler;
import org.apache.fesod.excel.write.handler.context.RowWriteHandlerContext;
import org.apache.poi.ss.usermodel.Row;

/**
 * Set the row height strategy
 *
 *
 */
public abstract class AbstractRowHeightStyleStrategy implements RowWriteHandler {
    @Override
    public void afterRowDispose(RowWriteHandlerContext context) {
        if (context.getHead() == null) {
            return;
        }
        if (context.getHead()) {
            setHeadColumnHeight(context.getRow(), context.getRelativeRowIndex());
        } else {
            setContentColumnHeight(context.getRow(), context.getRelativeRowIndex());
        }
    }

    /**
     * Sets the height of header
     *
     * @param row
     * @param relativeRowIndex
     */
    protected abstract void setHeadColumnHeight(Row row, int relativeRowIndex);

    /**
     * Sets the height of content
     *
     * @param row
     * @param relativeRowIndex
     */
    protected abstract void setContentColumnHeight(Row row, int relativeRowIndex);
}
