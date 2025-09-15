package cn.idev.excel.benchmark.analyzer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.openjdk.jmh.annotations.Mode;

/**
 * Analyzes and compares benchmark statistics between different libraries
 */
public class ComparisonAnalysis {

    private final Map<String, BenchmarkStatistics> statistics;
    private final Mode benchmarkMode;
    private final List<ComparisonResult> comparisons;

    public ComparisonAnalysis(Map<String, BenchmarkStatistics> statistics, Mode benchmarkMode) {
        this.statistics = statistics;
        this.benchmarkMode = benchmarkMode;
        this.comparisons = generateComparisons();
    }

    /**
     * Generate pairwise comparisons between libraries for the same operation
     */
    private List<ComparisonResult> generateComparisons() {
        List<ComparisonResult> results = new ArrayList<>();

        // Group by operation, dataset size, and file format
        Map<String, List<BenchmarkStatistics>> byConfig = new HashMap<>();
        for (BenchmarkStatistics stats : statistics.values()) {
            String configKey = String.format(
                    "%s-%s-%s",
                    stats.operation,
                    stats.datasetSize != null ? stats.datasetSize : "UNKNOWN",
                    stats.fileFormat != null ? stats.fileFormat : "UNKNOWN");
            byConfig.computeIfAbsent(configKey, k -> new ArrayList<>()).add(stats);
        }

        // Compare within each configuration
        for (Map.Entry<String, List<BenchmarkStatistics>> entry : byConfig.entrySet()) {
            List<BenchmarkStatistics> configStats = entry.getValue();

            if (configStats.size() >= 2) {
                // Find FastExcel and Apache POI statistics for consistent comparison order
                BenchmarkStatistics fastExcelStats = null;
                BenchmarkStatistics apachePoiStats = null;

                for (BenchmarkStatistics stats : configStats) {
                    if ("FastExcel".equals(stats.library)) {
                        fastExcelStats = stats;
                    } else if ("Apache POI".equals(stats.library)) {
                        apachePoiStats = stats;
                    }
                }

                // Only create comparison if both FastExcel and Apache POI are present
                if (fastExcelStats != null && apachePoiStats != null) {
                    // Always compare FastExcel vs Apache POI (consistent order)
                    String[] parts = entry.getKey().split("-");
                    String operation = parts[0];
                    ComparisonResult comparison = compareTwo(fastExcelStats, apachePoiStats, operation);
                    results.add(comparison);
                }
            }
        }

        return results;
    }

    /**
     * Compare two benchmark statistics
     */
    private ComparisonResult compareTwo(BenchmarkStatistics stats1, BenchmarkStatistics stats2, String operation) {
        // Throughput comparison
        double throughputRatio = stats1.throughputStats.mean / stats2.throughputStats.mean;
        boolean throughputSignificant = stats1.throughputStats.confidenceInterval95.isSignificantlyDifferentFrom(
                stats2.throughputStats.confidenceInterval95);

        // Memory comparison
        double memoryRatio = stats1.peakMemoryStats.mean / stats2.peakMemoryStats.mean;
        boolean memorySignificant = stats1.peakMemoryStats.confidenceInterval95.isSignificantlyDifferentFrom(
                stats2.peakMemoryStats.confidenceInterval95);

        // Execution time comparison
        double timeRatio = stats1.executionTimeStats.mean / stats2.executionTimeStats.mean;
        boolean timeSignificant = stats1.executionTimeStats.confidenceInterval95.isSignificantlyDifferentFrom(
                stats2.executionTimeStats.confidenceInterval95);

        // Overall performance scores
        double score1 = stats1.getPerformanceScore();
        double score2 = stats2.getPerformanceScore();

        // Determine winner
        String winner = determineWinner(stats1, stats2);
        String recommendation = generateRecommendation(stats1, stats2, throughputRatio, memoryRatio);

        return new ComparisonResult(
                stats1.library,
                stats2.library,
                operation,
                stats1.datasetSize != null ? stats1.datasetSize : "N/A",
                stats1.fileFormat != null ? stats1.fileFormat : "N/A",
                throughputRatio,
                throughputSignificant,
                memoryRatio,
                memorySignificant,
                timeRatio,
                timeSignificant,
                score1,
                score2,
                winner,
                recommendation);
    }

