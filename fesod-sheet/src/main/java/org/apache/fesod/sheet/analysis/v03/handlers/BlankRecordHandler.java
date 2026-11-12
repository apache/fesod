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

package org.apache.fesod.sheet.analysis.v03.handlers;

import org.apache.fesod.sheet.analysis.v03.IgnorableXlsRecordHandler;
import org.apache.fesod.sheet.context.xls.XlsReadContext;
import org.apache.fesod.sheet.metadata.data.ReadCellData;
import org.apache.poi.hssf.record.BlankRecord;
import org.apache.poi.hssf.record.Record;

/**
 * Handler for {@link BlankRecord} entries encountered while streaming an XLS (BIFF v03) workbook.
 * <p>
 * A {@code BlankRecord} represents a cell that carries formatting but no value. This handler stores
 * it in the current row's cell map as an empty {@link ReadCellData}, so downstream listeners can
 * still observe the cell's position. If the physical column is filtered out by the current column
 * resolver, the record is skipped.
 */
public class BlankRecordHandler extends AbstractXlsRecordHandler implements IgnorableXlsRecordHandler {

    /**
     * Register the blank cell described by {@code record} into the current row's cell map.
     *
     * @param xlsReadContext the active XLS read context, used to resolve the target column index and
     *                       to access the current sheet's cell map
     * @param record         the record to process, expected to be a {@link BlankRecord}
     * @throws ClassCastException if {@code record} is not a {@link BlankRecord}
     */
    @Override
    public void processRecord(XlsReadContext xlsReadContext, Record record) {
        BlankRecord br = (BlankRecord) record;

        Integer targetColumnIndex = xlsReadContext.readSheetHolder().determineTargetColumnIndex(br.getColumn());
        if (targetColumnIndex == null) {
            return;
        }

        xlsReadContext
                .xlsReadSheetHolder()
                .getCellMap()
                .put(targetColumnIndex, ReadCellData.newEmptyInstance(br.getRow(), targetColumnIndex));
    }
}
