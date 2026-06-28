# Fesod Benchmark Guide

This guide provides a comprehensive overview of the Fesod benchmark module, including how to run benchmarks, interpret the results, and contribute new benchmarks.

## Overview

The benchmark module measures and analyzes the performance of Fesod for various Excel operations, such as reading, writing, and filling data. It uses the [Java Microbenchmark Harness (JMH)](https://openjdk.java.net/projects/code-tools/jmh/) to ensure accurate and reliable benchmark results.

Key goals:

- Provide a standardized way to measure the performance of Fesod.
- Track performance regressions and improvements over time.
- Compare the performance of Fesod with other Excel libraries, such as Apache POI.
- Help users make informed decisions about how to use Fesod for their specific needs.

> **Note:** Benchmark code in this module is not part of the Fesod public API.

## How to Run Benchmarks

### Using the Shade JAR (Recommended)

Build the uber-jar and run benchmarks directly:

```bash
mvn clean package -f fesod-benchmark/pom.xml -DskipTests
java -jar fesod-benchmark/target/benchmarks.jar
```

Run a specific benchmark class:

```bash
java -jar fesod-benchmark/target/benchmarks.jar ReadBenchmark
```

Run with JMH GC profiler for memory analysis:

```bash
java -jar fesod-benchmark/target/benchmarks.jar -prof gc
```

Export results as JSON:

```bash
java -jar fesod-benchmark/target/benchmarks.jar -rf json -rff results.json
```

### Using the Comparison Runner

The `ComparisonBenchmarkRunner` provides a pre-configured Fesod vs Apache POI comparison:

```bash
java -cp fesod-benchmark/target/benchmarks.jar \
  org.apache.fesod.sheet.benchmark.comparison.ComparisonBenchmarkRunner
```

Results are written to `target/benchmark-results/<session-id>/`.

### Using Maven Profiles

```bash
# Run all benchmarks via Maven
mvn verify -f fesod-benchmark/pom.xml -P benchmark -Dbenchmark.pattern=.*

# Run a specific benchmark
mvn verify -f fesod-benchmark/pom.xml -P benchmark -Dbenchmark.pattern=ReadBenchmark

# Quick CI smoke test (1 fork, 1 iteration, 0 warmup)
mvn verify -f fesod-benchmark/pom.xml -P benchmark-test
```

## Benchmark Suites

The benchmark module includes the following suites:

| Suite | Description |
|---|---|
| **Comparison** | Head-to-head comparison of Fesod vs Apache POI for read, write, and streaming operations |
| **Operations** | Focused benchmarks for read (`ReadBenchmark`), write (`WriteBenchmark`), and fill (`FillBenchmark`) operations |

### Dataset Sizes

| Size | Rows | Use Case |
|---|---|---|
| `SMALL` | 1,000 | Quick development feedback |
| `MEDIUM` | 10,000 | Standard CI benchmarks |
| `LARGE` | 100,000 | Performance analysis |
| `EXTRA_LARGE` | 1,000,000 | Stress testing (comparison benchmarks only) |

## JMH Best Practices Applied

This benchmark module follows JMH best practices:

1. **No manual timing** - JMH handles all timing measurements via `@BenchmarkMode`.
2. **No `System.gc()` calls** - Avoids unpredictable pauses that distort measurements.
3. **Fixed heap size** - `-Xms` equals `-Xmx` for stable GC behavior.
4. **Pre-loaded data** - All test data is generated in `@Setup(Level.Trial)` to exclude I/O from measurements.
5. **Fixed random seed** - Ensures reproducible data generation across runs.
6. **Fair comparison** - Both Fesod and Apache POI write/read the same columns.
7. **`Blackhole.consume()`** - Prevents dead code elimination by the JIT compiler.
8. **`@OperationsPerInvocation`** - Allows JMH to correctly calculate throughput.

## Interpreting Results

JMH produces output in the following format:

```
Benchmark                              (datasetSize)  (fileFormat)  Mode  Cnt     Score     Error  Units
FastExcelVsPoiBenchmark.benchmarkFesodRead   SMALL       XLSX       avgt    5     2.345 ±   0.123  ms/op
FastExcelVsPoiBenchmark.benchmarkPoiRead     SMALL       XLSX       avgt    5     5.678 ±   0.456  ms/op
```

Key columns:
- **Mode**: `avgt` (average time), `thrpt` (throughput), `ss` (single shot)
- **Score**: The benchmark score (lower is better for `avgt`, higher for `thrpt`)
- **Error**: 99% confidence interval
- **Units**: `ms/op` (milliseconds per operation), `ops/s` (operations per second)