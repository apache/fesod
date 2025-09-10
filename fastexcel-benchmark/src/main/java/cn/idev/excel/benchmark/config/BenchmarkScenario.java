package cn.idev.excel.benchmark.config;

import cn.idev.excel.benchmark.core.BenchmarkConfiguration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Configuration class for defining benchmark scenarios with specific parameters.
 * Allows for parameterized testing across different configurations.
 */
public class BenchmarkScenario {

    private String name;
    private String description;
    private List<BenchmarkConfiguration.DatasetSize> datasetSizes;
    private List<BenchmarkConfiguration.FileFormat> fileFormats;
    private List<BenchmarkConfiguration.OperationType> operationTypes;
    private List<Integer> batchSizes;
    private List<Integer> threadCounts;
    private BenchmarkExecutionConfig executionConfig;
    private BenchmarkMemoryConfig memoryConfig;
    private List<String> includePatterns;
    private List<String> excludePatterns;
    private boolean enableMemoryProfiling;
    private boolean enableComparison;
    private String outputFormat;
    private String reportTemplate;

    public BenchmarkScenario() {
        this.datasetSizes = new ArrayList<>();
        this.fileFormats = new ArrayList<>();
        this.operationTypes = new ArrayList<>();
        this.batchSizes = new ArrayList<>();
        this.threadCounts = new ArrayList<>();
        this.includePatterns = new ArrayList<>();
        this.excludePatterns = new ArrayList<>();
        this.executionConfig = new BenchmarkExecutionConfig();
        this.memoryConfig = new BenchmarkMemoryConfig();
        this.enableMemoryProfiling = true;
        this.enableComparison = false;
        this.outputFormat = "HTML";
        this.reportTemplate = "default";
    }

    // Builder pattern for easy configuration
    public static class Builder {
        private final BenchmarkScenario scenario;

        public Builder(String name) {
            this.scenario = new BenchmarkScenario();
            this.scenario.name = name;
        }

        public Builder description(String description) {
            scenario.description = description;
            return this;
        }

        public Builder datasetSizes(BenchmarkConfiguration.DatasetSize... sizes) {
            scenario.datasetSizes.addAll(Arrays.asList(sizes));
            return this;
        }

        public Builder fileFormats(BenchmarkConfiguration.FileFormat... formats) {
            scenario.fileFormats.addAll(Arrays.asList(formats));
            return this;
        }

        public Builder operationTypes(BenchmarkConfiguration.OperationType... types) {
            scenario.operationTypes.addAll(Arrays.asList(types));
            return this;
        }

        public Builder batchSizes(Integer... sizes) {
            scenario.batchSizes.addAll(Arrays.asList(sizes));
            return this;
        }

        public Builder threadCounts(Integer... counts) {
            scenario.threadCounts.addAll(Arrays.asList(counts));
            return this;
        }

        public Builder executionConfig(BenchmarkExecutionConfig config) {
            scenario.executionConfig = config;
            return this;
        }

        public Builder memoryConfig(BenchmarkMemoryConfig config) {
            scenario.memoryConfig = config;
            return this;
        }

        public Builder includePatterns(String... patterns) {
            scenario.includePatterns.addAll(Arrays.asList(patterns));
            return this;
        }

        public Builder excludePatterns(String... patterns) {
            scenario.excludePatterns.addAll(Arrays.asList(patterns));
            return this;
        }

        public Builder enableMemoryProfiling(boolean enable) {
            scenario.enableMemoryProfiling = enable;
            return this;
        }

        public Builder enableComparison(boolean enable) {
            scenario.enableComparison = enable;
            return this;
        }

        public Builder outputFormat(String format) {
            scenario.outputFormat = format;
            return this;
        }

        public Builder reportTemplate(String template) {
            scenario.reportTemplate = template;
            return this;
        }

        public BenchmarkScenario build() {
            // Validate scenario
            if (scenario.name == null || scenario.name.trim().isEmpty()) {
                throw new IllegalArgumentException("Scenario name is required");
            }

            // Set defaults if not specified
            if (scenario.datasetSizes.isEmpty()) {
                scenario.datasetSizes.addAll(Arrays.asList(
                        BenchmarkConfiguration.DatasetSize.SMALL,
                        BenchmarkConfiguration.DatasetSize.MEDIUM,
                        BenchmarkConfiguration.DatasetSize.LARGE));
            }

            if (scenario.fileFormats.isEmpty()) {
                scenario.fileFormats.addAll(
                        Arrays.asList(BenchmarkConfiguration.FileFormat.XLSX, BenchmarkConfiguration.FileFormat.XLS));
            }

            if (scenario.operationTypes.isEmpty()) {
                scenario.operationTypes.addAll(Arrays.asList(
                        BenchmarkConfiguration.OperationType.READ, BenchmarkConfiguration.OperationType.WRITE));
            }

            if (scenario.batchSizes.isEmpty()) {
                scenario.batchSizes.addAll(Arrays.asList(500, 1000, 2000));
            }

            if (scenario.threadCounts.isEmpty()) {
                scenario.threadCounts.add(1); // Single-threaded by default
            }

            return scenario;
        }
    }

