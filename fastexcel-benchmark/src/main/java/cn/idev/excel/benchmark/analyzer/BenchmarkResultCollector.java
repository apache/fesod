package cn.idev.excel.benchmark.analyzer;

import cn.idev.excel.benchmark.comparison.FastExcelVsPoiBenchmark.ComparisonResult;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.openjdk.jmh.annotations.Mode;

/**
 * Collects and analyzes benchmark results across multiple iterations.
 * Provides statistical analysis based on JMH BenchmarkMode.
 */
public class BenchmarkResultCollector {

    private final Mode benchmarkMode;
    private final Map<String, List<ComparisonResult>> resultsByKey;
    private final List<ComparisonResult> allResults;

    public BenchmarkResultCollector(Mode benchmarkMode) {
        this.benchmarkMode = benchmarkMode;
        this.resultsByKey = new HashMap<>();
        this.allResults = new ArrayList<>();
    }

    /**
     * Add a benchmark result to the collector
     */
    public void addResult(ComparisonResult result) {
        allResults.add(result);
        String key = generateKey(result);
        resultsByKey.computeIfAbsent(key, k -> new ArrayList<>()).add(result);
    }

    /**
     * Generate a unique key for grouping results
     */
    private String generateKey(ComparisonResult result) {
        return String.format(
                "%s-%s-%s-%s",
                result.library,
                result.operation,
                result.datasetSize != null ? result.datasetSize : "UNKNOWN",
                result.fileFormat != null ? result.fileFormat : "UNKNOWN");
    }

    /**
     * Get aggregated statistics for all result groups
     */
    public Map<String, BenchmarkStatistics> getAggregatedStatistics() {
        Map<String, BenchmarkStatistics> statistics = new HashMap<>();

        for (Map.Entry<String, List<ComparisonResult>> entry : resultsByKey.entrySet()) {
            String key = entry.getKey();
            List<ComparisonResult> results = entry.getValue();

            if (!results.isEmpty()) {
                BenchmarkStatistics stats = calculateStatistics(results);
                statistics.put(key, stats);
            }
        }

        return statistics;
    }

    /**
     * Calculate statistics for a group of results based on benchmark mode
     */
    private BenchmarkStatistics calculateStatistics(List<ComparisonResult> results) {
        if (results.isEmpty()) {
            throw new IllegalArgumentException("Results list cannot be empty");
        }

        BenchmarkStatistics.Builder builder =
                new BenchmarkStatistics.Builder().withSampleSize(results.size()).withBenchmarkMode(benchmarkMode);

        // Get the reference result for metadata
        ComparisonResult reference = results.get(0);
        builder.withLibrary(reference.library)
                .withOperation(reference.operation)
                .withDatasetSize(reference.datasetSize)
                .withFileFormat(reference.fileFormat);

        // Calculate execution time statistics
        List<Double> executionTimes =
                results.stream().mapToDouble(r -> r.executionTimeMs).boxed().collect(Collectors.toList());

        builder.withExecutionTimeStats(calculateTimeStatistics(executionTimes));

        // Calculate throughput statistics
        List<Double> throughputs = results.stream()
                .mapToDouble(ComparisonResult::getThroughputRowsPerSecond)
                .boxed()
                .collect(Collectors.toList());

        builder.withThroughputStats(calculateThroughputStatistics(throughputs));

        // Calculate memory statistics
        List<Double> peakMemoryMB = results.stream()
                .mapToDouble(ComparisonResult::getPeakMemoryUsageMB)
                .boxed()
                .collect(Collectors.toList());

        List<Double> avgMemoryMB = results.stream()
                .mapToDouble(ComparisonResult::getAvgMemoryUsageMB)
                .boxed()
                .collect(Collectors.toList());

        builder.withPeakMemoryStats(calculateMemoryStatistics(peakMemoryMB))
                .withAvgMemoryStats(calculateMemoryStatistics(avgMemoryMB));

        // Calculate GC statistics
        List<Double> gcCounts =
                results.stream().mapToDouble(r -> r.gcCount).boxed().collect(Collectors.toList());

        List<Double> gcTimes =
                results.stream().mapToDouble(r -> r.gcTimeMs).boxed().collect(Collectors.toList());

        builder.withGcCountStats(calculateBasicStatistics(gcCounts)).withGcTimeStats(calculateBasicStatistics(gcTimes));

        // Calculate overall metrics
        long totalProcessedRows =
                results.stream().mapToLong(r -> r.processedRows).sum();

        double avgFileSize =
                results.stream().mapToDouble(r -> r.fileSizeBytes).average().orElse(0.0);

        builder.withTotalProcessedRows(totalProcessedRows).withAvgFileSizeBytes((long) avgFileSize);

        return builder.build();
    }

    /**
     * Calculate time-specific statistics (considers benchmark mode)
     */
    private StatisticalSummary calculateTimeStatistics(List<Double> values) {
        StatisticalSummary basic = calculateBasicStatistics(values);

        // For AverageTime mode, lower is better
        if (benchmarkMode == Mode.AverageTime) {
            return new StatisticalSummary(
                    basic.mean,
                    basic.stdDev,
                    basic.min,
                    basic.max,
                    basic.p50,
                    basic.p95,
                    basic.p99,
                    basic.min, // best score is minimum
                    basic.max, // worst score is maximum
                    calculateConfidenceInterval(values, 0.95),
                    "ms (lower is better)");
        }

        return basic;
    }

