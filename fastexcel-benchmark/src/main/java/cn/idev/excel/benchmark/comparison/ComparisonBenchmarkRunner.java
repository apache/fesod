package cn.idev.excel.benchmark.comparison;

import java.io.IOException;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

/**
 * Runner for FastExcel vs Apache POI comparison benchmarks.
 * Generates comprehensive comparison reports with performance analysis.
 */
public class ComparisonBenchmarkRunner {

    /**
     * Utility method to repeat a string a given number of times
     * @param s the string to repeat
     * @param times the number of times to repeat
     * @return the repeated string
     */
    private static String repeat(String s, int times) {
        StringBuilder sb = new StringBuilder(s.length() * times);
        for (int i = 0; i < times; i++) {
            sb.append(s);
        }
        return sb.toString();
    }

    public static void main(String[] args) throws RunnerException, IOException {
        System.out.println("Starting FastExcel vs Apache POI Comparison Benchmarks");
        System.out.println(repeat("=", 70));

        // Run comparison benchmarks
        runComparisonBenchmarks();

        System.out.println("\nComparison benchmarks completed successfully!");
    }

    /**
     * Run all comparison benchmarks
     */
    private static void runComparisonBenchmarks() throws RunnerException {
        System.out.println("\nRunning FastExcel vs Apache POI Benchmarks");
        System.out.println(repeat("-", 50));

        Options options = new OptionsBuilder()
                .include(cn.idev.excel.benchmark.comparison.FastExcelVsPoiBenchmark.class.getSimpleName())
                .param("datasetSize", "SMALL", "MEDIUM", "LARGE", "EXTRA_LARGE")
                .param("fileFormat", "XLSX", "XLS")
                .warmupIterations(2)
                .measurementIterations(3)
                .forks(1)
                .build();

        new Runner(options).run();
    }
}
