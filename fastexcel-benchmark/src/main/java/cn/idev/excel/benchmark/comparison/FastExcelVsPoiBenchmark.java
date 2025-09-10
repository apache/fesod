package cn.idev.excel.benchmark.comparison;

import cn.idev.excel.EasyExcel;
import cn.idev.excel.ExcelReader;
import cn.idev.excel.ExcelWriter;
import cn.idev.excel.benchmark.core.AbstractBenchmark;
import cn.idev.excel.benchmark.core.BenchmarkConfiguration;
import cn.idev.excel.benchmark.data.BenchmarkData;
import cn.idev.excel.benchmark.utils.BenchmarkFileUtil;
import cn.idev.excel.benchmark.utils.DataGenerator;
import cn.idev.excel.benchmark.utils.MemoryProfiler;
import cn.idev.excel.context.AnalysisContext;
import cn.idev.excel.event.AnalysisEventListener;
import cn.idev.excel.write.metadata.WriteSheet;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
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
 * Comprehensive comparison benchmarks between FastExcel (EasyExcel) and Apache POI.
 * Tests performance across different operations and dataset sizes.
 */
@BenchmarkMode(Mode.All)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
@Warmup(iterations = 2, time = 3, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 3, time = 5, timeUnit = TimeUnit.SECONDS)
@Fork(1)
public class FastExcelVsPoiBenchmark extends AbstractBenchmark {

    @Param({"SMALL", "MEDIUM", "LARGE", "EXTRA_LARGE"})
    private String datasetSize;

    @Param({"XLSX", "XLS"})
    private String fileFormat;

    private File testFile;
    private List<BenchmarkData> testDataList;
    private MemoryProfiler memoryProfiler;

    @Setup(Level.Trial)
    public void setupTrial() throws Exception {
        super.setupTrial();

        // Generate test data
        BenchmarkConfiguration.DatasetSize size = BenchmarkConfiguration.DatasetSize.valueOf(datasetSize);
        int rowCount = size.getRowCount();
        testDataList = DataGenerator.generateTestData(size);

        // Create test file
        String fileName = String.format("comparison_%s.%s", datasetSize.toLowerCase(), fileFormat.toLowerCase());
        testFile = BenchmarkFileUtil.createTestFile(fileName);

        // Pre-populate test file
        writeTestFile();

        // Initialize memory profiler
        memoryProfiler = new MemoryProfiler();

        System.out.printf("Setup comparison benchmark: %s format, %d rows%n", fileFormat, rowCount);
    }

    @TearDown(Level.Trial)
    public void tearDownTrial() throws Exception {
        if (memoryProfiler != null) {
            memoryProfiler.shutdown();
        }

        if (testFile != null && testFile.exists()) {
            testFile.delete();
        }

        super.tearDownTrial();
    }

    protected void setupBenchmark() throws Exception {
        // Custom setup logic if needed
    }

    protected void tearDownBenchmark() throws Exception {
        // Custom teardown logic if needed
    }