    /**
     * Execution configuration for benchmarks
     */
    public static class BenchmarkExecutionConfig {
        private int warmupIterations = 2;
        private int warmupTime = 3; // seconds
        private int measurementIterations = 3;
        private int measurementTime = 5; // seconds
        private int forks = 1;
        private String timeUnit = "MILLISECONDS";
        private String mode = "All"; // All, Throughput, AverageTime, etc.
        private int timeout = 300; // seconds

        // Constructors
        public BenchmarkExecutionConfig() {}

        public BenchmarkExecutionConfig(
                int warmupIterations, int warmupTime, int measurementIterations, int measurementTime, int forks) {
            this.warmupIterations = warmupIterations;
            this.warmupTime = warmupTime;
            this.measurementIterations = measurementIterations;
            this.measurementTime = measurementTime;
            this.forks = forks;
        }

        // Getters and setters
        public int getWarmupIterations() {
            return warmupIterations;
        }

        public void setWarmupIterations(int warmupIterations) {
            this.warmupIterations = warmupIterations;
        }

        public int getWarmupTime() {
            return warmupTime;
        }

        public void setWarmupTime(int warmupTime) {
            this.warmupTime = warmupTime;
        }

        public int getMeasurementIterations() {
            return measurementIterations;
        }

        public void setMeasurementIterations(int measurementIterations) {
            this.measurementIterations = measurementIterations;
        }

        public int getMeasurementTime() {
            return measurementTime;
        }

        public void setMeasurementTime(int measurementTime) {
            this.measurementTime = measurementTime;
        }

        public int getForks() {
            return forks;
        }

        public void setForks(int forks) {
            this.forks = forks;
        }

        public String getTimeUnit() {
            return timeUnit;
        }

        public void setTimeUnit(String timeUnit) {
            this.timeUnit = timeUnit;
        }

        public String getMode() {
            return mode;
        }

        public void setMode(String mode) {
            this.mode = mode;
        }

        public int getTimeout() {
            return timeout;
        }

        public void setTimeout(int timeout) {
            this.timeout = timeout;
        }
    }

    /**
     * Memory configuration for benchmarks
     */
    public static class BenchmarkMemoryConfig {
        private String heapSize = "2g";
        private String newGenSize = "512m";
        private String gcAlgorithm = "G1GC";
        private boolean enableGcLogging = true;
        private int memoryProfilingInterval = 50; // milliseconds
        private boolean forceGcBetweenTests = true;
        private long memoryThreshold = 1024 * 1024 * 1024; // 1GB

        // Constructors
        public BenchmarkMemoryConfig() {}

        public BenchmarkMemoryConfig(String heapSize, String gcAlgorithm, boolean enableGcLogging) {
            this.heapSize = heapSize;
            this.gcAlgorithm = gcAlgorithm;
            this.enableGcLogging = enableGcLogging;
        }

        // Getters and setters
        public String getHeapSize() {
            return heapSize;
        }

        public void setHeapSize(String heapSize) {
            this.heapSize = heapSize;
        }

        public String getNewGenSize() {
            return newGenSize;
        }

        public void setNewGenSize(String newGenSize) {
            this.newGenSize = newGenSize;
        }

        public String getGcAlgorithm() {
            return gcAlgorithm;
        }

        public void setGcAlgorithm(String gcAlgorithm) {
            this.gcAlgorithm = gcAlgorithm;
        }

        public boolean isEnableGcLogging() {
            return enableGcLogging;
        }

        public void setEnableGcLogging(boolean enableGcLogging) {
            this.enableGcLogging = enableGcLogging;
        }

        public int getMemoryProfilingInterval() {
            return memoryProfilingInterval;
        }

        public void setMemoryProfilingInterval(int memoryProfilingInterval) {
            this.memoryProfilingInterval = memoryProfilingInterval;
        }

        public boolean isForceGcBetweenTests() {
            return forceGcBetweenTests;
        }

        public void setForceGcBetweenTests(boolean forceGcBetweenTests) {
            this.forceGcBetweenTests = forceGcBetweenTests;
        }

        public long getMemoryThreshold() {
            return memoryThreshold;
        }

        public void setMemoryThreshold(long memoryThreshold) {
            this.memoryThreshold = memoryThreshold;
        }
    }

    // Getters and setters for BenchmarkScenario
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<BenchmarkConfiguration.DatasetSize> getDatasetSizes() {
        return datasetSizes;
    }

