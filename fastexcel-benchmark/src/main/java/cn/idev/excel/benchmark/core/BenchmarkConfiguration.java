package cn.idev.excel.benchmark.core;

/**
 * Configuration class for benchmark parameters
 */
public class BenchmarkConfiguration {

    /**
     * Dataset sizes for benchmarks
     */
    public enum DatasetSize {
        SMALL(1000, "1K"),
        MEDIUM(10000, "10K"),
        LARGE(100000, "100K"),
        EXTRA_LARGE(1000000, "1M");

        private final int rowCount;
        private final String label;

        DatasetSize(int rowCount, String label) {
            this.rowCount = rowCount;
            this.label = label;
        }

        public int getRowCount() {
            return rowCount;
        }

        public String getLabel() {
            return label;
        }
    }

    /**
     * File formats supported for benchmarking
     */
    public enum FileFormat {
        XLSX("xlsx"),
        XLS("xls"),
        CSV("csv");

        private final String extension;

        FileFormat(String extension) {
            this.extension = extension;
        }

        public String getExtension() {
            return extension;
        }
    }

    /**
     * Benchmark operation types
     */
    public enum OperationType {
        READ,
        WRITE,
        FILL,
        CONVERT
    }

    // Default benchmark configuration
    public static final int DEFAULT_WARMUP_ITERATIONS = 3;
    public static final int DEFAULT_MEASUREMENT_ITERATIONS = 5;
    public static final int DEFAULT_FORK_COUNT = 1;
    public static final String DEFAULT_OUTPUT_DIR = "target/benchmark-results";
    public static final String DEFAULT_BASELINE_DIR = "src/test/resources/baselines";

    // Memory monitoring configuration
    public static final boolean ENABLE_MEMORY_PROFILING = true;
    public static final long MEMORY_SAMPLING_INTERVAL_MS = 100;

    // Performance thresholds for regression detection
    public static final double PERFORMANCE_REGRESSION_THRESHOLD = 0.15; // 15% degradation
    public static final double MEMORY_REGRESSION_THRESHOLD = 0.20; // 20% increase
}
