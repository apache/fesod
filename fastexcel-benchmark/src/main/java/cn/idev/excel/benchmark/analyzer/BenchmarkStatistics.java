package cn.idev.excel.benchmark.analyzer;

import org.openjdk.jmh.annotations.Mode;

/**
 * Comprehensive statistics for a benchmark configuration
 */
public class BenchmarkStatistics {

    // Metadata
    public final String library;
    public final String operation;
    public final String datasetSize;
    public final String fileFormat;
    public final Mode benchmarkMode;
    public final int sampleSize;

    // Performance statistics
    public final StatisticalSummary executionTimeStats;
    public final StatisticalSummary throughputStats;

    // Memory statistics
    public final StatisticalSummary peakMemoryStats;
    public final StatisticalSummary avgMemoryStats;

    // GC statistics
    public final StatisticalSummary gcCountStats;
    public final StatisticalSummary gcTimeStats;

    // Aggregate metrics
    public final long totalProcessedRows;
    public final long avgFileSizeBytes;

    private BenchmarkStatistics(Builder builder) {
        this.library = builder.library;
        this.operation = builder.operation;
        this.datasetSize = builder.datasetSize;
        this.fileFormat = builder.fileFormat;
        this.benchmarkMode = builder.benchmarkMode;
        this.sampleSize = builder.sampleSize;
        this.executionTimeStats = builder.executionTimeStats;
        this.throughputStats = builder.throughputStats;
        this.peakMemoryStats = builder.peakMemoryStats;
        this.avgMemoryStats = builder.avgMemoryStats;
        this.gcCountStats = builder.gcCountStats;
        this.gcTimeStats = builder.gcTimeStats;
        this.totalProcessedRows = builder.totalProcessedRows;
        this.avgFileSizeBytes = builder.avgFileSizeBytes;
    }

    /**
     * Get overall performance score (higher is better)
     */
    public double getPerformanceScore() {
        // Normalize metrics to a 0-100 scale
        double throughputScore = Math.min(100, throughputStats.mean / 1000.0); // Assume 1000 rows/sec = 100
        double memoryScore = Math.max(0, 100 - (peakMemoryStats.mean / 10.0)); // 10MB = 90 score
        double stabilityScore = Math.max(0, 100 - (executionTimeStats.getCoefficientOfVariation()));

        // Weighted average: throughput (40%), memory (30%), stability (30%)
        return (throughputScore * 0.4) + (memoryScore * 0.3) + (stabilityScore * 0.3);
    }

    /**
     * Check if results are statistically stable
     */
    public boolean isStable() {
        double threshold = 10.0; // 10% coefficient of variation threshold
        return executionTimeStats.isStable(threshold)
                && throughputStats.isStable(threshold)
                && peakMemoryStats.isStable(threshold);
    }

    /**
     * Get efficiency ratio (rows processed per MB of memory)
     */
    public double getMemoryEfficiencyRatio() {
        if (peakMemoryStats.mean == 0) return 0;
        return totalProcessedRows / peakMemoryStats.mean;
    }

    /**
     * Get formatted summary
     */
    @Override
    public String toString() {
        return String.format(
                "%s-%s: %.2f rows/sec, %.2f MB peak memory, %.2f ms avg time (n=%d)",
                library, operation, throughputStats.mean, peakMemoryStats.mean, executionTimeStats.mean, sampleSize);
    }

