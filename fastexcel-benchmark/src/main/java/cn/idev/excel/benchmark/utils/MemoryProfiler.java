package cn.idev.excel.benchmark.utils;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class for profiling memory usage during benchmark execution
 */
public class MemoryProfiler {

    private static final Logger logger = LoggerFactory.getLogger(MemoryProfiler.class);

    private final MemoryMXBean memoryBean;
    private final List<GarbageCollectorMXBean> gcBeans;
    private volatile ScheduledExecutorService scheduler;
    private final AtomicBoolean running;
    private final Object schedulerLock = new Object();

    // Memory tracking variables
    private final AtomicLong maxUsedMemory;
    private final AtomicLong totalMemorySamples;
    private final AtomicLong sumMemoryUsage;
    private final List<Long> memorySnapshots;

    // GC tracking variables
    private long initialGcCount;
    private long initialGcTime;
    private long startTime;

    public MemoryProfiler() {
        this.memoryBean = ManagementFactory.getMemoryMXBean();
        this.gcBeans = ManagementFactory.getGarbageCollectorMXBeans();
        this.scheduler = createScheduler();
        this.running = new AtomicBoolean(false);
        this.maxUsedMemory = new AtomicLong(0);
        this.totalMemorySamples = new AtomicLong(0);
        this.sumMemoryUsage = new AtomicLong(0);
        this.memorySnapshots = new ArrayList<>();
    }

    /**
     * Start memory profiling
     */
    public void start() {
        if (running.compareAndSet(false, true)) {
            reset();
            startTime = System.currentTimeMillis();

            // Record initial GC stats
            initialGcCount = getTotalGcCount();
            initialGcTime = getTotalGcTime();

            // Create a new scheduler if needed
            synchronized (schedulerLock) {
                if (scheduler.isShutdown() || scheduler.isTerminated()) {
                    scheduler = createScheduler();
                }
            }

            try {
                // Start memory sampling
                scheduler.scheduleAtFixedRate(
                        this::sampleMemory,
                        0,
                        cn.idev.excel.benchmark.core.BenchmarkConfiguration.MEMORY_SAMPLING_INTERVAL_MS,
                        TimeUnit.MILLISECONDS);

                logger.debug("Memory profiling started");
            } catch (Exception e) {
                logger.warn("Failed to start memory sampling: {}", e.getMessage());
                running.set(false);
            }
        }
    }

    /**
     * Stop memory profiling
     */
    public void stop() {
        if (running.compareAndSet(true, false)) {
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
            logger.debug("Memory profiling stopped");
        }
    }

    /**
     * Reset all memory tracking variables
     */
    public void reset() {
        maxUsedMemory.set(0);
        totalMemorySamples.set(0);
        sumMemoryUsage.set(0);
        synchronized (memorySnapshots) {
            memorySnapshots.clear();
        }
    }

    /**
     * Sample current memory usage
     */
    private void sampleMemory() {
        try {
            MemoryUsage heapUsage = memoryBean.getHeapMemoryUsage();
            long currentUsed = heapUsage.getUsed();

            // Update max memory usage
            maxUsedMemory.updateAndGet(current -> Math.max(current, currentUsed));

            // Update average calculation
            totalMemorySamples.incrementAndGet();
            sumMemoryUsage.addAndGet(currentUsed);

            // Store snapshot for detailed analysis
            synchronized (memorySnapshots) {
                memorySnapshots.add(currentUsed);
            }

        } catch (Exception e) {
            logger.warn("Error sampling memory usage", e);
        }
    }

    /**
     * Get current memory snapshot
     */
    public MemorySnapshot getSnapshot() {
        MemoryUsage heapUsage = memoryBean.getHeapMemoryUsage();

        long maxUsed = maxUsedMemory.get();
        long samples = totalMemorySamples.get();
        long avgUsed = samples > 0 ? sumMemoryUsage.get() / samples : 0;

        long currentGcCount = getTotalGcCount();
        long currentGcTime = getTotalGcTime();

        return new MemorySnapshot(
                maxUsed,
                avgUsed,
                heapUsage.getCommitted(),
                currentGcCount - initialGcCount,
                currentGcTime - initialGcTime,
                System.currentTimeMillis() - startTime);
    }

    /**
     * Shutdown the profiler
     */
    public void shutdown() {
        stop();
        synchronized (schedulerLock) {
            if (scheduler != null) {
                scheduler.shutdownNow();
            }
        }
    }

