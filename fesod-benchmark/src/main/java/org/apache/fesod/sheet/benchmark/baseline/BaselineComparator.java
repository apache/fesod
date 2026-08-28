/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.fesod.sheet.benchmark.baseline;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Compares a fresh {@link BaselineRunner} result file against the committed baseline and
 * renders a Markdown report for humans (PR comment / job summary), plus a process exit
 * code used as the CI gate.
 *
 * <p>Comparison rules:
 * <ul>
 *   <li>Direction aware: for {@code avgt}/{@code ss} lower is better, for {@code thrpt}
 *       higher is better.</li>
 *   <li>Two signals per benchmark: primary metric (average time) and
 *       {@code gc.alloc.rate.norm} (bytes allocated per op).</li>
 *   <li>Noise aware: a regression is only a hard failure when it exceeds the fail
 *       threshold <em>and</em> the JMH confidence intervals (score &plusmn; scoreError)
 *       do not overlap; an overlapping regression is downgraded to a warning, which keeps
 *       CI stable on shared runners.</li>
 *   <li>A benchmark that exists in the baseline but not in the current run fails the gate
 *       (unless {@code --allow-missing}) so the tracked contract cannot silently shrink.</li>
 * </ul>
 *
 * <p>Exit codes: {@code 0} = pass (or bootstrap mode without baseline),
 * {@code 1} = regression gate failed, {@code 2} = usage or I/O error.
 */
public final class BaselineComparator {

    private static final String GC_ALLOC_RATE_NORM = "gc.alloc.rate.norm";

    private static final String DEFAULT_BASELINE = "fesod-benchmark/baseline/jmh-baseline.json";
    private static final String DEFAULT_BASELINE_META = "fesod-benchmark/baseline/baseline-meta.json";
    private static final String DEFAULT_CURRENT = "target/baseline-current.json";

    private static final String OK = ":white_check_mark: OK";
    private static final String IMPROVED = ":green_circle: FASTER";
    private static final String WARN = ":warning: WARN";
    private static final String FAIL = ":red_circle: FAIL";
    private static final String NEW = ":new: NEW";
    private static final String MISSING = ":black_circle: MISSING";

    private static final String UP = ":small_red_triangle:";
    private static final String DOWN = ":small_red_triangle_down:";

    private enum Verdict {
        OK(0),
        IMPROVED(1),
        WARN(2),
        FAIL(3),
        NEW(0),
        MISSING(3);

        private final int severity;

        Verdict(int severity) {
            this.severity = severity;
        }
    }

    /** One JMH result entry: primary metric plus the gc allocation metric when present. */
    private static final class Metric {
        final String key;
        final String displayName;
        final String mode;
        final double score;
        final double scoreError;
        final String scoreUnit;
        final Double allocScore;
        final double allocError;
        final String allocUnit;

        Metric(
                String key,
                String displayName,
                String mode,
                double score,
                double scoreError,
                String scoreUnit,
                Double allocScore,
                double allocError,
                String allocUnit) {
            this.key = key;
            this.displayName = displayName;
            this.mode = mode;
            this.score = score;
            this.scoreError = scoreError;
            this.scoreUnit = scoreUnit;
            this.allocScore = allocScore;
            this.allocError = allocError;
            this.allocUnit = allocUnit;
        }
    }

    /** One report row: baseline/current metric pair plus the computed verdict. */
    private static final class Row {
        final Metric baseline;
        final Metric current;
        Verdict verdict;
        String timeDetail;
        String allocDetail;

        Row(Metric baseline, Metric current) {
            this.baseline = baseline;
            this.current = current;
        }

        Metric currentOrBaseline() {
            return current != null ? current : baseline;
        }
    }

    private final double warnPct;
    private final double failPct;
    private final boolean allowMissing;

    private BaselineComparator(double warnPct, double failPct, boolean allowMissing) {
        this.warnPct = warnPct;
        this.failPct = failPct;
        this.allowMissing = allowMissing;
    }

