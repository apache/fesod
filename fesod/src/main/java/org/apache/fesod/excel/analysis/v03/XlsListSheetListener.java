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

package org.apache.fesod.excel.analysis.v03;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.fesod.excel.analysis.v03.handlers.BofRecordHandler;
import org.apache.fesod.excel.analysis.v03.handlers.BoundSheetRecordHandler;
import org.apache.fesod.excel.context.xls.XlsReadContext;
import org.apache.fesod.excel.exception.ExcelAnalysisException;
import org.apache.poi.hssf.eventusermodel.EventWorkbookBuilder;
import org.apache.poi.hssf.eventusermodel.FormatTrackingHSSFListener;
import org.apache.poi.hssf.eventusermodel.HSSFEventFactory;
import org.apache.poi.hssf.eventusermodel.HSSFListener;
import org.apache.poi.hssf.eventusermodel.HSSFRequest;
import org.apache.poi.hssf.eventusermodel.MissingRecordAwareHSSFListener;
import org.apache.poi.hssf.record.BOFRecord;
import org.apache.poi.hssf.record.BoundSheetRecord;
import org.apache.poi.hssf.record.Record;

/**
 * In some cases, you need to know the number of sheets in advance and only read the file once in advance.
 *
 *
 */
public class XlsListSheetListener implements HSSFListener {
    private final XlsReadContext xlsReadContext;
    private static final Map<Short, XlsRecordHandler> XLS_RECORD_HANDLER_MAP = new HashMap<Short, XlsRecordHandler>();

    static {
        // Initialize the map with handlers for specific record types
        XLS_RECORD_HANDLER_MAP.put(BOFRecord.sid, new BofRecordHandler());
        XLS_RECORD_HANDLER_MAP.put(BoundSheetRecord.sid, new BoundSheetRecordHandler());
    }

    /**
     * Constructor for initializing the listener with a given context.
     *
     * @param xlsReadContext The context object containing information about the XLS file being processed.
     */
    public XlsListSheetListener(XlsReadContext xlsReadContext) {
        this.xlsReadContext = xlsReadContext;
        xlsReadContext.xlsReadWorkbookHolder().setNeedReadSheet(Boolean.FALSE);
    }

    /**
     * Processes a specific record by delegating it to the appropriate handler based on its SID.
     *
     * @param record The record to be processed.
     */
    @Override
    public void processRecord(Record record) {
        XlsRecordHandler handler = XLS_RECORD_HANDLER_MAP.get(record.getSid());
        if (handler == null) {
            return;
        }
        handler.processRecord(xlsReadContext, record);
    }

    /**
     * Executes the event-based processing of the XLS file.
     * It sets up listeners and processes workbook events.
     */
    public void execute() {
        MissingRecordAwareHSSFListener listener = new MissingRecordAwareHSSFListener(this);
        HSSFListener formatListener = new FormatTrackingHSSFListener(listener);
        HSSFEventFactory factory = new HSSFEventFactory();
        HSSFRequest request = new HSSFRequest();
        EventWorkbookBuilder.SheetRecordCollectingListener workbookBuildingListener =
                new EventWorkbookBuilder.SheetRecordCollectingListener(formatListener);
        request.addListenerForAllRecords(workbookBuildingListener);
        try {
            factory.processWorkbookEvents(
                    request, xlsReadContext.xlsReadWorkbookHolder().getPoifsFileSystem());
        } catch (IOException e) {
            throw new ExcelAnalysisException(e);
        }
    }
}