    /**
     * Calculate throughput-specific statistics
     */
    private StatisticalSummary calculateThroughputStatistics(List<Double> values) {
        StatisticalSummary basic = calculateBasicStatistics(values);

        // For throughput, higher is better
        return new StatisticalSummary(
                basic.mean,
                basic.stdDev,
                basic.min,
                basic.max,
                basic.p50,
                basic.p95,
                basic.p99,
                basic.max, // best score is maximum
                basic.min, // worst score is minimum
                calculateConfidenceInterval(values, 0.95),
                "rows/sec (higher is better)");
    }

    /**
     * Calculate memory-specific statistics
     */
    private StatisticalSummary calculateMemoryStatistics(List<Double> values) {
        StatisticalSummary basic = calculateBasicStatistics(values);

        // For memory usage, lower is typically better
        return new StatisticalSummary(
                basic.mean,
                basic.stdDev,
                basic.min,
                basic.max,
                basic.p50,
                basic.p95,
                basic.p99,
                basic.min, // best score is minimum
                basic.max, // worst score is maximum
                calculateConfidenceInterval(values, 0.95),
                "MB (lower is better)");
    }

    /**
     * Calculate basic statistical measures
     */
    private StatisticalSummary calculateBasicStatistics(List<Double> values) {
        if (values.isEmpty()) {
            return StatisticalSummary.empty();
        }

        Collections.sort(values);

        double mean = values.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
        double variance = values.stream()
                .mapToDouble(v -> Math.pow(v - mean, 2))
                .average()
                .orElse(0.0);
        double stdDev = Math.sqrt(variance);

        double min = values.get(0);
        double max = values.get(values.size() - 1);
        double p50 = percentile(values, 50);
        double p95 = percentile(values, 95);
        double p99 = percentile(values, 99);

        ConfidenceInterval ci95 = calculateConfidenceInterval(values, 0.95);

        return new StatisticalSummary(mean, stdDev, min, max, p50, p95, p99, min, max, ci95, "");
    }

    /**
     * Calculate percentile value
     */
    private double percentile(List<Double> sortedValues, double percentile) {
        if (sortedValues.isEmpty()) {
            return 0.0;
        }

        if (percentile <= 0) return sortedValues.get(0);
        if (percentile >= 100) return sortedValues.get(sortedValues.size() - 1);

        double index = (percentile / 100.0) * (sortedValues.size() - 1);
        int lowerIndex = (int) Math.floor(index);
        int upperIndex = (int) Math.ceil(index);

        if (lowerIndex == upperIndex) {
            return sortedValues.get(lowerIndex);
        }

        double lowerValue = sortedValues.get(lowerIndex);
        double upperValue = sortedValues.get(upperIndex);
        double weight = index - lowerIndex;

        return lowerValue + weight * (upperValue - lowerValue);
    }

    /**
     * Calculate confidence interval
     */
    private ConfidenceInterval calculateConfidenceInterval(List<Double> values, double confidence) {
        if (values.size() < 2) {
            double value = values.isEmpty() ? 0.0 : values.get(0);
            return new ConfidenceInterval(value, value, confidence);
        }

        double mean = values.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
        double stdDev = Math.sqrt(values.stream()
                .mapToDouble(v -> Math.pow(v - mean, 2))
                .average()
                .orElse(0.0));

        // Using t-distribution for small samples
        double tValue = getTValue(values.size() - 1, confidence);
        double margin = tValue * (stdDev / Math.sqrt(values.size()));

        return new ConfidenceInterval(mean - margin, mean + margin, confidence);
    }

    /**
     * Get t-value for confidence interval (simplified approximation)
     */
    private double getTValue(int degreesOfFreedom, double confidence) {
        // Simplified t-values for common confidence levels
        if (confidence >= 0.99) return 2.576; // 99%
        if (confidence >= 0.95) return 1.96; // 95%
        if (confidence >= 0.90) return 1.645; // 90%
        return 1.96; // default to 95%
    }

    /**
     * Get comparison analysis between libraries
     */
    public ComparisonAnalysis getComparisonAnalysis() {
        Map<String, BenchmarkStatistics> stats = getAggregatedStatistics();
        return new ComparisonAnalysis(stats, benchmarkMode);
    }

    /**
     * Get all collected results
     */
    public List<ComparisonResult> getAllResults() {
        return new ArrayList<>(allResults);
    }

    /**
     * Get results grouped by key
     */
    public Map<String, List<ComparisonResult>> getGroupedResults() {
        return new HashMap<>(resultsByKey);
    }

    /**
     * Clear all collected results
     */
    public void clear() {
        allResults.clear();
        resultsByKey.clear();
    }

    /**
     * Get summary report as formatted string
     */
    public String getSummaryReport() {
        StringBuilder report = new StringBuilder();
        report.append("=== Benchmark Results Summary ===\n");
        report.append(String.format("Benchmark Mode: %s\n", benchmarkMode));
        report.append(String.format("Total Results: %d\n", allResults.size()));
        report.append(String.format("Unique Configurations: %d\n\n", resultsByKey.size()));

        Map<String, BenchmarkStatistics> stats = getAggregatedStatistics();
        for (Map.Entry<String, BenchmarkStatistics> entry : stats.entrySet()) {
            report.append(entry.getValue().toDetailedString()).append("\n\n");
        }

        // Add comparison analysis
        ComparisonAnalysis analysis = getComparisonAnalysis();
        report.append(analysis.toString());

        return report.toString();
    }
}