    public static void main(String[] args) {
        String baselinePath = DEFAULT_BASELINE;
        String baselineMetaPath = DEFAULT_BASELINE_META;
        String currentPath = DEFAULT_CURRENT;
        String reportPath = null;
        double warnPct = 10.0d;
        double failPct = 20.0d;
        boolean allowMissing = false;

        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if ("--baseline".equals(arg) && i + 1 < args.length) {
                baselinePath = args[++i];
            } else if ("--baseline-meta".equals(arg) && i + 1 < args.length) {
                baselineMetaPath = args[++i];
            } else if ("--current".equals(arg) && i + 1 < args.length) {
                currentPath = args[++i];
            } else if ("--report".equals(arg) && i + 1 < args.length) {
                reportPath = args[++i];
            } else if ("--warn".equals(arg) && i + 1 < args.length) {
                warnPct = Double.parseDouble(args[++i]);
            } else if ("--fail".equals(arg) && i + 1 < args.length) {
                failPct = Double.parseDouble(args[++i]);
            } else if ("--allow-missing".equals(arg)) {
                allowMissing = true;
            } else {
                usageAndExit("Unknown or incomplete argument: " + arg);
            }
        }
        if (failPct <= warnPct) {
            usageAndExit("--fail threshold must be greater than --warn threshold");
        }

