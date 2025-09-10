package cn.idev.excel.benchmark.streaming;

import cn.idev.excel.EasyExcel;
import cn.idev.excel.ExcelReader;
import cn.idev.excel.ExcelWriter;
import cn.idev.excel.benchmark.core.AbstractBenchmark;
import cn.idev.excel.benchmark.core.BenchmarkConfiguration;
import cn.idev.excel.benchmark.data.BenchmarkData;
import cn.idev.excel.benchmark.memory.StreamingMemoryProfiler;
import cn.idev.excel.benchmark.memory.StreamingMemoryProfiler.StreamingMemoryReport;
import cn.idev.excel.benchmark.utils.BenchmarkFileUtil;
import cn.idev.excel.benchmark.utils.DataGenerator;
import cn.idev.excel.context.AnalysisContext;
import cn.idev.excel.event.AnalysisEventListener;
import cn.idev.excel.write.metadata.WriteSheet;
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
 * Comprehensive benchmarks for streaming operations with memory profiling.
 * Tests FastExcel's streaming capabilities for large datasets.
 */
@BenchmarkMode(Mode.All)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
@Warmup(iterations = 2, time = 2, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 3, time = 5, timeUnit = TimeUnit.SECONDS)
@Fork(1)
public class StreamingBenchmark extends AbstractBenchmark {

    @Param({"SMALL", "MEDIUM", "LARGE", "EXTRA_LARGE"})
    private String datasetSize;

    @Param({"XLSX"})
    private String fileFormat;

    private StreamingMemoryProfiler memoryProfiler;
    private File testFile;
    private List<BenchmarkData> testDataList;

    @Setup(Level.Trial)
    public void setupTrial() throws Exception {
        super.setupTrial();

        memoryProfiler = new StreamingMemoryProfiler();

        // Generate test data
        BenchmarkConfiguration.DatasetSize size = BenchmarkConfiguration.DatasetSize.valueOf(datasetSize);
        int rowCount = size.getRowCount();
        testDataList = DataGenerator.generateTestData(size);

        // Create test file
        String fileName = String.format("streaming_test_%s.%s", datasetSize.toLowerCase(), fileFormat.toLowerCase());
        testFile = BenchmarkFileUtil.createTestFile(fileName);

        // Pre-populate test file for read operations
        writeTestFile();

        System.out.printf("Setup streaming benchmark: %s format, %d rows%n", fileFormat, rowCount);
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
        // Force GC before each benchmark to get clean memory state
        memoryProfiler.forceGc();
    }

    /**
     * Benchmark streaming read operations with memory profiling
     */
    @Benchmark
    public StreamingReadResult benchmarkStreamingRead(Blackhole blackhole) {
        memoryProfiler.startProfiling();

        AtomicLong processedRows = new AtomicLong(0);
        AtomicLong totalBytes = new AtomicLong(0);

        try {
            ExcelReader excelReader = EasyExcel.read(
                            testFile,
                            BenchmarkData.class,
                            new StreamingAnalysisEventListener(processedRows, totalBytes, blackhole))
                    .build();

            excelReader.readAll();
            excelReader.finish();

        } catch (Exception e) {
            throw new RuntimeException("Streaming read failed", e);
        }

        StreamingMemoryReport memoryReport = memoryProfiler.stopProfiling();

        return new StreamingReadResult(processedRows.get(), totalBytes.get(), memoryReport);
    }

    /**
     * Benchmark streaming write operations with memory profiling
     */
    @Benchmark
    public StreamingWriteResult benchmarkStreamingWrite(Blackhole blackhole) {
        File outputFile = BenchmarkFileUtil.createTestFile(String.format(
                "streaming_write_%s_%d.%s",
                datasetSize.toLowerCase(), System.currentTimeMillis(), fileFormat.toLowerCase()));

        memoryProfiler.startProfiling();

        long writtenRows = 0;
        long totalBytes = 0;

        try {
            ExcelWriter excelWriter =
                    EasyExcel.write(outputFile, BenchmarkData.class).build();
            WriteSheet writeSheet = EasyExcel.writerSheet("StreamingData").build();

            // Write data in batches to simulate streaming
            int batchSize = Math.min(1000, testDataList.size() / 10);
            for (int i = 0; i < testDataList.size(); i += batchSize) {
                int endIndex = Math.min(i + batchSize, testDataList.size());
                List<BenchmarkData> batch = testDataList.subList(i, endIndex);

                excelWriter.write(batch, writeSheet);
                writtenRows += batch.size();

                // Estimate bytes written
                totalBytes += batch.size() * estimateRowSize();

                blackhole.consume(batch);

                // Allow memory profiler to capture snapshots
                Thread.yield();
            }

            excelWriter.finish();

        } catch (Exception e) {
            throw new RuntimeException("Streaming write failed", e);
        } finally {
            if (outputFile.exists()) {
                outputFile.delete();
            }
        }

        StreamingMemoryReport memoryReport = memoryProfiler.stopProfiling();

        return new StreamingWriteResult(writtenRows, totalBytes, memoryReport);
    }

