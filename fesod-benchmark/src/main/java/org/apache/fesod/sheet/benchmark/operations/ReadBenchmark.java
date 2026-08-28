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

package org.apache.fesod.sheet.benchmark.operations;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.fesod.sheet.EasyExcel;
import org.apache.fesod.sheet.benchmark.core.AbstractBenchmark;
import org.apache.fesod.sheet.benchmark.core.BenchmarkConfiguration;
import org.apache.fesod.sheet.benchmark.data.BenchmarkData;
import org.apache.fesod.sheet.benchmark.utils.BenchmarkFileUtil;
import org.apache.fesod.sheet.benchmark.utils.DataGenerator;
import org.apache.fesod.sheet.context.AnalysisContext;
import org.apache.fesod.sheet.read.listener.ReadListener;
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
@Fork(
        value = 1,
        jvmArgs = {"-Xms2g", "-Xmx2g"})
public class ReadBenchmark extends AbstractBenchmark {

    // Test files for different sizes and formats
    private String xlsxSmallFile;
    private String xlsxMediumFile;
    private String xlsxLargeFile;
    private String xlsxExtraLargeFile;

    private String csvSmallFile;
    private String csvMediumFile;
    private String csvLargeFile;
    private String csvExtraLargeFile;

    @Override
    protected void setupBenchmark() throws Exception {
        logger.info("Setting up read benchmark test files...");

        // Generate test files for all sizes and formats
        generateTestFiles();

        logger.info("Read benchmark setup completed");
    }

    @Override
    protected void tearDownBenchmark() throws Exception {
        // Clean up temporary files
        BenchmarkFileUtil.cleanupTempFiles();
        logger.info("Read benchmark cleanup completed");
    }

