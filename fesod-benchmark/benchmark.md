# Fesod Benchmark Guide

This guide provides a comprehensive overview of the Fesod benchmark module: the performance regression gate that runs in CI, how to interpret its reports, how to refresh the baseline, and how to run the manual analysis suites.

> **Note:** Benchmark code in this module is not part of the Fesod public API.

## Overview

The benchmark module measures the performance of Fesod for spreadsheet operations (read, write, fill) using the [Java Microbenchmark Harness (JMH)](https://openjdk.java.net/projects/code-tools/jmh/). It serves two purposes:

1. **Performance regression gate (primary)** — a small, fast, stable baseline suite runs on every pull request that touches production code, is compared against a committed baseline, and **fails the CI job** when a regression beyond the configured threshold is detected. Results are posted as a PR comment and in the job summary so maintainers can see the performance impact of a change at a glance.
2. **Manual analysis suites (secondary)** — larger benchmark suites (`ReadBenchmark`, `WriteBenchmark`, `FillBenchmark`, and the Fesod-vs-POI comparison suite) for deep-dive performance work. These are not part of the CI gate.

## Performance Baseline CI

### How it works

```
pull request / push / dispatch
        │
        ▼
┌─────────────────────────────────────────────────────────────┐
│ benchmark job (ubuntu-24.04, JDK 17 Temurin — fixed env)    │
│                                                             │
│  BaselineRunner ──► target/baseline-current.json            │
│        │                              │                     │
│        │                    BaselineComparator              │
│        │                     vs baseline/jmh-baseline.json   │
│        │                              │                     │
│        │                   benchmark-report.md              │
│        ▼                              ▼                     │
│  regression?  ─── no ──► PR comment + step summary + PASS   │
│      │ yes (beyond fail threshold, non-overlapping          │
│      │      JMH error bars)                                 │
│      ▼                                                      │
│  job FAILS, PR comment + step summary show the deltas       │
└─────────────────────────────────────────────────────────────┘
        │  (workflow_dispatch with update_baseline, or first run)
        ▼
┌─────────────────────────────────────────────────────────────┐
│ update-baseline job: commits new jmh-baseline.json + meta,  │
│ opens a PR that a maintainer reviews and merges             │
└─────────────────────────────────────────────────────────────┘
```

Components (package `org.apache.fesod.sheet.benchmark.baseline`):

| Component | Role |
|---|---|
| `BaselineBenchmark` | The tracked performance contract: `write` and `read` operations, XLSX and CSV formats, 1K/10K rows × 20 columns, average time per operation |
| `BaselineRunner` | Runs the suite with a fixed execution contract (2 forks, 3×1s warmup, 5×1s measurement, `-Xms1g -Xmx1g -XX:+UseG1GC`, gc profiler for allocation tracking) and writes JMH JSON |
| `BaselineComparator` | Compares against the committed baseline, renders the Markdown report, returns the CI gate exit code |
| `baseline/jmh-baseline.json` | The committed reference results — the only source of truth for "known good performance" |
| `baseline/baseline-meta.json` | Baseline provenance: source commit, JDK, runner, generation date |

Workflow: [`.github/workflows/benchmark.yml`](../.github/workflows/benchmark.yml)

### Reading the report

Every PR touching `fesod-sheet`, `fesod-common`, `fesod-shaded`, `fesod-benchmark`, or the root POM gets a sticky PR comment (and an entry in the job step summary) that looks like this:

| Benchmark | Mode | Baseline | Current | Δ time | Δ alloc | Verdict |
|---|:-:|---:|---:|---:|---:|:-:|
| `read [datasetSize=MEDIUM, fileFormat=XLSX]` | avgt | 356 ms/op | 402 ms/op | 🔺 +12.9% | +0.4% | :warning: WARN |
| `write [datasetSize=SMALL, fileFormat=CSV]` | avgt | 48.1 ms/op | 47.9 ms/op | −0.4% | +0.1% | :white_check_mark: OK |

Verdicts:

- :white_check_mark: **OK** — within the warn threshold (default ±10%).
- :green_circle: **FASTER** — at least the warn threshold faster than the baseline.
- :warning: **WARN** — slower beyond the warn threshold, *or* beyond the fail threshold but still within JMH statistical error bars (noisy CI runner). Human judgement required; does not block the PR.
- :red_circle: **FAIL** — slower beyond the fail threshold (default 20%) **and** the JMH confidence intervals (score ± scoreError) do not overlap. Blocks the PR.
- :new: **NEW** / :black_circle: **MISSING** — benchmarks absent from the baseline / current run. NEW is informational; MISSING blocks the PR so the tracked contract cannot silently shrink.

Two signals are tracked per benchmark:

- **Δ time** — change in average time per operation (ms/op).
- **Δ alloc** — change in `gc.alloc.rate.norm` (bytes allocated per operation). Allocation is nearly noise-free and often the earliest indicator of a regression, e.g. accidental object churn in a hot loop.

The confidence-interval rule is what keeps CI usable on shared GitHub runners: a 30% "regression" whose error bars overlap the baseline is noise-ambiguous and only warns, while a consistent regression fails regardless of the threshold.

### Configuring thresholds

Defaults: warn at 10%, fail at 20%. They can be changed:

- **Per dispatch run** — inputs of the *Benchmark* workflow (`warn_threshold`, `fail_threshold`).
- **Repository-wide without code changes** — set repository variables `BENCHMARK_WARN_PCT` / `BENCHMARK_FAIL_PCT` (Settings → Secrets and variables → Actions → Variables).

### Updating the baseline

The baseline records absolute numbers and therefore must be generated on the same environment as the CI comparisons: an `ubuntu-24.04` GitHub runner with JDK 17 (Temurin). **Never commit a baseline generated on a local machine** — hardware differences would invalidate every comparison.

To refresh (after an intentional performance change, a new benchmark method, or a JDK/runner upgrade):

1. *Actions → Benchmark → Run workflow* on the target branch, check **update_baseline**, run.
2. The workflow runs the suite, compares against the old baseline (the delta report is embedded in the PR body), then opens a PR updating `baseline/jmh-baseline.json` and `baseline/baseline-meta.json`.
3. A maintainer reviews the embedded delta report and merges.

The very first run on a repository bootstraps automatically: with no baseline present, the run reports every metric as NEW, exit 0, and the update job opens the initial baseline PR.

### Adding a benchmark to the baseline suite

1. Add a `@Benchmark` method (or a `@Param` value) to `BaselineBenchmark`. Keep each addition under ~30s of suite runtime — the gate runs on every PR.
2. The method appears as :new: NEW on the next PR runs (informational only, does not block).
3. Refresh the baseline so the new metric becomes part of the tracked contract.
4. **Do not rename existing benchmark methods or params** — they are the baseline keys; a rename shows up as MISSING + NEW and fails the gate until the baseline is refreshed.

### Reproducing a CI run locally

```bash
mvn clean package -f fesod-benchmark/pom.xml -DskipTests

# same suite / same JVM contract as CI (results land in target/baseline-current.json)
java -cp fesod-benchmark/target/benchmarks.jar \
  org.apache.fesod.sheet.benchmark.baseline.BaselineRunner

# compare your local run against the committed baseline
# (absolute times are NOT comparable across machines — look at alloc/op and rough magnitudes)
java -cp fesod-benchmark/target/benchmarks.jar \
  org.apache.fesod.sheet.benchmark.baseline.BaselineComparator \
  --baseline fesod-benchmark/baseline/jmh-baseline.json \
  --baseline-meta fesod-benchmark/baseline/baseline-meta.json \
  --current fesod-benchmark/target/baseline-current.json \
  --report fesod-benchmark/target/benchmark-report.md

# quick smoke run (1 fork, 1×1s measurement, SMALL datasets only)
java -Dbenchmark.forks=1 -Dbenchmark.warmup.iterations=1 -Dbenchmark.measurement.iterations=1 \
  -Dbenchmark.datasetSizes=SMALL \
  -cp fesod-benchmark/target/benchmarks.jar \
  org.apache.fesod.sheet.benchmark.baseline.BaselineRunner
```

All knobs are system properties on `BaselineRunner` (`benchmark.forks`, `benchmark.warmup.iterations`, `benchmark.warmup.seconds`, `benchmark.measurement.iterations`, `benchmark.measurement.seconds`, `benchmark.datasetSizes`, `benchmark.fileFormats`, `benchmark.result`).

## Running the Analysis Suites

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
# Run all analysis benchmarks via Maven
mvn verify -f fesod-benchmark/pom.xml -P benchmark -Dbenchmark.pattern=.*

# Run a specific analysis benchmark
mvn verify -f fesod-benchmark/pom.xml -P benchmark -Dbenchmark.pattern=ReadBenchmark

# Quick smoke test of the baseline suite (1 fork, 1 iteration, SMALL datasets)
mvn verify -f fesod-benchmark/pom.xml -P benchmark-test
```

## Benchmark Suites

| Suite | Description | CI gate |
|---|---|---|
| **Baseline** | `BaselineBenchmark` — write/read, XLSX/CSV, 1K/10K rows | ✅ runs on every PR, blocks regressions |
| **Comparison** | Head-to-head comparison of Fesod vs Apache POI for read, write, and streaming operations | manual |
| **Operations** | Focused benchmarks for read (`ReadBenchmark`), write (`WriteBenchmark`), and fill (`FillBenchmark`) operations | manual |

### Dataset Sizes

| Size | Rows | Use Case |
|---|---|---|
| `SMALL` | 1,000 | Quick development feedback / baseline suite |
| `MEDIUM` | 10,000 | Standard CI benchmarks / baseline suite |
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

## Interpreting Raw Results

JMH produces output in the following format:

```
Benchmark                              (datasetSize)  (fileFormat)  Mode  Cnt     Score     Error  Units
FastExcelVsPoiBenchmark.benchmarkFesodRead   SMALL       XLSX       avgt    5     2.345 ±   0.123  ms/op
FastExcelVsPoiBenchmark.benchmarkPoiRead     SMALL       XLSX       avgt    5     5.678 ±   0.456  ms/op
```

Key columns:
- **Mode**: `avgt` (average time), `thrpt` (throughput), `ss` (single shot)
- **Score**: The benchmark score (lower is better for `avgt`, higher is better for `thrpt`)
- **Error**: 99.9% confidence interval — the same error bars the baseline comparator uses to downgrade noise-ambiguous regressions
- **Units**: `ms/op` (milliseconds per operation), `ops/s` (operations per second)
