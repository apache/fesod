package cn.idev.excel.benchmark.operations;

import cn.idev.excel.EasyExcel;
import cn.idev.excel.ExcelWriter;
import cn.idev.excel.benchmark.core.AbstractBenchmark;
import cn.idev.excel.benchmark.core.BenchmarkConfiguration;
import cn.idev.excel.benchmark.data.BenchmarkData;
import cn.idev.excel.benchmark.utils.BenchmarkFileUtil;
import cn.idev.excel.benchmark.utils.DataGenerator;
import cn.idev.excel.write.metadata.WriteSheet;
import cn.idev.excel.write.metadata.WriteTable;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
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
 * Comprehensive benchmarks for FastExcel write operations
 */
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
@State(Scope.Benchmark)
@Warmup(iterations = 3, time = 2)
@Measurement(iterations = 5, time = 3)
@Fork(1)
public class WriteBenchmark extends AbstractBenchmark {

    // Test data for different sizes
    private List<BenchmarkData> smallData;
    private List<BenchmarkData> mediumData;
    private List<BenchmarkData> largeData;
    private List<BenchmarkData> extraLargeData;

    // Batch data for streaming tests
    private List<List<BenchmarkData>> smallBatches;
    private List<List<BenchmarkData>> mediumBatches;
    private List<List<BenchmarkData>> largeBatches;

    // Data generator
    private DataGenerator dataGenerator;

    @Override
    protected void setupBenchmark() throws Exception {
        logger.info("Setting up write benchmark test data...");

        dataGenerator = new DataGenerator();

        // Generate test data sets
        generateTestData();

        logger.info("Write benchmark setup completed");
    }

    @Override
    protected void tearDownBenchmark() throws Exception {
        // Clean up temporary files
        BenchmarkFileUtil.cleanupTempFiles();
        logger.info("Write benchmark cleanup completed");
    }

    private void generateTestData() {
        // Generate data for different sizes
        smallData = dataGenerator.generateData(BenchmarkConfiguration.DatasetSize.SMALL);
        mediumData = dataGenerator.generateData(BenchmarkConfiguration.DatasetSize.MEDIUM);
        largeData = dataGenerator.generateData(BenchmarkConfiguration.DatasetSize.LARGE);
        extraLargeData = dataGenerator.generateData(BenchmarkConfiguration.DatasetSize.EXTRA_LARGE);

        // Generate batch data for streaming
        smallBatches = dataGenerator.generateDataInBatches(BenchmarkConfiguration.DatasetSize.SMALL.getRowCount(), 100);
        mediumBatches =
                dataGenerator.generateDataInBatches(BenchmarkConfiguration.DatasetSize.MEDIUM.getRowCount(), 1000);
        largeBatches =
                dataGenerator.generateDataInBatches(BenchmarkConfiguration.DatasetSize.LARGE.getRowCount(), 5000);

        logger.debug(
                "Generated test data - Small: {}, Medium: {}, Large: {}, Extra Large: {} rows",
                smallData.size(),
                mediumData.size(),
                largeData.size(),
                extraLargeData.size());
    }

    // XLSX Write Benchmarks - Different sizes
    @Benchmark
    public void writeXlsxSmall(Blackhole blackhole) throws Exception {
        String filePath = BenchmarkFileUtil.getTempFilePath(
                BenchmarkConfiguration.FileFormat.XLSX, BenchmarkConfiguration.DatasetSize.SMALL, "WriteBenchmark");

        EasyExcel.write(filePath, BenchmarkData.class).sheet("BenchmarkData").doWrite(smallData);

        long fileSize = BenchmarkFileUtil.getFileSize(filePath);
        consumeData(fileSize, blackhole);
    }

    @Benchmark
    public void writeXlsxMedium(Blackhole blackhole) throws Exception {
        String filePath = BenchmarkFileUtil.getTempFilePath(
                BenchmarkConfiguration.FileFormat.XLSX, BenchmarkConfiguration.DatasetSize.MEDIUM, "WriteBenchmark");

        EasyExcel.write(filePath, BenchmarkData.class).sheet("BenchmarkData").doWrite(mediumData);

        long fileSize = BenchmarkFileUtil.getFileSize(filePath);
        consumeData(fileSize, blackhole);
    }

