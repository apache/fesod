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

import java.util.ArrayList;
import java.util.List;
import org.apache.fesod.excel.context.xls.XlsReadContext;
import org.apache.fesod.excel.exception.ExcelAnalysisStopException;
import org.apache.fesod.excel.read.metadata.ReadSheet;
import org.apache.fesod.excel.read.metadata.holder.xls.XlsReadWorkbookHolder;
import org.apache.fesod.excel.util.SheetUtils;
import org.apache.poi.hssf.record.BOFRecord;
import org.apache.poi.hssf.record.BoundSheetRecord;
import org.apache.poi.hssf.record.Record;

/**
 * Record handler
 */
public class BofRecordHandler extends AbstractXlsRecordHandler {

    @Override
    public void processRecord(XlsReadContext xlsReadContext, Record record) {
        BOFRecord br = (BOFRecord) record;
        XlsReadWorkbookHolder xlsReadWorkbookHolder = xlsReadContext.xlsReadWorkbookHolder();
        if (br.getType() == BOFRecord.TYPE_WORKBOOK) {
            xlsReadWorkbookHolder.setReadSheetIndex(null);
            xlsReadWorkbookHolder.setIgnoreRecord(Boolean.FALSE);
            return;
        }
        if (br.getType() != BOFRecord.TYPE_WORKSHEET) {
            return;
        }
        // Init read sheet Data
        initReadSheetDataList(xlsReadWorkbookHolder);
        Integer readSheetIndex = xlsReadWorkbookHolder.getReadSheetIndex();
        if (readSheetIndex == null) {
            readSheetIndex = 0;
            xlsReadWorkbookHolder.setReadSheetIndex(readSheetIndex);
        }
        ReadSheet actualReadSheet =
                xlsReadWorkbookHolder.getActualSheetDataList().get(readSheetIndex);
        assert actualReadSheet != null : "Can't find the sheet.";
        // Copy the parameter to the current sheet
        ReadSheet readSheet = SheetUtils.match(actualReadSheet, xlsReadContext);
        if (readSheet != null) {
            xlsReadContext.currentSheet(readSheet);
            xlsReadContext.xlsReadWorkbookHolder().setIgnoreRecord(Boolean.FALSE);
        } else {
            xlsReadContext.xlsReadWorkbookHolder().setIgnoreRecord(Boolean.TRUE);
        }
        xlsReadContext.xlsReadWorkbookHolder().setCurrentSheetStopped(Boolean.FALSE);
        // Go read the next one
        xlsReadWorkbookHolder.setReadSheetIndex(xlsReadWorkbookHolder.getReadSheetIndex() + 1);
    }

    private void initReadSheetDataList(XlsReadWorkbookHolder xlsReadWorkbookHolder) {
        if (xlsReadWorkbookHolder.getActualSheetDataList() != null) {
            return;
        }
        BoundSheetRecord[] boundSheetRecords =
                BoundSheetRecord.orderByBofPosition(xlsReadWorkbookHolder.getBoundSheetRecordList());
        List<ReadSheet> readSheetDataList = new ArrayList<>();
        for (int i = 0; i < boundSheetRecords.length; i++) {
            BoundSheetRecord boundSheetRecord = boundSheetRecords[i];
            ReadSheet readSheet = new ReadSheet(i, boundSheetRecord.getSheetname());
            readSheet.setHidden(boundSheetRecord.isHidden());
            readSheet.setVeryHidden(boundSheetRecord.isVeryHidden());
            readSheetDataList.add(readSheet);
        }
        xlsReadWorkbookHolder.setActualSheetDataList(readSheetDataList);
        // Just need to get the list of sheets
        if (!xlsReadWorkbookHolder.getNeedReadSheet()) {
            throw new ExcelAnalysisStopException("Just need to get the list of sheets.");
        }
    }
}
