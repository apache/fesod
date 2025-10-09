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

import org.apache.fesod.excel.metadata.Head;
import org.apache.fesod.excel.metadata.data.WriteCellData;
import org.apache.fesod.excel.write.handler.context.CellWriteHandlerContext;
import org.apache.fesod.excel.write.metadata.style.WriteCellStyle;

/**
 * Use the same style for the column
 *
 *
 */
public abstract class AbstractVerticalCellStyleStrategy extends AbstractCellStyleStrategy {

    @Override
    protected void setHeadCellStyle(CellWriteHandlerContext context) {
        if (stopProcessing(context)) {
            return;
        }
        WriteCellData<?> cellData = context.getFirstCellData();
        WriteCellStyle.merge(headCellStyle(context), cellData.getOrCreateStyle());
    }

    @Override
    protected void setContentCellStyle(CellWriteHandlerContext context) {
        if (context.getFirstCellData() == null) {
            return;
        }
        WriteCellData<?> cellData = context.getFirstCellData();
        WriteCellStyle.merge(contentCellStyle(context), cellData.getOrCreateStyle());
    }

    /**
     * Returns the column width corresponding to each column head
     *
     * @param context
     * @return
     */
    protected WriteCellStyle headCellStyle(CellWriteHandlerContext context) {
        return headCellStyle(context.getHeadData());
    }

    /**
     * Returns the column width corresponding to each column head
     *
     * @param head Nullable
     * @return
     */
    protected WriteCellStyle headCellStyle(Head head) {
        return null;
    }

    /**
     * Returns the column width corresponding to each column head.
     *
     * @param context
     * @return
     */
    protected WriteCellStyle contentCellStyle(CellWriteHandlerContext context) {
        return contentCellStyle(context.getHeadData());
    }

    /**
     * Returns the column width corresponding to each column head
     *
     * @param head Nullable
     * @return
     */
    protected WriteCellStyle contentCellStyle(Head head) {
        return null;
    }

    protected boolean stopProcessing(CellWriteHandlerContext context) {
        if (context.getFirstCellData() == null) {
            return true;
        }
        return context.getHeadData() == null;
    }
}
