package cn.idev.excel.benchmark.memory;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Advanced memory profiler specifically designed for streaming operations.
 * Provides detailed memory usage tracking with streaming-specific metrics.
 */
public class StreamingMemoryProfiler {

    private final MemoryMXBean memoryBean;
    private final List<GarbageCollectorMXBean> gcBeans;
    private volatile ScheduledExecutorService scheduler;
    private final Object schedulerLock = new Object();

    private final List<MemorySnapshot> snapshots;
    private final AtomicLong totalAllocations;
    private final AtomicLong peakMemoryUsage;
    private final AtomicLong gcCount;
    private final AtomicLong gcTime;

    private volatile boolean profiling = false;
    private long startTime;
    private long initialHeapUsed;
    private long initialNonHeapUsed;

    public StreamingMemoryProfiler() {
        this.memoryBean = ManagementFactory.getMemoryMXBean();
        this.gcBeans = ManagementFactory.getGarbageCollectorMXBeans();
        this.scheduler = createScheduler();

        this.snapshots = new ArrayList<>();
        this.totalAllocations = new AtomicLong(0);
        this.peakMemoryUsage = new AtomicLong(0);
        this.gcCount = new AtomicLong(0);
        this.gcTime = new AtomicLong(0);
    }

    /**
     * Start memory profiling for streaming operations
     */
    public void startProfiling() {
        if (profiling) {
            return;
        }

        // Create a new scheduler if needed
        synchronized (schedulerLock) {
            if (scheduler.isShutdown() || scheduler.isTerminated()) {
                scheduler = createScheduler();
            }
        }

        profiling = true;
        startTime = System.currentTimeMillis();

        // Capture initial state
        MemoryUsage heapUsage = memoryBean.getHeapMemoryUsage();
        MemoryUsage nonHeapUsage = memoryBean.getNonHeapMemoryUsage();

        initialHeapUsed = heapUsage.getUsed();
        initialNonHeapUsed = nonHeapUsage.getUsed();

        // Reset counters
        snapshots.clear();
        totalAllocations.set(0);
        peakMemoryUsage.set(heapUsage.getUsed());
        resetGcCounters();

        // Start periodic sampling
        try {
            scheduler.scheduleAtFixedRate(this::captureSnapshot, 0, 50, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            profiling = false;
            throw new RuntimeException("Failed to start memory profiling", e);
        }
    }

    /**
     * Stop memory profiling and return results
     */
    public StreamingMemoryReport stopProfiling() {
        if (!profiling) {
            throw new IllegalStateException("Profiling is not active");
        }

        profiling = false;
        synchronized (schedulerLock) {
            if (scheduler != null && !scheduler.isShutdown()) {
                scheduler.shutdown();
                try {
                    if (!scheduler.awaitTermination(1, TimeUnit.SECONDS)) {
                        scheduler.shutdownNow();
                    }
                } catch (InterruptedException e) {
                    scheduler.shutdownNow();
                    Thread.currentThread().interrupt();
                }
            }
        }

        // Capture final snapshot
        captureSnapshot();

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        return new StreamingMemoryReport(
                duration,
                new ArrayList<>(snapshots),
                totalAllocations.get(),
                peakMemoryUsage.get(),
                calculateMemoryEfficiency(),
                calculateStreamingOverhead(),
                getGcStatistics());
    }

    /**
     * Capture current memory state
     */
    private void captureSnapshot() {
        long timestamp = System.currentTimeMillis() - startTime;
        MemoryUsage heapUsage = memoryBean.getHeapMemoryUsage();
        MemoryUsage nonHeapUsage = memoryBean.getNonHeapMemoryUsage();

        long currentHeapUsed = heapUsage.getUsed();
        long currentNonHeapUsed = nonHeapUsage.getUsed();

        // Update peak memory usage
        peakMemoryUsage.updateAndGet(current -> Math.max(current, currentHeapUsed));

        // Calculate allocation rate
        long heapDelta = currentHeapUsed - initialHeapUsed;
        if (heapDelta > 0) {
            totalAllocations.addAndGet(heapDelta);
        }

        MemorySnapshot snapshot = new MemorySnapshot(
                timestamp,
                currentHeapUsed,
                heapUsage.getCommitted(),
                heapUsage.getMax(),
                currentNonHeapUsed,
                nonHeapUsage.getCommitted(),
                calculateAllocationRate(),
                getCurrentGcCount(),
                getCurrentGcTime());

        snapshots.add(snapshot);
    }

    /**
     * Reset GC counters to baseline
     */
    private void resetGcCounters() {
        long initialGcCount = 0;
        long initialGcTime = 0;

        for (GarbageCollectorMXBean gcBean : gcBeans) {
            initialGcCount += gcBean.getCollectionCount();
            initialGcTime += gcBean.getCollectionTime();
        }

        gcCount.set(initialGcCount);
        gcTime.set(initialGcTime);
    }

    /**
     * Get current GC count
     */
    private long getCurrentGcCount() {
        long currentGcCount = 0;
        for (GarbageCollectorMXBean gcBean : gcBeans) {
            currentGcCount += gcBean.getCollectionCount();
        }
        return currentGcCount - gcCount.get();
    }

    /**
     * Get current GC time
     */
    private long getCurrentGcTime() {
        long currentGcTime = 0;
        for (GarbageCollectorMXBean gcBean : gcBeans) {
            currentGcTime += gcBean.getCollectionTime();
        }
        return currentGcTime - gcTime.get();
    }

    /**
     * Calculate allocation rate in bytes per second
     */
    private double calculateAllocationRate() {
        if (snapshots.isEmpty()) {
            return 0.0;
        }

        long elapsed = System.currentTimeMillis() - startTime;
        if (elapsed == 0) {
            return 0.0;
        }

        return (double) totalAllocations.get() / (elapsed / 1000.0);
    }

    /**
     * Calculate memory efficiency (processed data / peak memory usage)
     */
    private double calculateMemoryEfficiency() {
        long peak = peakMemoryUsage.get();
        if (peak == 0) {
            return 0.0;
        }

        // Estimate processed data size based on allocation patterns
        long processedData = totalAllocations.get();
        return (double) processedData / peak;
    }

    /**
     * Calculate streaming overhead compared to batch processing
     */
    private double calculateStreamingOverhead() {
        // Calculate overhead based on memory usage patterns
        if (snapshots.size() < 2) {
            return 0.0;
        }

        long totalMemoryTime = 0;
        for (int i = 1; i < snapshots.size(); i++) {
            MemorySnapshot current = snapshots.get(i);
            MemorySnapshot previous = snapshots.get(i - 1);

            long timeDelta = current.getTimestamp() - previous.getTimestamp();
            long memoryUsed = current.getHeapUsed();

            totalMemoryTime += memoryUsed * timeDelta;
        }

        long duration = snapshots.get(snapshots.size() - 1).getTimestamp();
        long averageMemory = totalMemoryTime / duration;

        // Calculate overhead as ratio of average memory to minimum required
        long minMemory =
                snapshots.stream().mapToLong(MemorySnapshot::getHeapUsed).min().orElse(averageMemory);

        return (double) (averageMemory - minMemory) / minMemory;
    }

    /**
     * Get GC statistics
     */
    private GcStatistics getGcStatistics() {
        long totalGcCount = getCurrentGcCount();
        long totalGcTime = getCurrentGcTime();

        double gcOverhead = 0.0;
        long duration = System.currentTimeMillis() - startTime;
        if (duration > 0) {
            gcOverhead = (double) totalGcTime / duration;
        }

        return new GcStatistics(totalGcCount, totalGcTime, gcOverhead);
    }

    /**
     * Force garbage collection for testing purposes
     */
    public void forceGc() {
        System.gc();
        System.runFinalization();

        // Give GC time to complete
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Check if profiling is currently active
     */
    public boolean isProfiling() {
        return profiling;
    }

    /**
     * Get current memory usage
     */
    public MemoryUsage getCurrentHeapUsage() {
        return memoryBean.getHeapMemoryUsage();
    }

    /**
     * Get current non-heap memory usage
     */
    public MemoryUsage getCurrentNonHeapUsage() {
        return memoryBean.getNonHeapMemoryUsage();
    }

    /**
     * Create a new scheduler
     */
    private ScheduledExecutorService createScheduler() {
        return Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "StreamingMemoryProfiler");
            t.setDaemon(true);
            return t;
        });
    }

    /**
     * Shutdown the profiler
     */
    public void shutdown() {
        profiling = false;
        synchronized (schedulerLock) {
            if (scheduler != null) {
                scheduler.shutdownNow();
            }
        }
    }

    /**
     * Memory snapshot at a specific point in time
     */
    public static class MemorySnapshot {
        private final long timestamp;
        private final long heapUsed;
        private final long heapCommitted;
        private final long heapMax;
        private final long nonHeapUsed;
        private final long nonHeapCommitted;
        private final double allocationRate;
        private final long gcCount;
        private final long gcTime;

        public MemorySnapshot(
                long timestamp,
                long heapUsed,
                long heapCommitted,
                long heapMax,
                long nonHeapUsed,
                long nonHeapCommitted,
                double allocationRate,
                long gcCount,
                long gcTime) {
            this.timestamp = timestamp;
            this.heapUsed = heapUsed;
            this.heapCommitted = heapCommitted;
            this.heapMax = heapMax;
            this.nonHeapUsed = nonHeapUsed;
            this.nonHeapCommitted = nonHeapCommitted;
            this.allocationRate = allocationRate;
            this.gcCount = gcCount;
            this.gcTime = gcTime;
        }

        // Getters
        public long getTimestamp() {
            return timestamp;
        }

        public long getHeapUsed() {
            return heapUsed;
        }

        public long getHeapCommitted() {
            return heapCommitted;
        }

        public long getHeapMax() {
            return heapMax;
        }

        public long getNonHeapUsed() {
            return nonHeapUsed;
        }

        public long getNonHeapCommitted() {
            return nonHeapCommitted;
        }

        public double getAllocationRate() {
            return allocationRate;
        }

        public long getGcCount() {
            return gcCount;
        }

        public long getGcTime() {
            return gcTime;
        }

        public double getHeapUtilization() {
            return heapMax > 0 ? (double) heapUsed / heapMax : 0.0;
        }
    }

    /**
     * Garbage collection statistics
     */
    public static class GcStatistics {
        private final long totalCollections;
        private final long totalTime;
        private final double overhead;

        public GcStatistics(long totalCollections, long totalTime, double overhead) {
            this.totalCollections = totalCollections;
            this.totalTime = totalTime;
            this.overhead = overhead;
        }

        public long getTotalCollections() {
            return totalCollections;
        }

        public long getTotalTime() {
            return totalTime;
        }

        public double getOverhead() {
            return overhead;
        }

        public double getAverageCollectionTime() {
            return totalCollections > 0 ? (double) totalTime / totalCollections : 0.0;
        }
    }

    /**
     * Comprehensive memory profiling report for streaming operations
     */
    public static class StreamingMemoryReport {
        private final long duration;
        private final List<MemorySnapshot> snapshots;
        private final long totalAllocations;
        private final long peakMemoryUsage;
        private final double memoryEfficiency;
        private final double streamingOverhead;
        private final GcStatistics gcStatistics;

        public StreamingMemoryReport(
                long duration,
                List<MemorySnapshot> snapshots,
                long totalAllocations,
                long peakMemoryUsage,
                double memoryEfficiency,
                double streamingOverhead,
                GcStatistics gcStatistics) {
            this.duration = duration;
            this.snapshots = snapshots;
            this.totalAllocations = totalAllocations;
            this.peakMemoryUsage = peakMemoryUsage;
            this.memoryEfficiency = memoryEfficiency;
            this.streamingOverhead = streamingOverhead;
            this.gcStatistics = gcStatistics;
        }

        // Getters
        public long getDuration() {
            return duration;
        }

        public List<MemorySnapshot> getSnapshots() {
            return snapshots;
        }

        public long getTotalAllocations() {
            return totalAllocations;
        }

        public long getPeakMemoryUsage() {
            return peakMemoryUsage;
        }

        public double getMemoryEfficiency() {
            return memoryEfficiency;
        }

        public double getStreamingOverhead() {
            return streamingOverhead;
        }

        public GcStatistics getGcStatistics() {
            return gcStatistics;
        }

        public double getAllocationRate() {
            return duration > 0 ? (double) totalAllocations / (duration / 1000.0) : 0.0;
        }

        public double getAverageMemoryUsage() {
            if (snapshots.isEmpty()) {
                return 0.0;
            }

            return snapshots.stream()
                    .mapToLong(MemorySnapshot::getHeapUsed)
                    .average()
                    .orElse(0.0);
        }

        public MemorySnapshot getPeakSnapshot() {
            return snapshots.stream()
                    .max((s1, s2) -> Long.compare(s1.getHeapUsed(), s2.getHeapUsed()))
                    .orElse(null);
        }

        @Override
        public String toString() {
            return String.format(
                    "StreamingMemoryReport{duration=%dms, peakMemory=%d bytes, "
                            + "totalAllocations=%d bytes, efficiency=%.2f, overhead=%.2f%%, "
                            + "gcCollections=%d, gcTime=%dms}",
                    duration,
                    peakMemoryUsage,
                    totalAllocations,
                    memoryEfficiency,
                    streamingOverhead * 100,
                    gcStatistics.getTotalCollections(),
                    gcStatistics.getTotalTime());
        }
    }
}