    @Setup(Level.Invocation)
    public void setupInvocation() {
        // Clean memory state before each benchmark
        System.gc();
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // ============================================================================
    // WRITE OPERATION BENCHMARKS
    // ============================================================================

    /**
     * FastExcel write benchmark
     */
    @Benchmark
    public ComparisonResult benchmarkFastExcelWrite(Blackhole blackhole) {
        File outputFile = BenchmarkFileUtil.createTestFile(String.format(
                "fastexcel_write_%s_%d.%s",
                datasetSize.toLowerCase(), System.currentTimeMillis(), fileFormat.toLowerCase()));

        long startTime = System.currentTimeMillis();

        // Start memory profiling
        memoryProfiler.reset();
        memoryProfiler.start();
        long initialMemory = memoryProfiler.getUsedMemory();

        try {
            ExcelWriter excelWriter =
                    EasyExcel.write(outputFile, BenchmarkData.class).build();
            WriteSheet writeSheet = EasyExcel.writerSheet("TestData").build();

            excelWriter.write(testDataList, writeSheet);
            excelWriter.finish();

            blackhole.consume(outputFile);

        } catch (Exception e) {
            throw new RuntimeException("FastExcel write failed", e);
        } finally {
            if (outputFile.exists()) {
                outputFile.delete();
            }
        }

        // Stop memory profiling and get detailed stats
        memoryProfiler.stop();
        MemoryProfiler.MemorySnapshot snapshot = memoryProfiler.getSnapshot();
        MemoryProfiler.MemoryStatistics stats = memoryProfiler.getDetailedStatistics();

        long endTime = System.currentTimeMillis();

        return new ComparisonResult(
                "FastExcel",
                "Write",
                testDataList.size(),
                endTime - startTime,
                snapshot.getMaxUsedMemory(),
                stats.getAvgMemory(),
                snapshot.getMaxUsedMemory() - initialMemory,
                snapshot.getAllocatedMemory(),
                (int) snapshot.getGcCount(),
                (int) snapshot.getGcTime(),
                outputFile.length(),
                stats.getMinMemory(),
                stats.getStdDevMemory(),
                stats.getP95Memory(),
                snapshot.getMaxUsedMemory() > 0 ? (double) snapshot.getMaxUsedMemory() / stats.getAvgMemory() : 0.0);
    }

    /**
     * Apache POI write benchmark
     */
    @Benchmark
    public ComparisonResult benchmarkPoiWrite(Blackhole blackhole) {
        File outputFile = BenchmarkFileUtil.createTestFile(String.format(
                "poi_write_%s_%d.%s", datasetSize.toLowerCase(), System.currentTimeMillis(), fileFormat.toLowerCase()));

        long startTime = System.currentTimeMillis();

        // Start memory profiling
        memoryProfiler.reset();
        memoryProfiler.start();
        long initialMemory = memoryProfiler.getUsedMemory();

        try (FileOutputStream fos = new FileOutputStream(outputFile)) {
            Workbook workbook = createWorkbook();
            Sheet sheet = workbook.createSheet("TestData");

            // Create header row
            Row headerRow = sheet.createRow(0);
            String[] headers = {"ID", "String Value", "Int Value", "Double Value", "Date Value", "Boolean Value"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
            }

            // Write data rows
            for (int i = 0; i < testDataList.size(); i++) {
                BenchmarkData data = testDataList.get(i);
                Row row = sheet.createRow(i + 1);

                row.createCell(0).setCellValue(data.getId() != null ? data.getId() : 0);
                row.createCell(1).setCellValue(data.getStringData() != null ? data.getStringData() : "");
                row.createCell(2).setCellValue(data.getIntValue() != null ? data.getIntValue() : 0);
                row.createCell(3).setCellValue(data.getDoubleValue() != null ? data.getDoubleValue() : 0.0);
                if (data.getDateValue() != null) {
                    row.createCell(4).setCellValue(data.getDateValue());
                }
                row.createCell(5).setCellValue(data.getBooleanFlag() != null ? data.getBooleanFlag() : false);
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

        // Stop memory profiling and get detailed stats
        memoryProfiler.stop();
        MemoryProfiler.MemorySnapshot snapshot = memoryProfiler.getSnapshot();
        MemoryProfiler.MemoryStatistics stats = memoryProfiler.getDetailedStatistics();

        long endTime = System.currentTimeMillis();

        return new ComparisonResult(
                "Apache POI",
                "Write",
                testDataList.size(),
                endTime - startTime,
                snapshot.getMaxUsedMemory(),
                stats.getAvgMemory(),
                snapshot.getMaxUsedMemory() - initialMemory,
                snapshot.getAllocatedMemory(),
                (int) snapshot.getGcCount(),
                (int) snapshot.getGcTime(),
                outputFile.length(),
                stats.getMinMemory(),
                stats.getStdDevMemory(),
                stats.getP95Memory(),
                snapshot.getMaxUsedMemory() > 0 ? (double) snapshot.getMaxUsedMemory() / stats.getAvgMemory() : 0.0);
    }

    // ============================================================================
    // READ OPERATION BENCHMARKS
    // ============================================================================

    /**
     * FastExcel read benchmark
     */
    @Benchmark
    public ComparisonResult benchmarkFastExcelRead(Blackhole blackhole) {
        // First, create a test file with FastExcel
        createTestFileWithFastExcel();

        long startTime = System.currentTimeMillis();

        // Start memory profiling
        memoryProfiler.reset();
        memoryProfiler.start();
        long initialMemory = memoryProfiler.getUsedMemory();

        AtomicLong processedRows = new AtomicLong(0);

        try {
            ExcelReader excelReader = EasyExcel.read(
                            testFile, BenchmarkData.class, new AnalysisEventListener<BenchmarkData>() {
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
            throw new RuntimeException("FastExcel read failed", e);
        }

        // Stop memory profiling and get detailed stats
        memoryProfiler.stop();
        MemoryProfiler.MemorySnapshot snapshot = memoryProfiler.getSnapshot();
        MemoryProfiler.MemoryStatistics stats = memoryProfiler.getDetailedStatistics();

        long endTime = System.currentTimeMillis();

        return new ComparisonResult(
                "FastExcel",
                "Read",
                processedRows.get(),
                endTime - startTime,
                snapshot.getMaxUsedMemory(),
                stats.getAvgMemory(),
                snapshot.getMaxUsedMemory() - initialMemory,
                snapshot.getAllocatedMemory(),
                (int) snapshot.getGcCount(),
                (int) snapshot.getGcTime(),
                testFile.length(),
                stats.getMinMemory(),
                stats.getStdDevMemory(),
                stats.getP95Memory(),
                snapshot.getMaxUsedMemory() > 0 ? (double) snapshot.getMaxUsedMemory() / stats.getAvgMemory() : 0.0);
    }

    /**
     * Apache POI read benchmark
     */
    @Benchmark
    public ComparisonResult benchmarkPoiRead(Blackhole blackhole) {
        // First, create a test file with POI
        createTestFileWithPoi();

        long startTime = System.currentTimeMillis();

        // Start memory profiling
        memoryProfiler.reset();
        memoryProfiler.start();
        long initialMemory = memoryProfiler.getUsedMemory();

        long processedRows = 0;

        try (FileInputStream fis = new FileInputStream(testFile)) {
            Workbook workbook = createWorkbook();
            workbook = WorkbookFactory.create(fis);
            Sheet sheet = workbook.getSheetAt(0);

            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue; // Skip header

                // Read all cells
                for (Cell cell : row) {
                    blackhole.consume(cell.toString());
                }

                processedRows++;
            }

            workbook.close();

        } catch (Exception e) {
            throw new RuntimeException("POI read failed", e);
        }

        // Stop memory profiling and get detailed stats
        memoryProfiler.stop();
        MemoryProfiler.MemorySnapshot snapshot = memoryProfiler.getSnapshot();
        MemoryProfiler.MemoryStatistics stats = memoryProfiler.getDetailedStatistics();

        long endTime = System.currentTimeMillis();

        return new ComparisonResult(
                "Apache POI",
                "Read",
                processedRows,
                endTime - startTime,
                snapshot.getMaxUsedMemory(),
                stats.getAvgMemory(),
                snapshot.getMaxUsedMemory() - initialMemory,
                snapshot.getAllocatedMemory(),
                (int) snapshot.getGcCount(),
                (int) snapshot.getGcTime(),
                testFile.length(),
                stats.getMinMemory(),
                stats.getStdDevMemory(),
                stats.getP95Memory(),
                snapshot.getMaxUsedMemory() > 0 ? (double) snapshot.getMaxUsedMemory() / stats.getAvgMemory() : 0.0);
    }

    // ============================================================================
    // STREAMING OPERATION BENCHMARKS
    // ============================================================================

    /**
     * FastExcel streaming read benchmark
     */
    @Benchmark
    public ComparisonResult benchmarkFastExcelStreamingRead(Blackhole blackhole) {
        createTestFileWithFastExcel();

        long startTime = System.currentTimeMillis();

        // Start memory profiling
        memoryProfiler.reset();
        memoryProfiler.start();
        long initialMemory = memoryProfiler.getUsedMemory();

        AtomicLong processedRows = new AtomicLong(0);
        List<BenchmarkData> batch = new ArrayList<>();
        int batchSize = 1000;

        try {
            ExcelReader excelReader = EasyExcel.read(
                            testFile, BenchmarkData.class, new AnalysisEventListener<BenchmarkData>() {
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
            throw new RuntimeException("FastExcel streaming read failed", e);
        }

        // Stop memory profiling and get detailed stats
        memoryProfiler.stop();
        MemoryProfiler.MemorySnapshot snapshot = memoryProfiler.getSnapshot();
        MemoryProfiler.MemoryStatistics stats = memoryProfiler.getDetailedStatistics();

        long endTime = System.currentTimeMillis();

        return new ComparisonResult(
                "FastExcel",
                "StreamingRead",
                processedRows.get(),
                endTime - startTime,
                snapshot.getMaxUsedMemory(),
                stats.getAvgMemory(),
                snapshot.getMaxUsedMemory() - initialMemory,
                snapshot.getAllocatedMemory(),
                (int) snapshot.getGcCount(),
                (int) snapshot.getGcTime(),
                testFile.length(),
                stats.getMinMemory(),
                stats.getStdDevMemory(),
                stats.getP95Memory(),
                snapshot.getMaxUsedMemory() > 0 ? (double) snapshot.getMaxUsedMemory() / stats.getAvgMemory() : 0.0);
    }

    /**
     * Apache POI streaming read benchmark (using XSSF streaming)
     */
    @Benchmark
    public ComparisonResult benchmarkPoiStreamingRead(Blackhole blackhole) {
        createTestFileWithPoi();

        long startTime = System.currentTimeMillis();

        // Start memory profiling
        memoryProfiler.reset();
        memoryProfiler.start();
        long initialMemory = memoryProfiler.getUsedMemory();

        long processedRows = 0;

        try (FileInputStream fis = new FileInputStream(testFile)) {
            BenchmarkConfiguration.FileFormat format = BenchmarkConfiguration.FileFormat.valueOf(fileFormat);
            if (format == BenchmarkConfiguration.FileFormat.XLSX) {
                // Use XSSF streaming for XLSX files
                org.apache.poi.xssf.streaming.SXSSFWorkbook streamingWorkbook =
                        new org.apache.poi.xssf.streaming.SXSSFWorkbook();

                // Read with streaming approach
                Workbook workbook = WorkbookFactory.create(fis);
                Sheet sheet = workbook.getSheetAt(0);

                for (Row row : sheet) {
                    if (row.getRowNum() == 0) continue; // Skip header

                    for (Cell cell : row) {
                        blackhole.consume(cell.toString());
                    }

                    processedRows++;

                    // Simulate streaming by processing in batches
                    if (processedRows % 1000 == 0) {
                        System.gc(); // Force memory cleanup
                    }
                }

                workbook.close();
                streamingWorkbook.close();
            } else {
                // Regular POI for XLS files (no streaming support)
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
            }

        } catch (Exception e) {
            throw new RuntimeException("POI streaming read failed", e);
        }

        // Stop memory profiling and get detailed stats
        memoryProfiler.stop();
        MemoryProfiler.MemorySnapshot snapshot = memoryProfiler.getSnapshot();
        MemoryProfiler.MemoryStatistics stats = memoryProfiler.getDetailedStatistics();

        long endTime = System.currentTimeMillis();

        return new ComparisonResult(
                "Apache POI",
                "StreamingRead",
                processedRows,
                endTime - startTime,
                snapshot.getMaxUsedMemory(),
                stats.getAvgMemory(),
                snapshot.getMaxUsedMemory() - initialMemory,
                snapshot.getAllocatedMemory(),
                (int) snapshot.getGcCount(),
                (int) snapshot.getGcTime(),
                testFile.length(),
                stats.getMinMemory(),
                stats.getStdDevMemory(),
                stats.getP95Memory(),
                snapshot.getMaxUsedMemory() > 0 ? (double) snapshot.getMaxUsedMemory() / stats.getAvgMemory() : 0.0);
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

    /**
     * Create test file using FastExcel
     */
    private void createTestFileWithFastExcel() {
        try {
            EasyExcel.write(testFile, BenchmarkData.class).sheet("TestData").doWrite(testDataList);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create test file with FastExcel", e);
        }
    }

    /**
     * Create test file using Apache POI
     */
    private void createTestFileWithPoi() {
        try (FileOutputStream fos = new FileOutputStream(testFile)) {
            Workbook workbook = createWorkbook();
            Sheet sheet = workbook.createSheet("TestData");

            // Create header row
            Row headerRow = sheet.createRow(0);
            String[] headers = {"ID", "String Value", "Int Value", "Double Value", "Date Value", "Boolean Value"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
            }

            // Write data rows
            for (int i = 0; i < testDataList.size(); i++) {
                BenchmarkData data = testDataList.get(i);
                Row row = sheet.createRow(i + 1);

                row.createCell(0).setCellValue(data.getId() != null ? data.getId() : 0);
                row.createCell(1).setCellValue(data.getStringData() != null ? data.getStringData() : "");
                row.createCell(2).setCellValue(data.getIntValue() != null ? data.getIntValue() : 0);
                row.createCell(3).setCellValue(data.getDoubleValue() != null ? data.getDoubleValue() : 0.0);
                if (data.getDateValue() != null) {
                    row.createCell(4).setCellValue(data.getDateValue());
                }
                row.createCell(5).setCellValue(data.getBooleanFlag() != null ? data.getBooleanFlag() : false);
            }

            workbook.write(fos);
            workbook.close();

        } catch (IOException e) {
            throw new RuntimeException("Failed to create test file with POI", e);
        }
    }

    /**
     * Result class for comparison benchmarks
     */
    public static class ComparisonResult {
        public final String library;
        public final String operation;
        public final long processedRows;
        public final long executionTimeMs;
        public final long memoryUsageBytes;
        public final long peakMemoryUsageBytes;
        public final long avgMemoryUsageBytes;
        public final long memoryAllocatedBytes;
        public final long gcCount;
        public final long gcTimeMs;
        public final long fileSizeBytes;
        public final long minMemoryUsageBytes;
        public final long stdDevMemoryUsageBytes;
        public final long p95MemoryUsageBytes;
        public final double memoryGrowthRate;

        public ComparisonResult(
                String library,
                String operation,
                long processedRows,
                long executionTimeMs,
                long memoryUsageBytes,
                long fileSizeBytes) {
            this.library = library;
            this.operation = operation;
            this.processedRows = processedRows;
            this.executionTimeMs = executionTimeMs;
            this.memoryUsageBytes = memoryUsageBytes;
            this.peakMemoryUsageBytes = memoryUsageBytes;
            this.avgMemoryUsageBytes = memoryUsageBytes;
            this.memoryAllocatedBytes = 0;
            this.gcCount = 0;
            this.gcTimeMs = 0;
            this.fileSizeBytes = fileSizeBytes;
            this.minMemoryUsageBytes = memoryUsageBytes;
            this.stdDevMemoryUsageBytes = 0;
            this.p95MemoryUsageBytes = memoryUsageBytes;
            this.memoryGrowthRate = 0.0;
        }

        public ComparisonResult(
                String library,
                String operation,
                long processedRows,
                long executionTimeMs,
                long peakMemoryUsageBytes,
                long avgMemoryUsageBytes,
                long memoryUsageBytes,
                long memoryAllocatedBytes,
                long gcCount,
                long gcTimeMs,
                long fileSizeBytes) {
            this.library = library;
            this.operation = operation;
            this.processedRows = processedRows;
            this.executionTimeMs = executionTimeMs;
            this.peakMemoryUsageBytes = peakMemoryUsageBytes;
            this.avgMemoryUsageBytes = avgMemoryUsageBytes;
            this.memoryUsageBytes = memoryUsageBytes;
            this.memoryAllocatedBytes = memoryAllocatedBytes;
            this.gcCount = gcCount;
            this.gcTimeMs = gcTimeMs;
            this.fileSizeBytes = fileSizeBytes;
            this.minMemoryUsageBytes = avgMemoryUsageBytes;
            this.stdDevMemoryUsageBytes = 0;
            this.p95MemoryUsageBytes = peakMemoryUsageBytes;
            this.memoryGrowthRate = memoryUsageBytes > 0 ? (double) peakMemoryUsageBytes / memoryUsageBytes : 0.0;
        }

        public ComparisonResult(
                String library,
                String operation,
                long processedRows,
                long executionTimeMs,
                long peakMemoryUsageBytes,
                long avgMemoryUsageBytes,
                long memoryUsageBytes,
                long memoryAllocatedBytes,
                long gcCount,
                long gcTimeMs,
                long fileSizeBytes,
                long minMemoryUsageBytes,
                long stdDevMemoryUsageBytes,
                long p95MemoryUsageBytes,
                double memoryGrowthRate) {
            this.library = library;
            this.operation = operation;
            this.processedRows = processedRows;
            this.executionTimeMs = executionTimeMs;
            this.peakMemoryUsageBytes = peakMemoryUsageBytes;
            this.avgMemoryUsageBytes = avgMemoryUsageBytes;
            this.memoryUsageBytes = memoryUsageBytes;
            this.memoryAllocatedBytes = memoryAllocatedBytes;
            this.gcCount = gcCount;
            this.gcTimeMs = gcTimeMs;
            this.fileSizeBytes = fileSizeBytes;
            this.minMemoryUsageBytes = minMemoryUsageBytes;
            this.stdDevMemoryUsageBytes = stdDevMemoryUsageBytes;
            this.p95MemoryUsageBytes = p95MemoryUsageBytes;
            this.memoryGrowthRate = memoryGrowthRate;
        }

        public double getThroughputRowsPerSecond() {
            return executionTimeMs > 0 ? (processedRows * 1000.0) / executionTimeMs : 0.0;
        }

        public double getMemoryEfficiencyRatio() {
            return memoryUsageBytes > 0 ? (double) processedRows / memoryUsageBytes : 0.0;
        }

        public double getThroughputMBPerSecond() {
            return executionTimeMs > 0 ? (fileSizeBytes / (1024.0 * 1024.0)) / (executionTimeMs / 1000.0) : 0.0;
        }

        public double getPeakMemoryUsageMB() {
            return peakMemoryUsageBytes / (1024.0 * 1024.0);
        }

        public double getAvgMemoryUsageMB() {
            return avgMemoryUsageBytes / (1024.0 * 1024.0);
        }

        public double getMinMemoryUsageMB() {
            return minMemoryUsageBytes / (1024.0 * 1024.0);
        }

        public double getStdDevMemoryUsageMB() {
            return stdDevMemoryUsageBytes / (1024.0 * 1024.0);
        }

        public double getP95MemoryUsageMB() {
            return p95MemoryUsageBytes / (1024.0 * 1024.0);
        }

        @Override
        public String toString() {
            return String.format(
                    "%s-%s{rows=%d, time=%dms, peakMem=%.2f MB, avgMem=%.2f MB, minMem=%.2f MB, stdDev=%.2f MB, p95=%.2f MB, allocMem=%d bytes, gcCount=%d, gcTime=%dms, throughput=%.2f rows/s, efficiency=%.2e, growthRate=%.2f}",
                    library,
                    operation,
                    processedRows,
                    executionTimeMs,
                    getPeakMemoryUsageMB(),
                    getAvgMemoryUsageMB(),
                    getMinMemoryUsageMB(),
                    getStdDevMemoryUsageMB(),
                    getP95MemoryUsageMB(),
                    memoryAllocatedBytes,
                    gcCount,
                    gcTimeMs,
                    getThroughputRowsPerSecond(),
                    getMemoryEfficiencyRatio(),
                    memoryGrowthRate);
        }
    }
}
