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

package org.apache.fesod.excel.analysis.v03.handlers;

import java.util.LinkedHashMap;
import org.apache.fesod.excel.analysis.v03.IgnorableXlsRecordHandler;
import org.apache.fesod.excel.context.xls.XlsReadContext;
import org.apache.fesod.excel.enums.RowTypeEnum;
import org.apache.fesod.excel.metadata.Cell;
import org.apache.fesod.excel.metadata.data.ReadCellData;
import org.apache.fesod.excel.read.metadata.holder.ReadRowHolder;
import org.apache.fesod.excel.read.metadata.holder.xls.XlsReadSheetHolder;
import org.apache.poi.hssf.eventusermodel.dummyrecord.LastCellOfRowDummyRecord;
import org.apache.poi.hssf.eventusermodel.dummyrecord.MissingCellDummyRecord;
import org.apache.poi.hssf.record.Record;

/**
 * Record handler
 */
public class DummyRecordHandler extends AbstractXlsRecordHandler implements IgnorableXlsRecordHandler {
    @Override
    public void processRecord(XlsReadContext xlsReadContext, Record record) {
        XlsReadSheetHolder xlsReadSheetHolder = xlsReadContext.xlsReadSheetHolder();
        if (record instanceof LastCellOfRowDummyRecord) {
            // End of this row
            LastCellOfRowDummyRecord lcrdr = (LastCellOfRowDummyRecord) record;
            xlsReadSheetHolder.setRowIndex(lcrdr.getRow());
            xlsReadContext.readRowHolder(new ReadRowHolder(
                    lcrdr.getRow(),
                    xlsReadSheetHolder.getTempRowType(),
                    xlsReadContext.readSheetHolder().getGlobalConfiguration(),
                    xlsReadSheetHolder.getCellMap()));
            xlsReadContext.analysisEventProcessor().endRow(xlsReadContext);
            xlsReadSheetHolder.setCellMap(new LinkedHashMap<Integer, Cell>());
            xlsReadSheetHolder.setTempRowType(RowTypeEnum.EMPTY);
        } else if (record instanceof MissingCellDummyRecord) {
            MissingCellDummyRecord mcdr = (MissingCellDummyRecord) record;
            // Some abnormal XLS, in the case of data already exist, or there will be a "MissingCellDummyRecord"
            // records, so if the existing data, empty data is ignored
            xlsReadSheetHolder
                    .getCellMap()
                    .putIfAbsent(mcdr.getColumn(), ReadCellData.newEmptyInstance(mcdr.getRow(), mcdr.getColumn()));
        }
    }
}
