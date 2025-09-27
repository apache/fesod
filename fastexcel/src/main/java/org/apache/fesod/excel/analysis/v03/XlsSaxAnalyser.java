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
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.fesod.excel.analysis.ExcelReadExecutor;
import org.apache.fesod.excel.analysis.v03.handlers.BlankRecordHandler;
import org.apache.fesod.excel.analysis.v03.handlers.BofRecordHandler;
import org.apache.fesod.excel.analysis.v03.handlers.BoolErrRecordHandler;
import org.apache.fesod.excel.analysis.v03.handlers.BoundSheetRecordHandler;
import org.apache.fesod.excel.analysis.v03.handlers.DateWindow1904RecordHandler;
import org.apache.fesod.excel.analysis.v03.handlers.DummyRecordHandler;
import org.apache.fesod.excel.analysis.v03.handlers.EofRecordHandler;
import org.apache.fesod.excel.analysis.v03.handlers.FormulaRecordHandler;
import org.apache.fesod.excel.analysis.v03.handlers.HyperlinkRecordHandler;
import org.apache.fesod.excel.analysis.v03.handlers.IndexRecordHandler;
import org.apache.fesod.excel.analysis.v03.handlers.LabelRecordHandler;
import org.apache.fesod.excel.analysis.v03.handlers.LabelSstRecordHandler;
import org.apache.fesod.excel.analysis.v03.handlers.MergeCellsRecordHandler;
import org.apache.fesod.excel.analysis.v03.handlers.NoteRecordHandler;
import org.apache.fesod.excel.analysis.v03.handlers.NumberRecordHandler;
import org.apache.fesod.excel.analysis.v03.handlers.ObjRecordHandler;
import org.apache.fesod.excel.analysis.v03.handlers.RkRecordHandler;
import org.apache.fesod.excel.analysis.v03.handlers.SstRecordHandler;
import org.apache.fesod.excel.analysis.v03.handlers.StringRecordHandler;
import org.apache.fesod.excel.analysis.v03.handlers.TextObjectRecordHandler;
import org.apache.fesod.excel.context.xls.XlsReadContext;
import org.apache.fesod.excel.exception.ExcelAnalysisException;
import org.apache.fesod.excel.exception.ExcelAnalysisStopException;
import org.apache.fesod.excel.exception.ExcelAnalysisStopSheetException;
import org.apache.fesod.excel.read.metadata.ReadSheet;
import org.apache.fesod.excel.read.metadata.holder.xls.XlsReadWorkbookHolder;
import org.apache.poi.hssf.OldExcelFormatException;
import org.apache.poi.hssf.eventusermodel.EventWorkbookBuilder;
import org.apache.poi.hssf.eventusermodel.FormatTrackingHSSFListener;
import org.apache.poi.hssf.eventusermodel.HSSFEventFactory;
import org.apache.poi.hssf.eventusermodel.HSSFListener;
import org.apache.poi.hssf.eventusermodel.HSSFRequest;
import org.apache.poi.hssf.eventusermodel.MissingRecordAwareHSSFListener;
import org.apache.poi.hssf.record.BOFRecord;
import org.apache.poi.hssf.record.BlankRecord;
import org.apache.poi.hssf.record.BoolErrRecord;
import org.apache.poi.hssf.record.BoundSheetRecord;
import org.apache.poi.hssf.record.DateWindow1904Record;
import org.apache.poi.hssf.record.EOFRecord;
import org.apache.poi.hssf.record.FormulaRecord;
import org.apache.poi.hssf.record.HyperlinkRecord;
import org.apache.poi.hssf.record.IndexRecord;
import org.apache.poi.hssf.record.LabelRecord;
import org.apache.poi.hssf.record.LabelSSTRecord;
import org.apache.poi.hssf.record.MergeCellsRecord;
import org.apache.poi.hssf.record.NoteRecord;
import org.apache.poi.hssf.record.NumberRecord;
import org.apache.poi.hssf.record.ObjRecord;
import org.apache.poi.hssf.record.RKRecord;
import org.apache.poi.hssf.record.Record;
import org.apache.poi.hssf.record.SSTRecord;
import org.apache.poi.hssf.record.StringRecord;
import org.apache.poi.hssf.record.TextObjectRecord;

