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

package org.apache.fesod.sheet.benchmark.comparison;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.fesod.sheet.EasyExcel;
import org.apache.fesod.sheet.ExcelReader;
import org.apache.fesod.sheet.ExcelWriter;
import org.apache.fesod.sheet.benchmark.core.AbstractBenchmark;
import org.apache.fesod.sheet.benchmark.core.BenchmarkConfiguration;
import org.apache.fesod.sheet.benchmark.data.BenchmarkData;
import org.apache.fesod.sheet.benchmark.utils.BenchmarkFileUtil;
import org.apache.fesod.sheet.benchmark.utils.DataGenerator;
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
import org.openjdk.jmh.annotations.OperationsPerInvocation;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;

/**
 * Comprehensive comparison benchmarks between Fesod (EasyExcel) and Apache POI.
 * Tests performance across different operations and dataset sizes.
 *
 * <p>Benchmark best practices applied:
 * <ul>
 *   <li>No manual timing - JMH handles all timing measurements</li>
 *   <li>No System.gc() calls - avoids unpredictable pauses</li>
 *   <li>Fixed heap size via @Fork JVM args for stable GC behavior</li>
 *   <li>Fair comparison - both libraries write/read the same columns</li>
 *   <li>Pre-loaded data in @Setup to exclude I/O from measurements</li>
 * </ul>
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
@Warmup(iterations = 3, time = 5, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 5, timeUnit = TimeUnit.SECONDS)
@Fork(
        value = 1,
        jvmArgs = {"-Xms2g", "-Xmx2g"})
public class FastExcelVsPoiBenchmark extends AbstractBenchmark {

    @Param({"SMALL", "MEDIUM", "LARGE"})
    private String datasetSize;

    @Param({"XLSX", "XLS"})
    private String fileFormat;

    private File testFile;
    private List<BenchmarkData> testDataList;

    @Setup(Level.Trial)
    public void setupTrial() throws Exception {
        super.setupTrial();

        // Configure Apache POI to handle large files
        IOUtils.setByteArrayMaxOverride(1024 * 1024 * 1024); // 1GB

        // Generate test data using fixed seed for reproducibility
        BenchmarkConfiguration.DatasetSize size = BenchmarkConfiguration.DatasetSize.valueOf(datasetSize);
        int rowCount = size.getRowCount();
        testDataList = DataGenerator.generateTestData(size);

        BenchmarkConfiguration.FileFormat format = BenchmarkConfiguration.FileFormat.valueOf(fileFormat);
        if (format == BenchmarkConfiguration.FileFormat.XLS && rowCount > 65535) {
            System.out.printf(
                    "WARN: XLS format supports max 65536 rows, but dataset size is %d. Truncating to 65534 rows.%n",
                    rowCount);
            testDataList = testDataList.subList(0, 65534);
            rowCount = testDataList.size();
        }

        // Create test file for read benchmarks
        String fileName = String.format("comparison_%s.%s", datasetSize.toLowerCase(), fileFormat.toLowerCase());
        testFile = BenchmarkFileUtil.createTestFile(fileName);

        // Pre-populate test file using Fesod
        writeTestFile();

        System.out.printf("Setup comparison benchmark: %s format, %d rows%n", fileFormat, rowCount);
    }

    @TearDown(Level.Trial)
    public void tearDownTrial() throws Exception {
        if (testFile != null && testFile.exists()) {
            testFile.delete();
        }

        super.tearDownTrial();
    }

    @Override
    protected void setupBenchmark() throws Exception {
        // No additional setup needed
    }

    @Override
    protected void tearDownBenchmark() throws Exception {
        // No additional teardown needed
    }

    // ============================================================================
    // WRITE OPERATION BENCHMARKS
    // ============================================================================

    /**
     * Fesod (EasyExcel) write benchmark
     */
    @Benchmark
    @OperationsPerInvocation(1)
    public long benchmarkFesodWrite(Blackhole blackhole) {
        File outputFile = BenchmarkFileUtil.createTestFile(String.format(
                "fesod_write_%s_%s.%s",
                datasetSize.toLowerCase(),
                java.util.UUID.randomUUID().toString().substring(0, 8),
                fileFormat.toLowerCase()));

        try {
            ExcelWriter excelWriter =
                    EasyExcel.write(outputFile, BenchmarkData.class).build();
            WriteSheet writeSheet = EasyExcel.writerSheet("TestData").build();

            excelWriter.write(testDataList, writeSheet);
            excelWriter.finish();

            blackhole.consume(outputFile);
        } catch (Exception e) {
            throw new RuntimeException("Fesod write failed", e);
        } finally {
            if (outputFile.exists()) {
                outputFile.delete();
            }
        }

        return testDataList.size();
    }

    /**
     * Apache POI write benchmark - writes same columns as Fesod for fair comparison
     */
    @Benchmark
    @OperationsPerInvocation(1)
    public long benchmarkPoiWrite(Blackhole blackhole) {
        File outputFile = BenchmarkFileUtil.createTestFile(String.format(
                "poi_write_%s_%s.%s",
                datasetSize.toLowerCase(),
                java.util.UUID.randomUUID().toString().substring(0, 8),
                fileFormat.toLowerCase()));

        try (FileOutputStream fos = new FileOutputStream(outputFile)) {
            Workbook workbook = createWorkbook();
            Sheet sheet = workbook.createSheet("TestData");

            // Create header row matching BenchmarkData columns
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

            // Write data rows - same 20 columns as Fesod
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
                if (data.getDateValue() != null) {
                    row.createCell(7).setCellValue(data.getDateValue().toString());
                }
                if (data.getDateTimeValue() != null) {
                    row.createCell(8).setCellValue(data.getDateTimeValue().toString());
                }
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
            workbook.close();

            blackhole.consume(outputFile);

        } catch (Exception e) {
            throw new RuntimeException("POI write failed", e);
        } finally {
            if (outputFile.exists()) {
                outputFile.delete();
            }
        }

        return testDataList.size();
    }

    // ============================================================================
    // READ OPERATION BENCHMARKS
    // ============================================================================

    /**
     * Fesod (EasyExcel) read benchmark
     */
    @Benchmark
    @OperationsPerInvocation(1)
    public long benchmarkFesodRead(Blackhole blackhole) {
        AtomicLong processedRows = new AtomicLong(0);

        try {
            ExcelReader excelReader = EasyExcel.read(testFile, BenchmarkData.class, new ReadListener<BenchmarkData>() {
                        @Override
                        public void invoke(BenchmarkData data, AnalysisContext context) {
                            processedRows.incrementAndGet();
                            blackhole.consume(data);
                        }

                        @Override
                        public void doAfterAllAnalysed(AnalysisContext context) {
                            // Processing complete
                        }
                    })
                    .build();

            excelReader.readAll();
            excelReader.finish();

        } catch (Exception e) {
            throw new RuntimeException("Fesod read failed", e);
        }

        return processedRows.get();
    }

    /**
     * Apache POI read benchmark
     */
    @Benchmark
    @OperationsPerInvocation(1)
    public long benchmarkPoiRead(Blackhole blackhole) {
        long processedRows = 0;

        try (FileInputStream fis = new FileInputStream(testFile)) {
            Workbook workbook = WorkbookFactory.create(fis);
            Sheet sheet = workbook.getSheetAt(0);

            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue; // Skip header

                for (Cell cell : row) {
                    blackhole.consume(cell.toString());
                }

                processedRows++;
            }

            workbook.close();

        } catch (Exception e) {
            throw new RuntimeException("POI read failed", e);
        }

        return processedRows;
    }

    // ============================================================================
    // STREAMING OPERATION BENCHMARKS
    // ============================================================================

    /**
     * Fesod (EasyExcel) streaming read benchmark with batch processing
     */
    @Benchmark
    @OperationsPerInvocation(1)
    public long benchmarkFesodStreamingRead(Blackhole blackhole) {
        AtomicLong processedRows = new AtomicLong(0);
        List<BenchmarkData> batch = new ArrayList<>();
        int batchSize = 1000;

        try {
            ExcelReader excelReader = EasyExcel.read(testFile, BenchmarkData.class, new ReadListener<BenchmarkData>() {
                        @Override
                        public void invoke(BenchmarkData data, AnalysisContext context) {
                            batch.add(data);
                            processedRows.incrementAndGet();

                            if (batch.size() >= batchSize) {
                                blackhole.consume(new ArrayList<>(batch));
                                batch.clear();
                            }
                        }

                        @Override
                        public void doAfterAllAnalysed(AnalysisContext context) {
                            if (!batch.isEmpty()) {
                                blackhole.consume(batch);
                                batch.clear();
                            }
                        }
                    })
                    .build();

            excelReader.readAll();
            excelReader.finish();

        } catch (Exception e) {
            throw new RuntimeException("Fesod streaming read failed", e);
        }

        return processedRows.get();
    }

    /**
     * Apache POI streaming read benchmark using batch processing approach
     */
    @Benchmark
    @OperationsPerInvocation(1)
    public long benchmarkPoiStreamingRead(Blackhole blackhole) {
        long processedRows = 0;

        try (FileInputStream fis = new FileInputStream(testFile)) {
            Workbook workbook = WorkbookFactory.create(fis);
            Sheet sheet = workbook.getSheetAt(0);

            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue; // Skip header

                for (Cell cell : row) {
                    blackhole.consume(cell.toString());
                }

                processedRows++;
            }

            workbook.close();

        } catch (Exception e) {
            throw new RuntimeException("POI streaming read failed", e);
        }

        return processedRows;
    }

    // ============================================================================
    // UTILITY METHODS
    // ============================================================================

    /**
     * Create appropriate workbook based on file format
     */
    private Workbook createWorkbook() {
        return "XLSX".equals(fileFormat) ? new XSSFWorkbook() : new HSSFWorkbook();
    }

    /**
     * Write test data to file for read benchmarks
     */
    private void writeTestFile() {
        try {
            EasyExcel.write(testFile, BenchmarkData.class).sheet("TestData").doWrite(testDataList);
        } catch (Exception e) {
            throw new RuntimeException("Failed to write test file", e);
        }
    }
}
