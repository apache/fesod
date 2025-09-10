package cn.idev.excel.benchmark.config;

import cn.idev.excel.benchmark.core.BenchmarkConfiguration;
import cn.idev.excel.benchmark.utils.BenchmarkFileUtil;
import com.alibaba.fastjson2.JSON;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Manager for benchmark scenarios - handles loading, saving, and executing scenarios.
 * Supports both programmatic and file-based scenario configuration.
 */
public class BenchmarkScenarioManager {

    private static final Logger logger = LoggerFactory.getLogger(BenchmarkScenarioManager.class);
    private static final String SCENARIOS_DIR = "benchmark-scenarios";
    private static final String DEFAULT_SCENARIOS_FILE = "default-scenarios.json";

    private final Map<String, BenchmarkScenario> scenarios;
    private final Map<String, Class<?>> benchmarkClasses;

    public BenchmarkScenarioManager() {
        this.scenarios = new HashMap<>();
        this.benchmarkClasses = new HashMap<>();

        // Register benchmark classes
        registerBenchmarkClasses();

        // Load default scenarios
        loadDefaultScenarios();
    }

    /**
     * Register available benchmark classes
     */
    private void registerBenchmarkClasses() {
        try {
            benchmarkClasses.put("read", Class.forName("cn.idev.excel.benchmark.operations.ReadBenchmark"));
            benchmarkClasses.put("write", Class.forName("cn.idev.excel.benchmark.operations.WriteBenchmark"));
            benchmarkClasses.put("fill", Class.forName("cn.idev.excel.benchmark.operations.FillBenchmark"));
            benchmarkClasses.put("streaming", Class.forName("cn.idev.excel.benchmark.streaming.StreamingBenchmark"));
            benchmarkClasses.put(
                    "memoryefficiency", Class.forName("cn.idev.excel.benchmark.streaming.MemoryEfficiencyBenchmark"));
            benchmarkClasses.put(
                    "comparison", Class.forName("cn.idev.excel.benchmark.comparison.FastExcelVsPoiBenchmark"));
        } catch (ClassNotFoundException e) {
            System.err.println("Warning: Some benchmark classes not found: " + e.getMessage());
        }
    }

    /**
     * Load default benchmark scenarios
     */
    private void loadDefaultScenarios() {
        // Performance testing scenario
        scenarios.put("performance", createPerformanceScenario());

        // Memory testing scenario
        scenarios.put("memory", createMemoryScenario());

        // Comparison scenario
        scenarios.put("comparison", createComparisonScenario());

        // Quick test scenario
        scenarios.put("quick", createQuickTestScenario());

        // Comprehensive scenario
        scenarios.put("comprehensive", createComprehensiveScenario());

        // Load from file if exists
        loadScenariosFromFile();
    }

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

    /**
     * Create performance testing scenario
     */
    private BenchmarkScenario createPerformanceScenario() {
        return new BenchmarkScenario.Builder("performance")
                .description("Performance testing across different dataset sizes and formats")
                .datasetSizes(
                        BenchmarkConfiguration.DatasetSize.SMALL,
                        BenchmarkConfiguration.DatasetSize.MEDIUM,
                        BenchmarkConfiguration.DatasetSize.LARGE)
                .fileFormats(BenchmarkConfiguration.FileFormat.XLSX, BenchmarkConfiguration.FileFormat.XLS)
                .operationTypes(BenchmarkConfiguration.OperationType.READ, BenchmarkConfiguration.OperationType.WRITE)
                .batchSizes(1000, 2000)
                .includePatterns("*ReadBenchmark*", "*WriteBenchmark*")
                .executionConfig(new BenchmarkScenario.BenchmarkExecutionConfig(3, 3, 5, 5, 1))
                .enableMemoryProfiling(true)
                .outputFormat("HTML")
                .build();
    }

    /**
     * Create memory testing scenario
     */
    private BenchmarkScenario createMemoryScenario() {
        return new BenchmarkScenario.Builder("memory")
                .description("Memory efficiency testing with streaming operations")
                .datasetSizes(BenchmarkConfiguration.DatasetSize.LARGE, BenchmarkConfiguration.DatasetSize.EXTRA_LARGE)
                .fileFormats(BenchmarkConfiguration.FileFormat.XLSX)
                .batchSizes(500, 1000, 2000, 5000)
                .includePatterns("*StreamingBenchmark*", "*MemoryEfficiencyBenchmark*")
                .executionConfig(new BenchmarkScenario.BenchmarkExecutionConfig(2, 5, 3, 10, 1))
                .memoryConfig(new BenchmarkScenario.BenchmarkMemoryConfig("4g", "G1GC", true))
                .enableMemoryProfiling(true)
                .outputFormat("HTML")
                .build();
    }

