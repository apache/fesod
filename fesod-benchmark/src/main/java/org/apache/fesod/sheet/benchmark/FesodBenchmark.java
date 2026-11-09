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
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.fesod.sheet.ExcelReader;
import org.apache.fesod.sheet.FesodSheet;
import org.apache.fesod.sheet.benchmark.data.BenchmarkData;
import org.apache.fesod.sheet.benchmark.util.BenchmarkFileUtil;
import org.apache.fesod.sheet.benchmark.util.DataGenerator;
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
 * Core Fesod benchmark suite: read and write, the two hot paths of the library.
 *
 * <ul>
 *   <li>Operations: {@link #write} and {@link #read}</li>
 *   <li>Formats: XLSX and CSV</li>
 *   <li>Dataset sizes: SMALL (1K), MEDIUM (10K) and LARGE (100K rows), 20 columns each —
 *       LARGE guards the project's core promise of streaming through big files without
 *       loading them fully into memory</li>
 *   <li>Average time per operation (ms/op); add {@code -prof gc} to also measure
 *       allocation per op, the most direct signal for memory-efficiency characteristics</li>
 * </ul>
 *
 * <p>The execution contract (forks, warmup, measurement, fixed JVM args) is part of the
 * class annotations and should be kept stable so that results collected at different
 * times remain comparable. Test data is generated with a fixed seed (see
 * {@link DataGenerator}) for reproducibility. Run it through the shaded
 * {@code benchmarks.jar} as described in {@code fesod-benchmark/benchmark.md}.
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 2)
@Fork(
        value = FesodBenchmark.FORKS,
        jvmArgs = {"-Xms1g", "-Xmx1g", "-XX:+UseG1GC"})
public class FesodBenchmark {

    /** Fork count used when the suite is executed directly through the JMH launcher. */
    public static final int FORKS = 3;

    @Param({"SMALL", "MEDIUM", "LARGE"})
    private String datasetSize;

    @Param({"XLSX", "CSV"})
    private String fileFormat;

    private List<BenchmarkData> data;
    private File readFile;
    private File writeFile;
    private String extension;

    @Setup(Level.Trial)
    public void setupTrial() {
        BenchmarkConfiguration.DatasetSize size = BenchmarkConfiguration.DatasetSize.valueOf(datasetSize);
        extension = BenchmarkConfiguration.FileFormat.valueOf(fileFormat).getExtension();
        data = DataGenerator.generateTestData(size);

        readFile = BenchmarkFileUtil.createTestFile(
                String.format("fesod_read_%s_%s.%s", datasetSize.toLowerCase(), extension, extension));
        FesodSheet.write(readFile, BenchmarkData.class).sheet("Sheet1").doWrite(data);

        // Fixed target for the write benchmark: every invocation overwrites the same
        // file, and cleanup happens once in tearDownTrial so no I/O other than the
        // write itself is part of the measured op.
        writeFile = BenchmarkFileUtil.createTestFile(
                String.format("fesod_write_%s_%s.%s", datasetSize.toLowerCase(), extension, extension));

        System.out.printf("FesodBenchmark setup: %s / %s / %d rows%n", fileFormat, datasetSize, data.size());
    }

    @TearDown(Level.Trial)
    public void tearDownTrial() {
        BenchmarkFileUtil.delete(readFile);
        BenchmarkFileUtil.delete(writeFile);
    }

    /**
     * Write {@code datasetSize} rows of 20 columns to the fixed output file
     * (overwritten each invocation, deleted in {@link #tearDownTrial}).
     */
    @Benchmark
    public long write(Blackhole blackhole) {
        FesodSheet.write(writeFile, BenchmarkData.class).sheet("Sheet1").doWrite(data);
        blackhole.consume(writeFile.length());
        return data.size();
    }

    /**
     * Read the whole pre-generated file through the streaming reader.
     */
    @Benchmark
    public long read(Blackhole blackhole) {
        AtomicLong processedRows = new AtomicLong(0);

        try (ExcelReader excelReader = FesodSheet.read(
                        readFile, BenchmarkData.class, new ReadListener<BenchmarkData>() {
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
}
