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
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

/**
 * Single entry point for the performance baseline suite used by the regression CI.
 *
 * <p>Runs {@link BaselineBenchmark} with a fixed execution contract (JVM settings, forks,
 * iterations, gc profiler) so that a run recorded today is comparable with the committed
 * baseline. Every knob can be overridden with system properties for local experiments,
 * but CI always uses the defaults:
 *
 * <pre>
 *   benchmark.forks                   (default 2)
 *   benchmark.warmup.iterations       (default 3)
 *   benchmark.warmup.seconds          (default 1)
 *   benchmark.measurement.iterations  (default 5)
 *   benchmark.measurement.seconds     (default 1)
 *   benchmark.datasetSizes            (default SMALL,MEDIUM,LARGE)
 *   benchmark.fileFormats             (default XLSX,CSV)
 *   benchmark.result                  (default target/baseline-current.json)
 * </pre>
 *
 * <p>Usage with the shaded jar:
 * <pre>
 *   java -cp fesod-benchmark/target/benchmarks.jar \
 *       org.apache.fesod.sheet.benchmark.baseline.BaselineRunner
 * </pre>
 *
 * <p>The gc profiler is always enabled: allocation per operation is far less noisy than
 * wall-clock time on shared CI runners and is tracked in the baseline as a second signal.
 */
public final class BaselineRunner {

    private static final String[] FIXED_JVM_ARGS = {"-Xms1g", "-Xmx1g", "-XX:+UseG1GC"};

    private BaselineRunner() {}

    public static void main(String[] args) throws RunnerException {
        int forks = intProperty("benchmark.forks", 3);
        int warmupIterations = intProperty("benchmark.warmup.iterations", 3);
        int warmupSeconds = intProperty("benchmark.warmup.seconds", 1);
        int measurementIterations = intProperty("benchmark.measurement.iterations", 5);
        int measurementSeconds = intProperty("benchmark.measurement.seconds", 2);
        String[] datasetSizes =
                property("benchmark.datasetSizes", "SMALL,MEDIUM,LARGE").split(",");
        String[] fileFormats = property("benchmark.fileFormats", "XLSX,CSV").split(",");
        String resultFile = property("benchmark.result", "target/baseline-current.json");

        printEnvironment(forks, warmupIterations, warmupSeconds, measurementIterations, measurementSeconds, resultFile);

        File resultParent = new File(resultFile).getAbsoluteFile().getParentFile();
        if (resultParent != null && !resultParent.exists()) {
            resultParent.mkdirs();
        }

        Options opt = new OptionsBuilder()
                .include(BaselineBenchmark.class.getSimpleName())
                .param("datasetSize", trim(datasetSizes))
                .param("fileFormat", trim(fileFormats))
                .forks(forks)
                .warmupIterations(warmupIterations)
                .warmupTime(TimeValue.seconds(warmupSeconds))
                .measurementIterations(measurementIterations)
                .measurementTime(TimeValue.seconds(measurementSeconds))
                .addProfiler("gc")
                .shouldFailOnError(true)
                .jvmArgs(FIXED_JVM_ARGS)
                .result(resultFile)
                .resultFormat(ResultFormatType.JSON)
                .build();

        new Runner(opt).run();

        System.out.println();
        System.out.println("=====================================================");
        System.out.println("Baseline run finished. Results written to: " + resultFile);
        System.out.println("Compare with the committed baseline via BaselineComparator.");
        System.out.println("=====================================================");
    }

    private static void printEnvironment(
            int forks,
            int warmupIterations,
            int warmupSeconds,
            int measurementIterations,
            int measurementSeconds,
            String resultFile) {
        System.out.println("=====================================================");
        System.out.println("Fesod baseline suite");
        System.out.println("  JVM        : " + System.getProperty("java.vm.name") + " "
                + System.getProperty("java.version") + " (" + System.getProperty("java.vendor") + ")");
        System.out.println("  OS         : " + System.getProperty("os.name") + " " + System.getProperty("os.version")
                + " " + System.getProperty("os.arch"));
        System.out.println("  Forks      : " + forks);
        System.out.println("  Warmup     : " + warmupIterations + " x " + warmupSeconds + "s");
        System.out.println("  Measurement: " + measurementIterations + " x " + measurementSeconds + "s");
        System.out.println("  JVM args   : " + String.join(" ", FIXED_JVM_ARGS));
        System.out.println("  Profiler   : gc");
        System.out.println("  Result     : " + resultFile);
        System.out.println("=====================================================");
    }

    private static String[] trim(String[] values) {
        String[] trimmed = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            trimmed[i] = values[i].trim();
        }
        return trimmed;
    }

    private static String property(String name, String defaultValue) {
        String value = System.getProperty(name);
        return value == null || value.trim().isEmpty() ? defaultValue : value.trim();
    }

    private static int intProperty(String name, int defaultValue) {
        String value = System.getProperty(name);
        if (value == null || value.trim().isEmpty()) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            System.out.println("WARN: cannot parse -D" + name + "=" + value + ", using default " + defaultValue);
            return defaultValue;
        }
    }
}