    public void setDatasetSizes(List<BenchmarkConfiguration.DatasetSize> datasetSizes) {
        this.datasetSizes = datasetSizes;
    }

    public List<BenchmarkConfiguration.FileFormat> getFileFormats() {
        return fileFormats;
    }

    public void setFileFormats(List<BenchmarkConfiguration.FileFormat> fileFormats) {
        this.fileFormats = fileFormats;
    }

    public List<BenchmarkConfiguration.OperationType> getOperationTypes() {
        return operationTypes;
    }

    public void setOperationTypes(List<BenchmarkConfiguration.OperationType> operationTypes) {
        this.operationTypes = operationTypes;
    }

    public List<Integer> getBatchSizes() {
        return batchSizes;
    }

    public void setBatchSizes(List<Integer> batchSizes) {
        this.batchSizes = batchSizes;
    }

    public List<Integer> getThreadCounts() {
        return threadCounts;
    }

    public void setThreadCounts(List<Integer> threadCounts) {
        this.threadCounts = threadCounts;
    }

    public BenchmarkExecutionConfig getExecutionConfig() {
        return executionConfig;
    }

    public void setExecutionConfig(BenchmarkExecutionConfig executionConfig) {
        this.executionConfig = executionConfig;
    }

    public BenchmarkMemoryConfig getMemoryConfig() {
        return memoryConfig;
    }

    public void setMemoryConfig(BenchmarkMemoryConfig memoryConfig) {
        this.memoryConfig = memoryConfig;
    }

    public List<String> getIncludePatterns() {
        return includePatterns;
    }

    public void setIncludePatterns(List<String> includePatterns) {
        this.includePatterns = includePatterns;
    }

    public List<String> getExcludePatterns() {
        return excludePatterns;
    }

    public void setExcludePatterns(List<String> excludePatterns) {
        this.excludePatterns = excludePatterns;
    }

    public boolean isEnableMemoryProfiling() {
        return enableMemoryProfiling;
    }

    public void setEnableMemoryProfiling(boolean enableMemoryProfiling) {
        this.enableMemoryProfiling = enableMemoryProfiling;
    }

    public boolean isEnableComparison() {
        return enableComparison;
    }

    public void setEnableComparison(boolean enableComparison) {
        this.enableComparison = enableComparison;
    }

    public String getOutputFormat() {
        return outputFormat;
    }

    public void setOutputFormat(String outputFormat) {
        this.outputFormat = outputFormat;
    }

    public String getReportTemplate() {
        return reportTemplate;
    }

    public void setReportTemplate(String reportTemplate) {
        this.reportTemplate = reportTemplate;
    }

    /**
     * Generate JMH parameters for this scenario
     */
    public String[] generateJmhParameters() {
        List<String> parameters = new ArrayList<>();

        // Dataset sizes
        if (!datasetSizes.isEmpty()) {
            parameters.add("datasetSize");
            parameters.addAll(datasetSizes.stream().map(Enum::name).collect(Collectors.toList()));
        }

        // File formats
        if (!fileFormats.isEmpty()) {
            parameters.add("fileFormat");
            parameters.addAll(fileFormats.stream().map(Enum::name).collect(Collectors.toList()));
        }

        // Batch sizes
        if (!batchSizes.isEmpty()) {
            parameters.add("batchSize");
            parameters.addAll(batchSizes.stream().map(String::valueOf).collect(Collectors.toList()));
        }

        // Thread counts
        if (!threadCounts.isEmpty()) {
            parameters.add("threadCount");
            parameters.addAll(threadCounts.stream().map(String::valueOf).collect(Collectors.toList()));
        }

        return parameters.toArray(new String[0]);
    }

    /**
     * Get total number of benchmark combinations
     */
    public int getTotalCombinations() {
        int combinations = 1;

        if (!datasetSizes.isEmpty()) combinations *= datasetSizes.size();
        if (!fileFormats.isEmpty()) combinations *= fileFormats.size();
        if (!batchSizes.isEmpty()) combinations *= batchSizes.size();
        if (!threadCounts.isEmpty()) combinations *= threadCounts.size();

        return combinations;
    }

    /**
     * Estimate execution time in minutes
     */
    public double estimateExecutionTimeMinutes() {
        int totalCombinations = getTotalCombinations();
        int totalTime = (executionConfig.warmupIterations * executionConfig.warmupTime)
                + (executionConfig.measurementIterations * executionConfig.measurementTime);

        return (totalCombinations * totalTime * executionConfig.forks) / 60.0;
    }

    @Override
    public String toString() {
        return String.format(
                "BenchmarkScenario{name='%s', combinations=%d, estimatedTime=%.1f min}",
                name, getTotalCombinations(), estimateExecutionTimeMinutes());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BenchmarkScenario that = (BenchmarkScenario) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
