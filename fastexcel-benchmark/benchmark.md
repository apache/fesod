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