package cn.idev.excel.benchmark;

import cn.idev.excel.benchmark.config.BenchmarkScenario;
import cn.idev.excel.benchmark.config.BenchmarkScenarioManager;
import cn.idev.excel.benchmark.core.BenchmarkConfiguration;
import java.util.Arrays;
import java.util.Scanner;
import org.openjdk.jmh.runner.RunnerException;

/**
 * Main command-line runner for FastExcel benchmarks.
 * Provides an easy interface for executing benchmark scenarios.
 */
public class BenchmarkRunner {

    private final BenchmarkScenarioManager scenarioManager;

    public BenchmarkRunner() {
        this.scenarioManager = new BenchmarkScenarioManager();
    }

    public static void main(String[] args) {
        BenchmarkRunner runner = new BenchmarkRunner();

        if (args.length == 0) {
            runner.runInteractiveMode();
        } else {
            runner.runCommandLineMode(args);
        }
    }

    /**
     * Run in interactive mode with menu
     */
    private void runInteractiveMode() {
        Scanner scanner = new Scanner(System.in);

        printWelcomeMessage();

        while (true) {
            displayMenu();
            String choice = scanner.nextLine().trim();

            try {
                switch (choice) {
                    case "1":
                        listScenarios();
                        break;
                    case "2":
                        runScenario(scanner);
                        break;
                    case "3":
                        createCustomScenario(scanner);
                        break;
                    case "4":
                        runQuickTest();
                        break;
                    case "5":
                        runMemoryBenchmark();
                        break;
                    case "6":
                        showBenchmarkInfo();
                        break;
                    case "7":
                        System.out.println("Goodbye!");
                        return;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
                e.printStackTrace();
            }

            System.out.println("\nPress Enter to continue...");
            scanner.nextLine();
        }
    }

    /**
     * Run in command-line mode with arguments
     */
    private void runCommandLineMode(String[] args) {
        try {
            if (args.length == 1) {
                String command = args[0].toLowerCase();

                switch (command) {
                    case "list":
                    case "--list":
                    case "-l":
                        scenarioManager.printAvailableScenarios();
                        break;
                    case "quick":
                    case "--quick":
                    case "-q":
                        runQuickTest();
                        break;
                    case "memory":
                    case "--memory":
                    case "-m":
                        runMemoryBenchmark();
                        break;
                    case "help":
                    case "--help":
                    case "-h":
                        printUsage();
                        break;
                    default:
                        // Try to run as scenario name
                        if (scenarioManager.getScenarioNames().contains(command)) {
                            scenarioManager.executeScenario(command);
                        } else {
                            System.err.println("Unknown command or scenario: " + command);
                            printUsage();
                        }
                }
            } else if (args.length == 2 && "run".equals(args[0].toLowerCase())) {
                String scenarioName = args[1];
                scenarioManager.executeScenario(scenarioName);
            } else {
                printUsage();
            }
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Print welcome message
     */
    private void printWelcomeMessage() {
        System.out.println(repeat("=", 70));
        System.out.println("FastExcel Benchmark Suite");
        System.out.println("Comprehensive performance testing for FastExcel library");
        System.out.println(repeat("=", 70));
        System.out.println();
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
     * Display main menu
     */
    private void displayMenu() {
        System.out.println(repeat("=", 50));
        System.out.println("1. List available scenarios");
        System.out.println("2. Run benchmark scenario");
        System.out.println("3. Create custom scenario");
        System.out.println("4. Run quick test");
        System.out.println("5. Run memory efficiency benchmark");
        System.out.println("6. Show benchmark information");
        System.out.println("7. Exit");
        System.out.println(repeat("-", 50));
        System.out.print("Enter your choice (1-7): ");
    }

    /**
     * List available scenarios
     */
    private void listScenarios() {
        scenarioManager.printAvailableScenarios();
    }

    /**
     * Run a specific scenario
     */
    private void runScenario(Scanner scanner) throws RunnerException {
        System.out.println("\nAvailable scenarios:");
        scenarioManager.getScenarioNames().forEach(name -> System.out.println("  - " + name));

        System.out.print("\nEnter scenario name: ");
        String scenarioName = scanner.nextLine().trim();

        BenchmarkScenario scenario = scenarioManager.getScenario(scenarioName);
        if (scenario == null) {
            System.out.println("Scenario not found: " + scenarioName);
            return;
        }

        System.out.println("\nScenario Details:");
        System.out.println("Name: " + scenario.getName());
        System.out.println("Description: " + scenario.getDescription());
        System.out.println("Combinations: " + scenario.getTotalCombinations());
        System.out.println(
                "Estimated time: " + String.format("%.1f", scenario.estimateExecutionTimeMinutes()) + " minutes");

        System.out.print("\nProceed with execution? (y/N): ");
        String confirm = scanner.nextLine().trim().toLowerCase();

        if ("y".equals(confirm) || "yes".equals(confirm)) {
            System.out.println("\nStarting benchmark execution...");
            scenarioManager.executeScenario(scenario);
        } else {
            System.out.println("Benchmark execution cancelled.");
        }
    }

    /**
     * Create a custom scenario interactively
     */
    private void createCustomScenario(Scanner scanner) {
        System.out.println("\n" + repeat("=", 50));
        System.out.println("CREATE CUSTOM SCENARIO");
        System.out.println(repeat("=", 50));

        System.out.print("Scenario name: ");
        String name = scanner.nextLine().trim();

        if (name.isEmpty()) {
            System.out.println("Scenario name cannot be empty.");
            return;
        }

        System.out.print("Description: ");
        String description = scanner.nextLine().trim();

        BenchmarkScenario.Builder builder = new BenchmarkScenario.Builder(name).description(description);

        // Dataset sizes
        System.out.println("\nSelect dataset sizes (comma-separated):");
        System.out.println("Available: " + Arrays.toString(BenchmarkConfiguration.DatasetSize.values()));
        System.out.print("Dataset sizes [SMALL,MEDIUM,LARGE]: ");
        String sizes = scanner.nextLine().trim();
        if (!sizes.isEmpty()) {
            String[] sizeArray = sizes.split(",");
            BenchmarkConfiguration.DatasetSize[] datasetSizes = Arrays.stream(sizeArray)
                    .map(String::trim)
                    .map(String::toUpperCase)
                    .map(BenchmarkConfiguration.DatasetSize::valueOf)
                    .toArray(BenchmarkConfiguration.DatasetSize[]::new);
            builder.datasetSizes(datasetSizes);
        }

        // File formats
        System.out.println("\nSelect file formats (comma-separated):");
        System.out.println("Available: " + Arrays.toString(BenchmarkConfiguration.FileFormat.values()));
        System.out.print("File formats [XLSX,XLS]: ");
        String formats = scanner.nextLine().trim();
        if (!formats.isEmpty()) {
            String[] formatArray = formats.split(",");
            BenchmarkConfiguration.FileFormat[] fileFormats = Arrays.stream(formatArray)
                    .map(String::trim)
                    .map(String::toUpperCase)
                    .map(BenchmarkConfiguration.FileFormat::valueOf)
                    .toArray(BenchmarkConfiguration.FileFormat[]::new);
            builder.fileFormats(fileFormats);
        }

        // Batch sizes
        System.out.print("\nBatch sizes (comma-separated) [1000,2000]: ");
        String batchSizes = scanner.nextLine().trim();
        if (!batchSizes.isEmpty()) {
            Integer[] sizes_array = Arrays.stream(batchSizes.split(","))
                    .map(String::trim)
                    .map(Integer::valueOf)
                    .toArray(Integer[]::new);
            builder.batchSizes(sizes_array);
        }

        // Include patterns
        System.out.print("\nInclude patterns (comma-separated, optional): ");
        String includePatterns = scanner.nextLine().trim();
        if (!includePatterns.isEmpty()) {
            String[] patterns = includePatterns.split(",");
            builder.includePatterns(Arrays.stream(patterns).map(String::trim).toArray(String[]::new));
        }

        // Memory profiling
        System.out.print("\nEnable memory profiling? (Y/n): ");
        String memoryProfiling = scanner.nextLine().trim().toLowerCase();
        builder.enableMemoryProfiling(!"n".equals(memoryProfiling) && !"no".equals(memoryProfiling));

        // Enhanced memory profiling options
        if (!"n".equals(memoryProfiling) && !"no".equals(memoryProfiling)) {
            System.out.print("Enable detailed memory statistics? (Y/n): ");
            String detailedMemory = scanner.nextLine().trim().toLowerCase();

            System.out.print("Memory profiling interval in ms [50]: ");
            String intervalInput = scanner.nextLine().trim();
            int interval = 50;
            if (!intervalInput.isEmpty()) {
                try {
                    interval = Integer.parseInt(intervalInput);
                } catch (NumberFormatException e) {
                    System.out.println("Invalid interval, using default 50ms");
                }
            }

            System.out.print("Memory threshold in MB [1024]: ");
            String thresholdInput = scanner.nextLine().trim();
            int threshold = 1024;
            if (!thresholdInput.isEmpty()) {
                try {
                    threshold = Integer.parseInt(thresholdInput);
                } catch (NumberFormatException e) {
                    System.out.println("Invalid threshold, using default 1024MB");
                }
            }

            // Set memory configuration
            BenchmarkScenario.BenchmarkMemoryConfig memoryConfig = new BenchmarkScenario.BenchmarkMemoryConfig();
            memoryConfig.setMemoryProfilingInterval(interval);
            memoryConfig.setMemoryThreshold(threshold * 1024L * 1024L); // Convert MB to bytes
            builder.memoryConfig(memoryConfig);
        }

        try {
            BenchmarkScenario scenario = builder.build();
            scenarioManager.addScenario(scenario);

            System.out.println("\nCustom scenario created successfully!");
            System.out.println("Name: " + scenario.getName());
            System.out.println("Combinations: " + scenario.getTotalCombinations());
            System.out.println(
                    "Estimated time: " + String.format("%.1f", scenario.estimateExecutionTimeMinutes()) + " minutes");

            // Show memory profiling info
            if (scenario.isEnableMemoryProfiling()) {
                System.out.println("Memory Profiling: Enabled");
                System.out.println("  Interval: " + scenario.getMemoryConfig().getMemoryProfilingInterval() + "ms");
                System.out.println(
                        "  Threshold: " + scenario.getMemoryConfig().getMemoryThreshold() / (1024 * 1024) + "MB");
            } else {
                System.out.println("Memory Profiling: Disabled");
            }

            System.out.print("\nRun this scenario now? (y/N): ");
            String runNow = scanner.nextLine().trim().toLowerCase();
            if ("y".equals(runNow) || "yes".equals(runNow)) {
                scenarioManager.executeScenario(scenario);
            }

        } catch (Exception e) {
            System.err.println("Error creating scenario: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Run quick test
     */
    private void runQuickTest() throws RunnerException {
        System.out.println("\nRunning quick test scenario...");
        System.out.println("This is a fast smoke test to verify benchmark functionality.");

        scenarioManager.executeScenario("quick");
    }

    /**
     * Run memory efficiency benchmark
     */
    private void runMemoryBenchmark() throws RunnerException {
        System.out.println("\nRunning memory efficiency benchmark...");
        System.out.println("This benchmark focuses on memory usage patterns and efficiency.");

        // Create a memory-focused scenario
        BenchmarkScenario memoryScenario = new BenchmarkScenario.Builder("memory-efficiency")
                .description("Memory efficiency testing with detailed profiling")
                .datasetSizes(BenchmarkConfiguration.DatasetSize.MEDIUM, BenchmarkConfiguration.DatasetSize.LARGE)
                .fileFormats(BenchmarkConfiguration.FileFormat.XLSX)
                .batchSizes(500, 1000, 2000)
                .includePatterns("*MemoryEfficiency*", "*Streaming*")
                .executionConfig(new BenchmarkScenario.BenchmarkExecutionConfig(2, 5, 3, 10, 1))
                .memoryConfig(new BenchmarkScenario.BenchmarkMemoryConfig("4g", "G1GC", true))
                .enableMemoryProfiling(true)
                .outputFormat("HTML")
                .build();

        scenarioManager.addScenario(memoryScenario);
        scenarioManager.executeScenario(memoryScenario);
    }

    /**
     * Show benchmark information
     */
    private void showBenchmarkInfo() {
        System.out.println("\n" + repeat("=", 70));
        System.out.println("FASTEXCEL BENCHMARK INFORMATION");
        System.out.println(repeat("=", 70));

        System.out.println("\nBenchmark Categories:");
        System.out.println("  • Micro-benchmarks: Test individual components (converters, data processing)");
        System.out.println("  • Operation benchmarks: Test read, write, and fill operations");
        System.out.println("  • Streaming benchmarks: Test memory-efficient streaming operations");
        System.out.println("  • Comparison benchmarks: Compare FastExcel vs Apache POI");
        System.out.println("  • Memory efficiency benchmarks: Test memory usage patterns");

        System.out.println("\nDataset Sizes:");
        for (BenchmarkConfiguration.DatasetSize size : BenchmarkConfiguration.DatasetSize.values()) {
            System.out.printf("  • %-12s: %,d rows (%s)%n", size.name(), size.getRowCount(), size.getLabel());
        }

        System.out.println("\nSupported File Formats:");
        for (BenchmarkConfiguration.FileFormat format : BenchmarkConfiguration.FileFormat.values()) {
            System.out.printf("  • %-4s: %s%n", format.name(), format.getExtension());
        }

        System.out.println(
                "\nAvailable Scenarios: " + scenarioManager.getScenarioNames().size());
        scenarioManager.getScenarioNames().forEach(name -> System.out.println("  • " + name));

        System.out.println("\nFramework: JMH (Java Microbenchmark Harness)");
        System.out.println("Memory Profiling: Real-time monitoring with GC tracking");
        System.out.println("Enhanced Memory Features:");
        System.out.println("  • Peak memory usage tracking");
        System.out.println("  • Average memory consumption");
        System.out.println("  • Memory allocation rate monitoring");
        System.out.println("  • Garbage collection statistics");
        System.out.println("  • Memory efficiency ratios");
        System.out.println("  • 95th percentile memory usage");
        System.out.println("  • Memory growth rate analysis");
        System.out.println("Report Formats: HTML, JSON, Text");
    }

    /**
     * Print command-line usage
     */
    private void printUsage() {
        System.out.println("FastExcel Benchmark Runner");
        System.out.println("Usage:");
        System.out.println("  java -jar fastexcel-benchmark.jar [command]");
        System.out.println();
        System.out.println("Commands:");
        System.out.println("  (no args)          - Run in interactive mode");
        System.out.println("  list, -l, --list   - List available scenarios");
        System.out.println("  quick, -q, --quick - Run quick test scenario");
        System.out.println("  memory, -m, --memory - Run memory efficiency benchmark");
        System.out.println("  run <scenario>     - Run specific scenario");
        System.out.println("  <scenario>         - Run scenario by name");
        System.out.println("  help, -h, --help   - Show this help");
        System.out.println();
        System.out.println("Available scenarios:");
        scenarioManager.getScenarioNames().forEach(name -> System.out.println("  • " + name));
        System.out.println();
        System.out.println("Examples:");
        System.out.println("  java -jar fastexcel-benchmark.jar quick");
        System.out.println("  java -jar fastexcel-benchmark.jar memory");
        System.out.println("  java -jar fastexcel-benchmark.jar run performance");
        System.out.println("  java -jar fastexcel-benchmark.jar comparison");
    }

    /**
     * Get the scenario manager (for testing)
     */
    public BenchmarkScenarioManager getScenarioManager() {
        return scenarioManager;
    }
}