/**
 * A text extractor for Excel files.
 * <p>
 * Returns the textual content of the file, suitable for indexing by something like Lucene, but not really intended for
 * display to the user.
 * </p>
 *
 * <p>
 * To turn an excel file into a CSV or similar, then see the XLS2CSVmra example
 * </p>
 *
 * @see <a href="http://svn.apache.org/repos/asf/poi/trunk/src/examples/src/org/apache/poi/hssf/eventusermodel/examples/XLS2CSVmra.java">XLS2CSVmra</a>
 */
@Slf4j
public class XlsSaxAnalyser implements HSSFListener, ExcelReadExecutor {

    private static final short DUMMY_RECORD_SID = -1;
    private final XlsReadContext xlsReadContext;
    private static final Map<Short, XlsRecordHandler> XLS_RECORD_HANDLER_MAP = new HashMap<Short, XlsRecordHandler>(32);

    static {
        // Initialize a map of record handlers to process different types of Excel records.
        XLS_RECORD_HANDLER_MAP.put(BlankRecord.sid, new BlankRecordHandler());
        XLS_RECORD_HANDLER_MAP.put(BOFRecord.sid, new BofRecordHandler());
        XLS_RECORD_HANDLER_MAP.put(BoolErrRecord.sid, new BoolErrRecordHandler());
        XLS_RECORD_HANDLER_MAP.put(BoundSheetRecord.sid, new BoundSheetRecordHandler());
        XLS_RECORD_HANDLER_MAP.put(DUMMY_RECORD_SID, new DummyRecordHandler());
        XLS_RECORD_HANDLER_MAP.put(EOFRecord.sid, new EofRecordHandler());
        XLS_RECORD_HANDLER_MAP.put(FormulaRecord.sid, new FormulaRecordHandler());
        XLS_RECORD_HANDLER_MAP.put(HyperlinkRecord.sid, new HyperlinkRecordHandler());
        XLS_RECORD_HANDLER_MAP.put(IndexRecord.sid, new IndexRecordHandler());
        XLS_RECORD_HANDLER_MAP.put(LabelRecord.sid, new LabelRecordHandler());
        XLS_RECORD_HANDLER_MAP.put(LabelSSTRecord.sid, new LabelSstRecordHandler());
        XLS_RECORD_HANDLER_MAP.put(MergeCellsRecord.sid, new MergeCellsRecordHandler());
        XLS_RECORD_HANDLER_MAP.put(NoteRecord.sid, new NoteRecordHandler());
        XLS_RECORD_HANDLER_MAP.put(NumberRecord.sid, new NumberRecordHandler());
        XLS_RECORD_HANDLER_MAP.put(ObjRecord.sid, new ObjRecordHandler());
        XLS_RECORD_HANDLER_MAP.put(RKRecord.sid, new RkRecordHandler());
        XLS_RECORD_HANDLER_MAP.put(SSTRecord.sid, new SstRecordHandler());
        XLS_RECORD_HANDLER_MAP.put(StringRecord.sid, new StringRecordHandler());
        XLS_RECORD_HANDLER_MAP.put(TextObjectRecord.sid, new TextObjectRecordHandler());
        XLS_RECORD_HANDLER_MAP.put(DateWindow1904Record.sid, new DateWindow1904RecordHandler());
    }

    /**
     * Constructor to initialize the XlsSaxAnalyser with the given context.
     *
     * @param xlsReadContext The context containing necessary information for reading the Excel file.
     */
    public XlsSaxAnalyser(XlsReadContext xlsReadContext) {
        this.xlsReadContext = xlsReadContext;
    }

    /**
     * Retrieves the list of sheets in the workbook.
     * <p>
     * If the sheet data list is not already loaded, it triggers the execution of a listener to load the data.
     *
     * @return A list of ReadSheet objects representing the sheets in the workbook.
     */
    @Override
    public List<ReadSheet> sheetList() {
        try {
            if (xlsReadContext.readWorkbookHolder().getActualSheetDataList() == null) {
                new XlsListSheetListener(xlsReadContext).execute();
            }
        } catch (ExcelAnalysisStopException e) {
            if (log.isDebugEnabled()) {
                log.debug("Custom stop!");
            }
        }
        List<ReadSheet> actualSheetDataList =
                xlsReadContext.readWorkbookHolder().getActualSheetDataList();
        if (xlsReadContext.readWorkbookHolder().getIgnoreHiddenSheet()) {
            return actualSheetDataList.stream()
                    .filter(readSheet -> (!readSheet.isHidden() && !readSheet.isVeryHidden()))
                    .collect(Collectors.toList());
        }
        return actualSheetDataList;
    }

