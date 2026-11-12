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
import org.apache.poi.hssf.record.RKRecord;
import org.apache.poi.hssf.record.Record;

/**
 * Handler for the BIFF {@code RK} record, which encodes a numeric or short-text cell value in a compact
 * 30-bit representation.
 * <p>
 * This handler does not decode the RK value; it registers the cell's position in the current row's cell map
 * as an empty {@link ReadCellData}, so downstream listeners can still observe that the cell exists. Records
 * whose physical column is filtered out by the current column resolver are skipped.
 */
public class RkRecordHandler extends AbstractXlsRecordHandler implements IgnorableXlsRecordHandler {

    /**
     * {@inheritDoc}
     * <p>
     * Resolves the target column from {@link RKRecord#getColumn()}; if the column is not filtered out, stores an
     * empty {@link ReadCellData} at {@link RKRecord#getRow()} into the current sheet's cell map.
     *
     * @param xlsReadContext the current XLS read context, never {@code null}
     * @param record         the {@link RKRecord} to process, never {@code null}
     * @throws ClassCastException if {@code record} is not an {@link RKRecord}
     */
    @Override
    public void processRecord(XlsReadContext xlsReadContext, Record record) {
        RKRecord re = (RKRecord) record;

        Integer targetColumnIndex = xlsReadContext.readSheetHolder().determineTargetColumnIndex(re.getColumn());
        if (targetColumnIndex == null) {
            return;
        }

        xlsReadContext
                .xlsReadSheetHolder()
                .getCellMap()
                .put(targetColumnIndex, ReadCellData.newEmptyInstance(re.getRow(), targetColumnIndex));
    }
}
