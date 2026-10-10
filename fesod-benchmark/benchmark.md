# Fesod Benchmark Module

JMH benchmarks for Fesod spreadsheet operations, plus (per this proposal) a
release-time performance regression gate. Benchmark code is not part of the
Fesod public API.

It covers three complementary suites (package `org.apache.fesod.sheet.benchmark`):

| Suite | What it measures |
|---|---|
| `FesodBenchmark` | The core hot paths — **read** and **write**, XLSX and CSV, 1K/10K/100K rows × 20 columns, average time per op |
| `FesodVsPoiBenchmark` | Plain-API comparison with **Apache POI** on the same data: write (`ExcelWriter` vs XSSF/HSSF `Workbook`) and read (Fesod streaming reader vs POI's DOM-based `WorkbookFactory`); XLSX and XLS (XLS truncated to 65,534 data rows) |
| `StreamingBenchmark` | Streaming-API comparison for the **large-file** scenario, XLSX only: **read** — Fesod streaming reader vs POI **SAX event model** (`XSSFReader` + `XSSFSheetXMLHandler`); **write** — Fesod batched `ExcelWriter` vs POI **SXSSF** sliding-window workbook; 1K/10K/100K rows |

Shared support code: `BenchmarkConfiguration` (dataset sizes, file formats),
`data.BenchmarkData` (the 20-column data model), `util.DataGenerator`
(fixed-seed, date-anchored — same seed always produces the same rows) and
`util.BenchmarkFileUtil` (scratch files live under `target/benchmark-testdata`,
which `mvn clean` removes).

## Building and running

Build the shaded benchmark jar, then use the standard JMH launcher:

```bash
./mvnw -B -ntp -pl fesod-benchmark -am package -DskipTests

# Whole core suite (all sizes × formats)
java -jar fesod-benchmark/target/benchmarks.jar FesodBenchmark

# One slice, e.g. LARGE XLSX reads only
java -jar fesod-benchmark/target/benchmarks.jar 'FesodBenchmark\.read$' \
    -p datasetSize=LARGE -p fileFormat=XLSX

# Fesod vs POI, XLSX only
java -jar fesod-benchmark/target/benchmarks.jar FesodVsPoiBenchmark -p fileFormat=XLSX

# Streaming read for the large-file scenario: Fesod vs POI SAX event model
java -jar fesod-benchmark/target/benchmarks.jar StreamingBenchmark

# Add allocation-per-op (bytes/op) — the most direct memory-efficiency signal
java -jar fesod-benchmark/target/benchmarks.jar FesodBenchmark -prof gc
```

Maven shortcuts:

```bash
# Full run of a suite via the exec plugin
./mvnw -B -ntp -pl fesod-benchmark verify -Pbenchmark '-Dbenchmark.pattern=FesodBenchmark'

# Quick smoke run (SMALL/CSV, 1 fork, minimal iterations) — sanity check after changes
./mvnw -B -ntp -pl fesod-benchmark verify -Pbenchmark-test
```

## Execution contracts

The annotation contracts are part of each suite and should be kept stable so that
results collected at different times stay comparable:

| Suite | Mode | Forks | Warmup | Measurement | JVM args |
|---|---|---|---|---|---|
| `FesodBenchmark` | avgt, ms/op | 3 | 3 × 1s | 5 × 2s | `-Xms1g -Xmx1g -XX:+UseG1GC` |
| `FesodVsPoiBenchmark` | avgt, ms/op | 1 | 3 × 5s | 5 × 5s | `-Xms2g -Xmx2g` |
| `StreamingBenchmark` | avgt, ms/op | 1 | 3 × 5s | 5 × 5s | `-Xms2g -Xmx2g` |

## Notes on interpreting results

- **Environment matters.** Time measurements fluctuate with the host (CPU,
  contention, disk). Compare runs from the same machine and similar load, and
  trust JMH's error bars (`score ± error`): overlapping intervals between two
  runs mean "no observable difference".
- **`-prof gc` allocation per op is the most stable signal** (typically ±0.1%
  run-to-run on the same machine). For Fesod's *streaming without OOM* promise,
  allocation per op should stay linear in the number of rows and independent of
  file size — an accidental full-file buffering shows up here as an
  orders-of-magnitude jump long before timing changes.
- **LARGE (100K rows) is where streaming behavior is actually exercised.** At
  1K/10K rows even a non-streaming implementation looks fine; memory
  characteristics only separate at larger sizes.
- **Read-path workload symmetry (vs-POI suites).** The write comparisons are
  symmetric — both libraries convert the same objects into the same 20 columns.
  The read comparisons are deliberately asymmetric, mirroring each library's
  idiomatic end-to-end usage: Fesod runs its full pipeline (parse + type
  conversion such as `BigDecimal`/dates + reflective row-object mapping into
  `BenchmarkData`), while the POI side consumes raw cell text — POI has no
  built-in row-object mapping, and hand-rolling one in the benchmark would
  measure benchmark code rather than either library. The asymmetry is
  conservative for Fesod: the POI side does strictly less work per row, so the
  comparison cannot overstate Fesod's advantage.
- **Data is deterministic**: fixed seed and a fixed date anchor
  (`2024-01-01`) mean any difference between two runs comes from the code or
  the environment, not from the data.

## Extending

- New benchmark methods are added like any JMH benchmark (`@Benchmark` method +
  `@Param`s). Keep the class-level contract annotations unchanged when adding
  to an existing suite; create a new suite class if a different contract is
  needed.
- If a benchmark writes files, write them through `BenchmarkFileUtil` so they
  land in `target/benchmark-testdata`. Write benchmarks target a fixed file
  (overwritten each invocation) and delete it from the trial `@TearDown` via
  `BenchmarkFileUtil.delete` — never inside the measured method, so cleanup
  I/O stays out of the reported time.

## Performance regression gate (this PR's proposal)

On top of the manual suites above, this PR wires `FesodBenchmark` into a
**release-time regression gate**:

- **Triggers**: release tags (`[0-9]+.*`) and manual `workflow_dispatch` — never
  per pull request (shared-runner noise makes every-push gating flaky and
  wasteful). Workflow: [`.github/workflows/benchmark.yml`](../.github/workflows/benchmark.yml).
- **Components** (package `org.apache.fesod.sheet.benchmark.baseline`):
  `BaselineRunner` runs `FesodBenchmark` with the pinned contract (3 forks,
  3×1s warmup, 5×2s measurement, `-Xms1g -Xmx1g -XX:+UseG1GC`, gc profiler);
  `BaselineComparator` compares against the committed baseline
  ([`baseline/`](baseline/)) and renders the Markdown report with the gate verdict.
- **Tiered verdicts** (calibrated against measured noise on real runners):

  | Signal | Noise | Decision |
  |---|---|---|
  | `gc.alloc.rate.norm` (alloc per op) | ±0.1% | Regression beyond threshold → **fail** |
  | avgt beyond fail threshold **and** non-overlapping JMH error bars | ±7% typical | **Fail** |
  | avgt beyond threshold with overlapping error bars | — | **WARN only** |
  | Tracked benchmark missing from a run | — | **Fail** |

  Thresholds default to warn 10% / fail 20% (overridable via dispatch inputs or
  repo variables `BENCHMARK_WARN_PCT` / `BENCHMARK_FAIL_PCT`).
- **Baseline lifecycle**: the baseline is only ever generated on
  `ubuntu-24.04` + JDK 17 Temurin GitHub runners; the first run bootstraps it
  via an automated PR, passing tags advance it via automated PRs, and a
  regression on a tag posts the report on the matching GitHub Release (or
  opens an issue). Benchmark method/param names are the baseline keys — do not
  rename them without a baseline refresh.
- **ASF compliance**: only `actions/*` are used; bot pushes go only to
  `benchmark/baseline-*` branches; baseline JSON enters the repo exclusively
  through reviewed PRs.

This gate is the part of the original PR #575 that was split out per review
feedback; it is proposed here separately for discussion.
