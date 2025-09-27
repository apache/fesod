package cn.idev.excel.benchmark.operations;

import cn.idev.excel.EasyExcel;
import cn.idev.excel.benchmark.core.AbstractBenchmark;
import cn.idev.excel.benchmark.core.BenchmarkConfiguration;
import cn.idev.excel.benchmark.data.BenchmarkData;
import cn.idev.excel.benchmark.utils.BenchmarkFileUtil;
import cn.idev.excel.benchmark.utils.DataGenerator;
import cn.idev.excel.context.AnalysisContext;
import cn.idev.excel.read.listener.ReadListener;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;

/**
 * Comprehensive benchmarks for FastExcel read operations
 */
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
@State(Scope.Benchmark)
@Warmup(iterations = 3, time = 2)
@Measurement(iterations = 5, time = 3)
@Fork(1)
public class ReadBenchmark extends AbstractBenchmark {

    // Test files for different sizes and formats
    private String xlsxSmallFile;
    private String xlsxMediumFile;
    private String xlsEXTRA_LARGEFile;
    private String xlsxExtraLargeFile;

    private String csvSmallFile;
    private String csvMediumFile;
    private String csvLargeFile;
    private String csvExtraLargeFile;

    // Listeners for different testing scenarios
    private CountingReadListener countingListener;
    private CollectingReadListener collectingListener;
    private ProcessingReadListener processingListener;

    @Override
    protected void setupBenchmark() throws Exception {
        logger.info("Setting up read benchmark test files...");

        // Generate test files for all sizes and formats
        generateTestFiles();

        // Initialize listeners
        countingListener = new CountingReadListener();
        collectingListener = new CollectingReadListener();
        processingListener = new ProcessingReadListener();

        logger.info("Read benchmark setup completed");
    }

    @Override
    protected void tearDownBenchmark() throws Exception {
        // Clean up temporary files
        BenchmarkFileUtil.cleanupTempFiles();
        logger.info("Read benchmark cleanup completed");
    }

    @Override
    protected void setupIteration0() throws Exception {
        // Reset listeners for each iteration
        countingListener.reset();
        collectingListener.reset();
        processingListener.reset();
    }

    private void generateTestFiles() {
        DataGenerator generator = new DataGenerator();

        // Generate XLSX files
        xlsxSmallFile = generateAndWriteTestFile(
                BenchmarkConfiguration.FileFormat.XLSX, BenchmarkConfiguration.DatasetSize.SMALL, generator);
        xlsxMediumFile = generateAndWriteTestFile(
                BenchmarkConfiguration.FileFormat.XLSX, BenchmarkConfiguration.DatasetSize.MEDIUM, generator);
        xlsEXTRA_LARGEFile = generateAndWriteTestFile(
                BenchmarkConfiguration.FileFormat.XLSX, BenchmarkConfiguration.DatasetSize.LARGE, generator);
        xlsxExtraLargeFile = generateAndWriteTestFile(
                BenchmarkConfiguration.FileFormat.XLSX, BenchmarkConfiguration.DatasetSize.EXTRA_LARGE, generator);

        // Generate CSV files
        csvSmallFile = generateAndWriteTestFile(
                BenchmarkConfiguration.FileFormat.CSV, BenchmarkConfiguration.DatasetSize.SMALL, generator);
        csvMediumFile = generateAndWriteTestFile(
                BenchmarkConfiguration.FileFormat.CSV, BenchmarkConfiguration.DatasetSize.MEDIUM, generator);
        csvLargeFile = generateAndWriteTestFile(
                BenchmarkConfiguration.FileFormat.CSV, BenchmarkConfiguration.DatasetSize.LARGE, generator);
        csvExtraLargeFile = generateAndWriteTestFile(
                BenchmarkConfiguration.FileFormat.CSV, BenchmarkConfiguration.DatasetSize.EXTRA_LARGE, generator);
    }

    private String generateAndWriteTestFile(
            BenchmarkConfiguration.FileFormat format,
            BenchmarkConfiguration.DatasetSize size,
            DataGenerator generator) {
        String filePath = BenchmarkFileUtil.getTempFilePath(format, size, "ReadBenchmark");
        List<BenchmarkData> data = generator.generateData(size);

        try {
            EasyExcel.write(filePath, BenchmarkData.class)
                    .sheet("BenchmarkData")
                    .doWrite(data);

            logger.debug(
                    "Generated test file: {} ({} rows, {})",
                    filePath,
                    size.getRowCount(),
                    BenchmarkFileUtil.getFileSizeFormatted(filePath));
            return filePath;
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate test file: " + filePath, e);
        }
    }

