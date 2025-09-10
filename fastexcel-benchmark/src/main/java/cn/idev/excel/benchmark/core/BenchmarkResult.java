package cn.idev.excel.benchmark.core;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Represents the result of a benchmark execution
 */
public class BenchmarkResult {

    private String benchmarkName;
    private String operationType;
    private String fileFormat;
    private int datasetSize;
    private LocalDateTime timestamp;

    // Performance metrics
    private double throughputOpsPerSecond;
    private double averageTimeMs;
    private double minTimeMs;
    private double maxTimeMs;
    private double standardDeviation;

    // Memory metrics
    private long maxMemoryUsedBytes;
    private long avgMemoryUsedBytes;
    private long memoryAllocatedBytes;
    private int gcCount;
    private long gcTimeMs;

    // Additional metadata
    private Map<String, Object> metadata;
    private String jvmVersion;
    private String osInfo;
    private int warmupIterations;
    private int measurementIterations;
    private int forkCount;

    // Constructors
    public BenchmarkResult() {
        this.timestamp = LocalDateTime.now();
    }

    public BenchmarkResult(String benchmarkName, String operationType, String fileFormat, int datasetSize) {
        this();
        this.benchmarkName = benchmarkName;
        this.operationType = operationType;
        this.fileFormat = fileFormat;
        this.datasetSize = datasetSize;
    }

    // Getters and setters
    public String getBenchmarkName() {
        return benchmarkName;
    }

    public void setBenchmarkName(String benchmarkName) {
        this.benchmarkName = benchmarkName;
    }

    public String getOperationType() {
        return operationType;
    }

    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }

    public String getFileFormat() {
        return fileFormat;
    }

    public void setFileFormat(String fileFormat) {
        this.fileFormat = fileFormat;
    }

    public int getDatasetSize() {
        return datasetSize;
    }

    public void setDatasetSize(int datasetSize) {
        this.datasetSize = datasetSize;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public double getThroughputOpsPerSecond() {
        return throughputOpsPerSecond;
    }

    public void setThroughputOpsPerSecond(double throughputOpsPerSecond) {
        this.throughputOpsPerSecond = throughputOpsPerSecond;
    }

    public double getAverageTimeMs() {
        return averageTimeMs;
    }

    public void setAverageTimeMs(double averageTimeMs) {
        this.averageTimeMs = averageTimeMs;
    }

    public double getMinTimeMs() {
        return minTimeMs;
    }

    public void setMinTimeMs(double minTimeMs) {
        this.minTimeMs = minTimeMs;
    }

    public double getMaxTimeMs() {
        return maxTimeMs;
    }

    public void setMaxTimeMs(double maxTimeMs) {
        this.maxTimeMs = maxTimeMs;
    }

    public double getStandardDeviation() {
        return standardDeviation;
    }

    public void setStandardDeviation(double standardDeviation) {
        this.standardDeviation = standardDeviation;
    }

    public long getMaxMemoryUsedBytes() {
        return maxMemoryUsedBytes;
    }

    public void setMaxMemoryUsedBytes(long maxMemoryUsedBytes) {
        this.maxMemoryUsedBytes = maxMemoryUsedBytes;
    }

    public long getAvgMemoryUsedBytes() {
        return avgMemoryUsedBytes;
    }

    public void setAvgMemoryUsedBytes(long avgMemoryUsedBytes) {
        this.avgMemoryUsedBytes = avgMemoryUsedBytes;
    }

    public long getMemoryAllocatedBytes() {
        return memoryAllocatedBytes;
    }

    public void setMemoryAllocatedBytes(long memoryAllocatedBytes) {
        this.memoryAllocatedBytes = memoryAllocatedBytes;
    }

    public int getGcCount() {
        return gcCount;
    }

    public void setGcCount(int gcCount) {
        this.gcCount = gcCount;
    }

    public long getGcTimeMs() {
        return gcTimeMs;
    }

    public void setGcTimeMs(long gcTimeMs) {
        this.gcTimeMs = gcTimeMs;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }

    public String getJvmVersion() {
        return jvmVersion;
    }

    public void setJvmVersion(String jvmVersion) {
        this.jvmVersion = jvmVersion;
    }

    public String getOsInfo() {
        return osInfo;
    }

    public void setOsInfo(String osInfo) {
        this.osInfo = osInfo;
    }

    public int getWarmupIterations() {
        return warmupIterations;
    }

    public void setWarmupIterations(int warmupIterations) {
        this.warmupIterations = warmupIterations;
    }

    public int getMeasurementIterations() {
        return measurementIterations;
    }

    public void setMeasurementIterations(int measurementIterations) {
        this.measurementIterations = measurementIterations;
    }

    public int getForkCount() {
        return forkCount;
    }

    public void setForkCount(int forkCount) {
        this.forkCount = forkCount;
    }

    @Override
    public String toString() {
        return String.format(
                "BenchmarkResult{name='%s', operation='%s', format='%s', size=%d, throughput=%.2f ops/sec, avgTime=%.2f ms, maxMemory=%d bytes}",
                benchmarkName,
                operationType,
                fileFormat,
                datasetSize,
                throughputOpsPerSecond,
                averageTimeMs,
                maxMemoryUsedBytes);
    }
}
