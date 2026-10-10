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

package org.apache.fesod.sheet.read;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.fesod.sheet.FesodSheet;
import org.apache.fesod.sheet.context.AnalysisContext;
import org.apache.fesod.sheet.event.AnalysisEventListener;
import org.apache.fesod.sheet.support.ExcelTypeEnum;
import org.apache.fesod.sheet.testkit.Tags;
import org.apache.fesod.sheet.testkit.base.AbstractExcelTest;
import org.apache.poi.hssf.record.BOFRecord;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * Workbook-global substreams that are not worksheets (charts, Excel 4 macro sheets, VB modules) carry their own
 * BOUNDSHEET entry, so they occupy a slot in the sheet list. Reading must skip their records without shifting the
 * pairing between worksheet substreams and their BOUNDSHEET entries.
 */
@Tag(Tags.READ)
public class XlsNonWorksheetSubstreamTest extends AbstractExcelTest {

    @Test
    public void readWorksheetFollowingAChartSubstream() throws Exception {
        File file = flipFirstSheetSubstreamToChart();

        List<Map<Integer, String>> rows = FesodSheet.read(file)
                .excelType(ExcelTypeEnum.XLS)
                .headRowNumber(0)
                .sheet(1)
                .doReadSync();

        Assertions.assertEquals(1, rows.size(), "the worksheet after the chart substream must be read");
        Assertions.assertEquals("300", rows.get(0).get(0));
    }

    @Test
    public void readAllSkipsTheChartSubstreamAndKeepsSheetPairing() throws Exception {
        File file = flipFirstSheetSubstreamToChart();

        List<String> readSheetNames = new ArrayList<>();
        List<Map<Integer, String>> allRows = new ArrayList<>();
        FesodSheet.read(file, new AnalysisEventListener<Map<Integer, String>>() {
                    @Override
                    public void invoke(Map<Integer, String> data, AnalysisContext context) {
                        readSheetNames.add(context.readSheetHolder().getSheetName());
                        allRows.add(data);
                    }

                    @Override
                    public void doAfterAllAnalysed(AnalysisContext context) {}
                })
                .excelType(ExcelTypeEnum.XLS)
                .headRowNumber(0)
                .doReadAll();

        Assertions.assertEquals(1, allRows.size(), "only the worksheet carries grid data");
        Assertions.assertEquals("sheet1", readSheetNames.get(0), "the worksheet data must land under its own sheet");
        Assertions.assertEquals("300", allRows.get(0).get(0));
    }

    @Test
    public void readUnmodifiedWorkbookWorksAsBefore() throws Exception {
        File file = writeTwoSheetWorkbook();

        List<Map<Integer, String>> rows = FesodSheet.read(file)
                .excelType(ExcelTypeEnum.XLS)
                .headRowNumber(0)
                .sheet(1)
                .doReadSync();

        Assertions.assertEquals(1, rows.size());
        Assertions.assertEquals("300", rows.get(0).get(0));
    }

    /**
     * Builds a two-sheet workbook and rewrites the Workbook stream so the first sheet substream declares itself as a
     * chart (BOF type 0x0020) while keeping its original cell records - the layout Excel writes when a chart sheet
     * precedes a worksheet.
     */
    private File flipFirstSheetSubstreamToChart() throws Exception {
        byte[] original = writeTwoSheetWorkbookBytes();

        POIFSFileSystem fs = new POIFSFileSystem(new ByteArrayInputStream(original));
        ByteArrayOutputStream workbookStream = new ByteArrayOutputStream();
        fs.createDocumentInputStream("Workbook").transferTo(workbookStream);
        fs.close();

        ByteBuffer buf = ByteBuffer.wrap(workbookStream.toByteArray()).order(ByteOrder.LITTLE_ENDIAN);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int bofCount = 0;
        while (buf.remaining() >= 4) {
            int sid = buf.getShort() & 0xFFFF;
            int size = buf.getShort() & 0xFFFF;
            if (buf.remaining() < size) {
                break;
            }
            byte[] body = new byte[size];
            buf.get(body);
            if (sid == BOFRecord.sid && size >= 8) {
                bofCount++;
                // The second BOF in the stream is the first sheet substream; its document type sits at body offset 2.
                if (bofCount == 2) {
                    body[2] = (byte) BOFRecord.TYPE_CHART;
                    body[3] = 0;
                }
            }
            out.write(sid & 0xFF);
            out.write((sid >> 8) & 0xFF);
            out.write(size & 0xFF);
            out.write((size >> 8) & 0xFF);
            out.write(body);
        }

        POIFSFileSystem target = new POIFSFileSystem(new ByteArrayInputStream(original));
        target.getRoot().getEntry("Workbook").delete();
        target.getRoot().createDocument("Workbook", new ByteArrayInputStream(out.toByteArray()));
        File file = createTempFile("chartSubstream", org.apache.fesod.sheet.testkit.enums.ExcelFormat.XLS);
        try (FileOutputStream fileOutput = new FileOutputStream(file)) {
            target.writeFilesystem(fileOutput);
        }
        target.close();
        return file;
    }

    private File writeTwoSheetWorkbook() throws Exception {
        File file = createTempFile("twoSheetWorkbook", org.apache.fesod.sheet.testkit.enums.ExcelFormat.XLS);
        try (FileOutputStream fileOutput = new FileOutputStream(file)) {
            fileOutput.write(writeTwoSheetWorkbookBytes());
        }
        return file;
    }

    private byte[] writeTwoSheetWorkbookBytes() throws Exception {
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet0 = workbook.createSheet("sheet0");
        HSSFRow row0 = sheet0.createRow(0);
        row0.createCell(0).setCellValue(100);
        row0.createCell(1).setCellValue(200);
        HSSFSheet sheet1 = workbook.createSheet("sheet1");
        HSSFRow row1 = sheet1.createRow(0);
        row1.createCell(0).setCellValue(300);
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        workbook.write(bytes);
        workbook.close();
        return bytes.toByteArray();
    }
}