    /**
     * Benchmark streaming read-write pipeline with memory profiling
     */
    @Benchmark
    public StreamingPipelineResult benchmarkStreamingPipeline(Blackhole blackhole) {
        File outputFile = BenchmarkFileUtil.createTestFile(String.format(
                "streaming_pipeline_%s_%d.%s",
                datasetSize.toLowerCase(), System.currentTimeMillis(), fileFormat.toLowerCase()));

        memoryProfiler.startProfiling();

        AtomicLong processedRows = new AtomicLong(0);
        AtomicLong totalBytes = new AtomicLong(0);

        try {
            // Create streaming pipeline: read -> transform -> write
            ExcelWriter excelWriter =
                    EasyExcel.write(outputFile, BenchmarkData.class).build();
            WriteSheet writeSheet = EasyExcel.writerSheet("PipelineData").build();

            List<BenchmarkData> batch = new ArrayList<>();
            int batchSize = 500;

            ExcelReader excelReader = EasyExcel.read(
                            testFile, BenchmarkData.class, new AnalysisEventListener<BenchmarkData>() {
                                @Override
                                public void invoke(BenchmarkData data, AnalysisContext context) {
                                    // Transform data (simulate processing)
                                    data.setStringData("Processed: " + data.getStringData());
                                    if (data.getIntValue() != null) {
                                        data.setIntValue(data.getIntValue() * 2);
                                    }

                                    batch.add(data);
                                    processedRows.incrementAndGet();
                                    totalBytes.addAndGet(estimateRowSize());

                                    // Write in batches to maintain streaming behavior
                                    if (batch.size() >= batchSize) {
                                        excelWriter.write(new ArrayList<>(batch), writeSheet);
                                        blackhole.consume(batch);
                                        batch.clear();

                                        // Allow memory profiler to capture snapshots
                                        Thread.yield();
                                    }
                                }

                                @Override
                                public void doAfterAllAnalysed(AnalysisContext context) {
                                    // Write remaining data
                                    if (!batch.isEmpty()) {
                                        excelWriter.write(batch, writeSheet);
                                        blackhole.consume(batch);
                                        batch.clear();
                                    }
                                }
                            })
                    .build();

            excelReader.readAll();
            excelReader.finish();
            excelWriter.finish();

        } catch (Exception e) {
            throw new RuntimeException("Streaming pipeline failed", e);
        } finally {
            if (outputFile.exists()) {
                outputFile.delete();
            }
        }

        StreamingMemoryReport memoryReport = memoryProfiler.stopProfiling();

        return new StreamingPipelineResult(processedRows.get(), totalBytes.get(), memoryReport);
    }