    @Benchmark
    public void writeXlsEXTRA_LARGE(Blackhole blackhole) throws Exception {
        String filePath = BenchmarkFileUtil.getTempFilePath(
                BenchmarkConfiguration.FileFormat.XLSX, BenchmarkConfiguration.DatasetSize.LARGE, "WriteBenchmark");

        EasyExcel.write(filePath, BenchmarkData.class).sheet("BenchmarkData").doWrite(largeData);

        long fileSize = BenchmarkFileUtil.getFileSize(filePath);
        consumeData(fileSize, blackhole);
    }

    @Benchmark
    public void writeXlsxExtraLarge(Blackhole blackhole) throws Exception {
        String filePath = BenchmarkFileUtil.getTempFilePath(
                BenchmarkConfiguration.FileFormat.XLSX,
                BenchmarkConfiguration.DatasetSize.EXTRA_LARGE,
                "WriteBenchmark");

        EasyExcel.write(filePath, BenchmarkData.class).sheet("BenchmarkData").doWrite(extraLargeData);

        long fileSize = BenchmarkFileUtil.getFileSize(filePath);
        consumeData(fileSize, blackhole);
    }

    // CSV Write Benchmarks - Different sizes
    public void writeXlsSmall(Blackhole blackhole) throws Exception {
        String filePath = BenchmarkFileUtil.getTempFilePath(
                BenchmarkConfiguration.FileFormat.XLS, BenchmarkConfiguration.DatasetSize.SMALL, "WriteBenchmark");

        EasyExcel.write(filePath, BenchmarkData.class).sheet("BenchmarkData").doWrite(smallData);

        long fileSize = BenchmarkFileUtil.getFileSize(filePath);
        consumeData(fileSize, blackhole);
    }

    @Benchmark
    public void writeCsvMedium(Blackhole blackhole) throws Exception {
        String filePath = BenchmarkFileUtil.getTempFilePath(
                BenchmarkConfiguration.FileFormat.CSV, BenchmarkConfiguration.DatasetSize.MEDIUM, "WriteBenchmark");

        EasyExcel.write(filePath, BenchmarkData.class).sheet("BenchmarkData").doWrite(mediumData);

        long fileSize = BenchmarkFileUtil.getFileSize(filePath);
        consumeData(fileSize, blackhole);
    }

    @Benchmark
    public void writeCsvLarge(Blackhole blackhole) throws Exception {
        String filePath = BenchmarkFileUtil.getTempFilePath(
                BenchmarkConfiguration.FileFormat.CSV, BenchmarkConfiguration.DatasetSize.LARGE, "WriteBenchmark");

        EasyExcel.write(filePath, BenchmarkData.class).sheet("BenchmarkData").doWrite(largeData);

        long fileSize = BenchmarkFileUtil.getFileSize(filePath);
        consumeData(fileSize, blackhole);
    }

    @Benchmark
    public void writeCsvExtraLarge(Blackhole blackhole) throws Exception {
        String filePath = BenchmarkFileUtil.getTempFilePath(
                BenchmarkConfiguration.FileFormat.CSV,
                BenchmarkConfiguration.DatasetSize.EXTRA_LARGE,
                "WriteBenchmark");

        EasyExcel.write(filePath, BenchmarkData.class).sheet("BenchmarkData").doWrite(extraLargeData);

        long fileSize = BenchmarkFileUtil.getFileSize(filePath);
        consumeData(fileSize, blackhole);
    }

    // Streaming write benchmarks using ExcelWriter
    @Benchmark
    public void writeXlsEXTRA_LARGEStreaming(Blackhole blackhole) throws Exception {
        String filePath = BenchmarkFileUtil.getTempFilePath(
                BenchmarkConfiguration.FileFormat.XLSX,
                BenchmarkConfiguration.DatasetSize.LARGE,
                "StreamingWriteBenchmark");

        try (ExcelWriter excelWriter =
                EasyExcel.write(filePath, BenchmarkData.class).build()) {
            WriteSheet writeSheet = EasyExcel.writerSheet("StreamingData").build();

            for (List<BenchmarkData> batch : largeBatches) {
                excelWriter.write(batch, writeSheet);
            }
        }

        long fileSize = BenchmarkFileUtil.getFileSize(filePath);
        consumeData(fileSize, blackhole);
    }

