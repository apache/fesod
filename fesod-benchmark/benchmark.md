# Fesod Benchmark Guide

This guide provides a comprehensive overview of the Fesod benchmark module: the performance regression gate that runs in CI, how to interpret its reports, how to refresh the baseline, and how to run the manual analysis suites.

> **Note:** Benchmark code in this module is not part of the Fesod public API.

## Overview

The benchmark module measures the performance of Fesod for spreadsheet operations (read, write, fill) using the [Java Microbenchmark Harness (JMH)](https://openjdk.java.net/projects/code-tools/jmh/). It serves two purposes:

1. **Performance regression gate (primary)** — a small, stable baseline suite runs on **release tags** and **on demand** (`workflow_dispatch`), is compared against a committed baseline, and **fails the CI job** when a high-confidence regression is detected (see [the tiered gate](#the-tiered-gate) below). Reports are appended to the job summary, regressions surface as `::error` annotations on the run page, and a regression on a release tag additionally posts the report on the matching GitHub Release (or opens an issue) so maintainers cannot miss it.
2. **Manual analysis suites (secondary)** — larger benchmark suites (`ReadBenchmark`, `WriteBenchmark`, `FillBenchmark`, and the Fesod-vs-POI comparison suite) for deep-dive performance work. These are not part of the CI gate.

## Performance Baseline CI

### When the gate runs

| Trigger | What happens |
|---|---|
| `push` of a release tag (`[0-9]+.*`, e.g. `2.1.0-incubating`) | Full suite + comparison. Regression → job fails and the report is posted on the GitHub Release of that tag (or an issue is opened). Pass → a baseline refresh PR is opened automatically, so the baseline lifecycle follows releases. |
| `workflow_dispatch` (manual) | Same suite + comparison on any branch — e.g. while developing a performance-sensitive change (if the branch has an open PR, the report is also commented there). Check **update_baseline** to accept a performance change and open a baseline refresh PR. |

The gate intentionally does **not** run per pull request: GitHub shared runners fluctuate too much for every-push gating to be worth the CI time. The tag is the release-time checkpoint; manual runs cover anything in between.

### Alignment with Fesod's low-memory positioning

Fesod's core promise is *streaming through large spreadsheets without OOM*, so the baseline suite is shaped to guard exactly that:

- **LARGE (100K rows × 20 columns) is tracked**, not just small dev-feedback sizes: at 1K/10K rows even a non-streaming implementation looks fine, and a regression that accidentally buffers a whole sheet would go unnoticed. At 100K rows such a mistake moves allocation per op by orders of magnitude and is caught immediately by the (deterministic) alloc/op signal.
- **Allocation per op is a first-class tracked metric.** It is the most direct quantitative expression of the memory-efficiency claim: streaming keeps it linear in rows and independent of file size. Its measured noise is ±0.1%, so it is the signal the hard gate trusts most.
- **EXTRA_LARGE (1M rows) is intentionally excluded** from the CI baseline: a single 1M-row XLSX write takes tens of seconds per op, which does not fit a CI budget. It remains available in the manual analysis suites (`WriteBenchmark`/`ReadBenchmark` with `-p datasetSize=EXTRA_LARGE`).
- **The forked JVM uses a fixed `-Xms1g -Xmx1g` heap** — deliberately generous and constant. This is a *measurement* choice (a stable, roomy heap removes GC-adaptivity noise from the timing signal), not a simulation of a memory-constrained user. The low-memory capability itself is guarded by alloc/op linearity above and by the existing unit/integration tests in `fesod-sheet`, not by squeezing the benchmark JVM.
- Time-signal precision note: a LARGE XLSX write takes ~2.5s/op, so each 2s measurement iteration yields ~1 sample — LARGE time deltas come with wide JMH error bars and usually resolve as WARN rather than FAIL; regressions at this size are still caught reliably through the alloc signal and by the magnitude of the delta.

### The tiered gate

Whether a regression should fail CI was evaluated against measured noise on real `ubuntu-24.04` runners (same code, consecutive runs):

| Signal | Measured noise | Decision |
|---|---|---|
| `gc.alloc.rate.norm` (bytes allocated per op) | **±0.1%** — deterministic, unaffected by CPU contention | Regression beyond threshold → **hard fail**. Near-zero false-positive rate. |
| Average time per op, beyond fail threshold **and** JMH confidence intervals (score ± scoreError) **do not overlap** | ±7% typical, ±15%+ tail | **Hard fail** — high confidence that it is a real regression, not runner noise. |
| Average time per op beyond threshold **but** intervals overlap | — | **WARN only** — noise-ambiguous; failing here would make CI flaky and train people to ignore red gates. |
| Tracked benchmark missing from a run | — | **Hard fail** — the tracked contract cannot silently shrink. |

If a run's execution contract (forks/iterations) differs from the baseline's, the report carries a prominent warning — deltas across contracts are indicative only.

### How it works

```
release tag / manual dispatch
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
│        │                   + ::error/::warning annotations  │
│        ▼                              ▼                     │
│  regression?  ─── no ──► step summary + (PR comment) + PASS │
│      │ yes (tiered gate: alloc regression, or time beyond   │
│      │      threshold with non-overlapping JMH error bars)  │
│      ▼                                                      │
│  job FAILS, report + annotations show the deltas            │
└─────────────────────────────────────────────────────────────┘
        │                    │
        │ tag + regression   │ tag + pass, or dispatch with
        ▼                    ▼ update_baseline, or no baseline
┌───────────────────────┐  ┌──────────────────────────────────┐
│ notify-regression job │  │ update-baseline job:             │
│ report comment on the │  │ commits new jmh-baseline.json +  │
│ GitHub Release (or    │  │ meta, opens a PR a maintainer    │
│ opens an issue)       │  │ reviews and merges               │
└───────────────────────┘  └──────────────────────────────────┘
```

Components (package `org.apache.fesod.sheet.benchmark.baseline`):

| Component | Role |
|---|---|
| `BaselineBenchmark` | The tracked performance contract: `write` and `read` operations, XLSX and CSV formats, 1K/10K/100K rows × 20 columns, average time and allocation per operation |
| `BaselineRunner` | Runs the suite with a fixed execution contract (3 forks, 3×1s warmup, 5×2s measurement, `-Xms1g -Xmx1g -XX:+UseG1GC`, gc profiler for allocation tracking) and writes JMH JSON |
| `BaselineComparator` | Compares against the committed baseline, renders the Markdown report, returns the CI gate exit code |
| `baseline/jmh-baseline.json` | The committed reference results — the only source of truth for "known good performance" |
| `baseline/baseline-meta.json` | Baseline provenance: source commit, JDK, runner, generation date |

Workflow: [`.github/workflows/benchmark.yml`](../.github/workflows/benchmark.yml)

### Reading the report

Every run appends a report to the job step summary; a manual run on a branch with an open PR additionally updates a sticky PR comment. The report looks like this:

| Benchmark | Mode | Baseline | Current | Δ time | Alloc/op (Δ) | Verdict |
|---|:-:|---:|---:|---:|---:|:-:|
| `read [datasetSize=MEDIUM, fileFormat=XLSX]` | avgt | 356 ms/op | 402 ms/op | 🔺 +12.9% | 1.9 GB (+0.4%) | :warning: WARN |
| `write [datasetSize=SMALL, fileFormat=CSV]` | avgt | 48.1 ms/op | 47.9 ms/op | −0.4% | 17.8 MB (+0.1%) | :white_check_mark: OK |
| `write [datasetSize=LARGE, fileFormat=XLSX]` | avgt | 2.45 s/op | 2.51 s/op | +2.4% | 2.1 GB (+0.2%) | :white_check_mark: OK |

Verdicts:

- :white_check_mark: **OK** — within the warn threshold (default ±10%).
- :green_circle: **FASTER** — at least the warn threshold faster than the baseline.
- :warning: **WARN** — slower beyond the warn threshold, *or* beyond the fail threshold but still within JMH statistical error bars (noisy CI runner). Human judgement required; does not block the PR.
- :red_circle: **FAIL** — slower beyond the fail threshold (default 20%) **and** the JMH confidence intervals (score ± scoreError) do not overlap. Blocks the PR.
- :new: **NEW** / :black_circle: **MISSING** — benchmarks absent from the baseline / current run. NEW is informational; MISSING blocks the PR so the tracked contract cannot silently shrink.

Two signals are tracked per benchmark:

- **Δ time** — change in average time per operation (ms/op).
- **Alloc/op (Δ)** — bytes allocated per operation (`gc.alloc.rate.norm`) and its change. This is the most direct signal for the library's *low-memory* positioning: streaming implementations keep allocation linear in rows and independent of file size, so an absolute value that suddenly jumps (e.g. 208 MB → 2 GB for the same 10K-row write) exposes accidental full-buffering immediately. Allocation is also nearly noise-free on CI runners (±0.1%), making it the trusted hard gate.

The confidence-interval rule is what keeps CI usable on shared GitHub runners: a 30% "regression" whose error bars overlap the baseline is noise-ambiguous and only warns, while a consistent regression fails regardless of the threshold.

### Configuring thresholds

Defaults: warn at 10%, fail at 20%. They can be changed:

- **Per dispatch run** — inputs of the *Benchmark* workflow (`warn_threshold`, `fail_threshold`).
- **Repository-wide without code changes** — set repository variables `BENCHMARK_WARN_PCT` / `BENCHMARK_FAIL_PCT` (Settings → Secrets and variables → Actions → Variables).

### Updating the baseline

The baseline records absolute numbers and therefore must be generated on the same environment as the CI comparisons: an `ubuntu-24.04` GitHub runner with JDK 17 (Temurin). **Never commit a baseline generated on a local machine** — hardware differences would invalidate every comparison.

The baseline lifecycle follows releases:

- **Release tag passes the gate** → the update job automatically opens a baseline refresh PR from that tag (a maintainer merges it; the baseline advances to the release).
- **Release tag regresses** → the baseline is *not* advanced; the notify job posts the regression report on the tag's GitHub Release (or opens an issue). Fix the regression and re-tag, or deliberately accept it via a manual refresh below.
- **Manual refresh** (after an intentionally accepted performance change, a new benchmark method, or a JDK/runner upgrade): *Actions → Benchmark → Run workflow* on the target branch with **update_baseline** checked. The workflow runs the suite, embeds the old-vs-new delta report in the PR body, and opens a PR updating `baseline/jmh-baseline.json` and `baseline/baseline-meta.json` for maintainer review.
- **First run on a repository** bootstraps automatically: with no baseline present, the run reports every metric as NEW, exit 0, and the update job opens the initial baseline PR.

### Adding a benchmark to the baseline suite

1. Add a `@Benchmark` method (or a `@Param` value) to `BaselineBenchmark`. Keep each addition under ~30s of suite runtime — the gate runs on release tags and manual dispatch.
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

### ASF compliance notes

The workflow follows the [Apache GitHub Actions policy](https://infra.apache.org/github-actions-policy.html):

- Only `actions/*` actions are used (no SHA pinning required for that namespace); job concurrency is 3; no `pull_request_target` with secrets.
- The `update-baseline` job pushes only to a fresh `benchmark/baseline-*` branch and opens a PR — it never pushes to `main`, which is protected (squash-only, 1 review). Because merges are squash merges, the commit that actually lands on `main` is authored by the merging committer, and the bot-produced branch is discarded — the same pattern Dependabot already uses in Apache repositories.
- The generated baseline JSON and metadata are convenience data, not release artifacts; they only enter the repository through maintainer-reviewed PRs.

**If the organization's GitHub settings make the default token read-only or disallow Actions-created PRs**, the `update-baseline` job will fail at the push/PR step. The fallback is built in: the `benchmark-results` artifact of the run already contains `baseline-current.json` — download it from the run page, place it at `fesod-benchmark/baseline/jmh-baseline.json` (regenerate `baseline-meta.json` from the run's summary data), and commit it as a normal reviewed change.

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
| **Baseline** | `BaselineBenchmark` — write/read, XLSX/CSV, 1K/10K/100K rows | ✅ release tags + manual dispatch, blocks high-confidence regressions |
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