    /**
     * Benchmark memory-constrained streaming operations
     */
    @Benchmark
    public MemoryConstrainedResult benchmarkMemoryConstrainedStreaming(Blackhole blackhole) {
        // Simulate memory-constrained environment
        long maxHeapSize = Runtime.getRuntime().maxMemory();
        long targetMemoryLimit = maxHeapSize / 4; // Use only 25% of available heap

        memoryProfiler.startProfiling();

        AtomicLong processedRows = new AtomicLong(0);
        AtomicLong memoryViolations = new AtomicLong(0);
        AtomicLong totalBytes = new AtomicLong(0);

        try {
            ExcelReader excelReader = EasyExcel.read(
                            testFile, BenchmarkData.class, new AnalysisEventListener<BenchmarkData>() {
                                private int batchCount = 0;

                                @Override
                                public void invoke(BenchmarkData data, AnalysisContext context) {
                                    processedRows.incrementAndGet();
                                    totalBytes.addAndGet(estimateRowSize());
                                    blackhole.consume(data);

                                    // Check memory usage periodically
                                    if (++batchCount % 100 == 0) {
                                        long currentMemory = memoryProfiler
                                                .getCurrentHeapUsage()
                                                .getUsed();
                                        if (currentMemory > targetMemoryLimit) {
                                            memoryViolations.incrementAndGet();

                                            // Force GC to stay within memory limit
                                            System.gc();
                                            Thread.yield();
                                        }
                                    }
                                }

                                @Override
                                public void doAfterAllAnalysed(AnalysisContext context) {
                                    // Final memory check
                                    long finalMemory =
                                            memoryProfiler.getCurrentHeapUsage().getUsed();
                                    System.out.printf(
                                            "Final memory usage: %d bytes (limit: %d)%n",
                                            finalMemory, targetMemoryLimit);
                                }
                            })
                    .build();

            excelReader.readAll();
            excelReader.finish();

        } catch (Exception e) {
            throw new RuntimeException("Memory-constrained streaming failed", e);
        }

        StreamingMemoryReport memoryReport = memoryProfiler.stopProfiling();

        return new MemoryConstrainedResult(
                processedRows.get(), totalBytes.get(), memoryViolations.get(), targetMemoryLimit, memoryReport);
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
     * Estimate row size for memory calculations
     */
    private long estimateRowSize() {
        // Rough estimate based on TestData structure
        return 100; // bytes per row
    }

    /**
     * Streaming event listener for read operations
     */
    private static class StreamingAnalysisEventListener extends AnalysisEventListener<BenchmarkData> {
        private final AtomicLong processedRows;
        private final AtomicLong totalBytes;
        private final Blackhole blackhole;

        public StreamingAnalysisEventListener(AtomicLong processedRows, AtomicLong totalBytes, Blackhole blackhole) {
            this.processedRows = processedRows;
            this.totalBytes = totalBytes;
            this.blackhole = blackhole;
        }

        @Override
        public void invoke(BenchmarkData data, AnalysisContext context) {
            processedRows.incrementAndGet();
            totalBytes.addAndGet(100); // Estimate row size
            blackhole.consume(data);
        }

        @Override
        public void doAfterAllAnalysed(AnalysisContext context) {
            // Optional: final processing
        }
    }

    /**
     * Result classes for different streaming benchmark types
     */
    public static class StreamingReadResult {
        public final long processedRows;
        public final long totalBytes;
        public final StreamingMemoryReport memoryReport;

        public StreamingReadResult(long processedRows, long totalBytes, StreamingMemoryReport memoryReport) {
            this.processedRows = processedRows;
            this.totalBytes = totalBytes;
            this.memoryReport = memoryReport;
        }

        @Override
        public String toString() {
            return String.format(
                    "StreamingRead{rows=%d, bytes=%d, memory=%s}", processedRows, totalBytes, memoryReport);
        }
    }

    public static class StreamingWriteResult {
        public final long writtenRows;
        public final long totalBytes;
        public final StreamingMemoryReport memoryReport;

        public StreamingWriteResult(long writtenRows, long totalBytes, StreamingMemoryReport memoryReport) {
            this.writtenRows = writtenRows;
            this.totalBytes = totalBytes;
            this.memoryReport = memoryReport;
        }

        @Override
        public String toString() {
            return String.format("StreamingWrite{rows=%d, bytes=%d, memory=%s}", writtenRows, totalBytes, memoryReport);
        }
    }

    public static class StreamingPipelineResult {
        public final long processedRows;
        public final long totalBytes;
        public final StreamingMemoryReport memoryReport;

        public StreamingPipelineResult(long processedRows, long totalBytes, StreamingMemoryReport memoryReport) {
            this.processedRows = processedRows;
            this.totalBytes = totalBytes;
            this.memoryReport = memoryReport;
        }

        @Override
        public String toString() {
            return String.format(
                    "StreamingPipeline{rows=%d, bytes=%d, memory=%s}", processedRows, totalBytes, memoryReport);
        }
    }

    public static class MemoryConstrainedResult {
        public final long processedRows;
        public final long totalBytes;
        public final long memoryViolations;
        public final long memoryLimit;
        public final StreamingMemoryReport memoryReport;

        public MemoryConstrainedResult(
                long processedRows,
                long totalBytes,
                long memoryViolations,
                long memoryLimit,
                StreamingMemoryReport memoryReport) {
            this.processedRows = processedRows;
            this.totalBytes = totalBytes;
            this.memoryViolations = memoryViolations;
            this.memoryLimit = memoryLimit;
            this.memoryReport = memoryReport;
        }

        @Override
        public String toString() {
            return String.format(
                    "MemoryConstrained{rows=%d, bytes=%d, violations=%d, limit=%d, memory=%s}",
                    processedRows, totalBytes, memoryViolations, memoryLimit, memoryReport);
        }
    }
}
