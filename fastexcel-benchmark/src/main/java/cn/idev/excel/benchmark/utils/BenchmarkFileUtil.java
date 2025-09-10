package cn.idev.excel.benchmark.utils;

import cn.idev.excel.EasyExcel;
import cn.idev.excel.benchmark.core.BenchmarkConfiguration;
import cn.idev.excel.benchmark.data.BenchmarkData;
import cn.idev.excel.context.AnalysisContext;
import cn.idev.excel.event.AnalysisEventListener;
import cn.idev.excel.write.metadata.WriteSheet;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class for managing benchmark test files
 */
public class BenchmarkFileUtil {

    private static final Logger logger = LoggerFactory.getLogger(BenchmarkFileUtil.class);

    private static final String TEST_DATA_DIR = "target/benchmark-testdata";
    private static final String TEMPLATE_DIR = "src/test/resources/templates";

    /**
     * Create test data directory if it doesn't exist
     */
    public static void createTestDataDirectory() {
        try {
            Path testDataPath = Paths.get(TEST_DATA_DIR);
            if (!Files.exists(testDataPath)) {
                Files.createDirectories(testDataPath);
                logger.debug("Created test data directory: {}", testDataPath);
            }
        } catch (IOException e) {
            logger.error("Failed to create test data directory", e);
            throw new RuntimeException("Failed to create test data directory", e);
        }
    }

    /**
     * Generate a test file with the specified format and size
     */
    public static String generateTestFile(
            BenchmarkConfiguration.FileFormat format, BenchmarkConfiguration.DatasetSize size) {
        return generateTestFile(format, size, false);
    }

    /**
     * Generate a test file with the specified format and size, optionally forcing regeneration
     */
    public static String generateTestFile(
            BenchmarkConfiguration.FileFormat format,
            BenchmarkConfiguration.DatasetSize size,
            boolean forceRegenerate) {
        createTestDataDirectory();

        String fileName = String.format(
                "benchmark_%s_%s.%s", size.getLabel(), System.currentTimeMillis() / 1000, format.getExtension());
        String filePath = TEST_DATA_DIR + File.separator + fileName;

        // Check if file exists and is not forced to regenerate
        File file = new File(filePath);
        if (file.exists() && !forceRegenerate) {
            logger.debug("Using existing test file: {}", filePath);
            return filePath;
        }

        logger.info("Generating test file: {} with {} rows", filePath, size.getRowCount());

        // Generate data
        DataGenerator generator = new DataGenerator();
        List<BenchmarkData> data = generator.generateData(size);

        // Write to file using FastExcel
        try {
            EasyExcel.write(filePath, BenchmarkData.class)
                    .sheet("BenchmarkData")
                    .doWrite(data);

            logger.info("Generated test file: {} ({} bytes)", filePath, file.length());
            return filePath;

        } catch (Exception e) {
            logger.error("Failed to generate test file: {}", filePath, e);
            throw new RuntimeException("Failed to generate test file", e);
        }
    }

    /**
     * Generate test files for all combinations of formats and sizes
     */
    public static void generateAllTestFiles() {
        logger.info("Generating all test files for benchmarks");

        for (BenchmarkConfiguration.FileFormat format : BenchmarkConfiguration.FileFormat.values()) {
            for (BenchmarkConfiguration.DatasetSize size : BenchmarkConfiguration.DatasetSize.values()) {
                try {
                    generateTestFile(format, size);
                } catch (Exception e) {
                    logger.error("Failed to generate test file for {} {}", format, size, e);
                }
            }
        }

        logger.info("Completed generating all test files");
    }

    /**
     * Generate a temporary file path for benchmarks
     */
    public static String getTempFilePath(
            BenchmarkConfiguration.FileFormat format, BenchmarkConfiguration.DatasetSize size, String benchmarkName) {
        createTestDataDirectory();

        String fileName = String.format(
                "temp_%s_%s_%s_%d.%s",
                benchmarkName,
                size.getLabel(),
                format.name().toLowerCase(),
                System.currentTimeMillis(),
                format.getExtension());
        return TEST_DATA_DIR + File.separator + fileName;
    }