    // XLSX Read Benchmarks - Different sizes
    @Benchmark
    public void readXlsxSmall(Blackhole blackhole) throws Exception {
        EasyExcel.read(xlsxSmallFile, BenchmarkData.class, countingListener)
                .sheet()
                .doRead();
        consumeData(countingListener.getCount(), blackhole);
    }

    @Benchmark
    public void readXlsxMedium(Blackhole blackhole) throws Exception {
        EasyExcel.read(xlsxMediumFile, BenchmarkData.class, countingListener)
                .sheet()
                .doRead();
        consumeData(countingListener.getCount(), blackhole);
    }

    @Benchmark
    public void readXlsEXTRA_LARGE(Blackhole blackhole) throws Exception {
        EasyExcel.read(xlsEXTRA_LARGEFile, BenchmarkData.class, countingListener)
                .sheet()
                .doRead();
        consumeData(countingListener.getCount(), blackhole);
    }

    @Benchmark
    public void readXlsxExtraLarge(Blackhole blackhole) throws Exception {
        EasyExcel.read(xlsxExtraLargeFile, BenchmarkData.class, countingListener)
                .sheet()
                .doRead();
        consumeData(countingListener.getCount(), blackhole);
    }

    // CSV Read Benchmarks - Different sizes
    @Benchmark
    public void readCsvSmall(Blackhole blackhole) throws Exception {
        EasyExcel.read(csvSmallFile, BenchmarkData.class, countingListener)
                .sheet()
                .doRead();
        consumeData(countingListener.getCount(), blackhole);
    }

    @Benchmark
    public void readCsvMedium(Blackhole blackhole) throws Exception {
        EasyExcel.read(csvMediumFile, BenchmarkData.class, countingListener)
                .sheet()
                .doRead();
        consumeData(countingListener.getCount(), blackhole);
    }

    @Benchmark
    public void readCsvLarge(Blackhole blackhole) throws Exception {
        EasyExcel.read(csvLargeFile, BenchmarkData.class, countingListener)
                .sheet()
                .doRead();
        consumeData(countingListener.getCount(), blackhole);
    }

    @Benchmark
    public void readCsvExtraLarge(Blackhole blackhole) throws Exception {
        EasyExcel.read(csvExtraLargeFile, BenchmarkData.class, countingListener)
                .sheet()
                .doRead();
        consumeData(countingListener.getCount(), blackhole);
    }

    // Stream reading benchmarks
    @Benchmark
    public void readXlsEXTRA_LARGEWithStreaming(Blackhole blackhole) throws Exception {
        try (FileInputStream fis = new FileInputStream(xlsEXTRA_LARGEFile)) {
            EasyExcel.read(fis, BenchmarkData.class, countingListener).sheet().doRead();
            consumeData(countingListener.getCount(), blackhole);
        }
    }

    @Benchmark
    public void readCsvLargeWithStreaming(Blackhole blackhole) throws Exception {
        try (FileInputStream fis = new FileInputStream(csvLargeFile)) {
            EasyExcel.read(fis, BenchmarkData.class, countingListener).sheet().doRead();
            consumeData(countingListener.getCount(), blackhole);
        }
    }

    // Different listener types benchmarks
    @Benchmark
    public void readXlsEXTRA_LARGECountingOnly(Blackhole blackhole) throws Exception {
        EasyExcel.read(xlsEXTRA_LARGEFile, BenchmarkData.class, countingListener)
                .sheet()
                .doRead();
        consumeData(countingListener.getCount(), blackhole);
    }

    @Benchmark
    public void readXlsEXTRA_LARGECollecting(Blackhole blackhole) throws Exception {
        EasyExcel.read(xlsEXTRA_LARGEFile, BenchmarkData.class, collectingListener)
                .sheet()
                .doRead();
        consumeData(collectingListener.getData().size(), blackhole);
    }

    @Benchmark
    public void readXlsEXTRA_LARGEProcessing(Blackhole blackhole) throws Exception {
        EasyExcel.read(xlsEXTRA_LARGEFile, BenchmarkData.class, processingListener)
                .sheet()
                .doRead();
        consumeData(processingListener.getProcessedCount(), blackhole);
    }

    // Head configuration benchmarks
    @Benchmark
    public void readXlsEXTRA_LARGEWithHeadRowNumber(Blackhole blackhole) throws Exception {
        EasyExcel.read(xlsEXTRA_LARGEFile, BenchmarkData.class, countingListener)
                .headRowNumber(1)
                .sheet()
                .doRead();
        consumeData(countingListener.getCount(), blackhole);
    }

    @Benchmark
    public void readXlsEXTRA_LARGESkipRows(Blackhole blackhole) throws Exception {
        EasyExcel.read(xlsEXTRA_LARGEFile, BenchmarkData.class, countingListener)
                .headRowNumber(2) // Skip first row
                .sheet()
                .doRead();
        consumeData(countingListener.getCount(), blackhole);
    }

