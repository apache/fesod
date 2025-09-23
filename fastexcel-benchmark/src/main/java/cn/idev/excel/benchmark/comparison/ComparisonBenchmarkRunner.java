package cn.idev.excel.benchmark.comparison;

import java.io.File;
import java.util.UUID;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

/**
 * Enhanced comparison benchmark runner with file-based result collection
 * to solve JMH fork=0 static variable sharing issues
 */
public class ComparisonBenchmarkRunner {

    public static void main(String[] args) throws RunnerException {
        System.out.println("Starting Enhanced FastExcel vs Apache POI Comparison Benchmark...");

        // Generate unique session ID for this benchmark run
        String sessionId = UUID.randomUUID().toString().substring(0, 8) + "_" + System.currentTimeMillis();
        String resultDirPath = "target/benchmark-results";
        File resultDir = new File(resultDirPath, sessionId);

        System.out.println("Session ID: " + sessionId);
        System.out.println("Result directory: " + resultDir.getAbsolutePath());

        // Ensure target directory exists
        File targetDir = new File("target");
        if (!targetDir.exists()) {
            targetDir.mkdirs();
        }

        // Configure benchmark options with session ID as system property
        Options opt = new OptionsBuilder()
                .include(FastExcelVsPoiBenchmark.class.getSimpleName())
                .param("datasetSize", "SMALL", "MEDIUM", "LARGE", "EXTRA_LARGE")
                .param("fileFormat", "XLSX")
                .forks(1)
                .warmupIterations(2)
                .measurementIterations(5)
                .jvmArgs(
                        "-Xmx6g",
                        "-XX:+UseG1GC",
                        "-Dbenchmark.session.id=" + sessionId,
                        "-Dbenchmark.result.dir=" + resultDirPath)
                .result("target/jmh-results-" + sessionId + ".json")
                .resultFormat(ResultFormatType.JSON)
                .build();

        // Run benchmarks
        System.out.println("Starting benchmark execution...");
        new Runner(opt).run();
        System.out.println("Benchmark completed successfully!");
    }
}
