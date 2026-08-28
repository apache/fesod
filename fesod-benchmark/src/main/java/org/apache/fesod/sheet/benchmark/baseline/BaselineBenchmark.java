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

package org.apache.fesod.sheet.benchmark.baseline;

import java.io.File;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.fesod.sheet.EasyExcel;
import org.apache.fesod.sheet.ExcelReader;
import org.apache.fesod.sheet.benchmark.core.BenchmarkConfiguration;
import org.apache.fesod.sheet.benchmark.data.BenchmarkData;
import org.apache.fesod.sheet.benchmark.utils.BenchmarkFileUtil;
import org.apache.fesod.sheet.benchmark.utils.DataGenerator;
import org.apache.fesod.sheet.context.AnalysisContext;
import org.apache.fesod.sheet.read.listener.ReadListener;
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
 * Performance baseline suite used by the regression CI
 * ({@code .github/workflows/benchmark.yml}).
 *
 * <p>This class is the performance contract of Fesod: every benchmark method here is
 * tracked against the committed baseline in {@code fesod-benchmark/baseline/} and a
 * regression beyond the configured threshold fails the CI job. It is intentionally
 * kept small, stable and fast so that it can run on every pull request:
 *
 * <ul>
 *   <li>Operations: {@link #write} and {@link #read} — the two hot paths of the library</li>
 *   <li>Formats: XLSX and CSV</li>
 *   <li>Dataset sizes: SMALL (1K rows) and MEDIUM (10K rows), 20 columns each</li>
 *   <li>Average time per operation (ms/op) plus allocation per op (via the gc profiler)</li>
 * </ul>
 *
 * <p>Guidelines when evolving this suite:
 *
 * <ul>
 *   <li>Do not rename existing benchmark methods or parameters — they are the baseline keys;
 *       renaming requires a baseline refresh (see {@code fesod-benchmark/benchmark.md}).</li>
 *   <li>Adding new benchmark methods is safe: they show up as NEW and become part of the
 *       contract at the next baseline refresh.</li>
 *   <li>Data is generated with a fixed seed (see {@link DataGenerator}) for reproducibility.</li>
 *   <li>Always run via {@link BaselineRunner} so JVM settings match the recorded baseline.</li>
 * </ul>
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
@Fork(
        value = BaselineBenchmark.CI_FORKS,
        jvmArgs = {"-Xms1g", "-Xmx1g", "-XX:+UseG1GC"})
public class BaselineBenchmark {

    /** Fork count used when the suite is executed directly through the JMH launcher. */
    public static final int CI_FORKS = 2;

    @Param({"SMALL", "MEDIUM"})
    private String datasetSize;

    @Param({"XLSX", "CSV"})
    private String fileFormat;

    private List<BenchmarkData> data;
    private File readFile;

    @Setup(Level.Trial)
    public void setupTrial() {
        BenchmarkConfiguration.DatasetSize size = BenchmarkConfiguration.DatasetSize.valueOf(datasetSize);
        data = DataGenerator.generateTestData(size);

        String fileName = String.format(
                "baseline_read_%s_%s.%s",
                datasetSize.toLowerCase(), fileFormat.toLowerCase(), fileFormat.toLowerCase());
        readFile = BenchmarkFileUtil.createTestFile(fileName);
        EasyExcel.write(readFile, BenchmarkData.class).sheet("Sheet1").doWrite(data);

        System.out.printf("Baseline setup: %s / %s / %d rows%n", fileFormat, datasetSize, data.size());
    }

    @TearDown(Level.Trial)
    public void tearDownTrial() {
        if (readFile != null && readFile.exists()) {
            readFile.delete();
        }
    }

    /**
     * Write {@code datasetSize} rows of 20 columns to a fresh file (deleted afterwards).
     */
    @Benchmark
    public long write(Blackhole blackhole) {
        File outputFile = BenchmarkFileUtil.createTestFile(String.format(
                "baseline_write_%s_%s_%s.%s",
                datasetSize.toLowerCase(),
                fileFormat.toLowerCase(),
                UUID.randomUUID().toString().substring(0, 8),
                fileFormat.toLowerCase()));

        try {
            EasyExcel.write(outputFile, BenchmarkData.class).sheet("Sheet1").doWrite(data);
            blackhole.consume(outputFile.length());
        } finally {
            if (outputFile.exists()) {
                outputFile.delete();
            }
        }
        return data.size();
    }

    /**
     * Read the whole pre-generated file through the streaming reader.
     */
    @Benchmark
    public long read(Blackhole blackhole) {
        AtomicLong processedRows = new AtomicLong(0);

        ExcelReader excelReader = EasyExcel.read(readFile, BenchmarkData.class, new ReadListener<BenchmarkData>() {
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
                .build();
        try {
            excelReader.readAll();
        } finally {
            excelReader.finish();
        }
        return processedRows.get();
    }
}
