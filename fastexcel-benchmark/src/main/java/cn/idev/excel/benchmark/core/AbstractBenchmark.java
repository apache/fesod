package cn.idev.excel.benchmark.core;

import cn.idev.excel.benchmark.utils.MemoryProfiler;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract base class for all benchmarks providing common functionality
 */
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
@State(Scope.Benchmark)
@Warmup(iterations = BenchmarkConfiguration.DEFAULT_WARMUP_ITERATIONS, time = 1)
@Measurement(iterations = BenchmarkConfiguration.DEFAULT_MEASUREMENT_ITERATIONS, time = 1)
@Fork(BenchmarkConfiguration.DEFAULT_FORK_COUNT)
public abstract class AbstractBenchmark {

    protected static final Logger logger = LoggerFactory.getLogger(AbstractBenchmark.class);

    protected MemoryProfiler memoryProfiler;
    protected String outputDirectory;
    protected String benchmarkName;

    @Setup(Level.Trial)
    public void setupTrial() throws Exception {
        benchmarkName = this.getClass().getSimpleName();
        outputDirectory = BenchmarkConfiguration.DEFAULT_OUTPUT_DIR + File.separator + benchmarkName;

        // Create output directories
        createDirectories();

        // Initialize memory profiler if enabled
        if (BenchmarkConfiguration.ENABLE_MEMORY_PROFILING) {
            memoryProfiler = new MemoryProfiler();
        }

        logger.info("Setting up benchmark: {}", benchmarkName);
        setupBenchmark();
    }

    @TearDown(Level.Trial)
    public void tearDownTrial() throws Exception {
        logger.info("Tearing down benchmark: {}", benchmarkName);
        tearDownBenchmark();

        if (memoryProfiler != null) {
            memoryProfiler.stop();
        }
    }

    @Setup(Level.Iteration)
    public void setupIteration() throws Exception {
        if (memoryProfiler != null) {
            try {
                memoryProfiler.reset();
                memoryProfiler.start();
            } catch (Exception e) {
                logger.warn("Failed to start memory profiler: {}", e.getMessage());
                // Continue without memory profiling
            }
        }
        setupIteration0();
    }

    @TearDown(Level.Iteration)
    public void tearDownIteration() throws Exception {
        tearDownIteration0();

        if (memoryProfiler != null) {
            try {
                memoryProfiler.stop();
                logMemoryUsage();
            } catch (Exception e) {
                logger.warn("Failed to stop memory profiler: {}", e.getMessage());
                // Continue without memory profiling
            }
        }
    }

    /**
     * Template method for benchmark-specific setup
     */
    protected abstract void setupBenchmark() throws Exception;

    /**
     * Template method for benchmark-specific teardown
     */
    protected abstract void tearDownBenchmark() throws Exception;

    /**
     * Template method for iteration-specific setup
     */
    protected void setupIteration0() throws Exception {
        // Default implementation does nothing
    }

    /**
     * Template method for iteration-specific teardown
     */
    protected void tearDownIteration0() throws Exception {
        // Default implementation does nothing
    }

    /**
     * Create necessary output directories
     */
    private void createDirectories() throws IOException {
        Path outputPath = Paths.get(outputDirectory);
        if (!Files.exists(outputPath)) {
            Files.createDirectories(outputPath);
        }
    }

    /**
     * Log memory usage information
     */
    private void logMemoryUsage() {
        if (memoryProfiler != null) {
            MemoryProfiler.MemorySnapshot snapshot = memoryProfiler.getSnapshot();
            logger.debug(
                    "Memory usage - Max: {} MB, Avg: {} MB, Allocated: {} MB, GC Count: {}, GC Time: {} ms",
                    snapshot.getMaxUsedMemoryMB(),
                    snapshot.getAvgUsedMemoryMB(),
                    snapshot.getAllocatedMemoryMB(),
                    snapshot.getGcCount(),
                    snapshot.getGcTime());
        }
    }

    /**
     * Get a temporary file path for the given format and size
     */
    protected String getTempFilePath(
            BenchmarkConfiguration.FileFormat format, BenchmarkConfiguration.DatasetSize size) {
        return outputDirectory + File.separator + "temp_" + size.getLabel() + "." + format.getExtension();
    }

    /**
     * Clean up temporary files
     */
    protected void cleanupTempFiles() {
        try {
            Path outputPath = Paths.get(outputDirectory);
            if (Files.exists(outputPath)) {
                Files.walk(outputPath)
                        .filter(path -> path.getFileName().toString().startsWith("temp_"))
                        .forEach(path -> {
                            try {
                                Files.deleteIfExists(path);
                            } catch (IOException e) {
                                logger.warn("Failed to delete temp file: {}", path, e);
                            }
                        });
            }
        } catch (IOException e) {
            logger.warn("Failed to cleanup temp files", e);
        }
    }

    /**
     * Force garbage collection and wait for it to complete
     */
    protected void forceGC() {
        System.gc();
        System.runFinalization();
        try {
            Thread.sleep(100); // Give GC time to complete
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Consume data to prevent JVM optimizations
     */
    protected void consumeData(Object data, Blackhole blackhole) {
        if (blackhole != null) {
            blackhole.consume(data);
        }
    }

    /**
     * Get current memory usage in bytes
     */
    protected long getCurrentMemoryUsage() {
        Runtime runtime = Runtime.getRuntime();
        return runtime.totalMemory() - runtime.freeMemory();
    }

    /**
     * Get the benchmark result template with common metrics
     */
    protected BenchmarkResult createResultTemplate(String operationType, String fileFormat, int datasetSize) {
        BenchmarkResult result = new BenchmarkResult(benchmarkName, operationType, fileFormat, datasetSize);

        // Set system information
        result.setJvmVersion(System.getProperty("java.version"));
        result.setOsInfo(System.getProperty("os.name") + " " + System.getProperty("os.version"));
        result.setWarmupIterations(BenchmarkConfiguration.DEFAULT_WARMUP_ITERATIONS);
        result.setMeasurementIterations(BenchmarkConfiguration.DEFAULT_MEASUREMENT_ITERATIONS);
        result.setForkCount(BenchmarkConfiguration.DEFAULT_FORK_COUNT);

        return result;
    }
}
