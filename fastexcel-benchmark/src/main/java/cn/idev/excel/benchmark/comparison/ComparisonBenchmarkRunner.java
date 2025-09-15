package cn.idev.excel.benchmark.comparison;

import cn.idev.excel.benchmark.analyzer.BenchmarkReportGenerator;
import cn.idev.excel.benchmark.analyzer.BenchmarkResultCollector;
import cn.idev.excel.benchmark.analyzer.ComparisonAnalysis;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
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
        System.out.println("Benchmark execution completed.");

        // Read results from files and generate reports
        System.out.println("\nReading results from files...");
        try {
            // Find the actual result directory (in case session ID differs)
            File resultBaseDir = new File(resultDirPath);
            File actualResultDir = findActualResultDirectory(resultBaseDir, sessionId);

            if (actualResultDir == null) {
                System.err.println("No result directory found! Check if benchmark ran successfully.");
                return;
            }

            System.out.println("Using result directory: " + actualResultDir.getAbsolutePath());

            List<FastExcelVsPoiBenchmark.ComparisonResult> results = readResultsFromFiles(actualResultDir);
            System.out.println("Read " + results.size() + " results from files.");

            if (results.isEmpty()) {
                System.err.println("No results found! Check if benchmark ran successfully.");
                return;
            }

            // Create result collector and add all results
            BenchmarkResultCollector collector =
                    new BenchmarkResultCollector(org.openjdk.jmh.annotations.Mode.AverageTime);
            for (FastExcelVsPoiBenchmark.ComparisonResult result : results) {
                collector.addResult(result);
            }

            // Generate comprehensive analysis report
            System.out.println("\nGenerating comprehensive analysis report...");
            ComparisonAnalysis analysis = collector.getComparisonAnalysis();
            BenchmarkReportGenerator reportGenerator = new BenchmarkReportGenerator();

            // Print summary to console
            String summary = collector.getSummaryReport();
            System.out.println(summary);
            System.out.println(analysis.getSummary());

            // Generate structured reports
            Path outputDir = Paths.get("target/benchmark-reports");
            Files.createDirectories(outputDir);

            // Generate HTML report only
            Path htmlPath = outputDir.resolve("benchmark-comparison.html");
            reportGenerator.generateHtmlReport(analysis, collector, htmlPath);
            System.out.println("HTML Report generated: " + htmlPath.toAbsolutePath());
        } catch (IOException e) {
            System.err.println("Error reading results or generating reports: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("Benchmark completed successfully!");
    }

    /**
     * Find the actual result directory (in case session ID differs due to timing)
     */
    private static File findActualResultDirectory(File resultBaseDir, String sessionId) {
        if (!resultBaseDir.exists() || !resultBaseDir.isDirectory()) {
            return null;
        }

        // First try the exact session ID
        File exactDir = new File(resultBaseDir, sessionId);
        if (exactDir.exists() && exactDir.isDirectory()) {
            return exactDir;
        }

        // If exact match fails, find the most recent directory with JSON files
        File[] dirs = resultBaseDir.listFiles(File::isDirectory);
        if (dirs == null || dirs.length == 0) {
            return null;
        }

        File mostRecentDir = null;
        long mostRecentTime = 0;

        for (File dir : dirs) {
            // Check if directory contains JSON result files
            File[] jsonFiles = dir.listFiles((d, name) -> name.endsWith(".json"));
            if (jsonFiles != null && jsonFiles.length > 0) {
                long dirTime = dir.lastModified();
                if (dirTime > mostRecentTime) {
                    mostRecentTime = dirTime;
                    mostRecentDir = dir;
                }
            }
        }

        return mostRecentDir;
    }

    /**
     * Read all ComparisonResult objects from JSON files in the result directory
     */
    private static List<FastExcelVsPoiBenchmark.ComparisonResult> readResultsFromFiles(File resultDir)
            throws IOException {
        List<FastExcelVsPoiBenchmark.ComparisonResult> results = new ArrayList<>();

        if (!resultDir.exists() || !resultDir.isDirectory()) {
            System.err.println("Result directory does not exist: " + resultDir.getAbsolutePath());
            return results;
        }

        File[] files = resultDir.listFiles((dir, name) -> name.endsWith(".json"));
        if (files == null || files.length == 0) {
            System.err.println("No JSON result files found in: " + resultDir.getAbsolutePath());
            return results;
        }

        System.out.println("Found " + files.length + " result files:");
        for (File file : files) {
            System.out.println("  - " + file.getName());
            try {
                FastExcelVsPoiBenchmark.ComparisonResult result = parseResultFromJsonFile(file);
                if (result != null) {
                    results.add(result);
                }
            } catch (Exception e) {
                System.err.println("Error parsing result file " + file.getName() + ": " + e.getMessage());
            }
        }

        return results;
    }

    /**
     * Parse a ComparisonResult from a JSON file
     */
    private static FastExcelVsPoiBenchmark.ComparisonResult parseResultFromJsonFile(File file) throws IOException {
        try (FileReader reader = new FileReader(file)) {
            // Use fastjson2 for parsing JSON instead of custom implementation
            JSONObject jsonObject = JSON.parseObject(reader);

            return new FastExcelVsPoiBenchmark.ComparisonResult(
                    jsonObject.getString("library"),
                    jsonObject.getString("operation"),
                    jsonObject.getString("datasetSize"),
                    jsonObject.getString("fileFormat"),
                    jsonObject.getLongValue("processedRows"),
                    jsonObject.getLongValue("executionTimeMs"),
                    jsonObject.getLongValue("peakMemoryUsageBytes"),
                    jsonObject.getLongValue("avgMemoryUsageBytes"),
                    jsonObject.getLongValue("memoryUsageBytes"),
                    jsonObject.getLongValue("memoryAllocatedBytes"),
                    jsonObject.getLongValue("gcCount"),
                    jsonObject.getLongValue("gcTimeMs"),
                    jsonObject.getLongValue("fileSizeBytes"),
                    jsonObject.getLongValue("minMemoryUsageBytes"),
                    jsonObject.getLongValue("stdDevMemoryUsageBytes"),
                    jsonObject.getLongValue("p95MemoryUsageBytes"),
                    jsonObject.getDoubleValue("memoryGrowthRate"));
        } catch (Exception e) {
            System.err.println("Error parsing JSON values from file " + file.getName() + ": " + e.getMessage());
            return null;
        }
    }
}
