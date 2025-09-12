package cn.idev.excel.benchmark.comparison;

import cn.idev.excel.benchmark.analyzer.BenchmarkReportGenerator;
import cn.idev.excel.benchmark.analyzer.BenchmarkResultCollector;
import cn.idev.excel.benchmark.analyzer.ComparisonAnalysis;
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
                .measurementIterations(3)
                .jvmArgs("-Dbenchmark.session.id=" + sessionId, "-Dbenchmark.result.dir=" + resultDirPath)
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
        StringBuilder content = new StringBuilder();
        try (FileReader reader = new FileReader(file)) {
            char[] buffer = new char[1024];
            int length;
            while ((length = reader.read(buffer)) != -1) {
                content.append(buffer, 0, length);
            }
        }

        // Simple JSON parsing (could use a JSON library for more robust parsing)
        String json = content.toString();

        try {
            String library = extractJsonValue(json, "library");
            String operation = extractJsonValue(json, "operation");
            String datasetSize = extractJsonValue(json, "datasetSize");
            String fileFormat = extractJsonValue(json, "fileFormat");
            long processedRows = Long.parseLong(extractJsonValue(json, "processedRows"));
            long executionTimeMs = Long.parseLong(extractJsonValue(json, "executionTimeMs"));
            long memoryUsageBytes = Long.parseLong(extractJsonValue(json, "memoryUsageBytes"));
            long peakMemoryUsageBytes = Long.parseLong(extractJsonValue(json, "peakMemoryUsageBytes"));
            long avgMemoryUsageBytes = Long.parseLong(extractJsonValue(json, "avgMemoryUsageBytes"));
            long memoryAllocatedBytes = Long.parseLong(extractJsonValue(json, "memoryAllocatedBytes"));
            long gcCount = Long.parseLong(extractJsonValue(json, "gcCount"));
            long gcTimeMs = Long.parseLong(extractJsonValue(json, "gcTimeMs"));
            long fileSizeBytes = Long.parseLong(extractJsonValue(json, "fileSizeBytes"));
            long minMemoryUsageBytes = Long.parseLong(extractJsonValue(json, "minMemoryUsageBytes"));
            long stdDevMemoryUsageBytes = Long.parseLong(extractJsonValue(json, "stdDevMemoryUsageBytes"));
            long p95MemoryUsageBytes = Long.parseLong(extractJsonValue(json, "p95MemoryUsageBytes"));
            double memoryGrowthRate = Double.parseDouble(extractJsonValue(json, "memoryGrowthRate"));

            return new FastExcelVsPoiBenchmark.ComparisonResult(
                    library,
                    operation,
                    datasetSize,
                    fileFormat,
                    processedRows,
                    executionTimeMs,
                    peakMemoryUsageBytes,
                    avgMemoryUsageBytes,
                    memoryUsageBytes,
                    memoryAllocatedBytes,
                    gcCount,
                    gcTimeMs,
                    fileSizeBytes,
                    minMemoryUsageBytes,
                    stdDevMemoryUsageBytes,
                    p95MemoryUsageBytes,
                    memoryGrowthRate);

        } catch (Exception e) {
            System.err.println("Error parsing JSON values from file " + file.getName() + ": " + e.getMessage());
            return null;
        }
    }

    /**
     * Extract a value from simple JSON string
     */
    private static String extractJsonValue(String json, String key) {
        // Use a more flexible pattern to find the key
        String searchPattern = "\"" + key + "\"";
        int keyIndex = json.indexOf(searchPattern);
        if (keyIndex == -1) {
            throw new RuntimeException(
                    "Key not found: " + key + " in JSON: " + json.substring(0, Math.min(500, json.length())));
        }

        // Find the colon after the key
        int colonIndex = json.indexOf(':', keyIndex);
        if (colonIndex == -1) {
            throw new RuntimeException("Colon not found after key: " + key);
        }

        // Skip whitespace after colon
        int start = colonIndex + 1;
        while (start < json.length() && Character.isWhitespace(json.charAt(start))) {
            start++;
        }

        int end;
        if (start < json.length() && json.charAt(start) == '"') {
            // String value
            start++; // Skip opening quote
            end = start;
            while (end < json.length() && json.charAt(end) != '"') {
                if (json.charAt(end) == '\\') {
                    end++; // Skip escaped character
                }
                end++;
            }
            if (end >= json.length()) {
                throw new RuntimeException("Unterminated string value for key: " + key);
            }
            return json.substring(start, end);
        } else {
            // Numeric value
            end = start;
            while (end < json.length()) {
                char c = json.charAt(end);
                if (Character.isDigit(c) || c == '.' || c == '-' || c == 'E' || c == 'e' || c == '+') {
                    end++;
                } else {
                    break;
                }
            }
            return json.substring(start, end).trim();
        }
    }
}
