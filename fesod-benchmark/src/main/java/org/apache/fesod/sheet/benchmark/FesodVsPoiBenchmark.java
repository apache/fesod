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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
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
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
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

/**
 * Side-by-side comparison of Fesod and Apache POI on the same data, using each
 * library's plain (in-memory) APIs: write via {@code ExcelWriter} vs an
 * XSSF/HSSF {@code Workbook}, read via the Fesod streaming reader vs POI's
 * DOM-based {@code WorkbookFactory}. The streaming-API comparison (POI SAX /
 * SXSSF) lives in {@link StreamingBenchmark}.
 *
 * <p>Fair-comparison measures applied:
 * <ul>
 *   <li>Both libraries write and read the same 20 columns</li>
 *   <li>Test data is generated with a fixed seed and pre-written in {@code @Setup},
 *       so measurement only covers the operation under test</li>
 *   <li>Fixed heap size via {@code @Fork} JVM args for stable GC behavior</li>
 * </ul>
 *
 * <p><b>Workload symmetry.</b> The write path is symmetric: both sides convert the
 * same in-memory objects into the same 20 columns. The read path is deliberately
 * asymmetric, mirroring each library's idiomatic end-to-end usage: Fesod runs its
 * full pipeline (parse + type conversion such as {@code BigDecimal}/dates +
 * reflective {@code BenchmarkData} instantiation), while the POI side consumes raw
 * cell text — POI has no built-in row-object mapping, and hand-rolling one in the
 * benchmark would measure benchmark code rather than either library. The asymmetry
 * is conservative for Fesod: the POI side does strictly less work per row, so the
 * comparison cannot overstate Fesod's advantage.
 *
 * <p>XLS is limited to 65,536 rows per sheet, so datasets are truncated to 65,534
 * data rows for that format. Run through the shaded {@code benchmarks.jar} as
 * described in {@code fesod-benchmark/benchmark.md}.
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
@Warmup(iterations = 3, time = 5, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 5, timeUnit = TimeUnit.SECONDS)
@Fork(
        value = 1,
        jvmArgs = {"-Xms2g", "-Xmx2g"})
public class FesodVsPoiBenchmark {

    /** XLS (BIFF8) supports at most 65,536 rows per sheet; keep one row for the header. */
    private static final int XLS_MAX_DATA_ROWS = 65534;

    @Param({"SMALL", "MEDIUM", "LARGE"})
    private String datasetSize;

    @Param({"XLSX", "XLS"})
    private String fileFormat;

    private File testFile;
    private File fesodWriteFile;
    private File poiWriteFile;
    private List<BenchmarkData> testDataList;
    private BenchmarkConfiguration.FileFormat format;
    private String extension;

    @Setup(Level.Trial)
    public void setupTrial() {
        // Allow POI to load the larger files generated by this suite
        IOUtils.setByteArrayMaxOverride(1024 * 1024 * 1024);

        format = BenchmarkConfiguration.FileFormat.valueOf(fileFormat);
        extension = format.getExtension();

        BenchmarkConfiguration.DatasetSize size = BenchmarkConfiguration.DatasetSize.valueOf(datasetSize);
        testDataList = DataGenerator.generateTestData(size);

        if (format == BenchmarkConfiguration.FileFormat.XLS && testDataList.size() > XLS_MAX_DATA_ROWS) {
            System.out.printf(
                    "WARN: XLS supports max %d data rows, truncating from %d rows.%n",
                    XLS_MAX_DATA_ROWS, testDataList.size());
            testDataList = new ArrayList<>(testDataList.subList(0, XLS_MAX_DATA_ROWS));
        }

        testFile = BenchmarkFileUtil.createTestFile(
                String.format("comparison_%s_%s.%s", datasetSize.toLowerCase(), extension, extension));

        // Pre-populate the test file used by the read benchmarks
        FesodSheet.write(testFile, BenchmarkData.class).sheet("TestData").doWrite(testDataList);

        // Fixed targets for the write benchmarks: every invocation overwrites the
        // same file, and cleanup happens once in tearDownTrial so no I/O other
        // than the write itself is part of the measured op.
        fesodWriteFile = BenchmarkFileUtil.createTestFile(
                String.format("fesod_vs_poi_fesod_write_%s.%s", datasetSize.toLowerCase(), extension));
        poiWriteFile = BenchmarkFileUtil.createTestFile(
                String.format("fesod_vs_poi_poi_write_%s.%s", datasetSize.toLowerCase(), extension));

        System.out.printf("FesodVsPoiBenchmark setup: %s format, %d rows%n", fileFormat, testDataList.size());
    }

    @TearDown(Level.Trial)
    public void tearDownTrial() {
        BenchmarkFileUtil.delete(testFile);
        BenchmarkFileUtil.delete(fesodWriteFile);
        BenchmarkFileUtil.delete(poiWriteFile);
    }

    // ============================================================================
    // WRITE
    // ============================================================================

    /**
     * Write the whole dataset with Fesod.
     */
    @Benchmark
    public long fesodWrite(Blackhole blackhole) {
        try (ExcelWriter excelWriter =
                FesodSheet.write(fesodWriteFile, BenchmarkData.class).build()) {
            WriteSheet writeSheet = FesodSheet.writerSheet("TestData").build();
            excelWriter.write(testDataList, writeSheet);
            blackhole.consume(fesodWriteFile.length());
        }
        return testDataList.size();
    }

    /**
     * Write the same columns with Apache POI.
     */
    @Benchmark
    public long poiWrite(Blackhole blackhole) throws Exception {
        try (FileOutputStream fos = new FileOutputStream(poiWriteFile);
                Workbook workbook = createWorkbook()) {
            Sheet sheet = workbook.createSheet("TestData");

            Row headerRow = sheet.createRow(0);
            String[] headers = {
                "ID", "String Data", "Integer Value", "Long Value", "Double Value",
                "BigDecimal Value", "Boolean Flag", "Date Value", "DateTime Value", "Category",
                "Description", "Status", "Float Value", "Short Value", "Byte Value",
                "Extra Data 1", "Extra Data 2", "Extra Data 3", "Extra Data 4", "Extra Data 5"
            };
            for (int i = 0; i < headers.length; i++) {
                headerRow.createCell(i).setCellValue(headers[i]);
            }

            for (int i = 0; i < testDataList.size(); i++) {
                BenchmarkData data = testDataList.get(i);
                Row row = sheet.createRow(i + 1);

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
                        .setCellValue(
                                data.getDateValue() != null
                                        ? data.getDateValue().toString()
                                        : "");
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

            workbook.write(fos);
            blackhole.consume(poiWriteFile.length());
        }
        return testDataList.size();
    }

    // ============================================================================
    // READ
    // ============================================================================

    /**
     * Stream the whole file with the Fesod reader.
     */
    @Benchmark
    public long fesodRead(Blackhole blackhole) {
        AtomicLong processedRows = new AtomicLong(0);

        try (ExcelReader excelReader = FesodSheet.read(
                        testFile, BenchmarkData.class, new ReadListener<BenchmarkData>() {
                            @Override
                            public void invoke(BenchmarkData data, AnalysisContext context) {
                                processedRows.incrementAndGet();
                                blackhole.consume(data);
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

    /**
     * Load the whole workbook with Apache POI (the DOM-based approach most POI
     * applications use).
     */
    @Benchmark
    public long poiRead(Blackhole blackhole) throws Exception {
        long processedRows = 0;

        try (FileInputStream fis = new FileInputStream(testFile);
                Workbook workbook = WorkbookFactory.create(fis)) {
            Sheet sheet = workbook.getSheetAt(0);

            for (Row row : sheet) {
                if (row.getRowNum() == 0) {
                    continue; // skip header
                }
                for (Cell cell : row) {
                    blackhole.consume(cell.toString());
                }
                processedRows++;
            }
        }
        return processedRows;
    }

    // ============================================================================
    // UTILITY METHODS
    // ============================================================================

    private Workbook createWorkbook() {
        return format == BenchmarkConfiguration.FileFormat.XLSX ? new XSSFWorkbook() : new HSSFWorkbook();
    }
}
