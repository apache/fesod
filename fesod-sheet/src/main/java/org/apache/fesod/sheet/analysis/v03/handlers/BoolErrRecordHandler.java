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

import java.util.List;
import org.apache.fesod.sheet.analysis.v03.IgnorableXlsRecordHandler;
import org.apache.fesod.sheet.context.xls.XlsReadContext;
import org.apache.fesod.sheet.enums.CellDataTypeEnum;
import org.apache.fesod.sheet.enums.RowTypeEnum;
import org.apache.fesod.sheet.metadata.data.ReadCellData;
import org.apache.poi.hssf.record.BoolErrRecord;
import org.apache.poi.hssf.record.Record;
import org.apache.poi.ss.formula.eval.ErrorEval;

/**
 * Record handler
 */
public class BoolErrRecordHandler extends AbstractXlsRecordHandler implements IgnorableXlsRecordHandler {

    @Override
    public void processRecord(XlsReadContext xlsReadContext, Record record) {
        BoolErrRecord ber = (BoolErrRecord) record;
        int originalColumnIndex = ber.getColumn();
        List<Integer> includeColumnIndexes =
                xlsReadContext.readSheetHolder().getReadSheet().getColumnIndexes();

        int targetColumnIndex = originalColumnIndex;
        if (includeColumnIndexes != null) {
            targetColumnIndex = includeColumnIndexes.indexOf(originalColumnIndex);
            if (targetColumnIndex < 0) {
                return;
            }
        }
        ReadCellData<?> cellData;
        if (ber.isError()) {
            // A BOOLERR record stores either a boolean or an error code; getBooleanValue() would
            // report the error code as `code != 0`.
            cellData = new ReadCellData<>(CellDataTypeEnum.ERROR, ErrorEval.getText(ber.getErrorValue()));
            cellData.setRowIndex(ber.getRow());
            cellData.setColumnIndex(targetColumnIndex);
        } else {
            cellData = ReadCellData.newInstance(ber.getBooleanValue(), ber.getRow(), targetColumnIndex);
        }
        xlsReadContext.xlsReadSheetHolder().getCellMap().put(targetColumnIndex, cellData);
        xlsReadContext.xlsReadSheetHolder().setTempRowType(RowTypeEnum.DATA);
    }
}
