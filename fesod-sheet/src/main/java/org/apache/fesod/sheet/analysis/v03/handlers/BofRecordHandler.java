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

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.fesod.sheet.context.xls.XlsReadContext;
import org.apache.fesod.sheet.exception.ExcelAnalysisException;
import org.apache.fesod.sheet.exception.ExcelAnalysisStopException;
import org.apache.fesod.sheet.read.metadata.ReadSheet;
import org.apache.fesod.sheet.read.metadata.holder.xls.XlsReadWorkbookHolder;
import org.apache.fesod.sheet.util.SheetUtils;
import org.apache.poi.hssf.record.BOFRecord;
import org.apache.poi.hssf.record.BoundSheetRecord;
import org.apache.poi.hssf.record.Record;

/**
 * Handles the BIFF {@code BOF} (Beginning Of File) record, which marks the start of the workbook stream or of a single
 * worksheet substream.
 * <p>
 * On a workbook-level {@code BOF} the read state is reset. On a sheet-level {@code BOF} the lazy sheet list is
 * initialised and the next {@link ReadSheet} in line is selected for reading, or marked as ignored when it does not
 * match the user's sheet selection.
 */
public class BofRecordHandler extends AbstractXlsRecordHandler {

    /**
     * {@inheritDoc}
     * <p>
     * The {@code record} must be a {@link BOFRecord}; other types are not supported by this handler.
     *
     * @param xlsReadContext the current XLS read context, never {@code null}
     * @param record         the {@link BOFRecord} to process, never {@code null}
     */
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

        List<ReadSheet> actualSheetDataList = xlsReadWorkbookHolder.getActualSheetDataList();
        if (CollectionUtils.isEmpty(actualSheetDataList) || readSheetIndex >= actualSheetDataList.size()) {
            throw new ExcelAnalysisException("Cannot find sheet at index " + readSheetIndex + ", total sheet count is "
                    + CollectionUtils.size(actualSheetDataList));
        }
        ReadSheet actualReadSheet = actualSheetDataList.get(readSheetIndex);
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

    /**
     * Build the actual list of {@link ReadSheet}s from the collected {@link BoundSheetRecord}s, ordered by their
     * {@code BOF} position.
     * <p>
     * This is a no-op when the list has already been initialised. If the caller only requested the sheet list
     * (see {@code XlsReadWorkbookHolder#needReadSheet()}), an {@link ExcelAnalysisStopException} is thrown right after
     * the list is built so that parsing stops without reading any cell content.
     *
     * @param xlsReadWorkbookHolder the workbook holder holding the bound sheet records, never {@code null}
     * @throws ExcelAnalysisStopException if only the sheet list was requested and parsing should stop immediately
     */
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