    /**
     * Create comparison scenario
     */
    private BenchmarkScenario createComparisonScenario() {
        return new BenchmarkScenario.Builder("comparison")
                .description("FastExcel vs Apache POI performance comparison")
                .datasetSizes(
                        BenchmarkConfiguration.DatasetSize.SMALL,
                        BenchmarkConfiguration.DatasetSize.MEDIUM,
                        BenchmarkConfiguration.DatasetSize.LARGE)
                .fileFormats(BenchmarkConfiguration.FileFormat.XLSX, BenchmarkConfiguration.FileFormat.XLS)
                .includePatterns("*FastExcelVsPoiBenchmark*")
                .executionConfig(new BenchmarkScenario.BenchmarkExecutionConfig(2, 3, 3, 5, 1))
                .enableComparison(true)
                .enableMemoryProfiling(true)
                .outputFormat("HTML")
                .build();
    }

    /**
     * Create quick test scenario
     */
    private BenchmarkScenario createQuickTestScenario() {
        return new BenchmarkScenario.Builder("quick")
                .description("Quick smoke test for all benchmark types")
                .datasetSizes(BenchmarkConfiguration.DatasetSize.SMALL)
                .fileFormats(BenchmarkConfiguration.FileFormat.XLSX)
                .batchSizes(1000)
                .executionConfig(new BenchmarkScenario.BenchmarkExecutionConfig(1, 1, 1, 1, 1))
                .enableMemoryProfiling(false)
                .outputFormat("JSON")
                .build();
    }

    /**
     * Create comprehensive scenario
     */
    private BenchmarkScenario createComprehensiveScenario() {
        return new BenchmarkScenario.Builder("comprehensive")
                .description("Comprehensive testing across all operations and configurations")
                .datasetSizes(
                        BenchmarkConfiguration.DatasetSize.SMALL,
                        BenchmarkConfiguration.DatasetSize.MEDIUM,
                        BenchmarkConfiguration.DatasetSize.LARGE,
                        BenchmarkConfiguration.DatasetSize.EXTRA_LARGE)
                .fileFormats(
                        BenchmarkConfiguration.FileFormat.XLSX,
                        BenchmarkConfiguration.FileFormat.XLS,
                        BenchmarkConfiguration.FileFormat.CSV)
                .operationTypes(
                        BenchmarkConfiguration.OperationType.READ,
                        BenchmarkConfiguration.OperationType.WRITE,
                        BenchmarkConfiguration.OperationType.FILL)
                .batchSizes(500, 1000, 2000)
                .threadCounts(1, 2, 4)
                .executionConfig(new BenchmarkScenario.BenchmarkExecutionConfig(3, 5, 5, 10, 2))
                .memoryConfig(new BenchmarkScenario.BenchmarkMemoryConfig("8g", "G1GC", true))
                .enableMemoryProfiling(true)
                .enableComparison(true)
                .outputFormat("JSON")
                .build();
    }

    /**
     * Add a custom scenario
     */
    public void addScenario(BenchmarkScenario scenario) {
        scenarios.put(scenario.getName(), scenario);
    }

    /**
     * Get scenario by name
     */
    public BenchmarkScenario getScenario(String name) {
        return scenarios.get(name);
    }

    /**
     * Get all available scenarios
     */
    public Map<String, BenchmarkScenario> getAllScenarios() {
        return new HashMap<>(scenarios);
    }

    /**
     * List available scenario names
     */
    public Set<String> getScenarioNames() {
        return scenarios.keySet();
    }

    /**
     * Execute a specific scenario
     */
    public void executeScenario(String scenarioName) throws RunnerException {
        BenchmarkScenario scenario = scenarios.get(scenarioName);
        if (scenario == null) {
            throw new IllegalArgumentException("Scenario not found: " + scenarioName);
        }

        executeScenario(scenario);
    }