    @Benchmark
    public void writeCsvLargeStreaming(Blackhole blackhole) throws Exception {
        String filePath = BenchmarkFileUtil.getTempFilePath(
                BenchmarkConfiguration.FileFormat.CSV,
                BenchmarkConfiguration.DatasetSize.LARGE,
                "StreamingWriteBenchmark");

        try (ExcelWriter excelWriter =
                EasyExcel.write(filePath, BenchmarkData.class).build()) {
            WriteSheet writeSheet = EasyExcel.writerSheet("StreamingData").build();

            for (List<BenchmarkData> batch : largeBatches) {
                excelWriter.write(batch, writeSheet);
            }
        }

        long fileSize = BenchmarkFileUtil.getFileSize(filePath);
        consumeData(fileSize, blackhole);
    }

    // Multiple sheets writing
    @Benchmark
    public void writeXlsxMultipleSheets(Blackhole blackhole) throws Exception {
        String filePath = BenchmarkFileUtil.getTempFilePath(
                BenchmarkConfiguration.FileFormat.XLSX,
                BenchmarkConfiguration.DatasetSize.MEDIUM,
                "MultiSheetWriteBenchmark");

        try (ExcelWriter excelWriter =
                EasyExcel.write(filePath, BenchmarkData.class).build()) {
            // Write to 3 different sheets
            for (int i = 0; i < 3; i++) {
                WriteSheet writeSheet =
                        EasyExcel.writerSheet(i, "Sheet" + (i + 1)).build();
                excelWriter.write(mediumData, writeSheet);
            }
        }

        long fileSize = BenchmarkFileUtil.getFileSize(filePath);
        consumeData(fileSize, blackhole);
    }

    // Output stream writing
    public void writeXlsxToOutputStream(Blackhole blackhole) throws Exception {
        String filePath = BenchmarkFileUtil.getTempFilePath(
                BenchmarkConfiguration.FileFormat.XLSX,
                BenchmarkConfiguration.DatasetSize.MEDIUM,
                "OutputStreamWriteBenchmark");

        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            EasyExcel.write(fos, BenchmarkData.class).sheet("OutputStreamData").doWrite(mediumData);
        }