    // Multiple sheets reading (using same file)
    @Benchmark
    public void readXlsxMultipleSheets(Blackhole blackhole) throws Exception {
        for (int i = 0; i < 3; i++) {
            EasyExcel.read(xlsxMediumFile, BenchmarkData.class, countingListener)
                    .sheet(0) // Always read first sheet since our test files have only one
                    .doRead();
        }
        consumeData(countingListener.getCount(), blackhole);
    }

    // Memory efficient reading with limited collections
    @Benchmark
    public void readXlsEXTRA_LARGEMemoryEfficient(Blackhole blackhole) throws Exception {
        LimitedCollectingReadListener limitedListener = new LimitedCollectingReadListener(1000);
        EasyExcel.read(xlsEXTRA_LARGEFile, BenchmarkData.class, limitedListener)
                .sheet()
                .doRead();
        consumeData(limitedListener.getData().size(), blackhole);
    }

    // Error handling benchmark
    @Benchmark
    public void readXlsxWithErrorHandling(Blackhole blackhole) throws Exception {
        ErrorHandlingReadListener errorListener = new ErrorHandlingReadListener();
        try {
            EasyExcel.read(xlsEXTRA_LARGEFile, BenchmarkData.class, errorListener)
                    .sheet()
                    .doRead();
        } catch (Exception e) {
            // Expected for some error scenarios
        }
        consumeData(errorListener.getProcessedCount(), blackhole);
    }

    // Read Listeners
    private static class CountingReadListener implements ReadListener<BenchmarkData> {
        private final AtomicLong count = new AtomicLong(0);

        @Override
        public void invoke(BenchmarkData data, AnalysisContext context) {
            count.incrementAndGet();
        }

        @Override
        public void doAfterAllAnalysed(AnalysisContext context) {
            // Nothing to do
        }

        public long getCount() {
            return count.get();
        }

        public void reset() {
            count.set(0);
        }
    }

    private static class CollectingReadListener implements ReadListener<BenchmarkData> {
        private final List<BenchmarkData> data = new ArrayList<>();

        @Override
        public void invoke(BenchmarkData item, AnalysisContext context) {
            data.add(item);
        }

        @Override
        public void doAfterAllAnalysed(AnalysisContext context) {
            // Nothing to do
        }

        public List<BenchmarkData> getData() {
            return data;
        }

        public void reset() {
            data.clear();
        }
    }

    private static class ProcessingReadListener implements ReadListener<BenchmarkData> {
        private final AtomicLong processedCount = new AtomicLong(0);

        @Override
        public void invoke(BenchmarkData data, AnalysisContext context) {
            // Simulate some processing
            if (data.getStringData() != null && data.getStringData().length() > 0) {
                String processed = data.getStringData().toUpperCase();
                // Simulate validation
                if (data.getIntValue() != null && data.getIntValue() > 0) {
                    processedCount.incrementAndGet();
                }
            }
        }

        @Override
        public void doAfterAllAnalysed(AnalysisContext context) {
            // Nothing to do
        }

        public long getProcessedCount() {
            return processedCount.get();
        }

        public void reset() {
            processedCount.set(0);
        }
    }

    private static class LimitedCollectingReadListener implements ReadListener<BenchmarkData> {
        private final List<BenchmarkData> data = new ArrayList<>();
        private final int maxSize;

        public LimitedCollectingReadListener(int maxSize) {
            this.maxSize = maxSize;
        }

        @Override
        public void invoke(BenchmarkData item, AnalysisContext context) {
            if (data.size() < maxSize) {
                data.add(item);
            }
        }

        @Override
        public void doAfterAllAnalysed(AnalysisContext context) {
            // Nothing to do
        }

        public List<BenchmarkData> getData() {
            return data;
        }

        public void reset() {
            data.clear();
        }
    }

    private static class ErrorHandlingReadListener implements ReadListener<BenchmarkData> {
        private final AtomicLong processedCount = new AtomicLong(0);
        private final AtomicLong errorCount = new AtomicLong(0);

        @Override
        public void invoke(BenchmarkData data, AnalysisContext context) {
            try {
                // Simulate processing that might fail
                if (data.getStringData() != null) {
                    processedCount.incrementAndGet();
                }
            } catch (Exception e) {
                errorCount.incrementAndGet();
            }
        }

        @Override
        public void doAfterAllAnalysed(AnalysisContext context) {
            // Nothing to do
        }

        public long getProcessedCount() {
            return processedCount.get();
        }

        public long getErrorCount() {
            return errorCount.get();
        }

        public void reset() {
            processedCount.set(0);
            errorCount.set(0);
        }
    }
}