    private void generateTestFiles() {
        DataGenerator generator = new DataGenerator();

        // Generate XLSX files
        xlsxSmallFile = generateAndWriteTestFile(
                BenchmarkConfiguration.FileFormat.XLSX, BenchmarkConfiguration.DatasetSize.SMALL, generator);
        xlsxMediumFile = generateAndWriteTestFile(
                BenchmarkConfiguration.FileFormat.XLSX, BenchmarkConfiguration.DatasetSize.MEDIUM, generator);
        xlsxLargeFile = generateAndWriteTestFile(
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
        CountingReadListener listener = new CountingReadListener();
        EasyExcel.read(xlsxSmallFile, BenchmarkData.class, listener).sheet().doRead();
        consumeData(listener.getCount(), blackhole);
    }

    @Benchmark
    public void readXlsxMedium(Blackhole blackhole) throws Exception {
        CountingReadListener listener = new CountingReadListener();
        EasyExcel.read(xlsxMediumFile, BenchmarkData.class, listener).sheet().doRead();
        consumeData(listener.getCount(), blackhole);
    }

    @Benchmark
    public void readXlsxLarge(Blackhole blackhole) throws Exception {
        CountingReadListener listener = new CountingReadListener();
        EasyExcel.read(xlsxLargeFile, BenchmarkData.class, listener).sheet().doRead();
        consumeData(listener.getCount(), blackhole);
    }

    @Benchmark
    public void readXlsxExtraLarge(Blackhole blackhole) throws Exception {
        CountingReadListener listener = new CountingReadListener();
        EasyExcel.read(xlsxExtraLargeFile, BenchmarkData.class, listener)
                .sheet()
                .doRead();
        consumeData(listener.getCount(), blackhole);
    }

    // CSV Read Benchmarks - Different sizes
    @Benchmark
    public void readCsvSmall(Blackhole blackhole) throws Exception {
        CountingReadListener listener = new CountingReadListener();
        EasyExcel.read(csvSmallFile, BenchmarkData.class, listener).sheet().doRead();
        consumeData(listener.getCount(), blackhole);
    }

    @Benchmark
    public void readCsvMedium(Blackhole blackhole) throws Exception {
        CountingReadListener listener = new CountingReadListener();
        EasyExcel.read(csvMediumFile, BenchmarkData.class, listener).sheet().doRead();
        consumeData(listener.getCount(), blackhole);
    }

    @Benchmark
    public void readCsvLarge(Blackhole blackhole) throws Exception {
        CountingReadListener listener = new CountingReadListener();
        EasyExcel.read(csvLargeFile, BenchmarkData.class, listener).sheet().doRead();
        consumeData(listener.getCount(), blackhole);
    }

    @Benchmark
    public void readCsvExtraLarge(Blackhole blackhole) throws Exception {
        CountingReadListener listener = new CountingReadListener();
        EasyExcel.read(csvExtraLargeFile, BenchmarkData.class, listener).sheet().doRead();
        consumeData(listener.getCount(), blackhole);
    }

    // Stream reading benchmarks
    @Benchmark
    public void readXlsxLargeWithStreaming(Blackhole blackhole) throws Exception {
        CountingReadListener listener = new CountingReadListener();
        try (FileInputStream fis = new FileInputStream(xlsxLargeFile)) {
            EasyExcel.read(fis, BenchmarkData.class, listener).sheet().doRead();
            consumeData(listener.getCount(), blackhole);
        }
    }

    @Benchmark
    public void readCsvLargeWithStreaming(Blackhole blackhole) throws Exception {
        CountingReadListener listener = new CountingReadListener();
        try (FileInputStream fis = new FileInputStream(csvLargeFile)) {
            EasyExcel.read(fis, BenchmarkData.class, listener).sheet().doRead();
            consumeData(listener.getCount(), blackhole);
        }
    }

    // Different listener types benchmarks
    @Benchmark
    public void readXlsxLargeCountingOnly(Blackhole blackhole) throws Exception {
        CountingReadListener listener = new CountingReadListener();
        EasyExcel.read(xlsxLargeFile, BenchmarkData.class, listener).sheet().doRead();
        consumeData(listener.getCount(), blackhole);
    }

    @Benchmark
    public void readXlsxLargeCollecting(Blackhole blackhole) throws Exception {
        CollectingReadListener listener = new CollectingReadListener();
        EasyExcel.read(xlsxLargeFile, BenchmarkData.class, listener).sheet().doRead();
        consumeData(listener.getData().size(), blackhole);
    }

    @Benchmark
    public void readXlsxLargeProcessing(Blackhole blackhole) throws Exception {
        ProcessingReadListener listener = new ProcessingReadListener();
        EasyExcel.read(xlsxLargeFile, BenchmarkData.class, listener).sheet().doRead();
        consumeData(listener.getProcessedCount(), blackhole);
    }

    // Head configuration benchmarks
    @Benchmark
    public void readXlsxLargeWithHeadRowNumber(Blackhole blackhole) throws Exception {
        CountingReadListener listener = new CountingReadListener();
        EasyExcel.read(xlsxLargeFile, BenchmarkData.class, listener)
                .headRowNumber(1)
                .sheet()
                .doRead();
        consumeData(listener.getCount(), blackhole);
    }

    @Benchmark
    public void readXlsxLargeSkipRows(Blackhole blackhole) throws Exception {
        CountingReadListener listener = new CountingReadListener();
        EasyExcel.read(xlsxLargeFile, BenchmarkData.class, listener)
                .headRowNumber(2) // Skip first row
                .sheet()
                .doRead();
        consumeData(listener.getCount(), blackhole);
    }

    // Multiple sheets reading (using same file)
    @Benchmark
    public void readXlsxMultipleSheets(Blackhole blackhole) throws Exception {
        CountingReadListener listener = new CountingReadListener();
        // Read the same sheet 3 times to simulate multi-sheet processing
        for (int i = 0; i < 3; i++) {
            EasyExcel.read(xlsxMediumFile, BenchmarkData.class, listener)
                    .sheet(0) // Always read first sheet since our test files have only one
                    .doRead();
        }
        consumeData(listener.getCount(), blackhole);
    }

    // Memory efficient reading with limited collections
    @Benchmark
    public void readXlsxLargeMemoryEfficient(Blackhole blackhole) throws Exception {
        LimitedCollectingReadListener listener = new LimitedCollectingReadListener(1000);
        EasyExcel.read(xlsxLargeFile, BenchmarkData.class, listener).sheet().doRead();
        consumeData(listener.getData().size(), blackhole);
    }

    // Error handling benchmark
    @Benchmark
    public void readXlsxWithErrorHandling(Blackhole blackhole) throws Exception {
        ErrorHandlingReadListener listener = new ErrorHandlingReadListener();
        EasyExcel.read(xlsxLargeFile, BenchmarkData.class, listener).sheet().doRead();
        consumeData(listener.getProcessedCount(), blackhole);
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
            processedCount.incrementAndGet();
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
