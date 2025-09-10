package cn.idev.excel.benchmark.streaming;

import cn.idev.excel.EasyExcel;
import cn.idev.excel.ExcelReader;
import cn.idev.excel.benchmark.core.AbstractBenchmark;
import cn.idev.excel.benchmark.core.BenchmarkConfiguration;
import cn.idev.excel.benchmark.data.BenchmarkData;
import cn.idev.excel.benchmark.memory.StreamingMemoryProfiler;
import cn.idev.excel.benchmark.memory.StreamingMemoryProfiler.StreamingMemoryReport;
import cn.idev.excel.benchmark.utils.BenchmarkFileUtil;
import cn.idev.excel.benchmark.utils.DataGenerator;
import cn.idev.excel.context.AnalysisContext;
import cn.idev.excel.event.AnalysisEventListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
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
 * Memory efficiency benchmarks comparing streaming vs batch processing.
 * Focuses on memory usage patterns and efficiency metrics.
 */
@BenchmarkMode(Mode.All)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
@Warmup(iterations = 2, time = 3, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 3, time = 5, timeUnit = TimeUnit.SECONDS)
@Fork(1)
public class MemoryEfficiencyBenchmark extends AbstractBenchmark {

    @Param({"LARGE", "EXTRA_LARGE"})
    private String datasetSize;

    @Param({"500", "1000", "2000", "5000"})
    private int batchSize;

    private StreamingMemoryProfiler memoryProfiler;
    private File testFile;
    private List<BenchmarkData> testDataList;
    private long expectedDataSize;

    @Setup(Level.Trial)
    public void setupTrial() throws Exception {
        super.setupTrial();

        memoryProfiler = new StreamingMemoryProfiler();

        // Generate large test dataset
        BenchmarkConfiguration.DatasetSize size = BenchmarkConfiguration.DatasetSize.valueOf(datasetSize);
        int rowCount = size.getRowCount();
        testDataList = new DataGenerator().generateData(rowCount);
        expectedDataSize = rowCount * 100L; // Estimated bytes per row

        // Create test file
        String fileName = String.format("memory_efficiency_%s.xlsx", datasetSize.toLowerCase());
        testFile = BenchmarkFileUtil.createTestFile(fileName);

        // Pre-populate test file
        writeTestFile();

        System.out.printf("Setup memory efficiency benchmark: %d rows, batch size %d%n", rowCount, batchSize);
    }