    /**
     * Execute a benchmark scenario
     */
    public void executeScenario(BenchmarkScenario scenario) throws RunnerException {
        System.out.println("Executing scenario: " + scenario.getName());
        System.out.println("Description: " + scenario.getDescription());
        System.out.println("Estimated execution time: " + String.format("%.1f", scenario.estimateExecutionTimeMinutes())
                + " minutes");
        System.out.println("Total combinations: " + scenario.getTotalCombinations());
        System.out.println(repeat("-", 70));

        // Build JMH options
        OptionsBuilder optionsBuilder = new OptionsBuilder();

        // Include/exclude patterns
        for (String pattern : scenario.getIncludePatterns()) {
            optionsBuilder.include(pattern);
        }
        for (String pattern : scenario.getExcludePatterns()) {
            optionsBuilder.exclude(pattern);
        }

        // If no patterns specified, include all benchmarks
        if (scenario.getIncludePatterns().isEmpty()) {
            optionsBuilder.include(".*Benchmark.*");
        }

        // Execution configuration
        BenchmarkScenario.BenchmarkExecutionConfig execConfig = scenario.getExecutionConfig();
        optionsBuilder
                .warmupIterations(execConfig.getWarmupIterations())
                .warmupTime(TimeValue.seconds(execConfig.getWarmupTime()))
                .measurementIterations(execConfig.getMeasurementIterations())
                .measurementTime(TimeValue.seconds(execConfig.getMeasurementTime()))
                .forks(execConfig.getForks())
                .timeout(TimeValue.seconds(execConfig.getTimeout()));

        // Set benchmark mode
        switch (execConfig.getMode().toUpperCase()) {
            case "THROUGHPUT":
                optionsBuilder.mode(org.openjdk.jmh.annotations.Mode.Throughput);
                break;
            case "AVERAGETIME":
                optionsBuilder.mode(org.openjdk.jmh.annotations.Mode.AverageTime);
                break;
            case "SAMPLETIME":
                optionsBuilder.mode(org.openjdk.jmh.annotations.Mode.SampleTime);
                break;
            case "SINGLESHOT":
                optionsBuilder.mode(org.openjdk.jmh.annotations.Mode.SingleShotTime);
                break;
            default:
                optionsBuilder.mode(org.openjdk.jmh.annotations.Mode.All);
        }

        // Set time unit
        switch (execConfig.getTimeUnit().toUpperCase()) {
            case "NANOSECONDS":
                optionsBuilder.timeUnit(TimeUnit.NANOSECONDS);
                break;
            case "MICROSECONDS":
                optionsBuilder.timeUnit(TimeUnit.MICROSECONDS);
                break;
            case "SECONDS":
                optionsBuilder.timeUnit(TimeUnit.SECONDS);
                break;
            default:
                optionsBuilder.timeUnit(TimeUnit.MILLISECONDS);
        }

        // Add parameters
        addParametersToOptions(optionsBuilder, scenario);

        // JVM options for memory configuration
        BenchmarkScenario.BenchmarkMemoryConfig memConfig = scenario.getMemoryConfig();
        List<String> jvmArgs = new ArrayList<>();
        jvmArgs.add("-Xmx" + memConfig.getHeapSize());
        jvmArgs.add("-Xmn" + memConfig.getNewGenSize());

        switch (memConfig.getGcAlgorithm().toUpperCase()) {
            case "G1GC":
                jvmArgs.add("-XX:+UseG1GC");
                break;
            case "PARALLEL":
                jvmArgs.add("-XX:+UseParallelGC");
                break;
            case "CMS":
                jvmArgs.add("-XX:+UseConcMarkSweepGC");
                break;
            case "ZGC":
                jvmArgs.add("-XX:+UseZGC");
                break;
        }

        if (memConfig.isEnableGcLogging()) {
            jvmArgs.add("-XX:+PrintGC");
            jvmArgs.add("-XX:+PrintGCDetails");
            jvmArgs.add("-XX:+PrintGCTimeStamps");
        }

        if (memConfig.isForceGcBetweenTests()) {
            jvmArgs.add("-XX:+DisableExplicitGC");
        }

        optionsBuilder.jvmArgs(jvmArgs.toArray(new String[0]));

        // Output format
        String outputDir = "benchmark-results";
        String timestamp = java.time.LocalDateTime.now()
                .format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
        String outputFile = String.format("%s/%s_results_%s", outputDir, scenario.getName(), timestamp);

        if ("JSON".equalsIgnoreCase(scenario.getOutputFormat())) {
            optionsBuilder.resultFormat(org.openjdk.jmh.results.format.ResultFormatType.JSON);
            optionsBuilder.result(outputFile + ".json");
        } else {
            optionsBuilder.resultFormat(org.openjdk.jmh.results.format.ResultFormatType.TEXT);
            optionsBuilder.result(outputFile + ".txt");
        }

        // Create output directory
        try {
            Files.createDirectories(Paths.get(outputDir));
        } catch (IOException e) {
            System.err.println("Warning: Could not create output directory: " + e.getMessage());
        }

        // Build and run
        Options options = optionsBuilder.build();
        new Runner(options).run();

        System.out.println("\nScenario execution completed: " + scenario.getName());
        System.out.println("Results saved to: " + outputFile);
    }

