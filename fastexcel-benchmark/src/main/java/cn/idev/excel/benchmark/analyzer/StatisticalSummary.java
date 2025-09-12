package cn.idev.excel.benchmark.analyzer;

/**
 * Statistical summary containing various statistical measures
 */
public class StatisticalSummary {
    public final double mean;
    public final double stdDev;
    public final double min;
    public final double max;
    public final double p50; // median
    public final double p95;
    public final double p99;
    public final double bestScore; // depends on metric type (min for time, max for throughput)
    public final double worstScore; // depends on metric type (max for time, min for throughput)
    public final ConfidenceInterval confidenceInterval95;
    public final String unit;

    public StatisticalSummary(
            double mean,
            double stdDev,
            double min,
            double max,
            double p50,
            double p95,
            double p99,
            double bestScore,
            double worstScore,
            ConfidenceInterval confidenceInterval95,
            String unit) {
        this.mean = mean;
        this.stdDev = stdDev;
        this.min = min;
        this.max = max;
        this.p50 = p50;
        this.p95 = p95;
        this.p99 = p99;
        this.bestScore = bestScore;
        this.worstScore = worstScore;
        this.confidenceInterval95 = confidenceInterval95;
        this.unit = unit;
    }

    /**
     * Create an empty statistical summary
     */
    public static StatisticalSummary empty() {
        return new StatisticalSummary(0, 0, 0, 0, 0, 0, 0, 0, 0, new ConfidenceInterval(0, 0, 0.95), "");
    }

    /**
     * Get coefficient of variation (relative standard deviation)
     */
    public double getCoefficientOfVariation() {
        return mean != 0 ? (stdDev / Math.abs(mean)) * 100 : 0;
    }

    /**
     * Check if the results are stable (low coefficient of variation)
     */
    public boolean isStable(double threshold) {
        return getCoefficientOfVariation() <= threshold;
    }

    /**
     * Get the range of values
     */
    public double getRange() {
        return max - min;
    }

    /**
     * Get margin of error for 95% confidence interval
     */
    public double getMarginOfError() {
        return (confidenceInterval95.upper - confidenceInterval95.lower) / 2.0;
    }

    @Override
    public String toString() {
        return String.format(
                "Mean: %.3f %s (±%.3f), Range: [%.3f, %.3f], P95: %.3f, CV: %.1f%%",
                mean, unit, stdDev, min, max, p95, getCoefficientOfVariation());
    }

    /**
     * Get detailed string representation
     */
    public String toDetailedString() {
        return String.format(
                "Statistics%s:\n" + "  Mean: %.3f ± %.3f %s\n"
                        + "  Range: [%.3f, %.3f] %s\n"
                        + "  Percentiles: P50=%.3f, P95=%.3f, P99=%.3f %s\n"
                        + "  Best/Worst: %.3f / %.3f %s\n"
                        + "  95%% CI: [%.3f, %.3f] %s\n"
                        + "  Coefficient of Variation: %.1f%%",
                unit.isEmpty() ? "" : " (" + unit + ")",
                mean,
                stdDev,
                unit,
                min,
                max,
                unit,
                p50,
                p95,
                p99,
                unit,
                bestScore,
                worstScore,
                unit,
                confidenceInterval95.lower,
                confidenceInterval95.upper,
                unit,
                getCoefficientOfVariation());
    }
}