    @TearDown(Level.Trial)
    public void tearDownTrial() {
        if (memoryProfiler != null) {
            memoryProfiler.shutdown();
        }

        if (testFile != null && testFile.exists()) {
            testFile.delete();
        }

        try {
            super.tearDownTrial();
        } catch (Exception e) {
            throw new RuntimeException("Failed to tear down trial", e);
        }
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
        System.runFinalization();
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Benchmark streaming read with configurable batch sizes
     */
    @Benchmark
    public MemoryEfficiencyResult benchmarkStreamingRead(Blackhole blackhole) {
        memoryProfiler.startProfiling();

        AtomicLong processedRows = new AtomicLong(0);
        AtomicLong peakBatchMemory = new AtomicLong(0);

        // Streaming batch processing with memory monitoring
        List<BenchmarkData> batch = new ArrayList<>(batchSize);

        try {
            ExcelReader excelReader = EasyExcel.read(
                            testFile, BenchmarkData.class, new AnalysisEventListener<BenchmarkData>() {
                                @Override
                                public void invoke(BenchmarkData data, AnalysisContext context) {
                                    batch.add(data);
                                    processedRows.incrementAndGet();

                                    if (batch.size() >= batchSize) {
                                        processBatch(batch, blackhole);

                                        // Monitor batch memory usage
                                        long currentMemory = memoryProfiler
                                                .getCurrentHeapUsage()
                                                .getUsed();
                                        peakBatchMemory.updateAndGet(peak -> Math.max(peak, currentMemory));

                                        // Clear batch for next iteration
                                        batch.clear();

                                        // Allow GC and profiler to capture state
                                        Thread.yield();
                                    }
                                }

                                @Override
                                public void doAfterAllAnalysed(AnalysisContext context) {
                                    if (!batch.isEmpty()) {
                                        processBatch(batch, blackhole);
                                        batch.clear();
                                    }
                                }
                            })
                    .build();

            excelReader.readAll();
            excelReader.finish();

        } catch (Exception e) {
            throw new RuntimeException("Streaming read failed", e);
        }

        StreamingMemoryReport memoryReport = memoryProfiler.stopProfiling();

        return new MemoryEfficiencyResult(
                "StreamingRead", processedRows.get(), expectedDataSize, batchSize, peakBatchMemory.get(), memoryReport);
    }

    /**
     * Benchmark batch read (load all data into memory at once)
     */
    @Benchmark
    public MemoryEfficiencyResult benchmarkBatchRead(Blackhole blackhole) {
        memoryProfiler.startProfiling();

        try {
            // Read all data into memory at once
            List<BenchmarkData> allData = new ArrayList<>();

            ExcelReader excelReader = EasyExcel.read(
                            testFile, BenchmarkData.class, new AnalysisEventListener<BenchmarkData>() {
                                @Override
                                public void invoke(BenchmarkData data, AnalysisContext context) {
                                    allData.add(data);
                                }

                                @Override
                                public void doAfterAllAnalysed(AnalysisContext context) {
                                    // Process all data at once
                                    blackhole.consume(allData);
                                }
                            })
                    .build();

            excelReader.readAll();
            excelReader.finish();

            long processedRows = allData.size();
            long peakMemory = memoryProfiler.getCurrentHeapUsage().getUsed();

            StreamingMemoryReport memoryReport = memoryProfiler.stopProfiling();

            return new MemoryEfficiencyResult(
                    "BatchRead",
                    processedRows,
                    expectedDataSize,
                    allData.size(), // Effective batch size is all data
                    peakMemory,
                    memoryReport);

        } catch (Exception e) {
            throw new RuntimeException("Batch read failed", e);
        }
    }

    /**
     * Benchmark memory-optimized streaming with aggressive GC
     */
    @Benchmark
    public MemoryEfficiencyResult benchmarkOptimizedStreaming(Blackhole blackhole) {
        memoryProfiler.startProfiling();

        AtomicLong processedRows = new AtomicLong(0);
        AtomicLong gcTriggers = new AtomicLong(0);
        AtomicLong peakBatchMemory = new AtomicLong(0);

        // Memory threshold for triggering GC (25% of max heap)
        long gcThreshold = Runtime.getRuntime().maxMemory() / 4;

        try {
            List<BenchmarkData> batch = new ArrayList<>(batchSize);

            ExcelReader excelReader = EasyExcel.read(
                            testFile, BenchmarkData.class, new AnalysisEventListener<BenchmarkData>() {
                                private int batchCount = 0;

                                @Override
                                public void invoke(BenchmarkData data, AnalysisContext context) {
                                    batch.add(data);
                                    processedRows.incrementAndGet();

                                    if (batch.size() >= batchSize) {
                                        processBatch(batch, blackhole);

                                        // Monitor memory and trigger GC if needed
                                        long currentMemory = memoryProfiler
                                                .getCurrentHeapUsage()
                                                .getUsed();
                                        peakBatchMemory.updateAndGet(peak -> Math.max(peak, currentMemory));

                                        if (currentMemory > gcThreshold && ++batchCount % 5 == 0) {
                                            System.gc();
                                            gcTriggers.incrementAndGet();
                                            Thread.yield();
                                        }

                                        batch.clear();
                                    }
                                }

                                @Override
                                public void doAfterAllAnalysed(AnalysisContext context) {
                                    if (!batch.isEmpty()) {
                                        processBatch(batch, blackhole);
                                        batch.clear();
                                    }
                                }
                            })
                    .build();

            excelReader.readAll();
            excelReader.finish();

        } catch (Exception e) {
            throw new RuntimeException("Optimized streaming failed", e);
        }

        StreamingMemoryReport memoryReport = memoryProfiler.stopProfiling();

        return new MemoryEfficiencyResult(
                "OptimizedStreaming",
                processedRows.get(),
                expectedDataSize,
                batchSize,
                peakBatchMemory.get(),
                memoryReport,
                gcTriggers.get());
    }

    /**
     * Benchmark adaptive batch sizing based on memory pressure
     */
    @Benchmark
    public MemoryEfficiencyResult benchmarkAdaptiveBatching(Blackhole blackhole) {
        memoryProfiler.startProfiling();

        AtomicLong processedRows = new AtomicLong(0);
        AtomicLong batchSizeAdjustments = new AtomicLong(0);
        AtomicLong peakBatchMemory = new AtomicLong(0);

        // Adaptive batch sizing parameters
        int minBatchSize = Math.max(100, batchSize / 4);
        int maxBatchSize = batchSize * 2;
        int currentBatchSize = batchSize;

        // Memory thresholds
        long memoryPressureThreshold = Runtime.getRuntime().maxMemory() / 3;
        long criticalMemoryThreshold = Runtime.getRuntime().maxMemory() / 2;

        try {
            List<BenchmarkData> batch = new ArrayList<>(maxBatchSize);

            ExcelReader excelReader = EasyExcel.read(
                            testFile, BenchmarkData.class, new AnalysisEventListener<BenchmarkData>() {
                                private int currentAdaptiveBatchSize = currentBatchSize;

                                @Override
                                public void invoke(BenchmarkData data, AnalysisContext context) {
                                    batch.add(data);
                                    processedRows.incrementAndGet();

                                    if (batch.size() >= currentAdaptiveBatchSize) {
                                        processBatch(batch, blackhole);

                                        // Check memory pressure and adjust batch size
                                        long currentMemory = memoryProfiler
                                                .getCurrentHeapUsage()
                                                .getUsed();
                                        peakBatchMemory.updateAndGet(peak -> Math.max(peak, currentMemory));

                                        if (currentMemory > criticalMemoryThreshold) {
                                            // Critical memory pressure - reduce batch size significantly
                                            currentAdaptiveBatchSize =
                                                    Math.max(minBatchSize, currentAdaptiveBatchSize / 2);
                                            batchSizeAdjustments.incrementAndGet();
                                            System.gc();
                                        } else if (currentMemory > memoryPressureThreshold) {
                                            // Medium memory pressure - reduce batch size slightly
                                            currentAdaptiveBatchSize =
                                                    Math.max(minBatchSize, (int) (currentAdaptiveBatchSize * 0.8));
                                            batchSizeAdjustments.incrementAndGet();
                                        } else if (currentMemory < memoryPressureThreshold / 2) {
                                            // Low memory pressure - can increase batch size
                                            currentAdaptiveBatchSize =
                                                    Math.min(maxBatchSize, (int) (currentAdaptiveBatchSize * 1.2));
                                            batchSizeAdjustments.incrementAndGet();
                                        }

                                        batch.clear();
                                    }
                                }

                                @Override
                                public void doAfterAllAnalysed(AnalysisContext context) {
                                    if (!batch.isEmpty()) {
                                        processBatch(batch, blackhole);
                                        batch.clear();
                                    }
                                }
                            })
                    .build();

            excelReader.readAll();
            excelReader.finish();

        } catch (Exception e) {
            throw new RuntimeException("Adaptive batching failed", e);
        }

        StreamingMemoryReport memoryReport = memoryProfiler.stopProfiling();

        return new MemoryEfficiencyResult(
                "AdaptiveBatching",
                processedRows.get(),
                expectedDataSize,
                batchSize,
                peakBatchMemory.get(),
                memoryReport,
                batchSizeAdjustments.get());
    }

    private void processBatch(List<BenchmarkData> batch, Blackhole blackhole) {
        for (BenchmarkData data : batch) {
            // Simulate processing work
            blackhole.consume(data.getStringData());
            if (data.getIntValue() != null) {
                blackhole.consume(data.getIntValue() * 2);
            }
        }
    }

    /**
     * Write test data to file
     */
    private void writeTestFile() {
        try {
            EasyExcel.write(testFile, BenchmarkData.class).sheet("TestData").doWrite(testDataList);
        } catch (Exception e) {
            throw new RuntimeException("Failed to write test file", e);
        }
    }

    /**
     * Result class for memory efficiency benchmarks
     */
    public static class MemoryEfficiencyResult {
        public final String benchmarkType;
        public final long processedRows;
        public final long expectedDataSize;
        public final int batchSize;
        public final long peakBatchMemory;
        public final StreamingMemoryReport memoryReport;
        public final long additionalMetric; // GC triggers, batch adjustments, etc.

        public MemoryEfficiencyResult(
                String benchmarkType,
                long processedRows,
                long expectedDataSize,
                int batchSize,
                long peakBatchMemory,
                StreamingMemoryReport memoryReport) {
            this(benchmarkType, processedRows, expectedDataSize, batchSize, peakBatchMemory, memoryReport, 0);
        }

        public MemoryEfficiencyResult(
                String benchmarkType,
                long processedRows,
                long expectedDataSize,
                int batchSize,
                long peakBatchMemory,
                StreamingMemoryReport memoryReport,
                long additionalMetric) {
            this.benchmarkType = benchmarkType;
            this.processedRows = processedRows;
            this.expectedDataSize = expectedDataSize;
            this.batchSize = batchSize;
            this.peakBatchMemory = peakBatchMemory;
            this.memoryReport = memoryReport;
            this.additionalMetric = additionalMetric;
        }

        public double getMemoryEfficiencyRatio() {
            return memoryReport.getPeakMemoryUsage() > 0
                    ? (double) expectedDataSize / memoryReport.getPeakMemoryUsage()
                    : 0.0;
        }

        public double getThroughputMBps() {
            return memoryReport.getDuration() > 0
                    ? (expectedDataSize / (1024.0 * 1024.0)) / (memoryReport.getDuration() / 1000.0)
                    : 0.0;
        }

        @Override
        public String toString() {
            return String.format(
                    "%s{rows=%d, batchSize=%d, peakMemory=%d, efficiency=%.2f, throughput=%.2f MB/s, additional=%d}",
                    benchmarkType,
                    processedRows,
                    batchSize,
                    peakBatchMemory,
                    getMemoryEfficiencyRatio(),
                    getThroughputMBps(),
                    additionalMetric);
        }
    }
}