    /**
     * Create a new scheduler
     */
    private ScheduledExecutorService createScheduler() {
        return Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "MemoryProfiler");
            t.setDaemon(true);
            return t;
        });
    }

    /**
     * Get total GC time across all collectors
     */
    private long getTotalGcTime() {
        return gcBeans.stream()
                .mapToLong(bean -> bean.getCollectionTime() > 0 ? bean.getCollectionTime() : 0)
                .sum();
    }

    /**
     * Get total GC count across all collectors
     */
    private long getTotalGcCount() {
        return gcBeans.stream()
                .mapToLong(bean -> bean.getCollectionCount() > 0 ? bean.getCollectionCount() : 0)
                .sum();
    }

    /**
     * Get current memory usage
     */
    public long getUsedMemory() {
        return memoryBean.getHeapMemoryUsage().getUsed();
    }

    /**
     * Get peak memory usage
     */
    public long getPeakMemoryUsage() {
        return maxUsedMemory.get();
    }

    /**
     * Get detailed memory statistics
     */
    public MemoryStatistics getDetailedStatistics() {
        List<Long> snapshots;
        synchronized (memorySnapshots) {
            snapshots = new ArrayList<>(memorySnapshots);
        }

        if (snapshots.isEmpty()) {
            return new MemoryStatistics(0, 0, 0, 0, 0);
        }

        long min = snapshots.stream().mapToLong(Long::longValue).min().orElse(0);
        long max = snapshots.stream().mapToLong(Long::longValue).max().orElse(0);
        double avg = snapshots.stream().mapToLong(Long::longValue).average().orElse(0);

        // Calculate standard deviation
        double variance = snapshots.stream()
                .mapToDouble(value -> Math.pow(value - avg, 2))
                .average()
                .orElse(0);
        double stdDev = Math.sqrt(variance);

        // Calculate 95th percentile
        snapshots.sort(Long::compareTo);
        int p95Index = (int) Math.ceil(0.95 * snapshots.size()) - 1;
        long p95 = snapshots.get(Math.max(0, p95Index));

        return new MemoryStatistics(min, max, (long) avg, (long) stdDev, p95);
    }

    /**
     * Memory snapshot data class
     */
    public static class MemorySnapshot {
        private final long maxUsedMemory;
        private final long avgUsedMemory;
        private final long allocatedMemory;
        private final long gcCount;
        private final long gcTime;
        private final long durationMs;

        public MemorySnapshot(
                long maxUsedMemory,
                long avgUsedMemory,
                long allocatedMemory,
                long gcCount,
                long gcTime,
                long durationMs) {
            this.maxUsedMemory = maxUsedMemory;
            this.avgUsedMemory = avgUsedMemory;
            this.allocatedMemory = allocatedMemory;
            this.gcCount = gcCount;
            this.gcTime = gcTime;
            this.durationMs = durationMs;
        }

        public long getMaxUsedMemory() {
            return maxUsedMemory;
        }

        public long getAvgUsedMemory() {
            return avgUsedMemory;
        }

        public long getAllocatedMemory() {
            return allocatedMemory;
        }

        public long getGcCount() {
            return gcCount;
        }

        public long getGcTime() {
            return gcTime;
        }

        public long getDurationMs() {
            return durationMs;
        }

        public double getMaxUsedMemoryMB() {
            return maxUsedMemory / (1024.0 * 1024.0);
        }

        public double getAvgUsedMemoryMB() {
            return avgUsedMemory / (1024.0 * 1024.0);
        }

        public double getAllocatedMemoryMB() {
            return allocatedMemory / (1024.0 * 1024.0);
        }

        @Override
        public String toString() {
            return String.format(
                    "MemorySnapshot{maxUsed=%.2f MB, avgUsed=%.2f MB, allocated=%.2f MB, gcCount=%d, gcTime=%d ms, duration=%d ms}",
                    getMaxUsedMemoryMB(), getAvgUsedMemoryMB(), getAllocatedMemoryMB(), gcCount, gcTime, durationMs);
        }
    }

    /**
     * Detailed memory statistics data class
     */
    public static class MemoryStatistics {
        private final long minMemory;
        private final long maxMemory;
        private final long avgMemory;
        private final long stdDevMemory;
        private final long p95Memory;

        public MemoryStatistics(long minMemory, long maxMemory, long avgMemory, long stdDevMemory, long p95Memory) {
            this.minMemory = minMemory;
            this.maxMemory = maxMemory;
            this.avgMemory = avgMemory;
            this.stdDevMemory = stdDevMemory;
            this.p95Memory = p95Memory;
        }

        public long getMinMemory() {
            return minMemory;
        }

        public long getMaxMemory() {
            return maxMemory;
        }

        public long getAvgMemory() {
            return avgMemory;
        }

        public long getStdDevMemory() {
            return stdDevMemory;
        }

        public long getP95Memory() {
            return p95Memory;
        }

        public double getMinMemoryMB() {
            return minMemory / (1024.0 * 1024.0);
        }

        public double getMaxMemoryMB() {
            return maxMemory / (1024.0 * 1024.0);
        }

        public double getAvgMemoryMB() {
            return avgMemory / (1024.0 * 1024.0);
        }

        public double getStdDevMemoryMB() {
            return stdDevMemory / (1024.0 * 1024.0);
        }

        public double getP95MemoryMB() {
            return p95Memory / (1024.0 * 1024.0);
        }
    }
}
