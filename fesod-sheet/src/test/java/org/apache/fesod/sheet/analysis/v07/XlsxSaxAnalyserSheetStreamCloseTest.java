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

package org.apache.fesod.sheet.analysis.v07;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.fesod.sheet.ExcelReader;
import org.apache.fesod.sheet.ExcelWriter;
import org.apache.fesod.sheet.FesodSheet;
import org.apache.fesod.sheet.context.AnalysisContext;
import org.apache.fesod.sheet.context.xlsx.DefaultXlsxReadContext;
import org.apache.fesod.sheet.context.xlsx.XlsxReadContext;
import org.apache.fesod.sheet.event.AnalysisEventListener;
import org.apache.fesod.sheet.read.metadata.ReadSheet;
import org.apache.fesod.sheet.read.metadata.ReadWorkbook;
import org.apache.fesod.sheet.support.ExcelTypeEnum;
import org.apache.fesod.sheet.testkit.Tags;
import org.apache.fesod.sheet.testkit.base.AbstractExcelTest;
import org.apache.fesod.sheet.testkit.builders.TestDataBuilder;
import org.apache.fesod.sheet.testkit.enums.ExcelFormat;
import org.apache.fesod.sheet.testkit.listeners.CollectingReadListener;
import org.apache.fesod.sheet.testkit.models.SimpleData;
import org.apache.fesod.sheet.util.FileUtils;
import org.apache.fesod.sheet.write.metadata.WriteSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * Verifies that {@link XlsxSaxAnalyser} closes sheet {@link InputStream}s that are skipped or left unread.
 */
@Tag(Tags.READ)
@Tag(Tags.ROUND_TRIP)
class XlsxSaxAnalyserSheetStreamCloseTest extends AbstractExcelTest {

    @Test
    void execute_keepsUnreadSheetStreamsOpen_untilReaderCloses() throws Exception {
        File file = writeThreeSheets();
        CollectingReadListener<SimpleData> listener = new CollectingReadListener<>();
        Map<Integer, CloseTrackingInputStream> tracked = null;

        try (ExcelReader excelReader =
                FesodSheet.read(file, SimpleData.class, listener).build()) {
            XlsxSaxAnalyser analyser = (XlsxSaxAnalyser) excelReader.excelExecutor();
            Map<Integer, InputStream> sheetMap = sheetMap(analyser);
            Assertions.assertEquals(3, sheetMap.size());

            tracked = wrapSheetStreams(sheetMap);
            excelReader.read(FesodSheet.readSheet(0).build());

            Assertions.assertEquals(1, listener.getRowCount());
            Assertions.assertEquals("sheet1", listener.getFirstRow().getName());
            Assertions.assertTrue(tracked.get(0).isClosed());
            Assertions.assertFalse(tracked.get(1).isClosed());
            Assertions.assertFalse(tracked.get(2).isClosed());

            excelReader.read(FesodSheet.readSheet(1).build());
            Assertions.assertEquals(2, listener.getRowCount());
            Assertions.assertTrue(tracked.get(1).isClosed());
            Assertions.assertFalse(tracked.get(2).isClosed());
        }

        Assertions.assertNotNull(tracked);
        assertAllClosed(tracked);
    }

    @Test
    void execute_closesRemainingSheetStreams_whenListenerThrows() throws Exception {
        File file = writeThreeSheets();
        AnalysisEventListener<SimpleData> failingListener = new AnalysisEventListener<SimpleData>() {
            @Override
            public void invoke(SimpleData data, AnalysisContext context) {
                throw new IllegalStateException("boom");
            }

            @Override
            public void doAfterAllAnalysed(AnalysisContext context) {
                // ignore code
            }
        };

        try (ExcelReader excelReader =
                FesodSheet.read(file, SimpleData.class, failingListener).build()) {
            XlsxSaxAnalyser analyser = (XlsxSaxAnalyser) excelReader.excelExecutor();
            Map<Integer, CloseTrackingInputStream> tracked = wrapSheetStreams(sheetMap(analyser));

            Assertions.assertThrows(IllegalStateException.class, excelReader::readAll);
            assertAllClosed(tracked);
        }
    }

    @Test
    void sequentialSheetReads_doNotCloseLaterSheetsEarly() {
        File file = writeThreeSheets();
        CollectingReadListener<SimpleData> listener = new CollectingReadListener<>();

        try (ExcelReader excelReader =
                FesodSheet.read(file, SimpleData.class, listener).build()) {
            excelReader.read(FesodSheet.readSheet(0).build());
            excelReader.read(FesodSheet.readSheet(1).build());
            excelReader.read(FesodSheet.readSheet(2).build());
        }

        Assertions.assertEquals(3, listener.getRowCount());
        Assertions.assertEquals("sheet1", listener.getRows().get(0).getName());
        Assertions.assertEquals("sheet2", listener.getRows().get(1).getName());
        Assertions.assertEquals("sheet3", listener.getRows().get(2).getName());
    }

    @Test
    void constructor_doesNotRetainHiddenSheetStreams_whenIgnoreHiddenSheet() throws Exception {
        File file = writeWorkbookWithHiddenSheet();

        try (ExcelReader excelReader =
                FesodSheet.read(file).ignoreHiddenSheet(Boolean.TRUE).build()) {
            XlsxSaxAnalyser analyser = (XlsxSaxAnalyser) excelReader.excelExecutor();
            List<ReadSheet> sheets = analyser.sheetList();
            Map<Integer, InputStream> sheetMap = sheetMap(analyser);

            Assertions.assertEquals(2, sheets.size());
            Assertions.assertEquals(2, sheetMap.size());
            Assertions.assertFalse(containsSheetName(sheets, "Hidden"));
            Assertions.assertTrue(containsSheetName(sheets, "Visible1"));
            Assertions.assertTrue(containsSheetName(sheets, "Visible2"));

            Map<Integer, CloseTrackingInputStream> tracked = wrapSheetStreams(sheetMap);
            excelReader.readAll();
            assertAllClosed(tracked);
        }
    }

