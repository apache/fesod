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
# FastExcel Benchmark Guide

This guide provides a comprehensive overview of the FastExcel benchmark module, including how to run benchmarks, interpret the results, and contribute new benchmarks.

## Overview

The benchmark module is designed to measure and analyze the performance of FastExcel for various Excel operations, such as reading, writing, and filling data. It uses the [Java Microbenchmark Harness (JMH)](https://openjdk.java.net/projects/code-tools/jmh/) to ensure accurate and reliable benchmark results.

The key goals of the benchmark module are:

- To provide a standardized way to measure the performance of FastExcel.
- To track performance regressions and improvements over time.
- To compare the performance of FastExcel with other Excel libraries, such as Apache POI.
- To help users make informed decisions about how to use FastExcel for their specific needs.

## How to Run Benchmarks

There are two primary ways to run the benchmarks: using the `benchmark-runner.sh` script or using Maven profiles.

### Using the `benchmark-runner.sh` Script

The `benchmark-runner.sh` script provides a convenient way to run the benchmarks with various options.

**Usage:**

```bash
./fastexcel-benchmark/scripts/benchmark-runner.sh [OPTIONS]
```

**Options:**

| Option | Description | Default |
|---|---|---|
| `-p`, `--profile` | Benchmark profile (quick, standard, comprehensive) | `standard` |
| `-o`, `--output` | Output directory for results | `benchmark-results` |
| `-j`, `--java-version` | Java version to use | `11` |
| `-m`, `--memory` | JVM heap size | `4g` |
| `-t`, `--pattern` | Benchmark pattern to match | |
| `-d`, `--dataset` | Dataset size (SMALL, MEDIUM, LARGE, EXTRA_LARGE, ALL) | `ALL` |
| `-f`, `--format` | Output format (json, csv, text) | `json` |
| `-r`, `--regression` | Enable regression analysis | |
| `-v`, `--verbose` | Enable verbose output | |
| `-h`, `--help` | Show this help message | |

**Profiles:**

- `quick`: Fast execution for development (2 warmup, 3 measurement, 1 fork).
- `standard`: Balanced execution for CI (3 warmup, 5 measurement, 1 fork).
- `comprehensive`: Thorough execution for nightly (5 warmup, 10 measurement, 2 forks).

**Examples:**

- Run standard benchmarks:
  ```bash
  ./fastexcel-benchmark/scripts/benchmark-runner.sh --profile standard
  ```
- Run quick benchmarks for read operations only:
  ```bash
  ./fastexcel-benchmark/scripts/benchmark-runner.sh --profile quick --pattern "ReadBenchmark"
  ```
- Run comprehensive benchmarks with regression analysis:
  ```bash
  ./fastexcel-benchmark/scripts/benchmark-runner.sh --profile comprehensive --regression
  ```

### Using Maven Profiles

You can also run the benchmarks using Maven profiles. This is useful for integrating the benchmarks into a CI/CD pipeline.

**Usage:**

```bash
mvn clean install -f fastexcel-benchmark/pom.xml -P <profile> -Dbenchmark.pattern=<pattern>
```

**Profiles:**

- `benchmark`: The primary profile for running benchmarks.

**Examples:**

- Run all benchmarks:
  ```bash
  mvn clean install -f fastexcel-benchmark/pom.xml -P benchmark
  ```
- Run a specific benchmark:
  ```bash
  mvn clean install -f fastexcel-benchmark/pom.xml -P benchmark -Dbenchmark.pattern=ReadBenchmark
  ```

## Benchmark Suites

The benchmark module includes the following suites:

- **Comparison:** Benchmarks comparing FastExcel with other libraries (e.g., Apache POI).
- **Config:** Benchmarks related to configuration options.
- **Core:** Core benchmark classes and utilities.
- **Data:** Benchmarks related to data handling and processing.
- **Memory:** Benchmarks focused on memory usage.
- **Operations:** Benchmarks for specific operations like read, write, and fill.
- **Streaming:** Benchmarks for streaming operations.

## Interpreting Results

The benchmarks produce output in the format specified by the `--format` option. The default format is JSON.

The output includes the following information:

- **Benchmark:** The name of the benchmark.
- **Mode:** The benchmark mode (e.g., `thrpt` for throughput, `avgt` for average time).
- **Threads:** The number of threads used.
- **Forks:** The number of forks used.
- **Warmup Iterations:** The number of warmup iterations.
- **Measurement Iterations:** The number of measurement iterations.
- **Score:** The benchmark score.
- **Score Error:** The error of the benchmark score.
- **Unit:** The unit of the benchmark score (e.g., `ops/s` for operations per second).