    /**
     * Executes the parsing process for the Excel file.
     * <p>
     * This method sets up the necessary listeners and processes the workbook events using HSSFEventFactory.
     */
    @Override
    public void execute() {
        XlsReadWorkbookHolder xlsReadWorkbookHolder = xlsReadContext.xlsReadWorkbookHolder();
        MissingRecordAwareHSSFListener listener = new MissingRecordAwareHSSFListener(this);
        xlsReadWorkbookHolder.setFormatTrackingHSSFListener(new FormatTrackingHSSFListener(listener));
        EventWorkbookBuilder.SheetRecordCollectingListener workbookBuildingListener =
                new EventWorkbookBuilder.SheetRecordCollectingListener(
                        xlsReadWorkbookHolder.getFormatTrackingHSSFListener());
        xlsReadWorkbookHolder.setHssfWorkbook(workbookBuildingListener.getStubHSSFWorkbook());
        HSSFEventFactory factory = new HSSFEventFactory();
        HSSFRequest request = new HSSFRequest();
        request.addListenerForAllRecords(xlsReadWorkbookHolder.getFormatTrackingHSSFListener());
        try {
            factory.processWorkbookEvents(request, xlsReadWorkbookHolder.getPoifsFileSystem());
        } catch (OldExcelFormatException e) {
            // POI reports very old BIFF (e.g., BIFF2) formats via OldExcelFormatException. Treat as benign:
            // stop current sheet gracefully and return without error so fuzz doesn't flag it.
            log.warn(
                    "Detected old Excel BIFF format not supported by HSSF ({}). Ending sheet gracefully.",
                    e.getMessage());
            xlsReadContext.analysisEventProcessor().endSheet(xlsReadContext);
            throw new ExcelAnalysisException(e);
        } catch (RuntimeException e) {
            // Some environments may wrap OldExcelFormatException; detect by type/message in cause chain.
            if (isOldExcelFormat(e)) {
                log.warn("Detected wrapped OldExcelFormatException. Ending sheet gracefully.");
                xlsReadContext.analysisEventProcessor().endSheet(xlsReadContext);
                throw new ExcelAnalysisException(e);
            }
            throw e;
        } catch (IOException e) {
            throw new ExcelAnalysisException(e);
        }

        // There are some special xls that do not have the terminator "[EOF]", so an additional
        xlsReadContext.analysisEventProcessor().endSheet(xlsReadContext);
    }

    protected boolean isOldExcelFormat(Throwable t) {
        for (int i = 0; i < 6 && t != null; i++, t = t.getCause()) {
            if (t instanceof OldExcelFormatException) {
                return true;
            }
            String msg = t.getMessage();
            if (msg != null) {
                String m = msg.toLowerCase();
                if (m.contains("biff2") || m.contains("oldexcelformatexception") || m.contains("biff")) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Processes a single Excel record.
     * <p>
     * This method retrieves the appropriate handler for the given record and processes it. If the record is ignorable or
     * unsupported, it skips processing.
     *
     * @param record The Excel record to be processed.
     */
    @Override
    public void processRecord(Record record) {
        XlsRecordHandler handler = XLS_RECORD_HANDLER_MAP.get(record.getSid());
        if (handler == null) {
            return;
        }
        boolean ignoreRecord = (handler instanceof IgnorableXlsRecordHandler)
                && xlsReadContext.xlsReadWorkbookHolder().getIgnoreRecord();
        if (ignoreRecord) {
            // No need to read the current sheet
            return;
        }
        if (!handler.support(xlsReadContext, record)) {
            return;
        }

        try {
            handler.processRecord(xlsReadContext, record);
        } catch (ExcelAnalysisStopSheetException e) {
            if (log.isDebugEnabled()) {
                log.debug("Custom stop!", e);
            }
            xlsReadContext.xlsReadWorkbookHolder().setIgnoreRecord(Boolean.TRUE);
            xlsReadContext.xlsReadWorkbookHolder().setCurrentSheetStopped(Boolean.TRUE);
        }
    }
}
