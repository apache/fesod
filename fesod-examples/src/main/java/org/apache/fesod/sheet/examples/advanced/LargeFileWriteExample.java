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

package org.apache.fesod.sheet.examples.advanced;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.fesod.sheet.ExcelWriter;
import org.apache.fesod.sheet.FesodSheet;
import org.apache.fesod.sheet.examples.util.ExampleFileUtil;
import org.apache.fesod.sheet.examples.write.data.DemoData;
import org.apache.fesod.sheet.util.FileUtils;
import org.apache.fesod.sheet.write.handler.WorkbookWriteHandler;
import org.apache.fesod.sheet.write.handler.context.WorkbookWriteHandlerContext;
import org.apache.fesod.sheet.write.metadata.WriteSheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

/**
 * Demonstrates writing very large Excel files (100,000+ rows) with memory optimization.
 *
 * <h2>Scenario</h2>
 * <p>You need to export a large dataset (e.g., database dump, log analysis) that would
 * exhaust memory if all rows were held at once. Fesod uses Apache POI's streaming
 * API (SXSSF) internally, but temporary XML files can consume significant disk space.</p>
 *
 * <h2>Key Optimization: Temporary File Compression</h2>
 * <p>When POI writes large XLSX files, it creates temporary XML files on disk
 * (one per sheet). These can be several times larger than the final file.
 * Enabling compression via {@code setCompressTempFiles(true)} significantly reduces
 * disk usage at the cost of slightly more CPU.</p>
 *
 * <h2>Architecture</h2>
 * <pre>
 * Data (in memory, batched)        Fesod          POI/SXSSF
 *     │                              │                 │
 *     ├─ 100 rows batch ───────────▶ write() ──────▶ temp XML (compressed)
 *     ├─ 100 rows batch ───────────▶ write() ──────▶ temp XML (append)
 *     │  ... (1000 batches)           │                 │
 *     └─ close() ──────────────────▶ finalize ─────▶ final .xlsx
 * </pre>
 *
 * <h2>Performance Tips</h2>
 * <ul>
 *   <li>Use {@code ExcelWriter} (try-with-resources) for batch writing instead of
 *       loading all data with {@code doWrite()}.</li>
 *   <li>Enable temp file compression for disk-constrained environments.</li>
 *   <li>Tune batch size (100 rows here) based on your row width and available memory.</li>
 *   <li>Monitor temp directory size: {@code FileUtils.getPoiFilesPath()}.</li>
 * </ul>
 *
 * <h2>Expected Result</h2>
 * <p>Writes 100,000 rows (1000 batches x 100 rows) to a single sheet without
 * OutOfMemoryError, using compressed temp files on disk.</p>
 *
 * <h2>Related Examples</h2>
 * <ul>
 *   <li>{@link org.apache.fesod.sheet.examples.write.BasicWriteExample} — Simple small-file write.</li>
 * </ul>
 *
 * @see ExcelWriter
 * @see org.apache.poi.xssf.streaming.SXSSFWorkbook#setCompressTempFiles(boolean)
 */
@Slf4j
public class LargeFileWriteExample {

    public static void main(String[] args) {
        compressedTemporaryFile();
    }

    /**
     * Writes 100,000 rows in batches with compressed temporary files.
     *
     * <p>Uses a {@link WorkbookWriteHandler} to access the underlying POI
     * {@link SXSSFWorkbook} and enable temp file compression. Writing is done
     * in 1,000 batches of 100 rows each via the {@link ExcelWriter} API.</p>
     */
    public static void compressedTemporaryFile() {
        log.info("Temporary XML files are stored at: {}", FileUtils.getPoiFilesPath());
        String fileName = ExampleFileUtil.getTempPath("largeFile" + System.currentTimeMillis() + ".xlsx");

        try (ExcelWriter excelWriter = FesodSheet.write(fileName, DemoData.class)
                .registerWriteHandler(new WorkbookWriteHandler() {
                    @Override
                    public void afterWorkbookCreate(WorkbookWriteHandlerContext context) {
                        Workbook workbook = context.getWriteWorkbookHolder().getWorkbook();
                        if (workbook instanceof SXSSFWorkbook) {
                            // Enable temporary file compression.
                            ((SXSSFWorkbook) workbook).setCompressTempFiles(true);
                        }
                    }
                })
                .build()) {
            WriteSheet writeSheet = FesodSheet.writerSheet("Template").build();
            // Write 100,000 rows in batches.
            for (int i = 0; i < 1000; i++) {
                excelWriter.write(data(), writeSheet);
            }
        }
        log.info("Successfully wrote large file: {}", fileName);
    }

    private static List<DemoData> data() {
        List<DemoData> list = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            DemoData data = new DemoData();
            data.setString("String" + i);
            data.setDate(new Date());
            data.setDoubleData(0.56);
            list.add(data);
        }
        return list;
    }
}