    @Test
    void constructor_closesSkippedHiddenSheetStream_whenIgnoreHiddenSheet() throws Exception {
        File file = writeWorkbookWithHiddenSheet();
        ReadWorkbook readWorkbook = new ReadWorkbook();
        readWorkbook.setFile(file);
        readWorkbook.setIgnoreHiddenSheet(Boolean.TRUE);
        XlsxReadContext context = new DefaultXlsxReadContext(readWorkbook, ExcelTypeEnum.XLSX);
        TrackingXlsxSaxAnalyser analyser = new TrackingXlsxSaxAnalyser(context);
        try {
            Assertions.assertFalse(containsSheetName(analyser.sheetList(), "Hidden"));
            boolean hiddenClosed = false;
            for (String description : analyser.closedDescriptions()) {
                if (description != null && description.contains("Hidden")) {
                    hiddenClosed = true;
                    break;
                }
            }
            Assertions.assertTrue(hiddenClosed, "Skipped hidden sheet stream should be closed during construction");
        } finally {
            analyser.close();
            if (context.xlsxReadWorkbookHolder().getOpcPackage() != null) {
                context.xlsxReadWorkbookHolder().getOpcPackage().revert();
            }
            if (context.xlsxReadWorkbookHolder().getReadCache() != null) {
                context.xlsxReadWorkbookHolder().getReadCache().destroy();
            }
            if (context.xlsxReadWorkbookHolder().getTempFile() != null) {
                FileUtils.delete(context.xlsxReadWorkbookHolder().getTempFile());
            }
        }
    }

    private File writeThreeSheets() {
        File file = createTempFileUnchecked();
        try (ExcelWriter excelWriter = FesodSheet.write(file, SimpleData.class).build()) {
            WriteSheet sheet1 = FesodSheet.writerSheet(0, "Sheet1").build();
            WriteSheet sheet2 = FesodSheet.writerSheet(1, "Sheet2").build();
            WriteSheet sheet3 = FesodSheet.writerSheet(2, "Sheet3").build();
            excelWriter.write(namedData("sheet1"), sheet1);
            excelWriter.write(namedData("sheet2"), sheet2);
            excelWriter.write(namedData("sheet3"), sheet3);
        }
        return file;
    }

    private File writeWorkbookWithHiddenSheet() throws IOException {
        File file = createTempFileUnchecked();
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            workbook.createSheet("Visible1").createRow(0).createCell(0).setCellValue("a");
            workbook.createSheet("Hidden").createRow(0).createCell(0).setCellValue("b");
            workbook.createSheet("Visible2").createRow(0).createCell(0).setCellValue("c");
            workbook.setSheetHidden(1, true);
            try (FileOutputStream outputStream = new FileOutputStream(file)) {
                workbook.write(outputStream);
            }
        }
        return file;
    }

    private File createTempFileUnchecked() {
        try {
            return createTempFile(ExcelFormat.XLSX);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private static List<SimpleData> namedData(String name) {
        List<SimpleData> data = TestDataBuilder.simpleData(1);
        data.get(0).setName(name);
        return data;
    }

    private static boolean containsSheetName(List<ReadSheet> sheets, String sheetName) {
        for (ReadSheet sheet : sheets) {
            if (sheetName.equals(sheet.getSheetName())) {
                return true;
            }
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    private static Map<Integer, InputStream> sheetMap(XlsxSaxAnalyser analyser) throws Exception {
        Field field = XlsxSaxAnalyser.class.getDeclaredField("sheetMap");
        field.setAccessible(true);
        return (Map<Integer, InputStream>) field.get(analyser);
    }

    private static Map<Integer, CloseTrackingInputStream> wrapSheetStreams(Map<Integer, InputStream> sheetMap) {
        Map<Integer, CloseTrackingInputStream> tracked = new HashMap<>();
        for (Map.Entry<Integer, InputStream> entry : sheetMap.entrySet()) {
            CloseTrackingInputStream wrapped = new CloseTrackingInputStream(entry.getValue());
            tracked.put(entry.getKey(), wrapped);
            sheetMap.put(entry.getKey(), wrapped);
        }
        return tracked;
    }

    private static void assertAllClosed(Map<Integer, CloseTrackingInputStream> tracked) {
        Assertions.assertFalse(tracked.isEmpty());
        for (Map.Entry<Integer, CloseTrackingInputStream> entry : tracked.entrySet()) {
            Assertions.assertTrue(
                    entry.getValue().isClosed(), "Sheet stream should be closed, sheetNo=" + entry.getKey());
        }
    }

    private static final class CloseTrackingInputStream extends FilterInputStream {
        private boolean closed;

        private CloseTrackingInputStream(InputStream in) {
            super(in);
        }

        @Override
        public void close() throws IOException {
            closed = true;
            super.close();
        }

        private boolean isClosed() {
            return closed;
        }
    }

    private static final class TrackingXlsxSaxAnalyser extends XlsxSaxAnalyser {
        private List<String> closedDescriptions;

        private TrackingXlsxSaxAnalyser(XlsxReadContext xlsxReadContext) throws Exception {
            super(xlsxReadContext, null);
        }

        private List<String> closedDescriptions() {
            if (closedDescriptions == null) {
                closedDescriptions = new ArrayList<>();
            }
            return closedDescriptions;
        }

        @Override
        void closeSheetInputStream(InputStream inputStream, String description) {
            closedDescriptions().add(description);
            super.closeSheetInputStream(inputStream, description);
        }
    }
}
