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

package org.apache.fesod.sheet.benchmark;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.fesod.sheet.ExcelReader;
import org.apache.fesod.sheet.ExcelWriter;
import org.apache.fesod.sheet.FesodSheet;
import org.apache.fesod.sheet.benchmark.data.BenchmarkData;
import org.apache.fesod.sheet.benchmark.util.BenchmarkFileUtil;
import org.apache.fesod.sheet.benchmark.util.DataGenerator;
import org.apache.fesod.sheet.context.AnalysisContext;
import org.apache.fesod.sheet.read.listener.ReadListener;
import org.apache.fesod.sheet.write.metadata.WriteSheet;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackageAccess;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.util.XMLHelper;
import org.apache.poi.xssf.eventusermodel.ReadOnlySharedStringsTable;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.eventusermodel.XSSFSheetXMLHandler;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFComment;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

/**
 * Streaming-API comparison for the large-file scenario, XLSX only: Fesod vs the
 * streaming APIs POI applications use when a workbook must not be fully held in
 * memory.
 *
 * <ul>
 *   <li>{@link #read}: Fesod's streaming reader vs POI's <b>SAX event model</b>
 *       ({@code XSSFReader} + {@code XSSFSheetXMLHandler}). Both stream every
 *       non-header cell's text, but the workloads are deliberately asymmetric,
 *       mirroring each library's idiomatic usage: Fesod runs its full pipeline
 *       (parse + type conversion + reflective {@code BenchmarkData} mapping),
 *       while the POI handler consumes raw formatted strings — POI has no
 *       built-in row-object mapping. The asymmetry is conservative for Fesod:
 *       the POI side does strictly less work per row.</li>
 *   <li>{@link #write}: Fesod writing row batches of 1000 through an
 *       {@code ExcelWriter} (its incremental write model) vs POI's
 *       <b>SXSSF</b> sliding-window workbook ({@code SXSSFWorkbook}, window of
 *       1000 rows).</li>
 * </ul>
 *
 * <p>XLSX only, because the scenario is large files — XLS (BIFF8) tops out at
 * 65,536 rows per sheet, and both the SAX event model and SXSSF are XSSF APIs.
 * The plain-API comparison lives in {@link FesodVsPoiBenchmark}.
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
@Warmup(iterations = 3, time = 5, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 5, timeUnit = TimeUnit.SECONDS)
@Fork(
        value = 1,
        jvmArgs = {"-Xms2g", "-Xmx2g"})
public class StreamingBenchmark {

    /** Row batch size for the Fesod batched write and the SXSSF window. */
    private static final int BATCH_ROWS = 1000;

    private static final String[] HEADERS = {
        "ID", "String Data", "Integer Value", "Long Value", "Double Value",
        "BigDecimal Value", "Boolean Flag", "Date Value", "DateTime Value", "Category",
        "Description", "Status", "Float Value", "Short Value", "Byte Value",
        "Extra Data 1", "Extra Data 2", "Extra Data 3", "Extra Data 4", "Extra Data 5"
    };

    @Param({"FESOD", "POI"})
    private String library;

    @Param({"SMALL", "MEDIUM", "LARGE"})
    private String datasetSize;

    private File testFile;
    private File writeFile;
    private List<BenchmarkData> testDataList;

    @Setup(Level.Trial)
    public void setupTrial() {
        BenchmarkConfiguration.DatasetSize size = BenchmarkConfiguration.DatasetSize.valueOf(datasetSize);
        testDataList = DataGenerator.generateTestData(size);

        testFile = BenchmarkFileUtil.createTestFile(String.format("streaming_read_%s.xlsx", datasetSize.toLowerCase()));
        FesodSheet.write(testFile, BenchmarkData.class).sheet("TestData").doWrite(testDataList);

        // Fixed target for the streaming-write benchmark: overwritten each
        // invocation, deleted in tearDownTrial so cleanup stays unmeasured.
        writeFile =
                BenchmarkFileUtil.createTestFile(String.format("streaming_write_%s.xlsx", datasetSize.toLowerCase()));

        System.out.printf("StreamingBenchmark setup: %s / %s / %d rows%n", library, datasetSize, testDataList.size());
    }

    @TearDown(Level.Trial)
    public void tearDownTrial() {
        BenchmarkFileUtil.delete(testFile);
        BenchmarkFileUtil.delete(writeFile);
    }

    @Benchmark
    public long read(Blackhole blackhole) throws Exception {
        if ("FESOD".equals(library)) {
            return fesodRead(blackhole);
        }
        return poiSaxRead(blackhole);
    }

    @Benchmark
    public long write(Blackhole blackhole) throws Exception {
        if ("FESOD".equals(library)) {
            return fesodStreamingWrite();
        }
        return poiSxssfWrite(blackhole);
    }

    // ============================================================================
    // STREAMING READ
    // ============================================================================

    private long fesodRead(Blackhole blackhole) {
        AtomicLong processedRows = new AtomicLong(0);

        try (ExcelReader excelReader = FesodSheet.read(
                        testFile, BenchmarkData.class, new ReadListener<BenchmarkData>() {
                            @Override
                            public void invoke(BenchmarkData row, AnalysisContext context) {
                                processedRows.incrementAndGet();
                                blackhole.consume(row);
                            }

                            @Override
                            public void doAfterAllAnalysed(AnalysisContext context) {
                                // no-op
                            }
                        })
                .build()) {
            excelReader.readAll();
        }
        return processedRows.get();
    }

    private long poiSaxRead(Blackhole blackhole) throws Exception {
        AtomicLong processedRows = new AtomicLong(0);

        try (OPCPackage pkg = OPCPackage.open(testFile, PackageAccess.READ)) {
            ReadOnlySharedStringsTable strings = new ReadOnlySharedStringsTable(pkg);
            XSSFReader xssfReader = new XSSFReader(pkg);
            XSSFReader.SheetIterator sheets = (XSSFReader.SheetIterator) xssfReader.getSheetsData();

            while (sheets.hasNext()) {
                try (InputStream sheetStream = sheets.next()) {
                    XSSFSheetXMLHandler.SheetContentsHandler cellHandler =
                            new XSSFSheetXMLHandler.SheetContentsHandler() {
                                private int currentRow = -1;

                                @Override
                                public void startRow(int rowNum) {
                                    currentRow = rowNum;
                                }

                                @Override
                                public void cell(String cellReference, String formattedValue, XSSFComment comment) {
                                    if (currentRow > 0) {
                                        blackhole.consume(formattedValue);
                                    }
                                }

                                @Override
                                public void endRow(int rowNum) {
                                    if (rowNum > 0) {
                                        processedRows.incrementAndGet();
                                    }
                                }
                            };

                    ContentHandler handler = new XSSFSheetXMLHandler(
                            xssfReader.getStylesTable(), strings, cellHandler, new DataFormatter(), false);

                    XMLReader parser = XMLHelper.newXMLReader();
                    parser.setContentHandler(handler);
                    parser.parse(new InputSource(sheetStream));
                }
            }
        }
        return processedRows.get();
    }

    // ============================================================================
    // STREAMING WRITE
    // ============================================================================

    private long fesodStreamingWrite() {
        try (ExcelWriter excelWriter =
                FesodSheet.write(writeFile, BenchmarkData.class).build()) {
            WriteSheet writeSheet = FesodSheet.writerSheet("TestData").build();
            for (int start = 0; start < testDataList.size(); start += BATCH_ROWS) {
                int end = Math.min(start + BATCH_ROWS, testDataList.size());
                excelWriter.write(testDataList.subList(start, end), writeSheet);
            }
        }
        return testDataList.size();
    }

    private long poiSxssfWrite(Blackhole blackhole) throws Exception {
        try (SXSSFWorkbook workbook = new SXSSFWorkbook(BATCH_ROWS)) {
            Sheet sheet = workbook.createSheet("TestData");

            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < HEADERS.length; i++) {
                headerRow.createCell(i).setCellValue(HEADERS[i]);
            }

            for (int i = 0; i < testDataList.size(); i++) {
                fillRow(sheet.createRow(i + 1), testDataList.get(i));
            }

            try (FileOutputStream fos = new FileOutputStream(writeFile)) {
                workbook.write(fos);
            }
        } // close() also disposes the SXSSF temp files
        blackhole.consume(writeFile.length());
        return testDataList.size();
    }

    private void fillRow(Row row, BenchmarkData data) {
        row.createCell(0).setCellValue(data.getId() != null ? data.getId() : 0);
        row.createCell(1).setCellValue(data.getStringData() != null ? data.getStringData() : "");
        row.createCell(2).setCellValue(data.getIntValue() != null ? data.getIntValue() : 0);
        row.createCell(3).setCellValue(data.getLongValue() != null ? data.getLongValue() : 0L);
        row.createCell(4).setCellValue(data.getDoubleValue() != null ? data.getDoubleValue() : 0.0);
        row.createCell(5)
                .setCellValue(
                        data.getBigDecimalValue() != null
                                ? data.getBigDecimalValue().doubleValue()
                                : 0.0);
        row.createCell(6).setCellValue(data.getBooleanFlag() != null ? data.getBooleanFlag() : false);
        row.createCell(7)
                .setCellValue(data.getDateValue() != null ? data.getDateValue().toString() : "");
        row.createCell(8)
                .setCellValue(
                        data.getDateTimeValue() != null
                                ? data.getDateTimeValue().toString()
                                : "");
        row.createCell(9).setCellValue(data.getCategory() != null ? data.getCategory() : "");
        row.createCell(10).setCellValue(data.getDescription() != null ? data.getDescription() : "");
        row.createCell(11).setCellValue(data.getStatus() != null ? data.getStatus() : "");
        row.createCell(12).setCellValue(data.getFloatValue() != null ? data.getFloatValue() : 0.0f);
        row.createCell(13).setCellValue(data.getShortValue() != null ? data.getShortValue() : 0);
        row.createCell(14).setCellValue(data.getByteValue() != null ? data.getByteValue() : 0);
        row.createCell(15).setCellValue(data.getExtraData1() != null ? data.getExtraData1() : "");
        row.createCell(16).setCellValue(data.getExtraData2() != null ? data.getExtraData2() : "");
        row.createCell(17).setCellValue(data.getExtraData3() != null ? data.getExtraData3() : "");
        row.createCell(18).setCellValue(data.getExtraData4() != null ? data.getExtraData4() : "");
        row.createCell(19).setCellValue(data.getExtraData5() != null ? data.getExtraData5() : "");
    }
}