        try {
            File currentFile = new File(currentPath);
            if (!currentFile.isFile()) {
                System.err.println("ERROR: current result file not found: " + currentPath);
                System.exit(2);
            }
            Map<String, Metric> current = index(readResults(currentFile));

            File baselineFile = new File(baselinePath);
            Map<String, Metric> baseline =
                    baselineFile.isFile() ? index(readResults(baselineFile)) : new TreeMap<String, Metric>();

            BaselineComparator comparator = new BaselineComparator(warnPct, failPct, allowMissing);
            List<Row> rows = comparator.compare(baseline, current);
            String report = comparator.renderReport(rows, baseline.isEmpty(), readMeta(new File(baselineMetaPath)));

            System.out.println();
            System.out.println(report);

            if (reportPath != null) {
                File reportFile = new File(reportPath);
                if (reportFile.getParentFile() != null
                        && !reportFile.getParentFile().exists()) {
                    reportFile.getParentFile().mkdirs();
                }
                Files.write(reportFile.toPath(), report.getBytes(StandardCharsets.UTF_8));
                System.out.println("Report written to: " + reportPath);
                System.out.println();
            }

            boolean gateFailed = false;
            for (Row row : rows) {
                if (row.verdict == Verdict.FAIL || (row.verdict == Verdict.MISSING && !allowMissing)) {
                    gateFailed = true;
                    System.err.println("REGRESSION: " + row.verdict + " " + row.currentOrBaseline().displayName
                            + " — time: " + row.timeDetail + ", alloc: " + row.allocDetail);
                }
            }

            System.exit(gateFailed ? 1 : 0);
        } catch (IOException e) {
            System.err.println("ERROR: " + e.getMessage());
            System.exit(2);
        }
    }

    // ------------------------------------------------------------------
    // Comparison
    // ------------------------------------------------------------------

    private List<Row> compare(Map<String, Metric> baseline, Map<String, Metric> current) {
        List<Row> rows = new ArrayList<Row>();
        TreeSet<String> keys = new TreeSet<String>();
        keys.addAll(baseline.keySet());
        keys.addAll(current.keySet());

        for (String key : keys) {
            Metric base = baseline.get(key);
            Metric cur = current.get(key);
            Row row = new Row(base, cur);

            if (base == null) {
                row.verdict = Verdict.NEW;
                row.timeDetail = "not in baseline";
                row.allocDetail = "n/a";
            } else if (cur == null) {
                row.verdict = Verdict.MISSING;
                row.timeDetail = "missing from current run";
                row.allocDetail = "n/a";
            } else {
                row.verdict = worst(metricVerdict(base, cur), allocVerdict(base, cur));
                row.timeDetail = timeDetail(base, cur);
                row.allocDetail = allocDetail(base, cur);
            }
            rows.add(row);
        }
        return rows;
    }

    private Verdict metricVerdict(Metric base, Metric cur) {
        boolean higherIsWorse = higherIsWorse(cur.mode);
        return verdict(base.score, base.scoreError, cur.score, cur.scoreError, higherIsWorse);
    }

    private Verdict allocVerdict(Metric base, Metric cur) {
        if (base.allocScore == null || cur.allocScore == null || base.allocScore <= 0 || cur.allocScore <= 0) {
            return Verdict.OK;
        }
        return verdict(base.allocScore, base.allocError, cur.allocScore, cur.allocError, true);
    }

    private Verdict verdict(double base, double baseErr, double cur, double curErr, boolean higherIsWorse) {
        double regressionPct = higherIsWorse ? (cur - base) / base * 100.0d : (base - cur) / base * 100.0d;
        boolean intervalsOverlap =
                higherIsWorse ? (cur - curErr) <= (base + baseErr) : (cur + curErr) >= (base - baseErr);

        if (regressionPct >= failPct && !intervalsOverlap) {
            return Verdict.FAIL;
        }
        if (regressionPct >= failPct || regressionPct >= warnPct) {
            return Verdict.WARN;
        }
        if (regressionPct <= -warnPct) {
            return Verdict.IMPROVED;
        }
        return Verdict.OK;
    }

    private String timeDetail(Metric base, Metric cur) {
        return changeDetail(cur.score - base.score, base.score);
    }

    private String allocDetail(Metric base, Metric cur) {
        if (base.allocScore == null || cur.allocScore == null || base.allocScore <= 0) {
            return "n/a";
        }
        return changeDetail(cur.allocScore - base.allocScore, base.allocScore);
    }

    private static String changeDetail(double delta, double base) {
        double pct = delta / base * 100.0d;
        String icon = "";
        if (pct > 0.05d) {
            icon = UP + " ";
        } else if (pct < -0.05d) {
            icon = DOWN + " ";
        }
        return String.format(Locale.ROOT, "%s%+.1f%%", icon, pct);
    }

    private static boolean higherIsWorse(String mode) {
        // thrpt: higher score = better; everything else JMH emits (avgt, ss, sampled) is time-like
        return !"thrpt".equals(mode);
    }

    private static Verdict worst(Verdict a, Verdict b) {
        return a.severity >= b.severity ? a : b;
    }

    // ------------------------------------------------------------------
    // Report rendering
    // ------------------------------------------------------------------

    private String renderReport(List<Row> rows, boolean bootstrap, JSONObject meta) {
        StringBuilder sb = new StringBuilder();

        if (bootstrap) {
            sb.append("## :bar_chart: Performance run — baseline not yet established\n\n");
            sb.append("No committed baseline found (or it is unreadable), so no comparison was performed.\n");
            sb.append("The CI update job will open a pull request that records this run as the initial baseline.\n");
            sb.append("Every value below becomes a tracked metric once that PR is merged.\n\n");
        } else {
            sb.append("## :bar_chart: Performance vs baseline\n\n");
            sb.append(baselineHeader(meta));
            sb.append("\n\n");
        }

        sb.append("| Benchmark | Mode | Baseline | Current | ");
        sb.append(bootstrap ? "Δ | Verdict |\n" : "Δ time | Δ alloc | Verdict |\n");
        sb.append(bootstrap ? "|---|:-:|---:|---:|---:|:-:|\n" : "|---|:-:|---:|---:|---:|---:|:-:|\n");

        for (Row row : rows) {
            Metric shown = row.current != null ? row.current : row.baseline;
            sb.append("| `")
                    .append(shown.displayName)
                    .append("` | ")
                    .append(shown.mode)
                    .append(" | ")
                    .append(row.baseline == null ? "—" : formatScore(row.baseline.score, row.baseline.scoreUnit))
                    .append(" | ")
                    .append(row.current == null ? "—" : formatScore(row.current.score, row.current.scoreUnit))
                    .append(" | ");
            if (bootstrap) {
                sb.append(row.current == null ? "—" : "new");
            } else {
                sb.append(row.timeDetail).append(" | ").append(row.allocDetail);
            }
            sb.append(" | ").append(verdictLabel(row)).append(" |\n");
        }

        sb.append("\n").append(summaryLine(rows, bootstrap)).append("\n");
        sb.append(legend());
        return sb.toString();
    }

    private String baselineHeader(JSONObject meta) {
        StringBuilder sb = new StringBuilder();
        sb.append("Baseline: ");
        if (meta == null) {
            sb.append("`").append(DEFAULT_BASELINE).append("` (no metadata)");
        } else {
            String sha = meta.getString("gitSha");
            sb.append("`")
                    .append(sha == null ? "unknown" : sha.substring(0, Math.min(7, sha.length())))
                    .append("`");
            String generatedAt = meta.getString("generatedAt");
            if (generatedAt != null) {
                sb.append(" · recorded ").append(generatedAt);
            }
            String jdk = meta.getString("jdkVersion");
            if (jdk != null) {
                sb.append(" · ").append(jdk);
            }
            String runner = meta.getString("runnerLabel");
            if (runner != null) {
                sb.append(" · ").append(runner);
            }
        }
        sb.append(String.format(Locale.ROOT, " · thresholds: warn ≥ %.0f%% / fail ≥ %.0f%%", warnPct, failPct));
        return sb.toString();
    }

    private String summaryLine(List<Row> rows, boolean bootstrap) {
        int ok = 0;
        int improved = 0;
        int warn = 0;
        int fail = 0;
        int missing = 0;
        int newCount = 0;
        for (Row row : rows) {
            switch (row.verdict) {
                case OK:
                    ok++;
                    break;
                case IMPROVED:
                    improved++;
                    break;
                case WARN:
                    warn++;
                    break;
                case FAIL:
                    fail++;
                    break;
                case MISSING:
                    missing++;
                    break;
                case NEW:
                    newCount++;
                    break;
                default:
                    break;
            }
        }

        StringBuilder sb = new StringBuilder("**Summary**: ");
        if (bootstrap) {
            sb.append(newCount).append(" metric(s) recorded, none compared yet");
            return sb.toString();
        }
        sb.append(ok)
                .append(" ")
                .append(OK)
                .append(" · ")
                .append(improved)
                .append(" ")
                .append(IMPROVED)
                .append(" · ")
                .append(warn)
                .append(" ")
                .append(WARN)
                .append(" · ")
                .append(fail)
                .append(" ")
                .append(FAIL);
        if (missing > 0) {
            sb.append(" · ").append(missing).append(" ").append(MISSING);
        }
        if (newCount > 0) {
            sb.append(" · ").append(newCount).append(" ").append(NEW);
        }

        boolean gateFailed = fail > 0 || (missing > 0 && !allowMissing);
        sb.append(" — gate: **").append(gateFailed ? "FAILED" : "PASSED").append("**");
        if (gateFailed) {
            sb.append(" (regression beyond fail threshold or missing benchmark)");
        }
        return sb.toString();
    }

    private String legend() {
        return "\n<details>\n<summary>Verdict legend & notes</summary>\n\n"
                + "- "
                + OK
                + ": within the warn threshold; "
                + IMPROVED
                + ": at least the warn threshold faster.\n"
                + "- "
                + WARN
                + ": slower beyond the warn threshold, or beyond the fail threshold but still within JMH "
                + "statistical error bars (noisy CI runner) — human judgement required.\n"
                + "- "
                + FAIL
                + ": slower beyond the fail threshold with non-overlapping error bars — blocks the pull request.\n"
                + "- "
                + NEW
                + ": not tracked by the baseline yet; becomes tracked at the next baseline refresh. "
                + MISSING
                + ": tracked benchmark absent from the current run.\n"
                + "- Time is the JMH average per operation; Δ alloc compares `gc.alloc.rate.norm` "
                + "(bytes allocated per operation) — a very stable regression signal.\n"
                + "- To deliberately accept a performance change, refresh the baseline: "
                + "*Actions → Benchmark → Run workflow* with **update_baseline** checked "
                + "(see `fesod-benchmark/benchmark.md`).\n"
                + "\n</details>\n";
    }

    private static String verdictLabel(Row row) {
        switch (row.verdict) {
            case IMPROVED:
                return IMPROVED;
            case WARN:
                return WARN;
            case FAIL:
                return FAIL;
            case NEW:
                return NEW;
            case MISSING:
                return MISSING;
            default:
                return OK;
        }
    }

    // ------------------------------------------------------------------
    // JMH JSON parsing
    // ------------------------------------------------------------------

    private static List<Metric> readResults(File file) throws IOException {
        String content = new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
        JSONArray array = JSON.parseArray(content);
        List<Metric> results = new ArrayList<Metric>();
        for (int i = 0; i < array.size(); i++) {
            JSONObject entry = array.getJSONObject(i);
            JSONObject primary = entry.getJSONObject("primaryMetric");
            if (primary == null) {
                continue;
            }
            JSONObject params = entry.getJSONObject("params");

            Double allocScore = null;
            double allocError = 0.0d;
            String allocUnit = null;
            JSONObject secondary = entry.getJSONObject("secondaryMetrics");
            if (secondary != null) {
                JSONObject alloc = secondary.getJSONObject(GC_ALLOC_RATE_NORM);
                if (alloc != null && alloc.containsKey("score")) {
                    allocScore = sanitize(alloc.getDouble("score"));
                    allocError = sanitize(alloc.containsKey("scoreError") ? alloc.getDouble("scoreError") : 0.0d);
                    allocUnit = alloc.getString("scoreUnit");
                }
            }

            String benchmark = entry.getString("benchmark");
            results.add(new Metric(
                    keyOf(benchmark, params),
                    displayNameOf(benchmark, params),
                    entry.getString("mode"),
                    primary.getDoubleValue("score"),
                    sanitize(primary.containsKey("scoreError") ? primary.getDouble("scoreError") : 0.0d),
                    primary.getString("scoreUnit"),
                    allocScore,
                    allocError,
                    allocUnit));
        }
        return results;
    }

    private static JSONObject readMeta(File file) {
        if (!file.isFile()) {
            return null;
        }
        try {
            return JSON.parseObject(new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8));
        } catch (IOException e) {
            return null;
        }
    }

    private static Map<String, Metric> index(List<Metric> metrics) {
        Map<String, Metric> byKey = new TreeMap<String, Metric>();
        for (Metric metric : metrics) {
            byKey.put(metric.key, metric);
        }
        return byKey;
    }

    private static String keyOf(String benchmark, JSONObject params) {
        return benchmark + paramsSuffix(params);
    }

    private static String displayNameOf(String benchmark, JSONObject params) {
        String simple = benchmark.substring(benchmark.lastIndexOf('.') + 1);
        return simple + paramsSuffix(params);
    }

    private static String paramsSuffix(JSONObject params) {
        if (params == null || params.isEmpty()) {
            return "";
        }
        List<String> parts = new ArrayList<String>();
        for (String key : new TreeSet<String>(params.keySet())) {
            parts.add(key + "=" + params.getString(key));
        }
        return " [" + String.join(", ", parts) + "]";
    }

    /** JMH emits "NaN" errors for short runs; treat those as "no error information". */
    private static double sanitize(Double value) {
        if (value == null || value.isNaN() || value.isInfinite()) {
            return 0.0d;
        }
        return value;
    }

    private static String formatScore(double score, String unit) {
        String number;
        double abs = Math.abs(score);
        if (abs >= 100.0d) {
            number = String.format(Locale.ROOT, "%.0f", score);
        } else if (abs >= 10.0d) {
            number = String.format(Locale.ROOT, "%.1f", score);
        } else {
            number = String.format(Locale.ROOT, "%.2f", score);
        }
        return unit == null || unit.isEmpty() ? number : number + " " + unit;
    }

    private static void usageAndExit(String message) {
        System.err.println("ERROR: " + message);
        System.err.println();
        System.err.println("Usage: BaselineComparator [--baseline <jmh.json>] [--baseline-meta <meta.json>]");
        System.err.println("                           --current <jmh.json> [--report <report.md>]");
        System.err.println("                           [--warn <pct>] [--fail <pct>] [--allow-missing]");
        System.exit(2);
    }
}