    /**
     * Add parameters to JMH options based on scenario configuration
     */
    private void addParametersToOptions(OptionsBuilder optionsBuilder, BenchmarkScenario scenario) {
        // Dataset sizes
        if (!scenario.getDatasetSizes().isEmpty()) {
            String[] sizes = scenario.getDatasetSizes().stream().map(Enum::name).toArray(String[]::new);
            optionsBuilder.param("datasetSize", sizes);
        }

        // File formats
        if (!scenario.getFileFormats().isEmpty()) {
            String[] formats =
                    scenario.getFileFormats().stream().map(Enum::name).toArray(String[]::new);
            optionsBuilder.param("fileFormat", formats);
        }

        // Batch sizes
        if (!scenario.getBatchSizes().isEmpty()) {
            String[] sizes =
                    scenario.getBatchSizes().stream().map(String::valueOf).toArray(String[]::new);
            optionsBuilder.param("batchSize", sizes);
        }

        // Thread counts
        if (!scenario.getThreadCounts().isEmpty()) {
            String[] counts =
                    scenario.getThreadCounts().stream().map(String::valueOf).toArray(String[]::new);
            optionsBuilder.param("threadCount", counts);
        }
    }

    /**
     * Save scenarios to file
     */
    public void saveScenariosToFile() {
        try {
            Files.createDirectories(Paths.get(SCENARIOS_DIR));
            Path scenariosFile = Paths.get(SCENARIOS_DIR, DEFAULT_SCENARIOS_FILE);

            String json = JSON.toJSONString(scenarios, com.alibaba.fastjson2.JSONWriter.Feature.PrettyFormat);
            Files.write(scenariosFile, json.getBytes());

            System.out.println("Scenarios saved to: " + scenariosFile);
        } catch (IOException e) {
            System.err.println("Error saving scenarios: " + e.getMessage());
        }
    }

    /**
     * Load scenarios from file
     */
    public void loadScenariosFromFile() {
        try {
            Path scenariosFile = Paths.get(SCENARIOS_DIR, DEFAULT_SCENARIOS_FILE);
            if (Files.exists(scenariosFile)) {
                String json = BenchmarkFileUtil.readString(scenariosFile);
                Map<String, BenchmarkScenario> loadedScenarios = JSON.parseObject(
                        json, new com.alibaba.fastjson2.TypeReference<Map<String, BenchmarkScenario>>() {});

                if (loadedScenarios != null) {
                    scenarios.putAll(loadedScenarios);
                    System.out.println("Loaded " + loadedScenarios.size() + " scenarios from file");
                }
            }
        } catch (IOException e) {
            System.err.println("Warning: Could not load scenarios from file: " + e.getMessage());
        }
    }

    /**
     * Print available scenarios
     */
    public void printAvailableScenarios() {
        System.out.println("Available Benchmark Scenarios:");
        System.out.println(repeat("=", 50));

        scenarios.values().stream()
                .sorted(Comparator.comparing(BenchmarkScenario::getName))
                .forEach(scenario -> {
                    System.out.printf("%-15s | %s%n", scenario.getName(), scenario.getDescription());
                    System.out.printf(
                            "%-15s | Combinations: %d, Est. time: %.1f min%n",
                            "", scenario.getTotalCombinations(), scenario.estimateExecutionTimeMinutes());
                    System.out.println(repeat("-", 50));
                });
    }

    /**
     * Validate scenario configuration
     */
    public boolean validateScenario(BenchmarkScenario scenario) {
        List<String> issues = new ArrayList<>();

        if (scenario.getName() == null || scenario.getName().trim().isEmpty()) {
            issues.add("Scenario name is required");
        }

        if (scenario.getDatasetSizes().isEmpty()) {
            issues.add("At least one dataset size must be specified");
        }

        if (scenario.getFileFormats().isEmpty()) {
            issues.add("At least one file format must be specified");
        }

        if (scenario.getExecutionConfig().getWarmupIterations() < 0) {
            issues.add("Warmup iterations must be non-negative");
        }

        if (scenario.getExecutionConfig().getMeasurementIterations() <= 0) {
            issues.add("Measurement iterations must be positive");
        }

        if (!issues.isEmpty()) {
            System.err.println("Scenario validation failed:");
            issues.forEach(issue -> System.err.println("  - " + issue));
            return false;
        }

        return true;
    }
}