    /**
     * Get detailed formatted report
     */
    public String toDetailedString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("=== %s - %s ===\n", library, operation));
        sb.append(String.format("Sample Size: %d\n", sampleSize));
        sb.append(String.format("Benchmark Mode: %s\n", benchmarkMode));
        sb.append(String.format("Total Processed Rows: %,d\n", totalProcessedRows));
        sb.append(String.format("Average File Size: %.2f MB\n", avgFileSizeBytes / (1024.0 * 1024.0)));
        sb.append(String.format("Performance Score: %.2f/100\n", getPerformanceScore()));
        sb.append(String.format("Results Stable: %s\n\n", isStable() ? "Yes" : "No"));

        sb.append("Execution Time:\n");
        sb.append("  ").append(executionTimeStats.toDetailedString()).append("\n\n");

        sb.append("Throughput:\n");
        sb.append("  ").append(throughputStats.toDetailedString()).append("\n\n");

        sb.append("Peak Memory Usage:\n");
        sb.append("  ").append(peakMemoryStats.toDetailedString()).append("\n\n");

        sb.append("Average Memory Usage:\n");
        sb.append("  ").append(avgMemoryStats.toDetailedString()).append("\n\n");

        sb.append("GC Count:\n");
        sb.append("  ").append(gcCountStats.toDetailedString()).append("\n\n");

        sb.append("GC Time:\n");
        sb.append("  ").append(gcTimeStats.toDetailedString()).append("\n");

        return sb.toString();
    }

    /**
     * Builder class for BenchmarkStatistics
     */
    public static class Builder {
        private String library;
        private String operation;
        private String datasetSize;
        private String fileFormat;
        private Mode benchmarkMode;
        private int sampleSize;
        private StatisticalSummary executionTimeStats;
        private StatisticalSummary throughputStats;
        private StatisticalSummary peakMemoryStats;
        private StatisticalSummary avgMemoryStats;
        private StatisticalSummary gcCountStats;
        private StatisticalSummary gcTimeStats;
        private long totalProcessedRows;
        private long avgFileSizeBytes;

        public Builder withLibrary(String library) {
            this.library = library;
            return this;
        }

        public Builder withOperation(String operation) {
            this.operation = operation;
            return this;
        }

        public Builder withDatasetSize(String datasetSize) {
            this.datasetSize = datasetSize;
            return this;
        }

        public Builder withFileFormat(String fileFormat) {
            this.fileFormat = fileFormat;
            return this;
        }

        public Builder withBenchmarkMode(Mode benchmarkMode) {
            this.benchmarkMode = benchmarkMode;
            return this;
        }

        public Builder withSampleSize(int sampleSize) {
            this.sampleSize = sampleSize;
            return this;
        }

        public Builder withExecutionTimeStats(StatisticalSummary executionTimeStats) {
            this.executionTimeStats = executionTimeStats;
            return this;
        }

        public Builder withThroughputStats(StatisticalSummary throughputStats) {
            this.throughputStats = throughputStats;
            return this;
        }

        public Builder withPeakMemoryStats(StatisticalSummary peakMemoryStats) {
            this.peakMemoryStats = peakMemoryStats;
            return this;
        }

        public Builder withAvgMemoryStats(StatisticalSummary avgMemoryStats) {
            this.avgMemoryStats = avgMemoryStats;
            return this;
        }

        public Builder withGcCountStats(StatisticalSummary gcCountStats) {
            this.gcCountStats = gcCountStats;
            return this;
        }

        public Builder withGcTimeStats(StatisticalSummary gcTimeStats) {
            this.gcTimeStats = gcTimeStats;
            return this;
        }

        public Builder withTotalProcessedRows(long totalProcessedRows) {
            this.totalProcessedRows = totalProcessedRows;
            return this;
        }

        public Builder withAvgFileSizeBytes(long avgFileSizeBytes) {
            this.avgFileSizeBytes = avgFileSizeBytes;
            return this;
        }

        public BenchmarkStatistics build() {
            // Set defaults for null values
            if (executionTimeStats == null) executionTimeStats = StatisticalSummary.empty();
            if (throughputStats == null) throughputStats = StatisticalSummary.empty();
            if (peakMemoryStats == null) peakMemoryStats = StatisticalSummary.empty();
            if (avgMemoryStats == null) avgMemoryStats = StatisticalSummary.empty();
            if (gcCountStats == null) gcCountStats = StatisticalSummary.empty();
            if (gcTimeStats == null) gcTimeStats = StatisticalSummary.empty();

            return new BenchmarkStatistics(this);
        }
    }
}