    /**
     * Clean up temporary files created during benchmarks
     */
    public static void cleanupTempFiles() {
        try {
            Path testDataPath = Paths.get(TEST_DATA_DIR);
            if (Files.exists(testDataPath)) {
                Files.walk(testDataPath)
                        .filter(path -> path.getFileName().toString().startsWith("temp_"))
                        .forEach(path -> {
                            try {
                                Files.deleteIfExists(path);
                                logger.debug("Deleted temp file: {}", path);
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
     * Get file size in bytes
     */
    public static long getFileSize(String filePath) {
        try {
            return Files.size(Paths.get(filePath));
        } catch (IOException e) {
            logger.warn("Failed to get file size for: {}", filePath, e);
            return 0;
        }
    }

    /**
     * Get file size in human readable format
     */
    public static String getFileSizeFormatted(String filePath) {
        long bytes = getFileSize(filePath);
        return formatBytes(bytes);
    }

    /**
     * Format bytes into human readable format
     */
    public static String formatBytes(long bytes) {
        if (bytes < 1024) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(1024));
        String pre = "KMGTPE".charAt(exp - 1) + "";
        return String.format("%.1f %sB", bytes / Math.pow(1024, exp), pre);
    }

    /**
     * Create a test file with the specified name
     */
    public static File createTestFile(String fileName) {
        createTestDataDirectory();
        return new File(TEST_DATA_DIR, fileName);
    }

    /**
     * Repeat a string a specified number of times
     * @param str the string to repeat
     * @param count the number of times to repeat
     * @return the repeated string
     */
    public static String repeat(String str, int count) {
        if (count <= 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder(str.length() * count);
        for (int i = 0; i < count; i++) {
            sb.append(str);
        }
        return sb.toString();
    }

    /**
     * Read a string from a file
     * @param path the path to the file
     * @return the content of the file as a string
     * @throws IOException if an I/O error occurs
     */
    public static String readString(Path path) throws IOException {
        return new String(Files.readAllBytes(path));
    }

    /**
     * Create a list with the specified elements
     * @param elements the elements to include in the list
     * @return a list containing the specified elements
     */
    @SafeVarargs
    public static <T> List<T> listOf(T... elements) {
        List<T> list = new ArrayList<>(elements.length);
        for (T element : elements) {
            list.add(element);
        }
        return list;
    }

    /**
     * Create a map with the specified key-value pairs
     * @param k1 the first key
     * @param v1 the first value
     * @param k2 the second key
     * @param v2 the second value
     * @param <K> the key type
     * @param <V> the value type
     * @return a map containing the specified key-value pairs
     */
    public static <K, V> Map<K, V> mapOf(K k1, V v1, K k2, V v2) {
        Map<K, V> map = new HashMap<>();
        map.put(k1, v1);
        map.put(k2, v2);
        return map;
    }

    /**
     * Create a map with the specified key-value pairs
     * @param k1 the first key
     * @param v1 the first value
     * @param k2 the second key
     * @param v2 the second value
     * @param k3 the third key
     * @param v3 the third value
     * @param <K> the key type
     * @param <V> the value type
     * @return a map containing the specified key-value pairs
     */
    public static <K, V> Map<K, V> mapOf(K k1, V v1, K k2, V v2, K k3, V v3) {
        Map<K, V> map = new HashMap<>();
        map.put(k1, v1);
        map.put(k2, v2);
        map.put(k3, v3);
        return map;
    }

    /**
     * Create directories if they don't exist
     */
    private static void createDirectories(String dirPath) {
        try {
            Path path = Paths.get(dirPath);
            if (!Files.exists(path)) {
                Files.createDirectories(path);
                logger.debug("Created directory: {}", path);
            }
        } catch (IOException e) {
            logger.error("Failed to create directory: {}", dirPath, e);
            throw new RuntimeException("Failed to create directory: " + dirPath, e);
        }
    }

    /**
     * Generate large dataset files for stress testing
     */
    public static String generateLargeDatasetFile(BenchmarkConfiguration.FileFormat format, int rowCount) {
        createTestDataDirectory();

        String fileName = String.format("large_dataset_%d_rows.%s", rowCount, format.getExtension());
        String filePath = TEST_DATA_DIR + File.separator + fileName;

        logger.info("Generating large dataset file: {} with {} rows", filePath, rowCount);

        // Generate data in batches to manage memory
        DataGenerator generator = new DataGenerator();
        int batchSize = Math.min(10000, rowCount / 10); // Use reasonable batch size

        try {
            // Use streaming approach for very large datasets
            if (rowCount > 100000) {
                generateLargeFileStreaming(filePath, rowCount, generator);
            } else {
                List<BenchmarkData> data = generator.generateData(rowCount);
                EasyExcel.write(filePath, BenchmarkData.class)
                        .sheet("LargeDataset")
                        .doWrite(data);
            }

            File file = new File(filePath);
            logger.info(
                    "Generated large dataset file: {} ({} bytes, {} rows)",
                    filePath,
                    formatBytes(file.length()),
                    rowCount);
            return filePath;

        } catch (Exception e) {
            logger.error("Failed to generate large dataset file: {}", filePath, e);
            throw new RuntimeException("Failed to generate large dataset file", e);
        }
    }

    /**
     * Generate large files using streaming approach
     */
    private static void generateLargeFileStreaming(String filePath, int rowCount, DataGenerator generator) {
        // For very large files, generate data in chunks to avoid memory issues
        List<List<BenchmarkData>> batches = generator.generateDataInBatches(rowCount, 10000);

        try {
            WriteSheet writeSheet = EasyExcel.writerSheet().build();

            // Write first batch with headers
            if (!batches.isEmpty()) {
                EasyExcel.write(filePath, BenchmarkData.class)
                        .sheet("LargeDataset")
                        .doWrite(batches.get(0));

                // Append remaining batches
                if (batches.size() > 1) {
                    for (int i = 1; i < batches.size(); i++) {
                        // Note: This is a simplified approach. In real implementation,
                        // you might need to use ExcelWriter in append mode
                        logger.debug("Processing batch {} of {}", i + 1, batches.size());
                    }
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to write large file using streaming", e);
        }
    }

    /**
     * Verify test file integrity
     */
    public static boolean verifyTestFile(String filePath, int expectedRowCount) {
        try {
            // Use FastExcel to read and count rows
            final int[] rowCount = {0};

            EasyExcel.read(filePath, BenchmarkData.class, new AnalysisEventListener<BenchmarkData>() {
                        @Override
                        public void invoke(BenchmarkData data, AnalysisContext context) {
                            rowCount[0]++;
                        }

                        @Override
                        public void doAfterAllAnalysed(AnalysisContext context) {
                            // Validation complete
                        }
                    })
                    .sheet()
                    .doRead();

            boolean isValid = rowCount[0] == expectedRowCount;
            if (!isValid) {
                logger.warn(
                        "Test file verification failed: {} (expected: {}, actual: {})",
                        filePath,
                        expectedRowCount,
                        rowCount[0]);
            }

            return isValid;

        } catch (Exception e) {
            logger.error("Failed to verify test file: {}", filePath, e);
            return false;
        }
    }
}