    /**
     * Determine the overall winner between two statistics
     */
    private String determineWinner(BenchmarkStatistics stats1, BenchmarkStatistics stats2) {
        double score1 = stats1.getPerformanceScore();
        double score2 = stats2.getPerformanceScore();

        if (Math.abs(score1 - score2) < 5.0) { // Less than 5% difference
            return "Tie";
        }

        return score1 > score2 ? stats1.library : stats2.library;
    }

    /**
     * Generate recommendation based on comparison
     */
    private String generateRecommendation(
            BenchmarkStatistics stats1, BenchmarkStatistics stats2, double throughputRatio, double memoryRatio) {
        StringBuilder recommendation = new StringBuilder();

        if (throughputRatio > 1.2) {
            recommendation.append(
                    String.format("%s is significantly faster (%.1fx throughput). ", stats1.library, throughputRatio));
        } else if (throughputRatio < 0.8) {
            recommendation.append(String.format(
                    "%s is significantly faster (%.1fx throughput). ", stats2.library, 1.0 / throughputRatio));
        }

        if (memoryRatio < 0.8) {
            recommendation.append(String.format(
                    "%s uses significantly less memory (%.1fx reduction). ", stats1.library, 1.0 / memoryRatio));
        } else if (memoryRatio > 1.2) {
            recommendation.append(String.format(
                    "%s uses significantly less memory (%.1fx reduction). ", stats2.library, memoryRatio));
        }

        if (stats1.isStable() && !stats2.isStable()) {
            recommendation.append(String.format("%s has more stable performance. ", stats1.library));
        } else if (!stats1.isStable() && stats2.isStable()) {
            recommendation.append(String.format("%s has more stable performance. ", stats2.library));
        }

        if (recommendation.length() == 0) {
            recommendation.append("Both libraries show similar performance characteristics.");
        }

        return recommendation.toString().trim();
    }

    /**
     * Get all comparison results
     */
    public List<ComparisonResult> getComparisons() {
        return new ArrayList<>(comparisons);
    }

    /**
     * Get summary of all comparisons
     */
    public String getSummary() {
        StringBuilder summary = new StringBuilder();
        summary.append("=== Performance Comparison Analysis ===\n\n");

        for (ComparisonResult comparison : comparisons) {
            summary.append(comparison.toString()).append("\n\n");
        }

        return summary.toString();
    }

    @Override
    public String toString() {
        return getSummary();
    }

    /**
     * Result of comparing two benchmark statistics
     */
    public static class ComparisonResult {
        public final String library1;
        public final String library2;
        public final String operation;
        public final String datasetSize;
        public final String fileFormat;
        public final double throughputRatio; // library1 / library2
        public final boolean throughputSignificant;
        public final double memoryRatio; // library1 / library2
        public final boolean memorySignificant;
        public final double timeRatio; // library1 / library2
        public final boolean timeSignificant;
        public final double score1;
        public final double score2;
        public final String winner;
        public final String recommendation;

        public ComparisonResult(
                String library1,
                String library2,
                String operation,
                String datasetSize,
                String fileFormat,
                double throughputRatio,
                boolean throughputSignificant,
                double memoryRatio,
                boolean memorySignificant,
                double timeRatio,
                boolean timeSignificant,
                double score1,
                double score2,
                String winner,
                String recommendation) {
            this.library1 = library1;
            this.library2 = library2;
            this.operation = operation;
            this.datasetSize = datasetSize;
            this.fileFormat = fileFormat;
            this.throughputRatio = throughputRatio;
            this.throughputSignificant = throughputSignificant;
            this.memoryRatio = memoryRatio;
            this.memorySignificant = memorySignificant;
            this.timeRatio = timeRatio;
            this.timeSignificant = timeSignificant;
            this.score1 = score1;
            this.score2 = score2;
            this.winner = winner;
            this.recommendation = recommendation;
        }

        @Override
        public String toString() {
            return String.format(
                    "%s vs %s (%s - %s, %s):\n" + "  Throughput: %.2fx %s\n"
                            + "  Memory: %.2fx %s\n"
                            + "  Execution Time: %.2fx %s\n"
                            + "  Performance Scores: %.1f vs %.1f\n"
                            + "  Winner: %s\n"
                            + "  Recommendation: %s",
                    library1,
                    library2,
                    operation,
                    datasetSize,
                    fileFormat,
                    throughputRatio,
                    throughputSignificant ? "(significant)" : "(not significant)",
                    memoryRatio,
                    memorySignificant ? "(significant)" : "(not significant)",
                    timeRatio,
                    timeSignificant ? "(significant)" : "(not significant)",
                    score1,
                    score2,
                    winner,
                    recommendation);
        }
    }
}
