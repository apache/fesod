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

package org.apache.fesod.sheet.write.style.column;

import java.util.List;
import org.apache.fesod.sheet.metadata.Head;
import org.apache.fesod.sheet.metadata.data.WriteCellData;
import org.apache.fesod.sheet.write.metadata.holder.WriteSheetHolder;
import org.apache.poi.ss.usermodel.Cell;

/**
 * A {@link AbstractColumnWidthStyleStrategy} that sizes each column from its header, so a whole
 * column shares one width regardless of the content cells written into it.
 *
 * <p>Subclasses only need to implement {@link #columnWidth(Head, Integer)}; the base class takes
 * care of calling it at the right moments and of converting the returned width into POI's
 * {@code 1/256-character} unit before applying it via
 * {@link org.apache.poi.ss.usermodel.Sheet#setColumnWidth(int, int)}.</p>
 */
public abstract class AbstractHeadColumnWidthStyleStrategy extends AbstractColumnWidthStyleStrategy {

    /**
     * {@inheritDoc}
     *
     * <p>This implementation only acts on header cells and on the very first data row (so that the
     * width is applied once per column when there is no header). It queries
     * {@link #columnWidth(Head, Integer)} and, if a non-{@code null} width is returned, scales it
     * by {@code 256} and applies it to the sheet via
     * {@link org.apache.poi.ss.usermodel.Sheet#setColumnWidth(int, int)}.</p>
     */
    @Override
    protected void setColumnWidth(
            WriteSheetHolder writeSheetHolder,
            List<WriteCellData<?>> cellDataList,
            Cell cell,
            Head head,
            Integer relativeRowIndex,
            Boolean isHead) {
        boolean needSetWidth = relativeRowIndex != null && (isHead || relativeRowIndex == 0);
        if (!needSetWidth) {
            return;
        }
        Integer width = columnWidth(head, cell.getColumnIndex());
        if (width != null) {
            width = width * 256;
            writeSheetHolder.getSheet().setColumnWidth(cell.getColumnIndex(), width);
        }
    }

    /**
     * Returns the width, in characters, that should be applied to the given column.
     *
     * <p>Returning {@code null} means "do not change the width" and the base class will leave the
     * column untouched. Non-{@code null} values are multiplied by {@code 256} internally to match
     * POI's column-width unit before being handed to the underlying sheet.</p>
     *
     * @param head        the head metadata for the column; may be {@code null} when writing
     *                    without a header
     * @param columnIndex the zero-based index of the column being sized; never {@code null}
     * @return the desired column width measured in characters, or {@code null} to keep the
     *         column's existing width unchanged
     */
    protected abstract Integer columnWidth(Head head, Integer columnIndex);
}
