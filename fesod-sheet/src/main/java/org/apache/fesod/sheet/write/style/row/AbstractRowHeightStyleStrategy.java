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

package org.apache.fesod.sheet.write.style.row;

import org.apache.fesod.sheet.write.handler.RowWriteHandler;
import org.apache.fesod.sheet.write.handler.context.RowWriteHandlerContext;
import org.apache.poi.ss.usermodel.Row;

/**
 * Base class for strategies that set the height of rows as they are written.
 *
 * <p>The framework invokes {@link #afterRowDispose(RowWriteHandlerContext)} once per row after all
 * cells in that row have been disposed. This implementation dispatches to
 * {@link #setHeadColumnHeight(Row, int)} for header rows and to
 * {@link #setContentColumnHeight(Row, int)} for content rows, so subclasses only need to implement
 * the sides they care about.</p>
 *
 * <p>Note that the two abstract methods keep the historical {@code Column} in their names even
 * though they configure a {@link Row}'s height; only the Javadoc has been clarified to avoid
 * breaking existing subclasses.</p>
 */
public abstract class AbstractRowHeightStyleStrategy implements RowWriteHandler {
    /**
     * {@inheritDoc}
     *
     * <p>This implementation skips rows whose {@code isHead} flag is {@code null} (for example when
     * filling data) and otherwise dispatches to {@link #setHeadColumnHeight(Row, int)} or
     * {@link #setContentColumnHeight(Row, int)}.</p>
     */
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
     * Sets the height of a header row. Called by {@link #afterRowDispose(RowWriteHandlerContext)}
     * for every row that is flagged as a header row.
     *
     * @param row              the header row whose height should be set; never {@code null}
     * @param relativeRowIndex the row index relative to the current header block, starting from
     *                         {@code 0}; useful for distinguishing multi-level headers
     */
    protected abstract void setHeadColumnHeight(Row row, int relativeRowIndex);

    /**
     * Sets the height of a content row. Called by {@link #afterRowDispose(RowWriteHandlerContext)}
     * for every row that is not a header row.
     *
     * @param row              the content row whose height should be set; never {@code null}
     * @param relativeRowIndex the row index relative to the current write batch, starting from
     *                         {@code 0}; useful for styling the first data row differently
     */
    protected abstract void setContentColumnHeight(Row row, int relativeRowIndex);
}