        long fileSize = BenchmarkFileUtil.getFileSize(filePath);
        consumeData(fileSize, blackhole);
    }

    // Table-based writing
    public void writeXlsxTableFormat(Blackhole blackhole) throws Exception {
        String filePath = BenchmarkFileUtil.getTempFilePath(
                BenchmarkConfiguration.FileFormat.XLSX,
                BenchmarkConfiguration.DatasetSize.LARGE,
                "TableFormatWriteBenchmark");

        try (ExcelWriter excelWriter =
                EasyExcel.write(filePath, BenchmarkData.class).build()) {
            WriteSheet writeSheet = EasyExcel.writerSheet("TableData").build();
            WriteTable writeTable = EasyExcel.writerTable(0).build();

            // Write data in table format
            for (List<BenchmarkData> batch : largeBatches) {
                excelWriter.write(batch, writeSheet, writeTable);
            }
        }

        long fileSize = BenchmarkFileUtil.getFileSize(filePath);
        consumeData(fileSize, blackhole);
    }

    // Memory efficient batch writing
    public void writeXlsxMemoryEfficientBatches(Blackhole blackhole) throws Exception {
        String filePath = BenchmarkFileUtil.getTempFilePath(
                BenchmarkConfiguration.FileFormat.XLSX,
                BenchmarkConfiguration.DatasetSize.LARGE,
                "MemoryEfficientWriteBenchmark");

        try (ExcelWriter excelWriter =
                EasyExcel.write(filePath, BenchmarkData.class).build()) {
            WriteSheet writeSheet = EasyExcel.writerSheet("BatchData").build();

            // Write in small batches to reduce memory usage
            int batchSize = 1000;
            for (int i = 0; i < largeData.size(); i += batchSize) {
                int endIndex = Math.min(i + batchSize, largeData.size());
                List<BenchmarkData> batch = largeData.subList(i, endIndex);
                excelWriter.write(batch, writeSheet);
            }
        }

        long fileSize = BenchmarkFileUtil.getFileSize(filePath);
        consumeData(fileSize, blackhole);
    }

    // Dynamic data generation and writing
    @Benchmark
    public void writeXlsxDynamicGeneration(Blackhole blackhole) throws Exception {
        String filePath = BenchmarkFileUtil.getTempFilePath(
                BenchmarkConfiguration.FileFormat.XLSX,
                BenchmarkConfiguration.DatasetSize.MEDIUM,
                "DynamicWriteBenchmark");

        try (ExcelWriter excelWriter =
                EasyExcel.write(filePath, BenchmarkData.class).build()) {
            WriteSheet writeSheet = EasyExcel.writerSheet("DynamicData").build();

            // Generate and write data on-the-fly
            DataGenerator.DataStream dataStream =
                    dataGenerator.generateStreamingData(BenchmarkConfiguration.DatasetSize.MEDIUM.getRowCount());

            List<BenchmarkData> batch = new ArrayList<>();
            int batchSize = 1000;

            for (BenchmarkData data : dataStream) {
                batch.add(data);

                if (batch.size() >= batchSize) {
                    excelWriter.write(batch, writeSheet);
                    batch.clear();
                }
            }

            // Write remaining data
            if (!batch.isEmpty()) {
                excelWriter.write(batch, writeSheet);
            }
        }

        long fileSize = BenchmarkFileUtil.getFileSize(filePath);
        consumeData(fileSize, blackhole);
    }

    // Write with different data characteristics
    @Benchmark
    public void writeXlsEXTRA_LARGEStrings(Blackhole blackhole) throws Exception {
        String filePath = BenchmarkFileUtil.getTempFilePath(
                BenchmarkConfiguration.FileFormat.XLSX,
                BenchmarkConfiguration.DatasetSize.MEDIUM,
                "LargeStringsWriteBenchmark");

        List<BenchmarkData> largeStringData = dataGenerator.generateDataWithCharacteristics(
                BenchmarkConfiguration.DatasetSize.MEDIUM.getRowCount(),
                DataGenerator.DataCharacteristics.defaults().withLargeStrings());

        EasyExcel.write(filePath, BenchmarkData.class).sheet("LargeStringData").doWrite(largeStringData);

        long fileSize = BenchmarkFileUtil.getFileSize(filePath);
        consumeData(fileSize, blackhole);
    }

    @Benchmark
    public void writeXlsxRepeatedValues(Blackhole blackhole) throws Exception {
        String filePath = BenchmarkFileUtil.getTempFilePath(
                BenchmarkConfiguration.FileFormat.XLSX,
                BenchmarkConfiguration.DatasetSize.MEDIUM,
                "RepeatedValuesWriteBenchmark");

        List<BenchmarkData> repeatedData = dataGenerator.generateDataWithCharacteristics(
                BenchmarkConfiguration.DatasetSize.MEDIUM.getRowCount(),
                DataGenerator.DataCharacteristics.defaults().withRepeatedValues());

        EasyExcel.write(filePath, BenchmarkData.class).sheet("RepeatedData").doWrite(repeatedData);

        long fileSize = BenchmarkFileUtil.getFileSize(filePath);
        consumeData(fileSize, blackhole);
    }

    @Benchmark
    public void writeXlsxNullValues(Blackhole blackhole) throws Exception {
        String filePath = BenchmarkFileUtil.getTempFilePath(
                BenchmarkConfiguration.FileFormat.XLSX,
                BenchmarkConfiguration.DatasetSize.MEDIUM,
                "NullValuesWriteBenchmark");

        List<BenchmarkData> nullData = dataGenerator.generateDataWithCharacteristics(
                BenchmarkConfiguration.DatasetSize.MEDIUM.getRowCount(),
                DataGenerator.DataCharacteristics.defaults().withNullValues());

        EasyExcel.write(filePath, BenchmarkData.class).sheet("NullData").doWrite(nullData);

        long fileSize = BenchmarkFileUtil.getFileSize(filePath);
        consumeData(fileSize, blackhole);
    }

    // Concurrent writing simulation
    @Benchmark
    public void writeXlsxConcurrentSimulation(Blackhole blackhole) throws Exception {
        // Simulate concurrent writing by writing multiple files
        String[] filePaths = new String[3];
        List<List<BenchmarkData>> dataSets = BenchmarkFileUtil.listOf(smallData, mediumData, smallData);

        for (int i = 0; i < 3; i++) {
            filePaths[i] = BenchmarkFileUtil.getTempFilePath(
                    BenchmarkConfiguration.FileFormat.XLSX,
                    BenchmarkConfiguration.DatasetSize.SMALL,
                    "ConcurrentWriteBenchmark_" + i);

            EasyExcel.write(filePaths[i], BenchmarkData.class)
                    .sheet("ConcurrentData" + i)
                    .doWrite(dataSets.get(i));
        }

        long totalSize = 0;
        for (String filePath : filePaths) {
            totalSize += BenchmarkFileUtil.getFileSize(filePath);
        }

        consumeData(totalSize, blackhole);
    }
}
