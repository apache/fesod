package cn.idev.excel.benchmark.analyzer;

/**
 * Represents a confidence interval for statistical analysis
 */
public class ConfidenceInterval {
    public final double lower;
    public final double upper;
    public final double confidence;

    public ConfidenceInterval(double lower, double upper, double confidence) {
        this.lower = lower;
        this.upper = upper;
        this.confidence = confidence;
    }

    /**
     * Get the width of the confidence interval
     */
    public double getWidth() {
        return upper - lower;
    }

    /**
     * Get the center point of the confidence interval
     */
    public double getCenter() {
        return (lower + upper) / 2.0;
    }

    /**
     * Check if a value falls within this confidence interval
     */
    public boolean contains(double value) {
        return value >= lower && value <= upper;
    }

    /**
     * Get margin of error (half the width)
     */
    public double getMarginOfError() {
        return getWidth() / 2.0;
    }

    @Override
    public String toString() {
        return String.format("[%.3f, %.3f] (%.0f%% CI)", lower, upper, confidence * 100);
    }

    /**
     * Check if this confidence interval overlaps with another
     */
    public boolean overlaps(ConfidenceInterval other) {
        return this.lower <= other.upper && this.upper >= other.lower;
    }

    /**
     * Check if this confidence interval is significantly different from another
     * (no overlap indicates statistical significance)
     */
    public boolean isSignificantlyDifferentFrom(ConfidenceInterval other) {
        return !overlaps(other);
    }
}
